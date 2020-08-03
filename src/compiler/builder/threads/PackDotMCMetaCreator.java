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
	private final String packFormat;
	
	public PackDotMCMetaCreator(String description, String packFormat)
	{
		this.description = description != null ? description : "";
		this.packFormat = packFormat != null ? packFormat : "5";
	}
	
	@Override
	public void run()
	{
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File(PACK_DOT_MCMETA)))
		{
			String jsonDescription = this.description;
			if (!(jsonDescription.startsWith("{") && jsonDescription.endsWith("}")) &&
			    !(jsonDescription.startsWith("[") && jsonDescription.endsWith("]")) &&
			    !(jsonDescription.startsWith("\"") && jsonDescription.endsWith("\"")))
			{
				jsonDescription = "\"" + jsonDescription + "\"";
			}
			fileOutputStream.write(("{" +
			                        "\"pack\":{" +
			                        "\"pack_format\":" + packFormat + "," +
			                        "\"description\":" + jsonDescription +
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
