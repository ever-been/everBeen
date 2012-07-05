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

/*! \file DiskDrive.h
 *  \author Branislav Repcek
 *  \date 2. 12. 2005
 *
 *  \brief Header file for DiskDrive class.
 */

#ifndef DISK_DRIVE_INCLUDED
#define DISK_DRIVE_INCLUDED

#include "XMLFileOutput.h"
#include "UnicodeString.h"

namespace hwdet
{

    /*! \brief Storage class for disk drive related data.
     */
    class DiskDrive : public hwdet::XMLElement
    {
    public:

        //! Constructor.
        DiskDrive(unsigned long long disk_size, const String &model_name, const String &media, const String &device);

        //! Get size of the drive in bytes.
        unsigned long long GetSize(void) const;

        //! Get model name of the drive.
        String GetModelName(void) const;

        //! Get type of media in drive.
        String GetMediaType(void) const;

        //! Get name of device assigned to the drive by system.
        String GetDeviceName(void) const;
        
    private:
        //! Disk size in bytes.
        XMLValueElement< unsigned long long > *size;

        //! Model name.
        XMLValueElement< String >             *model;

        //! Media type identification.
        XMLValueElement< String >             *media_type;

        //! Device name.
        XMLValueElement< String >             *device_name;
    };

}

#endif
