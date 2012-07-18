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

/*! \file HardwareDescription.h
 *  \author Branislav Repcek
 *  \date 23. 10. 2006
 *
 *  \brief Header file for HardwareDescription class.
 */

#ifndef HARDWARE_DESCRIPTION_INCLUDED
#define HARDWARE_DESCRIPTION_INCLUDED

#include <vector>
#include "String.h"

namespace lm
{

    /*! \brief Stores names of all drives and network adapters that are being monitored by the Load Monitor library.
     */
    class HardwareDescription
    {
    public:
        //! Default ctor.
        HardwareDescription(void);

        //! Create empty description.
        HardwareDescription(unsigned long long time_stamp);

        //! Re-allocate drive data.
        void ReallocateDrives(size_t n);

        //! Re-allocate adapter data.
        void ReallocateAdapters(size_t n);

        //! Names of all drives.
        std::vector< String > drives;

        //! Names of all adapters.
        std::vector< String > adapters;

        //! Number of processors.
        size_t                processor_count;

        //! Total size of the available physical memory in bytes.
        unsigned long long    memory_size;

        //! Timestamp.
        unsigned long long    time;
    };
} // namespace lm

#endif
