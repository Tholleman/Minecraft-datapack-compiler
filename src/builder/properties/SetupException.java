package builder.properties;

/**
 * An exception that is thrown while setting up
 */
public class SetupException extends RuntimeException
{
	public SetupException(String message)
	{
		super(message);
	}
	
	public SetupException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
