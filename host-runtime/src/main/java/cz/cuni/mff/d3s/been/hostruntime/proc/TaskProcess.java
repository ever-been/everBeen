package cz.cuni.mff.d3s.been.hostruntime.proc;

import java.util.Collection;

import org.apache.commons.exec.CommandLine;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * 
 * Host Runtime abstraction of a task process. The class does not represent
 * running process of a task (at least not now), but rather an environment
 * needed by such process.
 * 
 * Confused? Come up with a better name/abstraction.
 * 
 * TODO sligtly misleading name? TODO what else belongs here?
 * 
 * @author Martin Sixta
 */
public interface TaskProcess {

	/**
	 * Returns all identifiers of Bpks needed by the process.
	 * 
	 * @return Collection of all Bpk identifiers needed by the process, always not
	 *         null
	 */
	public Collection<BpkIdentifier> getBkpDependencies();

	/**
	 * Returns all identifiers of Artifacts needed by the process.
	 * 
	 * @return Collection of all Artifact identifiers needed by the process,
	 *         always not null
	 */
	public Collection<ArtifactIdentifier> getArtifactDependencies();

	/**
	 * 
	 * Returns command line of the process.
	 * 
	 * @return
	 * @throws cz.cuni.mff.d3s.been.hostruntime.TaskException
	 *           when command line cannot be prepared for the task
	 */
	public CommandLine createCommandLine() throws TaskException;
}
