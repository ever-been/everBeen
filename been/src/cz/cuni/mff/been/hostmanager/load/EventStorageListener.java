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

import java.io.IOException;

import cz.cuni.mff.been.hostmanager.HostManagerApplicationData;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerLogger;

/**
 * Listener which will store all incoming events in the appropriate file (each host has its own file).
 *
 * @author Branislav Repcek
 */
class EventStorageListener extends LoadMonitorEventListener {

	private static final long	serialVersionUID	= 7581396015721863355L;

	/**
	 * Logger.
	 */
	private HostManagerLogger logger;
	
	/**
	 * App data.
	 */
	private HostManagerApplicationData appData;
	
	/**
	 * Create new listener.
	 * 
	 * @param appData Application data.
	 */
	public EventStorageListener(HostManagerApplicationData appData) {
			
		super();
		
		this.logger = appData.getLogger();
		this.appData = appData;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorEventListener#onAccept(cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent)
	 */
	@Override
	protected void onAccept(LoadMonitorEvent event) {

		try {
			String eventFileName = appData.getDatabase().getLoadFilePath(event.getHostName());

			LoadFileParser< LoadMonitorEvent > eventWriter = 
				new LoadFileParser< LoadMonitorEvent >(eventFileName, true, LoadMonitorEvent.class);
			
			long loadFilePos = eventWriter.getPosition();
			
			// Write event to the load file.
			eventWriter.append(event);
			
			eventWriter.close();
			
			String eventMapFileName = 
				appData.getDatabase().getLoadMapFilePath(event.getHostName());
		
			LoadMapFile loadMapWriter = new LoadMapFile(eventMapFileName, true);
			
			loadMapWriter.append(new LoadMapFile.FileEntry(
					event.getTimestamp(), 
					loadFilePos, 
					event.getType()));
			
			loadMapWriter.close();
		} catch (HostManagerException e) {
			logger.logError("Unable to append load data for host \""
					+ event.getHostName() + "\".", e);
		} catch (IOException e) {
			logger.logError("Unable to append load data for host \""
					+ event.getHostName() + "\".", e);
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorEventListener#onReject(cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent)
	 */
	@Override
	protected void onReject(LoadMonitorEvent event) {
		
		// No events are rejected because we use empty filter. Nothing is needed here.
	}
}
