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

package cz.cuni.mff.been.hostmanager;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.been.common.id.OID;
import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.PropertyDescription;
import cz.cuni.mff.been.hostmanager.database.PropertyDescriptionTable;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;
import cz.cuni.mff.been.hostmanager.database.SimpleHostInfo;
import cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition;
import cz.cuni.mff.been.hostmanager.load.LoadServerInterface;

import static cz.cuni.mff.been.services.Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN;

/**
 * Basic interface for Host Manager. It allows adding/removing/querying hosts. It provides methods
 * that work with Software Aliases, User properties, etc.
 * 
 * @author Branislav Repcek
 */
public interface HostManagerInterface extends Remote {	
	
	/**
	 * RMI path to the main interface of the Host Manager.
	 */
	final String URL = "/been/hostmanager/" + HOST_MANAGER_REMOTE_INTERFACE_MAIN;
	
	/** Name prefix of the detector tasks. */
	final String DETECTOR_PREFIX = "detectortask-";
	
	/**
	 * Default format string for date formatting.
	 */
	final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
	
	/**
	 * Default format string for time formatting.
	 */
	final String DEFAULT_TIME_FORMAT = "HH:mm.ss";
	
	/**
	 * Default format string for date and time formatting used through Host Manager.
	 */
	final String DEFAULT_DATE_TIME_FORMAT = DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT;

	/**
	 * Adds host to database. It will look on the network for the host and perform all tests required to
	 * create database entry. 
	 * This method works asynchronously - it will return immediately. To check result of the operation
	 * call {@link #getOperationStatus} or {@link #removeOperationStatus} with handle returned. 
	 * 
	 * @param hostName Name of the host to add to the database.
	 * 
	 * @return Handle which can be later used to get information about status of the operation.
	 * 
	 * @throws UnknownHostException If host was not found on network.
	 * @throws RemoteException If RMI error occurred.
	 */
	OperationHandle addHost(String hostName) throws UnknownHostException, RemoteException;

	/**
	 * Removes host from database.
	 * 
	 * @param hostName Name of host to remove from the database.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If specified host is not in database.
	 * @throws HostDatabaseException If an error occurred while working with database.
	 */
	void removeHost(String hostName) 
		throws RemoteException, ValueNotFoundException, HostDatabaseException;
	
	/**
	 * Refresh information about host. Host must be in database before calling this method.
	 * This method works asynchronously - it will return immediately. To determine status of the operation
	 * use {@link #getOperationStatus} or {@link #removeOperationStatus} methods.
	 *  
	 * @param hostName Name of host.
	 * 
	 * @return Handle to the operation. 
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws UnknownHostException If specified host was not found on network.
	 * @throws ValueNotFoundException If specified host is not in the database.
	 */
	OperationHandle refreshHost(String hostName) 
		throws RemoteException, UnknownHostException, ValueNotFoundException;
	
	/**
	 * Refresh all host in database. This will go through all entries in database and call
	 * {@link #refreshHost} on given host. To query status of refresh of hosts in database
	 * use handles returned.
	 * 
	 * @return Associative map which maps operation handle to the host name (that is, key is host name, 
	 *         value is operation handle). You can use returned handles to query status of each refresh.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	Map< String, OperationHandle > refreshAll() throws RemoteException;

	/**
	 * Get status of given operation by its handle. Host Manager will keep status of each operation
	 * it performs until status is removed by the user with removeOperationStatusInfo method.
	 * 
	 * @param handle Handle to operation. This handle is returned by the method which performs given
	 *        operation.
	 * @return Instance of HostOperationStatus class containing data about requested operation.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalArgumentException If operation with requested handle has not been found in cache.
	 */
	HostOperationStatus getOperationStatus(OperationHandle handle) 
		throws RemoteException, IllegalArgumentException;

	/**
	 * Remove info about status of given operation from the cache. This method is preferred to
	 * {@link #getOperationStatus} because it will keep cache clean and therefore subsequent 
	 * lookups will be faster.
	 * 
	 * @param handle Handle to the operation returned by method that performs operation.
	 * @return Status info about requested operation.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalArgumentException If operation with requested handle has not been found in chache.
	 */
	HostOperationStatus removeOperationStatus(OperationHandle handle)
	throws RemoteException, IllegalArgumentException;

