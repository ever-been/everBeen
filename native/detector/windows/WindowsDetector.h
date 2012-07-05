/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */

/*! \file WindowsDetector.h
 *  \author Branislav Repcek
 *  \date 5. 1. 2006
 *
 *  \brief Header file for Detector class.
 */

/*! \mainpage notitle
 *
 *  <center><h1>Detector for Windows</h1></center>
 *
 *  <p>This library collects data about hardware and software installed on the computer it is running on. Data is read
 *  from WMI and registry, therefore user has to have at least read access to those resources.</p>
 *  <br>
 *  <br>
 *  <b>Features:</b>
 *  <ul>
 *    <li>Detect operating system properties.</li>
 *    <li>Detect processor features (for multi-cpu machines each processor separately).</li>
 *    <li>Detect memory features (page file, RAM, virtual memory).</li>
 *    <li>Detect hard drives and partitions (partitions require administrative privileges).</li>
 *    <li>Detect installed software.</li>
 *    <li>Detect network adapters.</li>
 *  </ul>
 *  <p>Library can be compiled with Visual Studio 2005 or with Visual Studio 2003. To compile library you have to have
 *  JAVA_HOME environment variable set so it points to the installation directory of the Sun Java JDK (tested with 
 *  various 1.5.0_x versions).</p>
 *  <p>Detector library is part of the <a href="http://been.objectweb.org/">BEEN</a> project developed for
 *  Charles University in Prague, <a href="http://dsrg.mff.cuni.cz/">Distributed Systems Research Group</a></p>.
 */

#ifndef WINDOWS_DETECTOR_INCLUDED
#define WINDOWS_DETECTOR_INCLUDED

#include <wbemidl.h>
#include "../Common/UnicodeString.h"
#include "../Common/Detector.h"
#include "../Common/XMLFileOutput.h"
#include "../Common/MessageReporter.h"
#include "../Common/CheckedResult.h"
#include "../Common/DiskDrive.h"

namespace hwdet
{

    /*! \brief Windows detector core class.
     *
     *  This class does all hardware/software detection on Windows platform. It contains methods to detect OS, memory, drives, 
     *  network and installed software. All output is saved into XMLOutputFile which can then be written to any output stream.
     *  <br>
     *  It uses WMI to detect some of the features, so it will work only on Windows 2000, Windows XP or newer.
     *  <br>
     *  Tested on: 
     *  <ul>
     *    <li>Windows 2000 without Service Pack</li>
     *    <li>Windows XP 32 bit without Service Pack</li>
     *    <li>Windows XP 32 bit SP 2.0</li>
     *    <li>Windows Vista 32-Bit CTP build 5270</li>
     *  </ul>
     */
    class WindowsDetector : public Detector
    {
    public:
        //! Constructor.
        WindowsDetector(void);

        //! Destructor.
        ~WindowsDetector(void);

        //! Initialize detector.
        bool Initialize(void);

        //! Detect all features.
        bool Detect(void);

        //! Clear all used memory.
        bool Destroy(void);

        //! Get string containing output file.
        String GetOutputString(void) const;

        //! Write generated output to the stream.
        bool WriteToStream(OStream &stream) const;

    private:

        //! Pointer to the WMI Services provider.
        IWbemServices   *Services;

        //! Detect OS features.
        bool DetectOS(void);

        //! Detect CPU features.
        bool DetectCPU(void);

        //! Detect memory features.
        bool DetectMemory(void);

        //! Detect all drives.
        bool DetectDrives(void);

        //! Detect hard-drives.
        bool DetectHDD(XMLElement *drives);

        //! Detect partitions on a given HDD.
        bool DetectPartitions(DiskDrive *disk_drive);

        //! Detect CD/DVD drives.
        bool DetectCDRom(XMLElement *drives);

        //! Detect other drive types (tape drives).
        bool DetectOtherDrives(XMLElement *drives);

        //! Detect network features.
        bool DetectNetwork(void);

        //! Enumerate installed applications.
        bool DetectProducts(void);

        /*! \brief Test for succes/failure and report message.
         *
         *  This will test whether given CheckedResult is valid. If it is not, given message is reported.
         *
         *  \param cr Result to test.
         *  \param message Message to report in case of failure.
         *  \return <code>true</code> if CheckedResult is valid, <code>false</code> otherwise.
         */
        template< typename T >
        bool TestAndReport(const CheckedResult< T > &cr, const String &message)
        {
            if (cr.Failed())
            {
                ReportMessage(message);
            }
            return cr.Succeeded();
        }
    };
}

#endif
