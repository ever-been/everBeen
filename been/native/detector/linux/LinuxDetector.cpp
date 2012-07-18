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

/*! \file LinuxDetector.cpp
 *  \author Branislav Repcek
 *  \date 2. 2. 2006
 *
 *  \brief Implementation of LinuxDetector class.
 */

#include <iostream>
#include <vector>
#include <iomanip>
#include <algorithm>

#include <unistd.h>
#include <stdlib.h>
#include <sys/utsname.h>
#include <sys/vfs.h>
#include <time.h>
#include <ctype.h>
#include <net/if.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <errno.h>
#include <linux/sockios.h>

#include "../common/UnicodeString.h"
#include "../common/Processor.h"
#include "../common/Memory.h"
#include "../common/DiskDrive.h"
#include "../common/DiskPartition.h"
#include "../common/BeenDisk.h"
#include "../common/NetworkAdapter.h"
#include "LinuxDetector.h"
#include "FileParser.h"
//#include "FileSystemName.h"
#include "NetworkInterfaceName.h"
#include "DistributionRegistry.h"
#include "Distribution.h"
#include "FedoraCoreDistribution.h"
#include "GentooDistribution.h"


/*! Comment this define to remove CD-ROM drives from the list of the disk drives.
 */
#define DETECT_CDROM_DRIVES

using namespace std;

namespace hwdet
{
    /*! Constructor.
     */
    LinuxDetector::LinuxDetector(void) :
    Detector(TXT("LinuxDetector"), ::hwdet::DefaultEncoding),
    registry((MessageReporter *) this)
    {
        // note: register all custom distro handlers here.        
        registry.Register(new FedoraCoreDistribution(this));
        registry.Register(new GentooDistribution(this));
    }
    
    /*! Destructor.
     */
    LinuxDetector::~LinuxDetector(void)
    {
        Destroy();
    }
    
    /*! Initialize detector.
     * 
     *  \return true on success, false otherwise.
     */
    bool LinuxDetector::Initialize(void)
    {
        return true;
    }
    
    /*! Detect all features.
     * 
     *  \return true on success, false otherwise.
     */
    bool LinuxDetector::Detect(void)
    {
        output.ClearRootNode();
        root_node = new XMLElement(TXT("hostInfo"));
        output.SetRootNode(root_node);
    
        time_t current_time = time(NULL);
        tm time_down;
        
        localtime_r(&current_time, &time_down);
        
        StringStream stream;
        
        stream << (1900 + time_down.tm_year) << TXT('/') << setw(2) <<
            time_down.tm_mon << TXT('/') << setw(2) << time_down.tm_mday;
        
        root_node->AddSubNode(new XMLValueElement< String >(TXT("lastCheckDate"), stream.str()));
        
        stream.str(TXT(""));
        
        stream << setw(2) << time_down.tm_hour << TXT(':') << setw(2) << time_down.tm_min <<
            TXT('.') << setw(2) << time_down.tm_sec;
        
        root_node->AddSubNode(new XMLValueElement< String >(TXT("lastCheckTime"), stream.str()));
    
        root_node->AddSubNode(new XMLValueElement< String >(TXT("detector"), String(TXT("hwdet3_linux"))));
    
        // this is only temporary, real name is added by the java later
        root_node->AddSubNode(new XMLValueElement< String >(TXT("hostName"), String(TXT("localhost"))));
        
        return DetectOS() & DetectCPU() & DetectMemory() & DetectDrives() & 
            DetectNetwork() & DetectProducts();
    }
    
    /*! \return true on success, false otherwise.
     */
    bool LinuxDetector::Destroy(void) 
    {
        delete root_node;
        root_node = NULL;
    
        return true;
    }
    
    /*! \return String with all data collected by the detector.
     */
    String LinuxDetector::GetOutputString(void) const
    {
        OStringStream stream;
        output.Write(stream);
        return stream.str();
    }
    
    /*! \param stream Stream to write output to.
     * 
     *  \return true on success, false otherwise.
     */
    bool LinuxDetector::WriteToStream(OStream &stream) const
    {
        output.Write(stream);
        return true;
    }
    
    /*! \return true on success, false otherwise.
     */
    bool LinuxDetector::DetectOS(void)
    {
        ReportMessage(TXT("Detecting operating system features."));
    
        Distribution *distro = registry.GetBestFit();
        
        distro->DetectOS(root_node);
        
        return true;
    }
    
