#!/bin/bash
# script for distributing compiled been to virtual farm :-)
# prealpha version, use at your own risk.


BEEN_FILES="bin data dist lib native resources"

#echo "cleaning and building"

cd ~/willbeen
#ant clean
#ant all

echo "copying data to albatross"
rsync --delete -v -a -e 'ssh -p 22252' $BEEN_FILES been@charon.podzimek.org:/home/been/willbeen-xampler 	#copy data to albatross

ssh -p 22252 been@charon.podzimek.org './distribute.sh'


#gnome-terminal --title "BEEN Virtual Taskmanager (debug)" -x /bin/bash -i -c "ssh $albatross 'cd willbeen-xampler/bin; killall taskmanager.sh; sleep 1;./taskmanager-debug.sh'"

#sleep 10;

#for host in "$albatross" "$nightingale" "$skylark" "$sparrow" "$starling"
#do
#gnome-terminal --title "BEEN Virtual Hostruntime (debug)" -x /bin/bash -i -c "ssh $albatross 'cd willbeen-xampler/bin; killall hostruntime.sh; sleep 2;./hostruntime.sh albatross'"
#done

#firefox 'https://charon.podzimek.org:8080/been/'


