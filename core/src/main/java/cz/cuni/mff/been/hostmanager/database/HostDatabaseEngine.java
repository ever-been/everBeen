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

package cz.cuni.mff.been.hostmanager.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.xml.sax.InputSource;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.SubstituteVariableValues;

import cz.cuni.mff.been.hostmanager.HostDatabaseException;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.HostManagerLogger;
import cz.cuni.mff.been.hostmanager.HostQueryCallbackInterface;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;

import cz.cuni.mff.been.hostmanager.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;

import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.value.ValueString;

/**
 * This class manages host database. It takes care of all file operations on all files stored in database (load, host info, groups, history).
 * It is able to watch database integrity and should be able to recover from various errors.
 * 
 * @author Branislav Repcek
 */
public class HostDatabaseEngine implements Serializable {
	
	private static final long	serialVersionUID	= -248544788100198180L;

	/**
	 * Name of the database index file.
	 */
	private static final String DATABASE_INDEX_NAME = "db.index";
	
	/**
	 * Name of the file which contains alias definitions.
	 */
	public static final String ALIAS_DEFINITION_FILE = "alias-definitions";
	
	/**
	 * Maps name of the host to the host's data.
	 */
	private HashMap< String, HostInfo > hostData;
	
	/**
	 * Maps name of the group to the group's data.
	 */
	private HashMap< String, HostGroup > groupData;
	
	/**
	 * List of software alias definitions.
	 */
	private SoftwareAliasList aliases;
	
	/**
	 * Path to the root directory of the database.
	 */
	private String databasePath;
	
	/**
	 * Database index manager.
	 */
	private DatabaseIndex dbIndex;

	/**
	 * Logger.
	 */
	private HostManagerLogger logger;

	/**
	 * Variable substitution for aliases.
	 */
	private SubstituteVariableValues substitutor;
	
	/**
	 * Create new database engine in the given path.
	 * 
	 * @param dbPath Path to the root folder of the database.
	 * @param logger Logger which will be used when writing logs.
	 * 
	 * @throws FileNotFoundException if database directory does not exist or if database index was 
	 *         not found.
	 * @throws HostManagerException Other error occurred.
	 * @throws InvalidArgumentException If path is <code>null</code> or empty string.
	 * @throws HostDatabaseException If database error occurred (invalid files, etc.).
	 */
	public HostDatabaseEngine(String dbPath, HostManagerLogger logger)
		throws FileNotFoundException, HostManagerException, 
		       InvalidArgumentException, HostDatabaseException {
		
		this.logger = logger;
		this.logger.logInfo("Initialising database engine.");
		this.logger.logInfo("Database path is: \"" + dbPath + "\".");
		
		if ((dbPath == null) || (dbPath.length() == 0)) {
			throw new InvalidArgumentException("Illegal path to the database directory.");
		}
		
		databasePath = dbPath;

		if (databasePath.endsWith(File.separator)) {
			databasePath += File.separator;
		}

		// test whether database already exists
		File db = new File(databasePath);
		
		if (!db.exists()) {
			createDatabase();
		}
		
		// Load database.
		load();
		
		// NOTE: do not forget to change this regexp if property syntax changes.
		substitutor = new SubstituteVariableValues("[\\p{Alpha}_]+");
	}

	/**
	 * Load all data in the database. This will load index and all database files containing host
	 * info.
	 * 
	 * @throws HostDatabaseException If an error occurred.
	 */
	private synchronized void load() throws HostDatabaseException {
	
		long timeStart = System.currentTimeMillis();
		
		this.logger.logInfo("Loading data...");
		
		try {
			dbIndex = new DatabaseIndex(getFullFileName(DATABASE_INDEX_NAME), logger);
		} catch (InputParseException e) {
			this.logger.logError("Error parsing index file.", e);
			throw new HostDatabaseException(e);
		}	
	
		HostIndexEntry []list = dbIndex.getHostEntryList();
		
		hostData = new HashMap< String, HostInfo >();

		for (int i = 0; i < list.length; ++i) {

			HostInfo hi = null;
			
			try {
				FileInputStream inputStream = 
					new FileInputStream(getFullFileName(list[i].getDataFileName()));
				
				hi = new HostInfo(new InputSource(inputStream));
			} catch (FileNotFoundException e) {
				this.logger.logError("Unable to find host from the index.");
				dbIndex.removeHostEntry(list[i].getHostName());
				continue;
			} catch (InputParseException e) {
				this.logger.logError("Error parsing host file.", e);
				dbIndex.removeHostEntry(list[i].getHostName());
				continue;
			}
			
			if (hostData.put(list[i].getHostName(), hi) != null) {
				// Host was already in database, we have overwritten previous one
				this.logger.logError("Duplicate host: " + list[i].getHostName());
			}
		}
		
		this.logger.logInfo("Loading group data...");

		GroupIndexEntry []groupList = dbIndex.getGroupEntryList();
		
		groupData = new HashMap< String, HostGroup >();
		
		String defaultGroupMeta = "";
		String defaultGroupDesc = "";
		
		for (int i = 0; i < groupList.length; ++i) {

			HostGroup hg = null;
			
			try {
				FileInputStream input = new FileInputStream(getFullFileName(groupList[i].getDataFileName()));
				
				hg = new HostGroup(new InputSource(input));
			} catch (FileNotFoundException e) {
				this.logger.logError("Unable to find group from the index (name: \""
						+ groupList[i].getGroupName() + "\").");
				dbIndex.removeGroupEntry(groupList[i].getGroupName());
				continue;
			} catch (InputParseException e) {
				this.logger.logError("Error parsing group file (group: \"" + groupList[i].getGroupName()
						+ "\", file:\"" + groupList[i].getDataFileName() + "\").", e);
				dbIndex.removeGroupEntry(groupList[i].getGroupName());
				continue;
			}
			
			if (groupData.containsKey(hg.getName())) {
				this.logger.logError("Group \"" + hg.getName() + "\" is already in database. "
						+ "Older entry will be overwritten.");
			}

			// we have found default group
			if (hg.isDefaultGroup()) {
				if (hg.getDescription().length() > 0) {
					defaultGroupDesc = hg.getDescription();
				}
				
				if (hg.getMetadata().length() > 0) {
					defaultGroupMeta = hg.getMetadata();
				}
			} else {
				if (!checkGroupIntegrity(hg, false)) {
					String fileName = getFullFileName(groupList[i].getDataFileName());
					try {
						XMLHelper.saveXMLSerializable(hg, fileName, true, "UTF-16");
					} catch (Exception e) {
						this.logger.logError("Unable to save repaired group \"" + hg.getName()
								+ "\".", e);
					}
				}
				
				groupData.put(hg.getName(), hg);
			}
		}

		// construct default group with description and metadata found in database.
		// we do this because it is simpler than checking all hosts in group and adding/removing
		// some of them
		HostGroup newDefaultGroup = new HostGroup(HostGroup.DEFAULT_GROUP_NAME);
		
		for (String curHostName : hostData.keySet()) {
			newDefaultGroup.addHost(curHostName);
		}
		
		newDefaultGroup.setDescription(defaultGroupDesc);
		newDefaultGroup.setMetadata(defaultGroupMeta);

		if (dbIndex.hasGroupEntry(HostGroup.DEFAULT_GROUP_NAME)) {
			File oldGroupFile = 
				new File(getFullFileName(dbIndex.getGroupEntry(HostGroup.DEFAULT_GROUP_NAME).getDataFileName()));
			
			if (!oldGroupFile.delete()) {
				this.logger.logFatal("Unable to remove old default group file.");
				throw new HostDatabaseException("Unable to remove old default group file.");
			}
			
			dbIndex.removeGroupEntry(HostGroup.DEFAULT_GROUP_NAME);
		}

		String newGroupFileName = getUniqueFileName("0", "group");

		dbIndex.addGroupEntry(HostGroup.DEFAULT_GROUP_NAME, newGroupFileName);
		
		groupData.put(HostGroup.DEFAULT_GROUP_NAME, newDefaultGroup);
		
		try {
			XMLHelper.saveXMLSerializable(newDefaultGroup, getFullFileName(newGroupFileName), true, "UTF-16");
		} catch (Exception e) {
			this.logger.logFatal("Unable to save default group.", e);
			throw new HostDatabaseException("Unable to save default group.", e);
		}
		
		// save index if any modification have been made
		dbIndex.save();
		updateGroupProperty();

		// load definition file for software aliases
		this.logger.logInfo("Loading alias definition file.");
		try {
			aliases = new SoftwareAliasList(getFullFileName(ALIAS_DEFINITION_FILE));
		} catch (Exception e) {
			this.logger.logError("Unable to load alias definition file.", e);
			this.logger.logInfo("Creating empty alias definition file.");
			
			aliases = new SoftwareAliasList();
			
			try {
				aliases.save(getFullFileName(ALIAS_DEFINITION_FILE));
			} catch (Exception f) {
				this.logger.logFatal("Unable to save new alias definition file.", e);
				throw new HostDatabaseException("Unable to save new alias definition file.", e);
			}
		}
		long timeEnd = System.currentTimeMillis();
		
		this.logger.logInfo("Data loaded in " + (timeEnd - timeStart) + " ms.");
	}
	
