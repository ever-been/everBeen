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

}
