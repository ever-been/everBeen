package cz.cuni.mff.been.utils;

import java.io.File;

public class FileUtils {

	private FileUtils() {
		// instantiation not available
	}

	/**
	 * Construct and return path to file with given name in specified directory.
	 * (Simply join these two parts as dirName + {@link File#separator} +
	 * fileName... {@link File#separator} used only and only if needed.
	 * 
	 * @param dirName
	 * @param fileName
	 * @return constructed path
	 */
	public static String constructPath(String dirName, String fileName) {
		return dirName + (dirName.endsWith(File.separator) ? "" : File.separator) + fileName;
	}

	/**
	 * Delete directory recursively
	 * 
	 * @param file
	 *          file or directory to delete
	 * @return true if file has been deleted, false otherwise
	 */
	public static boolean delete(File file) {
		if (file.exists() && file.isDirectory()) {
			for (File childFile : file.listFiles()) {
				if (childFile.isDirectory()) {
					delete(childFile);
				} else {
					childFile.delete();
				}
			}
		}
		return (file.delete());
	}

}
