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

import java.util.Date;

import cz.cuni.mff.been.hostmanager.HostManagerApplicationData;
import cz.cuni.mff.been.hostmanager.HostManagerLogger;
import cz.cuni.mff.been.hostmanager.util.MiscUtils;

/**
 * This listener accepts all events from all hosts and stores samples in the sample cache.
 * Also it monitors events that inform about new host connecting and host shutting down.
 *
 * @author Branislav Repcek
 */
class ActivityMonitorListener extends LoadMonitorEventListener {

	private static final long	serialVersionUID	= 1121855120114955930L;

	/**
	 * Logger.
	 */
	private HostManagerLogger logger;
	
	/**
	 * Cache.
	 */
	private ActivityMonitorCache cache;

	/**
	 * Create new activity listener.
	 * 
	 * @param cache Cache manager.
	 * @param appData Connection to the rest of the service.
	 */
	public ActivityMonitorListener(ActivityMonitorCache cache, HostManagerApplicationData appData) {
		
		super();
		
		this.cache = cache;
		this.logger = appData.getLogger();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorEventListener#onAccept(cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent)
	 */
	@Override
	protected void onAccept(LoadMonitorEvent event) {
		
		logger.logTrace("Event: [" + MiscUtils.formatDate(new Date()) + "] " 
				+ event.getType().toString() + " @ " + event.getHostName());
		
		synchronized (cache) {
			// Try to store event in the cache.
			if (!cache.store(event)) {
				// Event was not stored - that means that we received event from host that has not
				// been reported via Task Manager and Activity Monitor so we report this as warning and
				// discard data.
				logger.logWarning("Received event from unregistered host \""
						+ event.getHostName() + "\".");
			}
			
			if (event.getType() == LoadMonitorEvent.EventType.MONITOR_SHUT_DOWN) {
				ActivityMonitorCache.CacheElement ce = cache.getCacheElement(event.getHostName());
				
				if (ce != null) {
					ce.setStatus(HostStatus.OFFLINE);
				}
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorEventListener#onReject(cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent)
	 */
	@Override
	protected void onReject(LoadMonitorEvent event) {
		
		// No filter, we accept every event
	}
}