	/**
	 * Save all resources.
	 */
	public synchronized void terminate() {
		
		try {
			dbIndex.save();
		} catch (Exception e) {
			logger.logError("Error saving index during database termination.");
		}
		
		try {
			aliases.save(getFullFileName(ALIAS_DEFINITION_FILE));
		} catch (Exception e) {
			logger.logError("Error saving alias definitions during database termination.");
		}
	}
	
	/**
	 * Create empty database.
	 * 
	 * @throws HostDatabaseException If there was an error while creating database files.
	 */
	private void createDatabase() throws HostDatabaseException {
		
		logger.logWarning("Database directory does not exist. Creating empty database.");
		
		File dataDir = new File(databasePath);
		
		if (!dataDir.mkdirs()) {
			throw new HostDatabaseException("Unable to create database directory.");
		}
	}
	
	/**
	 * Add host to the database and write its data to the XML file. Data file is validated and host is
	 * not added if incorrect data is received.
	 * 
	 * @param hostName Name of the host we are adding.
	 * @param data String containing host's data.
	 * @param detectorEncoding Encoding detector library used to write data.
	 * 
	 * @throws HostManagerException If host was already in database. 
	 * @throws InvalidArgumentException If data string specified for the host is not valid.
	 * @throws HostDatabaseException If there was an error updating files in the database.
	 */
	public void addHost(String hostName, String data, String detectorEncoding) 
		throws HostManagerException, InvalidArgumentException, HostDatabaseException {
		
		if (isHost(hostName)) {
			
			logger.logError("Attempted to add host which is already in database, host name \""
					+ hostName + "\".");
			throw new HostManagerException("Unable to add host which is already in database (\""
					+ hostName + "\").");
		}

		synchronized (this) {
			StringReader reader = new StringReader(data);
			
			HostInfo newHostInfo = null;
			
			try {
				newHostInfo = new HostInfo(new InputSource(reader));
			} catch (InputParseException e) {
				logger.logError("Invalid data received for host \"" + hostName + "\".", e);
				throw new InvalidArgumentException("Invalid data received for host \"" + hostName + "\"", e);
			}
			
			buildAliasTable(newHostInfo);
			
			String fileSuffix = MiscUtils.formatDate(newHostInfo.getCheckDateTime(), "-yyyy-MM-dd-HH-mm-ss");
			String dataFileName = getUniqueFileName(hostName + fileSuffix, "host");
			String loadFileName = getUniqueFileName(hostName, "load");
			String loadMapFileName = getUniqueFileName(hostName, "loadmap");
			
			try {
				XMLHelper.saveXMLSerializable(newHostInfo, 
						getFullFileName(dataFileName), true, detectorEncoding);
			} catch (Exception e) {
				logger.logError("Unable to save data file for host \"" + hostName + "\".", e);
				throw new HostDatabaseException("Unable to save data file for host \"" + hostName + "\"", e);
			}
	
			hostData.put(hostName, newHostInfo);
	
			dbIndex.addHostEntry(hostName, dataFileName, loadFileName, 
					loadMapFileName, newHostInfo.getCheckDateTime());			
			
			createEmptyLoadFiles(hostName);
	
			try {
				dbIndex.save();
			} catch (HostDatabaseException e) {
				logger.logError("Unable to update index after adding host.");
				throw e;
			}
			
			HostGroup defaultGroup = groupData.get(HostGroup.DEFAULT_GROUP_NAME);
			
			defaultGroup.addHost(hostName);
			
			try {
				String groupFileName = dbIndex.getGroupEntry(HostGroup.DEFAULT_GROUP_NAME).getDataFileName();
				
				XMLHelper.saveXMLSerializable(defaultGroup, 
				                              getFullFileName(groupFileName), 
				                              true, 
				                              "UTF-16");
			} catch (Exception e) {
				logger.logError("Unable to update default group.", e);
				throw new HostDatabaseException("Unable to save default group.", e);
			}

			updateGroupProperty();
		}
	}
	
