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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;

public class Cvs  {
	
	private Cvs() {
	}
	
	public static void checkout(String repository, String pass, String cvsRsh, String module, String tag, String date, String dest) throws AntTaskException {
		checkout(repository, pass, cvsRsh, module, tag, date, dest, false);
	}
	
	public static void checkout(String repository, String pass, String cvsRsh, String module, String tag, String date, String dest, boolean notRecursive) throws AntTaskException {

		AbstractCvsTask task = new AbstractCvsTask() {
			@Override
			protected OutputStream getOutputStream() {
				return System.out;
			}
			
			
			@Override
			protected OutputStream getErrorStream() {
				return System.err;
			}
		};
		task.setFailOnError(true);

		task.setTaskName("cvs");
		task.setTaskType("cvs");
		
		task.setCommand("checkout");
		task.setCvsRoot(repository);
		task.setCvsRsh(cvsRsh);
		task.setPackage(module);
		task.setTag(tag);
		task.setDate(date);
		task.setDest(new File(dest));
		
		if (notRecursive) {
			task.addCommandArgument("-l");
		}
		
		//if password is set, create temp password file
		try {
			if (pass != null) {
				File passFile = File.createTempFile("cvs","pass");
				passFile.deleteOnExit();
				FileWriter fw = new FileWriter(passFile);
				fw.write(pass);
				fw.flush();
				fw.close();
				task.setPassfile(passFile);
			}
			
		} catch (IOException e) {
			throw new AntTaskException("Can't write password file",e);
		}
		
		
		
		
		Target target = new Target();
		target.setName("checkout-cvs");
		target.addTask(task);
		task.setOwningTarget(target);
		
		Project project = new Project();
		project.init();
		project.addTarget(target);
		target.setProject(project);
		task.setProject(project);
		
		try {
			project.executeTarget("checkout-cvs");
		} catch (BuildException e) {
			throw new AntTaskException("Error checking out from CVS",e);
		}
	}
	
	public static String rlog(String repository, String pass, String cvsRsh, String module, String tag) throws AntTaskException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		AbstractCvsTask task = new AbstractCvsTask() { 
			
			@Override
			protected OutputStream getOutputStream() {
				return out;
			}
			
			@Override
			protected OutputStream getErrorStream() {
				return System.err;
			}
			
		};
		task.setFailOnError(true);

		task.setTaskName("cvs");
		task.setTaskType("cvs");
		
		task.setCommand("rlog");
		task.setCvsRoot(repository);
		task.setCvsRsh(cvsRsh);
		task.setPackage(module);
		task.setTag(tag);
		//task.setDate(date);
		//task.setDest(new File(destFile));
		
		//if password is set, create temp password file
		try {
			if (pass != null) {
				File passFile = File.createTempFile("cvs","pass");
				passFile.deleteOnExit();
				FileWriter fw = new FileWriter(passFile);
				fw.write(pass);
				fw.flush();
				fw.close();
				task.setPassfile(passFile);
			}
			
		} catch (IOException e) {
			throw new AntTaskException("Can't write password file",e);
		}
		
		Target target = new Target();
		target.setName("rlog-cvs");
		target.addTask(task);
		task.setOwningTarget(target);
		
		Project project = new Project();
		project.init();
		project.addTarget(target);
		target.setProject(project);
		task.setProject(project);
		
		try {
			project.executeTarget("rlog-cvs");
			return out.toString();
		} catch (BuildException e) {
			throw new AntTaskException("Error retrieving rlog from CVS",e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Cvs.checkout(":pserver:anonymous@omniorb.cvs.sourceforge.net:/cvsroot/omniorb",
				null,null,"omni","omni4_0_develop","1 Jan 2005","data");
	}
}
