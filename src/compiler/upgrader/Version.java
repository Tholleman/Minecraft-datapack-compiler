package compiler.upgrader;

public enum Version
{
	UNKNOWN(null, null),
	V1_0("Metafile 1.0", null),
	V1_1("Metafile 1.1", "Global variables can be added to the config file"),
	V1_2("Metafile 1.2", "If statements and no more \\clevel", "Change \\clevel to \\if <<COMPILE_LEVEL>> =="),
	V1_3("Metafile 1.3", "Zipping is now optional", "Add \"ZIP=true\" to config.properties"),
	V1_4("Metafile 1.4", "Made pack format a variable", "Add \"PACK_FORMAT=5\" to config.properties"),
	;
	
	public final String code;
	private final String changelog;
	private final String[] upgradeSteps;
	
	Version(String code, String changelog, String... upgradeSteps)
	{
		this.code = code;
		this.changelog = changelog != null ? changelog : "";
		this.upgradeSteps = upgradeSteps;
	}
	
	public String toPrint()
	{
		StringBuilder steps = new StringBuilder();
		for (String upgradeStep : upgradeSteps)
		{
			steps.append("- ").append(upgradeStep);
		}
		return code + ": " + changelog + "\n" +
		       steps;
	}
	
	public static Version getVersion(String code)
	{
		if (code == null) return UNKNOWN;
		for (Version value : values())
		{
			if (code.equals(value.code))
			{
				return value;
			}
		}
		return UNKNOWN;
	}
	
	public static Version current()
	{
		return V1_4;
	}
}
