package compiler.properties;

/**
 * A level a command can have for it to be parsed.
 *
 * @author Thomas Holleman
 */
public class CompileLevel
{
	public final String name;
	public final int level;
	public final String zipSuffix;
	
	private CompileLevel(String name, int level, String zipSuffix)
	{
		this.name = name;
		this.level = level;
		this.zipSuffix = zipSuffix;
	}
	
	public static final CompileLevel PRODUCTION = new CompileLevel("Production", 1, "");
	public static final CompileLevel DEVELOP = new CompileLevel("Develop", 2, "DEV");
	public static final CompileLevel VERBOSE = new CompileLevel("Verbose", 3, "VERBOSE");
	
	public static CompileLevel UNKNOWN(int level) {return new CompileLevel("CLevel " + level, level, "CLEVEL_" + level);}
}
