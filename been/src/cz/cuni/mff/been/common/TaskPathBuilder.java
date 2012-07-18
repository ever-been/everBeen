/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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
package cz.cuni.mff.been.common;


/**
 * Utility class for constructing task paths
 * @author Michal Tomcanyi
 */
public class TaskPathBuilder {

	private static final String WORKING_DIR = ":workingDirectory";
	private static final String TEMP_DIR = ":temporaryDirectory";
	private static final String TASK_DIR = ":taskDirectory";
	
	// separator to use when constructing path
	private final String separator;
	// constructed path
	private final StringBuilder path = new StringBuilder();
	
	/**
	 * Creates new TaskBuilderObject
	 * @param useWindowsSeparator	if <code>true</code>, windows double 
	 * 								backslash '\\'	will be set as separator.
	 * 								Otherwise UNIX '/' will be used as separator 
	 */
	public TaskPathBuilder(boolean useWindowsSeparator) {
		separator = useWindowsSeparator ? "\\" : "/";
	}
	
	/**
	 * Appends working directory of given task to constructed path
	 * @param taskTID	TID of task which working directory should be used
	 * @return <code>this</code> instance of TaskPathBuilder
	 * @throws IllegalArgumentException when <code>taskTID</code> is null or empty
	 */
	public TaskPathBuilder addWorking(String taskTID) {
		checkValidTid(taskTID);
		path.append("${").append(taskTID).append(WORKING_DIR).append("}");
		return this;
	}
	
	/**
	 * Appends temporary directory of given task to constructed path
	 * @param taskTID	TID of task which temporary directory should be used
	 * @return <code>this</code> instance of TaskPathBuilder
	 * @throws IllegalArgumentException when <code>taskTID</code> is null or empty
	 */
	public TaskPathBuilder addTemp(String taskTID) {
		checkValidTid(taskTID);
		path.append("${").append(taskTID).append(TEMP_DIR).append("}");
		return this;
	}

	/**
	 * Appends task directory of given task to constructed path
	 * @param taskTID	TID of task which task directory should be used
	 * @return <code>this</code> instance of TaskPathBuilder
	 * @throws IllegalArgumentException when <code>taskTID</code> is null or empty
	 */
	public TaskPathBuilder addTask(String taskTID) {
		checkValidTid(taskTID);
		path.append("${").append(taskTID).append(TASK_DIR).append("}");
		return this;
	}
	
	/**
	 * Adds any string as path element. The element is prepended with proper
	 * separator before appended to constructed path.
	 * When element is null or an empty string the path is unmodified
	 * 
	 * @param element	Path element to append 
	 * @return <code>this</code> instance of TaskPathBuilder
	 */
	public TaskPathBuilder add(String element) {
		if (StringUtils.isEmpty(element)) {
			// nothing to do
			return this;
		}
		
		addSeparator();
		path.append(element);
		
		return this;
	}
	
	/**
	 * Appends separator only to the constructed path
	 * @return <code>this</code> instance of TaskPathBuilder
	 */
	public TaskPathBuilder addSeparator() {
		path.append(separator);
		return this;
	}
	
	/**
	 * @return Constructed path
	 */
	public String get() {
		return path.toString();
	}
	
	/**
	 * Cleans the constructed path. (Set to an empty string)
	 * 
	 * @return The constructed path before cleaning.
	 */
	public String clean() {
		String p = path.toString();
		path.delete(0, path.length());
		return p;
	}
	
	
	private void checkValidTid(String tid) {
		if (StringUtils.isEmpty(tid)) {
			throw new IllegalArgumentException("Invalid value of TID: " + tid);
		}
	}
	
}
