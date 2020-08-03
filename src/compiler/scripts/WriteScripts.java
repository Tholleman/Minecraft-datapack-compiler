package compiler.scripts;

import compiler.FileStrings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteScripts
{
	private WriteScripts() {}
	
	public static void writeScripts(String fileExtension) throws IOException
	{
		String executeJar = "java -jar \"" + new File("./").getAbsolutePath() + File.separator + "compiler.jar\"";
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
		write("clean." + fileExtension, executeJar + " clean");
		System.out.println("Scripts are created");
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
