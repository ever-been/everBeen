package cz.cuni.mff.d3s.been.core.task;

import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.td.TaskDescriptor;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;

/**
 * @author Martin Sixta
 */
public class TaskEntries {

	public static TaskEntry create(String id, TaskDescriptor taskDescriptor) {
		TaskEntry entry = new TaskEntry(id, taskDescriptor);

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
}
