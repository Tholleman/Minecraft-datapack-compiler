package compiler.properties;

import java.io.FileInputStream;
import java.io.IOException;

import static compiler.constants.ErrorMessages.COULD_NOT_READ_PROPERTIES_FILE;
import static compiler.FileStrings.CONFIG_PATH;

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
	
	public enum Key
	{
		DATAPACK_NAME,
		CURRENT_MINECRAFT_VERSION,
		COMPILE_LEVEL,
		DESCRIPTION,
		CLEAN_AFTER
	}
	
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
	public static final String DATAPACK_NAME = propertiesLoader.getProperty(Key.DATAPACK_NAME.toString());
	
	/**
	 * The version of minecraft that the datapack is made for
	 */
	public static final String CURRENT_MINECRAFT_VERSION = propertiesLoader.getProperty(Key.CURRENT_MINECRAFT_VERSION.toString());
	
	/**
	 * The level of commands that should be compiled.
	 * <p>
	 * By default, commands have a level of 1.
	 * The compile level indicates the minimum a command has to be for it to be parsed.
	 */
	public static final CompileLevel COMPILE_LEVEL = CompileLevel.getLevel(Integer.parseInt(propertiesLoader.getProperty(Key.COMPILE_LEVEL.toString())));
	
	/**
	 * The description of the datapack
	 */
	public static final String DESCRIPTION = propertiesLoader.getProperty(Key.DESCRIPTION.toString());
	
	/**
	 * Remove the data directory and mcmeta file after building the .zip file when {@code true}
	 */
	public static final boolean CLEAN_AFTER = Boolean.parseBoolean(propertiesLoader.getProperty(Key.CLEAN_AFTER.toString()));
}
