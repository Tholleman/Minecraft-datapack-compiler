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
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String EXISTS = "exists";
	public static final String AND = "&&";
	public static final String OR = "||";
	public static final String NOT = "NOT";
	
	public static final String EQUALS = "==";
	
	public static final String GT = ">";
	public static final String GE1 = ">=";
	public static final String GE2 = "=>";
	public static final String ST = "<";
	public static final String SE1 = "<=";
	public static final String SE2 = "=<";
}
