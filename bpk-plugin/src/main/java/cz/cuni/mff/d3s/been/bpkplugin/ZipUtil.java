package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public void createZip(List<FileToArchive> files, File output)
			throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));
		addFilesToZip(files, out);
		out.flush();
		out.close();
	}

	private void addFilesToZip(List<FileToArchive> files, ZipOutputStream out)
			throws IOException {
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
