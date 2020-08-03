package compiler;

import compiler.upgrader.Version;

/**
 * Central reference for things related to files.
 */
public class FileStrings
{
	protected FileStrings() {}
	
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
	 * Path to the cheat sheet file
	 */
	public static final String RFC = Version.current().code + ".md";
	
	/**
	 * Grouping of the file extensions that are used.
	 */
	public static class FileExtensions
	{
		private FileExtensions() {}
		
		public static final String ZIP = ".zip";
		
		public static final String JSON = ".json";
		
		public static final String MCFUNCTION = ".mcfunction";
		
		public static final String SH = "sh";
		public static final String BAT = "bat";
		public static final String CMD = "cmd";
		
		public static String[] SCRIPTS() {return new String[]{"." + SH, "." + BAT, "." + CMD, ".jar"};}
	}
	
	/**
	 * Files that should be parsed from the data source directory
	 */
	public static String[] parseWhiteList() {return new String[]{".*\\" + FileExtensions.MCFUNCTION};}
}
