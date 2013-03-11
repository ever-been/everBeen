package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Tadeáš Palusga
 * 
 */
public class ZipFileUtil {

	/**
	 * @param zipFile
	 * @param toDir
	 * @throws IOException
	 * @throws ZipException
	 */
	public static void unzipToDir(File zipFile, File toDir) throws ZipException, IOException {
		ZipFile _zipFile = new ZipFile(zipFile);
		Enumeration<?> files = _zipFile.entries();
		File f = null;
		FileOutputStream fos = null;

		while (files.hasMoreElements()) {
			try {
				ZipEntry entry = (ZipEntry) files.nextElement();
				InputStream eis = _zipFile.getInputStream(entry);
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				f = new File(toDir.getAbsolutePath() + File.separator + entry.getName());

				if (entry.isDirectory()) {
					f.mkdirs();
					continue;
				} else {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}

				fos = new FileOutputStream(f);

				while ((bytesRead = eis.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}
		_zipFile.close();
	}

}