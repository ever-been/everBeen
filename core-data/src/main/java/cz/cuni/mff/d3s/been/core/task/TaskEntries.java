package cz.cuni.mff.d3s.been.core.task;

import static cz.cuni.mff.d3s.been.core.jaxb.Factory.TASK;

import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;

/**
 * Convenient functions for TaskEntry.
 * 
 * Use these instead of directly manipulating a TaskEntry.
 * 
 * @author Martin Sixta
 */
public class TaskEntries {

	private TaskEntries() {
		// prevents initialization
	}

	/**
	 * Creates a new {@link TaskEntry} from given {@link TaskDescriptor}. Entry is
	 * created with state {@link TaskState#CREATED}, with random UUID and default
	 * owner and runtime id.
	 * 
	 * @param taskDescriptor
	 *          for which the new entry is created
	 * @return initialized entry
	 */
	public static TaskEntry create(TaskDescriptor taskDescriptor, String taskContextId) {
		if (taskContextId == null) {
			throw new NullPointerException("Task context ID cannot be null.");
		}

		if (taskDescriptor == null) {
			throw new NullPointerException("Task descriptor cannot be null.");
		}

		TaskEntry entry = TASK.createTaskEntry();

		entry.setState(TaskState.CREATED);
		entry.setId(UUID.randomUUID().toString());
		entry.setTaskContextId(taskContextId);
		entry.setTaskDescriptor(taskDescriptor);

		// id of the data node which owns this entry
		entry.setOwnerId("0");

		// id of the host runtime on which the task is scheduled
		entry.setRuntimeId("0");

		return entry;
	}

	/**
	 * 
	 * Returns XML string of an entry.
	 * 
	 * @param entry
	 *          an entry to serialize
	 * @return XML serialization of the entry
	 * @throws IllegalArgumentException
	 *           if entry cannot be serialized
	 */
	public static String toXml(TaskEntry entry) throws IllegalArgumentException {
		BindingComposer<TaskEntry> composer = null;
		StringWriter writer = null;
		try {
			composer = XSD.TASKENTRY.createComposer(TaskEntry.class);

			writer = new StringWriter();

			composer.compose(entry, writer);

		} catch (SAXException | JAXBException e) {
			throw new IllegalArgumentException("TaskEntry can't be converted to XML", e);
		}

		return writer.toString();

	}

	/**
	 * 
	 * Sets a new state of a {@link TaskEntry}.
	 * 
	 * @param entry
	 *          entry to change
	 * @param newState
	 *          state to change to
	 * @param reasonFormat
	 *          format string with explanation why the change has been made
	 * @param reasonArgs
	 *          arguments for the format string
	 * @throws IllegalArgumentException
	 *           if the transition to a new state is illegal.
	 */
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


	/**
	 *
	 * Returns collection of transition taken by a {@link TaskEntry}.
	 *
	 * @param entry
	 * @return mutable list of transitions
	 *
	 * TODO: sixtm make it imutable/collection
	 */
	public static List<StateChangeEntry> getStateChangeEntries(TaskEntry entry) {
		if (!entry.isSetStateChangeLog()) {
			entry.setStateChangeLog(TASK.createStateChangeLog());
		}

		return entry.getStateChangeLog().getLogEntries();
	}

	/**
	 * Creates StateChangeEntry.
	 *
	 * @param state
	 * @param reasonFormat
	 * @param reasonArgs
	 * @return
	 */
	private static StateChangeEntry createStateChangeEntry(TaskState state,
														   String reasonFormat, Object... reasonArgs) {
		StateChangeEntry logEntry = TASK.createStateChangeEntry();
		logEntry.setState(state);
		logEntry.setReason(String.format(reasonFormat, reasonArgs));
		return logEntry;
	}
}
