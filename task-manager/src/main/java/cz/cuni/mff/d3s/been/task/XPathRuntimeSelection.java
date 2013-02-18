package cz.cuni.mff.d3s.been.task;

import com.hazelcast.query.Predicate;
import cz.cuni.mff.d3s.been.core.RuntimesUtils;
import cz.cuni.mff.d3s.been.core.query.XPathPredicate;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

import java.util.Collection;
import java.util.Random;

/**
 * @author Martin Sixta
 */
final class XPathRuntimeSelection implements IRuntimeSelection {
	private final Random rnd;

	public XPathRuntimeSelection() {
		rnd = new Random();
	}

	@Override
	public String select(TaskEntry taskEntry) throws NoRuntimeFoundException {
		TaskDescriptor td = taskEntry.getTaskDescriptor();

		String xpath;
		if (td.isSetHostRuntimes() && td.getHostRuntimes().isSetXpath()) {
			xpath = td.getHostRuntimes().getXpath();
		} else {
			xpath = "/";
		}


		Predicate predicate = new XPathPredicate(xpath);

		Collection<RuntimeInfo> infos = RuntimesUtils.getRuntimeMap().values(predicate);

		if (infos.size() == 0) {
			throw new NoRuntimeFoundException();
		}

		// Stupid Java
		RuntimeInfo[] ids = infos.toArray(new RuntimeInfo[infos.size()]);


		return (ids[rnd.nextInt(ids.length)].getId());
	}
}
