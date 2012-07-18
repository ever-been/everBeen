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

# This file will build Linux detector library

# It needs g++ (no idea about version, 3.4 should be enough).
# You need to have Java JDK installed and JAVA_HOME environment 
# variable should point to the installation of the JDK.
# Tested with SUN Java JDK 1.5.0_06.

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
all:$(OUTDIR) $(BINDIR) $(BINDIR)/HWDet3.lnd objclean
	

objclean:
	rm -rf $(OUTDIR)

clean:objclean
	rm -f $(BINDIR)/HWDet3.lnd

$(BINDIR):
	mkdir $(BINDIR)

$(OUTDIR):
	mkdir $(OUTDIR)

$(BINDIR)/HWDet3.lnd:$(OUTDIR)/LinuxDetector.o $(OUTDIR)/NativeDetector.o \
  $(OUTDIR)/LinuxAdvancedFeatures.o $(OUTDIR)/BasicFeatures.o $(OUTDIR)/BeenDisk.o \
  $(OUTDIR)/DiskDrive.o $(OUTDIR)/DiskPartition.o $(OUTDIR)/Memory.o \
  $(OUTDIR)/Processor.o $(OUTDIR)/Product.o $(OUTDIR)/UnicodeString.o $(OUTDIR)/FileParser.o \
  $(OUTDIR)/NetworkInterfaceName.o $(OUTDIR)/NetworkAdapter.o $(OUTDIR)/SystemHelper.o \
  $(OUTDIR)/ForkAndPipe.o $(SRCDIR)/DistributionRegistry.h $(SRCDIR)/Distribution.h \
  $(SRCDIR)/DefaultDistribution.h $(CMNDIR)/MessageReporter.h $(SRCDIR)/Packager.h \
  $(SRCDIR)/DefaultPackager.h $(OUTDIR)/RPMPackager.o $(OUTDIR)/FedoraCoreDistribution.o \
  $(OUTDIR)/GentooDistribution.o $(OUTDIR)/EqueryPackager.o $(OUTDIR)/GentooFindPackager.o
	g++ $(LDFLAGS) -o $(BINDIR)/HWDet3.lnd $(OUTDIR)/*.o

$(OUTDIR)/LinuxAdvancedFeatures.o:$(SRCDIR)/LinuxAdvancedFeatures.h $(SRCDIR)/LinuxAdvancedFeatures.cpp \
  $(CMNDIR)/AdvancedFeatures.h $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/LinuxAdvancedFeatures.o $(SRCDIR)/LinuxAdvancedFeatures.cpp

$(OUTDIR)/LinuxDetector.o:$(SRCDIR)/LinuxDetector.h $(SRCDIR)/LinuxDetector.cpp \
  $(CMNDIR)/Detector.h $(CMNDIR)/XMLFileOutput.h $(OUTDIR)/UnicodeString.o \
  $(OUTDIR)/Processor.o $(OUTDIR)/Memory.o $(OUTDIR)/DiskDrive.o $(OUTDIR)/DiskPartition \
  $(OUTDIR)/BeenDisk.o $(OUTDIR)/NetworkAdapter.o $(OUTDIR)/FileParser.o \
  $(SRCDIR)/DistributionRegistry.h $(SRCDIR)/Distribution.h $(OUTDIR)/FedoraCoreDistribution.o \
  $(OUTDIR)/GentooDistribution.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/LinuxDetector.o $(SRCDIR)/LinuxDetector.cpp

$(OUTDIR)/NativeDetector.o:$(SRCDIR)/NativeDetector.h $(SRCDIR)/NativeDetector.cpp \
  $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/NativeDetector.o $(SRCDIR)/NativeDetector.cpp

$(OUTDIR)/BasicFeatures.o:$(CMNDIR)/BasicFeatures.cpp $(CMNDIR)/BasicFeatures.h \
  $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/BasicFeatures.o $(CMNDIR)/BasicFeatures.cpp

$(OUTDIR)/BeenDisk.o:$(CMNDIR)/BeenDisk.cpp $(CMNDIR)/BeenDisk.h $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/BeenDisk.o $(CMNDIR)/BeenDisk.cpp

$(OUTDIR)/DiskDrive.o:$(CMNDIR)/DiskDrive.h $(CMNDIR)/DiskDrive.cpp $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/DiskDrive.o $(CMNDIR)/DiskDrive.cpp

$(OUTDIR)/DiskPartition.o:$(CMNDIR)/DiskPartition.h $(CMNDIR)/DiskPartition.cpp $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/DiskPartition.o $(CMNDIR)/DiskPartition.cpp

$(OUTDIR)/Memory.o:$(CMNDIR)/Memory.h $(CMNDIR)/Memory.cpp $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/Memory.o $(CMNDIR)/Memory.cpp

$(OUTDIR)/Processor.o:$(CMNDIR)/Processor.cpp $(CMNDIR)/Processor.h $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/Processor.o $(CMNDIR)/Processor.cpp

$(OUTDIR)/Product.o:$(CMNDIR)/Product.cpp $(CMNDIR)/Product.h $(CMNDIR)/XMLFileOutput.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/Product.o $(CMNDIR)/Product.cpp

$(OUTDIR)/UnicodeString.o:$(CMNDIR)/UnicodeString.h $(CMNDIR)/UnicodeString.cpp
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/UnicodeString.o $(CMNDIR)/UnicodeString.cpp

$(OUTDIR)/FileParser.o:$(SRCDIR)/FileParser.h $(SRCDIR)/FileParser.cpp $(OUTDIR)/UnicodeString.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/FileParser.o $(SRCDIR)/FileParser.cpp

$(OUTDIR)/NetworkAdapter.o:$(CMNDIR)/NetworkAdapter.h $(CMNDIR)/NetworkAdapter.cpp $(OUTDIR)/UnicodeString.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/NetworkAdapter.o $(CMNDIR)/NetworkAdapter.cpp

# $(OUTDIR)/FileSystemName.o:$(SRCDIR)/FileSystemName.h $(SRCDIR)/FileSystemName.cpp $(OUTDIR)/UnicodeString.o
#	g++ -c $(CXXFLAGS) -o $(OUTDIR)/FileSystemName.o $(SRCDIR)/FileSystemName.cpp

$(OUTDIR)/NetworkInterfaceName.o:$(SRCDIR)/NetworkInterfaceName.h $(SRCDIR)/NetworkInterfaceName.cpp \
  $(OUTDIR)/UnicodeString.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/NetworkInterfaceName.o $(SRCDIR)/NetworkInterfaceName.cpp

$(OUTDIR)/ForkAndPipe.o:$(SRCDIR)/ForkAndPipe.h $(SRCDIR)/ForkAndPipe.cpp $(OUTDIR)/SystemHelper.o \
  $(CMNDIR)/MessageReporter.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/ForkAndPipe.o $(SRCDIR)/ForkAndPipe.cpp

$(OUTDIR)/SystemHelper.o:$(SRCDIR)/SystemHelper.cpp $(SRCDIR)/SystemHelper.h $(OUTDIR)/UnicodeString.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/SystemHelper.o $(SRCDIR)/SystemHelper.cpp

$(OUTDIR)/FedoraCoreDistribution.o:$(SRCDIR)/FedoraCoreDistribution.h $(SRCDIR)/FedoraCoreDistribution.cpp\
  $(OUTDIR)/UnicodeString.o $(OUTDIR)/RPMPackager.o $(OUTDIR)/FileParser.o $(SRCDIR)/Distribution.h \
  $(OUTDIR)/LinuxAdvancedFeatures.o $(OUTDIR)/BasicFeatures.o $(OUTDIR)/SystemHelper.o  \
  $(CMNDIR)/OperatingSystem.h
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/FedoraCoreDistribution.o $(SRCDIR)/FedoraCoreDistribution.cpp

$(OUTDIR)/RPMPackager.o:$(SRCDIR)/RPMPackager.h $(SRCDIR)/RPMPackager.cpp $(OUTDIR)/Product.o \
  $(OUTDIR)/ForkAndPipe.o $(OUTDIR)/SystemHelper.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/RPMPackager.o $(SRCDIR)/RPMPackager.cpp

$(OUTDIR)/GentooDistribution.o:$(SRCDIR)/GentooDistribution.h $(SRCDIR)/GentooDistribution.cpp \
  $(OUTDIR)/UnicodeString.o $(OUTDIR)/RPMPackager.o $(OUTDIR)/FileParser.o $(SRCDIR)/Distribution.h \
  $(OUTDIR)/LinuxAdvancedFeatures.o $(OUTDIR)/BasicFeatures.o $(OUTDIR)/SystemHelper.o  \
  $(CMNDIR)/OperatingSystem.h $(OUTDIR)/GentooFindPackager.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/GentooDistribution.o $(SRCDIR)/GentooDistribution.cpp

$(OUTDIR)/EqueryPackager.o:$(SRCDIR)/EqueryPackager.h $(SRCDIR)/EqueryPackager.cpp \
  $(OUTDIR)/Product.o $(OUTDIR)/ForkAndPipe.o $(OUTDIR)/SystemHelper.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/EqueryPackager.o $(SRCDIR)/EqueryPackager.cpp
	
$(OUTDIR)/GentooFindPackager.o:$(SRCDIR)/GentooFindPackager.h $(SRCDIR)/GentooFindPackager.cpp \
  $(OUTDIR)/Product.o $(OUTDIR)/ForkAndPipe.o $(OUTDIR)/SystemHelper.o $(OUTDIR)/FileParser.o
	g++ -c $(CXXFLAGS) -o $(OUTDIR)/GentooFindPackager.o $(SRCDIR)/GentooFindPackager.cpp
