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

/*! \file DiskPartition.cpp
 *  \author Branislav Repcek
 *  \date 2. 12. 2005
 *
 *  \brief Implementation of DiskPartition class.
 */

#include "DiskPartition.h"
#include "UnicodeString.h"

using namespace std;

namespace hwdet
{
    /*! \param device Name of the device.
     *  \param disk_name Mount-point.
     *  \param filesystem_name Name of file system.
     *  \param total_size Total size of the partition in bytes.
     *  \param free_space_size Free space size in bytes.
     */
    DiskPartition::DiskPartition(const String &device, const String &disk_name, const String &filesystem_name, 
                                unsigned long long total_size, unsigned long long free_space_size) :
    XMLElement(TXT("diskPartition"))
    {
        AddSubNode((device_name = new XMLValueElement< String >(TXT("deviceName"), device)));
        AddSubNode((name = new XMLValueElement< String >(TXT("name"), disk_name)));
        AddSubNode((filesystem = new XMLValueElement< String >(TXT("fileSystem"), filesystem_name)));
        AddSubNode((size = new XMLValueElement< unsigned long long >(TXT("size"), total_size)));
        AddSubNode((free_space = new XMLValueElement< unsigned long long >(TXT("freeSpace"), free_space_size)));
    }
    
    /*! \return Device name string.
     */
    String DiskPartition::GetDeviceName(void) const
    {
        return device_name->GetNodeValue();
    }
    
    /*! \return Mount-point name.
     */
    String DiskPartition::GetName(void) const
    {
        return name->GetNodeValue();
    }
    
    /*! \return File system name.
     */
    String DiskPartition::GetFilesystem(void) const
    {
        return filesystem->GetNodeValue();
    }
    
    /*! \return Size of partition in bytes.
     */
    unsigned long long DiskPartition::GetSize(void) const
    {
        return size->GetNodeValue();
    }
    
    /*! \return Size of free space in bytes.
     */
    unsigned long long DiskPartition::GetFreeSpaceSize(void) const
    {
        return free_space->GetNodeValue();
    }
} // namespace hwdet
