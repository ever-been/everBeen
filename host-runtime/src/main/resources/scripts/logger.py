'''

   The purpose of this python script is to add support for
   transport log messages from running task to running
   instance of Host Runtime. 

   USAGE:
	 python log.py -t "this is a trace msg"
	 python log.py -d "this is a debug msg"
	 python log.py -i "this is an info msg"
	 python log.py -w "this is a warn msg"
	 python log.py -e "this is an error msg"
	 
   PREREQUISITIES:
	 python
	 pyzmq python module ('pip install pyzmq', 'easy_install pyzmq' etc)
	 
	 
	 
	 
	 @author:  Tadas Palusga
	 @version: 1.0

'''


''' IMPORTS '''
import sys
import os
import zmq
import time
import json


''' CONSTANTS '''

PORT_ENVPROP_NAME = "TaskLogs"

TRACE = 1
DEBUG = 2
INFO = 3
WARN = 4
ERROR = 5


''' 
   Sends msg with appropriate log level to running Host Runtime via zeromq library.
   Message is constructed as JSON object in following format (real JSON object
   is constructed without spaces):
   
   {
	   logLevel:  {X},
	   msg:   "msg body",
	   senderId:  "{SENDER_ID}"
   }
   
   where {X} is the appropriate log level (see constants defined earlier in this script)
   and {SENDER_ID} is obtained from environment variable (for name of this property see
   constants defined earlier in this script)
   
   address of target socket is
	tcp://localhost:PORT
   where PORT is obtained from from environment variable (for name of this property see
   constants defined earlier in this script)
   
'''
def send(logLevel, msg):
	# construct the msg in proper json format

	taskId = os.environ.get("been.task.id")
	contextId = os.environ.get("been.context.id")
	benchmarkId = os.environ.get("been.benchmark.id")
	millis = int(round(time.time() * 1000))

	taskLogMsg = [{'created':str(millis),'taskId':str(taskId),'contextId': str(contextId),'benchmarkId':str(benchmarkId),'message':{'name': 'logger','level': '999','message': msg.strip(),'errorTrace':'null','threadName':'main'}}]
	data_string = json.dumps(taskLogMsg)



	# open socket connection to running host runtime
	context = zmq.Context()

	# The default LINGER is -1, which means wait until all
	# messages have been sent before allowing termination.
	# Set to 0 to discard unsent messages immediately,
	# and any positive integer will be the number of
	# milliseconds to keep trying to send before discard.
	# TODO not sure we want to do this, causes problems for me: cannot find setsockopt
	# context.setsockopt(zmq.LINGER, 1000)

	sender = context.socket(zmq.PUSH)
	
	port = os.environ.get(PORT_ENVPROP_NAME)
	sender.connect(port)
 
	sender.send('LOG_MESSAGE#' + data_string)

	sender.close()
	context.term()

''' 
   MAIN EXECUTION LOGIC 
'''
if len(sys.argv) == 1:
	send(WARN, "Undefined operation in python task API for logging. Check your task implementation.")
	sys.exit()	
	
msgType = sys.argv[1]

if len(sys.argv) >= 3:
	msg = sys.argv[2]
elif sys.stdin.isatty():
	send(WARN, "Invalid usage of python task API for logging. Reading from connected tty device (interactive keyboard input)) is not supported.")
	sys.exit()
else:
	msg = sys.stdin.read()

if msgType == "-i":
	send(INFO, msg)
elif msgType == "-d":
	send(DEBUG, msg)
elif msgType == "-w":
	send(WARN, msg)
elif msgType == "-e":
	send(ERROR, msg)
elif msgType == "-t":
	send(TRACE, msg)
else:
	send(WARN, "Unrecognized operation '" + msgType + "' in python task API for logging. Check your task implementation.")
