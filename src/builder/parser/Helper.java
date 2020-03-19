package builder.parser;

/**
 * Helper class to do simple operations in a single location
 */
class Helper
{
	/**
	 * Utility class should not be instantiated
	 */
	private Helper() {}
	
	/**
	 * Split a string on white space
	 *
	 * @param line The line to split
	 *
	 * @return The resulting string array
	 */
	public static String[] splitOnWS(String line)
	{
		return line.split("\\s+");
	}
	
	/**
	 * Get the substring after a string
	 *
	 * @param line  The line to get a substring from
	 * @param after The part that should be removed and only the characters after this string should be returned
	 *
	 * @return The substring
	 */
	public static String reattach(String line, String after)
	{
		return line.substring(line.indexOf(after) + after.length() + 1);
	}
}
