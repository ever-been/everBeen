<?xml version="1.0"?>
<!--

  BEEN: Benchmarking Environment
  ==============================

  File author: Jiri Tauber

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

<!-- Task descriptor for software repository -->
<taskDescriptor xmlns="http://been.mff.cuni.cz/taskmanager/td" taskId="benchmarkmanagerng-tid" contextId="system" treeAddress="/legacy/benchmarkmanagerng" name="benchmarkmanagerng" >
	<package>
		<name>benchmarkmanagerng-1.0.bpk</name>
	</package>

	<hostRuntimes>
		<name>localhost</name>
	</hostRuntimes>

	<dependencies>
		<dependencyCheckPoint taskId="softwarerepository-tid" type="service status" value="running"/>
 	</dependencies>
</taskDescriptor>