	/**
	 * Refresh configuration information for given host. Old data for the host are moved to the history.
	 * Host has to be in database before calling this method.
	 * 
	 * @param hostName Name of the host for which data is to be set.
	 * @param newData Configuration data produced by the detector.
	 * @param detectorEncoding Encoding used by the detector.
	 * 
	 * @throws ValueNotFoundException If host is not in database.
	 * @throws InvalidArgumentException If data string received for the host is invalid.
	 * @throws HostDatabaseException If there was an error while updating database.
	 */
	public void refreshHost(String hostName, String newData, String detectorEncoding) 
		throws ValueNotFoundException, InvalidArgumentException, HostDatabaseException {
		
		if (!isHost(hostName)) {
			logger.logError("Unable to refresh host \"" + hostName + "\" which is not in database.");
			throw new ValueNotFoundException("Unable to refresh host \"" + hostName
					+ "\", host is not in database.");
		}
		
		synchronized (this) {
			StringReader reader = new StringReader(newData);
			
			HostInfo newHostInfo = null;
			
			try {
				newHostInfo = new HostInfo(new InputSource(reader));
			} catch (InputParseException e) {
				logger.logError("Invalid data received for host \"" + hostName + "\". "
						+ "Parser message: " + e.getMessage());
				throw new InvalidArgumentException("Invalid data received for host \"" + hostName + "\"", e);
			}
	
			buildAliasTable(newHostInfo);
			
			String fileSuffix = MiscUtils.formatDate(newHostInfo.getCheckDateTime(), "-yyyy-MM-dd-HH-mm-ss");
			String dataFileName = getUniqueFileName(hostName + fileSuffix, "host");
			
			try {
				XMLHelper.saveXMLSerializable(newHostInfo, getFullFileName(dataFileName), true, detectorEncoding);
			} catch (Exception e) {
				logger.logError("Unable to save data file for host \"" + hostName + "\".", e);
				throw new HostDatabaseException("Unable to save host \"" + hostName + "\"", e);
			}
			
			try {
				HostIndexEntry he = dbIndex.getHostEntry(hostName);
				he.addNewEntry(newHostInfo.getCheckDateTime(), dataFileName);
			} catch (Exception e) {
				logger.logError("Unable to add new entry to the index, host name \"" + hostName + "\".", e);
	
				// try to remove data file
				File file = new File(getFullFileName(dataFileName));
				
				if (!file.delete()) {
					logger.logWarning("Unable to delete data file for host \"" + hostName + "\""
							+ ", file \"" + file.getAbsolutePath() + "\".");
				}
				
				throw new HostDatabaseException("Unable to add entry to the history.", e);
			}
	
			try {
				dbIndex.save();
			} catch (HostDatabaseException e) {
				logger.logError("Unable to update index after host refresh.");
				throw e;
			}
			
			// update data in map
			hostData.put(hostName, newHostInfo);
			
			updateGroupProperty();
		}
	}
	
	/**
	 * Remove host from the database.
	 * 
	 * @param name Name of the host to remove.
	 * 
	 * @throws ValueNotFoundException If host was not found in database.
	 * @throws HostDatabaseException If there was an error while updating database files.
	 */
	public synchronized void removeHost(String name)
		throws ValueNotFoundException, HostDatabaseException {
		
		if (hostData.containsKey(name)) {
			
			// remove host from all groups that contain it.
			for (Iterator< Map.Entry< String, HostGroup > > it = groupData.entrySet().iterator();
			     it.hasNext(); ) {
				
				HostGroup current = it.next().getValue();
				
				if (current.containsHost(name)) {
					current.removeHost(name);
					
					String groupFileName = 
						getFullFileName(dbIndex.getGroupEntry(current.getName()).getDataFileName());

					// save group to the file
					try {
						XMLHelper.saveXMLSerializable(current, groupFileName, true, "UTF-16");
					} catch (Exception e) {
						logger.logError("Unable to save group \"" + current.getName() + "\".", e);
						throw new HostDatabaseException("Unable to save group \""
								+ current.getName() + "\"", e);
					}
				}
			}
			
			HostIndexEntry entry = null;
			
			try {
				entry = dbIndex.getHostEntry(name);
			
				// remove history files
				for (Pair< Date, String > current: entry.getHistoryEntryList()) {
					String entryFileName = current.getValue();
					File entryFile = new File(getFullFileName(entryFileName));
					
					if (!entryFile.delete()) {
						logger.logWarning("Unable to remove entry file \"" + entryFileName + "\".");
					}
				}
			} catch (Exception e) {
				// this should never happen, since we checked for the presence of the host earlier
				assert false : "removeHost: index is out of sync with hostData.";
			}
			
			deleteLoadFiles(name);
			
			// remove host data from table.
			hostData.remove(name);
			
			// remove entry from index
			dbIndex.removeHostEntry(name);
			dbIndex.save();			
		} else {
			logger.logError("Unable to remove host \"" + name + "\", "
					+ "host was not found in database.");
			throw new ValueNotFoundException("Unable to remove host \"" + name + "\", "
					+ "host was not found in database."); 
		}
	}
	
	/**
	 * Find host with given name.
	 * 
	 * @param name Name of the host to find.
	 * 
	 * @return Data about requested host.
	 * 
	 * @throws ValueNotFoundException If requested host was not found in database.
	 */
	public synchronized HostInfoInterface findHost(String name) throws ValueNotFoundException {
		
		HostInfo result = hostData.get(name);
		
		if (result != null) {
			return result;
		} else {
			throw new ValueNotFoundException("Host \"" + name + "\" was not found in database.");
		}
	}
	
	/**
	 * Test whether given host is in database.
	 * 
	 * @param name Name of the host to test.
	 * 
	 * @return <code>true</code> if host is in database, <code>false</code> otherwise.
	 */
	public synchronized boolean isHost(String name) {
		
		return hostData.containsKey(name);
	}
	
	/**
	 * Get names of all hosts in database.
	 * 
	 * @return Array containing names of all hosts in database.
	 */
	public synchronized String[] getHostNames() {
		
		String []res = new String[hostData.keySet().size()];
		
		hostData.keySet().toArray(res);
		
		return res;
	}
	
	/**
	 * Get a snapshot of the current database of host names.
	 * 
	 * @return An array of current host infos.
	 */
	public synchronized HostInfoInterface[] getHostInfos() {
		return hostData.values().toArray( new HostInfoInterface[ hostData.size() ] );
	}
	
	/**
	 * Create list of hosts matching given criteria.
	 * 
	 * @param restrictions Criteria host must match to be included in the result.
	 * 
	 * @return List containing names of host matching given criteria.
	 * 
	 * @throws ValueNotFoundException Unsupported property name.
	 * @throws ValueTypeIncorrectException Incorrect type of property value.
	 * @throws HostManagerException If other error occurred.
	 * 
	 * @see cz.cuni.mff.been.hostmanager.database.ObjectRestriction
	 * @see cz.cuni.mff.been.hostmanager.database.AlternativeRestriction
	 */
	public synchronized ArrayList< String > queryHosts(RestrictionInterface []restrictions) 
		throws ValueNotFoundException, ValueTypeIncorrectException, HostManagerException {
		
		ArrayList< String > result = new ArrayList< String >();
		
		for (Iterator< HostInfo > it = hostData.values().iterator(); it.hasNext(); ) {
			
			HostInfo current = it.next();
			
			if (current.test(restrictions, true)) {
				result.add(current.getHostName());
			}
		}
		
		return result;
	}
	
	/**
	 * Query host database with user specified function and create list of <code>HostInfo</code> objects 
	 * containing info about all hosts which have passed user defined test.
	 * 
	 * @param query Object which provides user-defined testing function.
	 * 
	 * @return List containing names of host matching given criteria.
	 * 
	 * @throws Exception Other error.
	 * 
	 * @see cz.cuni.mff.been.hostmanager.HostQueryCallbackInterface
	 */
	public synchronized ArrayList< String > queryHosts(HostQueryCallbackInterface query)
		throws Exception {
		
		ArrayList< String > result = new ArrayList< String >();
		
		for (Iterator< HostInfo > it = hostData.values().iterator(); it.hasNext(); ) {
			
			HostInfo current = it.next();
			
			if (query.match(current)) {
				result.add(current.getHostName());
			}
		}
		
		return result;
	}
	
