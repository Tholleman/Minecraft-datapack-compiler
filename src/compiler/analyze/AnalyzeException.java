package compiler.analyze;

import compiler.CompilerException;

public class AnalyzeException extends CompilerException
{
	public AnalyzeException(String message)
	{
		super(message);
	}
	
	public AnalyzeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
