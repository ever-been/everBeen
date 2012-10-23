package cz.cuni.mff.been.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * Utility facade that permits creation and extraction of tar archives.
 * 
 * @author darklight
 * 
 */
public final class TarUtils {

	/**
	 * Prevent instantiation.
	 */
	private TarUtils() {}

	/**
	 * Compress a given file/folder. If the target folder doesn't exist, create
	 * it.
	 * 
	 * @param source
	 *          File/folder to package.
	 * @param targetDir
	 *          Directory to package to.
	 * @param targetFileName
	 *          How to name the archive file.
	 * 
	 * @throws FileNotFoundException
	 *           When the source file can not be found.
	 * @throws IOException
	 *           On error while writing to target.
	 */
	public static void compress(File source, File targetDir, String targetFileName) throws FileNotFoundException, IOException {
		if (source == null) {
			throw new NullPointerException("Source file is null.");
		}
		if (targetDir == null) {
			throw new NullPointerException("Target directory is null.");
		}
		if (targetFileName == null) {
			throw new NullPointerException("File name is null.");
		}

		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}

		if (!targetDir.isDirectory()) {
			throw new IllegalArgumentException("The provided extraction folder is not a directory.");
		}

		File targetFile = new File(targetDir, targetFileName);
		if (targetFile.exists()) {
			FileUtils.delete(targetFile);
		}

		TarArchiveOutputStream targetStream = new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile))));
		addEntriesToArchiveRecursively(source, targetStream);
		targetStream.flush();
		targetStream.close();
	}

	/**
	 * Compress a given file/folder. If target folder doesn't exist, create it.
	 * Use default file name.
	 * 
	 * @see #compress(File, File, String)
	 * 
	 * @param source
	 *          Source to package.
	 * @param targetDir
	 *          Directory to package to.
	 * 
	 * @throws FileNotFoundException
	 *           In case source file/dir is not found.
	 * @throws IOException
	 *           If writing fails.
	 */
	public static void compress(File source, File targetDir) throws FileNotFoundException, IOException {
		compress(source, targetDir, source.getName() + ".tgz");
	}

	/**
	 * Extract the content of a tgz archive.
	 * 
	 * @param source
	 *          TGZ archive to unpack.
	 * @param target
	 *          Folder to extract the archive to.
	 * @throws FileNotFoundException
	 *           When the source file/dir cannot be found.
	 * @throws IOException
	 *           On error reading the input.
	 * @throws ZipException
	 *           When the provided source is not a valid gZip
	 * 
	 */
	public static void extract(File source, File target) throws FileNotFoundException, IOException, ZipException {
		if (source == null) {
			throw new NullPointerException("Extraction source is null.");
		}
		if (target == null) {
			throw new NullPointerException("Extraction target is null.");
		}
		if (!source.exists()) {
			throw new FileNotFoundException("Extraction source can not be found.");
		}
		if (!target.exists()) {
			target.mkdirs();
		} else {
			if (!target.isDirectory()) {
				throw new IllegalArgumentException("Extraction target is not a directory.");
			}
		}

		TarArchiveInputStream sourceStream = new TarArchiveInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(source))));

		for (TarArchiveEntry entry = (TarArchiveEntry) sourceStream.getNextEntry(); entry != null; entry = (TarArchiveEntry) sourceStream.getNextEntry()) {
			if (entry.isDirectory()) {
				File dir = new File(target, entry.getName());
				dir.mkdirs();
				continue;
			}
			if (entry.isFile()) {
				File targetFile = new File(target, entry.getName());
				targetFile.createNewFile();
				OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile));
				IOUtils.copy(sourceStream, os);
				os.close();
				continue;
			}
		}

		sourceStream.close();
	}
	static void addEntriesToArchiveRecursively(File file,
			TarArchiveOutputStream os) throws IOException {
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				addEntriesToArchiveRecursively(subFile, os);
			}
		} else {
			TarArchiveEntry entry = new TarArchiveEntry(file);
			os.putArchiveEntry(entry);
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			IOUtils.copy(is, os);
			is.close();
			os.closeArchiveEntry();
		}
	}
}
