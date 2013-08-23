## <a id="user.configuration">EverBEEN configuration</a>
* one configuration file
* how to generate one easily
* distribution via URL

### Configuration options

####<a id="user.configuration.cluster">Cluster Configuration</a>
	been.cluster.group=dev
	been.cluster.multicast.port=54327
	been.cluster.preferIPv4Stack=true
	been.cluster.mapstore.use=true
	been.cluster.multicast.group=224.2.2.3
	been.cluster.backup.count=1
	been.cluster.mapstore.write.delay=0
	been.cluster.mapstore.factory=cz.cuni.mff.d3s.been.mapstore.mongodb.MongoMapStoreFactory
	been.cluster.port=5701
	been.cluster.tcp.members=localhost:5701
	been.cluster.interfaces=
	been.cluster.logging=false
	been.cluster.join=multicast
	been.cluster.password=dev-pass
	been.cluster.socket.bind.any=true
####<a id="user.configuration.client">Cluster Client Configuration</a>
	been.cluster.client.members=localhost:5701
	been.cluster.client.timeout=120
####<a id="user.configuration.taskmanger">Task Manager Configuration</a>
	been.cluster.resubmit.maximum-allowed=10
	been.tm.scanner.delay=15
	been.tm.scanner.period=30
####<a id="user.configuration.objectrepo">Cluster Persistence Configuration</a>
	been.cluster.persistence.query-processing-timeout=5
	been.cluster.persistence.query-timeout=10
#### <a id="user.configuration.monitoring">Monitoring Configuration</a>
	been.monitoring.interval=5000
#### <a id="user.configuration.hostruntime">Host Runtime Configuration</a>
	hostruntime.tasks.max=15
	hostruntime.tasks.memory.threshold=90
	hostruntime.wrkdir.name=.HostRuntime
	hostruntime.tasks.wrkdir.maxHistory=4
#### <a id="user.configuration.mapstore">MapStore Configuration</a>
	been.cluster.mapstore.db.hostname=localhost
	been.cluster.mapstore.db.username=null
	been.cluster.mapstore.db.password=null
	been.cluster.mapstore.db.dbname=BEEN
#### <a id="user.configuration.mongostorage">Mongo Storage Configuration</a>
	mongodb.password=null
	mongodb.dbname=BEEN
	mongodb.username=null
	mongodb.hostname=localhost
#### <a id="user.configuration.fsbasedstorage">File System Based Store Configuration</a>
	hostruntime.swcache.folder=.swcache
	swrepository.persistence.folder=.swrepository
	hostruntime.swcache.maxSize=1024
#### <a id="user.configuration.swrepo">Software Repository Configuration</a>
	swrepository.port=8000
	swrepository.serviceInfoDetectionPeriod=30
	swrepository.serviceInfoTimeout=45
