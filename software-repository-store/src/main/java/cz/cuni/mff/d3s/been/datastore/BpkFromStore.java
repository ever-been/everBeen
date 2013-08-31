package cz.cuni.mff.d3s.been.datastore;

import java.io.IOException;
import java.io.InputStream;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

public class BpkFromStore implements Bpk {

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
