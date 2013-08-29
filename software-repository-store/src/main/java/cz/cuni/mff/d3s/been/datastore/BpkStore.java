package cz.cuni.mff.d3s.been.datastore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
	 * @param bpkIdentifier
	 *          Unique identifier for the retrieved BPK
	 * 
	 * @return The BPK, or <code>null</code> if such a BPK doesn't exist
	 */
	StoreReader getBpkReader(BpkIdentifier bpkIdentifier);

	/**
	 * Get an output stream to which the corresponding BPK should be written.
	 * 
	 * @param bpkIdentifier
	 *          unique identifier for the stored BPK
	 * 
	 * @return The persister stream for the requested BPK or <code>null</code> if
	 *         it could not be retrieved
	 */
	StorePersister getBpkPersister(BpkIdentifier bpkIdentifier);

	/**
	 * Return a list of available BPKs in the repository.
	 * 
	 * @return List of BpkIdentifier objects
	 */
	List<BpkIdentifier> listBpks();

	/**
	 * Return a map of available TaskDescriptors in BPK identified by given
	 * identifier.
	 * 
	 * 
	 * @param bpkIdentifier
	 *          unique identifier for the stored BPK
	 * 
	 * @return Map of TaskDescriptors where key is task descriptor file name and
	 *         value is descriptor xml.
	 * @throws IOException
	 *           if task descriptor file in BPK can't be read from any reason
	 */
	Map<String, String> getTaskDescriptors(BpkIdentifier bpkIdentifier) throws IOException;

	/**
	 * Return a map of available TaskContextDescriptors in BPK identified by given
	 * identifier.
	 * 
	 * 
	 * @param bpkIdentifier
	 *          unique identifier for the stored BPK
	 * 
	 * @return Map of TaskDescriptors where key is task context descriptor file
	 *         name and value is descriptor xml.
	 * 
	 * @throws IOException
	 *           if task context descriptor file in BPK can't be read from any
	 *           reason
	 */
	Map<String, String> getTaskContextDescriptors(BpkIdentifier bpkIdentifier) throws IOException;

	/**
	 * Checks if BPK with given identifier already is already stored in store.
	 * 
	 * @param bpkIdentifier
	 *          uniqued identifier for the searched BPK
	 * @return true if is BPK already stored in store, false otherwise
	 */
	boolean exists(BpkIdentifier bpkIdentifier);
}
