package cz.cuni.mff.d3s.been.hostruntime;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * Utility class for unzipping files/streams.
 * 
 * 
 * TODO we should move this to utility module or use 3rd party library
 * 
 * @author Tadeáš Palusga
 * @author Martin Sixta
 * 
 */
public class ZipFileUtil {

	/**
	 * Unzips a file to specified direcotry.
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
		try (ZipInputStream zipStream = new ZipInputStream(is)) {
			ZipEntry entry = zipStream.getNextEntry();

			while (entry != null) {

				File f = new File(toDir.getAbsolutePath() + File.separator + entry.getName());

				if (entry.isDirectory()) {
					f.mkdirs();

				} else {
					f.getParentFile().mkdirs();
					f.createNewFile();

					byte[] buffer = new byte[1024];
					int bytesRead = 0;

					try (FileOutputStream fos = new FileOutputStream(f)) {
						while ((bytesRead = zipStream.read(buffer)) != -1) {
							fos.write(buffer, 0, bytesRead);
						}
					}
				}

				entry = zipStream.getNextEntry();
			}

			zipStream.closeEntry();

		}
	}

}
