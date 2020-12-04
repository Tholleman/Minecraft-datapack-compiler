package compiler.initializer;

import compiler.properties.Property;
import compiler.properties.SetupException;
import compiler.upgrader.Version;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.MessageFormat;

import static compiler.initializer.InitializerStrings.*;
import static compiler.properties.Property.*;
import static java.io.File.separator;

public class Initialize
{
	private final String projectName;
	private final String namespace;
	
	private Initialize()
	{
		try
		{
			projectName = new File("./").getCanonicalFile().getName();
			namespace = projectName.trim().replaceAll("\\s+", "_").toLowerCase();
		}
		catch (IOException e)
		{
			throw new InitializeException(COULD_NOT_GET_PROJECT_NAME, e);
		}
	}
	
	public static void init()
	{
		new Initialize().followTemplate();
	}
	
	private void followTemplate()
	{
		try
		{
			File parentDir = new File(Initialize.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			File[] files = parentDir.listFiles();
			assert files != null;
			for (File file : files)
			{
				if (file.getName().equals(TEMPLATE_DIR_NAME) && file.isDirectory())
				{
					File[] toCopy = file.listFiles();
					assert toCopy != null;
					for (File child : toCopy)
					{
						templateCopy(child, projectName, "." + separator);
					}
					createConfigFile(projectName, "", 5, "");
					return;
				}
			}
			throw new InitializeException(MessageFormat.format(ERROR_NO_TEMPLATE_FOUND, parentDir.getPath()));
		}
		catch (URISyntaxException e)
		{
			throw new InitializeException(ERROR_NO_OWN_DIR, e);
		}
	}
	
	private void templateCopy(File toCopy, String projectName, String path)
	{
		String pathname = path + separator + replaceKeywords(toCopy.getName(), namespace, projectName);
		File copy = new File(pathname);
		if (toCopy.isDirectory())
		{
			if (!copy.mkdir())
			{
				throw new InitializeException(String.format(ERROR_CREATE_DIR, copy.getName()));
			}
			File[] files = toCopy.listFiles();
			assert files != null;
			for (File childToCopy : files)
			{
				templateCopy(childToCopy, projectName, pathname);
			}
		}
		else if (toCopy.isFile())
		{
			fileTemplateCopy(toCopy, copy, namespace, projectName);
		}
		else
		{
			throw new InitializeException(String.format(ERROR_UNKNOWN_FILE_TYPE, toCopy.getName()));
		}
	}
	
	private static void fileTemplateCopy(File toCopy, File copy, String namespace, String name)
	{
		for (String fileType : doNotParse())
		{
			if (toCopy.getName().endsWith(fileType))
			{
				try
				{
					Files.copy(toCopy.toPath(), copy.toPath());
				}
				catch (IOException e)
				{
					throw new InitializeException(String.format(ERROR_COPY, toCopy.getName(), copy.getName()), e);
				}
				return;
			}
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(toCopy));
		     FileWriter writer = new FileWriter(copy))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				writer.write(replaceKeywords(line + System.lineSeparator(), namespace, name));
			}
		}
		catch (IOException ioe)
		{
			throw new InitializeException(String.format(ERROR_COPY, toCopy.getName(), copy.getName()), ioe);
		}
	}
	
	private static String replaceKeywords(String todo, String namespace, String name)
	{
		return todo.replace(NAMESPACE_KEY, namespace).replace(NAME_KEY, name);
	}
	
	public static void createConfigFile(String projectName, String description, int format, String extraFiles)
	{
		try
		{
			Property.load();
		}
		catch (SetupException ignored)
		{
			// Fails when the file does not exist, this is not a problem
		}
		
		DATAPACK_NAME.setValueWhenEmpty(projectName);
		DATAPACK_DESCRIPTION.setValueWhenEmpty(description);
		DATAPACK_INCLUDE.setValueWhenEmpty(extraFiles);
		DATAPACK_FORMAT.setValueWhenEmpty("" + format);
		
		RESOURCEPACK_NAME.setValueWhenEmpty(projectName);
		RESOURCEPACK_DESCRIPTION.setValueWhenEmpty(description);
		RESOURCEPACK_INCLUDE.setValueWhenEmpty(extraFiles);
		RESOURCEPACK_FORMAT.setValueWhenEmpty("" + format);
		
		PARSE_STANDARD.setValueWhenEmpty(Version.current().code);
		BLACKLIST.setValueWhenEmpty(".*\\\\.txt, .*\\\\.md");
		ZIP.setValueWhenEmpty("true");
		CLEAN_AFTER.setValueWhenEmpty("false");
		PREFER_RESOURCEPACK_MCMETA.setValueWhenEmpty("false");
		
		Property.add("dev", "true");
		
		Property.store();
	}
}
