#!/bin/bash

# This is a migration tool that generates an ugly Maven hack. It scans through
# BEEN's lib folder for JARs and generates a system dependency for each. The
# output is written to stdout.

PATH_BASE='${project.basedir}'
INDENT_LEVEL=1

indent=""
for i in `seq $INDENT_LEVEL`; do
	indent="${indent}	"
done

find been/lib -name "*\.jar" | while read sysPath; do
	groupId="`echo $sysPath | sed 's/been\/lib\///; s/\/[^\/]*$//; s/\//\./'`"
	artifactId="`basename $sysPath`"
	echo "<dependency>"
	echo "	<groupId>$groupId</groupId>"
	echo "	<artifactId>$artifactId</artifactId>"
	echo "	<version>none</version>"
	echo "	<scope>system</scope>"
	echo "	<systemPath>${PATH_BASE}/${sysPath}</systemPath>"
	echo "</dependency>"
done | sed "s/^/$indent/"
