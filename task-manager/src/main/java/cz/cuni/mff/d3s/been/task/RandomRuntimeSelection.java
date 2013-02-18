package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.core.RuntimesUtils;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

import java.util.Collection;
import java.util.Random;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

/**
 * @author Martin Sixta
 */
final class RandomRuntimeSelection implements IRuntimeSelection {

	private final Random rnd;

	public RandomRuntimeSelection() {
		rnd = new Random();
	}

	@Override
	public String select(final TaskEntry taskEntry) throws NoRuntimeFoundException {
		Collection<RuntimeInfo> infos = RuntimesUtils.getRuntimes();

		if (infos.size() == 0) {
			throw new NoRuntimeFoundException();
		}

		// Stupid Java
		RuntimeInfo[] ids = infos.toArray(new RuntimeInfo[infos.size()]);


		return (ids[rnd.nextInt(ids.length)].getId());
	}
}
