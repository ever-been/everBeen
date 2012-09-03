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

import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.common.util.XMLHelper;

import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;


/**
 * Entry in the table of the hosts. Contains names of all files storing data for given host 
 * (including history).
 * 
 * @author Branislav Repcek
 */
class HostIndexEntry implements Serializable, XMLSerializableInterface, ModifiableInterface {

	private static final long	serialVersionUID	= 7380805532657766404L;

	/**
	 * Name of the host.
	 */
	private String hostName;
	
	/**
	 * File with host's data.
	 */
	private String dataFile;
	
	/**
	 * File with load data for host.
	 */
	private String loadFile;
	
	/**
	 * Name of the file with load map.
	 */
	private String loadMapFile;
	
	/**
	 * Date of newest entry (current).
	 */
	private Date date;
	
	/**
	 * List of entries in configuration history of the host. Key is date of the entry, value
	 * is name of the data file. 
	 */
	private HashMap< Date, String > historyEntries;
	
	/**
	 * Cache which stores latest entry in the history (not including current one). If <code>null</code>
	 * it has to be recalculated.
	 */
	private Pair< Date, String > newestEntry;
	
	/**
	 * Was instance modified (added/removed entries)?
	 */
	private boolean modified;
	
	/**
	 * Create new Index Entry. You can't set History Entries with this constructor since it is meant
	 * to create Index Entry for new host (that is, no history has been created yet).
	 * 
	 * @param newHostName Name of the host.
	 * @param newDataFile File with HostInfo data. 
	 * @param newLoadFile File to which LoadInfo for host will be written.
	 * @param newLoadMapFile Name of the file with load map.
	 * @param newDate Date when data file for the host was created.
	 */
	public HostIndexEntry(String newHostName, String newDataFile, String newLoadFile, String newLoadMapFile, Date newDate) {
		
		hostName = newHostName;
		dataFile = newDataFile;
		loadFile = newLoadFile;
		loadMapFile = newLoadMapFile;
		date = newDate;
		historyEntries = new HashMap< Date, String >();
		newestEntry = null;
		modified = true;
	}
	
	/**
	 * Read data from XML file node.
	 * 
	 * @param node Node with entry data.
	 * 
	 * @throws InputParseException Error parsing node data.
	 */
	public HostIndexEntry(Node node) throws InputParseException {

		historyEntries = new HashMap< Date, String >();
		
		parseXMLNode(node);
		
		newestEntry = null;
		modified = false;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		
		/* One node looks like this:
		   
		   <host name="minimal.kolej.mff.cuni.cz" file="minimal.kolej.mff.cuni.cz.host" 
		         load="minimal.kolej.mff.cuni.cz.load" date="2006/03/15 18:25.15"
		         loadmap="minimal.kolej.mff.cuni.cz.loadmap">
			   <history date="2006/03/15 18:25.15" file="minimal.kolej.mff.cuni.cz.2006-03-15-18-25-15.history"/>
		   </host>
		   
		   There can be any number of history sub-nodes. All attributes are mandatory.
		 */
		
		SimpleDateFormat format = new SimpleDateFormat(HostManagerInterface.DEFAULT_DATE_TIME_FORMAT);
		
		hostName = XMLHelper.getAttributeValueByName("name", node);
		dataFile = XMLHelper.getAttributeValueByName("file", node);
		loadFile = XMLHelper.getAttributeValueByName("load", node);
		loadMapFile = XMLHelper.getAttributeValueByName("loadmap", node);
		
		try {
			date = format.parse(XMLHelper.getAttributeValueByName("date", node));
		} catch (ParseException e) {
			throw new InputParseException("Error parsing entry date.", e);
		}
		
		ArrayList< Node > hist = XMLHelper.getChildNodesByName("history", node);
		
		for (Iterator< Node > it = hist.iterator(); it.hasNext(); ) {
			
			Node current = it.next(); 
			Date histDate = null;
			
			try {
				histDate = format.parse(XMLHelper.getAttributeValueByName("date", current));
			} catch (ParseException e) {
				throw new InputParseException(e);
			}
		
			String file = XMLHelper.getAttributeValueByName("file", current);
			
			historyEntries.put(histDate, file);
		}
	}

	/**
	 * Get number of history entries for host's configuration.
	 * 
	 * @return Number of entries in host's history.
	 */
	public int getHistoryLength() {
		
		return historyEntries.size();
	}
	
	/**
	 * Class used to sort dates by the reverse of the natural ordering.
	 *
	 * @author Branislav Repcek
	 */
	private class ReverseComparator implements Comparator< Date > {

		/**
		 * Compare two dates.
		 * 
		 * @param a First date.
		 * @param b Second date.
		 * 
		 * @return Positive number if first date is before second, 0 if they are equal and negative
		 *         number if first date is after the second one.  
		 */
		public int compare(Date a, Date b) {
			
			return -a.compareTo(b);
		}
	}
	
