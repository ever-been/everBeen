package cz.cuni.mff.d3s.been.taskapi;

/**
 * A registry that serves for unhooking released {@link ResultPersister} instances.
 */
interface ResultPersisterCatalog {

	/**
	 * Release a persister
	 *
	 * @param persister Persister to release
	 */
	void unhook(ResultPersister persister);
}
