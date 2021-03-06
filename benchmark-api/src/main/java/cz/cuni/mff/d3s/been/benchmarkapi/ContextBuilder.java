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
	 * Private constructor.
	 * 
	 * @param contextDescriptor
	 *          the context descriptor to use
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
	 *          value corresponding to the given property with {@code name}
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
	 * can be further customized by setting its properties , but the
	 * {@link TaskDescriptor} of the template will be used.
	 * 
	 * @param name
	 *          user visible name of the task
	 * @param templateName
	 *          name of the template the task should use as its base
	 * @return initialized and linked task for further customization
	 * @throws BenchmarkException
	 *           when no template with {@code templateName} exists
	 */
	public Task addTask(final String name, final String templateName) throws BenchmarkException {
		Task task = newEmptyTask().withName(name);
		task.getDescriptor().setFromTemplate(templateName);

		addTask(task);

		return task;

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
			if (template.isSetName() && template.getName().equals(templateName)) {
				String msg = String.format("Template with name '%s' already exists", templateName);
				throw new BenchmarkException(msg);
			}
		}

		Template template = new Template().withName(templateName).withTaskDescriptor(new TaskDescriptor());

		descriptor.getTemplates().getTemplate().add(template);
	}

	/**
	 * Returns template with {@code templateName}.
	 * 
	 * @param templateName
	 *          name of the template to look for
	 * @return the template with {@code templateName}
	 * @throws BenchmarkException
	 *           when no template with {@code templateName} exits
	 */
	public Template getTemplate(String templateName) throws BenchmarkException {
		for (Template template : descriptor.getTemplates().getTemplate()) {
			if (template.isSetName() && template.getName().equals(templateName)) {
				if (!template.isSetTaskDescriptor()) {
					template.setTaskDescriptor(new TaskDescriptor());
				}
				return template;
			}
		}

		String msg = String.format("No such with name '%s' exists", templateName);
		throw new BenchmarkException(msg);

	}

	/**
	 * Sets selector on a template.
	 * 
	 * @param templateName
	 *          name of the template to set the selector on
	 * @param selector
	 *          expression which selects Host Runtimes the task can run on
	 * 
	 * @throws BenchmarkException
	 *           when no template with {@code templateName} exits
	 */
	public void setSelector(final String templateName, final String selector) throws BenchmarkException {
		Template template = getTemplate(templateName);

		if (selector == null || selector.isEmpty()) {
			return;
		}

		if (!template.getTaskDescriptor().isSetHostRuntimes()) {
			template.getTaskDescriptor().setHostRuntimes(new HostRuntimes());
		}

		template.getTaskDescriptor().getHostRuntimes().setXpath(selector);

	}

	/**
	 * Sets tasks exclusivity for a template.
	 * 
	 * @param templateName
	 *          name of the template to set the {@code exclusivity} on
	 * @param exclusivity
	 *          exclusivity of all tasks created from the template
	 * @throws BenchmarkException
	 *           when no template with {@code templateName} exits
	 */
	public void setExclusivity(final String templateName, final TaskExclusivity exclusivity) throws BenchmarkException {
		Template template = getTemplate(templateName);
		template.getTaskDescriptor().setExclusive(exclusivity);
	}

	/**
	 * Returns the TaskContextDescriptor this class was used to build.
	 * 
	 * @return the resulting context descriptor
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
	 *           when an error has occurred during the creation of the descriptor
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

			TaskContextDescriptor contextDescriptor;

			BindingParser<TaskContextDescriptor> bindingComposer = TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
			contextDescriptor = bindingComposer.parse(inputStream);

			return new ContextBuilder(contextDescriptor);
		} catch (Exception e) {
			throw new BenchmarkException("Cannot parse input xml", e);
		}
	}

	/**
	 * Adds a task into the current context descriptor.
	 * 
	 * @param task
	 *          the task to add
	 * @return true if the task was successfully added
	 */
	private boolean addTask(Task task) {
		return descriptor.getTask().contains(task) || descriptor.getTask().add(task);
	}

	/**
	 * Creates a new empty task that can be added into the context.
	 * 
	 * @return a new empty task
	 */
	private Task newEmptyTask() {
		return new Task().withDescriptor(new Descriptor()).withProperties(new Properties());
	}

}
