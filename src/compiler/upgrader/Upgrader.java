package compiler.upgrader;

import compiler.CompilerException;
import compiler.FileStrings;
import compiler.properties.Property;

import static compiler.properties.Property.PARSE_STANDARD;
import static compiler.upgrader.Version.V1_1;

public class Upgrader
{
	private Upgrader() {}
	
	public static void upgrade()
	{
		if (Version.current() == Version.getVersion(PARSE_STANDARD.getValue()))
		{
			return;
		}
		System.out.println("Datapack is not written in " + Version.current().code);
		switch (Version.getVersion(PARSE_STANDARD.getValue()))
		{
			case V1_0:
				System.out.println("Upgrading to 1.1: ");
				System.out.println(V1_1.changelog);
				// Technically should check for variables that have the name of a properties key 
				// but the compiler isn't promoted yet.
				PARSE_STANDARD.setValue(V1_1.code);
				// fallthrough
			case V1_1:
				
				// These lines are reserved for the current version
				Property.store();
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
