package builder;

import builder.constants.FileStrings;
import builder.properties.Properties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static builder.constants.FileStrings.CONFIG_PATH;
import static builder.constants.FileStrings.SOURCE_DIRECTORY;
import static java.io.File.separator;

public class Initialize
{
	private Initialize() {}
	
	public static void init() throws IOException
	{
		String projectName = new File("./").getCanonicalFile().getName();
		createConfigFile(projectName);
		createDataSource(projectName.replaceAll("\\s+", "_"));
	}
	
	private static void createConfigFile(String projectName) throws IOException
	{
		File configFile = new File(CONFIG_PATH);
		if (configFile.exists()) return;
		try (FileOutputStream configBuilder = new FileOutputStream(configFile))
		{
			Properties.Key[] values = Properties.Key.values();
			for (int i = 0; i < values.length; i++)
			{
				Properties.Key key = values[i];
				configBuilder.write((key.toString() + "=").getBytes());
				switch (key)
				{
					case DATAPACK_NAME:
						configBuilder.write(projectName.getBytes());
						break;
					case CLEAN_AFTER:
						configBuilder.write("false".getBytes());
						break;
					case COMPILE_LEVEL:
						configBuilder.write("1".getBytes());
						break;
					default:
				}
				if (i != values.length - 1)
				{
					configBuilder.write("\n".getBytes());
				}
			}
		}
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
					throw new BuildException(name + " already exists but isn't a directory");
				}
			}
			else
			{
				if (!file.mkdir())
				{
					throw new BuildException("could not create directory " + name);
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
