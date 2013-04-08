package cz.cuni.mff.d3s.been.cluster;

public interface Reapable {
	/**
	 * Create a reaper thread that manages resource release for this service in
	 * case of a shutdown. This method must not invoke
	 * {@link InterruptedException}, lest its {@link Reaper} not be run.
	 * 
	 * @return An initialized but unstarted reaper thread.
	 */
	public Reaper createReaper();
}