	/**
	 * Test whether given host is in database.
	 * 
	 * @param hostName Name of host.
	 * @return true when given host is in database, false otherwise.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws UnknownHostException If host has not been found in the network.
	 */
	boolean isHostInDatabase(String hostName) throws RemoteException, UnknownHostException;

	/**
	 * Get info about host with given name. Host must be in database before calling this method.
	 * 
	 * @param hostName Name of host to get info about.
	 * @return Instance <code>HostInfoInterface</code> with all data available for requested host. 
	 * 
	 * @throws RemoteException if RMI error occurred.
	 * @throws ValueNotFoundException If requested host was not found in database.
	 */
	HostInfoInterface getHostInfo(String hostName) throws RemoteException, ValueNotFoundException;
	
	/**
	 * Gets all host info instances atomically, so that they can't be removed on the fly
	 * between reading the list of names and querying a name.
	 * Added by Andrej Podzimek
	 * 
	 * @return An array of HostInfoInterface, a snapshot of the database of hosts.
	 * @throws RemoteException When it rains.
	 */
	HostInfoInterface[] getHostInfos() throws RemoteException;
		
	/**
	 * Get brief info about host. Host must be in database before calling this function.
	 * 
	 * @param name name of host to get info about.
	 * @return {@link SimpleHostInfo} structure with data about host.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If requested host was not found in database.
	 */
	SimpleHostInfo getSimpleHostInfo(String name) throws RemoteException, ValueNotFoundException;
    
	/**
	 * Gets number of hosts in database.
	 * 
	 * @return Number of hosts in database.
	 * 
	 * @throws RemoteException if RMI error occurred.
	 */
	int getHostCount() throws RemoteException;
	
	/**
	 * Get list of all dates for which there's history entry in database for specific host.
	 * 
	 * @param name Name of the host.
	 * 
	 * @return Array containing dates of all history entries for specified host. If no history entries
	 *         are stored in database for specified host, zero-length array is returned.
	 * 
	 * @throws RemoteException RMI error occurred.
	 * @throws ValueNotFoundException if specified host was not found in database.
	 */
	Date[] getHostHistoryDates(String name) throws RemoteException, ValueNotFoundException;
	
	/**
	 * Get data about host's configuration in given time. This can be used to browse history of
	 * hardware and software changes of given host over time.
	 * 
	 * @param hostName Name of the host.
	 * @param entryDate Date of check.
	 * 
	 * @return Data about host's configuration in given time.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException if host was not found in database or if there is no entry with
	 *         date specified.
	 * @throws HostDatabaseException If there was an error while reading data from database.
	 */
	HostInfoInterface getHostHistoryEntry(String hostName, Date entryDate) 
		throws RemoteException, ValueNotFoundException, HostDatabaseException;
	
	/**
	 * Remove specified entry from host's history.
	 * 
	 * @param hostName Name of the host.
	 * @param entryDate Date of the entry to remove from history.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If host was not found in database or entry with specified date is
	 *         not in host's history. 
	 * @throws HostManagerException If other error occurred (eg. database error). 
	 * @throws HostDatabaseException If there was an error removing files from database.
	 */
	void removeHostHistoryEntry(String hostName, Date entryDate) 
		throws RemoteException, ValueNotFoundException, HostManagerException, HostDatabaseException;
	
	/**
	 * Get number of groups in group database. This should always return at least 1.
	 * 
	 * @return Number of groups.
	 * 
	 * @throws RemoteException if RMI error occurred.
	 */
	int getGroupCount() throws RemoteException;
	
	/**
	 * Test whether group with given name is already in database.
	 * 
	 * @param name Name of the group to test.
	 * @return <code>true</code> if group already exists, <code>false</code> otherwise.
	 * 
	 * @throws RemoteException RMI error.
	 */
	boolean isGroup(String name) throws RemoteException;
	
	/**
	 * Get group with given name.
	 * 
	 * @param name Name of group to retrieve.
	 * @return Data about requested group.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If requested group was not found in database.
	 */
	HostGroup getGroup(String name) throws RemoteException, ValueNotFoundException;
	
	/**
	 * A more convenient and atomic way of getting group descriptors.
	 * Added by Andrej Podzimek.
	 * 
	 * @return An array of group descriptors.
	 * @throws RemoteException When it rains.
	 */
	HostGroup[] getGroups() throws RemoteException;
	
