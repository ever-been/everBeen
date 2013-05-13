# TODO

* Write README.md (used on jenkins build page and github project main page)

* Benchmark Manager
    * If resubmitting a benchmark too often (more than x times in an hour), don't try
      any more
    * When resubmitting a benchmark, what should the behavior be? Should we wait for
      running contexts to finish?
* Results & Storage
    * Storage information (in which db should results be stored, etc.)
	* What should be persisted when you completely shutdown the cluster? Benchmark entries? Benchmark's storage?
	  What else?
	* Retrieving results from a user task
	* Allow a special user task (evaluator) to return a "file" (ZIP, PNG, CSV, ...)
* Context Planning & Dependencies
	* Currently, we have "submit context, wait for it to finish" behavior. What other
	  cases should we have? Context queues? Running up to X context simultaneously?
* Software Repository
	* Implement "SNAPSHOT" versioning (don't cache)
	* Implement "empty cache" feature
* Web Interface
	* Edit properties when submitting (+ specify debug)
	* \*.\*
* `find . -type f | xargs grep TODO`

* Win32 support
* Native task API (task, benchmark)


# Wish-list
* Allow persistence & retrieval of "big files", or "lots of records" generated by user tasks
* Solve "error reporting" (onContextFailed event) for Benchmarks
