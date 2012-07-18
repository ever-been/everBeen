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
import org.apache.tools.ant.types.FileSet;


/**
 * Copies a file to another file or directory using Ant. 
 * 
 * @author Jaroslav Urban
 */
public class Copy extends org.apache.tools.ant.taskdefs.Copy {
	/**
	 * Allocates a new <code>Copy</code> object.
	 * 
	 */
	private Copy() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("copy");
		setTaskType("copy");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Copy a file to another file.
	 * 
	 * @param srcFile file to be copied.
	 * @param toFile copy of the file.
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void copy(String srcFile, String toFile) 
	throws AntTaskException {
		Copy c = new Copy();

		File src = new File(srcFile);
		c.setFile(src);
		c.setTofile(new File(toFile));
		c.setTodir(null);
		c.setOverwrite(true);
		try {
			c.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
	
	/**
	 * Copy a file to a directory.
	 * 
	 * @param srcFile file to be copied.
	 * @param toDir destination directory.
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void copyToDir(String srcFile, String toDir) 
	throws AntTaskException {
		Copy c = new Copy();
		
		File src = new File(srcFile);
		File dest = new File(toDir);
		c.setFile(src);
		c.setTodir(dest);
		c.setTofile(null);
		c.setOverwrite(true);
		try {
			c.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
	
	/**
	 * Copies a directory to another directory.
	 * 
	 * @param srcDir source directory.
	 * @param toDir path to the copy of the directory (the full new name).
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void directoryCopy(String srcDir, String toDir)
	throws AntTaskException {
		filesetCopy(srcDir, "**", toDir);
	}
	
	
	/**
	 * Copies an Ant's fileset from one directory to another.
	 * 
	 * @param srcDir source directory.
	 * @param fileset the Ant's fileset.
	 * @param toDir target directory.
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void filesetCopy(String srcDir, String fileset, String toDir) 
	throws AntTaskException {
		Copy c = new Copy();
		
		File src = new File(srcDir);
		if (!src.isDirectory()) {
			throw new AntTaskException("Not a directory: " + srcDir);
		}

		FileSet fs = new FileSet();
		fs.setDir(src);
		fs.setIncludes(fileset);
		
		c.addFileset(fs);
		c.setTodir(new File(toDir));
		c.setOverwrite(true);
		try {
			c.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
}
