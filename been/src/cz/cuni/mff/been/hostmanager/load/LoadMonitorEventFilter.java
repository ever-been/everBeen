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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Simple filter for events. This filter is able to filter events based on the host they were raised
 * on and their type.
 *
 * @author Branislav Repcek
 */
public class LoadMonitorEventFilter implements Serializable {

	private static final long	serialVersionUID	= -8888031633603961228L;

	/**
	 * Types of events this filter will match. If <tt>null</tt> all event are matched.
	 */
	private TreeSet< LoadMonitorEvent.EventType > eventTypes;
	
	/**
	 * Names of hosts this filter will match. If <tt>null</tt> all host are matched.
	 */
	private HashSet< String > hostNames;
	
	/**
	 * Create default filter that will accept event of any type from any host.
	 */
	public LoadMonitorEventFilter() {
		
	}
	
	/**
	 * Create filter for given hostname. Filter will accept event of all types.
	 * 
	 * @param hostName Name of the host event must originate from to be accepted. If <tt>null</tt>
	 *        event from any host will be accepted.
	 */
	public LoadMonitorEventFilter(String hostName) {
		
		if (hostName != null) {
			hostNames = new HashSet< String >();
			hostNames.add(hostName);
		}
	}
	
	/**
	 * Create filter for given type of the event. Filter will accept event from all hosts.
	 * 
	 * @param eventType Type which event has to match to be accepted. If <tt>null</tt>, all event
	 *        types are accepted.
	 */
	public LoadMonitorEventFilter(LoadMonitorEvent.EventType eventType) {
		
		if (eventType != null) {
			eventTypes = new TreeSet< LoadMonitorEvent.EventType >();
			eventTypes.add(eventType);
		}
	}
	
	/**
	 * Create filter for given type of event and hostname.
	 * 
	 * @param hostName Name of the host event has to originate on to be accepted. If <tt>null</tt>
	 *        events from all hosts will be accepted.
	 * @param eventType Type of the event that event has to match. If <tt>null</tt> event of all 
	 *        types will be accepted.
	 */
	public LoadMonitorEventFilter(String hostName, LoadMonitorEvent.EventType eventType) {
		
		if (hostName != null) {
			hostNames = new HashSet< String >();
			hostNames.add(hostName);
		}

		if (eventType != null) {
			eventTypes = new TreeSet< LoadMonitorEvent.EventType >();
			eventTypes.add(eventType);
		}
	}
	
	/**
	 * Create filter for event of given type and hosts.
	 * 
	 * @param hostName List of hostnames. If event has been raised on any of the hosts it will be accepted.
	 *        If list is empty, <tt>null</tt> or contains only <tt>null</tt> elements, events from
	 *        all hosts will be accepted.
	 * @param eventType List of event types. If event is of any type from the list, it will be
	 *        accepted. If list is empty, <tt>null</tt> or contains only <tt>null</tt> elements, events
	 *        of all types will be accepted.
	 */
	public LoadMonitorEventFilter(String []hostName, LoadMonitorEvent.EventType []eventType) {
		
		if ((hostName != null) && (hostName.length >= 1)) {
			hostNames = new HashSet< String >();
			
			for (String c: hostName) {
				if (c != null) {
					hostNames.add(c);
				}
			}
			
			if (hostNames.size() == 0) {
				hostNames = null;
			}
		}
		
		if ((eventType != null) && (eventType.length >= 1)) {
			eventTypes = new TreeSet< LoadMonitorEvent.EventType >();
			
			for (LoadMonitorEvent.EventType c: eventType) {
				if (c != null) {
					eventTypes.add(c);
				}
			}
			
			if (eventTypes.size() == 0) {
				eventTypes = null;
			}
		}
	}
	
	/**
	 * Create filter for event of given type and hosts.
	 * 
	 * @param hostName Collection with list of hostnames. If event has been raised on any of the 
	 *        hosts it will be accepted. If collection is empty, <tt>null</tt> or contains only 
	 *        <tt>null</tt> elements, events from all hosts will be accepted.
	 * @param eventType Collection of event types. If event is of any type from the collection, 
	 *        it will be accepted. If collection is empty, <tt>null</tt> or contains only 
	 *        <tt>null</tt> elements, events of all types will be accepted.
	 */
	public LoadMonitorEventFilter(Collection< String > hostName, 
	                              Collection< LoadMonitorEvent.EventType > eventType) {
		
		if ((hostName != null) && (hostName.size() > 0)) {
			hostNames = new HashSet< String >();

			for (String c: hostName) {
				if (c != null) {
					hostNames.add(c);
				}
			}
			
			if (hostNames.size() == 0) {
				hostNames = null;
			}
		}

		if ((eventType != null) && (eventType.size() > 0)) {
			eventTypes = new TreeSet< LoadMonitorEvent.EventType >();
			
			for (LoadMonitorEvent.EventType c: eventType) {
				if (c != null) {
					eventTypes.add(c);
				}
			}
			
			if (eventTypes.size() == 0) {
				eventTypes = null;
			}
		}
	}
	
	/**
	 * @return Set containing names of host events have to originate on.
	 */
	public Set< String > getHostNames() {
		
		return hostNames;
	}
	
	/**
	 * @return Set containing types of events.
	 */
	public Set< LoadMonitorEvent.EventType > getEventTypes() {
		
		return eventTypes;
	}
	
	/**
	 * Test if given event matches criteria specified in this filter.
	 * 
	 * @param event Event to test.
	 * 
	 * @return <tt>true</tt> if event matches criteria in the filter, <tt>false</tt> otherwise.
	 */
	public boolean match(LoadMonitorEvent event) {
		
		boolean result = false;
		
		if (hostNames != null) {
			result = hostNames.contains(event.getHostName());
		} else {
			result = true;
		}
		
		if (eventTypes != null) {
			result &= eventTypes.contains(event.getType());
		} else {
			result &= true;
		}
		
		return result;
	}
}
