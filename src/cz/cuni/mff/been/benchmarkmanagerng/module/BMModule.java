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
package cz.cuni.mff.been.benchmarkmanagerng.module;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.AccessType;

import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;

/**
 * Base for benchmark Manager pluggable module containers.
 * BMGenerator and BMEvaluator inherit from this class only to differentiate the two.
 * 
 * @author Jiri Tauber
 */
@MappedSuperclass
@AccessType("field")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"name", "version"})})
public abstract class BMModule implements Serializable, Comparable<BMModule> {
	private static final long serialVersionUID = 5195490405248363765L;

	/** The module ID used in the Hibernate database */
	@Id @GeneratedValue
	protected Integer id;

	/** Pluggable module name */
	@Column(nullable=false)
	private String name = null;

	/** pluggable module version */
	@Column(nullable=false, length=20 )
	private String version = null;

	/** The module configuration */
	@Lob @Column(length=4096)
	private Configuration configuration = null;

	//----------------------------------------//

	public BMModule() { }

	public BMModule(String Name, String version) {
		this.name = Name;
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BMModule o) {
		return getPackageName().compareToIgnoreCase(o.getPackageName());
	}

	//----- Getters and Setters -----//
	public String getName() { return name; }
	public String getVersion() { return version; }

	/**
	 * @return The name of the pluggable module package
	 */
	public String getPackageName() { return name+'-'+version; }

	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration( Configuration config ){
		configuration = config;
	}

	/**
	 * @return The pluggable module descriptor containing the name and version
	 */
	public PluggableModuleDescriptor getPluggableModuleDescriptor() {
		return new PluggableModuleDescriptor(name, version);
	}

	/**
	 * Returns the described pluggable module.
	 * Uses the specified PluggableModuleManager to get the pluggable module instance.
	 *
	 * @return The pluggable module described by name and version
	 * @throws PluggableModuleException when something went wrong
	 */
	public PluggableModule getPluggableModule(PluggableModuleManager manager) throws PluggableModuleException {
		PluggableModule module = manager.getModule(getPluggableModuleDescriptor());
		if( module == null ){
			throw new PluggableModuleException("Couldn't load pluggable module instance!");
		}
		return module;
	}

	/**
	 * Checks if the module contains all the required information.
	 * It's simple way of checking module integrity if you don't
	 * have pluggable module manager available.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.BMModule#validate(PluggableModuleManager) validate(PluggableModuleManager)
	 * @return <code>true</code> if module is complete, <code>false</code> otherwise
	 */
	public boolean isComplete(){
		if( name == null || name.length() == 0 ) return false;
		if( version == null || version.length() == 0 ) return false;
		if( configuration == null ) return false;
		return true;
	}

	/**
	 * Validates the module.
	 * Checks include existence of name, version, configuration, and pluggable module package.
	 * Validity of configuration is checked if everything exists.
	 * 
	 * @param manager pluggable module manager used to load the pluggable module
	 * @return list of errors found in this module
	 */
	public Collection<String> validate(PluggableModuleManager manager){
		Collection<String> result = new LinkedList<String>();
		if( name == null || name.length() == 0 ) result.add("module is missing name");
		if( version == null || version.length() == 0 ) result.add("module "+name+" is missing version");
		if( configuration == null ) result.add("module "+name+" is missing configuration");
		if( result.size() == 0 ){
			try {
				ModuleInterface inst = (ModuleInterface)getPluggableModule(manager);
				result = inst.validateConfiguration(configuration);
			} catch( PluggableModuleException e ){
				result.add(name+" module couldn't validate the configuration because error occured: "+e.getMessage());
			}
		}
		return result;
	}

	//----- equals -----//
	/**
	 * Compares this pluggable module to another one. Disregards configuration.
	 * 
	 * @param obj
	 * @return true if the pluggable modules are the same
	 */
	public boolean isSimilarTo(Object obj){
		if( !(obj instanceof BMModule) ){ return false; }
		BMModule other = (BMModule)obj;
		if( !this.name.equals(other.name) ){ return false; }
		if( !this.version.equals(other.version) ){ return false; }

		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( !isSimilarTo(obj) ){ return false; }

		BMModule other = (BMModule)obj;
		if( !this.configuration.equals(other.configuration) ){ return false; }

		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getPackageName().hashCode();
	}

}
