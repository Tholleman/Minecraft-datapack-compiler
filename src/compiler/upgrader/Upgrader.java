package compiler.upgrader;

import compiler.CompilerException;
import compiler.FileStrings;

import static compiler.properties.Property.PARSE_STANDARD;
import static compiler.upgrader.Version.*;

public class Upgrader
{
	private Upgrader() {}
	
	public static void upgrade()
	{
		if (Version.current() == Version.getVersion(PARSE_STANDARD.getValue()))
		{
			return;
		}
		StringBuilder result = new StringBuilder("Datapack is not written in " + Version.current().code + "\n\n");
		switch (Version.getVersion(PARSE_STANDARD.getValue()))
		{
			case V1_0:
				result.append(V1_1.toPrint()).append("\n\n");
				// fallthrough
			case V1_1:
				result.append(V1_2.toPrint()).append("\n\n");
				// fallthrough
			case V1_2:
				result.append(V1_3.toPrint()).append("\n");
				break;
			case UNKNOWN:
			default:
				result.append("Unknown standard inside ").append(FileStrings.CONFIG_PATH).append("\n");
				break;
		}
		result.append("- Change ").append(PARSE_STANDARD.getKey()).append(" to ").append(Version.current().code);
		throw new CompilerException(result.toString());
	}
}
