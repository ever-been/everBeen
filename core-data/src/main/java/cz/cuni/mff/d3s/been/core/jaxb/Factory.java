/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.d3s.been.core.jaxb;



/**
 * This is a simple list of all JAXB object factories. 
 * 
 * @author Andrej Podzimek
 */
public final class Factory {

	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.td} package. */
	public static final cz.cuni.mff.d3s.been.core.task.ObjectFactory TASK;
	
	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.benchmark} package. */
	//public static final cz.cuni.mff.d3s.been.core.benchmark.ObjectFactory BENCHMARK;
	
	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.config} package. */
	//public static final cz.cuni.mff.d3s.been.core.config.ObjectFactory CONFIG;
	
	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.dataset} package. */
	//public static final cz.cuni.mff.d3s.been.core.dataset.ObjectFactory DATASET;
	
	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.group} package. */
	//public static final cz.cuni.mff.d3s.been.core.group.ObjectFactory GROUP;
	
	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.pmc} package. */
	//public static final cz.cuni.mff.d3s.been.core.pmc.ObjectFactory PMC;
	
	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.properties} package. */
	//public static final cz.cuni.mff.d3s.been.core.properties.ObjectFactory PROPERTIES;
	
	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.tuplit} package. */
	//public static final cz.cuni.mff.d3s.been.core.tuplit.ObjectFactory TUPLIT;

	/** A factory to produce instances from the {@code cz.cuni.mff.been.jaxb.td} package. */
	public static final cz.cuni.mff.d3s.been.core.ri.ObjectFactory RUNTIME;

	//public static final cz.cuni.mff.d3s.been.core.task.ObjectFactory TASKENTRY;
	
	static {
		TASK = new cz.cuni.mff.d3s.been.core.task.ObjectFactory();
		RUNTIME = new cz.cuni.mff.d3s.been.core.ri.ObjectFactory();
		//TASKENTRY = new  cz.cuni.mff.d3s.been.core.task.ObjectFactory();

		//BENCHMARK = new cz.cuni.mff.d3s.been.core.benchmark.ObjectFactory();
		//CONFIG = new cz.cuni.mff.d3s.been.core.config.ObjectFactory();
		//DATASET = new cz.cuni.mff.d3s.been.core.dataset.ObjectFactory();
		//GROUP = new cz.cuni.mff.d3s.been.core.group.ObjectFactory();
		//PMC = new cz.cuni.mff.d3s.been.core.pmc.ObjectFactory();
		//PROPERTIES = new cz.cuni.mff.d3s.core.bom.properties.ObjectFactory();
		//TUPLIT = new cz.cuni.mff.d3s.been.core.tuplit.ObjectFactory();

	}
}
