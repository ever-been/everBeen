#!/bin/bash

ant clean
ant deploy

gnome-terminal --title "BEEN Taskmanager (debug)" -x /bin/bash -i -c 'cd bin; ./taskmanager-debug.sh'
sleep 3; gnome-terminal --title "BEEN Hostruntime (debug)" -x /bin/bash -i -c 'cd bin; ./hostruntime-debug.sh'

firefox "http://localhost:8180/been/services/list/"
