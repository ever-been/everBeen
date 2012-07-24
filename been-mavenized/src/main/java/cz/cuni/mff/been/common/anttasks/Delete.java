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
 * Deletes files using Ant.
 * 
 * @author Jaroslav Urban
 */
public class Delete extends org.apache.tools.ant.taskdefs.Delete {
	/**
	 * 
	 * Allocates a new <code>Delete</code> object.
	 *
	 */
	private Delete() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("delete");
		setTaskType("delete");
		// dummy ant target
		setOwningTarget(null);
	}
	
	/**
	 * Deletes a file.
	 * 
	 * Throws an exception if the path does not refer to a file.
	 * Throws an exception if the file does not exist.
	 * 
	 * TODO Explain why it is better to use an Ant task instead of File.delete ...
	 * 
	 * @param path path to the file.
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void deleteFile(String path) throws AntTaskException {
		Delete d = new Delete();
		File f = new File(path);
		
		// The Ant task can delete both files and directories.
		// Make sure we really run it on a file. 
		if (!f.isFile ()) throw new AntTaskException ("The path argument does not refer to an existing file.");
		
		d.setFile (f);
		try {
			d.execute ();
		} catch (Exception e) {
			throw new AntTaskException (e);
		}
	}

	/**
	 * Deletes a directory.
	 * 
	 * Deletes the directory recursively (that is including its content).
	 * Throws an exception if the path does not refer to a directory.
	 * Throws an exception if the path does not exist. 
	 * 
	 * TODO Explain why it is better to use an Ant task for this ...
	 *
	 * @param path path to the directory.
	 * @throws AntTaskException if anything goes wrong.
	 */
	public static void deleteDirectory(String path) throws AntTaskException {
		Delete d = new Delete();
		File dir = new File(path);

		// The Ant task can delete both files and directories.
		// Make sure we really run it on a directory. 
		if (!dir.isDirectory ()) throw new AntTaskException ("The path argument does not refer to an existing directory.");
		
		d.setDir(dir);
		try {
			d.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
}
