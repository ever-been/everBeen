#!/bin/sh

#  BEEN: Benchmarking Environment
#  ==============================
#
#  File author: David Majda
#
#  GNU Lesser General Public License Version 2.1
#  ---------------------------------------------
#  Copyright (C) 2004-2006 Distributed Systems Research Group,
#  Faculty of Mathematics and Physics, Charles University in Prague
#
#  This library is free software; you can redistribute it and/or
#  modify it under the terms of the GNU Lesser General Public
#  License version 2.1, as published by the Free Software Foundation.
#
#  This library is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#  Lesser General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public
#  License along with this library; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
#  MA  02111-1307  USA

# Starts the Host Runtime

if [ -n "${BEEN_DEBUG}" ]; then
	echo "Running Host Runtime with debug option..."
	DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8101,server=y,suspend=n"
else
	DEBUG_OPTS="-XX:+HeapDumpOnOutOfMemoryError"
fi

if [ ! -z $* ]; then
  		TASK_MANAGER_HOSTNAME=$1
else
  		TASK_MANAGER_HOSTNAME=localhost
fi

if [ -z "${BEEN_HOME}" ]; then
  
  if [ -e "../dist/full" ]; then
	cd ..
	export BEEN_HOME=`pwd`/dist/full
	cd bin
  else
	cd ..
	export BEEN_HOME=`pwd`
	cd bin
  fi

fi
echo "BEEN_HOME=$BEEN_HOME";

HOSTRUNTIME_JAR="${BEEN_HOME}/hostruntime.jar"

DATA_DIRECTORY="${BEEN_HOME}/data/hostruntime"

if [ ! -d "${DATA_DIRECTORY}" ]; then
  echo "Fatal error: Host Runtime data directory not found: ${DATA_DIRECTORY}."
  exit 1
fi

ENDORSED="-Djava.endorsed.dirs=${BEEN_HOME}/lib/jaxb"

java ${ENDORSED} ${DEBUG_OPTS} -ea -jar $HOSTRUNTIME_JAR "${TASK_MANAGER_HOSTNAME}" "${DATA_DIRECTORY}"
