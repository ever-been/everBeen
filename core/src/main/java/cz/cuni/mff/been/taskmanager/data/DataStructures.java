/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.been.taskmanager.data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/*
 * Warning: some horrible code abound! :(
 * 
 * Note: I added some quick fixes - that means ugly hacks - to make stupid exceptions go away. This
 * thing was crashing practically every time with null pointer exception on shutdown...
 * All (I think) such ugly hacks have been marked with comments (XXX).
 * 
 *  BR
 */

/**
 * This class serves for storing informations about all <code>Tasks</code>,
 * <code>Contexts</code> and <code>Host Runtimes</code> known to
 * <code>Task Manager</code>.
 * 
 * @author Antonin Tomecek
 */
public class DataStructures {
	
	/**
	 * List of all Tasks in this <code>DataStructures</code> system.
	 */
	private LinkedList<TaskNode> tasks;
	
	/**
	 * List of all Contexts in this <code>DataStructures</code> system.
	 */
	private LinkedList<ContextNode> contexts;
	
	/**
	 * List of all Host Runtimes in this <code>DataStructures</code> system.
	 */
	private LinkedList<HostRuntimeNode> hostRuntimes;
	
	/**
	 * List of all Check Points in this <code>DataStructures</code> system.
	 */
	private LinkedList<CheckPointNode> checkPoints;
	
	/**
	 * Constructor of this <code>DataStructures</code>. It creates new objects
	 * and initializes all its private variables (lists).
	 */
	protected DataStructures() {
		this.tasks = new LinkedList<TaskNode>();
		this.contexts = new LinkedList<ContextNode>();
		this.hostRuntimes = new LinkedList<HostRuntimeNode>();
		this.checkPoints = new LinkedList<CheckPointNode>();
	}
	
	/**
	 * Add one <code>TaskNode</code> to system.
	 * 
	 * @param taskNode <code>TaskNode</code> to add.
	 */
	private void addTaskNode(TaskNode taskNode) {
		/* Add taskNode to list. */
		this.tasks.add(taskNode);
		
		/* Add taskNode to context. */
		ContextNode context = taskNode.getContext();
		context.addTask(taskNode);
		
		/* Add taskNode to hostRuntime (if not null). */
		HostRuntimeNode hostRuntime = taskNode.getHostRuntime();
		if (hostRuntime != null) {
			hostRuntime.addTask(taskNode);
		}
	}
	
	/**
	 * Add one <code>ContextNode</code> to system.
	 * 
	 * @param contextNode <code>ContextNode</code> to add.
	 */
	private void addContextNode(ContextNode contextNode) {
		/* Add contextNode to list. */
		this.contexts.add(contextNode);
	}
	
	/**
	 * Add one <code>HostRuntimeNode</code> to system.
	 * 
	 * @param hostRuntimeNode <code>HostRuntimeNode</code> to add.
	 */
	private void addHostRuntimeNode(HostRuntimeNode hostRuntimeNode) {
		/* Add hostRuntimeNode to list. */
		this.hostRuntimes.add(hostRuntimeNode);
	}
	
	/**
	 * Add one <code>CheckPointNode</code> to system.
	 * 
	 * @param checkPointNode <code>CheckPointNode</code> to add.
	 */
	private void addCheckPointNode(CheckPointNode checkPointNode) {
		/* Add checkPointNode to list. */
		this.checkPoints.add(checkPointNode);
		
		/* Add checkPointNode to task. */
		TaskNode task = checkPointNode.getTask();
		task.addCheckPoint(checkPointNode);
		
		/* Add checkPointNode to context. */
		ContextNode context = checkPointNode.getContext();
		context.addCheckPoint(checkPointNode);
		
		/* Add checkPointNode to hostRuntime. */
		HostRuntimeNode hostRuntime = checkPointNode.getHostRuntime();
		if (hostRuntime != null) {
			hostRuntime.addCheckPoint(checkPointNode);
		}
	}
	
	/**
	 * Remove one <code>TaskNode</code> from system.
	 * 
	 * @param taskNode <code>TaskNode</code> to remove.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	private void removeTaskNode(TaskNode taskNode) {
		/* Remove taskNode from context. */
		ContextNode context = taskNode.getContext();
		context.removeTask(taskNode);
		
		/* Remove taskNode from hostRuntime. */
		HostRuntimeNode hostRuntime = taskNode.getHostRuntime();
		if (hostRuntime != null) {
			hostRuntime.removeTask(taskNode);
		}
		
		/* Remove all checkPointNode(s) associated with taskNode. */
		while (!taskNode.getCheckPoints().isEmpty()) {
			this.removeCheckPointNode(taskNode.getCheckPoints().getLast());
		}
		
