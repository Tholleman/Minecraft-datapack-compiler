package compiler;

import compiler.builder.Builder;
import compiler.cleaner.Cleaner;
import compiler.importer.Importer;
import compiler.initializer.Initialize;
import compiler.properties.Property;
import compiler.upgrader.Upgrader;
import compiler.upgrader.Version;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Entry
{
	private static final Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler()
	{
		@Override
		public void uncaughtException(Thread t, Throwable e)
		{
			print(e, "");
		}
		
		private void print(Throwable e, String indent)
		{
			if (e == null) return;
			if (e instanceof MultiException)
			{
				for (Throwable exception : ((MultiException) e).getExceptions())
				{
					print(exception, indent);
					System.err.println();
				}
			}
			else
			{
				String exceptionType = e.getClass().getSimpleName() + ": ";
				StringBuilder newIndent = new StringBuilder(indent);
				for (int i = 0; i < exceptionType.length(); i++)
				{
					newIndent.append(' ');
				}
				System.err.println(indent + exceptionType + e.getMessage().replaceAll("\\n", "\n" + newIndent));
				if (e.getCause() != null) print(e.getCause(), "\t" + indent);
			}
		}
	};
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
		if (args != null && args.length > 0)
		{
			switch (args[0].toLowerCase())
			{
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
					File script = new File("build." + args[1]);
					try (FileWriter fw = new FileWriter(script))
					{
						fw.write("java -jar \"" + script.getAbsoluteFile().getParentFile().getAbsolutePath() + File.separator + "compiler.jar\"");
					}
					if (!script.setExecutable(true, true))
					{
						System.out.println("Make the file executable");
					}
					return;
				case "help":
					checkArgumentAmount(args, 0);
					System.out.print("Compiler arguments\n" +
					                 "Use no argument to build the datapack.\n" +
					                 "\n" +
					                 "Starting out\n" +
					                 "init: To create the framework for a new datapack.\n" +
					                 "import: To import an existing datapack.\n" +
					                 "scripts: Create an executable script, has to be run with sh, bat, or any other file extension you want.\n" +
					                 "\n" +
					                 "Miscellaneous\n" +
					                 "clean: To remove all artifacts that the regular build creates.\n" +
					                 "version: Shows which meta file standard this compiler works with.\n" +
					                 "help: To show this message again.\n" +
					                 "\n");
					return;
				default:
					throw new CompilerException("Unknown argument: \"" + args[0] + "\" use argument \"help\" to see which options you do have.");
			}
		}
		else
		{
			Property.load();
			if (!Version.current().code.equals(Property.PARSE_STANDARD.getValue())) Upgrader.upgrade();
			Builder.build();
		}
	}
	
	private static void checkArgumentAmount(String[] args, int max)
	{
		if (args.length - 1 > max) throw new CompilerException("Too many arguments for " + args[0]);
	}
}
