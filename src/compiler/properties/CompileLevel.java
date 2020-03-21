package compiler.properties;

/**
 * A level a command can have for it to be parsed.
 *
 * @author Thomas Holleman
 */
@SuppressWarnings("unused")
public enum CompileLevel
{
	/**
	 * Highest level, only the necessary commands are parsed.
	 */
	PRODUCTION("Production", 1, ""),
	
	/**
	 * Development level, necessary and commands that help with development are parsed.
	 */
	DEVELOP("Develop", 2, " DEV"),
	
	/**
	 * All commands are parsed to help track down a problem
	 */
	VERBOSE("Verbose", 3, " VERBOSE");
	
	public final String label;
	public final int level;
	public final String zipSuffix;
	
	CompileLevel(String label, int level, String zipSuffix)
	{
		this.label = label;
		this.level = level;
		this.zipSuffix = zipSuffix;
	}
	
	/**
	 * Get a {@link CompileLevel} based on a number
	 *
	 * @param level The number to get the level for
	 *
	 * @return The according {@link CompileLevel}
	 * @throws SetupException if the level is unknown
	 */
	static CompileLevel getLevel(int level)
	{
		// Loop through all known levels to find the one matching the parameter and return it
		for (CompileLevel value : values())
		{
			if (value.level == level)
			{
				return value;
			}
		}
		throw new SetupException("Unknown level");
	}
}
