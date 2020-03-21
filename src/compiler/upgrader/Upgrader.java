package compiler.upgrader;

import compiler.Entry;
import compiler.properties.Property;

import static compiler.properties.Property.PARSE_STANDARD;

public class Upgrader
{
	private Upgrader() {}
	
	public static void upgrade()
	{
		if (PARSE_STANDARD.getValue() == null)
		{
			unknownCompiler();
			return;
		}
		switch (PARSE_STANDARD.getValue())
		{
			default:
				unknownCompiler();
				break;
		}
	}
	
	private static void unknownCompiler()
	{
		System.out.println("Unknown compiler, assuming no upgrade is needed");
		PARSE_STANDARD.setValue(Entry.STANDARD);
		Property.store();
	}
}
