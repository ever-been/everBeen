## Decision timeline
* Apache Maven (vs. Apache Ant)
* * a no-brainer - the project was so big it just needed modules
* * describe mavenization attempts
* SLF4J (vs. nothing, really)
* * another no-brainer, as it's the only way to enable implementation swapping
* Hazelcast (vs. JMS, JGroups) over a generic cluster API
* * JMS had SPOF and JGroups was too low-level
* Full overhaul
* * attempts on refactoring standing RMI code to use Hazelcast were catastrophic
* How generic cluster API got impossible
* * would disable access to virtually every high-level function of the current cluster tech
* Separate services from tasks
* * chicken/egg problem with software bundles
* HTTP protocol for SWRepo
* * likeliness of large file transport (obsolete decision from current POV)
* Jackson as a serialization library
* * Universal serialization was needed
* * JSON is more traffic-economic than XML
* * Jackson has got cool mapping features for objects
* 0MQ for inter-process communication
* * It will probably take MS to describe why 0MQ (srsly IDK :D)
* MongoDB as storage
* * Full abstraction of user types (to avoid byte-code trafficking nonsense)
* * We were using JSON anyway
* * MongoDB is web-scale :D (OK srsly not this one, but I had to put it here just for kicks)
* Tapestry as WI framework
* * Less code time than pure JSP and TP knew how to use it
