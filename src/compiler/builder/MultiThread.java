package compiler.builder;

import compiler.constants.ErrorMessages;
import compiler.builder.json.Minify;
import compiler.builder.parser.Parser;
import compiler.builder.parser.ParsingException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Vector;

import static compiler.constants.ErrorMessages.COULD_NOT_CREATE_PACK_MCMETA;
import static compiler.FileStrings.PACK_DOT_MCMETA;

public abstract class MultiThread extends Thread
{
	private static final Vector<Thread> threads = new Vector<>();
	private static final Vector<Throwable> failures = new Vector<>();
	
	protected MultiThread()
	{
		threads.add(this);
		setUncaughtExceptionHandler((t, e) -> failures.add(e));
	}
	
	@Override
	public abstract void run();
	
	public static void rejoin() throws InterruptedException
	{
		for (Thread thread : threads)
		{
			thread.join();
		}
		if (!failures.isEmpty())
		{
			// TODO: cleanly communicate all errors
			throw new BuildException(failures.size() + " exception" + (failures.size() == 1 ? "" : "s") + ". " +
			                         "First exception: ", failures.get(0));
		}
	}
	
	public static class ParseThread extends MultiThread
	{
		public final File f;
		public final String output;
		public final int compileLevel;
		
		public ParseThread(File f, String output, int compileLevel)
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
	
	public static class MinifyThread extends MultiThread
	{
		private final FileInputStream inputStream;
		private final FileOutputStream outputStream;
		
		public MinifyThread(File input, String output) throws FileNotFoundException
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
	
	public static class PackDotMCMetaCreator extends MultiThread
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
	
	public static class Copy extends MultiThread
	{
		private final Path from;
		private final Path to;
		
		public Copy(Path from, Path to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public void run()
		{
			try
			{
				Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
			}
			catch (IOException e)
			{
				throw new BuildException("Could not copy", e);
			}
		}
	}
}
