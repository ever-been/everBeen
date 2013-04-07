package cz.cuni.mff.d3s.been.bpkplugin;

import static cz.cuni.mff.d3s.been.bpk.PackageNames.FILES_DIR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This util is designed only and only to create simple ZIP archive of specified
 * list of files (list of {@link FileToArchive})
 * 
 * @author Tadeas Palusga
 * 
 */
final class ZipUtil {

	/**
	 * Main method of this util. Creates ZIP archive from specified files in
	 * specified output file.
	 * 
	 * @param files
	 *          files to archive
	 * @param output
	 *          archive output file
	 * @throws IOException
	 *           when some of specified files is missing, cannot be read or output
	 *           file cannot be opened with WRITE privileges.
	 */
	public void createZip(Collection<ItemToArchive> items, File output) throws IOException {
		// initialize 
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));

		// The directory entry must be present in the ZIP file
		out.putNextEntry(new ZipEntry(FILES_DIR + File.separator));

		// fill archive
		addFilesToZip(items, out);

		// cleanup
		out.flush();
		out.close();
	}

	private void addFilesToZip(Collection<ItemToArchive> items,
			ZipOutputStream out) throws IOException {
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		// Compress the files
		for (ItemToArchive item : items) {
			out.putNextEntry(new ZipEntry(item.getPathInZip()));

			try (InputStream in = item.getInputStream()) {
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} catch (IOException e) {
				throw e;
			}

			out.closeEntry();
		}
	}

}
