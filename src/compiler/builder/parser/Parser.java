package compiler.builder.parser;

import compiler.constants.MetaTags;

import java.io.*;
import java.util.HashMap;

import static compiler.constants.ErrorMessages.*;
import static compiler.constants.Identifiers.*;
import static compiler.builder.parser.Helper.splitOnWS;

/**
 * Will change a file from this projects format to an mcfunction usable by minecraft
 */
public class Parser
{
	/**
	 * Parse a file
	 *
	 * @param file         The file to parse
	 * @param outputPath   The path to write to
	 * @param compileLevel The compile level that lines should have at minimum when clevel is given.
	 */
	public static void parse(File file, String outputPath, int compileLevel)
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file));
		     FileWriter fw = new FileWriter(outputPath))
		{
			new Parser(file.getParent(), br, fw, compileLevel).parse();
		}
		catch (IOException e)
		{
			throw new ParsingException(UNEXPECTED_ERROR_WHILE_INITIALIZING, e);
		}
	}
	
	private final String inputDir;
	private final Reader reader;
	private final Writer writer;
	private final HashMap<String, String> variables;
	private final int compileLevel;
	
	/**
	 * Constructor of a parser object.
	 * <p>
	 * Use {@link Parser#parse()} to start parsing the file after instantiating.
	 * <p>
	 * Use {@link Parser#parse(File, String, int)} To instantiate and parse immidiatly
	 *
	 * @param inputDir     The path to the input directory
	 * @param fileToParse  The file to parse
	 * @param output       The path of the file that should be created
	 * @param compileLevel The compile level that lines should have at minimum when clevel is given.
	 */
	public Parser(String inputDir, BufferedReader fileToParse, FileWriter output, int compileLevel)
	{
		this(inputDir, fileToParse, new Writer(output), new HashMap<>(), compileLevel);
	}
	
	private Parser(String inputDir, BufferedReader fileToParse, Writer writer, HashMap<String, String> variables, int compileLevel)
	{
		this.inputDir = inputDir;
		reader = new Reader(fileToParse);
		this.writer = writer;
		this.variables = variables;
		this.compileLevel = compileLevel;
	}
	
	/**
	 * Start parsing with the data given by the constructor.
	 */
	public void parse()
	{
		String line;
		while ((line = reader.readLine()) != null)
		{
			if (reader.isMeta())
			{
				handleMetaLine(line);
				continue;
			}
			writer.writeLine(line);
		}
	}
	
	private void handleMetaLine(String line)
	{
		String[] args = splitOnWS(line);
		
		switch (args[0])
		{
			case MetaTags.VAR:
				addVariable(args, line);
				break;
			case MetaTags.REPEAT:
				repeatNextLine(args);
				break;
			case MetaTags.FILE:
				parseFile(args, line);
				break;
			case MetaTags.COMPILE_LEVEL:
				handleCompileLevel(args);
				break;
			default:
				throw new ParsingException(UNKNOWN_LINE_META(args[0], reader.getLineCounter()));
		}
	}
	
	private void addVariable(String[] args, String line)
	{
		if (args.length < 3) throw new ParsingException(NOT_ENOUGH_ARGUMENTS_AT_LEAST(args[0], 3, reader.getLineCounter()));
		variables.put(args[1], Helper.reattach(line, args[1]));
	}
	
	private void repeatNextLine(String[] args)
	{
		try
		{
			if (args.length != 2) throw new ParsingException(NOT_ENOUGH_ARGUMENTS(args[0], 2, reader.getLineCounter()));
			writer.setRepeat(Integer.parseInt(args[1]));
		}
		catch (NumberFormatException nfEx)
		{
			throw new ParsingException(NOT_A_NUMBER(reader.getLineCounter()), nfEx);
		}
	}
	
	private void parseFile(String[] args, String line)
	{
		if (args.length < 2) throw new ParsingException(NOT_ENOUGH_ARGUMENTS_AT_LEAST(args[0], 2, reader.getLineCounter()));
		String filePath = inputDir + File.separator + Helper.reattach(line, args[0]);
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath))))
		{
			new Parser(inputDir, br, writer, variables, compileLevel).parse();
		}
		catch (IOException e)
		{
			throw new ParsingException(AN_ERROR_OCCURRED_WHILE_PARSING(filePath, reader.getLineCounter()), e);
		}
	}
	
	private void handleCompileLevel(String[] args)
	{
		if (args.length != 2) throw new ParsingException(NOT_ENOUGH_ARGUMENTS(args[0], 2, reader.getLineCounter()));
		try
		{
			if (Integer.parseInt(args[1]) > compileLevel) reader.skipOne();
		}
		catch (NumberFormatException nfEx)
		{
			throw new ParsingException(NOT_A_NUMBER(reader.getLineCounter()), nfEx);
		}
	}
	
	private class Reader
	{
		private final BufferedReader fileToParse;
		private int lineCounter = 0;
		private boolean meta;
		private boolean skipOne;
		private String next;
		
		public Reader(BufferedReader fileToParse)
		{
			assert fileToParse != null;
			this.fileToParse = fileToParse;
			meta = false;
			skipOne = false;
		}
		
		public String readLine()
		{
			try
			{
				StringBuilder result = new StringBuilder();
				boolean multilining = false;
				// Loop until a full command is found or until the end of the file is reached
				do
				{
					String line;
					// Read a line if the next line is still unknown
					// If the next line is known but it should be skipped, also read the next line
					if (next == null || useSkipOne())
					{
						line = fileToParse.readLine();
						lineCounter++;
						// If the end of the file is reached, return what's left
						if (line == null)
						{
							
							String lastCommand = result.toString();
							if (lastCommand.isEmpty())
							{
								return null;
							}
							return lastCommand;
						}
						
						// Remove all leading whitespaces
						line = line.replaceAll("^\\s+", "");
						
						// If the line is empty, read the next
						if (line.isEmpty()) continue;
						// If the line is a comment, read the next
						if (line.startsWith(COMMENT_PREFIX)) continue;
						// If the line should be skipped, read the next
						if (useSkipOne()) continue;
						
						// If the previous line isn't completed yet, check if this can be added
						// If it can't, set it as the next line.
						if (multilining)
						{
							// If the current line shouldn't be added to the result, set it as next and return the result
							if (line.startsWith(META_PREFIX) || line.startsWith(COMMAND_PREFIX))
							{
								next = line;
								return result.toString();
							}
							line = parseInlineMeta(line);
							
							result.append(line);
							continue;
						}
					}
					// Use the previously read line
					// There is no need to do any parsing of inline meta, comments, etc since those are done the moment the line was read
					else
					{
						line = next;
						next = null;
					}
					line = parseInlineMeta(line);
					
					// Now that the line is read and known to not be part of a previous line, identify it as a meta or command line
					if (line.startsWith(META_PREFIX))
					{
						meta = true;
						multilining = true;
						result.append(line.substring(META_PREFIX.length()));
					}
					else if (line.startsWith(COMMAND_PREFIX))
					{
						meta = false;
						multilining = true;
						result.append(line.substring(COMMAND_PREFIX.length()));
					}
					// The line is new but it's function is unknown.
					// Throw an error, the user probably made a mistake
					else
					{
						throw new ParsingException(UNEXPECTED_START(lineCounter));
					}
				} while (true);
			}
			catch (IOException e)
			{
				throw new ParsingException(UNKNOWN_READ_ERROR(getLineCounter()), e);
			}
		}
		
		private String parseInlineMeta(String line)
		{
			int last;
			while ((last = line.lastIndexOf(INLINE_META_PREFIX)) != -1)
			{
				int endIndex = line.indexOf(INLINE_META_SUFFIX, last);
				String result = handleInlineMeta(line.substring(last + INLINE_META_PREFIX.length(), endIndex));
				line = line.substring(0, last) + result + line.substring(endIndex + INLINE_META_SUFFIX.length());
			}
			return line;
		}
		
		private String handleInlineMeta(String line)
		{
			String[] args = splitOnWS(line);
			if (args.length == 1)
			{
				String variableValue = variables.get((args[0]));
				if (variableValue == null) throw new ParsingException(UNKNOWN_VARIABLE(args[0], reader.getLineCounter()));
				return variableValue;
			}
			if (args.length == 3)
			{
				switch (args[1])
				{
					case PLUS:
						return String.valueOf(Integer.parseInt(args[0]) + Integer.parseInt(args[2]));
					case MINUS:
						return String.valueOf(Integer.parseInt(args[0]) - Integer.parseInt(args[2]));
					case MULTIPLY:
						return String.valueOf(Integer.parseInt(args[0]) * Integer.parseInt(args[2]));
					case DIVIDE:
						return String.valueOf(Integer.parseInt(args[0]) / Integer.parseInt(args[2]));
					default:
						throw new ParsingException(UNKNOWN_OPERATOR(args[1], getLineCounter()));
				}
			}
			throw new ParsingException(UNKNOWN_INLINE_META(line, reader.getLineCounter()));
		}
		
		public int getLineCounter()
		{
			// If next is filled, the amount of lines read is 1 higher, subtract it when asked
			return lineCounter + (next == null ? 0 : 1);
		}
		
		public boolean isMeta()
		{
			return meta;
		}
		
		public void skipOne()
		{
			skipOne = true;
		}
		
		private boolean useSkipOne()
		{
			if (!skipOne) return false;
			
			skipOne = false;
			next = null;
			
			return true;
		}
	}
	
	private static class Writer
	{
		private static final int REPEAT_DEFAULT = 1;
		
		private final FileWriter output;
		private int repeat = REPEAT_DEFAULT;
		private boolean first = true;
		
		public Writer(FileWriter output)
		{
			assert output != null;
			this.output = output;
		}
		
		public void setRepeat(int amount) {repeat = amount;}
		
		public void writeLine(String line)
		{
			try
			{
				for (int i = 0; i < repeat; i++)
				{
					if (first)
					{
						output.write(line);
						first = false;
					}
					else
					{
						output.write(System.lineSeparator() + line);
					}
				}
				repeat = REPEAT_DEFAULT;
			}
			catch (IOException e)
			{
				throw new ParsingException(UNKNOWN_WRITE_ERROR, e);
			}
		}
	}
}
