package cz.cuni.mff.d3s.been.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.bpk.MetaInf;

/**
 * Class that represent a single BPK package file and its BPK identifier.
 * 
 * @author donarus
 */
public class BpkFileHolder implements BpkHolder {

	/** A file representing the BPK package */
	private File bpkFile;

	/** The BPK identifier of the package */
	private BpkIdentifier bpkIdentifier;

	/**
	 * Default constructor, initializes the object with the specified file as the
	 * BPK package.
	 * 
	 * @param bpkFile
	 *          the file with the BPK package
	 * @throws IOException
	 *           when the file cannot be read or an I/O error occurs
	 * @throws BpkConfigurationException
	 *           when the configuration in the BPK file is invalid
	 */
	public BpkFileHolder(File bpkFile) throws IOException, BpkConfigurationException {
		this.bpkFile = bpkFile;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(bpkFile);
	}

	@Override
	public BpkIdentifier getBpkIdentifier() throws BpkConfigurationException, IOException {
		if (bpkIdentifier == null) {
			try (InputStream stream = getInputStream()) {
				MetaInf metaInf = BpkResolver.resolve(stream).getMetaInf();

				bpkIdentifier = new BpkIdentifier().withGroupId(metaInf.getGroupId()).withBpkId(metaInf.getBpkId()).withVersion(
						metaInf.getVersion());
			}
		}

		return bpkIdentifier;
	}

	@Override
	public void close() throws IOException {
		// ignore - this class is not owner of given file
	}

}
