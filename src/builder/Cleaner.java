package builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static builder.constants.FileStrings.*;

public class Cleaner
{
	private Cleaner() {}
	
	public static void fullClean() throws IOException
	{
		//noinspection ConstantConditions
		for (File file : new File("./").listFiles())
		{
			if (file.getName().endsWith(ZIP)) unsafeDelete(file);
		}
		
		delete(new File(OUTPUT_DIRECTORY));
		delete(new File(PACK_DOT_MCMETA));
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
