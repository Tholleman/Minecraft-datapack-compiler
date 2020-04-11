package compiler.scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteScripts
{
	private WriteScripts() {}
	
	public static void writeScripts(String fileExtension) throws IOException
	{
		String executeJar = "java -jar \"" + new File("./").getAbsolutePath() + File.separator + "compiler.jar\"";
		write("build." + fileExtension, executeJar);
		write("init." + fileExtension, executeJar + " init");
		write("clean." + fileExtension, executeJar + " clean");
		write("import." + fileExtension, executeJar + " import");
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
			System.out.println("Make the file executable");
		}
	}
}
