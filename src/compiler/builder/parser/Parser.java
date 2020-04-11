package compiler.builder.parser;

import compiler.constants.MetaTags;
import compiler.properties.Property;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static compiler.builder.parser.Helper.splitOnWS;
import static compiler.constants.ErrorMessages.*;
import static compiler.constants.Identifiers.*;

/**
 * Will change a file from this projects format to an mcfunction usable by minecraft
 */
public class Parser
{
	/**
	 * Parse a file
	 *
	 * @param file       The file to parse
	 * @param outputPath The path to write to
	 * @param variables  The variables that are started with.
	 */
	public static void parse(File file, String outputPath, Map<String, String> variables)
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file));
		     FileWriter fw = new FileWriter(outputPath))
		{
			new Parser(file.getParent(), br, fw, variables).parse();
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
	 * Use {@link Parser#parse(File, String, Map)} To instantiate and parse immediately
	 *
	 * @param inputDir    The path to the input directory
	 * @param fileToParse The file to parse
	 * @param output      The path of the file that should be created
	 * @param variables   The variables that are started with
	 */
	public Parser(String inputDir, BufferedReader fileToParse, FileWriter output, Map<String, String> variables)
	{
		this(inputDir, fileToParse, new Writer(output), variables);
	}
	
	private Parser(String inputDir, BufferedReader fileToParse, Writer writer, Map<String, String> variables)
	{
		this.inputDir = inputDir;
		reader = new Reader(fileToParse);
		this.writer = writer;
		this.variables = new HashMap<>(variables);
		this.compileLevel = Integer.parseInt(variables.get(Property.COMPILE_LEVEL.getKey()));
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
		File file = new File(filePath);
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			new Parser(file.getParent(), br, writer, variables).parse();
		}
		catch (Exception e)
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
		private int lineStart;
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
					if (next == null || useSkipOne() && next == null)
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
							
							if (line.charAt(0) == ESCAPE)
							{
								line = line.substring(1);
							}
							
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
					lineStart = lineCounter;
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
			while ((last = lastInlineStart(line)) != -1)
			{
				int endIndex = firstInlineEnd(line, last);
				if (endIndex == -1)
				{
					throw new ParsingException("The " + INLINE_META_PREFIX + " " +
					                           "starting on line " + lineCounter + " " +
					                           "at position " + (last + 1) + " " +
					                           "was not closed with a " + INLINE_META_SUFFIX);
				}
				String result = handleInlineMeta(line.substring(last + INLINE_META_PREFIX.length(), endIndex));
				line = line.substring(0, last) + result + line.substring(endIndex + INLINE_META_SUFFIX.length());
			}
			line = line.replace(ESCAPE + INLINE_META_PREFIX, INLINE_META_PREFIX);
			return line;
		}
		
		private int lastInlineStart(String line)
		{
			int index = line.lastIndexOf(INLINE_META_PREFIX);
			while (index > 0 && line.charAt(index - 1) == ESCAPE &&
			       !(index != 1 && line.charAt(index - 2) == ESCAPE))
			{
				index = line.lastIndexOf(INLINE_META_PREFIX, index - 1);
			}
			return index;
		}
		
		private int firstInlineEnd(String line, int from)
		{
			int index = line.indexOf(INLINE_META_SUFFIX, from);
			while (index != -1 && line.charAt(index - 1) == ESCAPE &&
			       !(index > 1 && line.charAt(index - 2) == ESCAPE))
			{
				from = index + 1;
				index = line.indexOf(INLINE_META_SUFFIX, from);
			}
			return index;
		}
		
		private String handleInlineMeta(String line)
		{
			String[] args = splitOnWS(line);
			if (args.length == 1)
			{
				String variableValue = variables.get((args[0]));
				if (variableValue == null) throw new ParsingException(UNKNOWN_VARIABLE(args[0], reader.lineCounter));
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
			return lineStart;
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
			try
			{
				readLine();
			}
			catch (ParsingException ignored)
			{
				// If the skipped line was in the middle of a multilined command it will throw an error
			}
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