	/**
	 * Class used to sort pairs of dates and strings by the reverse of their natural ordering.
	 *
	 * @author Branislav Repcek
	 */
	private class ReversePairComparator implements Comparator< Pair< Date, String > > {
		
		/**
		 * Compare pair of date and string based on the date. String value is not considered
		 * in comparison.
		 * 
		 * @param a First pair.
		 * @param b Second pair.
		 * 
		 * @return Positive number if first pair's date is before the date from the second pair.
		 *         0 if both dates are equal. Negative number if date from first pair is after the
		 *         date from second pair.
		 */
		public int compare(Pair< Date, String > a, Pair< Date, String > b) {
			
			return -a.getKey().compareTo(b.getKey());
		}
	}
	
	/**
	 * Get list of dates from which history is available.
	 *
	 * @return List containing all entries for current host (including newest entry). 
	 *         List is sorted from most recent date to the oldest. 
	 */
	public List< Date > getHistoryEntryDates() {
		
		ArrayList< Date > result = new ArrayList< Date >();
		
		result.addAll(historyEntries.keySet());
		result.add(date);
		
		Collections.sort(result, new ReverseComparator());
		
		return result;
	}

	/**
	 * Get entry from history for given date.
	 * 
	 * @param entryDate Date of the entry. You can also use this method to query for newest entry (this will
	 *        always be the current one).
	 * 
	 * @return Name of the history file.
	 * 
	 * @throws ValueNotFoundException If entry with specified date was not found.
	 */
	public String getHistoryEntry(Date entryDate) throws ValueNotFoundException {
		
		if (entryDate.equals(date)) {
			return dataFile;
		} else {
			String result = historyEntries.get(entryDate);
			
			if (result == null) {
				throw new ValueNotFoundException("Unable to find history entry for date \""
						+ MiscUtils.formatDate(entryDate, HostManagerInterface.DEFAULT_DATE_TIME_FORMAT)
						+ "\".");
			} else {
				return result;
			}
		}
	}
	
	/**
	 * Get list of all history entries and their corresponding dates.
	 * 
	 * @return List containing pairs of date and file name string (includes newest entry). 
	 */
	public List< Pair< Date, String > > getHistoryEntryList() {
		
		ArrayList< Pair< Date, String > > list = new ArrayList< Pair< Date, String > >();
		
		for (Map.Entry< Date, String > entry: historyEntries.entrySet()) {
			list.add(new Pair< Date, String >(entry.getKey(), entry.getValue()));
		}
		
		list.add(new Pair< Date, String >(date, dataFile));
		
		Collections.sort(list, new ReversePairComparator());
		
		return list;
	}

