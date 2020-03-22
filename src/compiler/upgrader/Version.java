package compiler.upgrader;

public enum Version
{
	UNKNOWN(null, null),
	V1_0("Metafile 1.0", null);
	
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
			if (value.code.equals(code))
			{
				return value;
			}
		}
		return UNKNOWN;
	}
	
	public static Version current()
	{
		return V1_0;
	}
}