	/**
	 * Get list of dates of all history entries for specified host.
	 * 
	 * @param name Name of the host to query.
	 * 
	 * @return List containing dates of all configuration entries present for requested host.
	 * 
	 * @throws ValueNotFoundException If host was not found in database.
	 */
	public synchronized List< Date > getHostHistoryDates(String name)
		throws ValueNotFoundException {

		try {
			return dbIndex.getHostEntry(name).getHistoryEntryDates();
		} catch (ValueNotFoundException e) {
			throw new ValueNotFoundException("Unable to retrieve history dates for \"" + name
					+ "\", " + "host not found in database.", e);
		}
	}
	
	/**
	 * Get configuration of specified host in selected date.
	 * 
	 * @param name Name of the host.
	 * @param date Date of the requested entry.
	 * 
	 * @return Configuration of host at selected time.
	 * 
	 * @throws ValueNotFoundException If host was not found in database or if host does not have history
	 *         entry with requested date.
	 * @throws HostDatabaseException If an error occurred while parsing history file.
	 */
	public synchronized HostInfoInterface getHostHistoryEntry(String name, Date date) 
		throws ValueNotFoundException, HostDatabaseException {
		
		String hiFile = dbIndex.getHostEntry(name).getHistoryEntry(date);
			
		try {
			HostInfo hi = new HostInfo(new InputSource(new FileInputStream(getFullFileName(hiFile)))); 
			
			return hi;
		} catch (Exception e) {
			throw new HostDatabaseException("Error parsing input file for \"" + name + "\".", e);
		}
	}
	
	/**
	 * Remove entry from host's history.
	 * 
	 * @param hostName Name of the host.
	 * @param date Date of the entry to remove.
	 * 
	 * @throws ValueNotFoundException If host or history entry was not found in database. 
	 * @throws HostManagerException If error occurred when writing changes to the database.
	 * @throws HostDatabaseException There was an error while updating database.
	 */
	public synchronized void removeHostHistoryEntry(String hostName, Date date) 
		throws ValueNotFoundException, HostManagerException, HostDatabaseException {

		HostIndexEntry hie = dbIndex.getHostEntry(hostName);
		String entryFile = hie.getHistoryEntry(date);
		File f = new File(getFullFileName(entryFile));
		
		if (!f.delete()) {
			throw new HostDatabaseException("Unable to remove entry file for date \""
					+ MiscUtils.formatDate(date, HostManagerInterface.DEFAULT_DATE_TIME_FORMAT) + "\".");
		}
		
		hie.removeHistoryEntry(date);
				
		dbIndex.save();
	}
	
	/**
	 * Add group to the database.
	 * 
	 * @param group Group to add to the database. Group has to have unique name.
	 * 
	 * @throws HostDatabaseException If an error occurred during database synchronisation.
	 * @throws InvalidArgumentException If given group is not valid or if it is default group.
	 */
	public synchronized void addGroup(HostGroup group)
		throws HostDatabaseException, InvalidArgumentException {
		
		if (group.isDefaultGroup()) {
			// user is not permitted to add another default group
			throw new InvalidArgumentException("Can't add default group.");
		}
		
		if (isGroup(group.getName())) {
			// names of groups must be unique
			throw new InvalidArgumentException("Duplicate group name \"" + group.getName() + "\".");
		}
		
		// Generate name of the file in which group will be saved. We can't use name of the group
		// as we use for .host files, since group name can contain any characters. We will use
		// current time as the base for the group group name. 
		String newFileName = getUniqueFileName(String.valueOf(System.currentTimeMillis()), "group");

		try {
			XMLHelper.saveXMLSerializable(group, getFullFileName(newFileName), true, "UTF-16");
		} catch (Exception e) {
			logger.logError("Unable to save group \"" + group.getName() + "\" to file.", e);
			throw new HostDatabaseException("Unable to save group \"" + group.getName() + "\".", e);
		}

		dbIndex.addGroupEntry(group.getName(), newFileName);
		
		dbIndex.save();
		
		groupData.put(group.getName(), group);
		
		updateGroupProperty();
	}
	
	/**
	 * Remove group from the database.
	 * 
	 * @param name Name of the group to remove.
	 * @return The removed host group instance.
	 * @throws HostDatabaseException If an error occurred during database synchronisation.
	 * @throws ValueNotFoundException If group with specified name is not in database.
	 */
	public synchronized HostGroup removeGroup(String name)
		throws HostDatabaseException, ValueNotFoundException {
		HostGroup result;
		
		if (!isGroup(name)) {
			throw new ValueNotFoundException("Unable to remove non-existant group \"" + name + "\".");
		}
		
		String fileName = dbIndex.getGroupEntry(name).getDataFileName();
		
		File groupFile = new File(getFullFileName(fileName));
		
		if (!groupFile.delete()) {
			throw new HostDatabaseException("Unable to remove group file \"" + fileName + "\".");
		}
		
		result = groupData.remove(name);
		
		dbIndex.removeGroupEntry(name);
		
		dbIndex.save();
		
		updateGroupProperty();
		
		return result;
	}
	
	/**
	 * Update data for specified group. Group with same name (!!ID) has to be already in database. 
	 *  
	 * @param group New data for the group.
	 * 
	 * @throws HostManagerException If error occurred during data synchronisation.
	 * @throws InvalidArgumentException If given group is not valid.
	 * @throws ValueNotFoundException If group to update was not found in database.
	 */
	public synchronized void updateGroup(HostGroup group) 
		throws HostManagerException, InvalidArgumentException, ValueNotFoundException {

		if (!checkGroupIntegrity(group, true)) {
			logger.logError("Unable to modify group \"" + group.getName()
					+ "\" because new group is invalid.");
			throw new InvalidArgumentException("Unable to modify group \"" + group.getName()
					+ "\" because new group is invalid.");
		}
		
		HostGroup original = groupData.get(group.getName());
		
		if (original == null) {
			throw new ValueNotFoundException("Unable to find group \"" + group.getName()
					+ "\" needed for update.");
		}

		String originalFile = getFullFileName(dbIndex.getGroupEntry(original.getName()).getDataFileName());
		
		original.setDescription(group.getDescription());
		original.setMetadata(group.getMetadata());
		
		if (!original.isDefaultGroup()) {
			// if group is not default, we will modify list of hosts
			original.removeAllHosts();
			original.addGroup(group);
		}
		
		try {
			XMLHelper.saveXMLSerializable(original, originalFile, true, "UTF-16");
		} catch (Exception e) {
			throw new HostManagerException("Unable to update group \"" + original.getName() + "\".", e);
		}
		
		groupData.put(group.getName(), group);
		
		updateGroupProperty();
	}
	
	/**
	 * Rename existing group.
	 * 
	 * @param oldName Name of the group to rename.
	 * @param newName New name of the group.
	 * 
	 * @throws HostDatabaseException If an error occurred while working with the database.
	 * @throws ValueNotFoundException Group with specified name does not exist in database.
	 * @throws InvalidArgumentException If you try to rename default group or group with same name 
	 *         as new name already exists in database.
	 */
	public synchronized void renameGroup(String oldName, String newName) 
		throws ValueNotFoundException, HostDatabaseException, InvalidArgumentException {
		
		HostGroup oldGroup = findGroup(oldName);
		
		if (oldGroup.isDefaultGroup()) {
			throw new InvalidArgumentException("You cannot rename default group.");
		}
		
		if (isGroup(newName)) {
			throw new InvalidArgumentException("Unable to rename \"" + oldName + "\" to \""
					+ newName + "\". Group with new name already exists.");
		}
		
		HostGroup newGroup = new HostGroup(newName);
		
		newGroup.setDescription(oldGroup.getDescription());
		newGroup.setMetadata(oldGroup.getMetadata());
		newGroup.addGroup(oldGroup);
		
		removeGroup(oldName);
		
		addGroup(newGroup);
		
		updateGroupProperty();
	}
	
