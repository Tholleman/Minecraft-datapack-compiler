package compiler.builder;

import compiler.FileStrings;
import compiler.builder.threads.CopyThread;
import compiler.builder.threads.MinifyThread;
import compiler.builder.threads.PackDotMCMetaCreator;
import compiler.builder.threads.ParseThread;
import compiler.builder.zipper.Zipper;
import compiler.cleaner.Cleaner;
import compiler.constants.ErrorMessages;
import compiler.multi_thread.MultiThreadHandler;
import compiler.properties.Property;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static compiler.FileStrings.*;
import static compiler.properties.Property.*;

/**
 * The main class that will build the datapack with {@link #build()}
 *
 * @author Thomas Holleman
 */
public class Builder
{
	private Builder() {}
	
	/**
	 * Build the datapack using the files from {@link FileStrings#SOURCE_DIRECTORY}
	 * into {@link FileStrings#OUTPUT_DIRECTORY} and into a {@link FileStrings.FileExtensions#ZIP} file.
	 *
	 * @throws IOException          Could be thrown while reading the files out of {@link FileStrings#SOURCE_DIRECTORY},
	 *                              while writing to {@link FileStrings#OUTPUT_DIRECTORY},
	 *                              or while zipping it into a {@link FileStrings.FileExtensions#ZIP} file
	 * @throws InterruptedException Could be thrown while failing to join up the threads that were made to parse each file
	 */
	public static void build() throws IOException, InterruptedException
	{
		MultiThreadHandler threadHandler = new MultiThreadHandler();
		Cleaner.fullClean();
		System.out.println("Removed artifacts from previous build");
		
		boolean dataFirst = Boolean.parseBoolean(PREFER_RESOURCEPACK_MCMETA.getValue());
		if (!dataFirst)
		{
			ResourcepackBuilder.build(threadHandler);
		}
		
		boolean createdDataDirectory = iterate(new File(SOURCE_DIRECTORY), threadHandler);
		if (!createdDataDirectory)
		{
			System.out.println("The data source directory was empty (or only filled with files that are ignored)");
			return;
		}
		
		File file = new File(PACK_DOT_MCMETA);
		if (file.exists())
		{
			file.delete();
		}
		threadHandler.run(new PackDotMCMetaCreator(DATAPACK_DESCRIPTION.getValue(), DATAPACK_FORMAT.getValue()));
		
		threadHandler.join();
		System.out.println("All files inside \"" + SOURCE_DIRECTORY + "\" are now parsed and ready to be used");
		
		if (Boolean.parseBoolean(Property.ZIP.getValue()))
		{
			File[] toZip = getFilesToZip();
			Zipper.zip(toZip, getDestZipFile());
			System.out.println("The datapack is now a .zip file and ready to be distributed");
			
			if (dataFirst)
			{
				ResourcepackBuilder.build(threadHandler);
			}
			
			if (Boolean.parseBoolean(CLEAN_AFTER.getValue()))
			{
				Cleaner.postClean();
				System.out.println("\nRemoved artifacts that aren't the final zip");
			}
		}
	}
	
	private static String getDestZipFile()
	{
		String name = DATAPACK_NAME.getValue();
		if (name == null)
		{
			try
			{
				name = new File("./").getCanonicalFile().getName();
			}
			catch (IOException e)
			{
				name = "datapack";
			}
		}
		return name + FileExtensions.ZIP;
	}
	
	private static File[] getFilesToZip()
	{
		if (DATAPACK_INCLUDE.getValue().trim().isEmpty())
		{
			return new File[]{new File(PACK_DOT_MCMETA), new File(OUTPUT_DIRECTORY)};
		}
		String[] extraFileNames = DATAPACK_INCLUDE.getValue().split(",");
		File[] files = new File[extraFileNames.length + 2];
		files[0] = new File(PACK_DOT_MCMETA);
		files[1] = new File(OUTPUT_DIRECTORY);
		int nextIndex = 2;
		for (String extraFileName : extraFileNames)
		{
			files[nextIndex] = new File(extraFileName);
			nextIndex++;
		}
		return files;
	}
	
	private static boolean iterate(File f, MultiThreadHandler threadHandler) throws IOException
	{
		assert f != null;
		if (!f.exists())
		{
			if (SOURCE_DIRECTORY.equals(f.getPath()))
			{
				throw new BuildException(SOURCE_DIRECTORY + " does not exist.\n" +
				                         "Run this compiler with init or import to create the directory.\n" +
				                         "Run this compiler with help for more information");
			}
			throw new BuildException(f.getPath() + " does not exist.");
		}
		if (f.isDirectory())
		{
			return handleDirectory(f, threadHandler);
		}
		return handleFile(f, threadHandler);
	}
	
	private static boolean handleDirectory(File f, MultiThreadHandler threadHandler) throws IOException
	{
		File dataVersion = new File(dataVersion(f));
		if (!dataVersion.exists() && !dataVersion.mkdir())
		{
			throw new BuildException(ErrorMessages.COULD_NOT_CREATE_DIRECTORY);
		}
		
		boolean filled = false;
		
		File[] files = f.listFiles();
		assert files != null;
		for (File file : files)
		{
			filled = iterate(file, threadHandler) || filled;
		}
		
		if (!filled)
		{
			Files.delete(dataVersion.toPath());
		}
		return filled;
	}
	
	private static boolean handleFile(File f, MultiThreadHandler threadHandler)
	{
		assert f.isFile();
		
		for (String s : BLACKLIST.getValue().split(","))
		{
			if (f.getName().matches(s.trim())) return false;
		}
		
		for (String s : parseWhiteList())
		{
			if (f.getName().matches(s))
			{
				threadHandler.run(new ParseThread(f, dataVersion(f), getVariables()));
				return true;
			}
		}
		
		if (f.getName().endsWith(FileExtensions.JSON))
		{
			threadHandler.run(new MinifyThread(f, dataVersion(f)));
			return true;
		}
		
		threadHandler.run(new CopyThread(f.toPath(), new File(dataVersion(f)).toPath()));
		return true;
	}
	
	private static String dataVersion(File file)
	{
		return file.getPath().replaceFirst(SOURCE_DIRECTORY, OUTPUT_DIRECTORY);
	}
}
