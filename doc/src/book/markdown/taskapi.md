## Task and Benchmark API

TODO: AQL (description of the abstract querying language API)

One of the main goals of the current BEEN project was making the task API as simple as possible and to minimize the amount of work needed to create the whole benchmark. This was of the biggest problems with the previous BEEN versions as writing a complete and efficient benchmark required a tremendous amount of time both to study the provided API with the related Java classes and to implement the benchmark itself.

BEEN works with three different concepts of user-supplied code and configuration:

* **Task**, which is an elementary unit of code that can be submitted and run by BEEN. Tasks are created by subclassing the abstract `Task` class and implementing the appropriate methods. Each task has to be described by a XML **task descriptor** which specifies the main class to run and parameters of the task.

* **Task context** is a container for multiple tasks that can interact together, pass data to each other and synchronize among themselves. Tasks contexts don't contain any user-written code, they only serve as a wrapper for the contained tasks. Each task context is described by a XML **task context descriptor** that specifies which tasks should be contained within the context.

* **Benchmark** is a first-class object that *generates* task contexts based on its **generator task**, which is again a user-written code created by subclassing the abstract `Benchmark` class. Each benchmark is described by a XML **benchmark descriptor** which specifies the main class to run and parameters of the benchmark. A benchmark is different from a task, because its API is provides features for generating task contexts and it can also persist its state so it can be re-run when an error occurs and the generator task fails.

All these three concepts can be submitted to BEEN and run individually, if you only want to test a single task, you can submit it without providing a task context or a whole benchmark.

### Maven Plugin and Packaging

The easiest way to create a submittable item (e.g. a task) is by creating a Maven project and adding a dependency on the appropriate BEEN module (e.g. `task-api`) in `pom.xml` of the project:

	<dependency>
		<groupId>cz.cuni.mff.d3s.been</groupId>
		<artifactId>task-api</artifactId>
		<version>3.0.0</version>
	</dependency>

Tasks, contexts and benchmark must be packaged into a BPK file, which can then be uploaded to the BEEN cluster. Each BPK package can contain multiple submittable items and multiple XML descriptors. The problem of packaging is made easier by the supplied `bpk-plugin` Maven plugin. To preferred way to use it is to add this plugin to the `package` Maven goal in `pom.xml` of the project:

	<plugin>
		<groupId>cz.cuni.mff.d3s.been</groupId>
		<artifactId>bpk-plugin</artifactId>
		<version>3.0.0</version>
		<executions>
			<execution>
				<goals>
					<goal>buildpackage</goal>
				</goals>
			</execution>
		</executions>
		<configuration>
			...
		</configuration>
	</plugin>

In the plugin's configuration the user must specify at least one descriptor of a task, a context or a benchmark. To add a descriptor into the BPK, it should be added as a standard Java resource file and then referenced in the plugin configuration in `pom.xml` by using `<taskDescriptors>` or `<taskContextDescriptors>` element. For example the provided sample benchmark called `nginx-benchmark` uses this configuration:

	<configuration>
		<taskDescriptors>
			<param>src/main/resources/cz/cuni/mff/d3s/been/nginx/NginxBenchmark.td.xml</param>
		</taskDescriptors>
	</configuration>

This specifies that the package should publish a single descriptor named `NginxBenchmark.td.xml` which is located in the specified resource path. With such a configuration, creating the BPK package is simply a matter of invoking `mvn package` on this project – this will produce a `.bpk` file that can be uploaded into BEEN.

### Descriptor Format

There are two types of descriptors, task descriptors and task context descriptors. Note that benchmarks don't have a special descriptor format, instead you only provide a task descriptor for a generator task of the benchmark. These descriptors are written in XML and they must conform to the supplied XSD definitions (`task-descriptor.xsd` and `task-context-descriptor.xsd`).

The recommended naming practice is to name your task descriptors with the filename ending with `.td.xml` and your task context descriptors ending with `.tcd.xml`.

A simple task descriptor for a single task can look like this:

	<?xml version="1.0"?>
	<taskDescriptor xmlns="http://been.d3s.mff.cuni.cz/task-descriptor"
					groupId="my.sample.benchmark" bpkId="hello-world" version="3.0.0-SNAPSHOT"
					name="hello-world-task" type="task">
		<java>
			<mainClass>my.sample.benchmark.HelloWorldTask</mainClass>
		</java>
	</taskDescriptor>

This specifies the main class and package that should be used to run the task. Apart from this, you can specify what parameters the task should receive and their default values:

	<properties>
		<property name="key">value</property>
	</properties>

These properties will be presented to the user in the web interface before submitting the task and the user can modify them. Next, you can specify command line arguments passed to Java:

	<arguments>
		<argument>-Xms4m</argument>
		<argument>-Xmx8m</argument>
	</arguments>

