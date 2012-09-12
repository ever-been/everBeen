#!/bin/bash

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

# Starts the Task Manager
export BEEN_HOME=`pwd`
echo "BEEN_HOME=$BEEN_HOME";

TASKMANAGER_JAR="${BEEN_HOME}/taskmanager/target/taskmanager.one-jar.jar"
TASKDESCRIPTOR_DIR="${BEEN_HOME}/resources/task-descriptors"

if [ -n "${BEEN_DEBUG}" ]; then
	echo "Running Task Manager with debug option..."
	BEEN_SERVICES="1"
	DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8100,server=y,suspend=n"
else
	DEBUG_OPTS="-XX:+HeapDumpOnOutOfMemoryError"
fi

if [ -n "${BEEN_SERVICES}" ]; then
	echo "Will start services on first Host Runtime..."
	RUN_TASKS=("${TASKDESCRIPTOR_DIR}/softwarerepository.td" "${TASKDESCRIPTOR_DIR}/hostmanager.td" "${TASKDESCRIPTOR_DIR}/benchmarkmanagerng.td" "${TASKDESCRIPTOR_DIR}/resultsrepositoryng.td" "${TASKDESCRIPTOR_DIR}/clinterface.td")
fi	

if [ -z $1 ]; then
	VERBOSITY=INFO
else
	VERBOSITY=$1
fi

DATA_DIRECTORY="${BEEN_HOME}/data/taskmanager"

if [ ! -d "${DATA_DIRECTORY}" ]; then
  echo "Fatal error: Task Manager data directory not found: ${DATA_DIRECTORY}."
fi

java ${DEBUG_OPTS} -ea -jar $TASKMANAGER_JAR $VERBOSITY "${DATA_DIRECTORY}" ${RUN_TASKS[@]}
