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

import java.util.LinkedList;

import java.util.concurrent.ConcurrentMap;

import cz.cuni.mff.been.common.id.OID;
import cz.cuni.mff.been.hostmanager.HostManagerLogger;

/**
 * This class automatically creates new thread which will process all incoming events.
 *
 * @author Branislav Repcek
 */
class EventReceiver {

	/**
	 * Event processor thread.
	 */
	private EventProcessorThread thread;
	
	/**
	 * Logger.
	 */
	private HostManagerLogger logger;
	
	/**
	 * Create new EventReceiver.
	 * 
	 * @param queue Event queue.
	 * @param listeners Event listeners.
	 * @param logger Logger which will receive all logs.
	 */
	public EventReceiver(EventQueue queue, ConcurrentMap< OID, LoadMonitorEventListener > listeners,
			HostManagerLogger logger) {
		
		this.logger = logger;
		thread = new EventProcessorThread(queue, listeners);
		thread.start();
	}
	
	/**
	 * Stop receiving events.
	 */
	public void kill() {

		thread.interrupt();
	}
	
	/**
	 * Stop receiving events and process rest of the work queue.
	 */
	public void stop() {
		
		thread.stopMe();
	}
	
	/**
	 * This class will process all events that are in event queue and dispatch them to all listeners
	 * in listener list. 
	 *
	 * @author Branislav Repcek
	 */
	private class EventProcessorThread extends Thread {

		/**
		 * Stop?
		 */
		private boolean stop;
		
		/**
		 * Event queue.
		 */
		private final EventQueue queue;
		
		/**
		 * Event listeners.
		 */
		private ConcurrentMap< OID, LoadMonitorEventListener > listeners;
		
		/**
		 * Currently processed events.
		 */
		private LinkedList< LoadMonitorEvent > events;
		
		/**
		 * Create new EventProcessorThread.
		 * 
		 * @param queue Event queue.
		 * @param listeners Listener list.
		 */
		public EventProcessorThread(EventQueue queue, 
				ConcurrentMap< OID, LoadMonitorEventListener > listeners) {
			
			super();
			
			this.queue = queue;
			this.listeners = listeners;
			events = new LinkedList< LoadMonitorEvent >();
		}

		/**
		 * Stop processing events. Note that this will not stop immediately, but wait until the
		 * remaining data in queue is processed.
		 */
		public synchronized void stopMe() {
			
			stop = true;
		}
		
		/*
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {

			// Repeat until the end of the universe - 1
			while (!stop) {
				events.clear();
				queue.getAll(events);
				
				synchronized (listeners) {
					
					// For each listeners
					for (LoadMonitorEventListener listener: listeners.values()) {
						// Process all events
						for (LoadMonitorEvent event: events) {
							try {
								listener.handleEvent(event);
							} catch (Exception e) {
								// Do nothing, we have to process remaining events
								// hmm, very helpful message
								logger.logError("Listener error.", e);
							}
						}
					}
				}
				
				if (isInterrupted()) {
					return;
				}
			}
		}
	}
}