    /*! \return true on success, false otherwise.
     */
    bool LinuxDetector::DetectCPU(void) 
    {
        ReportMessage(TXT("Detecting CPU features."));
    
        XMLElement *processors = new XMLElement(TXT("processors"));
        root_node->AddSubNode(processors);
    
        vector< String > lines;
        bool res = ReadLines("/proc/cpuinfo", lines);
        
        if (!res)
        {
            ReportMessage(TXT("Error reading CPU data from /proc/cpuinfo"));
            return false;
        }
        
        for (vector< String >::const_iterator it = lines.begin(); it < lines.end(); ++it)
        {
            Processor *current_processor = new Processor();
            
            for ( ; (it != lines.end()) && (it->size() != 0); ++it)
            {
                size_t ends_at = 0;
                String name = GetFieldName(*it, &ends_at, TXT(": "));
                
                if (name == TXT("vendor_id"))
                {
                    current_processor->SetVendorName(it->substr(ends_at));
                }
                else if (name == TXT("model name"))
                {
                    current_processor->SetModelName(it->substr(ends_at));
                }
                else if (name == TXT("cpu MHz"))
                {
                    StringStream stream;
                    stream << it->substr(ends_at);
                    
                    unsigned long value = 0;
                    stream >> value;
                    
                    if (!stream.fail())
                    {
                        current_processor->SetSpeed(value);
                    }
                }
                else if (name == TXT("cache size"))
                {
                    StringStream stream;
                    stream << it->substr(ends_at);
                    
                    unsigned long cache = 0;
                    stream >> cache;
                    
                    if (!stream.fail())
                    {
                        current_processor->SetCacheSize(cache);
                    }
                }
            }
            
            ++it;
            processors->AddSubNode(current_processor);
        }
    
        return true;
    }
    
    /*! \return true on success, false otherwise.
     */
    bool LinuxDetector::DetectMemory(void)
    {
        ReportMessage(TXT("Detecting memory properties."));
    
        Memory *memory = new Memory();
        root_node->AddSubNode(memory);
        
        vector< String > lines;
        bool res = ReadLines("/proc/meminfo", lines);
        
        if (!res)
        {
            ReportMessage(TXT("Error reading memory info from the /proc/meminfo file."));
            return false;
        }
        
        for (vector< String >::const_iterator it = lines.begin(); it != lines.end(); ++it)
        {
            if (BeginsWith(*it, TXT("MemTotal")))
            {
                unsigned long long value = 0;
                bool r = GetFieldValue(*it, 2, value);
                
                if (r)
                {
                    // Convert from kilobytes to bytes
                    memory->SetPhysicalMemorySize(value * 1024ull);
                }
            }
            else if (BeginsWith(*it, TXT("SwapTotal")))
            {
                unsigned long long value = 0;
                bool r = GetFieldValue(*it, 2, value);
                
                if (r)
                {
                    // Convert from kilobytes to bytes
                    memory->SetSwapSize(value * 1024ull);
                }
            }
        }
        
        return true;
    }
    
    /*! \return true on success, false otherwise.
    */
    bool LinuxDetector::DetectDrives(void)
    {
        ReportMessage(TXT("Detecting drives."));
    
        DetectBeenDisk();
        
        XMLElement *drives = new XMLElement(TXT("diskDrives"));
        root_node->AddSubNode(drives);
        
        vector< String > drive_names = ReadDriveNames();
        vector< FstabEntry > fstab_entries = ReadMounts();
        
        for (vector< String >::const_iterator it = drive_names.begin(); it != drive_names.end(); ++it)
        {
            // Read data about current drive.
            DriveInfo current_drive(it->c_str());
            
#ifndef DETECT_CDROM_DRIVES
            // Test if the drive is CD-ROM
            if (current_drive.media == TXT("CD-ROM"))
            {
                // Skip it.
                continue;
            }
#endif
            
            DiskDrive *current_disk_drive = current_drive.GetDiskDrive();
            
            // Add drive to the list of drives.
            drives->AddSubNode(current_disk_drive);
            
            // If this is cd-rom, do not bother reading partition info.
            if (current_drive.media == TXT("CD-ROM"))
            {
                continue;
            }
            
            vector< String > partitions = ReadPartitionNames(current_drive.device_name);
            
            // Walk through all partitions on the drive.
            for (vector< String >::const_iterator part_it = partitions.begin(); part_it != partitions.end(); ++part_it)
            {
                DiskPartition *partition = 
                    PartitionInfo(*part_it, fstab_entries).GetDiskPartition();
                
                current_disk_drive->AddSubNode(partition);
            }
        }
        
        return true;
    }
    
