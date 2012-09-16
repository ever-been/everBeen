package cz.cuni.mff.been.taskmanager.data;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Context entry in task manager.
 * 
 * @author darklight
 * 
 */
public interface ContextEntry extends Cloneable, Serializable {

	/**
	 * Regular expression for match <code>contextId</code>.
	 */
	public static final Pattern REGEXP_CONTEXT_ID = Pattern.compile("^[a-zA-Z_0-9 -]+$");

	/**
	 * Set ID of this context.
	 * 
	 * @param contextId
	 *          ID of this context.
	 * @throws IllegalArgumentException
	 *           If <code>contextId</code> is not valid.
	 */
	void setContextId(String contextId);

	/**
	 * Set human readable name of this context.
	 * 
	 * @param contextName
	 *          Human readable name of this context.
	 * @throws IllegalArgumentException
	 *           If <code>contextName</code> is not valid.
	 */
	void setContextName(String contextName);

	/**
	 * Set human readable description of this context.
	 * 
	 * @param contextDescription
	 *          Human readable description of this context.
	 * @throws IllegalArgumentException
	 *           If <code>contextDescription</code> is not valid.
	 */
	void setContextDescription(String contextDescription);

	/**
	 * Set some magic object from outside... (no one understands to this).
	 * 
	 * @param magicObject
	 *          Some magic object from outside... (no one understands to this).
	 */
	void setMagicObject(Serializable magicObject);

	/**
	 * Get time of creation of this object (according with system creating this
	 * entry) in milliseconds. (Difference between current time and midnight,
	 * January 1, 1970 UTC.
	 * 
	 * @return Time of creation of this object.
	 */
	long getCurentTime();

	/**
	 * Get ID of this context.
	 * 
	 * @return ID of this context.
	 */
	String getContextId();

	/**
	 * Get human readable name of this context.
	 * 
	 * @return Human readable name of this context.
	 */
	String getContextName();

	/**
	 * Get human readable description of this context.
	 * 
	 * @return Human readable description of this context.
	 */
	String getContextDescription();

	/**
	 * Get some magic object from outside... (no one understands to this).
	 * 
	 * @return Some magic object from outside... (no one understands to this).
	 */
	Serializable getMagicObject();

	/**
	 * Test if context is still open.
	 * 
	 * @return <code>true</code> if context is still open <code>false</code>
	 *         otherwise.
	 */
	boolean isOpen();

	/**
	 * Sets number of finished tasks that will be kept in context without cleaning
	 * them by task manager. -1 means no tasks will be cleaned.
	 * 
	 * @param finishedTasksKept
	 */
	void setFinishedTasksKept(int finishedTasksKept);

	/**
	 * Number of finished tasks that will be kept in context without cleaning them
	 * by task manager. -1 means no tasks will be cleaned.
	 * 
	 * @return number of finished tasks kept
	 */
	int getFinishedTasksKept();

	/**
	 * Clone this {@link TaskEntry}.
	 * 
	 * @return The clone.
	 * 
	 * @throws CloneNotSupportedException
	 *           If cloning the object failed. Technically, this should never
	 *           happen.
	 */
	public ContextEntry clone() throws CloneNotSupportedException;

}
