package compiler.initializer;

import compiler.FileStrings;
import compiler.properties.Property;
import compiler.properties.SetupException;
import compiler.upgrader.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static compiler.FileStrings.SOURCE_DIRECTORY;
import static compiler.properties.Property.*;
import static java.io.File.separator;

public class Initialize
{
	private Initialize() {}
	
	public static void init() throws IOException
	{
		String projectName = new File("./").getCanonicalFile().getName();
		createConfigFile(projectName);
		createDataSource(projectName);
	}
	
	private static void createConfigFile(String projectName)
	{
		createConfigFile(projectName, "");
	}
	
	public static void createConfigFile(String projectName, String description)
	{
		try
		{
			Property.load();
		}
		catch (SetupException ignored)
		{
			// Fails when the file does not exist, this is not a problem
		}
		
		DATAPACK_NAME.setValueWhenEmpty(projectName);
		DATAPACK_VERSION.setValueWhenEmpty("1.0");
		DATAPACK_DESCRIPTION.setValueWhenEmpty(description);
		
		CLEAN_AFTER.setValueWhenEmpty("false");
		
		COMPILE_LEVEL.setValueWhenEmpty("1");
		PARSE_STANDARD.setValueWhenEmpty(Version.current().code);
		
		Property.store();
	}
	
	private static void createDataSource(String projectName) throws IOException
	{
		new Dir(FileStrings.SOURCE_DIRECTORY,
		        new Dir(projectName,
		                new Dir("advancements"),
		                new Dir("functions"),
		                new Dir("loot_tables"),
		                new Dir("predicates"),
		                new Dir("recipes"),
		                new Dir("structures"),
		                new Dir("tags",
		                        new Dir("blocks"),
		                        new Dir("entity_types"),
		                        new Dir("fluids"),
		                        new Dir("functions"),
		                        new Dir("items"))),
		        new Dir("minecraft",
		                new Dir("tags",
		                        new Dir("functions"))))
				.create();
		
		try (FileOutputStream mcLoadCreator = new FileOutputStream(new File(SOURCE_DIRECTORY + separator + "minecraft" + separator + "tags" + separator + "functions" + separator + "load.json")))
		{
			mcLoadCreator.write(("{\n" +
			                     "    \"values\": [\n" +
			                     "        \"" + projectName + ":load\"\n" +
			                     "    ]\n" +
			                     "}").getBytes());
		}
		
		try (FileOutputStream ownLoadCreator = new FileOutputStream(new File(SOURCE_DIRECTORY + separator + projectName + separator + "functions" + File.separator + "load.mcfunction")))
		{
			ownLoadCreator.write(("\\clevel 2\n" +
			                      "/tellraw @a \"" + projectName + " is loaded\"").getBytes());
		}
	}
	
	private static class Dir
	{
		private String path = "";
		public final String name;
		public final Dir[] subs;
		
		public Dir(String name, Dir... subs)
		{
			this.name = name;
			this.subs = subs;
		}
		
		public void create()
		{
			File file = new File(path + name);
			if (file.exists())
			{
				if (!file.isDirectory())
				{
					throw new InitializeException(name + " already exists but isn't a directory");
				}
			}
			else
			{
				if (!file.mkdir())
				{
					throw new InitializeException("could not create directory " + name);
				}
			}
			for (Dir sub : subs)
			{
				sub.path = path + name + separator;
				sub.create();
			}
		}
	}
}
