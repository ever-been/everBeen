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

import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * Leaf of the task tree that stores a task descriptor directly
 * 
 * @author Andrej Podzimek
 */
final class TaskTreeLeaf extends TaskTreeBasic {

	private static final long serialVersionUID = -3080237328745586867L;

	/** The task descriptor this leaf will store. */
	private final TaskEntry entry;

	/**
	 * Creates a new leaf and initializes its contents.
	 * 
	 * @param tree
	 *            The task tree this leaf belongs to.
	 * @param address
	 *            Address of the leaf.
	 * @param parentAddress
	 *            Address of the parent node of this leaf.
	 * @param entry
	 *            The task entry this leaf will store.
	 */
	TaskTreeLeaf(TaskTree tree, TaskTreeAddressBody address,
			TaskTreeAddressBody parentAddress, TaskEntry entry) {
		super(tree, address, parentAddress);
		this.entry = entry;
	}

	@Override
	public TaskEntry getTask() {
		return entry;
	}

	@Override
	public Type getType() {
		return Type.LEAF;
	}
}
