/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Unknown
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
package cz.cuni.mff.been.common.anttasks;

import java.io.File;

import org.apache.tools.ant.Project;

public class Tar extends org.apache.tools.ant.taskdefs.Tar {

	/**
	 * Allocates a new <code>Zip</code> object
	 */
	private Tar() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("tar");
		setTaskType("tar");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Archives directory to a tar file
	 * 
	 * @param sourceDir 	directory to archive
	 * @param archive 		destination archive
	 * @param compression	optional compression method to use. Available values are:
	 * <ul>
     *   <li>none - no compression
     *   <li>gzip - Gzip compression
     *   <li>bzip2 - Bzip2 compression
     * </ul>
     * <code>null</code> defaults to "none"
     * 
	 * @throws AntTaskException if anything goes wrong.
	 * 						
	 */
	public static void tar(String sourceDir, String archive, String compression) 
	throws AntTaskException {
		Tar task = new Tar();
		task.setBasedir(new File(sourceDir));
		task.setDestFile(new File(archive));
		if (compression != null) {
			TarCompressionMethod comp = new TarCompressionMethod();
			comp.setValue(compression);
			task.setCompression(comp);
		}
		try {
		task.execute();
		} catch (Exception e) {
			throw new AntTaskException("Archiving failed",e);
		}
	}
	
}
