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

import cz.cuni.mff.been.task.Service;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * An example service task.
 * 
 * @author Jaroslav Urban
 */
public class ExampleService1 extends Service {
	/** 
	 * the last part of the RMI URL to the one implemented 
	 * remote interface
	 */
	public static final String REMOTE_INTERFACE_1 = "iface1";
	/** the implementation of a remote interface **/
	private ExampleRemoteInterfaceImplementation impl;
	/** name of the service **/
	private static final String SERVICE_NAME = "example-service1";
	
	/**
	 * 
	 * Allocates a new <code>ExampleService1</code> object.
	 *
	 * @throws TaskInitializationException
	 */
	public ExampleService1() throws TaskInitializationException {
		try {
			// create the implementation
			impl = new ExampleRemoteInterfaceImplementation();
		}
		catch (RemoteException e) {
			e.printStackTrace();
			logFatal("Cannot create service implementation: " + e.getMessage());
			System.exit(EXIT_CODE_ERROR);
		}
		
		addRemoteInterface(REMOTE_INTERFACE_1, impl);
	}
	
	/**
	 * @see cz.cuni.mff.been.task.Service#getName()
	 */
	@Override
	public String getName() {
		return SERVICE_NAME;
	}

	/**
	 * @see cz.cuni.mff.been.task.Service#start()
	 */
	@Override
	protected void start(){
		// you have to override this method in your service,
		// it's automatically called in the startService() method
		// of the Service class
		
		// do some initialization in this method
		// the example task will only wait here to simulate 
		// initialization
		
		
		// yum yum yum churning files, tasty
		try {
			// pretend doing some work
			Thread.sleep(2000);
			
			// you could do something like impl.initialize()
			// or you can do the initialization right here in the task
		}
		catch (InterruptedException e) {
			// nothing bad happened, this shouldn't occur anyway
		}
	}

	/**
	 * @see cz.cuni.mff.been.task.Service#stop()
	 */
	@Override
	protected void stop(){
		// you have to override this method in your service,
		// it's automatically called in the stopService() method
		// of the Service class

		// do some cleanup in this method which is needed to cleanly
		// shutdown the service
		
		
		// this example task only pretends to do some cleanup
		try {
			//pretend doing some work
			Thread.sleep(2000);
			
			// you could do something like impl.finish()
			// or you can do the clean-up right here in the task
		}
		catch (InterruptedException e) {
			// nothing bad happened, this shouldn't occur anyway
		}
	}
}
