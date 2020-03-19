package builder.constants;

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
	
	public static final String CONFIG_PATH = "config.properties";
	
	public static final String PACK_DOT_MCMETA = "pack.mcmeta";
	
	/**
	 * String of the archive file type
	 * <p>
	 * Used to make and recognize zip files
	 */
	public static final String ZIP = ".zip";
	
	public static final String JSON = ".json";
	
	/**
	 * Files that should be ignored from the data source directory
	 */
	public static String[] zipBlackList() { return new String[]{".*\\.mctemplate", ".*\\.md", ".*\\.txt"};}
	
	/**
	 * Files that should be parsed from the data source directory
	 */
	public static String[] parseWhiteList() {return new String[]{".*\\.mcfunction"};}
}
