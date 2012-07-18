#  BEEN: Benchmarking Environment
#  ==============================
#
#  File author: Branislav Repcek
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

# This file will build Linux Load Monitor library

# It needs g++ (no idea about version, tested with 4.1).
# You need to have Java JDK installed and JAVA_HOME environment 
# variable should point to the installation of the JDK.
# Tested with SUN Java JDK 1.5.0_09. 

# Compiler flags
CXXFLAGS=-O2 -fPIC -pipe -pedantic -Wall -Wno-long-long -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux

# Linker flags
LDFLAGS=-shared

# Directory with resulting library
BINDIR=bin

# Directory with intermediate object files
OUTDIR=obj

# Directory containing common source and header files
CMNDIR=common

# Directory with Linux specific source and header files
SRCDIR=linux

# Default target
all:$(OUTDIR) $(BINDIR) $(BINDIR)/LoadMonitor.lnm objclean
	

objclean:
	rm -rf $(OUTDIR)

clean: objclean
	rm -f $(BINDIR)/LoadMonitor.lnm

$(BINDIR):
	mkdir $(BINDIR)

$(OUTDIR):
	mkdir $(OUTDIR)

$(BINDIR)/LoadMonitor.lnm:$(OUTDIR)/LoadMonitor.o $(OUTDIR)/LoadSample.o $(OUTDIR)/HardwareDescription.o $(OUTDIR)/LoadMonitorNative.o
	g++ $(LDFLAGS) -lrt -o $(BINDIR)/LoadMonitor.lnm $(OUTDIR)/*.o

$(OUTDIR)/LoadMonitorNative.o:$(SRCDIR)/LoadMonitorNative.cpp $(CMNDIR)/JavaVoidMethodCaller.h \
  $(CMNDIR)/String.h $(CMNDIR)/TypeList.h $(CMNDIR)/CompileTimeCheck.h $(SRCDIR)/LoadMonitorNative.h \
  $(OUTDIR)/LoadMonitor.o $(OUTDIR)/LoadSample.o $(OUTDIR)/HardwareDescription.o \
  $(CMNDIR)/CompileTimeCheck.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/LoadMonitorNative.o $(SRCDIR)/LoadMonitorNative.cpp

$(OUTDIR)/LoadMonitor.o: $(OUTDIR)/HardwareDescription.o $(OUTDIR)/LoadSample.o $(SRCDIR)/LoadMonitor.cpp \
  $(CMNDIR)/String.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/LoadMonitor.o $(SRCDIR)/LoadMonitor.cpp

$(OUTDIR)/HardwareDescription.o: $(CMNDIR)/HardwareDescription.cpp $(CMNDIR)/String.h $(CMNDIR)/HardwareDescription.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/HardwareDescription.o $(CMNDIR)/HardwareDescription.cpp

$(OUTDIR)/LoadSample.o: $(CMNDIR)/LoadSample.cpp $(CMNDIR)/LoadSample.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/LoadSample.o $(CMNDIR)/LoadSample.cpp
