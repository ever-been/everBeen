/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.taskmanager.tasktree;

import java.io.Serializable;
import java.rmi.RemoteException;

import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeItem.Type;

/**
 * This is a small adapter that splits the read and write access to the task tree. It implements
 * {@link TaskTreeQuery} and relays all the calls so that this implementation cannot be cast
 * to the reading interface.
 * 
 * @author Andrej Podzimek
 */
public final class TaskTreeReader implements TaskTreeQuery, Serializable {

	private static final long serialVersionUID = 3642321905249232212L;

	/** A reference to the real implementation we want to hide. */
	private final TaskTreeQuery query;

	/**
	 * Creates a new task tree reader.
	 * 
	 * @param query The query interface to read from.
	 */
	public TaskTreeReader( TaskTreeQuery query ) {
		this.query = query;
	}

	@Override
	public TaskTreeAddress addressFromPath( String path )
	throws MalformedAddressException, RemoteException {
		return query.addressFromPath( path );
	}

	@Override
	public TaskTreeAddress addressFromPath( String ... path )
	throws MalformedAddressException, RemoteException {
		return query.addressFromPath( path );
	}

	@Override
	public Type getTypeAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		return query.getTypeAt( address );
	}

	@Override
	public TaskEntry getTaskAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		return query.getTaskAt( address );
	}

	@Override
	public String[] getPathAt( TaskTreeAddress address )
	throws RemoteException {
		return query.getPathAt( address );
	}

	@Override
	public TaskTreeAddress[] getChildrenAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		return query.getChildrenAt( address );
	}

	public TaskTreeRecord getRecordAt(
		TaskTreeAddress address,
		boolean path,
		boolean data,
		boolean flags
	) throws IllegalAddressException, RemoteException {
		return query.getRecordAt( address, path, data, flags );
	}

	@Override
	public TaskTreeFlag[] getFlagsAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		return query.getFlagsAt( address );
	}

	@Override
	public TreeFlagValue getFlagValueAt( TaskTreeAddress address, TaskTreeFlag flag )
	throws IllegalAddressException, RemoteException {
		return query.getFlagValueAt( address, flag );
	}
}
