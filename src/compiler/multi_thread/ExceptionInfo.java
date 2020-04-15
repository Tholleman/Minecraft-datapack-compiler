package compiler.multi_thread;

public class ExceptionInfo
{
	public final Thread t;
	public final Throwable e;
	
	public ExceptionInfo(Thread t, Throwable e)
	{
		this.t = t;
		this.e = e;
	}
}
