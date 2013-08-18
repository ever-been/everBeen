package cz.cuni.mff.d3s.been.api;

import java.io.IOException;
import java.io.InputStream;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * A class that represents a BPK file and that can retrieve its BPK identifier
 * from its data file.
 * 
 * @author donarus
 */
public interface BpkHolder extends AutoCloseable {

	/**
	 * Retrieves the BPK identifier from the file that this objects represents.
	 * 
	 * @return the BPK identifier of the BPK package
	 * @throws BpkConfigurationException
	 *           when the configuration in the BPK file is invalid
	 * @throws IOException
	 *           when the file cannot be read or an I/O error occurs
	 */
	BpkIdentifier getBpkIdentifier() throws BpkConfigurationException, IOException;

	/**
	 * Returns an {@link InputStream} object with the data of the BPK file.
	 * 
	 * @return an input stream with the data of the BPK file
	 * @throws IOException
	 *           when the file cannot be read or an I/O error occurs
	 */
	InputStream getInputStream() throws IOException;

}
