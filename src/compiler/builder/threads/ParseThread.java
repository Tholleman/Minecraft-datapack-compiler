package compiler.builder.threads;

import compiler.builder.parser.Parser;
import compiler.builder.parser.ParsingException;
import compiler.constants.ErrorMessages;

import java.io.File;
import java.util.Map;

public class ParseThread extends Thread
{
	public final File f;
	public final String output;
	private final Map<String, String> variables;
	
	public ParseThread(File f, String output, Map<String, String> variables)
	{
		this.f = f;
		this.output = output;
		this.variables = variables;
	}
	
	@Override
	public void run()
	{
		try
		{
			Parser.parse(f, output, variables);
		}
		catch (ParsingException pEx)
		{
			throw new ParsingException(ErrorMessages.AN_ERROR_OCCURRED_WHILE_PARSING(f.getPath()), pEx);
		}
	}
}
