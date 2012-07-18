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

/*! \file LoadSample.h
 *  \author Branislav Repcek
 *  \date 2. 10. 2006
 *
 *  \brief Header file for the LoadSample class.
 */

#ifndef LOAD_SAMPLE_INCLUDED
#define LOAD_SAMPLE_INCLUDED

#ifndef _WIN32
#include <stddef.h>
#endif

#include <vector>
#include <jni.h>

namespace lm
{

    /*! \brief Storage for load sample data.
     */
    class LoadSample
    {
    public:
        //! Create empty sample.
        LoadSample(void);

        //! Create empty sample with timestamps.
        LoadSample(unsigned long long time, unsigned long long counter);

        //! Create pre-allocated sample.
        LoadSample(size_t processors, size_t networks, size_t drives);

        //! Reallocate storage for processor data.
        void ReallocProcessorData(size_t processors);

        //! Reallocate storage for network data.
        void ReallocNetworkData(size_t networks);

        //! Reallocate drive data.
        void ReallocateDriveData(size_t drives);

        //! CPU usage in percent.
        std::vector< jshort >   processor_usage;
        
        //! Bytes received per sec. for each network adapter.
        std::vector< jint >     network_bytes_in;

        //! Bytes sent per sec. for each network adapter.
        std::vector< jint >     network_bytes_out;

        //! Write speed for each drive.
        std::vector< jlong >    disk_write_bytes;

        //! Read speed for each drive.
        std::vector< jlong >    disk_read_bytes;

        //! Free RAM in bytes.
        unsigned long long                memory_free;

        //! Timestamp, 100 ns intervals since Windows epoch.
        unsigned long long                sample_time;

        //! Timestamp counter of the CPU current thread is running on.
        unsigned long long                tsc;

        //! Number of processes in thread queue.
        unsigned int                      processor_queue;

        //! Total number of running processes.
        unsigned int                      process_count;
    };
} // namespace lm

#endif
