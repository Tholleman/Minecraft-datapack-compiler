package compiler.multi_thread;

import java.util.ArrayList;

public class MultiThreadHandler
{
	public static Thread.UncaughtExceptionHandler multiExceptionHandler()
	{
		return multiExceptionHandler(Thread.currentThread().getUncaughtExceptionHandler());
	}
	
	public static Thread.UncaughtExceptionHandler multiExceptionHandler(Thread.UncaughtExceptionHandler singleExceptionHandler)
	{
		return new Thread.UncaughtExceptionHandler()
		{
			@Override
			public void uncaughtException(Thread t, Throwable e)
			{
				if (e instanceof MultiException)
				{
					MultiException multiException = (MultiException) e;
					for (ExceptionInfo exception : multiException.getExceptions())
					{
						uncaughtException(exception.t, exception.e);
					}
				}
				else
				{
					singleExceptionHandler.uncaughtException(t, e);
				}
			}
		};
	}
	
	private final ArrayList<Thread> threads = new ArrayList<>();
	private final ArrayList<ExceptionInfo> exceptions = new ArrayList<>();
	private final Thread.UncaughtExceptionHandler handler = (t, e) -> exceptions.add(new ExceptionInfo(t, e));
	
	public void run(Thread toAdd)
	{
		toAdd.setUncaughtExceptionHandler(handler);
		threads.add(toAdd);
		toAdd.start();
	}
	
	public void join() throws InterruptedException
	{
		for (Thread thread : threads)
		{
			thread.join();
		}
		if (!exceptions.isEmpty())
		{
			ArrayList<ExceptionInfo> copy = new ArrayList<>(exceptions);
			exceptions.clear();
			throw new MultiException(copy);
		}
		threads.clear();
	}
}
