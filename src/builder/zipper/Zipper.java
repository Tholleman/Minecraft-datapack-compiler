package builder.zipper;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip files and directories with {@link Zipper#zip(File[], String)} into 1 .zip file
 *
 * @author Thomas Holleman
 */
public class Zipper
{
	private Zipper() {}
	
	/**
	 * A constants for buffer size used to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;
	
	/**
	 * Compresses a list of files to a destination zip file
	 *
	 * @param listFiles   A collection of files and directories
	 * @param destZipFile The path of the destination zip file
	 *
	 * @throws IOException Could be thrown while zipping a file
	 */
	public static void zip(File[] listFiles, String destZipFile) throws IOException
	{
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile)))
		{
			for (File file : listFiles)
			{
				zip(file.getName(), file, zos);
			}
			zos.flush();
		}
	}
	
	/**
	 * Zip a given file/directory
	 *
	 * @param path The path of the file including the file/directory itself!
	 * @param file The file to zip
	 * @param zos  The output stream to write the file(s) to
	 *
	 * @throws IOException Could be thrown while zipping a file
	 */
	private static void zip(String path, File file, ZipOutputStream zos) throws IOException
	{
		assert path.endsWith(file.getName());
		if (file.isDirectory())
		{
			//noinspection ConstantConditions
			for (File supFile : file.listFiles())
			{
				zip(path + "/" + supFile.getName(), supFile, zos);
			}
		}
		else
		{
			write(path, file, zos);
		}
	}
	
	/**
	 * Zip a single file
	 *
	 * @param entry The path of the file including the file itself
	 * @param file  The file to zip
	 * @param zos   The output stream to write to
	 *
	 * @throws IOException Could be thrown throughout the zipping process
	 */
	private static void write(String entry, File file, ZipOutputStream zos) throws IOException
	{
		assert entry.endsWith(file.getName());
		assert file.isFile();
		zos.putNextEntry(new ZipEntry(entry));
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)))
		{
			byte[] bytesIn = new byte[BUFFER_SIZE];
			int read;
			while ((read = bis.read(bytesIn)) != -1)
			{
				zos.write(bytesIn, 0, read);
			}
			zos.closeEntry();
		}
	}
}
