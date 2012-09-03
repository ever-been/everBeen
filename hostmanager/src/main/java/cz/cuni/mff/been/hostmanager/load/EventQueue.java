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

import java.util.Collection;
import java.util.LinkedList;

/**
 * Thread safe queue for load events.
 *
 * @author Branislav Repcek
 */
class EventQueue {

	/**
	 * Event storage.
	 */
	private LinkedList< LoadMonitorEvent > queue;
	
	/**
	 * Create new empty event queue.
	 */
	public EventQueue() {
		
		queue = new LinkedList< LoadMonitorEvent >();
	}

	/**
	 * Test is queue is empty.
	 * 
	 * @return <tt>true</tt> is queue is empty, <tt>false</tt> otherwise.
	 */
	public synchronized boolean isEmpty() {
		
		return queue.isEmpty();
	}
	
	/**
	 * @return Number of events in the queue.
	 */
	public synchronized int size() {
		
		return queue.size();
	}
	
	/**
	 * Add new event to the queue.
	 * 
	 * @param event Event to add to the queue.
	 */
	public synchronized void add(LoadMonitorEvent event) {
		
		queue.addLast(event);
		notify();
	}
	
	/**
	 * Blocking method that will wait until there is some data in the queue.
	 * 
	 * @return first event in the queue.
	 */
	public synchronized LoadMonitorEvent getFirst() {
		
		while (queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		
		return queue.getFirst();
	}
	
	/**
	 * Get all events from the queue and store them in another Collection. This method is blocking
	 * and waits until there is data available.
	 * 
	 * @param target Collection into which events will be copied. Original contents of the target
	 *        collection will not be altered.
	 */
	public synchronized void getAll(Collection< LoadMonitorEvent > target) {
		
		while (queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		
		target.addAll(queue);
		queue.clear();
	}
	
	/**
	 * Add all events to the queue.
	 * 
	 * @param events Events to add to the queue.
	 */
	public synchronized void addAll(Collection< LoadMonitorEvent > events) {
		
		queue.addAll(events);
		notify();
	}
	
	/**
	 * Add all events to the queue.
	 * 
	 * @param events Events to add to the queue.
	 */
	public synchronized void addAll(LoadMonitorEvent []events) {
		
		for (LoadMonitorEvent event: events) {
			queue.addLast(event);
		}
		
		notify();
	}
}
