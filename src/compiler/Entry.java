package compiler;

import compiler.builder.Builder;
import compiler.cleaner.Cleaner;
import compiler.importer.Importer;
import compiler.initializer.Initialize;
import compiler.properties.Property;
import compiler.upgrader.Upgrader;

import java.io.IOException;

public class Entry
{
	public static final String STANDARD = "Metafile 1.0";
	
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
				System.err.println(indent + e.getMessage());
				if (e.getCause() != null) print(e.getCause(), indent + "\t");
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
				case "help":
					checkArgumentAmount(args, 0);
					System.out.println("        Use no argument to build the datapack.\n" +
					                   "clean : To remove all artifacts that the regular build creates.\n" +
					                   "init  : To create the framework for a new datapack.\n" +
					                   "import: To import an existing datapack." +
					                   "help  : To show this message again.");
					return;
				default:
					throw new CompilerException("Unknown argument: \"" + args[0] + "\" use argument \"help\" to see which options you do have.");
			}
		}
		else
		{
			Property.load();
			if (!STANDARD.equals(Property.PARSE_STANDARD.getValue())) Upgrader.upgrade();
			Builder.build();
		}
	}
	
	private static void checkArgumentAmount(String[] args, int max)
	{
		if (args.length - 1 > max) throw new CompilerException("Too many arguments for " + args[0]);
	}
}