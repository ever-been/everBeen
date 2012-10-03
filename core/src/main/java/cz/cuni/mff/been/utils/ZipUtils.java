package cz.cuni.mff.been.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
	
	/** Size of the buffer used when extracting or compressing files. */
	private static final int BUFFER_SIZE = 4096;
	
	private ZipUtils() {
		// isntantiation not available
	}
	
	
	/**
	 * Extract contents of the ZIP file to given path.
	 * 
	 * @param zipFilename
	 *          ZIP file to extract
	 * @param path
	 *          path to extract the files
	 * @throws IOException
	 *           if the extraction fails
	 */
	public static void extractZipFile(String zipFilename, String path) throws IOException {
		ZipFile zipFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			zipFile = new ZipFile(zipFilename);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (entry.isDirectory()) {
					new File(path, entry.getName()).mkdirs();
				} else {
					// assemble zip entry path and ensure directories are created
					String fName = FileUtils.constructPath(path, entry.getName());
					String dirName = fName.substring(0, fName.lastIndexOf(File.separator));
					File dir = new File(dirName);
					if (!dir.exists()) {
						dir.mkdirs();
					}

					inputStream = new BufferedInputStream(zipFile.getInputStream(entry), BUFFER_SIZE);
					outputStream = new BufferedOutputStream(new FileOutputStream(fName), BUFFER_SIZE);
					
					byte[] buffer = new byte[BUFFER_SIZE];
					int bytesRead;
					while((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);						
					}
					
					IOUtils.closeCloseableQuitely(outputStream);
					IOUtils.closeCloseableQuitely(inputStream);
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeCloseableQuitely(zipFile);
			IOUtils.closeCloseableQuitely(inputStream);
			IOUtils.closeCloseableQuitely(outputStream);
		}
	}
	
}