For debugging purposes, you can specify the `<debug>` element which will enable remote debugging when running the task. With the `<hostRuntimes>` element you can filter on which host runtimes can the task be run. The value of this setting is an XPath expression.

### Task API

To create a task submittable into BEEN, you should start by subclassing the `Task` abstract class. To do this, you only need to provide a single method called `run` which will optionally receive string arguments.

BEEN uses `slf4j` as its logging mechanism and provides a logging backend for all user-written code. This means that you can simply use the standard loggers and any logs with be automatically stored in the BEEN cluster.

Knowing this, the simplest task that will only log a single string can look something like this:

	package my.sample.benchmark;

	import cz.cuni.mff.d3s.been.taskapi.Task;
	import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

	public class HelloWorldTask extends Task {
		private static final Logger log = LoggerFactory.getLogger(HelloWorldTask.class);

		@Override
		public void run(String[] args) {
			log.info("Hello, world!");
		}
	}

If this class is in a Maven project as described in the previous section, it can be packaged into a BPK package by invoking `mvn package`. This package can be uploaded and run either from the web interface or client submitter.

BEEN provides several APIs for user-written tasks:

* *Properties* – Tasks are configurable either from their descriptors or by the benchmark that generated them. These properties are again configurable by the user before submitting the task. All properties have a name and a simple string value and these can be accessed via the `getProperty` method of the abstract `Task` class.

* *Result storing* – Each task can persist a result that it has gathered by using the API providing access to the persistence layer. To store a result, use a `ResultPersister` object, which can be created by using the method `createResultPersister` from the `Task` abstract class.

* *Synchronization and communication* – When multiple tasks run in a task context, they can interact with each other either for synchronization purposes or to exchange data. API for these jobs are provided by the `CheckpointController` class. BEEN provides the concepts of **checkpoints** and **latches**. Latches serve as context-wide atomic numbers with the methods for setting a value, decreasing the value by one and waiting until the latch reaches zero. Checkpoint are also waitable objects, but they can also provide a value that was previously set to the checkpoint.

### Task Properties

Every tasks has a key-value property storage. These properties can be set from various places: From the XML descriptor, from the user when submitting, inherited from a task context, set from a benchmark when it generates a task context. To access these values, you can use the `getProperty` method of the `Task` class:



### Persisting Results

XXX TODO

Persisting a result:

	SampleResult result = ...;
	EntityID eid = new EntityID().withKind("result").withGroup("sample");
	ResultPersister rp = results.createResultPersister(eid));
	rp.persist(result);

### Checkpoints and Latches

Checkpoints present a powerful mechanism for synchronization and communication between tasks. When tasks run in a task context, they share all their checkpoints and they can set a value to a checkpoint and another can wait for the checkpoint. This waiting is passive and once a value is assigned to a checkpoint, the waiter will receive it.

To use checkpoints, create a `CheckpointController`, which is an `AutoCloseable` object so the preferred way to use it is inside a try-catch block to ensure the object will be properly destroyed:

	try (CheckpointController requestor = CheckpointController.create()) {
		...
	} catch (MessagingException e) {
        ...
    }

Each checkpoint has a name, which is context-wide. All communication between tasks can only be done inside a single task context. You don't have to explicitly create a checkpoint, it will be created automatically once a task uses it. Setting a value to a checkpoint can be done with:

	requestor.checkPointSet("mycheckpoint", "the value");

A typical scenario is that one tasks wants to wait for another to pass a value. To wait until a value is set and also to receive the value you can use:

	String value = requestor.checkPointWait("mycheckpoint");

This call passively waits (possibly indefinitely) until a value is set to the checkpoint. There is also a variant of this method that takes another argument specifying a timeout, after which the call will throw an exception. Another method called `checkPointGet` can be used to retrieve the current value of a checkpoint without waiting.

Checkpoints initially don't have any value, and once a value is set, it cannot be changed. They work as a proper synchronization primitive, and setting a value is an atomic operation. The semantics don't change if you start waiting before or after the value is set.

Another provided synchronization primitive is a *latch*. They work best for counting values and for implementing rendez-vous synchronization. A latch provides a method to set an integer value:

	requestor.latchSet("mylatch", 5);

Another task can then call an atomic method to decrease the value of the latch:

	requestor.latchCountDown("mylatch");

You can then wait until the value reaches zero:

	requestor.latchWait("mylatch");

All operations on latches are atomic and the waiting is passive. Latches initially have a value of zero.

### Benchmark API

Writing a benchmark's generator task is similar to writing an ordinary task in the sense that you have to write a subclass, package it and then it will be submitted and run on a host runtime. However, the benchmark API is completely different, because the purpose of the benchmark is to provide a long-running code that will eventually generate new task contexts whenever there is all data for it available.

