package compiler.upgrader;

import compiler.CompilerException;
import compiler.FileStrings;

import static compiler.properties.Property.PARSE_STANDARD;
import static compiler.upgrader.Version.V1_1;

public class Upgrader
{
	private Upgrader() {}
	
	public static void upgrade()
	{
		switch (Version.getVersion(PARSE_STANDARD.getValue()))
		{
			case V1_0:
				System.out.println("Upgrading to 1.1: ");
				System.out.println(V1_1.changelog);
				// Technically should check for variables that have the name of a properties key 
				// but the compiler isn't promoted yet.
			case V1_1:
				break;
			case UNKNOWN:
			default:
				unknownCompiler();
				break;
		}
	}
	
	private static void unknownCompiler()
	{
		throw new CompilerException("Unknown standard inside " + FileStrings.CONFIG_PATH + ", " +
		                            "change " + PARSE_STANDARD.getKey() + " to " + Version.current().code);
	}
}
