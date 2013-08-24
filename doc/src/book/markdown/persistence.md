## <a id="user.persistence">Persistence layer</a>
EverBEEN persistence layer functions as a bridge between EverBEEN distributed memory and a database of choice, rather than a direct storage component. This enables EverBEEN to run without a persistence layer, at the cost of heap space and a risk of data loss in case of an unexpected cluster-wide shutdown. EverBEEN doesn't *need* a persistence layer per-se at any given point in time. User tasks, however, might attempt to work with previously acquired results. Such attempts will result in task-scope failures if the persistence layer is not running. Log archives, too, will be made unavailable if the persistence layer is offline.



### <a id="user.persistence.characteristics">Characteristics</a>
Follows an overview of the main characteristics of EverBEEN's persistence layer.



#### <a id="user.persistence.characteristics.bridging">Bridging</a>
The EverBEEN persistence layer doesn't offer any means of storing the objects per se. It only functions as an abstract access layer to an existing storage component (e.g. a database). EverBEEN comes with a default implementation of this bridge for the MongoDB database, but it is possible to port it to a different database (see [extension point](#user.persistence.extension) notes for more details). The user is responsible for setting up, running and maintaining the actual storage software.



#### <a id="user.persistence.characteristics.eventual">Eventual persistence</a>
As mentioned above, object-persisting commands (result stores, logging) do not, by themselves, execute insertions into the persistence layer. They submit objects into EverBEEN's distributed memory. When a persistence layer node is running, it continually drains this distributed memory, enacting the actual persistence of drained objects. This offers the advantage of being able to pursue persisting operations even in case the persistence layer is currently unavailable.

The downside of the bridging approach is that persisted objects might not find their way into the actual persistence layer immediately. It also means that should a cluster-wide shutdown occur while some objects are still in the shared memory, these objects will get lost. All that can be guaranteed is that submitted objects will eventually be persisted, provided that some data nodes and a persistence layer are running. This being said, experience shows that the transport of objects through the cluster and to the persistence layer is a matter of fractions of a second.



#### <a id="user.persistence.characteristics.scalability">Scalability</a>
As mentioned above, EverBEEN does not strictly rely on the existence of a persistence node for running user code, only to present the user with the data he requires. That being said, EverBEEN can also run multiple persistence nodes. In such case, it is the user's responsibility to set up these nodes in a way that makes sense.

While running multiple nodes, please keep in mind that these storage components will be draining the shared data structures concurrently and independently. It is entirely possible to setup EverBEEN to run two persistence nodes on two completely separate databases, but it will probably not result in any sensibly expectable behavior, as potentially related data will be scattered randomly across two isolated database instances.

Generally speaking, having multiple persistence layer nodes is only useful if you:

* Have highly limited resources for each persistence node and wish to load-balance accesses to the same database
* Have a synchronization/sharding strategy set up

Additional use-cases may arise if you decide to write your own database adapter. In that case, consult the [extension point](#user.persistence.extension) for more detail.



#### <a id="user.persistence.characteristics.cleanup">Automatic cleanup</a>
To prevent superfluous information from clogging the data storage, the Object Repository runs a Janitor component that performs database cleanup on a regular basis. The idea is to clean all old data for failed jobs and all metadata for successful jobs after a certain lifecycle period has passed. For lifecycle period and cleanup frequency adjustment, see the [janitor configuration](#user.configuration.objectrepo.janitor) section.



### <a id="user.persistence.components">Components</a>
Follows a brief description of components that contribute to forming the EverBEEN persistence layer.

* [Object Repository](#user.persistence.components.objectrepo)
* [Storage](#user.persistence.components.storage)
* [MapStore](#user.persistence.components.mapstore)



#### <a id="user.persistence.components.objectrepo">Object Repository</a>
It goes without saying that EverBEEN needs some place to store all the data your tasks will produce. That's what the Object Repository is for. Each time a task issues a command to submit a result, or logs a message, this information gets dispatched to the cluster, along with the associated object. The Object Repository provides a functional endpoint for this information. It effectively concentrates distributed data to its intended destination (a database, most likely). In addition, the Object Repository is also in charge of dispatching requested user data back.



#### <a id="user.persistence.components.storage">Storage</a>
The Storage component supplies the concrete database connector implementation. All communication between the Object Repository and the database is done through the Storage API.

The Storage component gets loaded dynamically by the Object Repository at startup. If you want to use a different database than MongoDB, this is the component you'll be replacing (along with the MapStore, potentially).



#### <a id="user.persistence.components.mapstore">MapStore</a>
Where the ObjectRepository stores user data, the MapStore is used to map EverBEEN cluster memory to a persistent storage, which enables EverBEEN to preserve job state memory through cluster-wide restarts. The MapStore runs on all *data nodes* (see [deployment](#user.deployment.nodes.types) for more information on node types).



### <a id="user.persistence.extension">Persistence extension points</a>
<!-- TODO -->
* abstractness
* * storage for unknown user types
* * why does this layer need to be abstract and dumb
* extension point (how is it meant to be extended, what is needed)
