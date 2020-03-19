package builder.parser;

/**
 * An exception that is thrown while parsing
 */
public class ParsingException extends RuntimeException
{
	public ParsingException(String message)
	{
		super(message);
	}
	
	public ParsingException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
