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

/*! \file LoadSample.cpp
 *  \author Branislav Repcek
 *  \date 3. 10. 2006
 *
 *  \brief Implementation of the methods from LoadSample class.
 */

// Disable warnings about deprecated "unsecure" functions from STL in Visual Studio 2005+.
#if _MSC_VER >= 1400
#define _SCL_SECURE_NO_DEPRECATE 1
#endif

#include <algorithm>
#include <vector>
#include <jni.h>
#include "LoadSample.h"

using namespace std;

namespace lm
{

    /*! Creates sample with no data.
     */
    LoadSample::LoadSample(void) :
    memory_free(0),
    sample_time(0),
    tsc(0),
    processor_queue(0),
    process_count(0)
    {
    }


    /*! Create sample with no data but given timestamps.
     *
     *  \param time Sample time, 100 ns intervals since Windows epoch.
     *  \param counter Timestamp counter of the CPU.
     */
    LoadSample::LoadSample(unsigned long long time, unsigned long long counter) :
    memory_free(0),
    sample_time(time),
    tsc(counter),
    processor_queue(0),
    process_count(0)
    {
    }

    /*! Creates sample with pre-allocated arrays for processor usage, network traffic and drive usage.
     *
     *  \param processors Number of processors.
     *  \param networks Number of network adapters.
     *  \param drives Number of disk drives.
     */
    LoadSample::LoadSample(size_t processors, size_t networks, size_t drives) :
    processor_usage(processors, 0),  // I <3 STL :)
    network_bytes_in(networks, 0),
    network_bytes_out(networks, 0),
    disk_write_bytes(drives,0),
    disk_read_bytes(drives, 0),
    memory_free(0),
    sample_time(0),
    tsc(0),
    processor_queue(0),
    process_count(0)
    {
    }

    /*! \param processors New number of processors.
     */
    void LoadSample::ReallocProcessorData(size_t processors)
    {
        vector< jshort >().swap(processor_usage);
        processor_usage.resize(processors, 0);
    }

    /*! \param networks New number of network adapters/interfaces.
     */
    void LoadSample::ReallocNetworkData(size_t networks)
    {
        vector< jint >().swap(network_bytes_in);
        vector< jint >().swap(network_bytes_out);
        network_bytes_in.resize(networks, 0);
        network_bytes_out.resize(networks, 0);
    }

    /*! \param drives New number of disk drives.
     */
    void LoadSample::ReallocateDriveData(size_t drives)
    {
        vector< jlong >().swap(disk_write_bytes);
        vector< jlong >().swap(disk_read_bytes);
        disk_write_bytes.resize(drives, 0);
        disk_read_bytes.resize(drives, 0);
    }
}
