<?xml version="1.0"?>
<!--

  BEEN: Benchmarking Environment
  ==============================

  File author: Jan Tattermusch

  GNU Lesser General Public License Version 2.1
  - - - - - - - - - - - - - - - - - - - - - - -
  Copyright (C) 2004-2006 Distributed Systems Research Group,
  Faculty of Mathematics and Physics, Charles University in Prague

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1, as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
  MA  02111-1307  USA

-->

<taskDescriptor xmlns="http://been.mff.cuni.cz/taskmanager/td" taskId="shell-script-1" treeAddress="/example/shellscript" contextId="system" name="shell-script" >
	<taskProperties>
		<taskProperty key="script">
<![CDATA[#!/bin/bash



echo "this is example shell stdout..."

echo "content of task property TEST: $TASKPROP_TEST"

echo "working directory: $WORKING_DIR"

echo "temporary directory: $TEMP_DIR"

echo "task directory: $TASK_DIR"

echo "this is example shell stderr..." >&2

]]>
		</taskProperty>

		<taskProperty key="TEST" value="THIS_IS_TEST_VALUE"/>
	</taskProperties>
	
	<package>
		<rsl>
			name == "shell-script"
		</rsl>
	</package>
	
	<hostRuntimes>
		<name>localhost</name>
	</hostRuntimes>
	
</taskDescriptor>
