client-*: Test programs
	- client-submitter: submits a task from a Task Descriptor


core-*: Core data structures / utility functions
	- core-date: core data structures
		- JAXB

	- core-utils: core utility functions

	- core-protocol: host runtime api / messages / protocol


task-*: Task related modules
	- task-runner: spawns a task
	- task-manager: manages tasks


node: That's where the fun starts
	- starts task-manager and/or runtime


host-runtime: self-explanatory name

software-repository: self-explanatory name