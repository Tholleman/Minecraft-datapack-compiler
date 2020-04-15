package compiler.builder.threads;

import compiler.builder.BuildException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static compiler.FileStrings.PACK_DOT_MCMETA;
import static compiler.constants.ErrorMessages.COULD_NOT_CREATE_PACK_MCMETA;

public class PackDotMCMetaCreator extends Thread
{
	private final String description;
	
	public PackDotMCMetaCreator(String description)
	{
		this.description = description != null ? description : "";
	}
	
	@Override
	public void run()
	{
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File(PACK_DOT_MCMETA)))
		{
			fileOutputStream.write(("{" +
			                        "\"pack\":{" +
			                        "\"pack_format\":5," +
			                        "\"description\":\"" + description + "\"" +
			                        "}" +
			                        "}").getBytes());
			fileOutputStream.flush();
		}
		catch (IOException e)
		{
			throw new BuildException(COULD_NOT_CREATE_PACK_MCMETA, e);
		}
	}
}