package compiler.builder;

import compiler.CompilerException;

public class BuildException extends CompilerException
{
	public BuildException(String message)
	{
		super(message);
	}
	
	public BuildException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
