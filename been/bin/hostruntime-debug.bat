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

set BEEN_DEBUG=1

hostruntime.bat %* 

