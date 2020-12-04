package compiler.scripts;

import compiler.FileStrings;
import compiler.initializer.Initialize;
import compiler.properties.Property;
import compiler.properties.SetupException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

public class WriteScripts
{
	private WriteScripts() {}
	
	public static void writeScripts(String fileExtension) throws IOException, URISyntaxException
	{
		String executeJar = "java -jar \"" + new File(Initialize.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath() + "\"";
		String passArgument = null;
		switch (fileExtension)
		{
			case FileStrings.FileExtensions.SH:
				passArgument = " $1";
				break;
			case FileStrings.FileExtensions.BAT:
			case FileStrings.FileExtensions.CMD:
				passArgument = " %1";
				break;
			default:
				passArgument = "";
				break;
		}
		// assertion for future development
		assert passArgument != null;
		write("build." + fileExtension, executeJar + passArgument);
		if (new File(FileStrings.CONFIG_PATH).exists())
		{
			try
			{
				Property.load();
				if (!Boolean.parseBoolean(Property.CLEAN_AFTER.getValue()))
				{
					createDataDependentScripts(fileExtension, executeJar);
				}
			}
			catch (SetupException e)
			{
				createDataDependentScripts(fileExtension, executeJar);
			}
		}
		else
		{
			createDataDependentScripts(fileExtension, executeJar);
		}
		System.out.println("Scripts are created");
	}
	
	private static void createDataDependentScripts(String fileExtension, String executeJar) throws IOException
	{
		write("clean." + fileExtension, executeJar + " clean");
		write("analyze." + fileExtension, executeJar + " analyze");
	}
	
	private static void write(String fileName, String content) throws IOException
	{
		File script = new File(fileName);
		try (FileWriter fw = new FileWriter(script))
		{
			fw.write(content);
		}
		if (!script.setExecutable(true, true))
		{
			System.out.println("Make " + fileName + " executable");
		}
	}
}
