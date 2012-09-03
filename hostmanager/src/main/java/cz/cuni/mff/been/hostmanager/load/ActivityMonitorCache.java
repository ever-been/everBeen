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

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

/**
 * Cache which stores data about activity of all hosts. 
 *
 * @author Branislav Repcek
 */
public class ActivityMonitorCache implements Iterable< String > {

	/**
	 * Cache data.
	 */
	private ConcurrentHashMap< String, CacheElement > data;
	
	/**
	 * Create new empty cache.
	 */
	public ActivityMonitorCache() {
		
		data = new ConcurrentHashMap< String, CacheElement >();
	}

	/**
	 * Remove all data stored in the cache.
	 */
	public synchronized void clear() {
		
		data.clear();
	}
	
	/**
	 * Test if given host is already in the cache.
	 * 
	 * @param hostName Fully qualified name of the host to test.
	 * 
	 * @return <tt>true</tt> if host is already in the cache, <tt>false</tt> otherwise.
	 * 
	 * @throws InvalidArgumentException If host name is <tt>null</tt> or empty string.
	 */
	public synchronized boolean isCached(String hostName) throws InvalidArgumentException {
		
		MiscUtils.verifyStringParameterBoth(hostName, "hostName");
		
		return data.contains(hostName);
	}
	
	/**
	 * Add new element to the cache. If such host already is already in the cache, old entry will
	 * be replaced by the new (empty) one.
	 * 
	 * @param hostName Name of the host (fully qualified).
	 * @param loadMonitor Reference to the Load Monitor that is running on the host.
	 * 
	 * @throws InvalidArgumentException If reference to the Load Monitor is <tt>null</tt> or if
	 *         host name is <tt>null</tt> or empty string.
	 */
	public synchronized void addCacheElement(String hostName, LoadMonitorInterface loadMonitor)
		throws InvalidArgumentException {

		MiscUtils.verifyStringParameterBoth(hostName, "hostName");

		data.put(hostName, new CacheElement(hostName, loadMonitor));
	}
	
	/**
	 * Get cached data for specified host.
	 * 
	 * @param hostName Fully qualified name of the host.
	 * 
	 * @return Entry for requested host or <tt>null</tt> if host is not in cache.
	 * 
	 * @throws InvalidArgumentException If host name is empty string or <tt>null</tt>.
	 */
	public synchronized CacheElement getCacheElement(String hostName) throws InvalidArgumentException {
		
		MiscUtils.verifyStringParameterBoth(hostName, "hostName");
		
		return data.get(hostName);
	}
	
	/**
	 * Retrieve reference to the Load Monitor running on the specified host.
	 * 
	 * @param hostName Fully qualified name of the host.
	 * 
	 * @return Reference to the Load Monitor.
	 * 
	 * @throws ValueNotFoundException If host is not in cache.
	 * @throws InvalidArgumentException If host name is empty string or <tt>null</tt>.
	 */
	public LoadMonitorInterface getLoadMonitor(String hostName)
		throws ValueNotFoundException, InvalidArgumentException {
		
		MiscUtils.verifyStringParameterBoth(hostName, "hostName");
		
		synchronized (data) {
			CacheElement x = data.get(hostName);
			
			if (x != null) {
				return x.getLoadMonitor();
			} else {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in cache.");
			}
		}
	}
	
	/**
	 * Retrieve status of the host.
	 * 
	 * @param hostName Fully qualified name of the host.
	 * 
	 * @return Status of the host.
	 * 
	 * @throws ValueNotFoundException If host is not in cache.
	 * @throws InvalidArgumentException If host name is empty string or <tt>null</tt>.
	 */
	public HostStatus getHostStatus(String hostName) 
		throws ValueNotFoundException, InvalidArgumentException {
		
		MiscUtils.verifyStringParameterBoth(hostName, "hostName");
		
		synchronized (data) {
			CacheElement x = data.get(hostName);
			
			if (x != null) {
				return x.getStatus();
			} else {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in cache.");
			}
		}
	}

	/**
	 * Remove host from the cache.
	 * 
	 * @param hostName Name of the host to remove.
	 * 
	 * @return Data about host from the cache.
	 * 
	 * @throws ValueNotFoundException If host is not in cache.
	 * @throws InvalidArgumentException If host name is empty string or <tt>null</tt>.
	 */
	public CacheElement removeCacheElement(String hostName)
		throws InvalidArgumentException, ValueNotFoundException {
		
		MiscUtils.verifyStringParameterBoth(hostName, "hostName");
		
		synchronized (data) {
			CacheElement result = data.get(hostName);
			
			if (result != null) {
				return result;
			} else {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in cache.");
			}
		}
	}
	
	/**
	 * Get timestamp of the last modification of the cache entry.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @return Time of the last modification of the cache entry (milliseconds since the Unix epoch).
	 * 
	 * @throws ValueNotFoundException If host is not in cache.
	 * @throws InvalidArgumentException If host name is empty string or <tt>null</tt>.
	 */
	public long getLastChangeTime(String hostName)
		throws InvalidArgumentException, ValueNotFoundException {
		
		MiscUtils.verifyStringParameterBoth(hostName, "hostName");
		
		synchronized (data) {
			CacheElement result = data.get(hostName);
			
			if (result != null) {
				return result.getLastChangeTime();
			} else {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in cache.");
			}
		}
	}
	