	/**
	 * Find group with specified name.
	 * 
	 * @param name Name of the group to find in the database.
	 * 
	 * @return Data for requested group.
	 * 
	 * @throws ValueNotFoundException If requested group was not found in database.
	 */
	public synchronized HostGroup findGroup(String name) throws ValueNotFoundException {
		
		HostGroup group = groupData.get(name);
		
		if (group != null) {
			return group;
		} else {
			throw new ValueNotFoundException("Group \"" + name + "\" was not found in database.");
		}
	}
	
	/**
	 * Get list of names of all groups in database.
	 * 
	 * @return Array containing names of all groups in database.
	 */
	public synchronized String[] getGroupNames() {
		
		String []result = new String[groupData.size()];
		
		groupData.keySet().toArray(result);
		
		return result;
	}
	
	/**
	 * Test whether group with given name already exists.
	 * 
	 * @param name Name to test.
	 * 
	 * @return <code>true</code> if group already exists, <code>false</code> otherwise.
	 */
	public synchronized boolean isGroup(String name) {
		
		return groupData.containsKey(name);
	}
	
	/**
	 * Get path to the database directory.
	 * 
	 * @return Path to the root folder of the database. Usually it is <tt>[task-directory]/working/hosts</tt>.
	 */
	public synchronized String getDatabasePath() {
		
		return databasePath;
	}
	
	/**
	 * Build list of groups specified host is member of.
	 * 
	 * @param hostName Name of the host to search in groups.
	 * 
	 * @return Array containing names of all groups containing specified host. Default group is 
	 *         included in the array.
	 */
	public synchronized String[] memberOf(String hostName) {
		
		if (hostData.containsKey(hostName)) {
			ArrayList< String > mof = new ArrayList< String >();
		
			for (Iterator< HostGroup > it = groupData.values().iterator(); it.hasNext(); ) {
				
				HostGroup current = it.next();
				
				if (current.containsHost(hostName)) {
					
					mof.add(current.getName());
				}
			}
			
			return (String []) mof.toArray();
		} else {
			return null;
		}
	}

	/**
	 * Get number of the hosts in the database.
	 * 
	 * @return Number of the hosts in the database.
	 */
	public synchronized int getHostCount() {
		
		return hostData.size();
	}
	
	/**
	 * Get number of the groups in the database.
	 * 
	 * @return Number of the groups in the database (including default group).
	 */
	public synchronized int getGroupCount() {
		
		return groupData.size();
	}
	
	/**
	 * Atomic snapshot of the list (map) of host groups.
	 * 
	 * @return An array of host groups.
	 */
	public synchronized HostGroup[] getGroups() {
		return groupData.values().toArray( new HostGroup[ groupData.size() ] );
	}
	
	/**
	 * Find hosts matching given criteria in the database.
	 * 
	 * @param conditions Conditions host must match to be included in the result.
	 * 
	 * @return List containing all hosts matching given criteria.
	 * 
	 * @throws ValueNotFoundException If value specified in one of the conditions was not found in the
	 *         records of specific host.
	 * @throws ValueTypeIncorrectException Type of value in one of the conditions is incorrect.
	 * @throws HostManagerException If other error occurred.
	 */
	public synchronized ArrayList< String > findMatchingHosts(RestrictionInterface []conditions) 
		throws ValueNotFoundException, ValueTypeIncorrectException, HostManagerException {
		
		ArrayList< String > result = new ArrayList< String >();
		
		for (HostInfo hi: hostData.values()) {
			
			if (hi.test(conditions, true)) {
				
				result.add(hi.getHostName());
			}
		}
		
		return result;
	}
	
	/**
	 * Find all hosts which satisfy given criteria.
	 * 
	 * @param callback Class which implements HostQueryCallbackInterface and is used to filter out 
	 *        matching hosts.
	 *        
	 * @return List containing all hosts which matched criteria given by the callback function.
	 * 
	 * @throws Exception If some error occurred (this is pretty vague to allow user-defined exceptions
	 *         to be thrown from inside the callback).
	 */
	public synchronized ArrayList< String > findMatchingHosts(HostQueryCallbackInterface callback)
		throws Exception {
		
		ArrayList< String > result = new ArrayList< String >();
		
		for (HostInfo hi: hostData.values()) {
			
			if (callback.match(hi)) {
				
				result.add(hi.getHostName());
			}
		}
		
		return result;
	}
	
	/**
	 * Get number of alias definitions.
	 * 
	 * @return Number of alias definitions.
	 */
	public synchronized int getAliasDefinitionCount() {
		return aliases.getAliasCount();
	}
	
	/**
	 * Added by Andrej Podzimek as a temporary fix to issue 314406.
	 * 
	 * @return An array containing a consistent snapshot of the list of software aliases.
	 */
	public synchronized SoftwareAliasDefinition[] getAliasDefinitions() {
		return aliases.toArray();
	}
	
	/**
	 * Added by Andrej Podzimek as a temporary fix to issue 314406.
	 * 
	 * @param name Name of the alias to look up.
	 * @return The alias definition indexed by name.
	 * @throws ValueNotFoundException When no such alias definition exists.
	 */
	public synchronized SoftwareAliasDefinition getAliasDefinitionByName( String name )
	throws ValueNotFoundException {
		for ( SoftwareAliasDefinition definition : aliases ) {
			if ( name.equals( definition.getAliasName() ) ) {
				return definition;
			}
		}
		throw new ValueNotFoundException( "No such alias name: " + name );
	}
	
	/**
	 * Get alias definition at given index.
	 * 
	 * @param i Index of definition to retrieve.
	 * 
	 * @return Requested alias definition.
	 * 
	 * @throws ValueNotFoundException f index is invalid.
	 */
	public synchronized SoftwareAliasDefinition getAliasDefinitition(int i)
		throws ValueNotFoundException {
		
		return aliases.get(i);
	}
	
	/**
	 * Add alias definition to the list of definitions. Note that alias definitions are immediately
	 * saved to the file, so if you want to add more aliases, use addAliasDefinitionList method.
	 * 
	 * @param ad Alias definition to add.
	 * 
	 * @return Index of the alias definition that was added.
	 * 
	 * @throws HostDatabaseException If there was an error while writing alias definition file.
	 */
	public synchronized int addAliasDefinition(SoftwareAliasDefinition ad)
		throws HostDatabaseException {
		
		aliases.add(ad);
		
		aliases.save(getFullFileName(ALIAS_DEFINITION_FILE));
		
		return aliases.getAliasCount() - 1;
	}
	
