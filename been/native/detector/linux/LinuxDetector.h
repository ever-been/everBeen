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

/*! \file LinuxDetector.h
 *  \author Branislav Repcek
 *  \date 2. 2. 2006
 *
 *  \brief Header file for LinuxDetector class.
 */

/*! \mainpage notitle
 *
 *  <center><h1>Detector for Linux</h1></center>
 *
 *  <p>This library collects data about hardware and software installed on the computer it is running on. Data is read
 *  from various files in the /proc directory and various other sources.</p>
 *  <br>
 *  <br>
 *  <b>Features:</b>
 *  <ul>
 *    <li>Detect operating system properties.</li>
 *    <li>Detect processor features (for multi-cpu machines each processor separately).</li>
 *    <li>Detect memory features (page file, RAM, virtual memory).</li>
 *    <li>Detect hard drives and partitions.</li>
 *    <li>Detect installed software. This works on distributions that use RPM or Portage. 
 *        Fedora Core and Gentoo are supported out-of-the box.</li>
 *    <li>Detect network adapters.</li>
 *  </ul>
 *  <p>Library can be compiled with g++ version 4 or newer. To compile library you have to have JAVA_HOME environment variable 
 *  set so it points to the installation directory of the Sun Java JDK (tested with  1.5.0_09 version).</p>
 *  <p>Detector library is part of the <a href="http://been.objectweb.org/">BEEN</a> project developed for
 *  Charles University in Prague, <a href="http://dsrg.mff.cuni.cz/">Distributed Systems Research Group</a></p>.
 */

#ifndef LINUX_DETECTOR_INCLUDED
#define LINUX_DETECTOR_INCLUDED

#include <vector>
#include <string>
#include <mntent.h>
#include "../common/UnicodeString.h"
#include "../common/Detector.h"
#include "../common/XMLFileOutput.h"
#include "../common/DiskDrive.h"
#include "../common/DiskPartition.h"
#include "../common/NetworkAdapter.h"
#include "DistributionRegistry.h"

namespace hwdet
{
    /*! \brief Linux detector class.
     * 
     *  This class collects all data about installed hardware and software.
     */
    class LinuxDetector : public Detector
    {
    public:
        //! Constructor.
        LinuxDetector(void);

        //! Destructor.
        ~LinuxDetector(void);

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
        /*! \brief Helper class which stores details about one drive device.
         * 
         *  This class is able to read info about device from the files in /proc/ide/&lt;devicename&gt;/
         *  directory.
         */
        class DriveInfo
        {
        public:
            //! Read data from proc.
            DriveInfo(const char *dev_name);
            
            //! Size of the one sector on drive.
            static const unsigned long long SECTOR_SIZE = 512;
            
            //! Length of the device name string.
            static const size_t DEVICE_NAME_LENGTH = 16;
            
            //! Name of the device.
            char               device_name[DEVICE_NAME_LENGTH];
            
            //! Total size of the drive.
            unsigned long long size;
            
            //! Media name.
            String             media;
            
            //! Model name.
            String             model;
            
            //! Get full path to the device.
            String GetFullDeviceName(void) const;
            
            //! Convert data to DiskDrive structure.
            DiskDrive *GetDiskDrive(void) const;
            
        private:
            //! Read info about size of the drive.
            void ReadSize(void);
            
            //! Read media type info.
            void ReadMedia(void);
            
            //! Read model info.
            void ReadModel(void);
            
            //! Path to the directory with drive info in proc.
            std::string        base_path;
        };
        
        /*! \brief Stores one entry from fstab file.
         */
        class FstabEntry
        {
        public:
            //! Create new entry.
            FstabEntry(const mntent *entry);
            
            //! Name of the device.
            String device_name;
            
            //! Filesystem name.
            String file_system;
            
            //! Mount point path.
            String mount_point;
            
