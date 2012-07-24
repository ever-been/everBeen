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
import java.util.Timer;
import java.util.TimerTask;

import cz.cuni.mff.been.common.id.OID;
import cz.cuni.mff.been.hostmanager.HostManagerApplicationData;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

/**
 * Monitors activity on the network and watches for newly connected or disconnected hosts.
 * 
 * @author Branislav Repcek
 */
class ActivityMonitor {

	/**
	 * Listener which stores incoming samples in the cache.
	 */
	private ActivityMonitorListener activityListener;

	/**
	 * Id of the activity listener.
	 */
	private OID activityListenerID;

	/**
	 * Load Server.
	 */
	private LoadServerImplementation loadServer;

	/**
	 * Settings and logs.
	 */
	private HostManagerApplicationData appData;

	/**
	 * Timer for monitoring thread.
	 */
	private Timer timer;

	/**
	 * Timer refresh speed (milliseconds).
	 */
	private long amRefreshTime;

	/**
	 * Time after which host is marked as dead (milliseconds).
	 */
	private long deadHostDelta;

	/**
	 * Cache manager.
	 */
	private ActivityMonitorCache cache;

	/**
	 * Create new ActivityMonitor and start its thread.
	 * 
	 * @param loadServer Reference to the LoadServer.
	 * @param appData Application's settings.
	 * @param cache Cache manager.
	 * 
	 * @throws LoadMonitorException If an error occurred while registering {@link ActivityMonitorListener}.
	 * 
	 * @see cz.cuni.mff.been.hostmanager.load.ActivityMonitorListener
	 */
	public ActivityMonitor(LoadServerImplementation loadServer, 
		HostManagerApplicationData appData, ActivityMonitorCache cache) throws LoadMonitorException {

		this.loadServer = loadServer;
		this.appData = appData;
		this.cache = cache;

		reloadOptions();
	}

	/**
	 * Restart thread with activity monitor.
	 */
	private void restartActivityMonitorThread() {

		if (timer != null) {
			timer.cancel();
		}

		timer = new Timer(true);
		timer.schedule(new ActivityMonitorThread(), 0, amRefreshTime);
	}

	/**
	 * Register listener.
	 *
	 * @throws LoadMonitorException If something failed (d'oh).
	 */
	private synchronized void registerListener() throws LoadMonitorException {

		if (activityListenerID != null) {
			// unregister old listener
			try {
				loadServer.unregisterEventListener(activityListenerID);
			} catch (RemoteException e) {
				appData.getLogger().logError("Unable to unregister Activity Monitor Listener.", e);
			} catch (ValueNotFoundException e) {
				appData.getLogger().logError("Unable to unregister Activity Monitor Listener.", e);
			} catch (InvalidArgumentException e) {
				assert false : "ActivityMonitorListener is null.";
			}

			activityListenerID = null;
		}

		appData.getLogger().logInfo("Creating ActivityMonitorListener.");
		activityListener = new ActivityMonitorListener(cache, appData);

		try {
			activityListenerID = this.loadServer.registerEventListener(activityListener);
		} catch (RemoteException e) {
			appData.getLogger().logFatal("Unable to register ActivityMonitorListener.", e);
			throw new LoadMonitorException("Unable to register ActivityMonitorListener.", e);
		} catch (InvalidArgumentException e) {
			assert false : "ActivityMonitorListener is null.";
		}
		appData.getLogger().logInfo("ActivityMonitorListener registered successfully.");
	}

	/**
	 * Reload options.
	 * 
	 * @throws LoadMonitorException If reload failed.
	 */
	public void reloadOptions() throws LoadMonitorException {

		try {
			amRefreshTime = appData.getConfiguration().getActivityMonitorInterval();
			deadHostDelta = appData.getConfiguration().getDeadHostTimeout();
		} catch (RemoteException e) {
			throw new LoadMonitorException("Unable to read configuration data for Activity Monitor.");
		}

		registerListener();
		restartActivityMonitorThread();
	}

	/**
	 * Stop Activity Monitor.
	 */
	public void stop() {

		try {
			loadServer.unregisterEventListener(activityListenerID);
		} catch (RemoteException e) {
			appData.getLogger().logError("Unable to unregister ActivityMonitorListener.", e);
		} catch (ValueNotFoundException e) {
			appData.getLogger().logError("Unable to unregister ActivityMonitorListener.", e);
		} catch (InvalidArgumentException e) {
			appData.getLogger().logError("Unable to unregister ActivityMonitorListener.", e);
		}

		// ^  LOL @ Java

		timer.cancel();
	}

	/**
	 * Get status of the host.
	 * 
	 * @param hostName Name of the host to check status of.
	 * 
	 * @return Status of the requested host. Note that this is only informative and may not be
	 *         completely accurate since host may have crashed right after reporting its status
	 *         correctly.
	 *         
	 * @throws InvalidArgumentException If host name is empty string or <tt>null</tt>. 
	 */
	public HostStatus getHostStatus(String hostName) throws InvalidArgumentException {

		synchronized (cache) {

			ActivityMonitorCache.CacheElement elem = cache.getCacheElement(hostName);

			if (elem == null) {
				return HostStatus.OFFLINE;
			} else {
				return elem.getStatus();
			}
		}
	}

	/**
	 * This method is called automatically when new host connects to the network.
	 * 
	 * @param hostName Name of the host that has connected to the network.
	 * 
	 * @throws InvalidArgumentException If host name is empty string or <tt>null</tt>.
	 * @throws ValueNotFoundException If host is not in cache yet.
	 */
	public void newHostConnected(String hostName)
	throws InvalidArgumentException, ValueNotFoundException {

		synchronized (cache) {
			ActivityMonitorCache.CacheElement e = cache.getCacheElement(hostName);

			if (e != null) {
				e.setStatus(HostStatus.ONLINE);
			} else {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not yet cached.");
			}
		}
	}

	/**
	 * Thread which periodically checks if all hosts are online.
	 *
	 * @author Branislav Repcek
	 */
	private class ActivityMonitorThread extends TimerTask {

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			synchronized (cache) {

				long currentTime = System.currentTimeMillis();

				for ( String name : cache ) {

					ActivityMonitorCache.CacheElement elem = cache.getCacheElement(name);

					if (elem == null) {
						continue;
					}

					long changeTime = 0;
					changeTime = elem.getLastChangeTime();

					switch (elem.getStatus()) {
						case ONLINE:
						case UNKNOWN:
							if (currentTime - changeTime > deadHostDelta) {
								// No data received from host for time longer than dead host timeout.
								// Host will be marked as crashed.
								appData.getLogger().logWarning("Host \"" + name
									+ "\" has been marked as CRASHED.");
								elem.setStatus(HostStatus.CRASHED);
							}
							break;

						case CRASHED:
							if (currentTime - changeTime <= deadHostDelta) {
								// Host came back online.
								appData.getLogger().logWarning("Host \"" + name
									+ "\" has come back ONLINE.");
								elem.setStatus(HostStatus.ONLINE);
							}
							break;

						default:
						case OFFLINE:
							// Nothing to do
							break;
					}
				}
			}
		}
	}
}
