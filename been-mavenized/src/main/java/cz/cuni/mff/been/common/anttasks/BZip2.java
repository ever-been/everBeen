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
 * Class that can compress file to bzip2 file
 * 
 * @author Jaroslav Urban
 */
public class BZip2 extends org.apache.tools.ant.taskdefs.BZip2 {
	/**
	 * Allocates a new <code>BZip2</code> object
	 *
	 */
	private BZip2() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("bzip2");
		setTaskType("bzip2");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Compresses a file to a bzip2 file
	 * 
	 * @param file file that should be compressed
	 * @param archive path to the created zip file
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void bzip(String file, String archive) 
		throws AntTaskException {
		BZip2 z = new BZip2();
		
		File srcFile = new File(file);
		if (srcFile.isDirectory()) {
			throw new AntTaskException("Not a file: " + file);
		}
		if (!srcFile.canRead()) {
			throw new AntTaskException("Cannot read file: " + file);
		}
		
		File destFile = new File(archive);
		
		z.setDestfile(destFile);
		z.setSrc(srcFile);
		
		try {
			z.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
}
