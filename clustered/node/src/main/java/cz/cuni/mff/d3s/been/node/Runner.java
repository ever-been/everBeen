package cz.cuni.mff.d3s.been.node;

import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.task.IManager;
import cz.cuni.mff.d3s.been.task.Managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Entry point for BEEN nodes.
 *
 * Responsibilities of BEEN nodes include:
 *   - joining the cluster
 *   - scheduling tasks
 *
 * Possibly, there can be three types of a node:
 *  - full: cluster membership + data + event handling
 *  - lite: cluster membership + event handling
 *  - client: event handling
 *
 *  Clients nodes could be used as "very lite" runtimes.
 *  Lite nodes have the overhead of cluster membership, but does not hold/replicate data.
 *
 *
 *  So far only full node is implemented.
 *
 * @author Martin Sixta
 */
public class Runner {

	private static final Logger log = LoggerFactory.getLogger(Runner.class);

	public static void main(String[] args) {

		// Handle command-line arguments

		// Join
		log.info("The node is connecting to the cluster");
		HazelcastInstance instance = Instance.getInstance();
		ClusterUtils.registerHazelcastInstance(instance);
		log.info("The node is now connected to the cluster");


		// Create and start the Task Manager
		log.info("Preparing to handle tasks requests");
		IManager taskManager = Managers.getManager(instance);
		taskManager.start();

	}
}
