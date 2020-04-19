package compiler;

import compiler.builder.Builder;
import compiler.cleaner.Cleaner;
import compiler.importer.Importer;
import compiler.initializer.Initialize;
import compiler.multi_thread.MultiThreadHandler;
import compiler.properties.Property;
import compiler.scripts.WriteScripts;
import compiler.upgrader.Upgrader;
import compiler.upgrader.Version;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

import static compiler.constants.ErrorMessages.MISSING_PROPERTIES;

public class Entry
{
	private static final Thread.UncaughtExceptionHandler exceptionHandler = MultiThreadHandler.multiExceptionHandler(new Thread.UncaughtExceptionHandler()
	{
		@Override
		public void uncaughtException(Thread t, Throwable e)
		{
			try (FileWriter fileWriter = new FileWriter("Error.txt", true))
			{
				print(e, "", fileWriter);
			}
			catch (IOException ioException)
			{
				ioException.printStackTrace();
			}
		}
		
		private void print(Throwable e, String indent, FileWriter fileWriter) throws IOException
		{
			if (e == null) return;
			String exceptionType = e.getClass().getSimpleName() + ": ";
			StringBuilder newIndent = new StringBuilder(indent);
			for (int i = 0; i < exceptionType.length(); i++)
			{
				newIndent.append(' ');
			}
			String toWrite = indent + exceptionType + e.getMessage().replaceAll("\\n", "\n" + newIndent);
			System.err.println(toWrite);
			fileWriter.write(toWrite + "\n");
			if (e.getCause() != null) print(e.getCause(), "\t" + indent, fileWriter);
		}
	});
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		File error = new File("Error.txt");
		if (error.exists() && error.isFile())
		{
			Files.delete(error.toPath());
		}
		Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
		if (args == null || args.length <= 0)
		{
			args = setAuto();
		}
		switch (args[0].toLowerCase())
		{
			case "build":
				checkArgumentAmount(args, 0);
				Property.load();
				if (!Version.current().code.equals(Property.PARSE_STANDARD.getValue()))
				{
					Upgrader.upgrade();
				}
				else
				{
					List<Property> missing = Property.check();
					if (!missing.isEmpty())
					{
						throw new CompilerException(MISSING_PROPERTIES(missing));
					}
					Builder.build();
				}
				return;
			case "clean":
				checkArgumentAmount(args, 0);
				Cleaner.fullClean();
				return;
			case "init":
				checkArgumentAmount(args, 0);
				Initialize.init();
				return;
			case "import":
				checkArgumentAmount(args, 0);
				Importer.create();
				return;
			case "version":
				checkArgumentAmount(args, 0);
				System.out.println("This compiler currently supports " + Version.current().code);
				return;
			case "scripts":
				checkArgumentAmount(args, 1);
				if (args.length != 2) throw new CompilerException("No script file type given, add sh for unix systems or bat for Windows.");
				WriteScripts.writeScripts(args[1]);
				return;
			case "help":
				checkArgumentAmount(args, 0);
				printFile("Help.txt");
				return;
			case "rfc":
				checkArgumentAmount(args, 0);
				Help.cheatSheet();
				return;
			case "license":
				checkArgumentAmount(args, 0);
				printFile("License.txt");
				break;
			case "credits":
				checkArgumentAmount(args, 0);
				printFile("Credits.txt");
				break;
			default:
				throw new CompilerException("Unknown argument: \"" + args[0] + "\" use argument \"help\" to see which options you do have.");
		}
	}
	
	private static String[] setAuto()
	{
		String[] args;
		if (new File(FileStrings.SOURCE_DIRECTORY).exists())
		{
			args = new String[]{"build"};
		}
		else if (new File(FileStrings.OUTPUT_DIRECTORY).exists())
		{
			args = new String[]{"import"};
		}
		else
		{
			args = new String[]{"init"};
		}
		return args;
	}
	
	private static void checkArgumentAmount(String[] args, int max)
	{
		if (args.length - 1 > max) throw new CompilerException("Too many arguments for " + args[0]);
	}
	
	private static void printFile(String path) throws IOException
	{
		try (BufferedReader br = new BufferedReader(new InputStreamReader(Entry.class.getResourceAsStream(path))))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				System.out.println(line);
			}
		}
	}
}
