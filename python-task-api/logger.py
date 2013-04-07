'''

   The purpose of this python script is to add support for
   transport log messages from running task to running
   instance of Host Runtime. 

   USAGE:
     python log.py --trace "this is a trace message"
     python log.py --debug "this is a debug message"
     python log.py --info  "this is an info message"
     python log.py --warn  "this is a warn message"
     python log.py --error "this is an error message"

'''


''' IMPORTS '''
import sys
import os
import zmq



''' CONSTANTS '''
TASK_ID_ENVPROP_NAME = "BEEN_TASK_ID"
PORT_ENVPROP_NAME = "BEEN_PORT"

LOG_LEVEL_TRACE = 1
LOG_LEVEL_DEBUG = 2
LOG_LEVEL_INFO = 3
LOG_LEVEL_WARN = 4
LOG_LEVEL_ERROR = 5


''' MESSAGE CREATOR METHOD '''
def sendMessage(logLevel, message):
    senderId = os.environ.get(TASK_ID_ENVPROP_NAME)
    jsonMessage = "{logLevel:" + str(logLevel) + ",message:\"" + message + "\",senderId:\"" + str(senderId) + "\"}"
    
    context = zmq.Context()
    sender = context.socket(zmq.PUSH)
    
    port = os.environ.get(PORT_ENVPROP_NAME)
    sender.connect("tcp://localhost:" + str(port))
 
    sender.send(jsonMessage)
    sender.close()
    context.term();

''' EXECUTION LOGIC '''
msgtype = sys.argv[1]
message = sys.argv[2]

if msgtype == "--info":
    sendMessage(LOG_LEVEL_INFO, message)
elif msgtype == "--debug":
    sendMessage(LOG_LEVEL_DEBUG, message)
elif msgtype == "--warn":
    sendMessage(LOG_LEVEL_WARN, message)
elif msgtype == "--error":
    sendMessage(LOG_LEVEL_ERROR, message)
elif msgtype == "--trace":
    sendMessage(LOG_LEVEL_TRACE, message)
else:
    sendMessage(LOG_LEVEL_WARN, "Unrecognized operation '" + msgtype + "' in python task API. Check your task implementation.")