	/**
	 * Add/replace group in group database. Group is automatically serialised to XML file.
	 * 
	 * @param newGroup Group to add.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostDatabaseException If there was an error writing new files to the database.
	 * @throws InvalidArgumentException If group you want to add is already in database.
	 */
	void addGroup(HostGroup newGroup) 
		throws RemoteException, HostDatabaseException, InvalidArgumentException;
	
	/**
	 * Create group which satisfies given criteria.
	 * 
	 * @param conditions Array of conditions hosts must satisfy to be included in group. 
	 *        For more information about syntax of conditions see 
	 *        {@link cz.cuni.mff.been.hostmanager.database.ObjectRestriction},
	 *        {@link cz.cuni.mff.been.hostmanager.database.AlternativeRestriction}
	 *        and {@link cz.cuni.mff.been.hostmanager.database.RSLRestriction} classes.
	 * @param groupName name of group. This name should be unique - there can't be two groups with
	 *        same name in database. However, this function does not test whether group with 
	 *        given name already exists in database (since it does not add group to the database).
	 *
	 * @return {@link cz.cuni.mff.been.hostmanager.database.HostGroup} which contains all hosts 
	 *         matching given criteria. Note that group is <u>not</u> added to the database.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If invalid property has been specified in conditions.
	 * @throws ValueTypeIncorrectException If value type in condition is not compatible with type 
	 *         of the property in the condition.
	 * @throws HostManagerException Incorrect group name (empty or <code>null</code> string).
	 * 
	 * @see cz.cuni.mff.been.hostmanager.database.ObjectRestriction
	 * @see cz.cuni.mff.been.hostmanager.database.AlternativeRestriction
	 */
	HostGroup createGroup(RestrictionInterface []conditions, String groupName) 
		throws RemoteException, ValueNotFoundException, ValueTypeIncorrectException, HostManagerException;
	
	/**
	 * Create group of hosts which match with function provided by user in 
	 * {@link HostQueryCallbackInterface}.
	 * 
	 * @param hq Interface with match function.
	 * @param groupName Name of group to create.
	 * 
	 * @return New group with hosts matching given function. Group is not added to the database.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException Invalid group name (<code>null</code> or empty string).
	 * @throws Exception Other error (may be thrown by the callback).
	 */
	HostGroup createGroup(HostQueryCallbackInterface hq, String groupName)
		throws RemoteException, HostManagerException, Exception;
	
	/**
	 * Remove group from database.
	 * 
	 * @param name Name of group to remove.
	 * @return The removed host group instance.
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostDatabaseException If an error occurred when working with database.
	 * @throws ValueNotFoundException If specified group is not in database.
	 */
	HostGroup removeGroup(String name) 
		throws RemoteException, HostDatabaseException, ValueNotFoundException;
	
	/**
	 * Update group in the database. This method should be used whenever group is modified outside the
	 * Host Manager to synchronise database. For default groups all modifications except the change of
	 * metadata or description are forbidden. To change name of the group use renameGroup method.
	 * 
	 * @param group Group to be updated. This group must already exist in database.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException Other error (for example IO error...).
	 * @throws ValueNotFoundException If group which has to be updated was not found in database.
	 * @throws InvalidArgumentException If given group is not valid.
	 */
	void updateGroup(HostGroup group) 
		throws RemoteException, HostManagerException, ValueNotFoundException, InvalidArgumentException;
	
	/**
	 * Rename existing group. You can't rename default group.
	 * 
	 * @param oldName Name of the group to rename. Group with specified name has to be in database.
	 * @param newName New name of the group. This name may be the same as the old one, but it can't be
	 *        name of the another group in the database.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostDatabaseException If an error occurred while working with the database.
	 * @throws ValueNotFoundException Group with specified name does not exist in database.
	 * @throws InvalidArgumentException If you try to rename default group or group with same name 
	 *         as new name already exists in database.
	 */
	void renameGroup(String oldName, String newName) 
		throws RemoteException, HostDatabaseException, ValueNotFoundException, InvalidArgumentException;
	
	/**
	 * Query host database with user specified function and create list of objects implementing
	 * {@link cz.cuni.mff.been.hostmanager.database.HostInfoInterface} that satisfy criteria
	 * given in the query.
	 * 
	 * @param hq Object which provides user testing function.
	 * @return List of hosts which have passed user test.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws Exception If other error occurred.
	 * 
	 * @see cz.cuni.mff.been.hostmanager.HostQueryCallbackInterface
	 */
	HostInfoInterface[] queryHosts(HostQueryCallbackInterface hq) throws RemoteException, Exception;
	