    /*! Reads names of all IDE HDD devices from /proc/diskstats.
     * 
     *  \return Names of all IDE hard disk devices. Note that only device names are returned
     *          (without the dev/ prefix).
     */
    vector< String > LinuxDetector::ReadDriveNames(void)
    {
        vector< String > lines;
        bool res = ReadLines("/proc/diskstats", lines);
        
        if (!res)
        {
            ReportMessage(TXT("Error reading /proc/diskstats file."));
            return vector< String >();
        }
        
        vector< String > names;
        for (vector< String >::const_iterator it = lines.begin(); it != lines.end(); ++it)
        {
            String drive_name;
            bool r = GetFieldValue(*it, 3, drive_name);
            
            if (!r)
            {
                break;
            }
            
            // Is this ide drive (name is hdXX and does not end with a number)?
            if (BeginsWith(drive_name, TXT("hd")) && (isalpha(drive_name[drive_name.size() - 1])))
            {
                names.push_back(drive_name);
            }
        }
        
        return names;
    }
    
    /*! Reads names of all partitions of given drive from the /proc/diskstats file.
     * 
     *  \return List of partition names for given drive.
     */
    vector< String > LinuxDetector::ReadPartitionNames(const String &drive_prefix)
    {
        vector< String > lines;
        bool res = ReadLines("/proc/diskstats", lines);
        
        if (!res)
        {
            ReportMessage(TXT("Error reading /proc/diskstats file."));
            return vector< String >();
        }
        
        vector< String > names;
        for (vector< String >::const_iterator it = lines.begin(); it != lines.end(); ++it)
        {
            String drive_name;
            bool r = GetFieldValue(*it, 3, drive_name);
            
            if (!r)
            {
                break;
            }
            
            // Device is partition if it has same prefix as the drive (e.g. hda) and ends with
            // an index number (e.g. hda3).
            if (BeginsWith(drive_name, drive_prefix) && (isdigit(drive_name[drive_name.size() - 1])))
            {
                names.push_back(drive_name);
            }
        }
        
        return names;
    }
    
    /*! This will read /proc/mounts file and parse entries for each currently mounted
     *  filesystem.
     * 
     *  \return List of all entries from the file. Empty list is returned if an error occured.
     */
    vector< LinuxDetector::FstabEntry > LinuxDetector::ReadMounts(void)
    {
        vector< LinuxDetector::FstabEntry > result;
        
        // Open /proc/mounts file
        FILE *file = setmntent("/proc/mounts", "r");
    
        if (file == NULL)
        {
            // Unable to open file.
            ReportMessage(TXT("Unable to open /proc/mounts file."));
            return result;
        }
    
        mntent *entry = NULL;
    
        // Read all entries from the file.
        do
        {
            entry = getmntent(file);
        
            if (entry)
            {
                // Read some data from mntent and store in the result.
                result.push_back(FstabEntry(entry));
            }
        } while (entry);
    
        if (fclose(file) != 0)
        {
            ReportMessage(TXT("Unable to close /proc/mounts file."));
        }
        
        return result;
    }
    
    /*! Read details about the disk on which BEEN is installed. Path to the drive is
     *  determined from the BEEN_HOME environment variable.
     * 
     *  \return true.
     */
    bool LinuxDetector::DetectBeenDisk(void)
    {
        BeenDisk *been_disk = new BeenDisk();
        root_node->AddSubNode(been_disk);
        
        Char *been_home = getenv(TXT("BEEN_HOME"));
        
        if (been_home)
        {
            been_disk->SetPath(been_home);
            
            struct statfs disk_stats;
            int result = statfs(been_home, &disk_stats);
            
            if (result)
            {
                ReportMessage(TXT("Unable to read details about the BEEN disk."));
                return true;
            }
            
            unsigned long long block_size = disk_stats.f_bsize;
            unsigned long long total_blocks = disk_stats.f_blocks;
            
            // Uncomment this if you want total size of the free space.
            //unsigned long long free_blocks = disk_stats.f_bfree;
            
            // Number of free block accessible to the non-superuser.
            unsigned long long free_blocks = disk_stats.f_bavail;
            
            been_disk->SetDiskSize(total_blocks * block_size);
            been_disk->SetUserFree(free_blocks * block_size);
        }

        return true;
    }
    