            /*! \brief Unary predicate which compares mount point of an entry with given string.
             * 
             *  This class can be used to search for entry with given mount point using
             *  the <code>find_if</code> function from the <code>algorithm</code> header
             *  from STL.
             */
            class MountPointEquals
            {
            public:
                /*! \brief Create new predicate.
                 * 
                 *  \param mp Mount point according to which this predicate will compare.
                 */
                MountPointEquals(const String &mp) :
                mount_point(mp)
                {
                }
                
                /*! \brief Compare mount point of the entry with given string.
                 * 
                 *  \param entry Entry to compare.
                 * 
                 *  \return true if mount point of the entry is same as mount point stored 
                 *          in this class.
                 */
                bool operator() (const FstabEntry &entry) const
                {
                    return entry.mount_point == mount_point;
                }
                
            private:
                //! Mount point. 
                String mount_point;
            };
            
            /*! \brief Unary predicate which compares device name of an entry with given string.
             * 
             *  This class can be used to search for entry with given device name using
             *  the <code>find_if</code> function from the <code>algorithm</code> header
             *  from STL.
             */
            class DeviceNameEquals
            {
            public:
                /*! \brief Create new predicate.
                 * 
                 *  \param dn Name of the device according to which this predicate will compare.
                 */
                DeviceNameEquals(const String &dn) :
                device_name(dn)
                {
                }
                
                /*! \brief Compare device name of the entry with given string.
                 * 
                 *  \param entry Entry to compare.
                 * 
                 *  \return true if device name of the entry is same as device name stored 
                 *          in this class.
                 */
                bool operator() (const FstabEntry &entry) const
                {
                    return entry.device_name == device_name;
                }
                
            private:
                //! Device name.
                String device_name;
            };
        };
        
        /*! \brief Stores details about one partition.
         */
        class PartitionInfo
        {
        public:
            //! Read data about the partition.
            PartitionInfo(const String &name, const std::vector< FstabEntry > &fstab_entries);
            
            //! Name of the device.
            String device_name;
            
            //! Full name of the device.
            String device_path;
            
            //! Size of the partition in bytes.
            unsigned long long size;
            
            //! Free space on the partition in bytes.
            unsigned long long free_space;
            
            //! Name of the filesystem on partition.
            String file_system;
            
            //! Mount point of the partition.
            String mount_point;
                        
            //! Convert data to XML node.
            DiskPartition *GetDiskPartition(void) const;
        };
    
        /*! \brief Stores data about one network interface.
         */
        class InterfaceInfo
        {
        public:
            //! Ctor.
            InterfaceInfo(const char *name, int socket_fd);
                
            //! Name of the interface.
            String iface_name;
            
            //! Vendor's name (always unknown on Linux).
            String vendor_name;
            
            //! Hardware address string (MAC).
            String hw_address;
            
            //! Type of the iface.
            String iface_type;
            
            //! Length of the hardware address of the device.
            static const size_t HW_ADDRESS_LENGTH = 6;
            
            //! MAC address data.
            unsigned char hw_addr_bytes[HW_ADDRESS_LENGTH];
            
            //! Convert to XML NetworkAdapter.
            NetworkAdapter *GetNetworkAdapter(void) const;
            
        private:
            //! Fill string version of the MAC.
            void HwAddressString(void);
        };
        
        //! Detect OS features.
        bool DetectOS(void);

        //! Detect CPU features.
        bool DetectCPU(void);

        //! Detect memory features.
        bool DetectMemory(void);

        //! Detect all drives.
        bool DetectDrives(void);
        
        //! Determine names of all IDE drives.
        std::vector< String > ReadDriveNames(void);
        
        //! Read names of all partitions on given drive.
        std::vector< String > ReadPartitionNames(const String &drive_prefix);
        
        //! Read data about all currently mounted filesystems.
        std::vector< FstabEntry > ReadMounts(void);
        
        //! Detect properties of the BEEN disk.
        bool DetectBeenDisk(void);

        //! Detect network features.
        bool DetectNetwork(void);

        //! Enumerate installed applications.
        bool DetectProducts(void);
        
        //! Distribution registry.
        DistributionRegistry    registry;
    };
}

#endif
