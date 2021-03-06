package compiler.upgrader;

public enum Version
{
	UNKNOWN(null, null),
	V1_0("Metafile 1.0", null),
	V1_1("Metafile 1.1", "Global variables can be added to the config file"),
	V1_2("Metafile 1.2", "If statements and no more \\clevel", "Change \\clevel to \\if <<COMPILE_LEVEL>> =="),
	V1_3("Metafile 1.3", "Zipping is now optional", "Add \"ZIP=true\" to config.properties"),
	V1_4("Metafile 1.4", "Made pack format and blacklist a variable",
	     "Add \"PACK_FORMAT=5\" to config.properties",
	     "Add \"BLACKLIST=.*\\.txt, .*\\.md\" to config.properties"),
	V1_5("Metafile 1.5", "Adding other files to the zip is now possible",
	     "Add \"ZIP_INCLUDE=\" to config.properties"),
	V1_6("Metafile 1.6", "Compiler can now also create resource packs",
	     "change ZIP_INCLUDE into DATAPACK_INCLUDE",
	     "change PACK_FORMAT into DATAPACK_FORMAT",
	     "add RESOURCEPACK_NAME=",
	     "add RESOURCEPACK_DESCRIPTION=",
	     "add RESOURCEPACK_INCLUDE=",
	     "add RESOURCEPACK_FORMAT=",
	     "add PREFER_RESOURCEPACK_MCMETA=false");
	
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
			steps.append("- ").append(upgradeStep).append('\n');
		}
		return code + ": " + changelog + "\n" +
		       steps.toString().trim();
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
		return V1_6;
	}
}
