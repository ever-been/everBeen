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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class stores nothing but the name of the directory for XSD files.
 * 
 * @author Andrej Podzimek
 */
public final class XSDRoot {
	
	//public static final String XSD_ROOT = "been.directory.jaxb";
	
	/** The root path itself. */
	static final Path ROOT;
	
	static {
		Path tmp = null;
		try {
			tmp = Files.createTempDirectory("been-jaxb-");
			assert tmp != null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		ROOT = tmp;
	}
}
