package cz.cuni.mff.been.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
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
	 * 
	 * @throws NullPointerException
	 *           When the search root or the filter regex is null
	 * @throws IOException
	 *           On I/O error or if the search root cannot be found.
	 */
	public static Iterable<File> findFilesRecursivelyByName(File searchRoot,
			String nameRegex) throws NullPointerException, IOException {
		if (searchRoot == null) {
			throw new NullPointerException("Provided search root was null.");
		}
		if (nameRegex == null) {
			throw new NullPointerException("Provided filter regex was null.");
		}
		if (!searchRoot.exists()) {
			throw new FileNotFoundException("Provided search root doesn't exist.");
		}

		Stack<File> pathsToSearch = new Stack<File>();
		SortedSet<File> result = new TreeSet<File>();
		Filter<Path> filter = new RegexNameFilter(nameRegex);

		if (!searchRoot.isDirectory()) {
			if (filter.accept(searchRoot.toPath())) {
				result.add(searchRoot);
			}
			return result;
		}

		for (pathsToSearch.push(searchRoot); !pathsToSearch.isEmpty();) {
			File currentDir = pathsToSearch.pop();
			Iterator<Path> fi = Files.newDirectoryStream(currentDir.toPath(), filter).iterator();
			while (fi.hasNext()) {
				result.add(fi.next().toFile());
			}
			Iterator<Path> di = Files.newDirectoryStream(currentDir.toPath(), new DirectoryFilter()).iterator();
			while (di.hasNext()) {
				pathsToSearch.push(di.next().toFile());
			}
		}
		return result;
	}
	static class RegexNameFilter implements Filter<Path> {
		final Pattern nameMatcher;

		RegexNameFilter(String nameRegex) {
			nameMatcher = Pattern.compile(nameRegex);
		}

		@Override
		public boolean accept(Path file) {
			return nameMatcher.matcher(file.toFile().getName()).matches();
		}
	}

	static class DirectoryFilter implements Filter<Path> {
		@Override
		public boolean accept(Path entry) throws IOException {
			return entry.toFile().isDirectory();
		}
	}

}
