package cz.cuni.mff.d3s.been.swrepository;

import java.io.IOException;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * Repository for BPKs - BEEN software packages.
 * 
 * @author darklight
 * 
 */
public interface BpkStore {
	/**
	 * Retrieve a BPK from the repository.
	 * 
	 * @param bpkIdentifier Unique identifier for the retrieved BPK
	 * 
	 * @return The BPK, or <code>null</code> if such a BPK doesn't exist
	 * 
	 * @throws IOException When the BPK file cannot be opened for reading.
	 */
	StoreReader getBpkReader(BpkIdentifier bpkIdentifier) throws IOException;

	/**
	 * Get an output stream to which the corresponding BPK should be written.
	 * 
	 * @param bpkIdentifier unique identifier for the stored BPK
	 * 
	 * @return The persister stream for the requested BPK or <code>null</code>
	 *         if it could not be retrieved
	 * 
	 * @throws IOException When the BPK file cannot be opened for writing
	 */
	StorePersister getBpkPersister(BpkIdentifier bpkIdentifier) throws IOException;
}
