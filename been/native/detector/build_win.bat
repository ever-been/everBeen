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


:: This batch compiles HWDet3.wnm using Visual Studio command-line compiler.
:: It will always compile with the newest version of Visual Studio available.
::
:: For this batch to work you need to have VC##COMNTOOLS variable defined where
:: ## denotes version of the Visual Studio. Currently versions 7.1, 8.0 and 9.0
:: are supported. This variable should be defined by default when Visual Studio 
:: is installed. 
:: If this variable is not defined on your system, create it and set it to
:: Common7\Tools directory in your Visual Studio installation directory.
::
:: You also need to have JAVA_HOME variable set so it points to the directory
:: where JDK is installed. Tested with SUN JDK 1.5.0.6 and several newer
:: versions up to 1.6.0_03.
::
:: Output folder for temporary compilation files is obj.
:: Compiled library is then copied to the bin directory.

@echo off

:: This variable determines configuration which will be used to compile detector.
:: You can choose one of ReleaseUnicode, ReleaseANSI, DebugUnicode, DebugANSI.
:: Default is ReleaseUnicode, which is recommended for Win2K and XP since
:: unicode is native character encoding on these systems.
set DETECTOR_CONFIGURATION=ReleaseUnicode

:: This variable determines platform for which detector will be compiled.
:: It is used only when compiling with Visual Studio 2005.
:: You should not change it.
set DETECTOR_PLATFORM=Win32

:: Compile detector with Visual Studio 2008 command-line tools
if defined VS90COMNTOOLS (

	echo * Building Windows detector using Visual Studio 2008

	:: Add Java include directory to the include search path
	set INCLUDE=%JAVA_HOME%\include;%JAVA_HOME%\include\win32

	:: Prepare environment
	call "%VS90COMNTOOLS%\vsvars32.bat"	

	if errorlevel 1 goto no_vsvars
	
	:: Build
	vcbuild "HWDet3 VC 9.0.sln" "%DETECTOR_CONFIGURATION%|%DETECTOR_PLATFORM%" /useenv
	
	if errorlevel 1 (
		echo * Error building library
		
		exit 1
	)
	
	:: Copy compiled library
	copy /Y obj\HWDet3.wnd bin\
	
	goto :eof
)

:: This is defined if VC 8.0 (aka Visual Studio 2005) is installed
if defined VS80COMNTOOLS (
	
	echo * Building Windows detector using Visual Studio 2005

	:: Add Java include directory to the include search path
	set INCLUDE=%JAVA_HOME%\include;%JAVA_HOME%\include\win32

	:: Prepare environment
	call "%VS80COMNTOOLS%\vsvars32.bat"	

	if errorlevel 1 goto no_vsvars
	
	:: Build
	vcbuild "HWDet3 VC 8.0.sln" "%DETECTOR_CONFIGURATION%|%DETECTOR_PLATFORM%" /useenv
	
	if errorlevel 1 (
		echo * Error building library
		
		exit 1
	)
	
	:: Copy compiled library
	copy /Y obj\HWDet3.wnd bin\
	
	goto :eof
)

:: This is defined if VC 7.1 (aka Visual Studio .NET 2003) is installed
if defined VS71COMNTOOLS (

	echo * Building Windows Detector using Visual Studio .NET 2003
	
	:: Add Java include directory to the include search path
	set INCLUDE=%JAVA_HOME%\include;%JAVA_HOME%\include\win32
	
	:: Prepare environment
	call "%VS71COMNTOOLS%\vsvars32.bat"
	
	if errorlevel 1 goto no_vsvars
	
	:: Build
	devenv.com "HWDet3 VC 7.1.sln" /Build "%DETECTOR_CONFIGURATION%" /useenv
	
	if errorlevel 1 (
		echo * Error building library
		
		exit 1
	)
	
	:: Copy compiled library
	copy /Y obj\HWDet3.wnd bin\
	
	goto :eof
)

:: No supported compiler was found, return error
echo * No supported compiler found

exit 1

:no_vsvars
	:: Unable to execute vsvars32.bat, return error
	echo * Error: no vsvars32.bat found
	
	exit 1
