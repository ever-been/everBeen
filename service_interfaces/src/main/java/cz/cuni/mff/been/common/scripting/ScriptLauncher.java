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

package cz.cuni.mff.been.common.scripting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import cz.cuni.mff.been.common.OutputReader;
import cz.cuni.mff.been.common.OutputType;
import cz.cuni.mff.been.core.utils.FileUtils;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * Utility class for launching shell script (on both windows and linux).
 * 
 * @author Jan Tattermusch
 */
public class ScriptLauncher {

	/**
	 * Creates new instance of class.
	 */
	public ScriptLauncher() {}

	/**
	 * Runs file with shell script in given environment
	 * 
	 * @param scriptFile
	 *          file to run
	 * @param environment
	 *          script's environment
	 * @return script's exitcode
	 * @throws ScriptException
	 */
	public int runShellScript(File scriptFile, ScriptEnvironment environment) throws ScriptException {

		chmodExecutable(scriptFile);

		try {
			ProcessBuilder procBuilder = new ProcessBuilder();

			// set working directory (random file)
			File workingDir;
			if (environment.getDirectory() != null) {
				workingDir = environment.getDirectory();
			} else {
				workingDir = new File(getTempDirectory());
			}

			workingDir.mkdirs();
			procBuilder.directory(workingDir);

			String[] cmdArray = new String[] { scriptFile.getAbsolutePath() };

			for (String variable : environment.variables()) {
				procBuilder.environment().put(variable, environment.getEnv(variable));
			}

			procBuilder.command(cmdArray);

			Process p = procBuilder.start();

			/* this needs to be closed otherwise JVM keeps the pipe open */
			p.getOutputStream().close();

			OutputReader stdOutReader = new OutputReader(p.getInputStream(), OutputType.STDOUT);
			stdOutReader.setOutputStream(System.out);
			stdOutReader.start();
			OutputReader stdErrReader = new OutputReader(p.getErrorStream(), OutputType.STDERR);
			stdErrReader.setOutputStream(System.err);
			stdErrReader.start();

			int result = -1;
			try {
				result = p.waitFor();

				stdOutReader.join();
				stdErrReader.join();

			} catch (InterruptedException e) {
				p.destroy();
				throw e;
			} finally {
				// stop reader threads in any case

				if (stdOutReader.isAlive()) {
					stdOutReader.interrupt();
				}
				if (stdErrReader.isAlive()) {
					stdErrReader.interrupt();
				}
			}

			return result;

		} catch (IOException e) {
			throw new ScriptException("Error occured when executing script.", e);
		} catch (InterruptedException e) {
			throw new ScriptException("Error occured when executing script.", e);
		}
	}

	/**
	 * Runs shell script in given environment
	 * 
	 * @param script
	 *          lines of script to run
	 * @param environment
	 *          script's environment
	 * @return script's exitcode
	 * @throws ScriptException
	 */

	public int runShellScript(String[] script, ScriptEnvironment environment) throws ScriptException {

		File scriptFile;
		try {
			scriptFile = makeTempScriptFile(script);
		} catch (IOException e) {
			throw new ScriptException("Error running file", e);
		}

		int result = runShellScript(scriptFile, environment);

		scriptFile.delete();

		return result;
	}

	/**
	 * Creates temporary file with script
	 * 
	 * Tries to detect whether running on windows and if so, adds .bat to file
	 * name
	 * 
	 * @param script
	 *          script lines
	 * @return temporary file with script
	 * @throws IOException
	 */
	private File makeTempScriptFile(String[] script) throws IOException {
		String tmpFileName = UUID.randomUUID().toString();

		if (CurrentTaskSingleton.getTaskHandle() != null) {
			if (CurrentTaskSingleton.getTaskHandle().isRunningInWindows()) {
				tmpFileName += ".bat";
			}
		}

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
	 * Adds execute permission to file
	 * 
	 * @param file
	 *          file to chmod
	 * @throws ScriptException
	 */
	private void chmodExecutable(File file) throws ScriptException {
		try {
			FileUtils.chmod(file, "rwxr-xr-x");
		} catch (IOException e) {
			throw new ScriptException("Error chmod-ing script (" + e.getMessage() + ")");
		}
	}

	/**
	 * 
	 * @return temporary directory to use
	 */
	public String getTempDirectory() {
		Task task = CurrentTaskSingleton.getTaskHandle();
		if (task == null) {
			return ".";
		} else {
			return task.getTempDirectory();
		}
	}

}