	/**
	 * Create list of hosts matching given criteria.
	 * 
	 * @param nvp Criteria.
	 * @return List of hosts matching given criteria.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If unsupported property name was found in the list of properties.
	 * @throws ValueTypeIncorrectException If incorrect type of property value was in the list of 
	 *         restrictions.
	 * @throws HostManagerException If other error occurred.
	 * 
	 * @see cz.cuni.mff.been.hostmanager.database.ObjectRestriction
	 * @see cz.cuni.mff.been.hostmanager.database.AlternativeRestriction
	 */
	HostInfoInterface[] queryHosts(RestrictionInterface []nvp)
		throws RemoteException, ValueNotFoundException, ValueTypeIncorrectException, HostManagerException;
	
	/**
	 * Get list of all descriptions of properties and objects in the database.
	 * 
	 * @return List containing all available descriptions.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	List< PropertyDescription > getPropertyDescriptionsList() throws RemoteException;
	
	/**
	 * Get description of the specified object or property.
	 * 
	 * @param path Full path to the object or property.
	 * 
	 * @return Details about given object or property.
	 * 
	 * @throws ValueNotFoundException If no description for given object or property has been found.
	 * @throws InvalidArgumentException If property path is invalid.
	 * @throws RemoteException If RMI error occurred.
	 */
	PropertyDescription getPropertyDescription(String path)
		throws ValueNotFoundException, InvalidArgumentException, RemoteException;
	
	/**
	 * Get class containing all property descriptions. This may be useful when a lot of queries for
	 * descriptions are needed since RMI calls can be slow.
	 * 
	 * @return Table containing all property descriptions.
	 * 
	 * @throws RemoteException If RMI error occured.
	 */
	PropertyDescriptionTable getPropertyDescriptionTable() throws RemoteException;
	
	/**
	 * Update user-defined properties of the host in the database.
	 * 
	 * @param host Host data containing modified user-defined properties.
	 * 
	 * @throws InvalidArgumentException If host is not found in database.
	 * @throws HostDatabaseException There was an error while updating database.
	 * @throws RemoteException If RMI error occurred.
	 */
	void updateUserProperties(HostInfoInterface host) 
		throws InvalidArgumentException, HostDatabaseException, RemoteException;
	
	/**
	 * Get names of all hosts for which database entry exists.
	 * 
	 * @return Array containing host names.
	 * 
	 * @throws RemoteException RMI error occurred.
	 */
	String[] getHostNames() throws RemoteException;
	
	/**
	 * Get names of all groups in Host Manager's database.
	 * 
	 * @return Array containing names of groups defined in system.
	 * 
	 * @throws RemoteException RMI error occurred.
	 */
	String[] getGroupNames() throws RemoteException;
	
	/**
	 * Get number of alias definitions currently in database.
	 * 
	 * @return Total number of alias definitions.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	int getAliasDefinitionCount() throws RemoteException;
	
	/**
	 * Get alias definition with given index.
	 * 
	 * @param i Index of the alias definition.
	 * 
	 * @return Alias definition with given index.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If index is invalid.
	 */
	SoftwareAliasDefinition getAliasDefinition(int i) throws RemoteException, ValueNotFoundException;
	
	/**
	 * Added by Andrej Podzimek as a temporary fix to issue 314406.
	 * 
	 * @param name Name of the software alias definition to retrieve.
	 * @return The database entry indexed by the name.
	 * @throws RemoteException When it rains.
	 * @throws ValueNotFoundException When it feels like that.
	 */
	SoftwareAliasDefinition getAliasDefinitionByName( String name )
	throws RemoteException, ValueNotFoundException;
	
	/**
	 * Added by Andrej Podzimek as a temporary fix to issue 314406.
	 * 
	 * @return An array of all alias definitions.
	 * @throws RemoteException When it rains.
	 */
	SoftwareAliasDefinition[] getAliasDefinitions() throws RemoteException;
	
	/**
	 * Remove alias definition with given index.
	 * 
	 * @param i Index of alias definition to remove.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If index is invalid.
	 * @throws HostDatabaseException If an error occurred while updating database.
	 */
	void removeAliasDefinition(int i) 
		throws RemoteException, ValueNotFoundException, HostDatabaseException;
	
