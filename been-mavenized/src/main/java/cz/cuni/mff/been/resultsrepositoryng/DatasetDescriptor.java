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

package cz.cuni.mff.been.resultsrepositoryng;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;


/**
 * Definition of dataset's tag names and their types.
 * Also contains flag, whether dataset
 * is transaction-enabled or trigger enabled.
 * 
 * @author Jan Tattermusch
 *
 */

public class DatasetDescriptor implements Serializable {
	
	private static final long serialVersionUID = 464613488494L;
	
	/**
	 * Represents types of datasets.
	 * Currently two types of datasets are supported:
	 * Trigger enabled datasets (rows have serial 
	 * number growing with time and thus 
	 * are suitable to have triggers on them - later saved
	 * records have bigger serial numbers).
	 * 
	 * Transaction enabled datasets 
	 * are allowed to have transactions on them.
	 * As a result, their row serial numbers that 
	 * are not necessarilly growing with time of saving
	 * and thus it's impossible to use them 
	 * with triggers (there's a risk some 
	 * rows will be not evaluated at all).
	 * 
	 * Default dataset type is TRIGGER_ENABLED.
	 *  
	 * @author Jan Tattermusch
	 *
	 */
	public enum DatasetType {
		TRIGGER_ENABLED,
		TRANSACTION_ENABLED
	}
	
	/** types of tags */
	private TreeMap<String, DataHandle.DataType> tagTypes;
	
	/** set of tags that make tuple's primary key together */
	private TreeSet<String> idTags;
	
	/** type of dataset */
	private DatasetType datasetType = DatasetType.TRIGGER_ENABLED; 
	
	/**
	 * Creates new instance of DatasetDescriptor
	 */
	public DatasetDescriptor() {
		tagTypes = new TreeMap<String,DataHandle.DataType>();
		idTags = new TreeSet<String> ();
	}
	
	/**
	 * Sets a tag of given name to given type
	 * @param tagName tag name
	 * @param type data type
	 * @param isInKey whether tag is part of tuple's primary key
	 */
	public void put(String tagName, DataHandle.DataType type, boolean isInKey) {
		tagTypes.put(tagName, type);
		if (isInKey) {
			idTags.add(tagName);
		}
	}
	
	/**
	 * Gets type of tag with given name
	 * @param tagName requested name
	 * @return associated data type
	 */
	public DataHandle.DataType get(String tagName) {
		return tagTypes.get(tagName);
	}
	
	/**
	 * @return collection of tag names 
	 */
	public Collection<String> tags() {
		return tagTypes.keySet();
	}
	
	/**
	 * 
	 * @return collection of tags in tuple's primary key
	 */
	public Collection<String> idTags() {
		return idTags;
	}
	
	/**
	 * @return collection of tags that are not in tuple's primary key 
	 */
	public Collection<String> dataTags() {
		TreeSet<String> result = new TreeSet<String> (tagTypes.keySet());
		result.removeAll(idTags);
		return result;
	}

	public DatasetType getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(DatasetType datasetType) {
		this.datasetType = datasetType;
	}

}
