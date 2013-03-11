package cz.cuni.mff.d3s.been.bpk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpkWithFile implements Bpk {
	private static final Logger log = LoggerFactory.getLogger(BpkWithFile.class);

	final BpkIdentifier identifier;
	final File file;

	BpkWithFile(BpkIdentifier identifier, File file) {
		this.identifier = identifier;
		this.file = file;
	}

	@Override
	public BpkIdentifier getBpkIdentifier() {
		return identifier;
	}

	@Override
	public InputStream getInputStream() {
		try {
			return new FileInputStream(file);
		} catch (IOException e) {
			log.error(
					"Failed to open RO stream to file \"{}\" - {}.",
					file.getAbsolutePath(),
					e.getMessage());
			return null;
		}
	}
	@Override
	public File getFile() {
		return file;
	}
}
