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
package cz.cuni.mff.been.resultsrepositoryng.implementation;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;


import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;


/** Results repository entity. It's persisted by Hibernate. */

@Entity
public class RRDataset {
	
    /**
     * Internal identifier of dataset
     */
	@Id
	@SuppressWarnings({ "unused" })
	private UUID id = UUID.randomUUID();
	
	/** Analysis to which dataset belongs to */
	@Column
	private String analysis;
	
	/** Dataset name */
	@Column
	private String name;
	
	
	/* this field will be serialized, basic and column
	 * annotations are mandatory here, because of 
	 * hibernate bug */
	/** Dataset descriptor of dataset */
	@Lob
	@Column(length = 1000000000) 
	private DatasetDescriptor datasetDescriptor;
	
	/**
	 * Creates new instance of RRDataset
	 * @param analysis  analysis name
	 * @param name dataset name
	 * @param descriptor dataset descriptor
	 */
	public RRDataset(String analysis, String name, DatasetDescriptor descriptor) {
		this.analysis = analysis;
		this.name = name;
		this.datasetDescriptor = descriptor;
	}
	
	/**
	 * Creates new instance of RRDataset.
	 * Has to be here because of Hibernate.
	 */
	public RRDataset() {
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	public String getAnalysis() {
		return analysis;
	}

	public DatasetDescriptor getDatasetDescriptor() {
		return datasetDescriptor;
	}

	public void setDatasetDescriptor(DatasetDescriptor datasetDescriptor) {
		this.datasetDescriptor = datasetDescriptor;
	}

}
