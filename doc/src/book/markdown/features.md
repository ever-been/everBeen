## Principal features
* scalability
* * you can actually add and remove nodes to improve shared memory and computational capacity
* * you can do it without messing everything up instantly
* * you can (presumably) have multiple persistence end-points over multiple Mongo shards
* user type transparency
* * you can put anything in a result and it gets serialized anyway
* * BEEN doesn't even care
* * If you change a result's version, BEEN doesn't explode in your face (although your task might, if you're not careful)
* extensibility - you can classpath-swap implementations of these:
* * logging
* * persistence
* * software cache
* * software store
* ease deployment
* * deployment is reasonably easy (once you configure the cluster, that is)
* * easy configuration (all in one place and you can generate the file)
* * remote configuration (load config from a URL)
* * the web interface manipulation is pretty straightforward
* easy measures
* * task implementation time significantly reduced
* * task contexts templating allows for quick customization
* * configurable benchmarks with a straightforward goal - task context creation
* straightforward in-task result manipulation
* * the user doesn't have to worry about serialization (if he uses Java) - he works with his own types
* * decent retrieval API that hides all the cluster hassle
* ...
* feel free to add more, there's never enough glory
