package compiler.multi_thread;

import java.io.Serializable;

public class ExceptionInfo implements Serializable
{
	public final transient Thread t;
	public final Throwable e;
	
	public ExceptionInfo(Thread t, Throwable e)
	{
		this.t = t;
		this.e = e;
	}
}
