/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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

package cz.cuni.mff.been.hostruntime;

import cz.cuni.mff.been.task.ShellScriptJob;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Task loader for Shell tasks.
 * Runs ShellScriptTask with set script filename
 * @author Jan Tattermusch
 */
public class ShellTaskLoader extends TaskLoader {

	/**
	 * Loads task (instance of ShellScriptTask)
	 */
	
	@Override
	protected void loadTask(String scriptFileName) {
		try {
			this.task = new ShellScriptJob(scriptFileName);
		} catch (TaskInitializationException e) {
			System.err.println("Cannot create instance of ShellScriptJob " 
					+ " : " + e.getMessage());
			e.printStackTrace();
			System.exit(Task.EXIT_CODE_ERROR);
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("You must give the scripts's filename as a command line parameter");
			System.exit(Task.EXIT_CODE_ERROR);
		}
		
		String taskClass = args[0];
		ShellTaskLoader loader = new ShellTaskLoader();
		loader.loadTask(taskClass);
		loader.runTask();
	}
}
