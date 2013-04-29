package cz.cuni.mff.d3s.been.test;

import static cz.cuni.mff.d3s.been.test.ExampleSyncTask.CLIENT_COUNT_KEY;
import static cz.cuni.mff.d3s.been.test.ExampleSyncTask.CLIENT_RUNS_KEY;
import static cz.cuni.mff.d3s.been.test.ExampleSyncTask.TYPE_KEY;
import static cz.cuni.mff.d3s.been.test.ExampleSyncTask.TaskType.CLIENT;
import static cz.cuni.mff.d3s.been.test.ExampleSyncTask.TaskType.SERVER;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.Descriptor;
import cz.cuni.mff.d3s.been.core.task.Task;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskProperties;
import cz.cuni.mff.d3s.been.core.task.TaskProperty;

/**
 * @author Martin Sixta
 */
public class SyncGenerator {

	private static int numberOfClients = 100;
	private static int numberOfRuns = 100;

	public static void main(String[] args) {

		TaskDescriptor td;

		TaskDescriptor tdTemplate = new TaskDescriptor();
		tdTemplate.setGroupId("cz.cuni.mff.d3s.been");
		tdTemplate.setBpkId("task-sync-test");
		tdTemplate.setVersion("3.0.0");

		TaskContextDescriptor tcd = new TaskContextDescriptor();
		Task serverTask = new Task();
		serverTask.setName("server");
		td = (TaskDescriptor) tdTemplate.clone();
		TaskProperties serverProperties = new TaskProperties();
		List<TaskProperty> serverProps = serverProperties.getProperty();

		serverProps.add(createProperty(TYPE_KEY, SERVER.toString()));
		serverProps.add(createProperty(CLIENT_COUNT_KEY, numberOfClients));
		serverProps.add(createProperty(CLIENT_RUNS_KEY, numberOfRuns));

		td.setProperties(serverProperties);

		serverTask.setDescriptor(new Descriptor().withTaskDescriptor(td));

		tcd.getTask().add(serverTask);

		for (int i = 0; i < numberOfClients; ++i) {
			Task clientTask = new Task();

			clientTask.setName("client-" + i);
			td = (TaskDescriptor) tdTemplate.clone();
			TaskProperties clientProperties = new TaskProperties();
			List<TaskProperty> props = clientProperties.getProperty();

			props.add(createProperty(TYPE_KEY, CLIENT.toString()));
			props.add(createProperty(CLIENT_COUNT_KEY, numberOfClients));
			props.add(createProperty(CLIENT_RUNS_KEY, numberOfRuns));

			clientTask.setDescriptor(new Descriptor().withTaskDescriptor(td));

			td.setProperties(clientProperties);

			tcd.getTask().add(clientTask);
		}

		BindingComposer<TaskContextDescriptor> composer = null;
		StringWriter writer = null;
		try {
			composer = XSD.TASK_CONTEXT_DESCRIPTOR.createComposer(TaskContextDescriptor.class);

			writer = new StringWriter();

			composer.compose(tcd, writer);

		} catch (SAXException | JAXBException e) {
			throw new IllegalArgumentException("TaskEntry can't be converted to XML", e);
		}

		System.out.println(writer.toString());

	}

	private static TaskProperty createProperty(String name, String value) {
		return new TaskProperty().withName(name).withValue(value);
	}

	private static TaskProperty createProperty(String name, int value) {
		return new TaskProperty().withName(name).withValue(Integer.toString(value));
	}
}
