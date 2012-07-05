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

/*! \file DiskPartition.h
 *  \author Branislav Repcek
 *  \date 3. 12. 2005
 *
 *  \brief Header file for DiskPartition class.
 */

#ifndef DISK_PARTITION_INCLUDED
#define DISK_PARTITION_INCLUDED

#include "XMLFileOutput.h"
#include "UnicodeString.h"

namespace hwdet
{

    /*! \brief Storage class for all partition related data.
     */
    class DiskPartition : public hwdet::XMLElement
    {
    public:

        //! Constructor.
        DiskPartition(const String &device, const String &disk_name, const String &filesystem_name, 
                      unsigned long long total_size, unsigned long long free_space_size);

        //! Get name of the device assigned to the partition.
        String GetDeviceName(void) const;

        //! Get mount-point.
        String GetName(void) const;

        //! Get file system name.
        String GetFilesystem(void) const;

        //! Get size of free space left on the partition.
        unsigned long long GetFreeSpaceSize(void) const;

        //! Get size of the partition.
        unsigned long long GetSize(void) const;

    private:

        //! Name of device assigned to the partition.
        XMLValueElement< String >             *device_name;

        //! Partition mount-point.
        XMLValueElement< String >             *name;

        //! Filesystem name.
        XMLValueElement< String >             *filesystem;

        //! Total size of the partition.
        XMLValueElement< unsigned long long > *size;

        //! Free space on the partition.
        XMLValueElement< unsigned long long > *free_space;
    };
}

#endif
