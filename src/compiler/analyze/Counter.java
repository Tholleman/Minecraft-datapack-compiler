package compiler.analyze;

public class Counter
{
	private int entityChecks = 0;
	private int spectatorPlayers = 0;
	private int createEntities = 0;
	private int nbt = 0;
	
	public void addEntityChecks()
	{
		this.entityChecks++;
	}
	
	public void addSpectatorPlayers()
	{
		this.spectatorPlayers++;
	}
	
	public void addCreateEntities()
	{
		this.createEntities++;
	}
	
	public void addNbt()
	{
		this.nbt++;
	}
	
	public int getEntityChecks()
	{
		return entityChecks;
	}
	
	public int getSpectatorPlayers()
	{
		return spectatorPlayers;
	}
	
	public int getCreateEntities()
	{
		return createEntities;
	}
	
	public int getNbt()
	{
		return nbt;
	}
}
