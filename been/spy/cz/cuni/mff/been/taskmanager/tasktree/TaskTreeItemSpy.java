package cz.cuni.mff.been.taskmanager.tasktree;

import java.util.Collection;

import cz.cuni.mff.been.taskmanager.data.TaskEntry;

final class TaskTreeItemSpy extends TaskTreeBasic {

	private static final long	serialVersionUID	= -7004754132605468391L;

	public TaskTreeItemSpy( TaskTree tree, TaskTreeAddressBody address, TaskTreeAddressBody parentAddress ) {
		super( tree, address, parentAddress );
	}

	@Override
	public
	Collection< TaskTreeAddressBody > getChildren() throws IllegalAddressException {
		throw new UnsupportedOperationException();
	}

	@Override
	public
	TaskEntry getTask() throws IllegalAddressException {
		throw new UnsupportedOperationException();
	}

	@Override
	public
	Type getType() {
		throw new UnsupportedOperationException();
	}
}
