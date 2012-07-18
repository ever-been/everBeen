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

/*! \file HardwareDescription.cpp
 *  \author Branislav Repcek
 *  \date 23. 10. 2006
 *
 *  \brief Implementation of methods from HardwareDescription class.
 */

// Disable warnings about deprecated "unsecure" functions from STL in Visual Studio 2005+.
#if _MSC_VER >= 1400
#define _SCL_SECURE_NO_DEPRECATE 1
#endif

#include <vector>
#include "String.h"
#include "HardwareDescription.h"

using namespace std;

namespace lm
{

    /*!
     */
    HardwareDescription::HardwareDescription(void) :
    processor_count(0),
    memory_size(0),
    time(0)
    {
    }

    /*! \param time_stamp Timestamp.
     */
    HardwareDescription::HardwareDescription(unsigned long long time_stamp) :
    processor_count(0),
    memory_size(0),
    time(time_stamp)
    {
    }

    /*! \param n New number of drives.
     */
    void HardwareDescription::ReallocateDrives(size_t n)
    {
        vector< String >().swap(drives);
        drives.resize(n, String());
    }

    /*! \param n Number of network adapters.
     */
    void HardwareDescription::ReallocateAdapters(size_t n)
    {
        vector< String >().swap(adapters);
        adapters.resize(n, String());
    }
}