To create a benchmark, subclass the abstract `Benchmark` class and implement the appropriate methods. The main method to implement is `generateTaskContext` which is called periodically by BEEN and it is expected to return a newly generated task context. This context is then submitted and run. When the context finishes, this method is called again. This loop is ended whenever this method returns `null`.

This approach is chosen to cover a lot of possible use cases. When the benchmark doesn't have data for a new task context, it can simply block until it is possible to create a new context. On the other hand, the benchmark cannot overhaul the cluster by submitting too many contexts. Instead, it's up to the cluster to call the `generateTaskContext` method whenever it seems fit.

For creating task contexts you should use the provided `ContextBuilder` class. This supports loading a task context from a XML file, modifying it and setting values of properties inside the context descriptor. If you have a prepared `.tcd` file with a context descriptor, a sample benchmark that will indefinitely generate this context can look like this:

	package my.sample.benchmark;

	import cz.cuni.mff.d3s.been.benchmarkapi.Benchmark;
	import cz.cuni.mff.d3s.been.benchmarkapi.BenchmarkException;
	import cz.cuni.mff.d3s.been.benchmarkapi.ContextBuilder;
	import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
	import cz.cuni.mff.d3s.been.core.task.TaskContextState;

	public class HelloWorldBenchmark extends Benchmark {
		@Override
		public TaskContextDescriptor generateTaskContext() throws BenchmarkException {
			ContextBuilder contextBuilder = ContextBuilder.createFromResource(HelloWorldBenchmark.class, "context.tcd.xml");
			TaskContextDescriptor taskContextDescriptor = contextBuilder.build();
			return taskContextDescriptor;
		}

		@Override
		public void onResubmit() { }

		@Override
		public void onTaskContextFinished(String s, TaskContextState taskContextState) { }
	}

Notice the methods `onResubmit` and `onTaskContextFinished` which are used as notifications for the benchmark. You can use these methods for whatever error handling or logging you need.

You are supposed to implement the logic for generating the contexts. When your benchmark is done and it will not generate any more contexts, return `null` from the `generateTaskContext` method.

### Creating Task Contexts

The preferred way of creating task contexts is to use the `ContextBuilder` class to load a XML file representing the context descriptor from a resource. This class also provides various methods for modifying the context descriptor and the contained tasks.

You can add tasks into the context via the `addTask` method, these tasks can be created using the `newEmptyTask`. The context descriptor can also provide *task templates* which can be used to create tasks.

However, when it's sufficient, you should create the whole descriptor in the XML file and only use `setProperty` to set the parameters to this task contexts. When the descriptor is done, you can generate it by calling `build` and return the resulting task context descriptor.

### Resubmitting and Benchmark Storage

Benchmarks are supposed to be long-running and BEEN provides a mechanism to keep benchmarks running even after a failure occurs. When a generator task exits with an error (e.g. power outage), it will get resubmitted and the benchmark will continue. To support this behavior, you should use the provided benchmark key-value storage for the internal state of the benchmark and not use any instance variables.

The `Benchmark` abstract class provides methods `storageGet` and `storageSet` which will use the cluster storage for the benchmark state. This storage will be restored whenever the generator task is resubmitted. The implementation of a benchmark that uses this storage can look like this:

	@Override
	public TaskContextDescriptor generateTaskContext() throws BenchmarkException {
		int currentRun = Integer.parseInt(this.storageGet("i", "0"));
		TaskContextDescriptor taskContextDescriptor;
		if (currentRun < 5) {
			// generate a regular context
			taskContextDescriptor = ...;
		} else {
			// we're done
			taskContextDescriptor = null;
		}

		currentRun++;
		this.storageSet("i", Integer.toString(currentRun));

		return taskContextDescriptor;
	}

### Evaluators

BEEN provides a special task type called **evaluator**. The purpose of such a task is to query the stored results, perform and statistical analyses and return an interpretation of the data that can be shown back to the user via web interface. Evaluators are again tasks and they can be run manually (as a single task) or within a benchmark or a context. It's up to the user when and how to run an evaluator.

To create an evaluator, subclass the abstract class `Evaluator` and implement the method `evaluate`. This method is supposed to return a `EvaluatorResult` object which will then be stored in the database. This object holds a byte array of data and a MIME type of it. BEEN supports a few MIME types with which it can directly work, e.g. if the result is a JPEG image, it will show it directly in the web interface. The list of supported MIME types can be found defined as contants inside the `EvaluatorResult` class.

An evaluator needs to retrieve data from the persistence layer, and it can do so using the provided `ResultFacade` interface. This object is available as an instance method on the `Task` superclass. Queries can be build using the `QueryBuilder` object which supports various conditions and query parameters. A simple query that will retrieve a collection of results can have this form:

	Query query = new QueryBuilder().on(...).with(...).fetch();
	Collection<MyResult> data = results.query(query, MyResult.class);

For example code of a simple evaluator that output a plot chart with the measured data and error intervals, see the sample `nginx-benchmark`.
