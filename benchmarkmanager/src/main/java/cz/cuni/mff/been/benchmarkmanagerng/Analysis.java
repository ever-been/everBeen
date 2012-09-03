/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
package cz.cuni.mff.been.benchmarkmanagerng;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;

import cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator;
import cz.cuni.mff.been.common.id.AID;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;


/**
 * Container class for Benchmark analysis. This class is persisted by Hibernate.
 * 
 * @author Jiri Tauber
 */
@Entity
@AccessType("field")
public class Analysis implements Serializable{
	@Transient
	private static final long serialVersionUID = 2463077040771609159L;

	/** The scale of the analysis runPeriod property. One minute now */
	@Transient
	public static final int RUN_PERIOD_SCALE = 60000;

	/** Analysis name regular expression */
	@Transient
	public static final String REGEX_NAME = "^[a-zA-Z0-9_]+$";

	/** The default RSL condition for hosts - any host */
	@Transient
	public static final Condition DEFAULT_HOST_RSL;
	
	static {
		try {
			DEFAULT_HOST_RSL = ParserWrapper.parseString("name =~ /.*/");
		} catch (ParseException exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	/** Field filled by Benchmark Manager, cached value of analysis state */
	@Transient
	private AnalysisState state = AnalysisState.UNKNOWN;


	/** Hibernate requires ID value, analysis is identified by it in update
	 * method and AID is generated from this value. 
	 */
	@Id @GeneratedValue
	private Integer id = null;

	/** The analysis name - Required field */
	@Column(unique=true, nullable = false)
	private String name;

	/** The analysis description - optional field */
	@Column(length=1024 )
	private String description;

	/** Link to the results page - optional */
	@Column(length=1024 )
	private String resultsLink; 

	/** Single generator pluggable module with its' configuration */
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private BMGenerator generator;

	/** List of evaluator pluggable modules with their configuration */
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private Collection<BMEvaluator> evaluators = new LinkedList<BMEvaluator>();

	/** Counter of successful generator runs */
	@Column(nullable=false)
	private Integer runCount = 0;

	/** Date and time of last successfull generator run */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastTime = null;

	/** Says how often should this analysis be ran in seconds */
	private Integer runPeriod = null;

	/** Which hosts should be allowed to run generator runner and context monitor */
	@Lob @Column(length=4096)
	private Condition generatorHostRSL = DEFAULT_HOST_RSL;  // any host

	//-----------------------------------------------------------------------//

	public Analysis(){}

	public Analysis(String name, String description, BMGenerator generator) throws AnalysisException{
		if( !name.matches(REGEX_NAME) ){
			throw new AnalysisException("Invalid analysis name '"+name+"'");
		}
		this.name = name;
		this.description = description;
		this.generator = generator;
	}

	/** Copy constructor used to unbox any hibernate-specific wrappers */
	public Analysis(Object o){
		if( !(o instanceof Analysis) ){
			throw new ClassCastException("Trying to create Analysis from different object");
		}

		this.id = ((Analysis)o).getID();
		this.name = ((Analysis)o).getName();
		this.description = ((Analysis)o).getDescription();
		this.resultsLink = ((Analysis)o).getResultsLink();
		this.runCount = ((Analysis)o).getRunCount();
		this.runPeriod = ((Analysis)o).getRunPeriod();
		this.lastTime = ((Analysis)o).getLastTime();
		this.generatorHostRSL = ((Analysis)o).generatorHostRSL;

		this.generator = ((Analysis)o).getGenerator();
		for( BMEvaluator evaluator : ((Analysis)o).getEvaluators() ){
			this.addEvaluator(evaluator);
		}
	}

	//----- Getters and Setters ---------------------------------------------//
	public Integer getID(){
		return id;
	}
	void setID(int id){
		this.id = id;
	}

	AID getAID(){
		return new AID(id, name);
	}

	public String getName() {
		return name;
	}
	
	public String getEvaluatorContext(){
		return name+"-evaluators";
	}

	public BMGenerator getGenerator() {
		return generator;
	}
	public void setGenerator(BMGenerator generator){
		this.generator = generator;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getResultsLink() {
		return resultsLink;
	}

	public void setResultsLink(String resultsLink) {
		this.resultsLink = resultsLink;
	}

	public Collection<BMEvaluator> getEvaluators() {
		return evaluators;
	}
	public void addEvaluator(BMEvaluator evaluator) {
		this.evaluators.add(evaluator);
	}
	public void removeEvaluator(BMEvaluator evaluator) {
		this.evaluators.remove(evaluator);
	}
	public void removeEvaluators() {
		this.evaluators.clear();
	}

	public Integer getRunCount(){
		return runCount;
	}
	public void increaseRunCount(){
		runCount++;
	}

	/** @return the last time analysis generator was scheduled */
	public Date getLastTime() {
		return lastTime;
	}
	public void setLastTime(Date lastRun) {
		this.lastTime = lastRun;
	}

	/** @return the run period in minutes. Null value indicates no automatic scheduling. */
	public Integer getRunPeriod() {
		return runPeriod;
	}

	/** Sets the run period in minutes. Null value indicates no automatic scheduling. */
	public void setRunPeriod(Integer runPeriod) {
		this.runPeriod = runPeriod;
	}

	/**
	 * @param generatorHostRSL the generatorHostRSL to set
	 */
	public void setGeneratorHostRSL(Condition generatorHostRSL) {
		this.generatorHostRSL = generatorHostRSL;
	}

	/**
	 * @return the generatorHostRSL
	 */
	public Condition getGeneratorHostRSL() {
		return generatorHostRSL;
	}

	/**
	 * @return the state
	 */
	public AnalysisState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(AnalysisState state) {
		this.state = state;
	}

	/**
	 * Determines whether this analysis should have ran before now.
	 * If {@code runPeriod} is {@code null} (i.e. sheduling is disabled)
	 * then the result is always {@code false}.<br>
	 * <b>Warning:</b> This function works with system time which might differ on each machine!
	 * 
	 * @return <b>false</b> when {@code runPeriod == null}<br>
	 *         otherwise value of {@code lastTime + (runPeriod * RUN_PERIOD_SCALE) < now()}<br>
	 */
	public boolean shouldBeScheduled(){
		if( runPeriod == null ){
			// no automatic scheduling
			return false;
		}
		if( lastTime == null ){
			// never ran before - certainly should be scheduled
			return true;
		}

		Date runAt = new Date();
		runAt.setTime(lastTime.getTime()+runPeriod*RUN_PERIOD_SCALE);
		if( runAt.before( new Date() ) ){
			// should have ran before now
			return true;
		} else {
			// not enough time passed since last run
			return false;
		}
	}


	/**
	 * Checks if the analysis contains all the required information.
	 * It's simple way of checking analysis integrity if you don't
	 * have pluggable module manager available or you don't want all
	 * the pluggable modules loaded.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.Analysis#validate(PluggableModuleManager) validate(PluggableModuleManager)
	 * @return <code>true</code> if module is complete, <code>false</code> otherwise
	 */
	public boolean isComplete(){
		if( name == null || name.length() == 0 ) return false;
		if( generatorHostRSL == null ) return false;
		// TODO probably needs RSL validation 

		if( generator == null ) return false;
		if( !generator.isComplete() ) return false;
		for (BMEvaluator ev : evaluators) {
			if( !ev.isComplete() ) return false;
		}
		return true;
	}

	/**
	 * Checks analysis for correct information and calls <code>validate()</code>
	 * on all the modules refferenced by the analysis.
	 * This method <b>loads all</b> pluggable modules refferenced in the Analysis, use
	 * <code>isComplete()</code> if you want to avoid that.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.Analysis#isComplete() isComplete()
	 * @param manager pluggable module manager to load the modules
	 * @return Collection of errors found in the analysis
	 */
	public Collection<String> validate(PluggableModuleManager manager){
		Collection<String> result = new LinkedList<String>();
		if( name == null || name.length() == 0 ) result.add("name is missing");
		if( generator == null ) result.add("generator module is missing");
		if( generatorHostRSL == null ) result.add("generator host RSL is missing");
		// TODO RSL validation

		else result.addAll(generator.validate(manager));

		for (BMEvaluator ev : evaluators) {
			result.addAll(ev.validate(manager));
		}
		return result;
	}

	//----- Overrides --------------------------------------------------------//
	/**
	 * Two Analysis objects are equal when all of their subcomponents are equal. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof Analysis) ){ return false; }
		Analysis other = (Analysis)obj;
		if( (this.name == null && other.name != null) ||
			(this.name != null && !this.name.equals(other.name)) ){ return false; }
		if( (this.description == null && other.description != null) ||
			(this.description != null && !this.description.equals(other.description)) ){ return false; }
		if( (this.resultsLink == null && other.resultsLink != null) ||
			(this.resultsLink != null && !this.resultsLink.equals(other.resultsLink)) ){ return false; }
		if( (this.generator == null && other.generator != null) ||
			(this.generator != null && !this.generator.equals(other.generator)) ){ return false; }
		// Conditions tend to fail because we never know whether they are compiled or not
		// Comparing string forms is less likely to provide false results
		if( (this.generatorHostRSL == null && other.generatorHostRSL != null) ||
			(this.generatorHostRSL != null && !this.generatorHostRSL.toString().equals(
					other.generatorHostRSL.toString())) ){ return false; }

		if( evaluators.size() != other.evaluators.size() ){ return false; }
		for (BMEvaluator ev : other.evaluators) {
			if( !evaluators.contains(ev) ){ return false; }
		}
		return true;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Analysis("+id+","+name+")";
	}

	
}
