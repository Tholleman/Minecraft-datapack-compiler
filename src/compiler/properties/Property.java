package compiler.properties;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static compiler.FileStrings.CONFIG_PATH;
import static compiler.constants.ErrorMessages.COULD_NOT_READ_PROPERTIES_FILE;
import static compiler.properties.CompileLevel.*;

/**
 * Supplier for the properties of a datapack
 *
 * @author Thomas Holleman
 */
public enum Property
{
	DATAPACK_NAME,
	DATAPACK_VERSION,
	DATAPACK_DESCRIPTION,
	
	CLEAN_AFTER,
	
	COMPILE_LEVEL,
	PARSE_STANDARD;
	
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
	
	public static void store()
	{
		try (FileWriter fw = new FileWriter(CONFIG_PATH))
		{
			fw.write("# Datapack data\n"
			         + DATAPACK_NAME.safeString() + "\n"
			         + DATAPACK_VERSION.safeString() + "\n"
			         + DATAPACK_DESCRIPTION.safeString() + "\n"
			         + "\n"
			         + "# Compiler \n"
			         + CLEAN_AFTER.safeString() + "\n"
			         + COMPILE_LEVEL.safeString() + "\n"
			         + PARSE_STANDARD.safeString() + "\n" +
			         "\n" +
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
	
	public static CompileLevel getCompileLevel()
	{
		int level = COMPILE_LEVEL.getValue() == null ? -1 : Integer.parseInt(COMPILE_LEVEL.getValue());
		switch (level)
		{
			case 1:
				return PRODUCTION;
			case 2:
				return DEVELOP;
			case 3:
				return VERBOSE;
			default:
				return UNKNOWN(level);
		}
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
	
	public void setValue(String value)
	{
		propertiesLoader.setProperty(getKey(), value);
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