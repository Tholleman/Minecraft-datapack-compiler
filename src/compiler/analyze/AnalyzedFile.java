package compiler.analyze;

import java.util.ArrayList;
import java.util.List;

public class AnalyzedFile
{
	public final String path;
	private final ArrayList<Line> entityChecks = new ArrayList<>();
	private final ArrayList<Line> spectatorPlayers = new ArrayList<>();
	private final ArrayList<Line> createEntities = new ArrayList<>();
	private final ArrayList<Line> nbt = new ArrayList<>();
	private final Counter counter;
	
	public AnalyzedFile(String path, Counter counter)
	{
		this.path = path;
		this.counter = counter;
	}
	
	public void addEntityCheck(Line line)
	{
		entityChecks.add(line);
		counter.addEntityChecks();
	}
	
	public void addSpectatorPlayers(Line line)
	{
		spectatorPlayers.add(line);
		counter.addSpectatorPlayers();
	}
	
	public void addCreateEntities(Line line)
	{
		createEntities.add(line);
		counter.addCreateEntities();
	}
	
	public void addNbt(Line line)
	{
		nbt.add(line);
		counter.addNbt();
	}
	
	public List<Line> getEntityChecks()
	{
		return new ArrayList<>(entityChecks);
	}
	
	public List<Line> getSpectatorPlayers()
	{
		return new ArrayList<>(spectatorPlayers);
	}
	
	public List<Line> getCreateEntities()
	{
		return new ArrayList<>(createEntities);
	}
	
	public List<Line> getNbt()
	{
		return new ArrayList<>(nbt);
	}
}
