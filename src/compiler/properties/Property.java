package compiler.properties;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static compiler.FileStrings.CONFIG_PATH;
import static compiler.constants.ErrorMessages.COULD_NOT_READ_PROPERTIES_FILE;

/**
 * Supplier for the properties of a datapack
 *
 * @author Thomas Holleman
 */
public enum Property
{
	DATAPACK_NAME,
	DATAPACK_DESCRIPTION,
	ZIP_INCLUDE,
	PACK_FORMAT,
	
	PARSE_STANDARD,
	BLACKLIST,
	ZIP,
	CLEAN_AFTER;
	
	private static final java.util.Properties propertiesLoader = new java.util.Properties();
	
	public static void load()
	{
		try (FileInputStream fis = new FileInputStream(CONFIG_PATH))
		{
			propertiesLoader.load(fis);
		}
		catch (IOException e)
		{
			throw new SetupException(COULD_NOT_READ_PROPERTIES_FILE, e);
		}
	}
	
	public static List<Property> check()
	{
		List<Property> missing = new ArrayList<>();
		for (Property value : Property.values())
		{
			if (!propertiesLoader.containsKey(value.getKey()))
			{
				missing.add(value);
			}
		}
		return missing;
	}
	
	public static void store()
	{
		try (FileWriter fw = new FileWriter(CONFIG_PATH))
		{
			fw.write("# Datapack data\n"
			         + DATAPACK_NAME.safeString() + "\n"
			         + DATAPACK_DESCRIPTION.safeString() + "\n"
			         + ZIP_INCLUDE.safeString() + "\n"
			         + PACK_FORMAT.safeString() + "\n"
			         + "\n"
			         + "# Compiler \n"
			         + PARSE_STANDARD.safeString() + "\n"
			         + BLACKLIST.safeString() + "\n"
			         + ZIP.safeString() + "\n"
			         + CLEAN_AFTER.safeString() + "\n"
			         + "\n" +
			         "# Global Variables\n");
			List<Property> properties = new ArrayList<>(Arrays.asList(Property.values()));
			remaining:
			for (Map.Entry<Object, Object> keyValueEntry : propertiesLoader.entrySet())
			{
				for (int i = 0; i < properties.size(); i++)
				{
					if (properties.get(i).getKey().equals(keyValueEntry.getKey()))
					{
						properties.remove(i);
						continue remaining;
					}
				}
				fw.write(keyValueEntry.getKey() + "=" + keyValueEntry.getValue() + "\n");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void add(String key, String value)
	{
		propertiesLoader.put(key, value);
	}
	
	public static Map<String, String> getVariables()
	{
		HashMap<String, String> variables = new HashMap<>();
		for (Map.Entry<Object, Object> keyValueEntry : propertiesLoader.entrySet())
		{
			variables.put((String) keyValueEntry.getKey(), (String) keyValueEntry.getValue());
		}
		return variables;
	}
	
	public String getKey()
	{
		return name();
	}
	
	public String getValue()
	{
		return propertiesLoader.getProperty(getKey());
	}
	
	public void setValueWhenEmpty(String value)
	{
		if (propertiesLoader.getProperty(getKey()) == null)
		{
			propertiesLoader.setProperty(getKey(), value);
		}
	}
	
	private String safeString()
	{
		return getKey() + "=" + (getValue() == null ? "" : getValue());
	}
	
	@Override
	public String toString()
	{
		return getValue();
	}
}