		/* Remove taskNode from list. */
		boolean removed = this.tasks.remove(taskNode);
		if (!removed) {
			throw new DataRuntimeException("TaskNode required to be removed not found in list of TaskNode elements");
		}
	}
	
	/**
	 * Remove one <code>ContextNode</code> from system.
	 * 
	 * @param contextNode <code>ContextNode</code> to remove.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	private void removeContextNode(ContextNode contextNode) {
		/* Remove contextNode from list. */
		boolean removed = this.contexts.remove(contextNode);
		if (!removed) {
			throw new DataRuntimeException("ContextNode required to be removed not found in list of ContextNode elements");
		}
	}
	
	/**
	 * Remove one <code>HostRuntimeNode</code> from system.
	 * 
	 * @param hostRuntimeNode <code>HostRuntimeNode</code> to remove.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	private void removeHostRuntimeNode(HostRuntimeNode hostRuntimeNode) {
		/* Remove hostRuntimeNode from list. */
		boolean removed = this.hostRuntimes.remove(hostRuntimeNode);
		if (!removed) {
			throw new DataRuntimeException("HostRuntimeNode required to be removed not found in list of HostRuntimeNode elements");
		}
	}
	
	/**
	 * Remove one <code>CheckPointNode</code> from system.
	 * 
	 * @param checkPointNode <code>CheckPointNode</code> to remove.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	private void removeCheckPointNode(CheckPointNode checkPointNode) {
		/* Remove checkPointNode from task. */
		TaskNode task = checkPointNode.getTask();
		task.removeCheckPoint(checkPointNode);
		
		/* Remove checkPointNode from context. */
		ContextNode context = checkPointNode.getContext();
		context.removeCheckPoint(checkPointNode);
		
		/* Remove checkPointNode from hostRuntime. */
		HostRuntimeNode hostRuntime = checkPointNode.getHostRuntime();
		
		// XXX: ugly hack UH001: added if to see if the hostRuntime is null
		if (hostRuntime == null) {
			System.out.println("UH001: HostRuntime node reference is null!");
		} else {
			hostRuntime.removeCheckPoint(checkPointNode);
		}
		
		/* Remove checkPointNode from list. */
		boolean removed = this.checkPoints.remove(checkPointNode);
		if (!removed) {
			throw new DataRuntimeException("CheckPointNode required to be removed not found in list of CheckPointNode elements");
		}
	}
	
	/**
	 * Return <code>TaskNode</code> associated with Task specified by its
	 * taskId and contextId.
	 * 
	 * @param taskId ID of Task.
	 * @param contextId ID of Context containing searched Task.
	 * @return <code>TaskNode</code> associated with Task specified by
	 * 	its <code>taskId</code> and <code>contextId</code> of Context
	 * 	containing it. <code>null</code> if not found.
	 */
	private TaskNode findTask(
			String taskId, String contextId) {
		TaskNode task = null;
		
		Iterator<TaskNode> taskIterator = this.tasks.iterator();
		while (taskIterator.hasNext()) {
			TaskNode comparedTask = taskIterator.next();
			TaskEntry comparedEntry = comparedTask.getTaskEntry();
			if (comparedEntry.getTaskId().equals(taskId)
					&& comparedEntry.getContextId().equals(contextId)) {
				task = comparedTask;
				break;
			}
		}
		
		return task;
	}
	
	/**
	 * Return <code>ContextNode</code> associated with Context specified by its
	 * contextId.
	 * 
	 * @param contextId ID of Context.
	 * @return <code>ContextNode</code> associated with Context specified by
	 * 	<code>contextId</code>. <code>null</code> if not found.
	 */
	private ContextNode findContext(String contextId) {
		ContextNode context = null;
		
		Iterator<ContextNode> contextIterator = this.contexts.iterator();
		while (contextIterator.hasNext()) {
			ContextNode comparedContext = contextIterator.next();
			if (comparedContext.getContextEntry().getContextId()
					.equals(contextId)) {
				context = comparedContext;
				break;
			}
		}
		
		return context;
	}
	
	/**
	 * Return <code>HostRuntimeNode</code> associated with Host Runtime
	 * specified by its hostName.
	 * 
	 * @param hostName Host name of Host Runtime.
	 * @return <code>HostRuntimeNode</code> associated with Host Runtime
	 * 	specified by its host name. <code>null</code> if not found.
	 */
	private HostRuntimeNode findHostRuntime(String hostName) {
		HostRuntimeNode hostRuntime = null;
		
		Iterator<HostRuntimeNode> hostRuntimeIterator
				= this.hostRuntimes.iterator();
		while (hostRuntimeIterator.hasNext()) {
			HostRuntimeNode comparedHostRuntime = hostRuntimeIterator.next();
			if (comparedHostRuntime.getHostRuntimeEntry().getHostName()
					.equals(hostName)) {
				hostRuntime = comparedHostRuntime;
				break;
			}
		}
		
		return hostRuntime;
	}
	
	/**
	 * Return <code>CheckPointNode</code>s associated with Check Points
	 * specified by one or more its properties (returned nodes are in correct
	 * order as added).
	 * 
	 * @param type Type of checkPoint (<code>null</code> for any).
	 * @param taskId ID of task which reached checkPoint (<code>null</code> for
	 * 	any).
	 * @param contextId ID of context containing checkPoint (<code>null</code>
	 * 	for any).
	 * @param magicObject Some magic object from outside... (no one understands
	 * 	to this).
	 * @return Array containing all <code>CheckPointNode</code>s associated
	 * 	with Check Points specified by its properties.
	 */
	private CheckPointNode[] findCheckPoints(
			String type, String taskId, String contextId,
			Serializable magicObject) {
		LinkedList<CheckPointNode> foundCheckPoints
			= new LinkedList<CheckPointNode>();
		
		Iterable<CheckPointNode> checkPointIterable;
		
		if (contextId != null) {
			/* Find context. */
			ContextNode context = this.findContext(contextId);
			if (context == null) {
				/* Nothing found. */
				return new CheckPointNode[0];
			} else {
				/* Search checkPoints only in this context. */
				checkPointIterable = context.getCheckPoints();
			}
		} else {
			/* Search checkPoints in all contexts. */
			checkPointIterable = this.checkPoints;
		}
		
		/* Search checkPoints in one or all contexts. */
		for ( CheckPointNode comparedCheckPointNode : checkPointIterable ) {
			CheckPointEntry comparedCheckPointEntry
				= comparedCheckPointNode.getCheckPointEntry();
			
			/* Compare taskId. */
			if (comparedCheckPointEntry.getTaskId().equals(taskId)) {
				String comparedType = comparedCheckPointEntry.getName();
				Serializable comparedMagicObject
					= comparedCheckPointEntry.getMagicObject();
				
				/* Compare type. */
				if ((type == null)
						|| ((comparedType != null)
								&& (comparedType.equals(type)))) {
					/* Compare magicOgject. */
					if ((magicObject == null)
							|| ((comparedMagicObject != null)
									&& (comparedMagicObject
											.equals(magicObject)))) {
						/* Add to list of found checkPoints. */
						foundCheckPoints.add(comparedCheckPointNode);
					}
				}
			}
		}
		
		return foundCheckPoints.toArray(new CheckPointNode[foundCheckPoints.size()]);
	}
	
	/**
	 * Return array of <code>TaskEntry</code> objects specified by (optionally)
	 * one or more from taskId, contextId, hostName.
	 * 
	 * @param taskId ID of Task (<code>null</code> for any).
	 * @param contextId ID of Context (<code>null</code> for any).
	 * @param hostName Host name of HostRuntime (<code>null</code> for any).
	 * @return Array containing all <code>TaskEntry</code> objects complying
	 * 	specified parameters.
	 */
	protected synchronized TaskEntry[] getTasks(String taskId, String contextId, String hostName) {
		/* Prepare Iterator for (subset of) tasks. */
		Iterator<TaskNode> taskIterator = null;
		if (contextId != null) {
			/* Search only in one context. */
			ContextNode contextNode = this.findContext(contextId);
			if (contextNode == null) {
				return new TaskEntry[0];
			}
			taskIterator = contextNode.getTasks().iterator();
		} else if (hostName != null) {
			/* Search only in one hostRuntime. */
			HostRuntimeNode hostRuntimeNode = this.findHostRuntime(hostName);
			if (hostRuntimeNode == null) {
				return new TaskEntry[0];
			}
			taskIterator = hostRuntimeNode.getTasks().iterator();
		} else {
			taskIterator = this.tasks.iterator();
		}
		
		/* List of matching entries. */
		LinkedList<TaskEntry> foundTaskEntries = new LinkedList<TaskEntry>();
		
		/* Find required entries. */
		while (taskIterator.hasNext()) {
			TaskNode taskNode = taskIterator.next();
			TaskEntry taskEntry = taskNode.getTaskEntry();
			/* Compare taskId. */
			if ((taskId != null)
					&& (!taskEntry.getTaskId().equals(taskId))) {
				continue;
			}
			/* Compare contextId. */
			if ((contextId != null)
					&& (!taskEntry.getContextId().equals(contextId))) {
				continue;
			}
			/* Compare hostName. */
			if ((hostName != null)
					&& ((taskEntry.getHostName() == null)
					|| (!taskEntry.getHostName().equals(hostName)))) {
				continue;
			}
			
			/* Add taskEntry to result. */
			foundTaskEntries.add(taskEntry);
		}
		
		return foundTaskEntries.toArray(
				new TaskEntry[foundTaskEntries.size()]);
	}
	
	/**
	 * Return <code>TaskEntry</code> specified by taskId and contextId.
	 * 
	 * @param taskId ID of Task.
	 * @param contextId ID of Context containing required Task.
	 * @return Required <code>TaskEntry</code> or <code>null</code> if not
	 * 	found.
	 */
	protected synchronized TaskEntry getTask(String taskId, String contextId) {
		TaskNode task = this.findTask(taskId, contextId);
		
		return ((task == null) ? null : task.getTaskEntry());
	}
	
	/**
	 * Return <code>TaskData</code> specified by taskId and contextId.
	 * 
	 * @param taskId ID of Task.
	 * @param contextId ID of Context containing required Task.
	 * @return Required <code>TaskData</code> or <code>null</code> if not
	 * 	found.
	 */
	protected synchronized TaskData getTaskData(String taskId, String contextId) {
		TaskNode task = this.findTask(taskId, contextId);
		return ((task == null) ? null : task.getTaskData());
	}
	
	/**
	 * Return array of <code>ContextEntry</code> objects specified by
	 * (optionally) contextId.
	 * 
	 * @param contextId ID of Context (<code>null</code> for any).
	 * @return Array containing all <code>ContextEntry</code> objects complying
	 * 	specified parameters.
	 */
	protected synchronized ContextEntry[] getContexts(String contextId) {
		ContextEntry[] contextEntries;
		
		if (contextId == null) {
			/* Return all Contexts. */
			contextEntries = new ContextEntry[this.contexts.size()];
			for (int i = 0; i < this.contexts.size(); i++) {
				contextEntries[i] = this.contexts.get(i).getContextEntry();
			}
		} else {
			/* Return only one Context. */
			ContextNode contextNode = this.findContext(contextId);
			if (contextNode == null) {
				contextEntries = new ContextEntry[0];
			} else {
				ContextEntry contextEntry = contextNode.getContextEntry();
				contextEntries = new ContextEntry[1];
				contextEntries[0] = contextEntry;
			}
		}
		return contextEntries;
	}
	
	/**
	 * Return <code>ConetextEntry</code> specified by contextId.
	 * 
	 * @param contextId ID of Context.
	 * @return Required <code>ContextEntry</code> or <code>null</code> if not
	 * 	found.
	 */
	protected synchronized ContextEntry getContext(String contextId) {
		ContextNode context = this.findContext(contextId);
		
		return ((context == null) ? null : context.getContextEntry());
	}
	
	/**
	 * Return array of <code>HostRuntimeEntry</code> objects specified by
	 * (optionally) hostName.
	 * 
	 * @param hostName Host name of HostRuntime
	 * @return Array containing all <code>HostRuntimeEntry</code> objects
	 * 	complying specified parameters.
	 */
	protected synchronized HostRuntimeEntry[] getHostRuntimes(String hostName) {
		HostRuntimeEntry[] hostRuntimeEntries;
		
		if (hostName == null) {
			/* Return all HostRuntimes. */
			hostRuntimeEntries = new HostRuntimeEntry[this.hostRuntimes.size()];
			int i = 0;
			for (HostRuntimeNode node : hostRuntimes) {
				hostRuntimeEntries[i++] = node.getHostRuntimeEntry();
			}
		} else {
			/* Return only one HostRuntime. */
			HostRuntimeNode hostRuntimeNode = this.findHostRuntime(hostName);
			if (hostRuntimeNode == null) {
				hostRuntimeEntries = new HostRuntimeEntry[0];
			} else {
				hostRuntimeEntries = new HostRuntimeEntry[] {
					hostRuntimeNode.getHostRuntimeEntry()
				};
			}
		}
		return hostRuntimeEntries;
	}
	
	/**
	 * Return <code>HostRuntimeEntry</code> specified by hostName.
	 * 
	 * @param hostName Host name of HostRuntime.
	 * @return Required <code>HostRuntimeEntry</code> or <code>null</code> if
	 * 	not found.
	 */
	protected synchronized HostRuntimeEntry getHostRuntime(
			String hostName) {
		HostRuntimeNode hostRuntime = this.findHostRuntime(hostName);
		
		return ((hostRuntime == null) ? null : hostRuntime.getHostRuntimeEntry());
	}
	
	/**
	 * Return <code>CheckPointEntry</code> specified by one or more from type,
	 * value, taskId, contextId.
	 * 
	 * @param name Name of CheckPoint (<code>null</code> for any).
	 * @param taskId ID of task which reached CheckPoint (<code>null</code> for
	 * 	any).
	 * @param contextId ID of Context containing CheckPoint (<code>null</code>
	 * 	for any).
	 * @param magicObject Some magic object from outside... (no one understands
	 * 	to this).
	 * @return Array containing all <code>CheckPointEntry</code>s associated
	 * 	with CheckPoints specified by its properties.
	 */
	protected synchronized CheckPointEntry[] getCheckPoints(
			String name, String taskId, String contextId,
			Serializable magicObject) {
		CheckPointNode[] checkPoints
			= this.findCheckPoints(name, taskId, contextId, magicObject);
		
		/* Prepare array of entries... */
		CheckPointEntry[] checkPointEntries
			= new CheckPointEntry[checkPoints.length];
		for (int i = 0; i < checkPoints.length; i++) {
			checkPointEntries[i] = checkPoints[i].getCheckPointEntry();
		}
		
		return checkPointEntries;
	}
	
	/**
	 * Link <code>TaskNode</code> specified by <code>TaskEntry</code> with
	 * <code>HostRuntimeNode</code> specified by <code>HostRuntimeEntry</code>
	 * (if task is not linked with any one yet).
	 * 
	 * @param taskEntry <code>TaskEntry</code> specifying linked task.
	 * @param hostRuntimeEntry <code>HostRuntimeEntry</code> specifying linked
	 * 	hostRuntime.
	 * @throws IllegalArgumentException If task or hostRuntime not found or if
	 * 	task is already linked.
	 */
	protected synchronized void linkTaskWithHostRuntime(
			TaskEntry taskEntry, HostRuntimeEntry hostRuntimeEntry) {
		String taskId = taskEntry.getTaskId();
		String contextId = taskEntry.getContextId();
		String hostName = hostRuntimeEntry.getHostName();
		
		/* Find taskNode. */
		TaskNode task = this.findTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Trying to link non-existing task "
					+ "(taskId \"" + taskId + "\", contextId \"" + contextId
					+ "\") with hostRuntime (hostName \""
					+ hostName + "\")");
		}
		/* Check if it is not linked with any hostRuntime yet. */
		HostRuntimeNode currentHostRuntime = task.getHostRuntime();
		if (currentHostRuntime != null) {
			throw new IllegalArgumentException("Trying to link task (taskId\""
					+ taskId + "\", contextId \"" + contextId
					+ "\") with hostRuntime (hostName \"" + hostName + "\")."
					+ "This task is already linked with some hostRuntime (hostName "
					+ "\"" + currentHostRuntime.getHostRuntimeEntry().getHostName() + "\")");
		}
		
		/* Find hostRuntime. */
		HostRuntimeNode hostRuntime = this.findHostRuntime(hostName);
		if (hostRuntime == null) {
			throw new IllegalArgumentException("Trying to link task (taskId \""
					+ taskId + "\", contextId \"" + contextId
					+ "\") with non-existing hostRuntime (hostName \"" + hostName + "\")");
//			addHostRuntime(hostRuntimeEntry);
//			hostRuntime = this.findHostRuntime(hostName);
		}
		
		/* Set hostName variable in TaskEntry. */
		task.getTaskEntry().setHostName(hostName);
		
		/* Add taskNode to hostRuntime. */
		hostRuntime.addTask(task);
		/* Link hostRuntimeNode to taskNode. */
		task.setHostRuntime(hostRuntime);
	}
	
	/**
	 * Unlink <code>TaskNode</code> specified by <code>TaskEntry</code> with
	 * linked <code>HostRuntimeNode</code> (if task is linked with someone).
	 * 
	 * @param taskEntry <code>TaskEntry</code> specifying linked task.
	 * @throws IllegalArgumentException If task found.
	 */
	protected synchronized void unlinkTaskWithHostRuntime(
			TaskEntry taskEntry) {
		String taskId = taskEntry.getTaskId();
		String contextId = taskEntry.getContextId();
		
		/* Find taskNode. */
		TaskNode task = this.findTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Trying to unlink non-existing task "
					+ "(taskId \"" + taskId + "\", contextId \"" + contextId + "\") with hostRuntime");
		}
		/* Check if it is linked with some hostRuntime. */
		HostRuntimeNode currentHostRuntime = task.getHostRuntime();
		if (currentHostRuntime == null) {
			return;  // no work needed to be done
		}
		
		/* Unset hostName variable in TaskEntry. */
		task.getTaskEntry().setHostName(null);
		
		/* Remove taskNode to hostRuntime. */
		currentHostRuntime.removeTask(task);
		/* Unlink hostRuntimeNode from taskNode. */
		task.setHostRuntime(null);
	}
	
