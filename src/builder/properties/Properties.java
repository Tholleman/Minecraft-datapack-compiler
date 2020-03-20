package builder.properties;

import java.io.FileInputStream;
import java.io.IOException;

import static builder.constants.ErrorMessages.COULD_NOT_READ_PROPERTIES_FILE;
import static builder.constants.FileStrings.CONFIG_PATH;

/**
 * Supplier for the properties of a datapack
 *
 * @author Thomas Holleman
 */
public class Properties
{
	/**
	 * Utility class should not be instantiated
	 */
	private Properties() {}
	
	/**
	 * Property loader
	 */
	private static final java.util.Properties propertiesLoader = new java.util.Properties();
	
	static
	{
		try
		{
			propertiesLoader.load(new FileInputStream(CONFIG_PATH));
		}
		catch (IOException e)
		{
			throw new SetupException(COULD_NOT_READ_PROPERTIES_FILE, e);
		}
	}
	
	/**
	 * Name of the datapack
	 */
	public static final String DATAPACK_NAME = propertiesLoader.getProperty("DATAPACK_NAME");
	
	/**
	 * The version of minecraft that the datapack is made for
	 */
	public static final String CURRENT_MINECRAFT_VERSION = propertiesLoader.getProperty("CURRENT_MINECRAFT_VERSION");
	
	/**
	 * The level of commands that should be compiled.
	 * <p>
	 * By default, commands have a level of 1.
	 * The compile level indicates the minimum a command has to be for it to be parsed.
	 */
	public static final CompileLevel COMPILE_LEVEL = CompileLevel.getLevel(Integer.parseInt(propertiesLoader.getProperty("COMPILE_LEVEL")));
	
	/**
	 * The description of the datapack
	 */
	public static final String DESCRIPTION = propertiesLoader.getProperty("DESCRIPTION");
	
	/**
	 * Remove the data directory and mcmeta file after building the .zip file when {@code true}
	 */
	public static final boolean CLEAN_AFTER = Boolean.parseBoolean(propertiesLoader.getProperty("CLEAN_AFTER"));
}
