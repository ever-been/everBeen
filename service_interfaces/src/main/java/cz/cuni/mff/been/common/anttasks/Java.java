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
import org.apache.tools.ant.types.Path;

/**
 * Runs the Java Virtual Machine.
 * 
 * @author Jaroslav Urban
 */
public class Java extends org.apache.tools.ant.taskdefs.Java {

	/**
	 * Allocates a new <code>Java</code> object.
	 * 
	 */
	private Java() {
		// dummy ant project
		Project antProject = new Project();
		antProject.init();
		setProject(antProject);
		// dummy ant task name
		setTaskName("java");
		setTaskType("java");
		// dummy ant target
		setOwningTarget(null);
	}

	/**
	 * Runs the Java Virtual Machine.
	 * 
	 * @param classname name of the class that should be run.
	 * @param classpath classpath for the JVM. You can either ":" or ";" characters
	 * as directory separators 
	 * @param directory directory in which should the JVM be started
	 * @throws AntTaskException if anyhthing goes wrong.
	 */
	public static void runJava(String classname, String classpath, String directory)
		throws AntTaskException {
		
		Java j = new Java();
		j.setFailonerror(true);
		
		Path p = new Path(j.getProject());
		p.setPath(classpath);
		j.setClasspath(p);
		
		j.setClassname(classname);
		j.setFork(true);
		File dir = new File(directory);
		j.setDir(dir);
		
		try {
			j.execute();
		} catch (Exception e) {
			throw new AntTaskException(e);
		}
	}
}
