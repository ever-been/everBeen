/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task.example.service1;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * Implementation of an example remote interface for a service
 * 
 * @author Jaroslav Urban
 */
public class ExampleRemoteInterfaceImplementation 
extends UnicastRemoteObject 
implements ExampleRemoteInterface{
	
	private static final long	serialVersionUID	= -5744097711425123734L;

	/**
	 * Allocates a new ExampleRemoteInterfaceImplementation object. 
	 * @throws RemoteException
	 */
	public ExampleRemoteInterfaceImplementation() throws RemoteException{
		// empty because you have to create a contructor that throws RemoteException
	}
	
	public void foo() throws RemoteException {
		CurrentTaskSingleton.getTaskHandle().logInfo("foo() invoked");
	}
}
