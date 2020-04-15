package compiler.builder.threads;

import compiler.builder.BuildException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyThread extends Thread
{
	private final Path from;
	private final Path to;
	
	public CopyThread(Path from, Path to)
	{
		this.from = from;
		this.to = to;
	}
	
	@Override
	public void run()
	{
		try
		{
			Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			throw new BuildException("Could not copy " + from);
		}
	}
}
