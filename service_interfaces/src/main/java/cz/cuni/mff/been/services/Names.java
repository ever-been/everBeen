package cz.cuni.mff.been.services;

/**
 * Official names of been services
 * @author donarus
 *
 * TODO: Change to enum (ms)
 */
public class Names {
	
	/** Official name of the command line interface service. */
	public static final String COMMAND_LINE_SERVICE_NAME = "clinterface";
	
	/** Human-readable name of the command line interface service. */
	public static final String COMMAND_LINE_SERVICE_HUMAN_NAME = "Command Line Interface";

	/** Official name of the debug assistant service. */
	public static final String DEBUG_ASSISTANT_SERVICE_NAME = "debugassistant";

	/** Human-readable name of the debug assistant service. */
	public static final String DEBUG_ASSISTANT_SERVICE_HUMAN_NAME = "Debug Assistant";



	// HostManager
	/**
	 * Service name.
	 */
	public static final String HOST_MANAGER_SERVICE_NAME = "hostmanager";

	/**
	 * Human-readable name of the service (displayed in the web interface).
	 */
	public static final String HOST_MANAGER_SERVICE_HUMAN_NAME = "Host Manager";

	/**
	 * Name of main remote interface for the Host Manager.
	 */
	public static final String HOST_MANAGER_REMOTE_INTERFACE_MAIN = "main";

	/**
	 * Name of the database interface.
	 */
	public static final String HOST_MANAGER_REMOTE_INTERFACE_DATABASE = "database";

	/**
	 * Name of the load server's interface.
	 */
	public static final String HOST_MANAGER_REMOTE_INTERFACE_LOAD_SERVER = "load";
	
}
