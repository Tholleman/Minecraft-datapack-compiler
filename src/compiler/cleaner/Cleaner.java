package compiler.cleaner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static compiler.cleaner.CleanerStrings.*;

public class Cleaner
{
	private Cleaner() {}
	
	public static void fullClean() throws IOException
	{
		File[] files = new File("./").listFiles();
		assert files != null;
		checkForSources(files);
		for (File file : files)
		{
			if (file.getName().endsWith(FileExtensions.ZIP)) unsafeDelete(file);
		}
		
		delete(new File(OUTPUT_DIRECTORY));
		delete(new File(RESOURCE_OUTPUT_DIRECTORY));
		delete(new File(PACK_DOT_MCMETA));
	}
	
	private static void checkForSources(File[] files)
	{
		boolean hasConfig = false;
		boolean hasDataSource = false;
		for (File file : files)
		{
			switch (file.getName())
			{
				case SOURCE_DIRECTORY:
					hasDataSource = true;
					// Both files need to exist for the check to end
					if (hasConfig) return;
					break;
				case CONFIG_PATH:
					hasConfig = true;
					// Both files need to exist for the check to end
					if (hasDataSource) return;
					break;
				default:
					break;
			}
		}
		throw new CleanerException(MISSING_SOURCE_FILES);
	}
	
	public static void postClean() throws IOException
	{
		delete(new File(OUTPUT_DIRECTORY));
		delete(new File(PACK_DOT_MCMETA));
	}
	
	private static void delete(File file) throws IOException
	{
		if (file.exists())
		{
			unsafeDelete(file);
		}
	}
	
	private static void unsafeDelete(File file) throws IOException
	{
		if (file.isDirectory())
		{
			//noinspection ConstantConditions
			for (File listFile : file.listFiles())
			{
				unsafeDelete(listFile);
			}
		}
		
		Files.delete(file.toPath());
	}
}
