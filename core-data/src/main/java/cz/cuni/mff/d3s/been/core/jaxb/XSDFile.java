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

	/** Imported file with common simple type definitions. */
	COMMON("common.xsd"),

	/** The schema for XML equivalents of task descriptors. */
	TASK_DESCRIPTOR("task-descriptor.xsd"),

	TASK_CONTEXT_DESCRIPTOR("task-context-descriptor.xsd"),

	TASKENTRY("task-entry.xsd"),

	RUNTIME("runtime-info.xsd"),

	HARDWARE_INFO("hardware-info.xsd"),

	BENCHMARK_ENTRY("benchmark-entry.xsd");

	/** A file instance pointing at the corresponding schema definition. */
	final File FILE;

	/**
	 * Initializes the enum member and stores a file name.
	 * 
	 * @param name
	 *          Name of a XSD file.
	 */
	private XSDFile(String name) {
		InputStream input = XSDFile.class.getClassLoader().getResourceAsStream(
				"xsd/" + name);
		try {
			Files.copy(input, XSDRoot.ROOT.resolve(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.FILE = new File(XSDRoot.ROOT.toFile(), name);
	}
}
