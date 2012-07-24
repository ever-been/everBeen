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

import java.io.Serializable;

import cz.cuni.mff.been.hostmanager.InvalidArgumentException;

/**
 * Base class for event listener. Event listener is an object that will receive all Load Monitor 
 * events. Listener than can ignore given event or process it. To filter out uninteresting events
 * instance of LoadMonitorEventFilter is used.
 * <br>
 * <br>
 * Each listener has two abstract methods you have to implement. First one (<tt>onAccept</tt>) is
 * called only when event is accepted (that is, it matches given filter). Second method 
 * (<tt>onReject</tt>) is called only when event is rejected by the filter.
 *
 * @author Branislav Repcek
 */
public abstract class LoadMonitorEventListener implements Serializable {

	private static final long	serialVersionUID	= 7351026220742154391L;

	/**
	 * Filter for events.
	 */
	private LoadMonitorEventFilter filter;
	
	/**
	 * Was last event accepted?
	 */
	private boolean accepted;
	
	/**
	 * Create listener that will accept all events.
	 */
	public LoadMonitorEventListener() {
		
		filter = new LoadMonitorEventFilter();
	}

	/**
	 * Create listener that will accept only event that are accepted by the given filter.
	 * 
	 * @param filter Filter that will be used to filter out events.
	 * 
	 * @throws InvalidArgumentException If filter is <tt>null</tt>
	 */
	public LoadMonitorEventListener(LoadMonitorEventFilter filter) 
		throws InvalidArgumentException {
		
		if (filter == null) {
			throw new InvalidArgumentException("null filter for event listener.");
		}
		
		this.filter = filter;
	}
	
	/**
	 * This method is called by the Load Server every time and event is received. Based on the
	 * event this will call either <tt>onAccept</tt> (if event has been accepted by the filter) or
	 * <tt>onReject</tt> (if the event has been rejected by the filter). <tt>isAccepted</tt> flag
	 * will be updated accordingly.
	 * 
	 * @param event Event to process.
	 */
	public void handleEvent(LoadMonitorEvent event) {
		
		if (filter.match(event)) {
			// filter has accepted event
			onAccept(event);
			accepted = true;
		} else {
			// event has been rejected
			onReject(event);
			accepted = false;
		}
	}

	/**
	 * @return <tt>true</tt> if last event has been accepted, <tt>false</tt> if it has been rejected.
	 */
	public boolean isAccepted() {
		
		return accepted;
	}
	
	/**
	 * This method is called every time event is received and is accepted by the filter.
	 * 
	 * @param event Event that has been received.
	 */
	protected abstract void onAccept(LoadMonitorEvent event);
	
	/**
	 * This method is called every time event is received and is rejected by the event filter.
	 * 
	 * @param event Event that has been received.
	 */
	protected abstract void onReject(LoadMonitorEvent event);
}
