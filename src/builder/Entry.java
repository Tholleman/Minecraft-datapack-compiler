package builder;

import java.io.IOException;

public class Entry
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if (argumentCheck("clean", args))
		{
			Cleaner.fullClean();
		}
		else if (argumentCheck("init", args))
		{
			Initialize.init();
		}
		else
		{
			Builder.build();
		}
	}
	
	private static boolean argumentCheck(String test, String[] args)
	{
		if (args == null) return false;
		if (args.length == 0) return false;
		assert test != null;
		return test.equalsIgnoreCase(args[0]);
	}
}
