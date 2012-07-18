@echo off

rem  BEEN: Benchmarking Environment
rem  ==============================
rem
rem  File author: David Majda
rem
rem  GNU Lesser General Public License Version 2.1
rem  ---------------------------------------------
rem  Copyright (C) 2004-2006 Distributed Systems Research Group,
rem  Faculty of Mathematics and Physics, Charles University in Prague
rem
rem  This library is free software; you can redistribute it and/or
rem  modify it under the terms of the GNU Lesser General Public
rem  License version 2.1, as published by the Free Software Foundation.
rem
rem  This library is distributed in the hope that it will be useful,
rem  but WITHOUT ANY WARRANTY; without even the implied warranty of
rem  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
rem  Lesser General Public License for more details.
rem
rem  You should have received a copy of the GNU Lesser General Public
rem  License along with this library; if not, write to the Free Software
rem  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
rem  MA  02111-1307  USA

title Task Manager

rem Starts the Task manager.

if not defined BEEN_HOME (
  if not exist "..\dist\full" (
    set BEEN_HOME=%CD%\..
  ) else (
    set BEEN_HOME=%CD%\..\dist\full
  )
)
set TASKDESCRIPTOR_DIR="%BEEN_HOME%\data\taskmanager\task-descriptors"

if defined BEEN_DEBUG (
  echo Running Task Manager with debug option...
  set BEEN_SERVICES=1
  set DEBUG_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8100,server=y,suspend=n
)

if defined BEEN_SERVICES (
  echo Will start services on first Host Runtime...
  set RUN_TASKS="%TASKDESCRIPTOR_DIR%\softwarerepository.td" "%TASKDESCRIPTOR_DIR%\hostmanager.td" "%TASKDESCRIPTOR_DIR%\benchmarkmanagerng.td" "%TASKDESCRIPTOR_DIR%\resultsrepositoryng.td" "%TASKDESCRIPTOR_DIR%\clinterface.td"
)


if "%1" == "" (
  set VERBOSITY=INFO
) else (
  set VERBOSITY=%1
)


set TASKMANAGER_JAR=%BEEN_HOME%\taskmanager.jar

set DATA_DIRECTORY=%BEEN_HOME%\data\taskmanager

if not exist %DATA_DIRECTORY% (
  echo Fatal error: Task Manager data directory not found: %DATA_DIRECTORY%.
  exit /b 1
)

set ENDORSED=-Djava.endorsed.dirs=%BEEN_HOME%\lib\jaxb

java %ENDORSED% %DEBUG_OPTS% -ea -jar "%TASKMANAGER_JAR%" %VERBOSITY% "%DATA_DIRECTORY%" %RUN_TASKS%
