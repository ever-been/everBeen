package cz.cuni.mff.d3s.been.api;

import java.io.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.bpk.MetaInf;

/**
 * Class that holds a stream-based BPK file and its BPK identifier.
 * 
 * @author donarus
 */
public class BpkStreamHolder implements BpkHolder {

	/** the BPK file this class represents */
	private File bpkFile;

	/** the BPK identifier of the BPK package */
	private BpkIdentifier bpkIdentifier;

	/**
	 * Default constructor, create the object with the specified stream as the
	 * source of the BPK data.
	 * 
	 * @param inputStream
	 *          the input stream with the BPK data
	 * @throws IOException
	 *           when the file cannot be read or an I/O error occurs
	 * @throws BpkConfigurationException
	 *           when the configuration in the BPK file is invalid
	 */
	public BpkStreamHolder(InputStream inputStream) throws IOException, BpkConfigurationException {
		if (bpkFile == null) {
			bpkFile = File.createTempFile("tmp", "bpk");
			FileOutputStream fos = new FileOutputStream(bpkFile);
			IOUtils.copy(inputStream, fos);
			bpkFile.deleteOnExit();
		}
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
		FileUtils.deleteQuietly(bpkFile);
	}

}
