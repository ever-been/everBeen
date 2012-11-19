package cz.cuni.mff.d3s.been.hostruntime;


public interface HostRuntimeInterface {

	void initialize();

	void terminate();

	void runTask(String taskDescriptor);



	void setProperty(String name, Object value);

	Object getProperty(String name);

	//Collection listProperties()  etc

	// Reservation of resources? Based on TaskTreeAdress?
	// void deleteContext(String contextID);

	// killTask(TaskTreeAddress )
	// getRunningTasks()
	// getStatus() // EXCLUSIVE, NON_EXCLUSIVE, context exclusive, etc ...

}

/*
TaskManager

scheduleTask()
	selectHostRuntime().runTask()




 */
