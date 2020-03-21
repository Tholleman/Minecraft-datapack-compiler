package compiler.properties;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

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
	
	public static void setup()
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
			         + PARSE_STANDARD.safeString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static CompileLevel getCompileLevel()
	{
		int level = Integer.parseInt(COMPILE_LEVEL.getValue());
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