package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cz.cuni.mff.d3s.been.bpk.PackageNames.FILES_DIR;

/**
 * This util is designed onaly and only to create simple Zip archive of
 * specified list of files (list of {@link FileToArchive})
 * 
 * @author donarus
 * 
 */
final class ZipUtil {

	/**
	 * Main method ot this util. Creates zip archive from specified files in
	 * specified output file.
	 * 
	 * @param files
	 *          files to archive
	 * @param output
	 *          archive output file
	 * @throws IOException
	 *           when some of specified files is missing, cannot be read or output
	 *           file cannot be opened with WRITE priviledes.
	 */
	public void createZip(List<FileToArchive> files, File output) throws IOException {
		// initialize 
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));

		// The directory entry must be present in the zip file
		out.putNextEntry(new ZipEntry(FILES_DIR + "/"));

		// fill archive
		addFilesToZip(files, out);
		
		// cleanup
		out.flush();
		out.close();
	}

	private void addFilesToZip(List<FileToArchive> files, ZipOutputStream out) throws IOException {
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		// Compress the files
		for (FileToArchive fto : files) {
			out.putNextEntry(new ZipEntry(fto.getPathInZip()));

			FileInputStream in = new FileInputStream(fto.getFile());
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			in.close();
			out.closeEntry();
		}
	}

}