    /*! \return true on success, false otherwise.
    */
    bool LinuxDetector::DetectNetwork(void)
    {
        ReportMessage(TXT("Detecting network interfaces."));
    
        XMLElement *network = new XMLElement(TXT("network"));
        root_node->AddSubNode(network);
        
        int socket_fd = socket(AF_INET, SOCK_DGRAM, 0);
        
        if (socket_fd < 0)
        {
            ReportMessage(TXT("Unable to open socket."));
            return false;
        }
        
        struct if_nameindex *iface_names = if_nameindex();
    
        if (iface_names == NULL)
        {
            ReportMessage(TXT("Unable to list network interfaces."));
            if_freenameindex(iface_names);
            close(socket_fd);
            return false;
        }
    
        for (struct if_nameindex *current = iface_names; current->if_index != 0; ++current)
        {
            InterfaceInfo iface_info(current->if_name, socket_fd);
            
            network->AddSubNode(iface_info.GetNetworkAdapter());
        }
        
        if_freenameindex(iface_names);
        close(socket_fd);
        
        return true;
    }
    
    /*! \return true on success, false otherwise.
    */
    bool LinuxDetector::DetectProducts(void)
    {
        ReportMessage(TXT("Detecting installed software."));
    
        XMLElement *products = new XMLElement(TXT("installedProducts"));
        root_node->AddSubNode(products);
        
        Distribution *distro = registry.GetBestFit();
        
        distro->DetectSoftware(products);
        
        return true;
    }
    
    /*! This will read files that store information about given drive from the proc filesystem.
     *  Note that this works for IDE devices only.
     * 
     *  \param dev_name Name of the drive's device.
     */
    LinuxDetector::DriveInfo::DriveInfo(const char *dev_name) :
    size(0),
    media(TXT("(unknown)")),
    model(TXT("(unknown)"))    
    {
        fill(device_name, device_name + DEVICE_NAME_LENGTH, 0);
        strcpy(device_name, dev_name);
     
        base_path = "/proc/ide/" + string(dev_name) + "/";
        
        ReadSize();
        ReadMedia();
        ReadModel();
    }
    
    /*! Reads data from /proc/ide/&lt;devicename&gt;/capacity file which contains only one number
     *  which represents size of the drive in sectors.
     */
    void LinuxDetector::DriveInfo::ReadSize(void)
    {
        const char *capacity_file = (base_path + "capacity").c_str();
        vector< String > lines;
        bool res = ReadLines(capacity_file, lines);
        
        if (!res || (lines.size() < 1))
        {
            return;
        }
        
        // Read number of sectors on the drive.
        StringStream stream;
        stream << lines[0];
        stream >> size;
        
        size *= SECTOR_SIZE;
        
        if (stream.fail())
        {
            size = 0;
        }
    }
    
    /*! Read data from the /proc/ide/&lt;devicename&gt;/media file. This file contains only
     *  one line which contains type of the media in the drive.
     */
    void LinuxDetector::DriveInfo::ReadMedia(void)
    {
        const char *media_file = (base_path + "media").c_str();
        vector< String > lines;
        bool res = ReadLines(media_file, lines);
        
        if (!res || (lines.size() < 1))
        {
            return;
        }
        
        if (lines[0] == TXT("disk"))
        {
            media = TXT("HDD");
        }
        else if (lines[0] == TXT("cdrom"))
        {
            media = TXT("CD-ROM");
        }
        else
        {
            media = lines[0];
        }
    }
    
    /*! Reads data from the /proc/ide/&lt;devicename&gt;/model file. This file contains only
     *  one line with the model name of the device.
     */
    void LinuxDetector::DriveInfo::ReadModel(void)
    {
        const char *model_file = (base_path + "model").c_str();
        vector< String > lines;
        bool res = ReadLines(model_file, lines);
        
        if (!res || (lines.size() < 1))
        {
            return;
        }
        
        model = lines[0];
    }
    
    /*! Create new instance of the DiskDrive class on the heap.
     * 
     *  \return Pointer to the DiskDrive class which contains same data as this class.
     */
    DiskDrive *LinuxDetector::DriveInfo::GetDiskDrive(void) const
    {
        DiskDrive *drive = new DiskDrive(size, model, media, GetFullDeviceName());
        
        return drive;
    }
    
