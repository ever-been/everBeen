## Used technologies
The reasoning why we chose this or that tech is already done in the decision timeline, this should be more of a list of all the stuff we used and what we used it for (including the technologies that have no real repercussions on the project but we just needed them). Consider a table with a lot of fancy logos...

### <a id="devel.techno.hazelcast">Hazelcast</a>

### <a id="devel.techno.zmq">0MQ</a>
[0MQ](http://zeromq.org/) is a message passing library which can also
act as a concurrency framework. It supports many advanced features. Best
source to learn more about the library is the [0MQ Guide](http://zguide.zeromq.org/):

The EverBEEN team chose the library as the primary communication technology between a Host Runtime and its tasks, especially because of:

* focus on message passing
* multi-platform support
* ease-of-use compared to plain sockets
* extensive list of language bindings
* support for different message passing patters
* extensive documentation

We decided to use the [Pure Java implementation of libzmq](https://github.com/zeromq/jeromq)
because of easier integration with the project without the need to either compile
the C library for each supported platform or add external dependency on it.

As an experiment the [Task Manager](#devel.services.taskmanager)'s internal communication
has been implemented on top of the library as well using the inter-process communication
protocol, somewhat resembling the Actor concurrency model.

### <a id="devel.techno.maven">Apache Maven</a>

### <a id="devel.techno.exec">Apache Commons Exec</a>
The previous version of the BEEN framework chose to implement executing of tasks
using basic primitives found in the Java SE (which is known to be hard).
The realization was buggy, confusing and fragile. Instead of re-inventing the
wheel once more the team decided to use time-proven [Apache Commins Exec](http://commons.apache.org/proper/commons-exec/) library.

### <a id="devel.techno.commons">Apache Commons</a>

*  (virtually everything around IO and compression)

### <a id="devel.techno.http">Apache HTTP Core/Components</a>

*  (HTTP server)

### <a id="devel.techno.bootstrap">Bootstrap</a>

* (cool skins, save time)

### <a id="devel.techno.hazelcast">Hazelcast</a>
### <a id="devel.techno.zmq">0MQ</a>


### <a id="devel.techno.jackson">Jackson </a>

* (JSON serialization for inter-process data transport and user type abstraction)

### <a id="devel.techno.jaxb">JAXB</a>

*  (serializable POJO generation)

### <a id="devel.techno.logback">Logback (logging impl)</a>
### <a id="devel.techno.mongodb">MongoDB</a>

*  (store all kinds of stuff)

### <a id="devel.techno.slf4j">SLF4J</a>

* (logging unification of custom logging implementations and standard libraries)

### <a id="devel.techno.tapestry">Tapestry</a>

### Other
* I definitely forgot about a half of these, feel free to complete this, just maintain the cool alphabetic ordering
