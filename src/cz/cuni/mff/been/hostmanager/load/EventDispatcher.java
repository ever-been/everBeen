/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager.load;

import java.rmi.RemoteException;

import java.util.LinkedList;

/**
 * Process incoming events and dispatch them to the Load Server (if valid reference exists).
 *
 * @author Branislav Repcek
 */
class EventDispatcher {

	/**
	 * Event processor thread.
	 */
	private EventProcessorThread thread;
	
	/**
	 * Create new dispatcher without connection to the server.
	 * 
	 * @param queue Event queue.
	 */
	public EventDispatcher(EventQueue queue) {
		
	}
	
	/**
	 * Create new EventDispatcher.
	 * 
	 * @param queue Event queue.
	 * @param loadServer Load Server to which data will be sent. If this is <tt>null</tt> samples
	 *        will not be sent but only thrown away.
	 */
	public EventDispatcher(EventQueue queue, LoadServerInterface loadServer) {
		
		thread = new EventProcessorThread(queue, loadServer);
		thread.start();
	}
	
	/**
	 * Stop dispatching events.
	 */
	public void kill() {

		if (thread != null) {
			thread.interrupt();
		}
	}
	
	/**
	 * Stop dispatching events as soon as the queue is empty. Call this method only when you are
	 * sure that no data will be added to the work queue.
	 */
	public void stop() {
		
		if (thread != null) {
			thread.stopMe();
		}
	}

	/**
	 * Test if dispatcher thread is running.
	 * 
	 * @return <tt>true</tt> if dispatcher thread is running, <tt>false</tt> otherwise.
	 */
	public synchronized boolean isRunning() {
		
		if (thread != null) {
			return thread.isAlive();
		} else {
			return false;
		}
	}
	
	/**
	 * Process events from queue and send them to the server.
	 *
	 * @author Branislav Repcek
	 */
	private class EventProcessorThread extends Thread {

		/**
		 * Stop?
		 */
		private boolean stop;
		
		/**
		 * Target server.
		 */
		private final LoadServerInterface loadServer;

		/**
		 * Event queue.
		 */
		private final EventQueue queue;
		
		/**
		 * Currently processed events.
		 */
		private LinkedList< LoadMonitorEvent > events;
		
		/**
		 * Create new EventProcessor.
		 * 
		 * @param queue Event queue.
		 * @param loadServer Target server. If <code>null</code> events will just be removed from
		 *        the queue.
		 */
		public EventProcessorThread(EventQueue queue, LoadServerInterface loadServer) {
			
			this.loadServer = loadServer;
			this.queue = queue;
			events = new LinkedList< LoadMonitorEvent >();
		}

		/**
		 * Stop dispatching events. Note that this method does not stop thread immediately, but
		 * thread will process all remaining events in the queue and then stop.
		 * To stop thread immediately, call interrupt.
		 */
		public synchronized void stopMe() {
			
			stop = true;
		}
		
		/*
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {

			while (!stop) {
				events.clear();
				queue.getAll(events);
				
				if (loadServer != null) {
					try {
						loadServer.reportEvents(events);
					} catch (RemoteException e) {
						// ignore
					}
				}
				
				if (isInterrupted()) {
					return;
				}
			}
			
			if (!queue.isEmpty()) {
				events.clear();
				queue.getAll(events);
				
				if (loadServer != null) {
					try {
						loadServer.reportEvents(events);
					} catch (RemoteException e) {
						// ignore
					}
				}
			}
		}
	}
}
