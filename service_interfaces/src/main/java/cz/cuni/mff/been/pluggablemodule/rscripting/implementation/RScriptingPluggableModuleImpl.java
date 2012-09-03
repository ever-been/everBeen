/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jan Tattermusch
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.pluggablemodule.rscripting.implementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.cuni.mff.been.common.OutputReader;
import cz.cuni.mff.been.common.OutputType;
import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.rscripting.RScriptingPluggableModule;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * R scripting pluggable module is a convenience pluggable module to run R
 * scripts easily.
 * 
 * This is R scripting pluggable module implementation.
 * 
 * @author Jan Tattermusch
 */
public class RScriptingPluggableModuleImpl extends PluggableModule implements
		RScriptingPluggableModule {

	/**
	 * Creates new instance of R pluggable module implementation.
	 * 
	 * @param manager
	 *            pluggable module manager
	 */
	public RScriptingPluggableModuleImpl(PluggableModuleManager manager) {
		super(manager);
	}

	/**
	 * Executes inline R script.
	 * 
	 * @param script
	 *            script lines to run
	 * @param homeDirectory
	 *            home directory where to run the script
	 * @return R process exitcode
	 * @throws ScriptException
	 */
	@Override
	public int runRScript(String[] script, String homeDirectory)
			throws ScriptException {

		File scriptFile;
		try {
			scriptFile = makeTempScriptFile(script);
		} catch (IOException e) {
			throw new ScriptException("Error creating temp file with script", e);
		}

		return runRScript(scriptFile, homeDirectory);
	}

	/**
	 * Executes R script.
	 * 
	 * @param scriptFile
	 *            script file to run
	 * @param homeDirectory
	 *            home directory where to run the script
	 * @return R process exitcode
	 * @throws ScriptException
	 */
	@Override
	public int runRScript(File scriptFile, String homeDirectory)
			throws ScriptException {
		ProcessBuilder procBuilder = new ProcessBuilder();

		procBuilder.directory(new File(homeDirectory));

		// build the command line
		List<String> cmdArray = new ArrayList<String>();
		cmdArray.add("R");
		cmdArray.add("CMD");
		cmdArray.add("BATCH");
		cmdArray.add("--vanilla");

		cmdArray.add(scriptFile.getAbsolutePath());

		procBuilder.command(cmdArray);

		Process p = null;
		int result = -1;
		OutputReader stdOutReader = null;
		OutputReader stdErrReader = null;
		try {
			p = procBuilder.start();

			/*
			 * this needs to be closed otherwise JVM creates a pipe that will
			 * never be closed
			 */
			p.getOutputStream().close();

			stdOutReader = new OutputReader(
					p.getInputStream(),
					OutputType.STDOUT);
			stdOutReader.start();

			stdErrReader = new OutputReader(
					p.getErrorStream(),
					OutputType.STDERR);
			stdErrReader.start();

			result = p.waitFor();

			stdOutReader.join();
			stdErrReader.join();

		} catch (IOException e) {
			throw new ScriptException("Error starting R", e);
		} catch (InterruptedException e) {
			if (p != null) {
				p.destroy();
			} // 'if' avoids stupid warnings.
			throw new ScriptException("Waiting for R was interrupted", e);
		} finally {
			if (stdOutReader != null && stdOutReader.isAlive()) {
				stdOutReader.interrupt();
			}
			if (stdErrReader != null && stdErrReader.isAlive()) {
				stdErrReader.interrupt();
			}
		}

		return result;
	}

	/**
	 * Creates temporary file with script
	 * 
	 * @param script
	 *            script lines
	 * @return temporary file with script
	 * @throws IOException
	 */
	private File makeTempScriptFile(String[] script) throws IOException {
		String tmpFileName = UUID.randomUUID().toString();

		File f = new File(getTempDirectory(), tmpFileName);
		FileOutputStream fos = new FileOutputStream(f);
		PrintWriter out = new PrintWriter(fos);
		try {
			for (String line : script) {
				out.println(line);
			}
		} finally {
			out.close();
			fos.close();
		}
		return f;
	}

	/**
	 * 
	 * @return temporary directory to use
	 */
	public String getTempDirectory() {
		Task task = CurrentTaskSingleton.getTaskHandle();
		if (task == null) {
			return "tmp";
		} else {
			return task.getTempDirectory();
		}
	}

}
