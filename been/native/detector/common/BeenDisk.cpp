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

/*! \file BeenDisk.cpp
 *  \author Branislav Repcek
 *  \date 2. 2. 2006
 *
 *  \brief Implementation of BeenDisk class.
 */

#include "BeenDisk.h"
#include "UnicodeString.h"

namespace hwdet
{
    /*!
     */
    BeenDisk::BeenDisk(void) :
    XMLElement(TXT("beenDisk"))
    {
        AddSubNode((path = new XMLValueElement< String >(TXT("path"), TXT(""))));
        AddSubNode((size = new XMLValueElement< unsigned long long >(TXT("size"), 0)));
        AddSubNode((free_space = new XMLValueElement< unsigned long long >(TXT("freeSpace"), 0)));
    }

    /*! \param Path Path to the directory with BEEN installation.
     *  \param user_total_size Total disk space available to the current user.
     *  \param user_free Free disk space available to the current user.
     */
    BeenDisk::BeenDisk(const String &Path, unsigned long long user_total_size, unsigned long long user_free) :
    XMLElement(TXT("beenDisk"))
    {
        AddSubNode((path = new XMLValueElement< String >(TXT("path"), Path)));
        AddSubNode((size = new XMLValueElement< unsigned long long >(TXT("size"), user_total_size)));
        AddSubNode((free_space = new XMLValueElement< unsigned long long >(TXT("freeSpace"), user_free)));
    }
    
    /*! \return Path to the root directory of the BEEN installation.
     */
    String BeenDisk::GetPath(void) const
    {
        return path->GetNodeValue();
    }
    
    /*! \param value New path to the BEEN_HOME.
     */
    void BeenDisk::SetPath(const String &value)
    {
        path->SetNodeValue(value);
    }
    
    /*! \return Size of the disk with BEEN as seen by the user associated with current thread. This may be less than total 
     *          number of bytes available on the drive due to the per-user quotas.
     */
    unsigned long long BeenDisk::GetDiskSize(void) const
    {
        return size->GetNodeValue();
    }
    
    /*! \param value New size of the disk in bytes.
     */
    void BeenDisk::SetDiskSize(unsigned long long value)
    {
        size->SetNodeValue(value);
    }
    
    /*! \return Free space available to the user associated with the current thread. This may be less than total number
     *          of free bytes available due to the per-user quotas.
     */
    unsigned long long BeenDisk::GetUserFree(void) const
    {
        return free_space->GetNodeValue();
    }

    /*! \param value New free space in bytes.
     */
    void BeenDisk::SetUserFree(unsigned long long value)
    {
        free_space->SetNodeValue(value);
    }
}
