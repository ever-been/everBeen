package cz.cuni.mff.been.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Utility facade for advanced file operations.
 * 
 * @author Tadeáš Palusga
 * 
 */
public final class FileUtils extends org.apache.commons.io.FileUtils {

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
	 * Delete file (no matter if file or directory) recursively. If you want
	 * file/directory check, see {@link FileUtils#deleteDirectory(File)} or
	 * {@link FileUtils#deleteFile(File)}
	 * 
	 * @param file
	 *          file or directory to delete
	 * @throws IOException
	 *           when file cannot be deleted from some reason
	 */
	public static void delete(File file) throws IOException {
		if (file.exists() && file.isDirectory()) {
			for (File childFile : file.listFiles()) {
				if (childFile.isDirectory()) {
					delete(childFile);
				} else {
					Files.delete(childFile.toPath());
				}
			}
		}
		Files.delete(file.toPath());
	}

	/**
	 * 
	 * This is similiar to deleteDirectory but does not throws IOException when
	 * the dir parameter does not exist or is not a directory ...
	 * 
	 * TODO: review/rewrite this function
	 * 
	 * @param dir
	 *          directory to be deleted
	 * @throws IOException
	 *           if given file is cannot be deleted from some reason
	 */
	public static void safeDeleteDirectory(File dir) throws IOException {
		if (!dir.exists()) {
			return;
		} else if (!dir.isDirectory()) {
			deleteFile(dir);
			return;
		} else {
			delete(dir);
		}
	}

	/**
	 * 
	 * Deletes the directory recursively (that is including its content).
	 * 
	 * @param dir
	 *          directory to be deleted
	 * @throws IOException
	 *           if given file is not directory or cannot be deleted from some
	 *           reason
	 */
	public static void deleteDirectory(File dir) throws IOException {
		if (!dir.isDirectory()) {
			throw new IOException(String.format("The dir argument '%s' does not refer to an existing directory.", dir != null
					? dir.getAbsolutePath() : "null"));
		}
		delete(dir);
	}

	/**
	 * 
	 * Deletes file.
	 * 
	 * @param file
	 *          file to be deleted
	 * @throws IOException
	 *           if given file is not file (is directory) or cannot be deleted
	 *           from some reason
	 */
	public static void deleteFile(File file) throws IOException {
		if (file.isDirectory()) {
			throw new IOException(String.format("The file argument '%s' does not refer to an existing file.", file != null
					? file.getAbsolutePath() : "null"));
		}
		delete(file);
	}

	/**
	 * Change file mode recursively in posix unix-like style (eg. rwxr-x---).
	 * 
	 * Ignored on operating systems, where chmod is not supported.
	 * 
	 * @param file
	 *          file or directory which should be chmoded
	 * @param perms
	 *          posix unix-like permissions
	 * @throws IOException
	 *           if something goes wrong
	 */
	public static void recursiveChmod(File file, String perms) throws IOException {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				recursiveChmod(f, perms);
			}
		}
		chmod(file, perms);
	}

	/**
	 * Change file mode in posix unix-like style (eg. rwxr-x---).
	 * 
	 * Ignored on operating systems, where chmod is not supported.
	 * 
	 * @param targetFile
	 *          file or directory which should be chmoded
	 * @param perms
	 *          posix unix-like permissions
	 * @throws IOException
	 *           if something goes wrong
	 */
	public static void chmod(File targetFile, String perms) throws IOException {
		try {
			Files.setPosixFilePermissions(targetFile.toPath(), java.nio.file.attribute.PosixFilePermissions.fromString(perms));
		} catch (UnsupportedOperationException e) {
			/* on windows-like systems - ignore */
		}
	}

	/**
	 * Find all files/folders whose name matches the specified regex. The search
	 * proceeds depth-first.
	 * 
	 * @param searchRoot
	 *          The root of the search.
	 * @param nameRegex
	 *          Matcher regex for the file name.
	 * 
	 * @return All files whose name matched the regex. The ordering is kept as
	 *         returned by the DFS.
	 */
	public static Iterable<File> findFilesRecursivelyByName(File searchRoot,
			String nameRegex) {
		if (searchRoot == null) {
			throw new NullPointerException("Provided search root was null.");
		}
		if (nameRegex == null) {
			throw new NullPointerException("Provided filter regex was null.");
		}

		Stack<File> pathsToSearch = new Stack<File>();
		List<File> result = new LinkedList<File>();
		FilenameFilter filter = new RegexFilter(nameRegex);

		if (!searchRoot.isDirectory()) {
			if (filter.accept(searchRoot.getParentFile(), searchRoot.getName())) {
				result.add(searchRoot);
			}
			return result;
		}

		for (pathsToSearch.push(searchRoot); !pathsToSearch.isEmpty();) {
			File currentDir = pathsToSearch.pop();
			for (File f : currentDir.listFiles(filter)) {
				result.add(f);
			}
			for (File f : currentDir.listFiles()) {
				if (f.isDirectory()) {
					pathsToSearch.push(f);
				}
			}
		}
		return result;
	}

	static class RegexFilter implements FilenameFilter {
		final Pattern nameMatcher;

		RegexFilter(String nameRegex) {
			nameMatcher = Pattern.compile(nameRegex);
		}

		@Override
		public boolean accept(File dir, String name) {
			return nameMatcher.matcher(name).matches();
		}
	}
}
