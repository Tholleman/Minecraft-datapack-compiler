package compiler.upgrader;

import compiler.FileStrings;

import static compiler.properties.Property.PARSE_STANDARD;
import static compiler.upgrader.Version.V1_1;
import static compiler.upgrader.Version.V1_2;

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
				System.out.println("Upgrade to 1.1: " + V1_1.changelog + "\n");
				// fallthrough
			case V1_1:
				System.out.println("Upgrade to 1.2: " + V1_2.changelog + "\n" +
				                   "- Change \\clevel to \\if <<COMPILE_LEVEL>> ==\n");
				break;
			case UNKNOWN:
			default:
				System.out.println("Unknown standard inside " + FileStrings.CONFIG_PATH);
				break;
		}
		System.out.println("- Change " + PARSE_STANDARD.getKey() + " to " + Version.current().code);
	}
}
