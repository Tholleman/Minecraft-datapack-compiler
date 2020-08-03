package compiler.initializer;

public class InitializerStrings extends compiler.FileStrings
{
	public static final String COULD_NOT_GET_PROJECT_NAME = "Could not get project name";
	public static final String TEMPLATE_DIR_NAME = "template";
	public static final String NAMESPACE_KEY = "<<namespace>>";
	public static final String NAME_KEY = "<<name>>";
	public static final String ERROR_NO_TEMPLATE_FOUND = "No template found.\n" +
	                                                     "Create a directory with the name \"template\" in {0}\n" +
	                                                     "The string " + NAMESPACE_KEY + " will be replaced with the actual namespace\n" +
	                                                     "The string " + NAME_KEY + " will be replaced with the directory name";
	public static final String ERROR_NO_OWN_DIR = "Could not find own directory and therefor not the template directory";
	public static final String ERROR_CREATE_DIR = "Could not create directory %s";
	public static final String ERROR_UNKNOWN_FILE_TYPE = "\"%s\" is not a directory or file";
	public static final String ERROR_COPY = "Could not copy %s to %s";
	
	public static String[] doNotParse() {return new String[]{".png"};}
}
