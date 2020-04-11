package compiler.builder;

import compiler.FileStrings;
import compiler.builder.MultiThread.MinifyThread;
import compiler.builder.MultiThread.PackDotMCMetaCreator;
import compiler.builder.MultiThread.ParseThread;
import compiler.builder.zipper.Zipper;
import compiler.cleaner.Cleaner;
import compiler.constants.ErrorMessages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static compiler.FileStrings.*;
import static compiler.builder.MultiThread.Copy;
import static compiler.builder.MultiThread.rejoin;
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
		if (DATAPACK_VERSION.getValue() != null &&
		    !DATAPACK_VERSION.getValue().trim().isEmpty())
		{
			System.out.print("Version " + DATAPACK_VERSION + "\n");
		}
		System.out.print("Compile level: " + getCompileLevel().name + "\n" +
		                 "\n");
		
		Cleaner.fullClean();
		System.out.println("Removed artifacts from previous build");
		
		boolean createdDataDirectory = iterate(new File(SOURCE_DIRECTORY));
		if (!createdDataDirectory)
		{
			System.out.println("The data source directory was empty (or only filled with files that are ignored)");
			return;
		}
		new PackDotMCMetaCreator(DATAPACK_DESCRIPTION.getValue()).start();
		File[] toZip = getFilesToZip();
		
		rejoin();
		
		System.out.println("All files inside \"" + SOURCE_DIRECTORY + "\" are now parsed and ready to be used");
		
		Zipper.zip(toZip, getDestZipFile());
		System.out.println("The datapack is now a .zip file and ready to be distributed");
		
		if (Boolean.parseBoolean(CLEAN_AFTER.getValue()))
		{
			Cleaner.postClean();
			System.out.println("\nRemoved artifacts that aren't the final zip");
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
		String version = DATAPACK_VERSION.getValue();
		if (version == null) version = "";
		return (name + " " + version + " " + getCompileLevel().zipSuffix).trim() + FileExtensions.ZIP;
	}
	
	private static File[] getFilesToZip()
	{
		return new File[]{new File(PACK_DOT_MCMETA), new File(OUTPUT_DIRECTORY)};
	}
	
	private static boolean iterate(File f) throws IOException
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
			return handleDirectory(f);
		}
		return handleFile(f);
	}
	
	private static boolean handleDirectory(File f) throws IOException
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
			filled = iterate(file) || filled;
		}
		
		if (!filled)
		{
			Files.delete(dataVersion.toPath());
		}
		return filled;
	}
	
	private static boolean handleFile(File f)
	{
		assert f.isFile();
		
		for (String s : zipBlackList())
		{
			if (f.getName().matches(s)) return false;
		}
		
		for (String s : parseWhiteList())
		{
			if (f.getName().matches(s))
			{
				new ParseThread(f, dataVersion(f), getVariables()).start();
				return true;
			}
		}
		
		if (f.getName().endsWith(FileExtensions.JSON))
		{
			new MinifyThread(f, dataVersion(f)).start();
			return true;
		}
		
		new Copy(f.toPath(), new File(dataVersion(f)).toPath()).start();
		return true;
	}
	
	private static String dataVersion(File file)
	{
		return file.getPath().replaceFirst(SOURCE_DIRECTORY, OUTPUT_DIRECTORY);
	}
}
