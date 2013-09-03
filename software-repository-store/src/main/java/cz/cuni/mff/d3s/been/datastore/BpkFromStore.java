package cz.cuni.mff.d3s.been.datastore;

import java.io.IOException;
import java.io.InputStream;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * A BPK retrieved from the {@link SoftwareStore}
 */
public class BpkFromStore implements Bpk {

	/**
	 * Create a BPK from the store
	 *
	 * @param reader Reader capable of providing the BPK's content from the store
	 * @param identifier Unique identifier of the BPK
	 */
	public BpkFromStore(StoreReader reader, BpkIdentifier identifier) {
		this.identifier = identifier;
		this.reader = reader;
	}

	private final StoreReader reader;
	private final BpkIdentifier identifier;

	@Override
	public BpkIdentifier getBpkIdentifier() {
		return identifier;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return reader.getContentStream();
	}
}
