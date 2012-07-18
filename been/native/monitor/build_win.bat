::  BEEN: Benchmarking Environment
::  ==============================
::
::  File author: Branislav Repcek
::
::  GNU Lesser General Public License Version 2.1
::  ---------------------------------------------
::  Copyright (C) 2004-2006 Distributed Systems Research Group,
::  Faculty of Mathematics and Physics, Charles University in Prague
::
::  This library is free software; you can redistribute it and/or
::  modify it under the terms of the GNU Lesser General Public
::  License version 2.1, as published by the Free Software Foundation.
::
::  This library is distributed in the hope that it will be useful,
::  but WITHOUT ANY WARRANTY; without even the implied warranty of
::  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
::  Lesser General Public License for more details.
::
::  You should have received a copy of the GNU Lesser General Public
::  License along with this library; if not, write to the Free Software
::  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
::  MA  02111-1307  USA



:: This batch compiles LoadMonitor.wnm using Visual Studio command-line compiler.
:: It will always compile with the newest version of Visual Studio available.
:: Visual Studio 2005 and Visual Studio 2008 are both supported.
::
:: For this batch to work you need to have VC80COMNTOOLS or VC90COMNTOOLS
:: environment variables defined. They should be defined by default when
:: Visual Studio is installed. If they are not, make them point to the
:: Common7\Tools directory in Visual Studio installation directory.
:: You also need to have JAVA_HOME variable set so it points to the directory
:: where JDK is installed. Tested with SUN JDK 1.5.0.5 and some newer versions
:: up to version 1.6.0_03.

:: Output folder for temporary compilation files is obj (this directory is removed).
:: Compiled library is then copied to the bin directory.

@echo off

:: Build using Visual Studio 2008 command-line tools
if defined VS90COMNTOOLS (
	
	echo * Building Windows Load Monitor using Visual Studio 2008

	:: Add Java include directory to the include search path
	set INCLUDE=%JAVA_HOME%\include;%JAVA_HOME%\include\win32

	:: Prepare environment
	call "%VS90COMNTOOLS%\vsvars32.bat"	

	if errorlevel 1 goto no_vsvars
	
	:: Build
	vcbuild "LoadMonitor VC 9.0.sln" "Release|Win32" /useenv
	
	if errorlevel 1 (
		echo * Error building library
		
		exit 1
	)
	
	:: Copy compiled library
	copy /Y obj\LoadMonitor.wnm bin\
	
	goto :eof
)

:: This is defined if VC 8.0 (aka Visual Studio 2005) is installed
if defined VS80COMNTOOLS (
	
	echo * Building Windows Load Monitor using Visual Studio 2005

	:: Add Java include directory to the include search path
	set INCLUDE=%JAVA_HOME%\include;%JAVA_HOME%\include\win32

	:: Prepare environment
	call "%VS80COMNTOOLS%\vsvars32.bat"	

	if errorlevel 1 goto no_vsvars
	
	:: Build
	vcbuild "LoadMonitor VC 8.0.sln" "Release|Win32" /useenv
	
	if errorlevel 1 (
		echo * Error building library
		
		exit 1
	)
	
	:: Copy compiled library
	copy /Y obj\LoadMonitor.wnm bin\
	
	goto :eof
)

:: This is defined if VC 7.1 (aka Visual Studio .NET 2003) is installed
if defined VS71COMNTOOLS (

    echo * Visual Studio 7.1 is not supported.
    
    exit 1
)

:: No supported compiler was found, return error
echo * No supported compiler found

exit 1

:no_vsvars
	:: Unable to execute vsvars32.bat, return error
	echo * Error: no vsvars32.bat found
	
	exit 1
