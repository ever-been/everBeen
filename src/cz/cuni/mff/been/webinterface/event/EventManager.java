/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.webinterface.event;

import java.util.LinkedList;
import java.util.List;

/**
 * Manager of events sent in the web interface. Implements the mediator pattern
 * in a very simple way - every listener is notified about every event sent to
 * the manager.
 * 
 * @author David Majda
 */
public class EventManager {
	/** Class instance (singleton pattern). */
	private static EventManager instance;
	/** List of listeners. */
	private List<EventListener> listeners = new LinkedList<EventListener>();
	
	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static EventManager getInstance() {
		if (instance == null) {
			instance = new EventManager();
		}
		return instance;
	}

	/**
	 * Registers new event listener.
	 * 
	 * @param listener listener to register
	 * @throws IllegalArgumentException if the listener was already registered
	 * @throws NullPointerException if the listener is <code>null</code>
	 */
	public void registerEventListener(EventListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener can not be null.");
		}
		if (listeners.contains(listener)) {
			throw new IllegalArgumentException("Listener is already registered.");
		}
		listeners.add(listener);
	}
	
	/**
	 * Unregisters existing event listener.
	 * 
	 * @param listener listener to unregister
	 * @throws IllegalArgumentException if the listener was not registered
	 * @throws NullPointerException if the listener is <code>null</code>
	 */
	public void unregisterEventListener(EventListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener can not be null.");
		}
		if (!listeners.contains(listener)) {
			throw new IllegalArgumentException("Listener is not registered.");
		}
		listeners.remove(listener);
	}

	/**
	 * Sends an event to all listeners.
	 * 
	 * @param event event to send
	 */
	public void sendEvent(Event event) {
		for (EventListener listener: listeners) {
			listener.receiveEvent(event);
		}
	}
}
