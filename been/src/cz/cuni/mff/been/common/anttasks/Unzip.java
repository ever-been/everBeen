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
import org.apache.tools.ant.types.PatternSet;

/**
 * Class decompresses files from zip/jar archives
 * 
 * @author Jaroslav Urban
 */
public class Unzip extends org.apache.tools.ant.taskdefs.Expand {
	/**
	 * Allocates a new <code>Zip</code> object
	 *
	 */
	private Unzip() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("unzip");
		setTaskType("unzip");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Decompresses an archive to a directory
	 * 
	 * @param archive 	to be dearchived
	 * @param directory to which the dearchived files should be put
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void unzip(String archive, String directory) 
		throws AntTaskException {
		
		Unzip z = createInstance(archive, directory);
		
		try {
			z.execute();
		} catch (Exception e) {
			throw new AntTaskException("Extract failed", e);
		}
	}
	
	public static void unzip(String archive, String includes, String directory)
		throws AntTaskException {

		Unzip z = createInstance(archive, directory);
		PatternSet extractPatternSet = new PatternSet();
		extractPatternSet.setIncludes(includes);
		try {
			z.execute();
		} catch (Exception e) {
			throw new AntTaskException("Extract failed", e);
		}
	}
	
	private static Unzip createInstance(String archive, String directory) 
		throws AntTaskException {
		Unzip z = new Unzip();
		
		File srcArchive = new File(archive);
		if (!srcArchive.exists()) {
			throw new AntTaskException("Source archive does not exist: " + archive);
		}
		
		File destDir = new File(directory);
		
		z.setSrc(srcArchive);
		z.setDest(destDir);
		
		return z;
	}
}
