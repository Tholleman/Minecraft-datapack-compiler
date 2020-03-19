package builder;

import builder.constants.ErrorMessages;
import builder.json.Minify;
import builder.parser.Parser;
import builder.parser.ParsingException;
import builder.properties.Properties;
import builder.zipper.Zipper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Vector;

import static builder.constants.ErrorMessages.COULD_NOT_CREATE_PACK_MCMETA;
import static builder.constants.FileStrings.*;
import static builder.properties.Properties.*;

/**
 * The main class that will build the datapack with {@link Builder#main(String[])}
 *
 * @author Thomas Holleman
 */
public class Builder
{
	/**
	 * Build the datapack using the files from {@link builder.constants.FileStrings#SOURCE_DIRECTORY}
	 * into {@link builder.constants.FileStrings#OUTPUT_DIRECTORY} and into a {@link builder.constants.FileStrings#ZIP} file.
	 *
	 * @param args Not used
	 *
	 * @throws IOException          Could be thrown while reading the files out of {@link builder.constants.FileStrings#SOURCE_DIRECTORY},
	 *                              while writing to {@link builder.constants.FileStrings#OUTPUT_DIRECTORY},
	 *                              or while zipping it into a {@link builder.constants.FileStrings#ZIP} file
	 * @throws InterruptedException Could be thrown while failing to join up the threads that were made to parse each file
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if (args.length > 0 && args[0].equalsIgnoreCase("clean"))
		{
			clean();
			return;
		}
		
		System.out.println("Minecraft version " + CURRENT_MINECRAFT_VERSION);
		System.out.println("Compile level: " + COMPILE_LEVEL.label);
		
		clean();
		
		iterate(new File(SOURCE_DIRECTORY));
		new PackDotMCMetaCreator(Properties.DESCRIPTION).start();
		File[] toZip = getFilesToZip();
		
		for (Thread thread : MultiThread.threads)
		{
			thread.join();
		}
		
		// All threads are now back into this one
		
		if (!MultiThread.failures.isEmpty())
		{
			for (Throwable failure : MultiThread.failures)
			{
				failure.printStackTrace();
			}
			return;
		}
		
		System.out.println("All files inside \"" + SOURCE_DIRECTORY + "\" are now parsed and ready to be used");
		
		Zipper.zip(toZip, getDestZipFile());
		System.out.println("The datapack is now a .zip file and ready to be distributed");
	}
	
	private Builder() {}
	
	private static String getDestZipFile()
	{
		return DATAPACK_NAME + " " + CURRENT_MINECRAFT_VERSION + COMPILE_LEVEL.zipSuffix + ZIP;
	}
	
	private static void clean() throws IOException
	{
		//noinspection ConstantConditions
		for (File file : new File("./").listFiles())
		{
			if (file.getName().endsWith(ZIP)) unsafeDelete(file);
		}
		
		delete(new File(OUTPUT_DIRECTORY));
		delete(new File(getDestZipFile()));
		delete(new File(PACK_DOT_MCMETA));
		
		System.out.println("Cleaned artifacts from previous build");
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
	
	private static File[] getFilesToZip()
	{
		return new File[]{new File(PACK_DOT_MCMETA), new File(OUTPUT_DIRECTORY)};
	}
	
	private static void iterate(File f) throws IOException
	{
		assert f != null;
		if (f.isDirectory())
		{
			File dataVersion = new File(dataVersion(f.getPath()));
			if (!dataVersion.exists() && !dataVersion.mkdir())
			{
				throw new BuildException(ErrorMessages.COULD_NOT_CREATE_DIRECTORY);
			}
			File[] files = f.listFiles();
			assert files != null;
			for (File file : files)
			{
				iterate(file);
			}
			return;
		}
		
		for (String s : zipBlackList())
		{
			if (f.getName().matches(s)) return;
		}
		
		if (parseFile(f, dataVersion(f.getPath()))) return;
		
		Files.copy(f.toPath(), new File(dataVersion(f.getPath())).toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	private static boolean parseFile(File f, String output) throws FileNotFoundException
	{
		for (String s : parseWhiteList())
		{
			if (f.getName().matches(s))
			{
				new ParseThread(f, output, COMPILE_LEVEL.level).start();
				return true;
			}
		}
		
		if (f.getName().endsWith(JSON))
		{
			new MinifyThread(f, output).start();
			return true;
		}
		return false;
	}
	
	private abstract static class MultiThread extends Thread
	{
		private static final Vector<Thread> threads = new Vector<>();
		private static final Vector<Throwable> failures = new Vector<>();
		
		public MultiThread()
		{
			threads.add(this);
			setUncaughtExceptionHandler((t, e) -> failures.add(e));
		}
		
		@Override
		public abstract void run();
	}
	
	private static class ParseThread extends MultiThread
	{
		public final File f;
		public final String output;
		public final int compileLevel;
		
		private ParseThread(File f, String output, int compileLevel)
		{
			this.f = f;
			this.output = output;
			this.compileLevel = compileLevel;
		}
		
		@Override
		public void run()
		{
			try
			{
				Parser.parse(f, output, compileLevel);
			}
			catch (ParsingException pEx)
			{
				throw new ParsingException(ErrorMessages.AN_ERROR_OCCURRED_WHILE_PARSING(f.getName()), pEx);
			}
		}
	}
	
	private static class MinifyThread extends MultiThread
	{
		private final FileInputStream inputStream;
		private final FileOutputStream outputStream;
		
		private MinifyThread(File input, String output) throws FileNotFoundException
		{
			inputStream = new FileInputStream(input);
			outputStream = new FileOutputStream(new File(output));
		}
		
		@Override
		public void run()
		{
			try
			{
				new Minify().minify(inputStream, outputStream);
			}
			catch (Exception e)
			{
				throw new BuildException(e.getMessage(), e);
			}
		}
	}
	
	private static class PackDotMCMetaCreator extends MultiThread
	{
		private final String description;
		
		public PackDotMCMetaCreator(String description)
		{
			this.description = description;
		}
		
		@Override
		public void run()
		{
			try (FileOutputStream fileOutputStream = new FileOutputStream(new File(PACK_DOT_MCMETA)))
			{
				fileOutputStream.write(("{" +
				                        "\"pack\":{" +
				                        "\"pack_format\":5," +
				                        "\"description\":\"" + description + "\"" +
				                        "}" +
				                        "}").getBytes());
				fileOutputStream.flush();
			}
			catch (IOException e)
			{
				throw new BuildException(COULD_NOT_CREATE_PACK_MCMETA, e);
			}
		}
	}
	
	private static String dataVersion(String path)
	{
		return path.replaceFirst(SOURCE_DIRECTORY, OUTPUT_DIRECTORY);
	}
}
