package cz.cuni.mff.been.taskmanager.data;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Host runtime entry in task manager.
 * 
 * @author darklight
 * 
 */
public interface HostRuntimeEntry extends Cloneable, Serializable {
	/**
	 * Regular expression for match <code>hostName</code>.
	 */
	public static final Pattern REGEXP_HOST_NAME = Pattern
			.compile("^[a-zA-Z_0-9.-]+$");

	/**
	 * Set name of host with this <code>Host Runtime</code>.
	 * 
	 * @param hostName
	 *            Name of host with this <code>Host Runtime</code>.
	 */
	public void setHostName(String hostName);

	/**
	 * Set ID of context with reservation of this Host Runtime (task's
	 * exclusivity).
	 * 
	 * @param reservation
	 *            ID of context, <code>null</code> for any, <code>""</code> for
	 *            none.
	 */
	public void setReservation(String reservation);

	/**
	 * Get time of creation of this object (according with system creating this
	 * entry) in milliseconds. (Difference between current time and midnight,
	 * January 1, 1970 UTC.
	 * 
	 * @return Time of creation of this object.
	 */
	public long getCurentTime();

	/**
	 * Get name of host with this <code>Host Runtime</code>.
	 * 
	 * @return Name of host with this <code>Host Runtime</code>.
	 */
	public String getHostName();

	/**
	 * Get ID of context with reservation of this Host Runtime (task's
	 * exclusivity).
	 * 
	 * @return ID of context, <code>null</code> if any, <code>""</code> if none.
	 */
	public String getReservation();

	/**
	 * Load units getter.
	 * 
	 * @return The current number of load units.
	 */
	public int getLoadUnits();

	/**
	 * Decreases the load units counter. TODO: This method should be neither
	 * public, nor implemented in this class. However, this design flaw can only
	 * be removed by merging the Host Manager with the Task Manager.
	 * 
	 * @param loadUnits
	 *            Number of load units to subtract.
	 * @throws IllegalStateException
	 *             When the number of units would fall below zero.
	 */
	public void removeLoad(int loadUnits);

	/**
	 * Increases the load units counter. TODO: This method should be neither
	 * public, nor implemented in this class. However, this design flaw can only
	 * be removed by merging the Host Manager with the Task Manager.
	 * 
	 * @param loadUnits
	 *            Number of load units to add.
	 * @param limit
	 *            Maximum number of load units this host runtime can handle.
	 * @return {@code true} when successful, {@code false} if the limit would be
	 *         exceeded.
	 */
	public boolean acceptLoad(int loadUnits, int limit);

	/**
	 * Increases the load units counter in a Quick & Dirty (Microsoft-like)
	 * manner. Useful when the Host Manager is not running and no memory limits
	 * are known. Always use {@code acceptLoad()} instead, unless you know what
	 * you're doing.
	 * 
	 * @param loadUnits
	 *            Number of load units to add.
	 */
	public void addLoad(int loadUnits);
}
