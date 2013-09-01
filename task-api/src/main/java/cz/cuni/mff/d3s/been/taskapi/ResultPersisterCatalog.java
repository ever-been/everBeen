package cz.cuni.mff.d3s.been.taskapi;

/**
 * A registry that serves for unhooking released {@link Persister} instances.
 */
interface ResultPersisterCatalog {

	/**
	 * Release a persister
	 *
	 * @param persister Persister to release
	 */
	void unhook(Persister persister);
}
