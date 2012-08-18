/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Andrej Podzimek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.clinterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import cz.cuni.mff.been.clinterface.Constants.DataError;
import cz.cuni.mff.been.clinterface.Constants.IntegrityError;
import cz.cuni.mff.been.clinterface.modules.BenchmarksModule;
import cz.cuni.mff.been.clinterface.modules.DummyModule;
import cz.cuni.mff.been.clinterface.modules.HostsModule;
import cz.cuni.mff.been.clinterface.modules.MetaModule;
import cz.cuni.mff.been.clinterface.modules.PackagesModule;
import cz.cuni.mff.been.clinterface.modules.ResultsModule;
import cz.cuni.mff.been.clinterface.modules.ServicesModule;
import cz.cuni.mff.been.clinterface.modules.TasksModule;
import cz.cuni.mff.been.task.CurrentTaskSingleton;

/**
 * This is an abstract ancestor of all modules implementing the functionality of
 * command line interface. There is a similar mechanism in webinterface package
 * ({@code Module}), but there are many differences between the two concepts. In
 * the command line interface:
 * <ol>
 * <li>Modules are normal dynamic instances rather than singletons.</li>
 * <li>Modules are pooled and carefully re-used to permit parallel access.</li>
 * <li>Only module classes are found by reflection, not the action handlers.</li>
 * <li>(The above implies:) Modules can support arbitrary action names.</li>
 * </ol>
 * 
 * @author Andrej Podzimek
 */
public abstract class CommandLineModule {

	/** A maaping of module names to module classes. */
	private static final Map<String, Class<? extends CommandLineModule>> classMap;

	/** A mapping of module names to module instance pools. */
	private static final Map<String, ConcurrentLinkedQueue<CommandLineModule>> instanceMap;

	/** A mapping of module classes to module instance pools. */
	private static final Map<Class<? extends CommandLineModule>, ConcurrentLinkedQueue<CommandLineModule>> reverseMap;

	/** A list of available module names, one per line. */
	public static final String MODULE_LIST;

	static {
		classMap = new TreeMap<String, Class<? extends CommandLineModule>>();
		instanceMap = new TreeMap<String, ConcurrentLinkedQueue<CommandLineModule>>();
		reverseMap = new HashMap<Class<? extends CommandLineModule>, ConcurrentLinkedQueue<CommandLineModule>>();

		/*
		 * You have to add all your module classes here. Unfortunately, module
		 * registration cannot be done from the descendant's static initializer.
		 * The JVM will not load its class when it doesn't seems to be needed,
		 * so the initializer would not run at all.
		 */
		registerModule(BenchmarksModule.MODULE_NAME, BenchmarksModule.class);
		registerModule(DummyModule.MODULE_NAME, DummyModule.class);
		registerModule(HostsModule.MODULE_NAME, HostsModule.class);
		registerModule(MetaModule.MODULE_NAME, MetaModule.class);
		registerModule(PackagesModule.MODULE_NAME, PackagesModule.class);
		registerModule(ResultsModule.MODULE_NAME, ResultsModule.class);
		registerModule(ServicesModule.MODULE_NAME, ServicesModule.class);
		registerModule(TasksModule.MODULE_NAME, TasksModule.class);

		StringBuilder moduleList = new StringBuilder();

		for (String module : classMap.keySet()) {
			moduleList.append(module).append('\n');
		}
		MODULE_LIST = moduleList.toString();
	}

	/**
	 * An almost hidden constructor that permits module instantiation.
	 */
	protected CommandLineModule() {
	}

	/**
	 * This is similar to the mechanism used by the web interface. However,
	 * actions can be dynamic here and it is up to the module to make a run-time
	 * decision about action name's correctness.
	 * 
	 * @param action
	 *            The requested action name.
	 * @param request
	 *            Command line options obtained from the native client.
	 * @param response
	 *            A connection to the native client's back channel.
	 * @throws ModuleOutputException
	 *             When output cannot be written.
	 * @throws ModuleSpecificException
	 *             When the input is not semantically correct.
	 */
	protected abstract void handleAction(
			String action,
			CommandLineRequest request,
			CommandLineResponse response) throws ModuleSpecificException,
			ModuleOutputException;

	/**
	 * List of actions getter.
	 * 
	 * @return A list of actions where {@code "\n\t"} is used as prefix and
	 *         separator.
	 */
	protected abstract String getActionsList();