	/**
	 * Added by Andrej Podzimek as a temporary fix to issue 314406.
	 * 
	 * @param name Name of the software alias definition to remove.
	 * @return The removed software alias definition instance.
	 * @throws RemoteException When it's foggy.
	 * @throws ValueNotFoundException When it rains
	 * @throws HostDatabaseException When it feels like that.
	 */
	SoftwareAliasDefinition removeAliasDefinitionByName( String name ) throws
	RemoteException, ValueNotFoundException, HostDatabaseException;
	
	/**
	 * Remove all alias definitions.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostDatabaseException If an error occurred while updating database.
	 */
	void removeAllAliasDefinitions() throws RemoteException, HostDatabaseException;
	
	/**
	 * Add one alias definition to the list of definitions. Note that multiple definitions with same
	 * properties are allowed, but it is discouraged to add one alias multiple times since it slows 
	 * down alias parsing and it also created ambiguous entries in the alias list of the host.
	 * This method automatically updates database files, so it may be slow to add more than one alias
	 * sequentially. To add multiple aliases at once use {@link #addAliasDefinitionList} method.
	 *  
	 * @param alias Alias definition to add to the definition list.
	 * 
	 * @return Index of the alias just added.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostDatabaseException If an error occurred while updating database.
	 */
	int addAliasDefinition(SoftwareAliasDefinition alias) 
		throws RemoteException, HostDatabaseException;
	
	/**
	 * Add multiple alias definitions to the list of alias definitions in the database. Note that 
	 * multiple occurrences of one alias are allowed and are not removed. Also note that it is 
	 * discouraged to have multiple alias definitions with same properties.
	 * 
	 * @param aliases List of aliases to add to the database.
	 * 
	 * @throws RemoteException f RMI error occurred.
	 * @throws HostDatabaseException If an error occurred while updating database.
	 */
	void addAliasDefinitionList(Iterable< SoftwareAliasDefinition > aliases)
		throws RemoteException, HostDatabaseException;
	
	/**
	 * Rebuild list of aliases for every host in database. You should call this method after you
	 * have made some modifications to the alias definitions. Note that this method will process
	 * every host in database and will need to parse all alias definitions for each of them.
	 * Therefore is has complexity O(H*D*max(A<sub>h</sub>)) where H is number of hosts, D is number
	 * of alias definitions and A<sub>h</sub> is number of application detected on host <i>h</i>.
	 * Therefore this update may take several seconds for big databases so you should call this
	 * method only when necessary.<br>
	 * Note that update stops on first error, so it may leave some hosts with old alias lists.
	 * However, database should not be corrupted.
	 * This will not modify history entries for any host, only current entries are processed.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostDatabaseException If an error occurred while updating database.
	 */
	void rebuildAliasTableForAllHosts() throws RemoteException, HostDatabaseException;
	
	/**
	 * Retrieve object which contains configuration of the Host Manager and Load Server.
	 * This object can be used to query or change configuration of the HM and LS.
	 * 
	 * @return Configuration manager for the Host Manager and Load Monitor.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	HostManagerOptionsInterface getConfiguration() throws RemoteException;
	
	/**
	 * Register new listener that will be called when events are raised in the Host Manager. Note
	 * that order in which listeners are called is not guaranteed to be the same as they were added.
	 * 
	 * @param listener Listener to register.
	 * 
	 * @return Id assigned to the listener by the Host Manager.
	 * 
	 * @throws InvalidArgumentException If listener is <tt>null</tt>.
	 * @throws RemoteException If RMI error occurred.
	 */
	OID registerEventListener(HostManagerEventListener listener)
		throws InvalidArgumentException, RemoteException;
	
	/**
	 * Unregister event listener previously registered via
	 * {@link #registerEventListener(HostManagerEventListener)} method.
	 * 
	 * @param listenerId Id of the listener to remove.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If id is <tt>null</tt>.
	 * @throws ValueNotFoundException If no listener with given id exists.
	 */
	void unregisterEventListener(OID listenerId)
		throws RemoteException, InvalidArgumentException, ValueNotFoundException;

	/**
	 * RMI reference to the Load Server running in this Host Manager.
	 * 
	 * @return RMI reference to the Load Server running in this Host Manager.
	 * @throws RemoteException If RMI error occurred.
	 */
	LoadServerInterface getLoadServer() throws RemoteException;
}
