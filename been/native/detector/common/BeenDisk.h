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

/*! \file BeenDisk.h
 *   \author Branislav Repcek
 *  \date 2. 2. 2006
 *
 *  \brief Header file for BeenDisk class.
 */

#ifndef BEEN_DISK_INCLUDED
#define BEEN_DISK_INCLUDED

#include "XMLFileOutput.h"
#include "UnicodeString.h"

namespace hwdet
{

    /*! \brief This class stores information about drive space available for BEEN to use.
     */
    class BeenDisk : public XMLElement
    {
    public:
        //! Default ctor.
        BeenDisk(void);
        
        //! Constructor.
        BeenDisk(const String &Path, unsigned long long user_total_size, unsigned long long user_free);

        //! Get path to the root of the BEEN installation.
        String GetPath(void) const;
        
        //! Set path.
        void SetPath(const String &value);

        //! Get size of the disk BEEN is located on.
        unsigned long long GetDiskSize(void) const;

        //! Set size of the drive.
        void SetDiskSize(unsigned long long value);
        
        //! Get free space available on disk BEEN is located on.
        unsigned long long GetUserFree(void) const;
        
        //! Set size of the free space.
        void SetUserFree(unsigned long long value);

    private:
        //! Path to root folder.
        XMLValueElement< String >             *path;

        //! Size of the drive.
        XMLValueElement< unsigned long long > *size;

        //! Free space on the drive.
        XMLValueElement< unsigned long long > *free_space;
    };
}

#endif