	/**
	 * Add all alias definitions from the list. Note that order of the definitions remains unchanged
	 * and also you can have multiple copies of the same definition. However, multiple copies are not
	 * recommended since it will slow down alias table building (not to mention, that it will result
	 * in multiple aliases with same properties).
	 * 
	 * @param ads List of aliases to add.
	 * 
	 * @throws HostDatabaseException If there was an error updating alias definition file.
	 */
	public synchronized void addAliasDefinitionList(Iterable< SoftwareAliasDefinition > ads) 
		throws HostDatabaseException {
		
		for (SoftwareAliasDefinition a: ads) {
			aliases.add(a);
		}
		
		aliases.save(getFullFileName(ALIAS_DEFINITION_FILE));
	}
	
	/**
	 * Remove alias definition.
	 * 
	 * @param index Index of the definition to remove.
	 * 
	 * @throws ValueNotFoundException If index is invalid.
	 * @throws HostDatabaseException  If there was an error updating alias definition file.
	 */
	public synchronized void removeAliasDefinition(int index)
		throws ValueNotFoundException, HostDatabaseException {
		
		aliases.remove(index);
		
		aliases.save(getFullFileName(ALIAS_DEFINITION_FILE));
	}
	
	/**
	 * Added by Andrej Podzimek as a temporary fix to issue 314406.
	 * 
	 * @param name Name of the software alias to delete.
	 * @return The removed software alias definition instance.
	 * @throws ValueNotFoundException When it rains.
	 * @throws HostDatabaseException When it's foggy.
	 */
	public synchronized SoftwareAliasDefinition removeAliasDefinitionByName( String name )
	throws ValueNotFoundException, HostDatabaseException {
		final int count = aliases.getAliasCount();
		SoftwareAliasDefinition result;
		
		for ( int i = 0; i < count; ++i ) {
			if ( name.equals( aliases.get( i ).getAliasName() ) ) {
				result = aliases.remove( i );
				aliases.save( getFullFileName( ALIAS_DEFINITION_FILE ) );
				return result;
			}
		}
		throw new ValueNotFoundException( "No such alias name: " + name );
	}
	
	/**
	 * Remove all alias definitions.
	 * 
	 * @throws HostDatabaseException If there was an error updating alias definition file.
	 */
	public synchronized void removeAllAliasDefinitions() throws HostDatabaseException {
		
		aliases.removeAll();
		
		aliases.save(getFullFileName(ALIAS_DEFINITION_FILE));
	}
	
	/**
	 * Rebuild table of aliases for each host in database (except host in history) after the alias
	 * definitions have been modified. Do not call this method often since it takes long to complete
	 * and it modifies a lot of files in database.
	 * Note that method will terminate on first error, so if it throws an exception, database update 
	 * may be half-finished (but database should not be corrupt).
	 * 
	 * @throws HostDatabaseException If an error occurred while updating database.
	 */
	public synchronized void rebuildAliasTableForAllHosts() throws HostDatabaseException {

		long timeStart = System.currentTimeMillis();
		
		logger.logInfo("Rebuilding database...");
		
		for (HostInfo hi: hostData.values()) {
			try {
				hi.removeAllAliases();
			} catch (Exception e) {
				logger.logError("Unable to rebuild alias table, error removing aliases from "
						+ "\"" + hi.getHostName() + "\".");
				throw new HostDatabaseException("Error removing aliases from host \""
						+ hi.getHostName() + "\".", e);
			}
			
			buildAliasTable(hi);
			
			try {
				String fileName = 
					getFullFileName(dbIndex.getHostEntry(hi.getHostName()).getDataFileName());

				XMLHelper.saveXMLSerializable(hi, fileName, true, "UTF-16");
			} catch (Exception e) {
				logger.logError("Unable to save data for \"" + hi.getHostName() + "\".");
				throw new HostDatabaseException("Unable to save data for \"" 
						+ hi.getHostName() + "\".", e);
			}
		}

		long timeEnd = System.currentTimeMillis();
		
		logger.logInfo("Rebuld finished in " + (timeEnd - timeStart) + " ms.");
	}
	
	/**
	 * This will check whether group contains invalid hosts (hosts not found in database) and remove them.
	 * For default group, hosts that are not in the group will be automatically added.
	 * 
	 * @param hg Group to check.
	 * @param readOnly If <code>true</code> group will be opened read-only, that is, no attempt to 
	 *        fix errors in the group will be made. If <code>false</code> function will repair errors
	 *        in the group.
	 * 
	 * @return <code>true</code> if the group was OK, <code>false</code> if there was an error in the group.
	 *         If there was an error you should save the group.
	 */
	private boolean checkGroupIntegrity(HostGroup hg, boolean readOnly) {
	
		HashSet< String > toRemove = new HashSet< String >();
		boolean hasError = false;

		// Test all hosts in group. If host is not in database, remove it from the group.
		for (String hostName: hg) {
			
			if (!hostData.containsKey(hostName)) {
			
				logger.logWarning("Host \"" + hostName + "\" from group \"" + hg.getName()
						+ "\" is not in database. Removing...");
				
				// Add host to the list of to-be-removed hosts. 
				toRemove.add(hostName);
				hasError = true;
			}
		}
		
		if (!readOnly) {
			for (String hName: toRemove) {
				
				hg.removeHost(hName); 
			}
		}

		// additional checks for default group
		if (hg.isDefaultGroup()) {
			if (readOnly) {
				if (!hasError) {
					if (getHostCount() != hg.getHostCount()) {
						// number of hosts in group does not equal to number of all hosts, 
						// so group is invalid
						hasError = true;
					} else {
						// same number of hosts, we have to check them one-by-one
						String []hostNames = getHostNames();
						
						for (String current: hostNames) {
							if (!hg.containsHost(current)) {
								hasError = true;
								break;
							}
						}
					}
				}
			} else {
				// this is sufficient test, since we have already removed invalid hosts
				if (hg.getHostCount() != getHostCount()) {
					// some hosts are missing in the group, we need to add all of them to the group
					logger.logWarning("Adding missing hosts to the default group (count: "
							+ hg.addHosts(getHostNames()) + ").");
					
					hasError = true;
				}				
			}
		}
		
		return !hasError;
	}
	
	/**
	 * Get iterator over the set of the host names.
	 * 
	 * @return Iterator over the set of host names.
	 */
	public synchronized Iterator< String > getHostNamesIterator() {
		
		return new HostNameIterator();
	}
	
	/**
	 * Get iterator over the set of group names.
	 * 
	 * @return Iterator over the set of group names.
	 */
	public synchronized Iterator< String > getGroupNamesIterator() {
		
		return new GroupNameIterator();
	}
	
	/**
	 * Update database file with new user-defined properties of given host.
	 * 
	 * @param hostName Name of the host.
	 * @param newProps Object with user-defined properties.
	 * 
	 * @throws HostDatabaseException If there was an error writing data to file.
	 * @throws InvalidArgumentException Host is not in database.
	 */
	public synchronized void updateUserProperties(String hostName, PropertyTreeInterface newProps) 
		throws HostDatabaseException, InvalidArgumentException {
		
		HostInfo host = hostData.get(hostName);
		
		if (host == null) {
			throw new InvalidArgumentException("Host \"" + hostName + "\" not found in database.");
		}
		
		host.setUserPropertiesObject(newProps);
		
		String hostFile = null;
		
		try {
			hostFile = getFullFileName(dbIndex.getHostEntry(hostName).getDataFileName());
		} catch (Exception e) {
			assert false : "Index is out of sync with db.";
		}
		
		try {
			XMLHelper.saveXMLSerializable(host, hostFile, true, "UTF-16");
		} catch (Exception e) {
			logger.logError("Unable to save new host file.", e);
			throw new HostDatabaseException("Unable to save new host file.", e);
		}
	}
	
