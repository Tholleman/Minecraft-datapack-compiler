package compiler.builder;

import compiler.MultiException;
import compiler.builder.json.Minify;
import compiler.builder.parser.Parser;
import compiler.builder.parser.ParsingException;
import compiler.constants.ErrorMessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Vector;

import static compiler.FileStrings.PACK_DOT_MCMETA;
import static compiler.constants.ErrorMessages.COULD_NOT_CREATE_PACK_MCMETA;

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
			throw new MultiException(failures);
		}
	}
	
	public static class ParseThread extends MultiThread
	{
		public final File f;
		public final String output;
		private final Map<String, String> variables;
		
		public ParseThread(File f, String output, Map<String, String> variables)
		{
			this.f = f;
			this.output = output;
			this.variables = variables;
		}
		
		@Override
		public void run()
		{
			try
			{
				Parser.parse(f, output, variables);
			}
			catch (ParsingException pEx)
			{
				throw new ParsingException(ErrorMessages.AN_ERROR_OCCURRED_WHILE_PARSING(f.getPath()), pEx);
			}
		}
	}
	
	public static class MinifyThread extends MultiThread
	{
		private final File input;
		private final String output;
		
		public MinifyThread(File input, String output)
		{
			this.input = input;
			this.output = output;
		}
		
		@Override
		public void run()
		{
			try (FileInputStream in = new FileInputStream(input);
			     FileOutputStream out = new FileOutputStream(new File(output)))
			{
				new Minify().minify(in, out);
			}
			catch (Exception e)
			{
				throw new BuildException(ErrorMessages.AN_ERROR_OCCURRED_WHILE_PARSING(input.getPath()), e);
			}
		}
	}
	
	public static class PackDotMCMetaCreator extends MultiThread
	{
		private final String description;
		
		public PackDotMCMetaCreator(String description)
		{
			this.description = description != null ? description : "";
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
		
		public Copy(Path from, Path to)
		{
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
				throw new BuildException("Could not copy " + from);
			}
		}
	}
}
