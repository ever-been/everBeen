## EverBEEN services
* Host Runtime
* * how it only helps when you want to run tasks
* * why does it make sense to run nodes without it
* Software Repository
* * functional necessities (availability from all nodes)
* * why it uses HTTP and how (describe request format)
* (Results) Repository
* * queue drains
* * async persist queue
* * abstract query machinery (query queue handling, effective querying without user type knowledge)
* Task Manager
* * why you can't see it
* * the responsibility pie (and how it gets sliced automagically)
* Web Interface
* * why it's not actually a service (but more like a client)
* * cluster client connection mechanism
