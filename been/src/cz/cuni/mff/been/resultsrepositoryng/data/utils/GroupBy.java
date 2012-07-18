/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.resultsrepositoryng.data.utils;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections15.multimap.MultiHashMap;

import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Implementation of Group By operation on Results Repository 
 * record collection.
 * @author Jan Tattermusch
 *
 */
public class GroupBy {
	
    /**
     * data to group
     */
	private Collection<DataHandleTuple> data;

	/**
	 * found groups
	 */
	private MultiHashMap<DataHandleTuple, DataHandleTuple> groups;
	
	/**
	 * Creates new grouping
	 * @param data data to group
	 * @param groupByTags grouping key (list of tags in group by clause)
	 */
	public GroupBy(Collection<DataHandleTuple> data, String[] groupByTags) {
		this.data = data;
		
		groupBy(groupByTags);
	}
	
	/**
	 * Performs the grouping.
	 * @param groupByTags grouping key
	 */
	private void groupBy(String[] groupByTags) {
		groups = new MultiHashMap<DataHandleTuple, DataHandleTuple>();
		
		for (DataHandleTuple tuple : data) {
			DataHandleTuple key = createKey(tuple, groupByTags);
			groups.put(key, tuple);
		}
	}
	
	/**
	 * Retrieves set of group keys found.
	 * @return set of group keys;
	 */
	public Set<DataHandleTuple> groups() {
		return groups.keySet();
	}
	
	/**
	 * Gets collection of records in group with given group key
	 * @param groupKey group key
	 * @return records in given group
	 */
	public Collection<DataHandleTuple> getGroup(DataHandleTuple groupKey) {
		return groups.get(groupKey);
	}
	
	/**
	 * Creates grouping key
	 * @param tuple    tuple to create key from
	 * @param tags     tags that grouping key consists of 
	 * @return  grouping key
	 */
	private DataHandleTuple createKey(DataHandleTuple tuple, String[] tags) {
		DataHandleTuple keyTuple = new DataHandleTuple();
		for (String tag : tags) {
			keyTuple.set(tag, tuple.get(tag));
		}
		return keyTuple;
	}

}
