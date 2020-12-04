package compiler.upgrader;

import compiler.CompilerException;
import compiler.FileStrings;
import compiler.properties.Property;

import java.util.Map;

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
				result.append(V1_3.toPrint()).append("\n\n");
				// fallthrough
			case V1_3:
				result.append(V1_4.toPrint()).append("\n\n");
				// fallthrough
			case V1_4:
				result.append(V1_5.toPrint()).append("\n");
				break;
			case V1_5:
				Map<String, String> variables = Property.getVariables();
				Property.DATAPACK_FORMAT.setValueWhenEmpty(variables.get("PACK_FORMAT"));
				Property.remove("PACK_FORMAT");
				Property.DATAPACK_INCLUDE.setValueWhenEmpty(variables.get("ZIP_INCLUDE"));
				Property.remove("ZIP_INCLUDE");
				
				Property.RESOURCEPACK_NAME.setValueWhenEmpty(Property.DATAPACK_NAME.getValue());
				Property.RESOURCEPACK_DESCRIPTION.setValueWhenEmpty(Property.DATAPACK_DESCRIPTION.getValue());
				Property.RESOURCEPACK_INCLUDE.setValueWhenEmpty(Property.DATAPACK_INCLUDE.getValue());
				Property.RESOURCEPACK_FORMAT.setValueWhenEmpty(Property.DATAPACK_FORMAT.getValue());
				
				Property.PREFER_RESOURCEPACK_MCMETA.setValueWhenEmpty("false");
				
				Property.store();
				
				// fallthrough
			case V1_6:
				assert V1_6 == current();
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
