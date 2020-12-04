package compiler.builder;

import compiler.builder.threads.CopyThread;
import compiler.builder.threads.MinifyThread;
import compiler.builder.threads.PackDotMCMetaCreator;
import compiler.builder.zipper.Zipper;
import compiler.constants.ErrorMessages;
import compiler.multi_thread.MultiThreadHandler;
import compiler.properties.Property;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static compiler.FileStrings.*;
import static compiler.properties.Property.*;

public class ResourcepackBuilder
{
	public static void build(MultiThreadHandler threadHandler) throws InterruptedException, IOException
	{
		File f = new File(RESOURCE_SOURCE_DIRECTORY);
		if (!f.exists()) return;
		boolean createdDataDirectory = iterate(f, threadHandler);
		if (!createdDataDirectory)
		{
			System.out.println("The \"" + RESOURCE_SOURCE_DIRECTORY + "\" directory was non-existent, empty, or only filled with files that are ignored");
			return;
		}
		
		File file = new File(PACK_DOT_MCMETA);
		if (file.exists())
		{
			file.delete();
		}
		threadHandler.run(new PackDotMCMetaCreator(RESOURCEPACK_DESCRIPTION.getValue(), RESOURCEPACK_FORMAT.getValue()));
		
		threadHandler.join();
		System.out.println("All files inside \"" + RESOURCE_SOURCE_DIRECTORY + "\" are now parsed and ready to be used");
		
		if (Boolean.parseBoolean(Property.ZIP.getValue()))
		{
			File[] toZip = getFilesToZip();
			Zipper.zip(toZip, getDestZipFile());
			System.out.println("The resourcepack is now a .zip file and ready to be distributed");
		}
	}
	
	private static String getDestZipFile()
	{
		String name = RESOURCEPACK_NAME.getValue();
		if (name == null || name.trim().isEmpty())
		{
			name = "resources";
		}
		else if (RESOURCEPACK_NAME.getValue().equals(DATAPACK_NAME.getValue()))
		{
			name += " resources";
		}
		return name + FileExtensions.ZIP;
	}
	
	private static File[] getFilesToZip()
	{
		if (RESOURCEPACK_INCLUDE.getValue().trim().isEmpty())
		{
			return new File[]{new File(PACK_DOT_MCMETA), new File(RESOURCE_OUTPUT_DIRECTORY)};
		}
		String[] extraFileNames = RESOURCEPACK_INCLUDE.getValue().split(",");
		File[] files = new File[extraFileNames.length + 2];
		files[0] = new File(PACK_DOT_MCMETA);
		files[1] = new File(RESOURCE_OUTPUT_DIRECTORY);
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
		return file.getPath().replaceFirst(RESOURCE_SOURCE_DIRECTORY, RESOURCE_OUTPUT_DIRECTORY);
	}
}
