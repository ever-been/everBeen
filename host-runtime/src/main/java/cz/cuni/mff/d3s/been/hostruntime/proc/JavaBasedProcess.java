package cz.cuni.mff.d3s.been.hostruntime.proc;

import static cz.cuni.mff.d3s.been.bpk.PackageNames.FILES_DIR;
import static cz.cuni.mff.d3s.been.bpk.PackageNames.LIB_DIR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.jar.JarFile;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.util.StringUtils;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * 
 * Abstraction of a JVM based task.
 * 
 * @author Martin Sixta
 */
class JavaBasedProcess implements TaskProcess {

	/**
	 * Name of Java's executable. This is overkill, isn't it?
	 */
	private static String JAVA_PROG = "java";

	/**
	 * Name of Java's classpath argument. See {@link #JAVA}.
	 */
	private static String JAVA_CLASSPATH_ARG = "-cp";

	/**
	 * Wildcard for classpath.
	 */
	private static String CP_WILDCARD = "*";

	private JavaRuntime runtime;
	private TaskDescriptor td;
	private Path taskDir;

	public JavaBasedProcess(JavaRuntime runtime, TaskDescriptor td, Path taskDir) {
		this.runtime = runtime;
		this.td = td;
		this.taskDir = taskDir;
	}

	@Override
	public Collection<BpkIdentifier> getBkpDependencies() {
		if (runtime.getBpkDependencies() != null) {
			return runtime.getBpkDependencies().getDependency();
		} else {
			return new LinkedList<>();
		}
	}

	@Override
	public Collection<ArtifactIdentifier> getArtifactDependencies() {
		if (runtime.getBpkArtifacts() != null) {
			return runtime.getBpkArtifacts().getArtifact();
		} else {
			return new LinkedList<>();
		}

	}

	@Override
	public CommandLine createCommandLine() throws TaskException {
		Path filesDir = Paths.get(FILES_DIR);
		Path libDir = Paths.get(LIB_DIR);
		Path jarPath = taskDir.resolve("files").resolve(runtime.getJarFile());

		String filesCp = filesDir.resolve(CP_WILDCARD).toString(); // dirty tricks
		String libCp = libDir.resolve(CP_WILDCARD).toString(); // dirty tricks

		// --------------------------------------------------------------------
		// program name
		// --------------------------------------------------------------------
		CommandLine cmdLine = new CommandLine(JAVA_PROG);

		// --------------------------------------------------------------------
		// classpath
		// --------------------------------------------------------------------
		String classpath = concatPaths(libCp, filesCp);
		cmdLine.addArgument(JAVA_CLASSPATH_ARG).addArgument(classpath);

		// --------------------------------------------------------------------
		// java options
		// --------------------------------------------------------------------
		if (td.isSetJava() && td.getJava().isSetJavaOptions()) {
			for (String javaOpt : td.getJava().getJavaOptions().getJavaOption()) {
				cmdLine.addArgument(javaOpt);
			}
		}

		// --------------------------------------------------------------------
		// debug
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// main class
		// --------------------------------------------------------------------
		cmdLine.addArgument(getMainClass(jarPath));

		// --------------------------------------------------------------------
		// task arguments
		// --------------------------------------------------------------------
		if (td.isSetArguments()) {
			for (String taskOpt : td.getArguments().getArgument()) {
				cmdLine.addArgument(taskOpt);
			}
		}

		return cmdLine;
	}

	/**
	 * 
	 * Concatenates java paths.
	 * 
	 * @param paths
	 *          Paths to concatenate.
	 * @return Concatenation of paths using path separator for current platform
	 */
	private String concatPaths(String... paths) {
		return StringUtils.toString(paths, File.pathSeparator);
	}

	/**
	 * 
	 * Determines Main-Class of a jar.
	 * 
	 * @param bpkJarPath
	 * @return Main-Class of the parameter
	 * @throws TaskException
	 *           when Main-Class cannot be determined
	 */
	private static String getMainClass(Path bpkJarPath) throws TaskException {
		try (JarFile jarfile = new JarFile(bpkJarPath.toFile())) {
			return jarfile.getManifest().getMainAttributes().getValue("Main-Class");
		} catch (IOException e) {
			throw new TaskException("Cannot determine Main-Class for JVM based task.", e);
		}
	}
}
