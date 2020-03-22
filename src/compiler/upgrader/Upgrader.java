package compiler.upgrader;

import compiler.properties.Property;

import static compiler.properties.Property.PARSE_STANDARD;

public class Upgrader
{
	private Upgrader() {}
	
	public static void upgrade()
	{
		switch (Version.getVersion(PARSE_STANDARD.getValue()))
		{
			case V1_0:
				break;
			
			case UNKNOWN:
			default:
				unknownCompiler();
				break;
		}
	}
	
	private static void unknownCompiler()
	{
		System.out.println("Unknown compiler, assuming no upgrade is needed");
		PARSE_STANDARD.setValue(Version.current().code);
		Property.store();
	}
}
