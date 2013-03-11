package cz.cuni.mff.d3s.been.swrepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.cuni.mff.d3s.been.datastore.StoreReader;

/**
 * A {@link FSBasedStore}-related implementation of {@link StoreReader}.
 * 
 * @author darklight
 * 
 */
class FSBasedStoreReader implements StoreReader {

	/** The file this reader is reading */
	private final File readFile;

	FSBasedStoreReader(File readFile) {
		this.readFile = readFile;
	}

	@Override
	public InputStream getContentStream() {
		try {
			return new FileInputStream(readFile);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public long getContentLength() {
		if (readFile == null) {
			return -1l;
		} else {
			return readFile.length();
		}
	}

}
