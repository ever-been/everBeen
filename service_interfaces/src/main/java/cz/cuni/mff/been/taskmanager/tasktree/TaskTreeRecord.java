/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Andrej Podzimek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.taskmanager.tasktree;

import java.io.Serializable;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeItem.Type;

/**
 * A simple class for traversing the tree of tasks efficiently. This avoids the
 * necessity to invoke two remote calls per node to find out the node type and
 * get the value it stores. That brings pain and synchronization problems.
 * 
 * @author Andrej Podzimek
 */
public final class TaskTreeRecord implements Serializable {

	private static final long serialVersionUID = -5297312934426806675L;

	/** Path of the requested element split into segments. */
	private final String[] segments;

	/** Type of the tree element this record represents. */
	private final Type type;

	/** The task entry this record conveys when referring to a LEAF element. */
	private final TaskEntry task;

	/**
	 * Children of an element this record conveys when referring to a NODE
	 * element.
	 */
	private final TaskTreeAddress[] children;

	/** Flags and their values, null if not requested. */
	private final Pair<TaskTreeFlag, TreeFlagValue>[] flags;

	/** A cache for the path string. */
	transient private String path;

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param segments
	 *            Tree path segments.
	 * @param task
	 *            The task this triplet will store, possibly null,
	 * @param flags
	 *            The array of flags and their values.
	 */
	TaskTreeRecord(String[] segments, TaskEntry task,
			Pair<TaskTreeFlag, TreeFlagValue>[] flags) {
		this.segments = segments;
		this.type = Type.LEAF;
		this.task = task;
		this.children = null;
		this.flags = flags;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param segments
	 *            Tree path segments.
	 * @param children
	 *            The array of children this tripplet will store, possibly null.
	 * @param flags
	 *            The array of flags and their values.
	 */
	TaskTreeRecord(String[] segments, TaskTreeAddress[] children,
			Pair<TaskTreeFlag, TreeFlagValue>[] flags) {
		this.segments = segments;
		this.type = Type.NODE;
		this.task = null;
		this.children = children;
		this.flags = flags;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param segments
	 *            Tree path segments.
	 * @param flags
	 *            The array of flags and their values.
	 */
	TaskTreeRecord(String[] segments, Pair<TaskTreeFlag, TreeFlagValue>[] flags) {
		this.segments = segments;
		this.type = null;
		this.task = null;
		this.children = null;
		this.flags = flags;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param segments
	 *            Tree path segments.
	 * @param task
	 *            The task this triplet will store, possibly null,
	 */
	TaskTreeRecord(String[] segments, TaskEntry task) {
		this.segments = segments;
		this.type = Type.LEAF;
		this.task = task;
		this.children = null;
		this.flags = null;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param segments
	 *            Tree path segments.
	 * @param children
	 *            The array of children this tripplet will store, possibly null.
	 */
	TaskTreeRecord(String[] segments, TaskTreeAddress[] children) {
		this.segments = segments;
		this.type = Type.NODE;
		this.task = null;
		this.children = children;
		this.flags = null;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param segments
	 *            Tree path segments.
	 */
	TaskTreeRecord(String[] segments) {
		this.segments = segments;
		this.type = null;
		this.task = null;
		this.children = null;
		this.flags = null;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param task
	 *            The task this triplet will store, possibly null,
	 * @param flags
	 *            The array of flags and their values.
	 */
	TaskTreeRecord(TaskEntry task, Pair<TaskTreeFlag, TreeFlagValue>[] flags) {
		this.segments = null;
		this.type = Type.LEAF;
		this.task = task;
		this.children = null;
		this.flags = flags;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param children
	 *            The array of children this tripplet will store, possibly null.
	 * @param flags
	 *            The array of flags and their values.
	 */
	TaskTreeRecord(TaskTreeAddress[] children,
			Pair<TaskTreeFlag, TreeFlagValue>[] flags) {
		this.segments = null;
		this.type = Type.NODE;
		this.task = null;
		this.children = children;
		this.flags = flags;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param flags
	 *            The array of flags and their values.
	 */
	TaskTreeRecord(Pair<TaskTreeFlag, TreeFlagValue>[] flags) {
		this.segments = null;
		this.type = null;
		this.task = null;
		this.children = null;
		this.flags = flags;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param task
	 *            The task this triplet will store, possibly null,
	 */
	TaskTreeRecord(TaskEntry task) {
		this.segments = null;
		this.type = Type.LEAF;
		this.task = task;
		this.children = null;
		this.flags = null;
	}

	/**
	 * Initializes an instance of the record with the supplied data items.
	 * 
	 * @param children
	 *            The array of children this tripplet will store, possibly null.
	 */
	TaskTreeRecord(TaskTreeAddress[] children) {
		this.segments = null;
		this.type = Type.NODE;
		this.task = null;
		this.children = children;
		this.flags = null;
	}

	/**
	 * Initializes an instance of the record to nulls.
	 */
	TaskTreeRecord() {
		this.segments = null;
		this.type = null;
		this.task = null;
		this.children = null;
		this.flags = null;
	}

	/**
	 * Path segments getter.
	 * 
	 * @return The requested element's tree address split into segments.
	 */
	public String[] getPathSegments() {
		return segments;
	}

	/**
	 * Type getter.
	 * 
	 * @return Type of the underlying tree element.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Task getter.
	 * 
	 * @return The task entry from a LEAF element, returns null on NODE
	 *         elements.
	 */
	public TaskEntry getTask() {
		return task;
	}

	/**
	 * Children getter.
	 * 
	 * @return The array of children from a NODE element, returns null on LEAF
	 *         elements.
	 */
	public TaskTreeAddress[] getChildren() {
		return children;
	}

	/**
	 * Flags getter.
	 * 
	 * @return The array of flags and their values, null if not requested.
	 */
	public Pair<TaskTreeFlag, TreeFlagValue>[] getFlags() {
		return flags;
	}

	/**
	 * Path string getter. Computes a path string from the path string segments.
	 * 
	 * @return A path string in standard slash syntax.
	 */
	public String getPathString() {
		if (null == path) {
			if (null != segments) {
				path = TaskTreeAddressBody.segToString(segments);
			}
		}
		return path;
	}
}
