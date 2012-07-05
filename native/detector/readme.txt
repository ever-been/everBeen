Compiling detectors:
 Detector can be compiled using target compile-detector from the ant build file. This target can
 compile detector using Visual Studio .NET 2003 or 2005 on Windows and using gcc/make on Linux.
 To clean all binaries use clean-detector task from the build file.
 
 To build Windows Detector you need Visual Studio 2005 or VS .NET 2003. You can open solution files
 for Windows dtector in Visual Studio 2005 (HWDet3 VC 8.0.sln) or in Visual Studio .NET 2003 
 (HWDet3 VC 7.1.sln) IDE.
 Four build configurations are provided for Windows Detector. DebugANSI, DebugUnicode, ReleaseANSI and
 ReleaseUnicode. *ANSI configurations will compile with ANSI character encoding. *Unicode configurations
 will build Unicode enabled library (this is default since on Win NT and newer Unicode is native encoding).
 To use different configuration when compiling Windows Detector see comments in build_win.bat file.
 You also need to set JAVA_HOME environment variable to point to the installation directory of Java JDK.

 To build Linux Detector you need make and gcc (3.4+).
 
 All intermediate files are placed in the obj directory which is automatically deleted when 
 compilation ends (if you use build.xml). Detector binaries for current platform are placed 
 in the bin directory (HWDet3.* files).