	/**
	 * Add new entry to the history. This will move current newest entry to the history and set 
	 * specified entry as the newest one.
	 * 
	 * @param entryDate Date of the new entry. This date can't already occur in history and it has 
	 *        to be later date than current newest date.
	 * @param entryFile Data file for the new entry.
	 * 
	 * @throws InvalidArgumentException If specified date is already in the entry or if date is before the
	 *         most recent one.
	 */
	public void addNewEntry(Date entryDate, String entryFile) throws InvalidArgumentException {
		
		if (getNewestEntry() != null) {
			try {
				if (!entryDate.after(getNewestEntry().getKey())) {
					// new date is not after the latest date from the history
					throw new InvalidArgumentException("New date is before the latest "
							+ "date from the history.");
				}
				
				if (hasHistoryEntryForDate(entryDate)) {
					// entry with specified date already exists
					throw new InvalidArgumentException("Entry with specified date is already "
							+ "in the index entry.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidArgumentException(e);
			}
		}
		
		try {
			historyEntries.put(date, dataFile);
			date = entryDate;
			dataFile = entryFile;
			newestEntry = getNewestHistoryEntry();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		modified = true;
	}
	
	/**
	 * Add new entry to the history.
	 * 
	 * @param entryDate Date of the entry.
	 * @param entryFile File with entry data,
	 * 
	 * @throws InvalidArgumentException If entry with specified date is already in the history.
	 */
	public void addHistoryEntry(Date entryDate, String entryFile) throws InvalidArgumentException {
		
		if (historyEntries.containsKey(entryDate)) {
			// History entry with given date is already in the history
			throw new InvalidArgumentException("Entry with specified date is already in the history.");
		} else {
			historyEntries.put(entryDate, entryFile);
			modified = true;
			if ((newestEntry == null) || (entryDate.after(newestEntry.getKey()))) {
				newestEntry = new Pair< Date, String >(entryDate, entryFile);
			}
		}
	}
	
	/**
	 * Remove specific entry from the history.
	 * 
	 * @param entryDate Date of the entry. If date specified is the date of current entry, current 
	 *        entry is removed and history is moved on entry up (latest history entry becomes current).
	 * 
	 * @throws ValueNotFoundException If given entry was not found in history.
	 */
	public void removeHistoryEntry(Date entryDate) throws ValueNotFoundException {

		if (entryDate.equals(date)) {
			Pair< Date, String > newTop = getNewestHistoryEntry();
			
			if (newTop != null) {
				date = newTop.getKey();
				dataFile = newTop.getValue();
			
				historyEntries.remove(newTop.getKey());
			} else {
				date = null;
				dataFile = null;
			}
		} else {
			if (historyEntries.containsKey(entryDate)) {
				historyEntries.remove(entryDate);
				newestEntry = null;
			} else {
				throw new ValueNotFoundException("Unable to remove entry for date \""
						+ MiscUtils.formatDate(entryDate, HostManagerInterface.DEFAULT_DATE_TIME_FORMAT)
						+ "\", entry does not exist.");
			}
		}
		
		modified = true;
	}
	
	/**
	 * Test whether there's a history entry for specified date.
	 * 
	 * @param testDate Date to test.
	 * 
	 * @return <code>true</code> if entry for specified date is in history, <code>false</code> otherwise.
	 */
	public boolean hasHistoryEntryForDate(Date testDate) {
		
		return (historyEntries.containsKey(testDate)) || (date.equals(testDate));
	}
	
	/**
	 * Remove all entries from the host's history. This will not remove history files.
	 * 
	 * @return Array containing names of files to remove.
	 */
	public String[] clearHistory() {
		
		String []res = new String[historyEntries.size()];

		historyEntries.values().toArray(res);
		historyEntries.clear();
		
		newestEntry = null;
		modified = true;
		
		return res;
	}
	
	/**
	 * Get name of the file with load info for the current host.
	 * 
	 * @return Name of the load file.
	 */
	public String getLoadFileName() {
		
		return loadFile;
	}
	
	/**
	 * @return Name of the load map file.
	 */
	public String getLoadMapFileName() {
		
		return loadMapFile;
	}
	
	/**
	 * Get name of the file with data about host collected by the detector.
	 *   
	 * @return Name of the data file.
	 */
	public String getDataFileName() {
		
		return dataFile;
	}
	
	/**
	 * Get name of the host.
	 * 
	 * @return Name of the host.
	 */
	public String getHostName() {
		
		return hostName;
	}
	
	/**
	 * Get date of current entry (that is, newest one).
	 * 
	 * @return Date of current entry.
	 */
	public Date getCurrentEntryDate() {
		
		return date;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	public Element exportAsElement(Document document) {
		
		Element entry = document.createElement("host");
		
		entry.setAttribute("name", hostName);
		entry.setAttribute("file", dataFile);
		entry.setAttribute("load", loadFile);
		entry.setAttribute("loadmap", loadMapFile);
		entry.setAttribute("date", MiscUtils.formatDate(date, HostManagerInterface.DEFAULT_DATE_TIME_FORMAT));
		
		Set< Date > dates = historyEntries.keySet();
		SimpleDateFormat formater = new SimpleDateFormat(HostManagerInterface.DEFAULT_DATE_TIME_FORMAT);
		
		for (Date current: dates) {
			Element historyElement = document.createElement("history");
			
			historyElement.setAttribute("date", formater.format(current));
			historyElement.setAttribute("file", historyEntries.get(current));
			
			entry.appendChild(historyElement);
		}
		
		return entry;
	}
	
	/**
	 * Get entry with the latest date from the history.
	 * 
	 * @return Date and data file name of the latest entry from the history.
	 */
	private Pair< Date, String > getNewestHistoryEntry() {
		
		if (newestEntry == null) {
			// we have to recalculate "cache"
			Pair< Date, String > current = null;
		
			for (Map.Entry< Date, String > tested: historyEntries.entrySet()) {
				if (current == null) {
					current = new Pair< Date, String >(tested.getKey(), tested.getValue());
				} else if (tested.getKey().after(current.getKey())) {
					current = new Pair< Date, String >(tested.getKey(), tested.getValue());
				}
			}
			
			newestEntry = current;
		}
		
		return newestEntry;
	}
	
	/**
	 * Get data about the newest entry.
	 * 
	 * @return Date and data file name of the newest entry.
	 */
	private Pair< Date, String > getNewestEntry() {
		
		Pair< Date, String > ne = getNewestHistoryEntry();
		
		if (ne != null) {
			if (ne.getKey().after(date)) {
				return newestEntry;
			} else {
				return new Pair< Date, String >(date, dataFile);
			}
		} else {
			return null;
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.ModifiableInterface#isModified()
	 */
	public boolean isModified() {
		
		return modified;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.ModifiableInterface#isModified(boolean)
	 */
	public boolean isModified(boolean reset) {
		
		boolean result = modified;
		
		if (reset) {
			modified = false;
		}
		
		return result;
	}
	
	/**
	 * This method will reset modification flag. You should call this after you have saved data in this 
	 * class to the file.
	 */
	public void saved() {
		
		modified = false;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = hostName + "={";
		
		List< Pair< Date, String > > list = getHistoryEntryList();
		
		for (Iterator< Pair< Date, String > > it = list.iterator(); it.hasNext(); ) {
			result += it.next().getValue() + (it.hasNext() ? ", " : "");
		}
		
		return result + "}";
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		
		return "host";
	}
}