	/**
	 * This should initialize a module and restore any state it may store. Used
	 * by the module instance pool to re-use instances.
	 */
	protected abstract void restoreState();

	/**
	 * Module name getter. Not needed here, but many BEEN components have this
	 * feature, so it might be advisable to implement it, too.
	 * 
	 * @return The official module name that should be used in mappings and the
	 *         like.
	 */
	protected abstract String getName();

	/**
	 * Disposes the module and stores its instance in its instance pool for
	 * future re-use. Restores its original state so that it can be used again.
	 */
	final void recycle() {
		restoreState();
		registerInstance(this);
	}

	/**
	 * Module instance getter. Looks up an instance in the instance pools and
	 * creates a new one if either no instances are available or all instances
	 * are busy.
	 * 
	 * @param name
	 *            Name of the requested module.
	 * @return An instance of the module found by name.
	 * @throws IllegalInputException
	 *             When an unknown module name is requested.
	 * @throws CommandLineException
	 *             When module instantiation fails.
	 */
	static CommandLineModule forName(String name) throws CommandLineException {
		ConcurrentLinkedQueue<CommandLineModule> queue;

		if ((queue = instanceMap.get(name)) == null) {
			throw new IllegalInputException(DataError.UNKNOWN_MODULE);
		} else {
			CommandLineModule result;

			if ((result = queue.poll()) == null) { // Need not care about sync.
													// ;-)
				try {
					result = classMap.get(name) // We know it exists.
							.getConstructor() // We know it exists.
							.newInstance();
				} catch (Exception exception) {
					throw new CommandLineException(
							IntegrityError.MOD_INST_ERR.MSG,
							exception); // There is no way to recover...
				}
				result.restoreState(); // Only for new instances.
			}
			return result;
		}
	}

	/**
	 * This registers a module class and maps its name to this class. It should
	 * be only invoked statically on class initialization. It doesn't include
	 * any synchronization.
	 * 
	 * @param name
	 *            Name of the new module for the {@link #forName(String)}
	 *            lookup.
	 * @param classs
	 *            A class implementing this module.
	 */
	private static void registerModule(
			String name,
			Class<? extends CommandLineModule> classs) {
		ConcurrentLinkedQueue<CommandLineModule> queue;

		queue = new ConcurrentLinkedQueue<CommandLineModule>();
		classMap.put(name, classs);
		instanceMap.put(name, queue);
		reverseMap.put(classs, queue);
	}

	/**
	 * A utility method to construct sets of strings.
	 * 
	 * @param strings
	 *            Elements of the set.
	 * @return The set.
	 */
	protected static Set<String> stringSet(String... strings) {
		return new TreeSet<String>(Arrays.asList(strings));
	}

	/**
	 * This ads a single instance of a module to the instance pool. This method
	 * must be called by {@link #recycle()} only.
	 * 
	 * @param instance
	 *            The instance that should be added to the pool.
	 */
	private static void registerInstance(CommandLineModule instance) {
		ConcurrentLinkedQueue<CommandLineModule> queue;

		if ((queue = reverseMap.get(instance.getClass())) == null) {
			CurrentTaskSingleton.getTaskHandle().logWarning(
					"Unknown module class refused: "
							+ instance.getClass().getName()); // Weird error,
																// old module
																// etc.
		} else {
			queue.offer(instance);
		}
	}

	/**
	 * Makes all instances in the current pool inaccessible, so that they can be
	 * garbage-collected. This method could be called after an extremely heavy
	 * load, but won't be necessary in most cases.
	 * 
	 * @param name
	 *            The module name for which instance pool should be flushed.
	 */
	static void flushPool(String name) {
		ConcurrentLinkedQueue<CommandLineModule> queue;

		if ((queue = instanceMap.get(name)) == null) { // Module already
														// removed...
			CurrentTaskSingleton.getTaskHandle().logWarning(
					"Invalid pool flush refused: " + name);
		} else {
			queue.clear();
		}
	}

	/**
	 * Flushes all module instance pools. This method could be called after an
	 * extremely heavy load, but won't be necessary in most cases.
	 */
	static void flushAllPools() {
		for (ConcurrentLinkedQueue<CommandLineModule> queue : instanceMap
				.values()) {
			queue.clear();
		}
	}
}
