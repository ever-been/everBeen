## EverBEEN services

### <a id="devel.services.hostruntime">Host Runtime</a>
* how it only helps when you want to run tasks
* why does it make sense to run nodes without it

### <a id="devel.services.taskmanager">Task Manager</a>
The Task Manager is at the heart of the EverBEEN framework, its responsibilities include:

* task scheduling
* context scheduling
* benchmark scheduling
* context state changes
* detection and correction of error states (benchmark failures, Host Runtimes failures, etc.)

Main characteristic:

* event-driven
* distributed
* redundant (in default configuration)

#### <a id="devel.services.taskmanager.distributed">Distributed approach to scheduling</a>
The most important characteristic of the Task Manger is that the computation is event-driven
and distributed among the *DATA* <!-- TODO link to types --> nodes. The implication
from such approach is that there is no central authority, bottleneck or single point
of failure. If a data node disconnects (or crashes) its responsibilities,along with
data, are transparently taken over by the rest of the cluster.

Distributed architecture is the major difference from previous versions
of the BEEN framework.

#### <a id="devel.services.taskmanager.implementation">Implementation</a>
The implementation of the Task Manager is heavily dependant on [Hazelcast](#devel.techno.hazelcast)
distributed data structures and its semantics, especially the `com.hazelcast.core.IMap`.

#### <a id="devel.services.taskmanager.workflow">Workflow</a>
The basic event-based workflow

 1. Receiving asynchronous Hazelcast event
 2. Generating appropriate message describing the event
 3. Generating appropriate action from the message
 4. Executing the action


Handling of internal messages is also message-driven, based on the [0MQ](#devel.techno.zmq)
library, somewhat resembling the Actor model. This has the advantage of separating
logic of message receiving and handling. Internal messages are executed in one thread,
which also removes the need for explicit locking and synchronization (which happens,
but is not responsibility of the Task Manager developer).


#### <a id="devel.services.taskmanager.ownership">Data ownership</a>
An important notion to remember is that an instance of the Task Manager handles
only entries which it owns, whenever possible (e.g. task entries). Ownership of data
means that it is stored in local memory and the node is responsible for it.
The design of Task Manager takes advantage of the locality and most operations
are local with regard to data ownership. This is highly desirable for the Task Manger to scale.

#### <a id="devel.services.taskmanager.structures">Main distributed structures</a>

* BEEN_MAP_TASKS - map containing runtime task information
* BEEN_MAP_TASK_CONTEXTS - map containing runtime context information
* BEEN_MAP_BENCHMARKS - map containing runtime context information

These distributed data structures are also backed by the [MapStore](#devel.services.mapstore)
(if enabled).

#### <a id="devel.services.taskmanager.tasks">Task scheduling</a>
<!-- TODO reference task states -->

The Task Manager is responsible for scheduling tasks - finding a Host Runtime
on which the task can run. Description of possible restrictions can be found at
[Host Runtime] <!-- TODO should point to user documentation for Host Runtime? -->.

A [distributed query](http://hazelcast.com/docs/2.6/manual/single_html/#MapQuery)
is used to find suitable Host Runtimes, spreading the load among `DATA` nodes.

An appropriate Host Runtime is also chosen based on Host Runtime utilization, less
overloaded Host Runtimes are preferred. Among equal hosts a Host Runtime is chosen
randomly.

The lifecycle of a task is commenced by inserting a `cz.cuni.mff.d3s.been.core.task.TaskEntry`
into the task map with a random UUID as the key and in the SUBMITTED state <!-- TODO link -->.
Inserting a new entry to the map causes an event which is handled by the owner
of the key - the Task Manager responsible for the key. The event is
converted to the `cz.cuni.mff.d3s.been.manager.msg.NewTaskMessage` and sent
to the processing thread. The handling logic is separated in order not to block
the Hazelcast service threads. In this regard handling of messages is serialized on the particular
node. The message then generates `cz.cuni.mff.d3s.been.manager.action.ScheduleTaskAction`
which is responsible for figuring out what to do. Several things might happen

* the task cannot be run because it's waiting on another task, the state is changed to WAITING
* the task cannot be run because there is no suitable Host Runtime for it, the state is changed to WAITING
* the task can be scheduled on a chosen Host Runtime, the state is changed to SCHEDULED and the runtime is notified.

If the task is scheduled, the chosen Host Runtime is responsible for the task until it finishes or fails.

WAITING tasks are still responsibility of the Task Manager which can try
to reschedule when an event happen, e.g.:

 * another tasks is removed from a Host Runtime
 * a new Host Runtime is connected

#### <a id="devel.services.taskmanager.benchmarks">Benchmark Scheduling</a>
Benchmark tasks are scheduled the same way as other tasks. The main difference is
that if a benchmark task fails (i.e. Host Runtime failure, but also programming error)
the framework can re-schedule the task on a different Host Runtime.


A problem can arise from re-scheduling an incorrectly written benchmark which fails
too often. There is a [configuration option](#user.configuration.taskmanager) which
controls how many re-submits to allow for a benchmark task.

Future implementation could deploy different heuristics to detect defective benchmark
tasks, such as failure-rate.


#### <a id="devel.services.taskmanager.contexts">Context Handling</a>

Contexts are not scheduled as an entity on Host Runtimes as they are containers
for related tasks. The Task Manager handles detection of contexts state changes.
The state of a contexts is decided from the states of its tasks.

<!-- TODO this should (also) be in user documentation? -->
Task context states:

 * WAITING - for future use
 * RUNNING - contained tasks are running, scheduled or waiting to be scheduled
 * FINISHED - all contained tasks finished without an error
 * FAILED - at least one task from the context failed

Future improvements may include heuristics for scheduling contexts as an entity (i.e. detection
that the context can not be scheduled at the moment, which is difficult because of the
distributed nature of scheduling. Any information gathered might be obsolete by the time
its read).

#### <a id="devel.services.taskmanager.errors">Handling exceptional events</a>

The current Hazelcast implementation (as of version 2.6) has one limitation.
When a key [migrates](http://hazelcast.com/docs/2.5/manual/single_html/#InternalsDistributedMap)
the new owner does not receive any event (`com.hazelcast.partition.MigrationListener` is not much useful
in this regard since it does not contain enough information). This might be a problem if e.g.
a node crashes and an event of type "new task added" is lost. To mitigate the problem
the Task Manager periodically scans (`cz.cuni.mff.d3s.been.manager.LocalKeyScanner`) its *local
keys* looking for irregularities. If it finds one it creates a message to fix it.

There are several situations this might happen:

* Host Runtime failure
* key migration
* cluster restart

Note that this is a safe net - most of the time the framework will receive an event
on which it can react appropriately (e.g. Host Runtime failed).

In the case of cluster restart there might be stale tasks which does not run anymore, but
the state loaded from the [MapStore](#devel.services.mapstore) is inconsistent. Such
situation will be recognized and corrected by the scan.

#### <a id="devel.services.taskmanager.events">Hazelcast events</a>
These are main sources of cluter-wide events, received from Hazelcast:

* Task Events - `cz.cuni.mff.d3s.been.manager.LocalTaskListener`
* Host Runtime events - `cz.cuni.mff.d3s.been.manager.LocalRuntimeListener`
* Contexts events - `cz.cuni.mff.d3s.been.manager.LocalContextListener`

#### <a id="devel.services.taskmanager.messages">Task Manger messages</a>
Main interface `cz.cuni.mff.d3s.been.manager.msg.TaskMessage`, messages are
created through the `cz.cuni.mff.d3s.been.manager.msg.Messages` factory.

Overview of main messages:

* `AbortTaskMessage`
* `ScheduleTaskMessage`
* `CheckSchedulabilityMessage`
* `RunContextMessage`

Detailed description is part of the source code nad Javadoc.


#### <a id="devel.services.taskmanager.actions">Task Manager actions</a>
Main interface `cz.cuni.mff.d3s.been.manager.action.TaskAction`, actions are
created through the `cz.cuni.mff.d3s.been.manager.action.Action` factory.

Overview of actions

* `AbortTaskAction`
* `ScheduleTaskAction`
* `RunContextAction`
* `NullAction`

Detailed description is part of the source code nad Javadoc.

#### <a id="devel.services.taskmanager.locking">Locking</a>

<!-- TODO -->

### <a id="devel.services.swrepo">Software Repository</a>

* functional necessities (availability from all nodes)
* why it uses HTTP and how (describe request format)

### <a id="devel.services.objectrepo">Object Repository</a>

* queue drains
* async persist queue
* abstract query machinery (query queue handling, effective querying without user type knowledge)

### Map Store {#devel.services.mapstore}

The MapStore allows the EverBEEN to persist runtime information, which can
be restored after restart or crash of the framework.

#### <a id="devel.services.mapstore.role">Role of the MapStore</a>

EverBEEN runtime information (such as tasks, contexts and benchmarks, etc.) are
persisted through the MapStore. This adds overhead to working with the distributed
objects, but allows restoring of the state after a cluster restart, providing an
user with more concise experience.

The implementation is build atop of Hazelcast Map Store - mechanism for storing/loading
of Hazelcast distributed objects to/from a persistence layer. The EverBEEN
team implemented a mapping to the MongoDB.

The main advantage of using the MapStore is transparent and easy access to Hazelcast
distributed structures with the ability to persist them - no explicit actions are
needed.

#### <a id="devel.services.mapstore.difference">Difference between the MapStore and the Object repository</a>
Both mechanism are used to persist objects - the difference is in the type of objects
being persisted. The [Object repository](#devel.services.objectrepo) stores
user generated information, whereas the MapStore handles (mainly) BEEN runtime
information - information essential to proper working of the framework.

The difference is also in level of transparency for users. Object persistence
happens on behalf of an user explicit request, the MapStore works "behind the scene".

Even though both implementations currently us MongoDB, in future the team envisage
implementations serving different needs (such as load balancing, persistence
guarantees, data ownership, data access, etc.)

#### <a id="devel.services.mapstore.extension">Extension point</a>
Adapting the layer to different persistence layer (such as relational database)
is relatively easy. By implementing the `com.hazelcast.core.MapStore` interface
and specifying the implementation to use at runtime, an user of the framework
has ability to change behaviour of the layer.

#### <a id="devel.services.mapstore.configuration">Configuration</a>
The layer can be configured to accommodate different needs:

* specify connection options (hostname, user, etc.)
* enable/disable
* change implementation
* write-through and write-back modes

Detailed description of configuration can be found at [Confiuration](#user.configuration).

### <a id="devel.services.webinterface">Web Interface</a>
* why it's not actually a service (but more like a client)
* cluster client connection mechanism
