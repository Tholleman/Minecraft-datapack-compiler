package compiler.builder.threads;

import compiler.builder.BuildException;
import compiler.builder.json.Minify;
import compiler.constants.ErrorMessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MinifyThread extends Thread
{
	private final File input;
	private final String output;
	
	public MinifyThread(File input, String output)
	{
		this.input = input;
		this.output = output;
	}
	
	@Override
	public void run()
	{
		try (FileInputStream in = new FileInputStream(input);
		     FileOutputStream out = new FileOutputStream(new File(output)))
		{
			new Minify().minify(in, out);
		}
		catch (Exception e)
		{
			throw new BuildException(ErrorMessages.AN_ERROR_OCCURRED_WHILE_PARSING(input.getPath()), e);
		}
	}
}
