## EverBEEN services

### <a id="devel.services.hostruntime">Host Runtime</a>
* how it only helps when you want to run tasks
* why does it make sense to run nodes without it

### <a id="devel.services.taskmanger">Task Manager</a>
The Task Manager is at the heart of the EverBEEN framework, its responsibilities include:

* task scheduling
* context scheduling
* benchmark scheduling
* context state changes
* detection and correction of error states (benchmark failures, Host Runtimes failures, etc.)

Main characteristic:

* event-driven
* distributed
* redundant (default configuration)

#### <a id="devel.services.taskmanager.distributed">Distributed approach to scheduling</a>
The main characteristics of the Task Manger is that the computation is event-driven
and distributed among the *DATA* (TODO link to explanation) nodes. The biggest implication
from such approach is that there is no central authority, bottleneck or single point
of failure. If a data node disconnects (i.e. crashes) its responsibilities,along with
data, are transparently taken over by the rest of the cluster.

#### <a id="devel.services.taskmanager.implementation">Implementation</a>
The implementation of the Task Manager is heavily dependant on [Hazelcast](#devel.techno.hazelcast)
distributed data structures and its semantics, especially the `com.hazelcast.core.IMap`.

#### <a id="devel.services.taskmanger.workflow">Workflow</a>
The basic event-based workflow

 1. Receiving asynchronous Hazelcast event
 2. Generating appropriate message depicting the event
 3. Generating appropriate action from the message
 4. Executing the action


Handling of internal messages is also message-driven, based on the [0MQ](#devel.techno.zmq)
library, somewhat resembling the Actor model. This has the advantage of separating
logic of message receiving and handling. Internal messages are executed in one thread,
which also removes the need for explicit locking and synchronization (which happens,
but is not responsibility of the Task Manager developer).


#### <a id="devel.services.taskmanager.ownership">Data ownership</a>
An important notion to remember is that an instance of the Task Manager handles
only entries which it owns, whenever possible (i.e. task entries). So it means
that most operation are local with with regard to data ownership. This is highly
desirable for the Task Manger to scale.

#### <a id="devel.services.taskmanager.tasks">Task scheduling</a>

#### <a id="devel.services.taskmanager.contexts">Context Scheduling</a>

#### <a id="devel.services.taskmanager.benchmarks">Benchmark Scheduling</a>


### <a id="devel.services.swrepo">Software Repository</a>

* functional necessities (availability from all nodes)
* why it uses HTTP and how (describe request format)

### <a id="devel.services.objectrepo">Object Repository</a>

* queue drains
* async persist queue
* abstract query machinery (query queue handling, effective querying without user type knowledge)

### <a id="devel.services.mapstore">Map Store - persistence of EverBEEN runtime information</a>

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
