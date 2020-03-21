package compiler;

import compiler.builder.Builder;
import compiler.cleaner.Cleaner;
import compiler.importer.Importer;
import compiler.initializer.Initialize;

import java.io.IOException;

public class Entry
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if (args != null && args.length > 0)
		{
			switch (args[0].toLowerCase())
			{
				case "clean":
					Cleaner.fullClean();
					return;
				case "init":
					Initialize.init();
					return;
				case "import":
					Importer.create();
					return;
				case "help":
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
			Builder.build();
		}
	}
}
