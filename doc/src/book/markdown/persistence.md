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
As mentioned above, EverBEEN comes with a default persistence solution for MongoDB. We realize, however, that this might not be the ideal use-case for everyone. Therefore, the MongoDB persistence layer is fully replaceable if you provide your own database implementation.

There are two components you might want to override - the [Storage](#user.persistence.extension.storage) and the [MapStore](#user.persistence.extension.mapstore).

If your goal is to relocate EverBEEN user data (benchmark results, logs etc.) to your own database and don't mind running a MongoDB as well for EverBEEN service data, you'll be fine just overriding the [Storage](#user.persistence.extension.storage). If you want to completely port all of EverBEEN's persistence, you'll have to override the [MapStore](#user.persistence.extension.mapstore) as well.

#### <a id="user.persistence.extension.storage">Storage</a>
As declared above, the *Storage* component is fully replacable by a different implementation than the default MongoDB adapter. However, we don't feel comfortable with letting you plunge into this extension point override without a few warnings first.

##### <a id="user.persistence.extension.storage.warning">Override warning</a>
 The issue with *Storage* implementation is that the persistence layer is designed to be completely devoid of any type knowledge. The reason for this is that *Storage* is used to persist and retrieve objects from user tasks. Should the *Storage* have any RTTI knowledge of the objects it works with, imagine what problems could arise when two tasks using two different versions of the same objects would attempt to use the same *Storage*.

To avoid this, the *Storage* only receives the object JSON and some information about the object's placement. This being said, the *Storage* still needs to perform effective querying based on some attributes of the objects it is storing.

This is generally not an issue with NoSQL databases or document-oriented stores, but it can be quite hard if you use a traditional ORM. The ORM approach additionally presents the aforementioned class version problem, which you would need to solve somehow. If ORM is the way you want to go, be prepared to run into the following:

* **EverBEEN classes** - You will probably need to map some of these in your ORM
* **User types** - You will likely need to share a user-type library with your co-developers to consent on permitted result objects
* **User type versions** - Should the version of this user-type library change, you will need to restart the *Storage* before running any new tasks on EverBEEN. Restarting EverBEEN will likely result in the dysfunction of tasks using an older version of the user-type library


##### <a id="user.persistence.extension.storage.overview>Override implementation overview</a>
If your intention is not to use ORM for *Storage* implementation, or you have really thought the consequences through, keep reading. To successfully replace the *Storage* implementation, you'll need to implement the following:

* [Storage](#)
* [StorageBuilder](#)

<!-- TODO javadoc link -->
Additionally, you'll need to create a **META-INF/services** folder in the jar with your implementation, and place a file named **cz.cuni.mff.d3s.been.storage.StorageBuilder** in it. You'll need to put a single line in that file, containing the full class name of your [StorageBuilder](#) implementation.

We also strongly recommend that you implement these as well:

* [QueryRedactorFactory](#) (along with [QueryRedactor](#) implementations)
* [QueryExecutorFactory](#) (along with [QueryExecutor](#) implementations)

The general idea is for you to implement the *Storage* component and to provide the *StorageBuilder* service, which configures and instantiates your *Storage* implementation. The **META-INF/services** entry is for the *ServiceLoader* EverBEEN uses to recognize your *StorageBuilder* implementation on the classpath. EverBEEN will then pass the *Properties* from the *been.conf* file (see [configuration](#user.configuration)) to your *StorageBuilder*. That way, you can use the common property file for your *Storage*'s configuration.

<!-- TODO javadoc link -->
The [Storage](#) interface is the main gateway between the [Object Repository]("user.persistence.components.objectrepo") and the database. When overriding the Storage, there will be two major use-cases you'll have to implement: the [asynchronous persist](#user.persistence.extension.storage.asyncper) and the [synchronous query](#user.persistence.extension.storage.qa).

##### <a id="user.persistence.extension.storage.asyncper">Asynchronous persist</a>
<!-- TODO javadoc link -->
All *persist* requests in EverBEEN are funneled through the [store](#) method. You'll receive two parameters in this method:

<a id="user.persistence.extension.storage.asyncper.eid">***entityId***</a>
The *entityId* is meant to determine the location of the stored entity. For example, if you're writing an SQL adapter, it should determine the table where the entity will be stored. For more information on the *entityId*, see [persistent object info](#user.persistence.extension.storage.objectinfo)


<a id="user.persistence.extension.storage.asyncper.json">***JSON***</a>
A serialized JSON representation of the object to be stored.

Generally, you'll need to decide where to put the object based on its *entityId* and then somehow map and store it using its *JSON*.

<!-- TODO javadoc link -->
The [store](#) method is asynchronous. It doesn't return any outcome information, but be sure to throw a *DAOException* when the persist attempt fails. That way, you'll make sure the *ObjectRepository* knows that the operation failed and will take action to prevent data loss.

##### <a id="user.persistence.extension.storage.qa">Query / Answer</a>
<!-- TODO AQL explanation -->

##### <a id="user.persistence.extension.storage.objectinfo">General persistent object info</a>
Although the *Storage* doesn't implicitly know any RTTI on the object it's working with, there are some safe assumptions you can make based on the *entityId* that comes with the object.

The *entityId* is composed of *kind* and *group*. The *kind* is supposed to represent what the persisted object actually is (e.g. a log message). These kinds are currently recognized by EverBEEN:

* **log** - log messages and host load monitoring
* **result** - stored task results
* **descriptor** - *task*/*context* configurations; used to store parameters with which a *task* or *context* was run
* **named-descriptor** - *task*/*context* configurations; user-stored configuration templates for *task* or *context* runs
<!-- TODO javadoc link -->
* **evaluation** - output of evaluations performed on task results; these objects contain serialized BLOBs - see [evaluations](#) for more detail
* **outcome** - meta-information about the state and outcome of jobs in EverBEEN; these are used in automatic cleanup

The *group* is supposed to provide a more granular grouping of objects and depends entirely on the object's *kind*.

If you need more detail on objects that you can encounter, be sure to also read the [ORM special](#user.persistence.extension.storage.ormspecial), which denotes what EverBEEN classes can be expected where and what *entityIds* can carry user types.

##### <a id="user.persistence.extension.storage.ormspecial">The ORM special</a>
<!-- TODO list needed mav modules -->
<!-- TODO list mapping to EverBEEN classes -->

#### <a id="user.persistence.extension.mapstore">MapStore</a>
<!-- TODO describe extension point -->
