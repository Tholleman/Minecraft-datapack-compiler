package compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static compiler.constants.Identifiers.*;
import static compiler.constants.MetaTags.*;

public class Help
{
	private Help() {}
	
	public static void cheatSheet()
	{
		try (FileWriter fw = new FileWriter(new File(FileStrings.RFC)))
		{
			fw.write("## Behaviour\n" +
			         "A directory will be copied with all the files inside of it.\n" +
			         "- Empty lines are removed.\n" +
			         "- A command that is spread over multiple lines is combined with all leading spaces removed.\n" +
			         "- Meta tags are acted on.\n" +
			         "\n" +
			         "## Lines\n" +
			         "There are 3 kinds of lines in a file\n" +
			         "- An output line which starts with a `" + COMMAND_PREFIX + "`.\n" +
			         "- A meta line which starts with a `" + META_PREFIX + " `.\n" +
			         "- A comment which starts with a `" + COMMENT_PREFIX + "`.\n" +
			         "\n" +
			         "## Acceptable meta tags\n" +
			         "### Inline meta tags\n" +
			         "Inline meta tags can be used anywhere, including inside meta tags.\n" +
			         "\n" +
			         "#### `" + INLINE_META_PREFIX + "name" + INLINE_META_SUFFIX + "`\n" +
			         "For variables\n" +
			         "\n" +
			         "#### `" + INLINE_META_PREFIX + "# operator #" + INLINE_META_SUFFIX + "`\n" +
			         "To perform a operation integer number operation.\n" +
			         "\n" +
			         "Valid operators are: `+`, `-`, `*`, and `/`\n" +
			         "\n" +
			         "### Line meta tags\n" +
			         "#### `\\" + FILE + " #`\n" +
			         "Will read a file as if the lines are written in the current file.\n" +
			         "Can be used to set up default variables or go the other way and fill a template.\n" +
			         "\n" +
			         "#### `\\" + REPEAT + " #`\n" +
			         "Repeat the next line `#` times.\n" +
			         "Has to be an integer.\n" +
			         "0 or negative is allowed but the line will be ignored.\n" +
			         "\n" +
			         "#### `\\" + VAR + " #1 #2`\n" +
			         "Set a variable to be used later\n" +
			         "\n" +
			         "#### `\\" + IF + " #`\n" +
			         "Will parse the next file if boolean expression is true.\n" +
			         "##### Boolean expressions\n" +
			         "- `" + TRUE + "` or `" + FALSE + "`\n" +
			         "- `" + EXISTS + " #` to check if a variable has a value (use the actual identifier, not `" + INLINE_META_PREFIX + "identifier" + INLINE_META_SUFFIX + "`)\n" +
			         "- `" + NOT + " #` to invert a boolean expression.\n" +
			         "- `# " + AND + " #` to check two boolean expressions.\n" +
			         "- `# " + OR + " #` to check at least one boolean expression.\n" +
			         "- `# " + EQUALS + " #` to \n" +
			         "- `# operator #`\n" +
			         "  - `" + GT + "`\n" +
			         "  - `" + GE1 + "` or `" + GE2 + "`\n" +
			         "  - `" + ST + "`\n" +
			         "  - `" + SE1 + "` or `" + SE2 + "`\n" +
			         "\n" +
			         "# Escape character\n" +
			         "It's possible to escape characters with a `" + ESCAPE + "`");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
