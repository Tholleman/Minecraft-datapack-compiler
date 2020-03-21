package compiler.initializer;

import compiler.CompilerException;

public class InitializeException extends CompilerException
{
	public InitializeException(String message)
	{
		super(message);
	}
	
	public InitializeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