//	/**
//	 * Set <code>TaskData</code> for already existing task (but with
//	 * <code>TaskData</code> not set yet.
//	 * 
//	 * @param taskEntry <code>TaskEntry</code> specifying added task.
//	 * @param taskData <code>TaskData</code> associated with task.
//	 * @throws NullPointerException If some input parameter is
//	 * 	<code>null</code>.
//	 * @throws IllegalArgumentException If specified task not found.
//	 * @throws IllegalStateException If <code>TaskData</code> already set.
//	 */
//	protected synchronized void setTaskData(String taskId, String contextId,
//			TaskData taskData) {
//		/* Check input parameters. */
//		if (taskId == null) {
//			throw new NullPointerException("taskId is null");
//		}
//		if (contextId == null) {
//			throw new NullPointerException("contextId is null");
//		}
//		if (taskData == null) {
//			throw new NullPointerException("taskData is null");
//		}
//		
//		/* Find task (if already exists). */
//		TaskNode foundTask = this.findTask(taskId, contextId);
//		if (foundTask == null) {
//			throw new IllegalArgumentException("Task not found (taskId \""
//					+ taskId + "\", contextId \"" + contextId + "\")");
//		}
//		
//		/* Check if TaskData is not set yet. */
//		if (foundTask.getTaskData() != null) {
//			throw new IllegalStateException("TaskData is already set (taskId "
//					+ "\"" + taskId + "\", contextId \"" + contextId + "\")");
//		}
//		
//		/* Set TaskData. */
//		foundTask.setTaskData(taskData);
//	}
	
	/**
	 * Add new <code>TaskNode</code> for new task to appropriate
	 * <code>context</code> and <code>hostRuntime</code> specified by variables
	 * of <code>taskEntry</code>.
	 * Note: Appropriate <code>context</code> and <code>hostRuntime</code> must
	 * already exist.
	 * 
	 * @param taskEntry <code>TaskEntry</code> specifying added task.
	 * @param taskData <code>TaskData</code> associated with task.
	 * @throws IllegalArgumentException If adding failed.
	 */
	protected synchronized void addTask(
			TaskEntry taskEntry, TaskData taskData) {
		String taskId = taskEntry.getTaskId();
		String contextId = taskEntry.getContextId();
//		String hostName = taskEntry.getHostName();
		
		/* Find context. */
		ContextNode context = this.findContext(contextId);
		if (context == null) {
			throw new IllegalArgumentException("Trying to add task \""
					+ taskEntry.getTaskId() + "\" to non-existing context \""
					+ taskEntry.getContextId() + "\"");
		}
		
//		/* Find hostRuntime. */
//		HostRuntimeNode hostRuntime = null;
//		if (hostName != null) {
//			hostRuntime = this.findHostRuntime(hostName);
//			if (hostRuntime == null) {
//				throw new IllegalArgumentException("Trying to add task \""
//						+ taskEntry.getTaskId() + "\" to non-existing "
//						+ "hostRuntime " + taskEntry.getHostName() + "\"");
//			}
//		}
		
		/* Find task (if already exists). */
		TaskNode foundTask = this.findTask(taskId, contextId);
		if (foundTask != null) {
			throw new IllegalArgumentException("Trying to add already added task \"" 
					+ taskId + "\" to context \"" + contextId + "\"");
		}
		
		/* Add new task. */
		TaskNode task = new TaskNode(taskEntry, context);
		task.setTaskData(taskData);
		this.addTaskNode(task);
	}
	
	/**
	 * Add new <code>ContextNode</code> for new context specified by variables
	 * of <code>contextEntry</code>.
	 * 
	 * @param contextEntry <code>ContextEntry</code> specifying added context.
	 * @throws IllegalArgumentException If adding failed.
	 */
	protected synchronized void addContext(ContextEntry contextEntry) {
		String contextId = contextEntry.getContextId();
		
		/* Find context (if already exists). */
		ContextNode foundContext = this.findContext(contextId);
		if (foundContext != null) {
			throw new IllegalArgumentException("Trying to add already existing context \"" + contextId + "\"");
		}
		
		/* Add new context. */
		ContextNode context = new ContextNode(contextEntry);
		this.addContextNode(context);
	}
	
	/**
	 * Add new <code>HostRuntimeNode</code> for new hostRuntime specified by
	 * variables of <code>hostRuntimeEntry</code>.
	 * 
	 * @param hostRuntimeEntry <code>HostRuntimeEntry</code> specifying added
	 * 	hostRuntime.
	 * @throws IllegalArgumentException If adding failed.
	 */
	protected synchronized void addHostRuntime(
			HostRuntimeEntry hostRuntimeEntry) {
		String hostName = hostRuntimeEntry.getHostName();
		
		/* Find hostRuntime (if already exists). */
		HostRuntimeNode foundHostRuntime = this.findHostRuntime(hostName);
		if (foundHostRuntime != null) {
			throw new IllegalArgumentException("Trying to add already added host runtime \"" + hostName + "\"");
		}
		
		/* Add new hostRuntime. */
		HostRuntimeNode hostRuntime = new HostRuntimeNode(hostRuntimeEntry);
		this.addHostRuntimeNode(hostRuntime);
	}
	
	/**
	 * Add new <code>CheckPointNode</code> for new checkPoint specified by
	 * variables of <code>CheckPointEntry</code>.
	 * 
	 * @param checkPointEntry <code>CheckPointEntry</code> specifying added
	 * 	checkPoint.
	 * @throws IllegalArgumentException If adding failed.
	 */
	protected synchronized void addCheckPoint(
			CheckPointEntry checkPointEntry) {
		String name = checkPointEntry.getName();
		String taskId = checkPointEntry.getTaskId();
		String contextId = checkPointEntry.getContextId();
		String hostName = checkPointEntry.getHostName();
		
		/* Find task. */
		TaskNode task = this.findTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Trying to add checkPoint (\""
					+ name + "\") to non-existing task \"" + taskId + "\" in context \"" 
					+ contextId + "\" on host \"" + hostName + "\"");
		}
		
		/* Find context. */
		ContextNode context = this.findContext(contextId);
		if (context == null) {
			throw new IllegalArgumentException("Trying to add checkPoint (\""
					+ name + "\") to task \"" + taskId 
					+ "\" in non-existing context \"" + contextId
					+ "\" on host \"" + hostName + "\"");
		}
		
		/* Find hostRuntime. */
		HostRuntimeNode hostRuntime = this.findHostRuntime(hostName);
