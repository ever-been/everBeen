package cz.cuni.mff.d3s.been.taskapi;

import static cz.cuni.mff.d3s.been.core.StatusCode.EX_USAGE;

import java.util.Arrays;

/**
 * 
 * Runs the class specified as the first command line argument.
 * 
 * The first argument must be that of the class to be created.
 * 
 * The Task object will be created and {@link Task#doMain(String[])} will be
 * called with the same arguments minus the first one.
 * 
 * @author Martin Sixta
 */
public class TaskRunner {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Name of the class to run is expected.");
			System.exit(EX_USAGE.getCode());
		}

		String className = args[0];

		Class<?> clazz = getClass(className);

		Task task = createTask(clazz);

		String[] taskArgs = createTaskArgs(args);

		task.doMain(taskArgs);

	}

	/**
	 * Returns the Class object associated with the class or interface with the
	 * given string name.
	 * 
	 * @param className
	 *          the fully qualified name of the desired class
	 * @return the Class object for the class with the specified name.
	 */
	private static Class<?> getClass(String className) {
		Class<?> clazz = null;
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.printf("Class '%s' cannot be found.\n", className);
			System.exit(EX_USAGE.getCode());
		}

		return clazz;

	}

	/**
	 * Creates a new Task object from its Class
	 * 
	 * The class is instantiated as if by a new expression with an empty argument
	 * list.
	 * 
	 * @param clazz
	 *          Class to create the object from
	 * @return
	 */
	private static Task createTask(Class<?> clazz) {
		Object obj = null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e) {
			System.err.printf("Cannot instantiate class %s. Error message: %s\n", clazz.getCanonicalName(), e.getMessage());
			System.exit(EX_USAGE.getCode());
		} catch (IllegalAccessException e) {
			System.err.printf("Cannot create class %s. Error message: %s\n", clazz.getCanonicalName(), e.getMessage());
			System.exit(EX_USAGE.getCode());
		}

		if (!(obj instanceof Task)) {
			System.err.printf("Class %s must be instance of %s\n", clazz.getCanonicalName(), Task.class);
			System.exit(EX_USAGE.getCode());
		}

		return (Task) obj;

	}

	/**
	 * Create arguments for a task from arguments supplied to the runner.
	 * 
	 * It strips the first arguments which is the class name.
	 * 
	 * @param args
	 * @return
	 */
	private static String[] createTaskArgs(String[] args) {
		assert (args.length >= 1);

		if (args.length == 1) {
			return new String[0];
		} else {
			return Arrays.copyOfRange(args, 1, args.length);
		}
	}

}