	/**
	 * Delete file from the database.
	 * 
	 * @param name Name of the file to delete (local name, not full path).
	 * 
	 * @throws FileNotFoundException If file does not exist.
	 */
	private void deleteFile(String name) throws FileNotFoundException, HostDatabaseException {
		
		String fname = getFullFileName(name);
		
		File file = new File(fname);
		
		if (!file.exists()) {
			throw new FileNotFoundException("Unable to find file \"" + name + "\".");
		}
		
		if (!file.delete()) {
			throw new HostDatabaseException("Unable to delete file: \"" + name + "\".");
		}
	}
	
	/**
	 * Get name of the load file of given host.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @return Name of the load file of the given host. Name if local only (not full path).
	 * 
	 * @throws ValueNotFoundException If host is not in database or if entry for given host does
	 *         not exist.
	 */
	private String getLoadFileName(String hostName) throws ValueNotFoundException {

		HostIndexEntry entry = dbIndex.getHostEntry(hostName);
		
		return entry.getLoadFileName();
	}
	
	/**
	 * Get name of the load map file for the given host.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @return Name of the load map file. Only name of the file is returned (not the full path).
	 * 
	 * @throws ValueNotFoundException If host is not in database or if entry for the host does not exist.
	 */
	private String getLoadMapFileName(String hostName) throws ValueNotFoundException {

		HostIndexEntry entry = dbIndex.getHostEntry(hostName);
		
		return entry.getLoadMapFileName();
	}
	
	/**
	 * Get full path of the load file of given host.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @return Name of the load file of the given host.
	 * 
	 * @throws ValueNotFoundException If host is not in database or if entry for given host does
	 *         not exist.
	 */
	public synchronized String getLoadFilePath(String hostName) throws ValueNotFoundException {
		
		return getFullFileName(getLoadFileName(hostName));
	}
	
	/**
	 * Get path of the load map file for the given host.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @return Name of the load map file.
	 * 
	 * @throws ValueNotFoundException If host is not in database or if entry for the host does not exist.
	 */
	public synchronized String getLoadMapFilePath(String hostName) throws ValueNotFoundException {
		
		return getFullFileName(getLoadMapFileName(hostName));
	}
	
	/**
	 * Delete file with load data for given host.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @throws ValueNotFoundException If host is not in database.
	 * @throws HostDatabaseException If an error occurred while deleting file.
	 */
	public synchronized void deleteLoadFiles(String hostName)
		throws ValueNotFoundException, HostDatabaseException {
		
		String fileName = getLoadFileName(hostName);
		String mapFileName = getLoadMapFileName(hostName);
		
		try {
			deleteFile(fileName);
		} catch (Exception e) {
			logger.logError("Unable to delete load file for \"" + hostName + "\".");
			throw new HostDatabaseException("Unable to delete load file for \"" + hostName + "\".", e);
		}
		
		try {
			deleteFile(mapFileName);
		} catch (Exception e) {
			logger.logError("Unable to delete load map file for \"" + hostName + "\".");
			throw new HostDatabaseException("Unable to delete load map file for \"" + hostName + "\".", e);
		}
	}
	
	/**
	 * Create empty files for load and load map.
	 * 
	 * @param hostName Name of the host. Host has to be in the database.
	 * 
	 * @return Pair containing absolute names of the files. 
	 * 
	 * @throws ValueNotFoundException If host is not in database.
	 * @throws HostDatabaseException If one of the files cannot be created.
	 */
	public synchronized Pair< String, String > createEmptyLoadFiles(String hostName) 
		throws ValueNotFoundException, HostDatabaseException {
		
		HostIndexEntry entry = dbIndex.getHostEntry(hostName);
		
		File loadFile = new File(getFullFileName(entry.getLoadFileName()));
		try {
			if (!loadFile.createNewFile()) {
				// file already exists
				new FileWriter(loadFile, false);
			}
		} catch (IOException e) {
			throw new HostDatabaseException("Unable to create load file.", e);
		}
		
		File mapFile = new File(getFullFileName(entry.getLoadMapFileName()));
		try {
			if (!mapFile.createNewFile()) {
				// file already exists
				new FileWriter(mapFile, false);
			}
		} catch (IOException e) {
			throw new HostDatabaseException("Unable to create new load map file.");
		}
		
		return new Pair< String, String >(loadFile.getAbsolutePath(), mapFile.getAbsolutePath());
	}
	
	/**
	 * Get full name of the database file.
	 * 
	 * @param fn Relative name of the file in database.
	 * 
	 * @return Absolute name of the requested file (including path to the database folder).
	 */
	private String getFullFileName(String fn) {
		
		return MiscUtils.concatenatePath(databasePath, fn);
	}
	