    /*! \return Full path to the device for the drive.
     */
    String LinuxDetector::DriveInfo::GetFullDeviceName(void) const
    {
        return String(TXT("/dev/")) + String(device_name);
    }
    
    /*! \param entry Entry which contains data from one line of the fstab file.
     */
    LinuxDetector::FstabEntry::FstabEntry(const mntent *entry) :
    device_name(entry->mnt_fsname),
    file_system(entry->mnt_type),
    mount_point(entry->mnt_dir)
    {
    }
    
    /*! \param name Name of the device.
     *  \param fstab_entries Entries from the fstab file.
     */
    LinuxDetector::PartitionInfo::PartitionInfo(const String &name, const vector< FstabEntry > &fstab_entries) :
    device_name(name),
    device_path(TXT("/dev/") + name),
    size(0),
    free_space(0),
    file_system(TXT("(unknown)")),
    mount_point(TXT("(unknown)"))
    {
        vector< FstabEntry >::const_iterator fstab_entry = 
            find_if(fstab_entries.begin(), fstab_entries.end(), 
                    FstabEntry::DeviceNameEquals(device_path));
        
        // Do we have entry in the fstab?
        if (fstab_entry != fstab_entries.end())
        {
            file_system = fstab_entry->file_system;
            mount_point = fstab_entry->mount_point;
            
            const char *mount_name = fstab_entry->mount_point.c_str();
            struct statfs data;
            int result = statfs(mount_name, &data);
    
            if (result)
            {
                // Failed to get data from statfs
                return;
            }
            
            unsigned long long block_size = data.f_bsize;
            unsigned long long blocks = data.f_blocks;
            
            // change to data.f_bfree to get free space for the superuser
            unsigned long long free_blocks = data.f_bavail;
            
            size = blocks * block_size;
            free_space = free_blocks * block_size;
        }
    }
    
    /*! \return DiskPartition object containing same data as this class.
     */
    DiskPartition *LinuxDetector::PartitionInfo::GetDiskPartition(void) const
    {
        DiskPartition *partition = new DiskPartition(device_path, mount_point, file_system, size, free_space);
        
        return partition;
    }
    
    /*! Query interface data from kernel via ioctl. 
     * 
     *  \param name Name of the interface to query.
     *  \param socket_fd Open socket to use in ioctl.
     */
    LinuxDetector::InterfaceInfo::InterfaceInfo(const char *name, int socket_fd) :
    iface_name(name),
    vendor_name(TXT("(unknown)")),
    hw_address(TXT("(unknown)")),
    iface_type(TXT("(unknown)"))
    {
        fill(hw_addr_bytes, hw_addr_bytes + HW_ADDRESS_LENGTH, 0);
        
        struct ifreq request;
    
        strcpy(request.ifr_ifrn.ifrn_name, iface_name.c_str());
        
        // Query HW address.
        int res = ioctl(socket_fd, SIOCGIFHWADDR, &request);
        if (res)
        {
            // Error.
            return;
        }
        
        struct sockaddr *addr = &request.ifr_hwaddr;
        
        copy((unsigned char *) &addr->sa_data, 
              (unsigned char *) &addr->sa_data + HW_ADDRESS_LENGTH, 
             hw_addr_bytes);
        
        HwAddressString();
        
        iface_type = GetInterfaceTypeByID(addr->sa_family);
    }
    
    /*! \return NetworkAdapter object containing same data as this class.
     */
    NetworkAdapter *LinuxDetector::InterfaceInfo::GetNetworkAdapter(void) const
    {
        NetworkAdapter *adapter = new NetworkAdapter(iface_name, vendor_name, hw_address, iface_type);
        
        return adapter;
    }
    
    /*! Convert bytes in the MAC address to the standard notation (hex bytes separated
     *  by colons).
     */
    void LinuxDetector::InterfaceInfo::HwAddressString(void)
    {
        StringStream stream;
        
        unsigned char *byte = hw_addr_bytes;
        
        stream.fill('0');
        
        for ( ; byte < hw_addr_bytes + HW_ADDRESS_LENGTH - 1; ++byte)
        {
            stream << hex << setw(2) << static_cast< unsigned int >(*byte) << ":";
        }
        
        stream << hex << setw(2) << static_cast< unsigned int >(*byte);
        
        hw_address = stream.str();
    }
}
