package cz.cuni.mff.d3s.been.core.task;

import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.Factory;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.td.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.taskentry.TaskEntryInfo;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

import cz.cuni.mff.d3s.been.core.entry.IEntry;

/**
 * BEEN entry describing a task.
 *
 *
 *
 * @author Martin Sixta
 */
public class TaskEntry implements IEntry {

	private TaskState state;
	private final String id;
	private final TaskDescriptor descriptor;
	private String runtime;

	// TODO: Idea
	// History of task state changes along with a message ("Scheduled to some host",
	// "Aborted because it rains", etc)


	public TaskEntry(String id, TaskDescriptor descriptor) {
		this.id = id;
		this.descriptor = descriptor;
		this.state = TaskState.CREATED;
	}


	public TaskState getState() {
		return state;
	}

	public void setState(TaskState newState, /*ignored*/ String message) {
		// TODO
		// if ( ! isAlloweTransition(oldState, newState)) {
		// 	throw new IllegalArgumentException("State change not permitted")
		// }
		this.state = newState;
	}


	public String getId() {
		return id;
	}


	public TaskDescriptor getDescriptor() {
		return descriptor;
	}



	@Override
	public String toString() {
		return id;
	}

	public String toXml() {
		TaskEntryInfo info = Factory.TASKENTRY.createTaskEntryInfo();
		info.setId(getId());

		if (getRuntime() != null) {
			info.setRuntime(getRuntime());
		} else {
			info.setRuntime("");
		}

		info.setState(getState().toString());
		StringWriter sw = new StringWriter();
		try {
			BindingComposer<TaskEntryInfo> bindingComposer = XSD.TASKENTRY.createComposer(TaskEntryInfo.class);
			bindingComposer.compose(info, sw);
		} catch (JAXBException | SAXException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}


}