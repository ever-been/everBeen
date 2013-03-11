package cz.cuni.mff.d3s.been.datastore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

public class BpkFromStore implements Bpk {

	private static final Logger log = LoggerFactory.getLogger(BpkFromStore.class);

	public BpkFromStore(StoreReader reader, BpkIdentifier identifier) {
		this.identifier = identifier;
		this.reader = reader;
	}

	final StoreReader reader;
	final BpkIdentifier identifier;

	@Override
	public BpkIdentifier getBpkIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return reader.getContentStream();
	}

	@Override
	@Deprecated
	public File getFile() {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("bpkFromStore", identifier.toString());
		} catch (IOException e) {
			log.error(
					"Could not create buffer file for BPK {} - {}",
					identifier.toString(),
					e.getMessage());
			return null;
		}
		FileOutputStream tempFileOs = null;
		try {
			tempFileOs = new FileOutputStream(tmpFile);
		} catch (IOException e) {
			log.error(
					"Could not open file \"{}\" for writing - {}.",
					tmpFile.getAbsolutePath(),
					e.getMessage());
			return null;
		}
		InputStream contentIs = null;
		try {
			contentIs = reader.getContentStream();
		} catch (IOException e) {
			log.error(
					"Failed to retrieve source stream for BPK {} - {}",
					identifier.toString(),
					e.getMessage());
			IOUtils.closeQuietly(tempFileOs);
			return null;
		}
		try {
			IOUtils.copy(contentIs, tempFileOs);
		} catch (IOException e) {
			log.error("Can't create TMP file for BPK {}", identifier.toString());
		}
		IOUtils.closeQuietly(tempFileOs);
		IOUtils.closeQuietly(contentIs);

		return tmpFile;
	}
}
