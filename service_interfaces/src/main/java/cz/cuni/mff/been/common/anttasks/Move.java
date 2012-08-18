/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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
 * Moves a file to another file or directory using Ant. 
 * 
 * @author Michal Tomcanyi
 */
public class Move extends org.apache.tools.ant.taskdefs.Move {
	/**
	 * Allocates a new <code>Move</code> object
	 */
	private Move() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("move");
		setTaskType("move");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Move a file to another file
	 * 
	 * @param srcFile file to be moved
	 * @param toFile target file
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void move(String srcFile, String toFile) 
	throws AntTaskException {
		Move c = new Move();

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
	 * Move a file to a directory
	 * 
	 * @param srcFile file to be moved
	 * @param toDir destination directory 
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void moveToDir(String srcFile, String toDir) 
	throws AntTaskException {
		Move c = new Move();
		
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
	 * Moves a directory to another directory
	 * 
	 * @param srcDir source directory
	 * @param toDir destination directory
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void directoryMove(String srcDir, String toDir) 
	throws AntTaskException {
		filesetMove(srcDir, "**", toDir);
	}
	
	/**
	 * Fileset-based move from <code>sourceDir</code> to <code>toDir</code>
	 * where source files are matched from sourceDir according to 
	 * <code>sourceIncludes</code>
	 * @param sourceDir		source directory
	 * @param sourceIncludes include to use to determine files to move (relative to sourceDir) 
	 * @param toDir			target directory
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void filesetMove(String sourceDir, String sourceIncludes, String toDir)
	throws AntTaskException {
		Move c = new Move();
		
		File src = new File(sourceDir);
		if (!src.isDirectory()) {
			throw new AntTaskException("Not a directory: " + sourceDir);
		}

		FileSet fs = new FileSet();
		fs.setDir(src);
		fs.setIncludes(sourceIncludes);
		
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
