package compiler.builder.parser;

import java.util.Map;

import static compiler.builder.parser.Helper.subArray;
import static compiler.constants.ErrorMessages.*;
import static compiler.constants.Identifiers.*;

class BooleanConverter
{
	private final Map<String, String> variables;
	private final int lineCounter;
	
	public BooleanConverter(Map<String, String> variables, int lineCounter)
	{
		this.variables = variables;
		this.lineCounter = lineCounter;
	}
	
	public boolean replaceBoolean(String[] args)
	{
		Boolean result = splitBooleanLine(args);
		if (result != null) return result;
		
		switch (args.length)
		{
			case 1:
				return parseBoolean(args[0]);
			case 2:
				if (EXISTS.equals(args[0]))
				{
					return variables.get(args[1]) != null;
				}
				break;
			case 3:
				result = handleNumberOperation(args);
				if (result != null) return result;
				break;
			default:
		}
		
		result = handleEquals(args);
		if (result != null) return result;
		
		StringBuilder line = new StringBuilder(args[0]);
		for (int i = 1; i < args.length; i++)
		{
			line.append(' ').append(args[i]);
		}
		throw new ParsingException(COULD_NOT_HANDLE_BOOLEAN(line.toString(), lineCounter));
	}
	
	private boolean parseBoolean(String arg)
	{
		if (TRUE.equals(arg))
		{
			return true;
		}
		else if (FALSE.equals(arg))
		{
			return false;
		}
		throw new ParsingException(NOT_TRUE_OR_FALSE(arg, lineCounter));
	}
	
	private Boolean splitBooleanLine(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals(AND))
			{
				return replaceBoolean(subArray(args, 0, i)) &&
				       replaceBoolean(subArray(args, i + 1));
			}
			if (args[i].equals(OR))
			{
				return (replaceBoolean(subArray(args, 0, i)) ||
				        replaceBoolean(subArray(args, i + 1)));
			}
		}
		if (args[0].equals(NOT))
		{
			return (!replaceBoolean(Helper.subArray(args, 1)));
		}
		return null;
	}
	
	private Boolean handleNumberOperation(String[] args)
	{
		try
		{
			switch (args[1])
			{
				case GT:
					return Integer.parseInt(args[0]) > Integer.parseInt(args[2]);
				case GE1:
				case GE2:
					return Integer.parseInt(args[0]) >= Integer.parseInt(args[2]);
				case ST:
					return Integer.parseInt(args[0]) < Integer.parseInt(args[2]);
				case SE1:
				case SE2:
					return Integer.parseInt(args[0]) <= Integer.parseInt(args[2]);
				default:
					return null;
			}
		}
		catch (NumberFormatException nfEx)
		{
			throw new ParsingException(FAILED_BOOLEAN_OP(args[1], lineCounter), nfEx);
		}
	}
	
	private Boolean handleEquals(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if (arg.equals(EQUALS))
			{
				if (args.length % 2 == 0) return false;
				if (i != (args.length - 1) / 2) return false;
				for (int j = 0; j < i; j++)
				{
					if (!args[j].equals(args[j + i + 1]))
					{
						return false;
					}
				}
				return true;
			}
		}
		return null;
	}
}
