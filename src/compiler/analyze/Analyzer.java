package compiler.analyze;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Analyzer
{
	private final Counter counter = new Counter();
	private final ArrayList<AnalyzedFile> analyzedFiles = new ArrayList<>();
	
	public static void analyze(File directory, File output)
	{
		assert directory != null;
		assert directory.exists();
		assert directory.isDirectory();
		
		Analyzer analyzer = new Analyzer();
		analyzer.analyzeDirectory(directory);
		
		analyzer.createReport(output);
	}
	
	private void analyzeDirectory(File f)
	{
		assert f.isDirectory();
		File[] children = f.listFiles();
		assert children != null;
		
		ArrayList<File> subDirectories = new ArrayList<>();
		for (File child : children)
		{
			if (child.isFile())
			{
				analyzeCode(child);
			}
			else if (child.isDirectory())
			{
				subDirectories.add(child);
			}
			else
			{
				throw new AnalyzeException(child.getName() + " is not a file nor directory");
			}
		}
		for (File subDirectory : subDirectories)
		{
			analyzeDirectory(subDirectory);
		}
	}
	
	private void analyzeCode(File functionFile)
	{
		try (BufferedReader br = new BufferedReader(new FileReader(functionFile)))
		{
			AnalyzedFile file = new AnalyzedFile(functionFile.getPath(), counter);
			analyzedFiles.add(file);
			String line;
			int lineCounter = 0;
			while ((line = br.readLine()) != null)
			{
				lineCounter++;
				if (line.contains("@e"))
				{
					file.addEntityCheck(new Line(line, lineCounter));
				}
				if (line.contains("@a") && !line.contains("gamemode="))
				{
					file.addSpectatorPlayers(new Line(line, lineCounter));
				}
				if (line.contains("summon ") || line.contains("loot spawn "))
				{
					file.addCreateEntities(new Line(line, lineCounter));
				}
				if (line.contains("nbt"))
				{
					file.addNbt(new Line(line, lineCounter));
				}
			}
		}
		catch (IOException e)
		{
			throw new AnalyzeException("Could not read " + functionFile.getName(), e);
		}
	}
	
	private void createReport(File output)
	{
		try (FileWriter fw = new FileWriter(output))
		{
			write(counter.getNbt() + " lines contain nbt", fw);
			for (AnalyzedFile analyzedFile : analyzedFiles)
			{
				List<Line> entityChecks = analyzedFile.getNbt();
				if (!entityChecks.isEmpty())
				{
					write(analyzedFile.path, fw);
					for (Line entityCheck : entityChecks)
					{
						write(entityCheck.number + ": " + entityCheck.value, fw);
					}
				}
			}
			write("", fw);
			write(counter.getEntityChecks() + " lines with @e", fw);
			for (AnalyzedFile analyzedFile : analyzedFiles)
			{
				List<Line> entityChecks = analyzedFile.getEntityChecks();
				if (!entityChecks.isEmpty())
				{
					write(analyzedFile.path, fw);
					for (Line entityCheck : entityChecks)
					{
						write(entityCheck.number + ": " + entityCheck.value, fw);
					}
				}
			}
			write("", fw);
			write(counter.getSpectatorPlayers() + " lines with @a that might also involve spectators", fw);
			for (AnalyzedFile analyzedFile : analyzedFiles)
			{
				List<Line> entityChecks = analyzedFile.getSpectatorPlayers();
				if (!entityChecks.isEmpty())
				{
					write(analyzedFile.path, fw);
					for (Line entityCheck : entityChecks)
					{
						write(entityCheck.number + ": " + entityCheck.value, fw);
					}
				}
			}
			write("", fw);
			write(counter.getCreateEntities() + " lines that summon entities", fw);
			for (AnalyzedFile analyzedFile : analyzedFiles)
			{
				List<Line> entityChecks = analyzedFile.getCreateEntities();
				if (!entityChecks.isEmpty())
				{
					write(analyzedFile.path, fw);
					for (Line entityCheck : entityChecks)
					{
						write(entityCheck.number + ": " + entityCheck.value, fw);
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new AnalyzeException("Could not write to file", e);
		}
	}
	
	private void write(String line, FileWriter fw) throws IOException
	{
		System.out.println(line);
		fw.write(line + System.lineSeparator());
	}
}
