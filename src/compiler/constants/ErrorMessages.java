package compiler.constants;

import static compiler.constants.Identifiers.*;

public class ErrorMessages
{
	/**
	 * Utility class should not be instantiated
	 */
	private ErrorMessages() {}
	
	public static final String COULD_NOT_READ_PROPERTIES_FILE = "Could not read properties file\n" +
	                                                            "Run this compiler with init or import to create the file.\n" +
	                                                            "Run this compiler with help for more information";
	
	public static String UNKNOWN_READ_ERROR(int line) {return "An unexpected error occurred while reading line " + line;}
	
	public static final String UNKNOWN_WRITE_ERROR = "An unexpected error occurred while writing line ";
	
	public static String UNEXPECTED_START(int line) {return "Line " + line + " started with an unexpected character";}
	
	public static String UNKNOWN_VARIABLE(String variable, int line) {return "Unknown variable \"" + variable + "\" on line " + line;}
	
	public static String UNKNOWN_LINE_META(String tag, int line) {return "Unknown line meta tag \"" + tag + "\" on line " + line;}
	
	public static String UNKNOWN_INLINE_META(String tag, int line) {return "Unknown inline meta tag \"" + tag + "\" on line " + line;}
	
	public static String UNCLOSED_INLINE_META(int lineCounter, int last)
	{
		return "The " + INLINE_META_PREFIX + " " +
		       "starting on line " + lineCounter + " " +
		       "at position " + (last + 1) + " " +
		       "was not closed with a " + INLINE_META_SUFFIX;
	}
	
	public static String UNKNOWN_OPERATOR(String operator, int line) {return "Unknown operator \"" + operator + "\" on line " + line;}
	
	public static String NOT_ENOUGH_ARGUMENTS(String tag, int expected, int line) {return "Expected " + expected + " arguments for tag " + tag + " on line " + line;}
	
	public static String NOT_ENOUGH_ARGUMENTS_AT_LEAST(String tag, int expected, int line) {return "Expected at least " + expected + " arguments for tag " + tag + " on line " + line;}
	
	public static String NOT_A_NUMBER(int line) {return "Expected a number on line " + line;}
	
	public static String AN_ERROR_OCCURRED_WHILE_PARSING(String fileName) {return "An unexpected error occurred while reading file \"" + fileName + "\"";}
	
	public static String AN_ERROR_OCCURRED_WHILE_PARSING(String fileName, int line) {return AN_ERROR_OCCURRED_WHILE_PARSING(fileName) + " from line " + line;}
	
	public static final String UNEXPECTED_ERROR_WHILE_INITIALIZING = "An unexpected error occurred while initializing";
	
	public static final String COULD_NOT_CREATE_DIRECTORY = "Could not create directory";
	
	public static final String COULD_NOT_CREATE_PACK_MCMETA = "Error while creating pack.mcmeta";
	
	public static String COULD_NOT_HANDLE_BOOLEAN(String line, int lineCounter) {return "Could not handle boolean expression \"" + line + "\" on line " + lineCounter;}
	
	public static String NOT_TRUE_OR_FALSE(String line, int lineCounter) {return "\"" + line + "\" is not " + TRUE + " or " + FALSE + " on line " + lineCounter;}
	
	public static String FAILED_BOOLEAN_OP(String operator, int lineCounter) {return "Could not handle the boolean operator " + operator + " on line " + lineCounter;}
}
