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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * A simple list of XSD file names. A fine thing to avoid typos in file names.
 * ;-)
 * 
 * @author Andrej Podzimek
 */
public enum XSDFile {

	/** Included file with common attribute group definitions. */
	ATTRTYPES("attrtypes.xsd"),

	/** The schema for analyses (bechmarks) deinitions. */
	BENCHMARK("benchmark.xsd"),

	/** Imported file with common simple type definitions. */
	COMMON("common.xsd"),

	/** The schema for condition clause (tree) definitions. */
	CONDITION("condition.xsd"),

	/** The schema for generator and evaluator module configuration metadata. */
	CONFIG("config.xsd"),

	/** The schema for dataset definitions (aka SQL table definitions). */
	DATASET("dataset.xsd"),

	/** The schema for host group definitions. */
	GROUP("group.xsd"),

	/** The schema for plugable module configuration files. */
	PMC("pmc.xsd"),

	/** The schema for host property tree definitions. */
	PROPERTIES("properties.xsd"),

	/** Included file with string value / binary value definitions. */
	STRBIN("strbin.xsd"),

	/** The schema for XML equivalents of task descriptors. */
	TD("td.xsd"),

	/** The schema for XML representation of triggers. */
	TRIGGER("trigger.xsd"),

	/** The schema for data handle tuple literals. */
	TUPLIT("tuplit.xsd"),

	TASKENTRY("taskentry.xsd"),

	RUNTIME("runtimeinfo.xsd"),

	HARDWARE_INFO("hardwareinfo.xsd");

	/** A file instance pointing at the corresponding schema definition. */
	final File FILE;

	/**
	 * Initializes the enum member and stores a file name.
	 * 
	 * @param name
	 *          Name of a XSD file.
	 */
	private XSDFile(String name) {
		InputStream input = XSDFile.class.getClassLoader().getResourceAsStream("xsd/" + name);
		try {
			Files.copy(input, XSDRoot.ROOT.resolve(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.FILE = new File(XSDRoot.ROOT.toFile(), name);
	}
}
