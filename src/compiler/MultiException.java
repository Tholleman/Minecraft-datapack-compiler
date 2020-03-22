package compiler;

import java.util.ArrayList;
import java.util.List;

public class MultiException extends RuntimeException
{
	private final List<Throwable> exceptions;
	
	public MultiException(List<Throwable> exceptions)
	{
		this.exceptions = exceptions;
	}
	
	public List<Throwable> getExceptions()
	{
		return new ArrayList<>(exceptions);
	}
}
