package cz.cuni.mff.d3s.been.util;

import java.io.*;
import java.util.Collection;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility facade designed to help with packing and unpacking of ZIP files.
 * 
 * @author Tadeas Palusga
 * 
 */
public final class ZipUtil {

	private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

	/**
	 * Main method of this util. Creates ZIP archive from specified files in
	 * specified output file.
	 * 
	 * @param items
	 *          files to archive
	 * @param outputFile
	 *          archive output file
	 * @throws IOException
	 *           when some of specified files is missing, cannot be read or output
	 *           file cannot be opened with WRITE privileges.
	 */
	public static void createZip(Collection<ItemToArchive> items, File outputFile) throws IOException {
		final ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		addFilesToZip(items, zaos);
		zaos.finish();
		zaos.close();
	}

	private static void addFilesToZip(Collection<ItemToArchive> items, ZipArchiveOutputStream zar) throws IOException {
		for (ItemToArchive item : items) {
			final String fixedName = (item.isDirectory()) ? nameWithSlash(item.getPathInZip())
					: nameWithoutSlash(item.getPathInZip());
			final ZipArchiveEntry zae = new ZipArchiveEntry(fixedName);
			zae.setSize(item.getSize());
			zar.putArchiveEntry(zae);

			if (!item.isDirectory()) {
				try (InputStream in = item.getInputStream()) {
					IOUtils.copy(in, zar);
				} catch (IOException e) {
					throw e;
				}
			}

			zar.closeArchiveEntry();
		}
	}

	/**
	 * Unzips a file to specified directory.
	 * 
	 * @param zipFile
	 *          file to unzip
	 * @param toDir
	 *          where to unzip
	 * @throws IOException
	 *           when the file cannot be unzipped to the directory
	 */
	public static void unzipToDir(File zipFile, File toDir) throws IOException {
		try (FileInputStream fis = new FileInputStream(zipFile)) {
			unzipToDir(fis, toDir);
		}
	}

	/**
	 * 
	 * Unzips content of a stream to specified directory.
	 * 
	 * @param is
	 *          stream to unzip
	 * @param toDir
	 *          where to unzip
	 * @throws IOException
	 *           when the content of the stream cannot be unzipped to specified
	 *           directory
	 */
	public static void unzipToDir(InputStream is, File toDir) throws IOException {
		try (ZipArchiveInputStream zipStream = new ZipArchiveInputStream(is)) {

			final long maxEntrySize = Integer.MAX_VALUE;

			while (true) {
				final ArchiveEntry entry = zipStream.getNextEntry();
				if (entry == null) {
					break;
				}

				if (!zipStream.canReadEntryData(entry)) {
					log.warn("Can't read entry \"{}\" from stream, skipping it", entry);
					continue;
				}

				final long entrySize = entry.getSize();

				if (entrySize > maxEntrySize) {
					log.error("Entry {} is too big", entry.toString());
					continue;
				}

				final File f = new File(toDir.getAbsolutePath() + File.separator + entry.getName());

				if (entry.isDirectory()) {
					f.mkdirs();
				} else {
					f.getParentFile().mkdirs();
					f.createNewFile();

					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(f);

						if (entrySize == -1) {
							log.debug("Size of entry {} is unknown", entry);
						}
						IOUtils.copy(zipStream, fos);

						log.debug("Copied ZIP entry \"{}\"", entry.toString());
					} catch (IOException e) {
						log.error("Problem copying ZIP entry \"{}\"", entry.toString(), e);
					} finally {
						try {
							fos.close();
						} catch (IOException e) {
							log.error("Cannot close write destination file \"{}\"", f.getAbsolutePath());
						}
					}
				}
			}

			zipStream.close();

		}
	}

	private static final String nameWithSlash(String name) {
		return (name.endsWith("/")) ? name : name + "/";
	}

	private static final String nameWithoutSlash(String name) {
		return (name.endsWith("/")) ? name.substring(0, name.length() - 1) : name;
	}
}
