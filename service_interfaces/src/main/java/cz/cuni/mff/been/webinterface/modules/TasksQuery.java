package cz.cuni.mff.been.webinterface.modules;

import cz.cuni.mff.been.softwarerepository.MatchException;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface;
import cz.cuni.mff.been.softwarerepository.PackageType;

import java.io.Serializable;

/**
 * Queries the task manager for all tasks.
 *
 * The class was extracted from the TaskModule.
 *
 * @author Martin Sixta
 *
 */
final class TasksQuery implements PackageQueryCallbackInterface,
		Serializable {

	private static final long serialVersionUID = -3720817515690864148L;

	@Override
	public boolean match(PackageMetadata metadata) throws MatchException {
		return metadata.getType() == PackageType.TASK;
	}
}