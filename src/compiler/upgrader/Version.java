package compiler.upgrader;

public enum Version
{
	UNKNOWN(null, null),
	V1_0("Metafile 1.0", null),
	V1_1("Metafile 1.1", "Global variables can be added to the config file");
	
	public final String code;
	public final String changelog;
	
	Version(String code, String changelog)
	{
		this.code = code;
		this.changelog = changelog != null ? changelog : "";
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
		return V1_1;
	}
}