	/**
	 * Find occurrences of string in array.
	 * 
	 * @param s String to look for.
	 * @param a Array to look in.
	 * 
	 * @return <code>true</code> if string was found in array, <code>false</code> otherwise. 
	 */
	private static boolean findInArray(String s, String []a) {

		if (a != null) {
			for (int i = 0; i < a.length; ++i) {
				
				if (a[i].equals(s)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Return unique name of the file in the database. For now only entity name is used as file name 
	 * (since entities are hosts, they should be unique and so are groups and history entries). 
	 * In case of collision, index is appended at the end of the name.
	 * 
	 * @param entityName Name of the entity.
	 * @param ext Extension of the file.
	 * 
	 * @return Name of the resulting file. Name returned should not collide with any file already existing
	 *         in directory. Only file name is returned, not full path. 
	 */
	private String getUniqueFileName(String entityName, String ext) {
		
		File dbDirFile = new File(databasePath);
		
		String []fileList = dbDirFile.list(new ExtensionFileFilter(ext));
		
		String result = entityName;

		if (findInArray(result + "." + ext, fileList)) {
			
			int add = 0;
			
			while (findInArray(result + "_" + String.valueOf(add) + "." + ext, fileList)) {
				add += 1;
			}
			
			result = result + "_" + String.valueOf(add);
		}
		
		return result + "." + ext;
	}
	
	/**
	 * For each host in database build list of groups host is member of and set it as its
	 * "memberof" property.
	 * This method is called every time list of groups or list of hosts are modified.
	 */
	private void updateGroupProperty() {
		
		HashMap< String, TreeSet< ValueString > > members = new HashMap< String, TreeSet< ValueString > >();
		
		for (String host : hostData.keySet() ) {
			members.put(host, new TreeSet< ValueString >());
		}
		
		for (HostGroup group : groupData.values()) {
			for (String host : group) {
				members.get(host).add(new ValueString(group.getName()));
			}
		}
		
		for (HostInfo host : hostData.values()) {
			host.setMemberOf(members.get(host.getHostName()));
		}
	}
	
	/**
	 * Build list of software aliases for given host. Note that one alias definition may match more
	 * than one application and one application may match more than one alias definition so this
	 * method runs in Theta(N.M) where N is number of alias definitions and M is number of applications
	 * detected on the host.
	 * 
	 * @param host Host which will receive list of aliases.
	 * 
	 *  @throws HostDatabaseException If there was an error while building alias table.
	 */
	private void buildAliasTable(HostInfo host) throws HostDatabaseException {
		
		ArrayList< SoftwareAlias > alias = new ArrayList< SoftwareAlias >();
		
		// for every product...
		for (int i = 0; i < host.getProductCount(); ++i) {
			Product product = host.getProduct(i);
			String aliasName = null;
			String productName = null;
			String productVersion = null;
			String productVendor = null;
			
			// ...test every alias definition
			for (SoftwareAliasDefinition ad: aliases) {
				if (ad.getOsRestriction() != null) {
					boolean osTest = false;
					
					try {
						osTest = host.getOperatingSystem().test(ad.getOsRestriction(), true);
					} catch (Exception e) {
						// os test failed, skip current alias
						continue;
					}
					
					if (!osTest) {
						// host does not have correct OS, skip this alias
						continue;
					}
				}
				
				boolean match = false;
				
				try {
					match = product.test(ad.getAppRestriction(), false);
				} catch (Exception e) {
					match = false;
				}
				
				if (match) {
					try {
						aliasName = evaluateStringWithPropertyVariables(ad.getAliasName(), product);
						productName = evaluateStringWithPropertyVariables(ad.getResultName(), product);
						productVendor = evaluateStringWithPropertyVariables(ad.getResultVendor(), product);
						productVersion = evaluateStringWithPropertyVariables(ad.getResultVersion(), product);
					} catch (IllegalArgumentException e) {
						logger.logError("Error building alias \"" + ad.getAliasName()
								+ "\", message: " + e.getMessage());
						continue;
					}
					alias.add(new SoftwareAlias(aliasName, productName, productVersion, productVendor));
				}
			}
		}
		
		// now all aliases to the host
		for (SoftwareAlias a: alias) {
			try {
				host.addAlias(a);
			} catch (Exception e) {
				throw new HostDatabaseException("Unable to add alias to the list", e);
			}
		}
	}
	
	/**
	 * Evaluate string which can contain names of properties from <code>PropertyTreeReadInterface</code>.
	 * Variable names are specified inside curly braces with dollar sign, e.g. <i>${version}</i> is
	 * variable for <i>version</i> property. Note that only local properties can be specified.
	 * If you don't want to use "${" or "}" inside the string as variable declaration, you can escape
	 * them with backslash character - that is, use <i>\$</i>, <i>\{</i> or <i>\}</i>.
	 * String can contain any characters. After it is evaluated, variables are substituted with the 
	 * value taken from the given instance of <code>PropertyTreeReadInterface</code>. Note that it
	 * is simple textual substitution, so don't use any math operators ;).
	 * Also note, that this is really simple parser, so it is quite benevolent sometimes (e.g. stray
	 * right curly brace in the middle of the string is not an error).
	 * 
	 * @param s Input string which may contain variables.
	 * @param pt Object which has to provide properties which match given variable names.
	 * @return Original string which has variable declarations replaced with respective values from
	 *         given object.
	 * 
	 * @throws IllegalArgumentException If input string is invalid. Read exception message for
	 *         detailed description.
	 */
	private String evaluateStringWithPropertyVariables(String s, PropertyTreeReadInterface pt) 
		throws IllegalArgumentException {
		
		return substitutor.parseString(s, new PropertyTreeDataProvider(pt));
	}
	
	/**
	 * Data provider which takes data from instance of <tt>PropertyTreeReadInterface</tt>.
	 *
	 * @author Branislav Repcek
	 */
	private static class PropertyTreeDataProvider 
		implements SubstituteVariableValues.VariableValueProviderInterface< String > {

		/**
		 * Data storage.
		 */
		private PropertyTreeReadInterface data;
		
		/**
		 * Construct new data provider with given data.
		 * 
		 * @param data Data storage.
		 */
		public PropertyTreeDataProvider(PropertyTreeReadInterface data) {
			
			this.data = data;
		}
		
		/*
		 * @see cz.cuni.mff.been.common.SubstituteVariableValues.VariableValueProviderInterface#getValue(java.lang.String)
		 */
		public String getValue(String variableName) {
			
			ValueCommonInterface value = null;
			
			try {
				value = data.getPropertyValue(variableName);
			} catch (Exception e) {
				return null;
			}
			
			return value.toString();
		}
	}
	
	/**
	 * Filter files based on their extension.
	 * 
	 * @author Branislav Repcek
	 */
	private static class ExtensionFileFilter implements FilenameFilter {
		
		/**
		 * Extension (including dot character).
		 */
		private String extension;
		
		/**
		 * Create new ExtensionFileFilter class.
		 * 
		 * @param ext Extension this filter will accept. Extension is specified without dot character.
		 */
		public ExtensionFileFilter(String ext) {
			
			extension = "." + ext;
		}
		
		/**
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name) {
			
			return name.endsWith(extension);
		}
	}
	
	/**
	 * Base class for read-only iterators.
	 * 
	 * @param <T> Type of the element over which iterator iterates.
	 *
	 * @author Branislav Repcek
	 */
	private abstract class ReadOnlyIterator< T > implements Iterator< T >, Serializable {
		
		private static final long	serialVersionUID	= 1642173857073462701L;

		/**
		 * Remove current element. This operation is not supported by read-only iterators, therefore 
		 * it will always throw UnsupportedOperationException.
		 */
		public final void remove() {
			
			throw new UnsupportedOperationException("remove is not supported for read-only iterators.");
		}
	}
	
	/**
	 * Read-only iterator class for host names. This is actually only read-only wrapper over
	 * standard string iterator.
	 *
	 * @author Branislav Repcek
	 */
	private class HostNameIterator extends ReadOnlyIterator< String > implements Serializable {
		
		private static final long	serialVersionUID	= 3797903008387882544L;

		/**
		 * Iterator pointing to the current element.
		 */
		private Iterator< String > it;
		
		/**
		 * Create iterator which points to the first element.
		 */
		public HostNameIterator() {
			
			it = hostData.keySet().iterator();
		}

		/**
		 * Get next element from the collection we are iterating over.
		 */
		public String next() {
			
			return it.next();
		}
		
		/**
		 * Test if collection has next element.
		 */
		public boolean hasNext() {
			
			return it.hasNext();
		}
	}
	
	/**
	 * Read-only iterator class for group names. This is actually only read-only wrapper over
	 * standard string iterator.
	 *
	 * @author Branislav Repcek
	 */
	private class GroupNameIterator extends ReadOnlyIterator< String > implements Serializable {
		
		private static final long	serialVersionUID	= -5846782611783144312L;

		/**
		 * Iterator pointing to the current element.
		 */
		private Iterator< String > it;
		
		/**
		 * Create iterator which points to the first element.
		 */
		public GroupNameIterator() {
			
			it = groupData.keySet().iterator();
		}
		
		/**
		 * Get next element from the collection we are iterating over.
		 */
		public String next() {
			
			return it.next();
		}
		
		/**
		 * Test if collection has next element.
		 */
		public boolean hasNext() {
			
			return it.hasNext();
		}
	}
}
