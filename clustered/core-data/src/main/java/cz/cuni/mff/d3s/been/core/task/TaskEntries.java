package cz.cuni.mff.d3s.been.core.task;

import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.List;

import static cz.cuni.mff.d3s.been.core.jaxb.Factory.TASK;

/**
 * Convenient functions for TaskEntry.
 *
 * Use these instead of directly manipulating a TaskEntry.
 *
 * @author Martin Sixta
 */
public class TaskEntries {

	public static TaskEntry create(String id, TaskDescriptor taskDescriptor) {
		TaskEntry entry = TASK.createTaskEntry();
		entry.setState(TaskState.CREATED);
		entry.setId(id);
		entry.setTaskDescriptor(taskDescriptor);

		return entry;
	}

	public static TaskEntry create(String id, String pathToTaskDescriptor) {
		BindingParser<TaskDescriptor> bindingComposer = null;
		try {
			bindingComposer = XSD.TD.createParser(TaskDescriptor.class);
			File file = new File(pathToTaskDescriptor);
			TaskDescriptor td = bindingComposer.parse(file);

			return create(id, td);

		} catch (SAXException | JAXBException | ConvertorException e) {

		    throw new IllegalArgumentException(e);
		}

	}

	public static void setState(TaskEntry entry, TaskState newState, String reason) throws IllegalArgumentException {
		TaskState oldState = entry.getState();

		if (!oldState.canChangeTo(newState)) {
			throw new IllegalStateException("Cannot change state from " + oldState + " to " + newState);
		}

		StateChangeEntry logEntry = createStateChangeEntry(newState, reason);

		getStateChangeEntries(entry).add(logEntry);

		entry.setState(newState);
	}

	private static StateChangeEntry createStateChangeEntry(TaskState state, String reason) {
		StateChangeEntry logEntry = TASK.createStateChangeEntry();
		logEntry.setState(state);
		logEntry.setReason(reason);
		return logEntry;
	}

	public static List<StateChangeEntry> getStateChangeEntries(TaskEntry entry) {
		if (!entry.isSetStateChangeLog()) {
			entry.setStateChangeLog(TASK.createStateChangeLog());
		}

		return entry.getStateChangeLog().getLogEntries();
	}
}
