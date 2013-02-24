package cz.cuni.mff.d3s.been.core.task;

import static cz.cuni.mff.d3s.been.core.jaxb.Factory.TASK;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;

/**
 * Convenient functions for TaskEntry.
 * 
 * Use these instead of directly manipulating a TaskEntry.
 * 
 * @author Martin Sixta
 */
public class TaskEntries {

	/**
	 * Creates a new {@link TaskEntry} from giten {@link TaskDescriptor}. Entry is
	 * created with state {@link TaskState#CREATED}, with random UUID and default
	 * owner and runtime id.
	 * 
	 * @param taskDescriptor
	 *          for which the new entry is created
	 * @return initialized entry
	 */
	public static TaskEntry create(TaskDescriptor taskDescriptor) {
		TaskEntry entry = TASK.createTaskEntry();

		entry.setState(TaskState.CREATED);
		entry.setId(UUID.randomUUID().toString());
		entry.setTaskDescriptor(taskDescriptor);

		entry.setOwnerId("0");
		// FIXME Martin - comments !! :) what is runtime ID :)
		entry.setRuntimeId("0");

		return entry;
	}

	// FIXME Martin - comments please :) why we can use this method. We do not want to search usage if we do not understand what is this method :) If one of the "create" methods here 
	public static TaskEntry create(String pathToTaskDescriptor) {
		BindingParser<TaskDescriptor> bindingComposer = null;
		try {
			bindingComposer = XSD.TD.createParser(TaskDescriptor.class);
			File file = new File(pathToTaskDescriptor);
			TaskDescriptor td = bindingComposer.parse(file);

			return create(td);

		} catch (SAXException | JAXBException | ConvertorException e) {

			throw new IllegalArgumentException(e);
		}

	}

	public static void setState(TaskEntry entry, TaskState newState,
			String reasonFormat, Object... reasonArgs) throws IllegalArgumentException {
		TaskState oldState = entry.getState();

		if (oldState == null || !oldState.canChangeTo(newState)) {
			throw new IllegalStateException("Cannot change state from " + oldState + " to " + newState);
		}

		StateChangeEntry logEntry = createStateChangeEntry(newState, String.format(reasonFormat, reasonArgs));

		getStateChangeEntries(entry).add(logEntry);

		entry.setState(newState);
	}

	private static StateChangeEntry createStateChangeEntry(TaskState state,
			String reasonFormat, Object... reasonArgs) {
		StateChangeEntry logEntry = TASK.createStateChangeEntry();
		logEntry.setState(state);
		logEntry.setReason(String.format(reasonFormat, reasonArgs));
		return logEntry;
	}

	public static List<StateChangeEntry> getStateChangeEntries(TaskEntry entry) {
		if (!entry.isSetStateChangeLog()) {
			entry.setStateChangeLog(TASK.createStateChangeLog());
		}

		return entry.getStateChangeLog().getLogEntries();
	}
}
