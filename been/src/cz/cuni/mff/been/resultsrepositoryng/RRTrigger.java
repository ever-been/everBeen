/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiří Täuber
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
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;


/**
 * Container for Results Repository trigger information. 
 * Can be persisted as a Hibernate entity.
 */
@Entity
public class RRTrigger implements Serializable {

	private static final long serialVersionUID = -5152903455534185789L;

	/** Trigger's identifier */
	@Id 
	private UUID id = UUID.randomUUID(); 
	
	/** Name of the analysis to which the trigger's dataset belongs */
	@Column
	private String analysis;
	
	/** Name of the dataset on which the trigger is hooked */
	@Column
	//@ManyToOne(cascade = { CascadeType.REMOVE }, targetEntity = RRDataset.class)
	private String dataset;
	
	/** Name of the evaluator that placed the trigger */
	@Column
	private String evaluator;
	
	/** Simple trigger condition. References to dataset name might cause undefined behavior */
	@Lob
	@Column(length = 1000000) 
	@Basic(fetch=FetchType.LAZY)
	private Condition condition;
	
	/** RR will schedule this task if condition is true */
	@Lob
	@Column(length = 1000000)
	@Basic(fetch=FetchType.LAZY)
	private TaskDescriptor triggeredTask;
	
	/** Last tuple serial number that have been processed by evaluator
	 * of this trigger */
	@Column
	private long lastProcessedSerial = 0;  
	
	/**
	 * Default constructor.
	 * Needed for hibernate.
	 */
	public RRTrigger() {
		
	}
	
	/**
	 * @param analysis
	 * @param dataset
	 * @param evaluator
	 * @param condition
	 * @param triggeredTask
	 */
	public RRTrigger(
		String analysis,
		String dataset,
		String evaluator,
		Condition condition,
		TaskDescriptor triggeredTask
	) {
		this.analysis = analysis;
		this.dataset = dataset;
		this.evaluator = evaluator;
		this.condition = condition;
		this.triggeredTask = triggeredTask;
	}

	/**
	 * @return identifier
	 */
	public UUID getId() {
		return id;
	}
	/**
	 * 
	 * @return analysis name
	 */
	public String getAnalysis() {
		return analysis;
	}
	
	/**
	 * @return the dataset name
	 */
	public String getDataset() {
		return dataset;
	}

	/**
	 * @return the evaluator
	 */
	public String getEvaluator() {
		return evaluator;
	}

	/**
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @return the triggeredTask
	 */
	public TaskDescriptor getTriggeredTask() {
		return triggeredTask;
	}

	public long getLastProcessedSerial() {
		return lastProcessedSerial;
	}
	
	public void setLastProcessedSerial(long lastProcessedSerial) {
		this.lastProcessedSerial = lastProcessedSerial;
	}
}