//		if (hostRuntime == null) {
//			throw new IllegalArgumentException("Trying to add checkPoint (\""
//					+ name + "\") to task \"" + taskId + "\" in context \""
//					+ context + "\" on non-existing host \"" + hostName + "\"");
//		}
		
		/* Add new checkPoint. */
		CheckPointNode checkPoint
			= new CheckPointNode(checkPointEntry, task, context, hostRuntime);
		this.addCheckPointNode(checkPoint);
	}
	
	/**
	 * Remove one <code>TaskNode</code> specified by <code>taskEntry</code>
	 * from system.
	 * 
	 * @param taskEntry <code>TaskEntry</code> specifying task to remove.
	 * @throws IllegalArgumentException If removing failed.
	 */
	protected synchronized void removeTask(TaskEntry taskEntry) {
		String taskId = taskEntry.getTaskId();
		String contextId = taskEntry.getContextId();
		
		/* Find task (if exists). */
		TaskNode task = this.findTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Trying to remove non-existing task "
					+ "(taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}
		
		/* Remove task. */
		this.removeTaskNode(task);
	}
	
	/**
	 * Remove one <code>ContextNode</code> specified by
	 * <code>contextEntry</code> from system.
	 * 
	 * @param contextEntry <code>ContextEntry</code> specifying context to
	 * 	remove.
	 * @throws IllegalArgumentException If removing failed.
	 */
	protected synchronized void removeContext(ContextEntry contextEntry) {
		String contextId = contextEntry.getContextId();
		
		/* Find context (if exists). */
		ContextNode context = this.findContext(contextId);
		if (context == null) {
			throw new IllegalArgumentException("Trying to remove non-existing context "
					+ "(contextId \"" + contextId + "\")");
		}
		
		/* Check if context is empty. */
		if (!context.isEmpty()) {
			throw new IllegalArgumentException("Trying to remove non-empty context "
					+ "(contextId \"" + contextId + "\")");
		}
		
		/* Remove context from list. */
		this.removeContextNode(context);
	}
	
	/**
	 * Remove one <code>HostRuntimeNode</code> specified by
	 * <code>hostRuntimeEntry</code> from system.
	 * 
	 * @param hostName Name of the host runtime to remove.
	 * @throws IllegalArgumentException If removing failed.
	 */
	protected synchronized void removeHostRuntime(String hostName) {
		/* Find hostRuntime (if exists). */
		HostRuntimeNode hostRuntime = this.findHostRuntime(hostName);
		if (hostRuntime == null) {
			throw new IllegalArgumentException("Trying to remove non-existing hostRuntime "
					+ "(hostName \"" + hostName	+ "\")");
		}
		
		/* Check if hostRuntime is empty. */
		if (!hostRuntime.isEmpty()) {
			throw new IllegalArgumentException("Trying to remove non-empty hostRuntime "
					+ "(hostName \"" + hostName + "\")");
		}
		
		/* Remove hostRuntime from list. */
		this.removeHostRuntimeNode(hostRuntime);
	}
	
	/**
	 * Remove one <code>CheckPointNode</code> specified by
	 * <code>checkPointEntry</code> from system. If more than one
	 * <code>CheckPointNode</code> matches with in compared variables (type,
	 * value, taskId, contextId) specified by <code>CheckPointEntry</code> then
	 * all of them are removed.
	 * 
	 * @param checkPointEntry <code>CheckPointEntry</code> specifying
	 * 	checkPoint to remove.
	 * @throws IllegalArgumentException If removing failed.
	 */
	protected synchronized void removeCheckPoint(
			CheckPointEntry checkPointEntry) {
		String type = checkPointEntry.getName();
		String taskId = checkPointEntry.getTaskId();
		String contextId = checkPointEntry.getContextId();
		String hostName = checkPointEntry.getHostName();
		Serializable magicObject = checkPointEntry.getMagicObject();
		
		/* Find checkPoint (if exists). */
		CheckPointNode[] checkPoints = this.findCheckPoints(type, taskId, contextId, magicObject);
		
		if (checkPoints.length == 0) {
			throw new IllegalArgumentException("Trying to remove non-existing checkPoint"
					+ " (type \"" + type + "\", taskId " + "\"" + taskId 
					+ "\", contextId \"" + contextId + "\", " + "hostName \"" + hostName + "\")");
		}
		
		/* Remove checkPoints. */
		for (CheckPointNode checkPointNode : checkPoints) {
			this.removeCheckPointNode(checkPointNode);
		}
	}
	
	/**
	 * Remove one <code>ContextNode</code> specified by
	 * <code>ContextEntry</code> from system by force (i.e. if context contains
	 * some tasks then they are removed from system automatically).
	 * 
	 * @param contextEntry <code>contextEntry</code> specifying context to
	 * 	remove.
	 * @throws IllegalArgumentException If removing failed.
	 */
	protected synchronized void removeContextByForce(ContextEntry contextEntry) {
		String contextId = contextEntry.getContextId();
		
		/* Find context (if exists). */
		ContextNode context = this.findContext(contextId);
		if (context == null) {
			throw new IllegalArgumentException("Trying to remove non-existing context "
					+ "(contextId \"" + contextId + "\")");
		}
		
		/* Check if context is empty. */
		if (!context.isEmpty()) {
			/* Empty out context (remove all its tasks). */
			LinkedList<TaskNode> tasks = context.getTasks();
			while (!tasks.isEmpty()) {
				TaskNode task = tasks.getFirst();
				this.removeTaskNode(task);
			}
//			Iterator<TaskNode> tasksIterator = tasks.iterator();
//			while (tasksIterator.hasNext()) {
//				TaskNode task = tasksIterator.next();
//				this.removeTaskNode(task);
//			}
		}
		
		/* Remove context from list. */
		this.removeContextNode(context);
	}
	
	/**
	 * Remove one <code>HostRuntimeNode</code> specified by
	 * <code>hostName</code> from system by force (i.e. if host runtime
	 * contains some tasks then they are removed from system automatically).
	 * 
	 * @param hostRuntimeEntry <code>HostRuntimeEntry</code> specifying host runtime to
	 * 	remove.
	 * @throws IllegalArgumentException If removing failed.
	 */
	protected synchronized void removeHostRuntimeByForce(
			HostRuntimeEntry hostRuntimeEntry) {
		String hostName = hostRuntimeEntry.getHostName();
		
		/* Find hostRuntime (if exists). */
		HostRuntimeNode hostRuntime = this.findHostRuntime(hostName);
		if (hostRuntime == null) {
			throw new IllegalArgumentException("Trying to remove non-existing hostRuntime "
					+ "(hostName \"" + hostName + "\")");
		}
		
		/* Check if hostRuntime is empty. */
		if (!hostRuntime.isEmpty()) {
			/* Empty out hostRuntime (remove all its tasks). */
			LinkedList<TaskNode> tasks = hostRuntime.getTasks();
			while (!tasks.isEmpty()) {
				TaskNode task = tasks.getFirst();
				this.removeTaskNode(task);
			}
//			Iterator<TaskNode> tasksIterator = tasks.iterator();
//			while (tasksIterator.hasNext()) {
//				TaskNode task = tasksIterator.next();
//				this.removeTaskNode(task);
//			}
		}
		
		/* Remove hostRuntime from list. */
		this.removeHostRuntimeNode(hostRuntime);
	}
}