	/**
	 * Get iterator over the set of the names of all hosts that are in the cache.
	 * 
	 * @return Iterator over the set of the host names.
	 */
	public synchronized Iterator< String > iterator() {
		return new Iterator< String >() {
			Iterator< String >	it = data.keySet().iterator();
			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
			
			@Override
			public String next() {
				return it.next();
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException( "No, don't do that!" );
			}
		};
	}
	
	/**
	 * Store event in the cache if the host from which event originated is already cached.
	 * 
	 * @param event Event to store.
	 * 
	 * @return <tt>true</tt> if event has been successfully stored in the cache, <tt>false</tt> if
	 *         host from which event originated is not in cache and event couldn't be stored.
	 */
	public boolean store(LoadMonitorEvent event) {
		
		synchronized (data) {
			CacheElement elem = data.get(event.getHostName());
			
			if (elem == null) {
				// Host is not in cache
				return false;
			}
			
			elem.reportEvent(event);
			
			return true;
		}
	}
	
	/**
	 * One entry in the cache. Stores all data about one host.
	 *
	 * @author Branislav Repcek
	 */
	public class CacheElement {
		
		/**
		 * Timestamp of the last modification of the class' data (milliseconds since Unix epoch).
		 */
		private long lastChangeTime;
		
		/**
		 * Last event received from the host.
		 */
		private LoadMonitorEvent lastEvent;
		
		/**
		 * Last known hardware configuration.
		 */
		private HardwareDescription lastDescription;
		
		/**
		 * Last sample received from the host.
		 */
		private LoadSample lastSample;
		
		/**
		 * Timestamp which specifies time when host was added to the cache.
		 */
		private long onlineSince;
		
		/**
		 * Reference to the Load Monitor running on the host.
		 */
		private LoadMonitorInterface loadMonitor;
		
		/**
		 * Status of the host.
		 */
		private HostStatus status;
		
		/**
		 * Fully qualified name of the host.
		 */
		private String hostName;
		
		/**
		 * Create new cache element.
		 * 
		 * @param hostName Name of the host (fully qualified).
		 * @param loadMonitor Reference to the Load Monitor running on the host.
		 * 
		 * @throws InvalidArgumentException If reference to the Load Monitor is <tt>null</tt> or if
		 *         host name is <tt>null</tt> or empty string.
		 */
		public CacheElement(String hostName, LoadMonitorInterface loadMonitor)
			throws InvalidArgumentException {
			
			MiscUtils.verifyParameterIsNotNull(loadMonitor, "loadMonitor");
			MiscUtils.verifyStringParameterBoth(hostName, "hostName");
			
			this.onlineSince = this.lastChangeTime = System.currentTimeMillis();
			this.loadMonitor = loadMonitor;
			this.status = HostStatus.ONLINE;
			this.hostName = hostName;
		}
		
		/**
		 * Report new event that has been received from the host. Last hardware description,
		 * sample, event and modification time will be set automatically.
		 * 
		 * @param event Event to report.
		 */
		public synchronized void reportEvent(LoadMonitorEvent event) {
			
			lastChangeTime = System.currentTimeMillis();
			lastEvent = event;
			
			if (event.hasHardwareDescription()) {
				lastDescription = event.getHardwareDescription();
			}
			
			if (event.hasSample()) {
				lastSample = event.getSample();
			}
		}

		/**
		 * @return Status of the host.
		 */
		public synchronized HostStatus getStatus() {
			
			return status;
		}

		/**
		 * @param status New status of the host.
		 */
		public synchronized void setStatus(HostStatus status) {
			
			this.status = status;
		}

		/**
		 * @return Last known hardware description of the host. This may be <tt>null</tt>.
		 */
		public synchronized HardwareDescription getLastDescription() {
			
			return lastDescription;
		}

		/**
		 * @return Last event received from the host. May be <tt>null</tt>.
		 */
		public synchronized LoadMonitorEvent getLastEvent() {
			
			return lastEvent;
		}

		/**
		 * @return Time of the last modification of this cache element in milliseconds since the 
		 *         Unix epoch.
		 */
		public synchronized long getLastChangeTime() {
			
			return lastChangeTime;
		}

		/**
		 * @return Most recent sample received from the host. This may be <tt>null</tt> if no samples
		 *         have been received.
		 */
		public synchronized LoadSample getLastSample() {
			
			return lastSample;
		}

		/**
		 * @return Reference to the Load Monitor that is running on the host.
		 */
		public synchronized LoadMonitorInterface getLoadMonitor() {
			
			return loadMonitor;
		}

		/**
		 * @return Time at which host has been added to the cache. Measured in milliseconds since
		 *         the Unix epoch.
		 */
		public synchronized long getOnlineSince() {
			
			return onlineSince;
		}
		
		/**
		 * @return Fully qualified name of the host.
		 */
		public synchronized String getHostName() {
			
			return hostName;
		}
	}
}
