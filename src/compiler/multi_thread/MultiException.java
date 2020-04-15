package compiler.multi_thread;

import java.util.ArrayList;
import java.util.List;

public class MultiException extends RuntimeException
{
	private final List<ExceptionInfo> exceptions;
	
	public MultiException(List<ExceptionInfo> exceptions)
	{
		super("Incorrect uncaught exception handler implemented. Only the first exception can be shown:", exceptions.get(0).e);
		this.exceptions = exceptions;
	}
	
	public List<ExceptionInfo> getExceptions()
	{
		return new ArrayList<>(exceptions);
	}
}
