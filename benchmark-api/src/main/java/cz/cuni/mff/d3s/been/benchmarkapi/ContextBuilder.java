package cz.cuni.mff.d3s.been.benchmarkapi;

import static cz.cuni.mff.d3s.been.core.jaxb.XSD.TASK_CONTEXT_DESCRIPTOR;

import java.io.InputStream;

import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.task.*;

/**
 * Builder of {@link cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor}
 * 
 * @author Martin Sixta
 */
public final class ContextBuilder {

	/**
	 * The descriptor we are building
	 */
	private TaskContextDescriptor descriptor;

	/**
	 * Private constructor
	 */
	private ContextBuilder(TaskContextDescriptor contextDescriptor) {
		this.descriptor = contextDescriptor;
	}

	/**
	 * Sets name of the context.
	 * 
	 * @param name
	 *          Name of the context which will be visible to a user.
	 */
	public void setName(final String name) {
		descriptor.setName(name);
	}

	/**
	 * Sets description of the context
	 * 
	 * @param contextDescription
	 *          Description of the context which will be visible to a user.
	 */
	public void setDescription(final String contextDescription) {
		descriptor.setContextDescription(contextDescription);
	}

	/**
	 * Sets a context-wide property.
	 * <p/>
	 * Previous value corresponding to the name will be overwritten.
	 * 
	 * @param name
	 *          name of the property which will be available to the whole context
	 * @param value
	 *          value corresponding to the given property with <code>name</code>
	 *          available to the whole context
	 */
	public void setProperty(final String name, final String value) {
		if (!descriptor.isSetProperties()) {
			descriptor.setProperties(new Properties());
		}

		for (Property property : descriptor.getProperties().getProperty()) {
			if (property.isSetName() && name.equals(property.getName())) {
				property.setValue(value);
				return;
			}
		}
		descriptor.getProperties().getProperty().add(new Property().withName(name).withValue(value));
	}

	/**
	 * Clears all tasks from the descriptor.
	 */
	public void clearTasks() {
		descriptor.getTask().clear();
	}

	/**
	 * Adds a {@link Task} to the context.
	 * <p/>
	 * The Task will be properly initialized and added to the context. The task
	 * can be further customized by setting its properties. \
	 * 
	 * @param name
	 *          user visible name of the task
	 * @return task initialized and linked task for further customization
	 */
	public Task addTask(String name) {
		Task task = newEmptyTask().withName(name);
		addTask(task);
		return task;
	}

	/**
	 * Adds a {@link Task} to the context with a link to a {@link Template}.
	 * <p/>
	 * The template must already exist.
	 * <p/>
	 * The Task will be properly initialized and added to the context. The task
	 * can be further customized by setting its properties (i.e. can override
	 * template)
	 * 
	 * @param name
	 *          user visible name of the task
	 * @param templateName
	 *          name of the template the task should use as its base
	 * @return initialized and linked task for further customization
	 * @throws BenchmarkException
	 *           when no template with <code>templateName</code> exists
	 */
	public Task addTask(String name, String templateName) throws BenchmarkException {

		if (descriptor.isSetTemplates()) {
			for (Template template : descriptor.getTemplates().getTemplate()) {
				if (template.isSetName() && template.getName().equals(templateName)) {
					Task task = newEmptyTask().withName(name);
					task.getDescriptor().setFromTemplate(templateName);

					addTask(task);

					return task;
				}
			}
		}

		throw new BenchmarkException("No such template " + templateName);
	}

	/**
	 * Adds named {@link Template} to the context.
	 * 
	 * @param templateName
	 *          name of the template
	 * @throws BenchmarkException
	 *           when template with the same name already exits
	 */
	public void addTemplate(String templateName) throws BenchmarkException {
		for (Template template : descriptor.getTemplates().getTemplate()) {
			if (template.isSetName() && templateName.equals(template.getName())) {
				String msg = String.format("Template with name '%s' already exists", templateName);
				throw new BenchmarkException(msg);
			}
		}

		Template template = new Template().withName(templateName).withTaskDescriptor(new TaskDescriptor());

		descriptor.getTemplates().getTemplate().add(template);
	}

	/**
	 * Returns the TaskContextDescriptor this class was used to build.
	 */
	public TaskContextDescriptor build() {
		return descriptor;
	}

	/**
	 * Creates a builder with empty {@link TaskContextDescriptor}
	 * 
	 * @param name
	 *          name of the context
	 * @return a builder which can be used to build a context descriptor
	 */
	public static ContextBuilder create(String name) {
		return new ContextBuilder(new TaskContextDescriptor().withName(name));
	}

	/**
	 * Creates the builder from a resource with TaskContextDescriptor xml.
	 * 
	 * @param classResourceBase
	 *          from where to look for the resource
	 * @param resourceName
	 *          name of the resource with xml representation of a
	 *          TaskContextDescriptor
	 * @return builder which is initialized from the resource
	 * @throws BenchmarkException
	 */
	public static
			ContextBuilder
			createFromResource(Class<?> classResourceBase, String resourceName) throws BenchmarkException {

		InputStream inputStream = classResourceBase.getResourceAsStream(resourceName);

		if (inputStream == null) {
			String msg = String.format("Cannot locate resource '%s'", resourceName);
			throw new BenchmarkException(msg);
		}

		try {

			TaskContextDescriptor contextDescriptor = null;

			BindingParser<TaskContextDescriptor> bindingComposer = TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
			contextDescriptor = bindingComposer.parse(inputStream);

			return new ContextBuilder(contextDescriptor);
		} catch (Exception e) {
			throw new BenchmarkException("Cannot parse input xml", e);
		}
	}

	private boolean addTask(Task task) {
		return descriptor.getTask().contains(task) || descriptor.getTask().add(task);
	}

	private Task newEmptyTask() {
		return new Task().withDescriptor(new Descriptor()).withProperties(new Properties());
	}

}
