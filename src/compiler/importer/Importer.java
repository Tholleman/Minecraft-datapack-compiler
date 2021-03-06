package compiler.importer;

import compiler.FileStrings;
import compiler.constants.Identifiers;
import compiler.initializer.Initialize;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static compiler.FileStrings.*;

public class Importer
{
	private Importer() {}
	
	public static void create() throws IOException
	{
		String extraFiles = listExtraFiles();
		// Checks
		if (new File(SOURCE_DIRECTORY).exists()) throw new ImportException(SOURCE_DIRECTORY + " already exists");
		File existingSources = new File(OUTPUT_DIRECTORY);
		if (!existingSources.exists()) throw new ImportException("No data directory found");
		File mcMeta = new File(PACK_DOT_MCMETA);
		if (!mcMeta.exists() || !mcMeta.isFile()) throw new ImportException("pack.mcmeta could not be found");
		
		// Import
		copy(existingSources);
		JSONObject pack = getPack(mcMeta);
		Initialize.createConfigFile(new File("./").getCanonicalFile().getName(),
		                            pack.get("description").toString(),
		                            pack.getInt("pack_format"),
		                            extraFiles);
	}
	
	private static String listExtraFiles()
	{
		StringBuilder output = new StringBuilder();
		File[] files = new File(".").listFiles();
		assert files != null;
		for (File file : files)
		{
			if (file.isFile())
			{
				if (notAnExtraFile(file)) continue;
				output.append(file.getName()).append(",");
			}
			else if (!file.getName().equals(OUTPUT_DIRECTORY))
			{
				output.append(file.getName()).append(",");
			}
		}
		String result = output.toString();
		if (result.length() > 0) result = result.substring(0, result.length() - 1);
		return result;
	}
	
	private static boolean notAnExtraFile(File file)
	{
		if (file.getName().endsWith(PACK_DOT_MCMETA))
		{
			return true;
		}
		for (String script : FileExtensions.SCRIPTS())
		{
			if (file.getName().endsWith(script))
			{
				return true;
			}
		}
		return false;
	}
	
	private static void copy(File file) throws IOException
	{
		assert file != null;
		if (file.isDirectory())
		{
			Files.createDirectory(new File(getSourceVersion(file)).toPath());
			//noinspection ConstantConditions
			for (File listFile : file.listFiles())
			{
				copy(listFile);
			}
		}
		else if (file.getName().endsWith(FileStrings.FileExtensions.MCFUNCTION))
		{
			importFunctionFile(file);
		}
		else
		{
			Files.copy(file.toPath(), new File(getSourceVersion(file)).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	private static void importFunctionFile(File file) throws IOException
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file));
		     FileWriter fw = new FileWriter(getSourceVersion(file)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				line = line.replace(Identifiers.INLINE_META_PREFIX, Identifiers.ESCAPE + Identifiers.INLINE_META_PREFIX)
				           .trim();
				if (line.isEmpty() ||
				    line.startsWith(Identifiers.COMMENT_PREFIX) ||
				    line.startsWith(Identifiers.COMMAND_PREFIX))
				{
					fw.write(line + "\n");
				}
				else
				{
					fw.write(Identifiers.COMMAND_PREFIX + line + "\n");
				}
			}
		}
	}
	
	private static String getSourceVersion(File file)
	{
		return file.getPath().replaceFirst(OUTPUT_DIRECTORY, SOURCE_DIRECTORY);
	}
	
	private static JSONObject getPack(File mcMeta) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(mcMeta)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
		}
		return new JSONObject(builder.toString()).getJSONObject("pack");
	}
}
