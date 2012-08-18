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
 * Class that can compress files to the zip format
 * 
 * @author Jaroslav Urban
 */
public class Zip extends org.apache.tools.ant.taskdefs.Zip {
	/**
	 * Allocates a new <code>Zip</code> object
	 *
	 */
	private Zip() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("zip");
		setTaskType("zip");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Compresses a directory to a zip file
	 * 
	 * @param directory path to directory that should be compressed
	 * @param archive path to the created zip file
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void zipDirectory(String directory, String archive) 
		throws AntTaskException {
		Zip z = new Zip();
		
		File srcDir = new File(directory);
		if (!srcDir.isDirectory()) {
			throw new AntTaskException("Not a directory: " + directory);
		}
		if (!srcDir.canRead()) {
			throw new AntTaskException("Cannot read directory: " + directory);
		}
		
		File destFile = new File(archive);
		
		z.setDestFile(destFile);
		z.setBasedir(srcDir);
		try {
			z.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
}
