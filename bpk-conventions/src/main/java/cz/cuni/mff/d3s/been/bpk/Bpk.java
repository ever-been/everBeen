package cz.cuni.mff.d3s.been.bpk;

import java.io.File;

public class Bpk {
	BpkIdentifier identifier;
	File file;

	public BpkIdentifier getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(BpkIdentifier identifier) {
		this.identifier = identifier;
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
