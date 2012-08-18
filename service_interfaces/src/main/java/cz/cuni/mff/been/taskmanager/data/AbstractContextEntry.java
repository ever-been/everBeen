package cz.cuni.mff.been.taskmanager.data;

/**
 * Enhancement of the {@link ContextEntry} interface. This class adds the
 * possibility to close the context using the {@link #close()} method.
 * 
 * @author darklight
 * 
 */
abstract class AbstractContextEntry implements ContextEntry {
	/** Serialization ID. */
	private static final long serialVersionUID = -6381268969417681651L;

	/**
	 * Close the context.
	 */
	abstract void close();

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
