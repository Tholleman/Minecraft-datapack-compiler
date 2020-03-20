package builder;

import builder.MultiThread.MinifyThread;
import builder.MultiThread.PackDotMCMetaCreator;
import builder.MultiThread.ParseThread;
import builder.constants.ErrorMessages;
import builder.properties.Properties;
import builder.zipper.Zipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static builder.MultiThread.Copy;
import static builder.MultiThread.rejoin;
import static builder.constants.FileStrings.*;
import static builder.properties.Properties.*;

/**
 * The main class that will build the datapack with {@link #build()}
 *
 * @author Thomas Holleman
 */
public class Builder
{
	private Builder() {}
	
	/**
	 * Build the datapack using the files from {@link builder.constants.FileStrings#SOURCE_DIRECTORY}
	 * into {@link builder.constants.FileStrings#OUTPUT_DIRECTORY} and into a {@link builder.constants.FileStrings#ZIP} file.
	 *
	 * @throws IOException          Could be thrown while reading the files out of {@link builder.constants.FileStrings#SOURCE_DIRECTORY},
	 *                              while writing to {@link builder.constants.FileStrings#OUTPUT_DIRECTORY},
	 *                              or while zipping it into a {@link builder.constants.FileStrings#ZIP} file
	 * @throws InterruptedException Could be thrown while failing to join up the threads that were made to parse each file
	 */
	public static void build() throws IOException, InterruptedException
	{
		System.out.println("Minecraft version " + CURRENT_MINECRAFT_VERSION);
		System.out.println("Compile level: " + COMPILE_LEVEL.label);
		
		Cleaner.fullClean();
		
		iterate(new File(SOURCE_DIRECTORY));
		new PackDotMCMetaCreator(Properties.DESCRIPTION).start();
		File[] toZip = getFilesToZip();
		
		rejoin();
		
		System.out.println("All files inside \"" + SOURCE_DIRECTORY + "\" are now parsed and ready to be used");
		
		Zipper.zip(toZip, getDestZipFile());
		
		if (CLEAN_AFTER)
		{
			Cleaner.postClean();
		}
		
		System.out.println("The datapack is now a .zip file and ready to be distributed");
	}
	
	private static String getDestZipFile()
	{
		return DATAPACK_NAME + " " + CURRENT_MINECRAFT_VERSION + COMPILE_LEVEL.zipSuffix + ZIP;
	}
	
	private static File[] getFilesToZip()
	{
		return new File[]{new File(PACK_DOT_MCMETA), new File(OUTPUT_DIRECTORY)};
	}
	
	private static boolean iterate(File f) throws IOException
	{
		assert f != null;
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
	
	private static boolean handleFile(File f) throws FileNotFoundException
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
				new ParseThread(f, dataVersion(f), COMPILE_LEVEL.level).start();
				return true;
			}
		}
		
		if (f.getName().endsWith(JSON))
		{
			new MinifyThread(f, dataVersion(f)).start();
			return true;
		}
		
		new Copy(f.toPath(), Path.of(dataVersion(f))).start();
		return true;
	}
	
	private static String dataVersion(File file)
	{
		return file.getPath().replaceFirst(SOURCE_DIRECTORY, OUTPUT_DIRECTORY);
	}
}
