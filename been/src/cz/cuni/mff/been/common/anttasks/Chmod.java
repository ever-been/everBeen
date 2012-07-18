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
 * Class that works like the UNIX chmod command. Has any effect only
 * in UNIX-like systems.
 * 
 * @author Jaroslav Urban
 */
public class Chmod extends org.apache.tools.ant.taskdefs.Chmod {
	/** 
	 * Number of files given on the command line to 1 chmod process.
	 * Shouldn't be too high, otherwise the filenames might not fit on 
	 * the command line.
	 */
	private static final int MAX_PARALLEL = 10;
	
	/**
	 * Allocates a new <code>Chmod</code> object.
	 *
	 */
	private Chmod() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("chmod");
		setTaskType("chmod");
		// dummy ant target
		setOwningTarget(null);
		
		setMaxParallel(MAX_PARALLEL);
	}
	
	/**
	 * Recursively sets the permissions of the directory and all it's 
	 * subdirectories and files.
	 * 
	 * @param directory  directory in which chmod will be performed.
	 * @param perms new permissions, in the chmod command style, e.g. "755",
	 * "a+rwx" etc..
	 * @throws AntTaskException when anything goes wrong.
	 */
	public static void recursiveDirectoryChmod(String directory, String perms) 
		throws AntTaskException {
		
		Chmod ch = new Chmod();
		
		File srcDir = new File(directory);
		if (!srcDir.isDirectory()) {
			throw new AntTaskException("Not a directory: " + directory);
		}

		ch.setDir(srcDir);
		ch.setIncludes("**");
		ch.setPerm(perms);
		try {
			ch.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}

	}

	/**
	 * Modifies permissions for single file.
	 * 
	 * @param fileName	target file name.
	 * @param perms	permissions to set (chmod-like syntax).
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void chmod(String fileName, String perms)
		throws AntTaskException {
		
		Chmod ch = new Chmod();
		
		File targetFile = new File(fileName);
		
		ch.setFile(targetFile);
		ch.setPerm(perms);
		try {
			ch.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
	
}
