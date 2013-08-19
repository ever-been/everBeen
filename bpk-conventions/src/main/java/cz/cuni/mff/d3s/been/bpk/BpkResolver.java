package cz.cuni.mff.d3s.been.bpk;

import java.io.*;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

/**
 * Utility class for reading BpkConfiguration.
 */
public class BpkResolver {

	/**
	 * Reads the BPK configuration from the specified file.
	 * 
	 * @param bpkFile
	 *          the file to parse
	 * @return parsed BPK configuration
	 * @throws BpkConfigurationException
	 *           when the input is invalid or an I/O error occurs
	 */
	public static BpkConfiguration resolve(File bpkFile) throws BpkConfigurationException {
		try (FileInputStream fis = new FileInputStream(bpkFile)) {
			return resolve(fis);
		} catch (IOException e) {
			throw new BpkConfigurationException("Cannot read configuration from " + bpkFile, e);
		}
	}

	/**
	 * Reads the BPK configuration from the specified file.
	 * 
	 * @param bpkPath
	 *          the path to the file to parse
	 * @return parsed BPK configuration
	 * @throws BpkConfigurationException
	 *           when the input is invalid or an I/O error occurs
	 */
	public static BpkConfiguration resolve(Path bpkPath) throws BpkConfigurationException {
		return resolve(bpkPath.toFile());
	}

	/**
	 * Reads the BPK configuration from the specified input stream.
	 * 
	 * @param bpkIs
	 *          the input stream to parse
	 * @return parsed BPK configuration
	 * @throws BpkConfigurationException
	 *           when the input is invalid or an I/O error occurs
	 */
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
