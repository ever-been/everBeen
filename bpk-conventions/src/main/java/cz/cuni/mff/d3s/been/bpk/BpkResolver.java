package cz.cuni.mff.d3s.been.bpk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class BpkResolver {

	public static BpkConfiguration resolve(File bpkFile) throws IOException, BpkConfigurationException {
		return resolve(new FileInputStream(bpkFile));
	}

	public static BpkConfiguration resolve(InputStream bpkIs) throws IOException, BpkConfigurationException {
		BpkConfiguration config = null;
		ZipArchiveInputStream bpkZipStream = new ZipArchiveInputStream(new BufferedInputStream(bpkIs));
		for (ArchiveEntry entry = bpkZipStream.getNextEntry(); entry != null; entry = bpkZipStream.getNextEntry()) {
			if (PackageNames.CONFIG_FILE.equals(entry.getName()) && bpkZipStream.canReadEntryData(entry)) {
				config = BpkConfigUtils.fromXml(bpkZipStream);
			}
		}
		bpkZipStream.close();
		return config;
	}

}
