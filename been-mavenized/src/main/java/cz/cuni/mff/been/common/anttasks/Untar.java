/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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

/**
 * Class decompresses files from zip/jar archives
 * 
 * @author Jaroslav Urban
 */
public class Untar extends org.apache.tools.ant.taskdefs.Untar {
	/**
	 * Allocates a new <code>Zip</code> object
	 */
	private Untar() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("untar");
		setTaskType("untar");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Decompresses an archive to a directory
	 * 
	 * @param archive 	to be dearchived
	 * @param directory to which the files should be put
	 * @param compression	compression method to use. Available values are:
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
	public static void untar(String archive, String directory, String compression) 
		throws AntTaskException {
		
		Untar z = new Untar();
		if (compression != null) {
			UntarCompressionMethod method = new UntarCompressionMethod();
			method.setValue(compression);
			z.setCompression(method);
		}
		
		File srcArchive = new File(archive);
		if (!srcArchive.exists()) {
			throw new AntTaskException("Source archive does not exist: " + archive);
		}
		
		File destDir = new File(directory);
		
		z.setSrc(srcArchive);
		z.setDest(destDir);
		try {
			z.execute();
		} catch (Exception e) {
			throw new AntTaskException("Extract failed", e);
		}
	}
	
	/**
	 * Dearchives .tar archive using no compression to given directory 
	 * @param archive	Source archive to dearchive
	 * @param directory	Destination directory
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void untar(String archive, String directory) 
		throws AntTaskException {
		untar(archive, directory, "none");
	}
}
