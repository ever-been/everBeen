## EverBEEN configuration {#user.configuration}
* one configuration file
* how to generate one easily
* distribution via URL

### Configuration options
Follows detailed description of available configuration options of the EverBEEN
framework. Default value for each configuration option is provided

####Cluster Configuration {#user.configuration.cluster}
Cluster configuration manages how nodes will form a cluster and
how the cluster will behave. The configuration is directly mapped to
Hazelcast configuration. These options are applicable only to *DATA* nodes. <!-- TODO link to DATA node -->

It is essential that cluster nodes use the same configuration for these options, otherwise they may not form a cluster.

`been.cluster.group`=*dev*
:	Group to which the nodes belong. Nodes with different group will not form a cluster.

`been.cluster.password`=*dev-pass*
:	Password for the group. If different password is used among nodes the will not for a cluster.

`been.cluster.join`=*multicast*
:	Manages how nodes form the cluster. Two values are possible:

	* *multicast* - only `been.cluster.multicast.*` options will be used
	* *tcp* - only  `been.cluster.tcp.members` option will be used


`been.cluster.multicast.group`=*224.2.2.3*
:	Specifies multicast group to use


`been.cluster.multicast.port`=*54327*
:	Specifies multicast port to use

`been.cluster.tcp.members`=*localhost:5701*
:	Semicolon separated list of `[ip|host][:port]` nodes to connect to.


`been.cluster.port`=*5701*
:	Port on which the node will listen to.

`been.cluster.interfaces`=
:	Semicolen separated list of interfaces Hazelcast should bind to, '*' wildcard can be use, e.g. *10.0.1.**

`been.cluster.preferIPv4Stack`=*true*
:	Whether to prefer IPv4 stack over IPv6


`been.cluster.backup.count`=*1*
: How many backups should the cluster keep.


`been.cluster.logging`=*false*
: Enables/Disables logging of Hazelcast messages. Note that if enabled messages will not appear among service logs.


`been.cluster.mapstore.use`=*true*
:	Wheather to use [MapStore](#devel.services.mapstore) to persist cluster runtime information

	been.cluster.mapstore.write.delay=0

`been.cluster.mapstore.factory`=*cz.cuni.mff.d3s.been.mapstore.mongodb.MongoMapStoreFactory*
:	Implementation of the [MapStore](#devel.services.mapstore), must be on the classpath when starting a node.

`been.cluster.socket.bind.any`=*true*
:	Whether to bind to local interfaces

#### Cluster Client Configuration</a> {#user.configuration.client}
	been.cluster.client.members=localhost:5701
	been.cluster.client.timeout=120
#### Task Manager Configuration {#user.configuration.taskmanager}
	been.cluster.resubmit.maximum-allowed=10
	been.tm.scanner.delay=15
	been.tm.scanner.period=30
#### Cluster Persistence Configuration {#user.configuration.objectrepo}
	been.cluster.persistence.query-processing-timeout=5
	been.cluster.persistence.query-timeout=10

#### Persistence Janitor Configuration {#user.configuration.objectrepo.janitor}
	been.repository.janitor.finished-longevity=96
	been.repository.janitor.failed-longevity=48
	been.repository.janitor.cleanup-interval=10

#### Monitoring Configuration {#user.configuration.monitoring}
	been.monitoring.interval=5000
#### Host Runtime Configuration {#user.configuration.hostruntime}
	hostruntime.tasks.max=15
	hostruntime.tasks.memory.threshold=90
	hostruntime.wrkdir.name=.HostRuntime
	hostruntime.tasks.wrkdir.maxHistory=4
#### MapStore Configuration {#user.configuration.mapstore}
	been.cluster.mapstore.db.hostname=localhost
	been.cluster.mapstore.db.username=null
	been.cluster.mapstore.db.password=null
	been.cluster.mapstore.db.dbname=BEEN
#### Mongo Storage Configuration {#user.configuration.mongostorage}
	mongodb.password=null
	mongodb.dbname=BEEN
	mongodb.username=null
	mongodb.hostname=localhost
#### File System Based Store Configuration {#user.configuration.fsbasedstorage}
	hostruntime.swcache.folder=.swcache
	swrepository.persistence.folder=.swrepository
	hostruntime.swcache.maxSize=1024
#### Software Repository Configuration {#user.configuration.swrepo}
	swrepository.port=8000
	swrepository.serviceInfoDetectionPeriod=30
	swrepository.serviceInfoTimeout=45
