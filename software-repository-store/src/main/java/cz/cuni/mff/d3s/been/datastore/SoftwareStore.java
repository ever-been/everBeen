package cz.cuni.mff.d3s.been.datastore;


/**
 * Generic persistence layer.
 * 
 * @author darklight
 *
 */
public interface SoftwareStore extends BpkStore, ArtifactStore {

	/**
	 * Initialize the store.
	 */
	public void init();

}
