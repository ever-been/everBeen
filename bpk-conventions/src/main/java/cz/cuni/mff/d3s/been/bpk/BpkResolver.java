package cz.cuni.mff.d3s.been.bpk;

import java.io.*;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

/**
 * Utility class for reading BpkConfiguration.
 */
public class BpkResolver {

	public static BpkConfiguration resolve(File bpkFile) throws BpkConfigurationException {
		try (FileInputStream fis = new FileInputStream(bpkFile)) {
			return resolve(fis);
		} catch (IOException e) {
			throw new BpkConfigurationException("Cannot read configuration from " + bpkFile, e);
		}
	}

	public static BpkConfiguration resolve(Path bpkPath) throws BpkConfigurationException {
		return resolve(bpkPath.toFile());
	}

	public static BpkConfiguration resolve(InputStream bpkIs) throws BpkConfigurationException {
		BpkConfiguration config = null;
		try (ZipArchiveInputStream bpkZipStream = new ZipArchiveInputStream(new BufferedInputStream(bpkIs))) {
			for (ArchiveEntry entry = bpkZipStream.getNextEntry(); entry != null; entry = bpkZipStream.getNextEntry()) {
				if (BpkNames.CONFIG_FILE.equals(entry.getName()) && bpkZipStream.canReadEntryData(entry)) {
					config = BpkConfigUtils.fromXml(bpkZipStream);
					break;
				}
			}
		} catch (IOException e) {
			throw new BpkConfigurationException("Cannot read configuration from the stream!", e);
		}

		if (config == null) {
			throw new BpkConfigurationException("Cannot find configuration in the archive!");
		} else {
			return config;
		}
	}
}
