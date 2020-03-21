package compiler;

/**
 * Central reference for things related to files.
 */
public class FileStrings
{
	private FileStrings() {}
	
	/**
	 * The directory that should be parsed and or copied
	 */
	public static final String SOURCE_DIRECTORY = "data source";
	
	/**
	 * The directory that should be the place where the files from {@link FileStrings#SOURCE_DIRECTORY} should be parsed/copied to
	 */
	public static final String OUTPUT_DIRECTORY = "data";
	
	/**
	 * Path to the config file
	 */
	public static final String CONFIG_PATH = "config.properties";
	
	/**
	 * Path to the pack.mcmeta file
	 */
	public static final String PACK_DOT_MCMETA = "pack.mcmeta";
	
	/**
	 * Grouping of the file extensions that are used.
	 */
	public static class FileExtensions
	{
		private FileExtensions() {}
		
		public static final String ZIP = ".zip";
		
		public static final String JSON = ".json";
		
		public static final String MCFUNCTION = ".mcfunction";
		
		public static final String MCTEMPLATE = ".mctemplate";
		
		public static final String MARKDOWN = ".md";
		
		public static final String TEXT = ".txt";
	}
	
	/**
	 * Files that should be ignored from the data source directory
	 */
	public static String[] zipBlackList()
	{
		return new String[]{".*\\" + FileExtensions.MCTEMPLATE,
		                    ".*\\" + FileExtensions.MARKDOWN,
		                    ".*\\" + FileExtensions.TEXT};
	}
	
	/**
	 * Files that should be parsed from the data source directory
	 */
	public static String[] parseWhiteList() {return new String[]{".*\\" + FileExtensions.MCFUNCTION};}
}
