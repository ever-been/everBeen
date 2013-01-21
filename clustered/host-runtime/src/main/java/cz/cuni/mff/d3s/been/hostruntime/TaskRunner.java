package cz.cuni.mff.d3s.been.hostruntime;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.pojo.BaseNodeInfo.HostRuntimeNodeInfo;

public class TaskRunner {

	/**
	 * SHOULD BE THREAD SAFE
	 * TODO verify if it is true :)
	 */
	private final Map<String, Process> runningTasks = new ConcurrentHashMap<>();
	
	private HostRuntimeNodeInfo nodeInfo;

	public final boolean tryRunTask(final RunTaskMessage runTaskMessage) {

		if (runningTasks.containsKey(runTaskMessage.name)) {
			// already running ... FIXME
			return false;
		}

		Thread thread = new Thread() {
			public void run() {
				try {
					// 0. add tasks in node info 
					nodeInfo.addRunningTask(runTaskMessage.name);
					
					// extract files from bpk
					// determine task type (java/python/shell)
					// determine main class and dependencies
					// download dependencies
					// determine classpath from downloaded deps
					// prepare JVM args
					// prepare TASK args

					// prepare log interface in separate thread (rest hazelcast
					// queue on exact ip:port)

					// ... HOW TO DO THIS WITHOUT DEPENDENCY ON HC?

					// ATOMIC OP START
					/*
					 * 1. set running tasks in node info
					 * 
					 * 2. start task in apache executor wrapped in special
					 * thread ... define retry count and timeouts
					 * 
					 * 3. after task finish, remove from running tasks in node
					 * info
					 * 
					 * 4. send task finished info
					 */
					// ATOMIC OP END

					String pName = new String(runTaskMessage.name);
					Process p = Runtime.getRuntime().exec("sleep 1000");
					runningTasks.put(pName, p);
					Thread taskShutdownHook = createTaskShutdownHook(p);
					Runtime.getRuntime().addShutdownHook(taskShutdownHook);
					p.waitFor();
					Runtime.getRuntime().removeShutdownHook(taskShutdownHook);
					runningTasks.remove(pName);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			private Thread createTaskShutdownHook(final Process p) {
				return new Thread() {
					@Override
					public void run() {
						p.destroy();
						nodeInfo.removeRunningTask(runTaskMessage.name);
					}
				};
			}
		};
		thread.start();
		return true;
	}

	public final void killTask(final KillTaskMessage message) {
		Process p = runningTasks.get(message.taskName);
		try {
			p.destroy();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		runningTasks.remove(message.taskName);

	}

	public final void setNodeInfo(final HostRuntimeNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;		
	}

}
