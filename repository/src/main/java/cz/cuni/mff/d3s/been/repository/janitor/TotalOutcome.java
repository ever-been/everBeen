package cz.cuni.mff.d3s.been.repository.janitor;

/**
 * The total outcome of an event in the BEEN cluster
 *
 * @author darklight
 */
interface TotalOutcome<T> {

	/**
	 * Figure out whether the event died prematurely (its persistent objects are in a zombie state and need to be purged)
	 *
	 * @return <code>true</code> if the state is zombie (event was logged as started, but no final state was found), <code>false</code> if both the start and the end of the event were found
	 */
	boolean isZombie();

	/**
	 * Figure out whether the event has failed (both the event's start and end were logged, and end state was <code>failed</code>)
	 *
	 * @return <code>true</code> if the final state of the event is <code>failed</code>, <code>false</code> if not or if the event is in zombie state
	 */
	boolean isFailed();

	/**
	 * Add a state entry that says something about the event of interest
	 *
	 * @param stateEntry The state entry to add
	 */
	void addStateEntry(T stateEntry);

	/**
	 * Get the ID of the event of interest
	 *
	 * @return The event's ID
	 */
	String getEventId();
}
