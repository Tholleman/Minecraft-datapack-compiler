package compiler.constants;

public class Identifiers
{
	/**
	 * Utility class should not be instantiated
	 */
	private Identifiers() {}
	
	public static final String COMMAND_PREFIX = "/";
	public static final String META_PREFIX = "\\";
	public static final char ESCAPE = '^';
	
	public static final String INLINE_META_PREFIX = "<<";
	public static final String INLINE_META_SUFFIX = ">>";
	
	public static final String COMMENT_PREFIX = "#";
	
	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String DIVIDE = "/";
	public static final String MULTIPLY = "*";
}
