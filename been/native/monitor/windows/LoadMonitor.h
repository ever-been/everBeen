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

/*! \file LoadMonitor.h
 *  \author Branislav Repcek
 *  \date 2. 10. 2006
 *
 *  \brief Header file for LoadMonitor class.
 */

/*! \mainpage notitle
 *
 *  <center><h1>Load Monitor for Windows</h1></center>
 *
 *  <p>This library collects various data about computer utilization on hosts running Windows. Data is collected via
 *  WMI High Performance Data Providers, therefore user account under which detector runs has to have read access to the WMI.
 *  </p>
 *  <br>
 *  <br>
 *  <b>Features:</b>
 *  <ul>
 *    <li>Processor usage of each processor separately.</li>
 *    <li>Network traffic (incomming and outgoing) for all network interfaces.</li>
 *    <li>Disk drive reads and writes for each IDE and SCSI drive.</li>
 *    <li>Free memory.</li>
 *    <li>Process count.</li>
 *    <li>Timestamps (TSC and Windows time).</li>
 *  </ul>
 *  <br>
 *  <b>Requirements:</b>
 *  <ul>
 *    <li>Windows XP, Windows Server 2003</li>
 *    <li>Visual Studio 2005 is <i>not</i> required when using compiled binaries.
 *  </ul>
 *  <p>Code has been developed in Visual Studio 2005 on 32-bit Windows XP SP2. To compile library you need to have JAVA_HOME
 *  set to the directory in which you have Sun JAVA JDK installed.</p>
 *  <br>
 *  <br>
 *  Load Monitor library is part of the <a href="http://been.objectweb.org/">BEEN</a> project developed for
 *  Charles University in Prague, <a href="http://dsrg.mff.cuni.cz/">Distributed Systems Research Group</a>.
 */

#ifndef LOAD_MONITOR_INCLUDED
#define LOAD_MONITOR_INCLUDED

#include <fstream>
#include <iostream>
#include <windows.h>
#include <wbemidl.h>
#include <comdef.h>
#include "../common/String.h"
#include "../common/LoadSample.h"
#include "../common/HardwareDescription.h"

/*  Define this if you want RDTSC in GetTSC method to be always preceded with the serializing instuctions.
 *  If RDTSC is not preceded by serializing instruction, it may be executed out-of-order and therefore may
 *  not return exact TSC. This is especially important if you use TSC to measure speed of some very short
 *  (typicaly hand-coded assebly) routines where in rare cases TSC may be read after all code from method
 *  has been executed. In Load Monitor this is not that important since TSC is used only as a simple 
 *  timestamp and it is not required to be exact. CPUID is used as a serializing instruction.
 */
#define SERIALIZE_TSC

/*  Change this to modify how logging behaves. If this is set to 0, logging is disabled. Value of 1 means, that logs are written to
 *  the standard output. Default value is 1.
 */
#define LOG_ENABLED 1


/*! \brief Load Monitor namespace.
 *
 *  This namespace contains all classes that are specific to the Load Monitor implementation.
 */
namespace lm
{

    /*! \brief Load Monitor which collects performance data about computer.
     *  
     *  WMI Hi-Perf data providers are used to collect all data. On WindowsXP and newer "formatted counters" are used. On older
     *  systems "raw counters" are used.
     */
    class LoadMonitor
    {
    public:

        //! Ctor.
        LoadMonitor(void);

        //! Dtor.
        ~LoadMonitor(void);

        //! Initialize all counters.
        bool Initialize(void);

        //! Free all resources and disconnect from WMI provider.
        bool Terminate(void);

        //! Take performance sample.
        LoadSample TakeSample(void);

        //! Get hardware description.
        HardwareDescription GetHardwareDescription(void);

    private:
    
        //! Assignment.
        LoadMonitor& operator =(LoadMonitor &) { return *this; }

        //! WMI Services.
        IWbemServices         *services;

        //! Data refresher.
        IWbemRefresher        *refresher;

        /*! \brief Base class for enumerator cache.
         *
         *  Classes derived from this base class should store all data related to enumerator of specific feature (e.g.
         *  enumerator ID, IDs of various properties, etc.).
         */
        class PerformanceCounterEnumCache
        {
        public:
            /*! \brief Create new cache object.
             */
            PerformanceCounterEnumCache(void) :
            is_cached(false)
            {
            }

            /*! \brief Free all resources.
             *
             *  Empty method. Note that derived class should call Release() in its destructor to properly free all resources.
             */
            virtual ~PerformanceCounterEnumCache(void)
            {
            }

            /*! \brief Add enumerator from this object to the refresher.
             *
             *  \param configure Pointer to the configure refresher object through which enumerator should be added.
             *  \param services WMI service provider.
             *
             *  \return true on success, false otherwise.
             */
            virtual bool AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services) = 0;

            /*! \brief Release all resources.
             *
             *  This should release all enumerators and other COM objects with their Release methods.
             */
            virtual void Release(void) = 0;

            /*! \brief Query data from enumerator.
             *
             *  This method should query all objects stored in enumeration using GetEnumObjects and then query required properties
             *  and put resulting data in LoadSample provided in parameter. If anything fails, sample should remain unchanged.
             *
             *  \param target Sample to which all data will be put. All data will be allocated as necessary.
             *
             *  \return true on succes, false otherwise. If unsuccessfull, sample will not be modified.
             */
            virtual bool QueryData(LoadSample &target) = 0;

            //! Get objects from given enumeration.
            HRESULT GetEnumObjects(IWbemHiPerfEnum *wbem_enum, size_t *object_count, IWbemObjectAccess ***access_objects);

            //! Clear WMI objects.
            void ClearEnumObjects(IWbemObjectAccess **access_objects, size_t object_count);

        protected:

            //! Are property handles cached?
            bool is_cached;

            /*! \brief Cache handles of all properties.
             *
             *  This method should be called only once in QueryData to cache IDs for all properties needed. This method
             *  provides just wrapper for actual caching that should be implemented in CacheImpl method.
             *
             *  \param access_object Instance of IWbemObjectAccess object which contains properties that require handles to be cached.
             *
             *  \return true if IDs have been successfully cached, false otherwise. If caching failed, QueryData should also fail.
             */
            bool Cache(IWbemObjectAccess *access_object)
            {
                if (!access_object)
                {
                    return false;
                }

                if (!is_cached)
                {
                    is_cached = CacheImpl(access_object);
                }

                return is_cached;
            }

            //! Property caching implementation.
            virtual bool CacheImpl(IWbemObjectAccess *access_object) = 0;
        };

        /*! \brief Cache for data related to processor usage.
         */
        class ProcessorEnumCache : public PerformanceCounterEnumCache
        {
        public:
            //! Create new cache object.
            ProcessorEnumCache(void);

            //! Destory cache object.
            virtual ~ProcessorEnumCache(void);

            //! Add object to the refresher.
            virtual bool AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services);

            //! Free resources.
            virtual void Release(void);

            //! Query data.
            virtual bool QueryData(LoadSample &target);

            //! Get number of processors.
            size_t GetCount();

        private:
            //! Enumerator for Win32_PerfRawData_PerfOS_Processor or Win32_PerfFormattedData_PerfOS_Processor classes.
            IWbemHiPerfEnum   *enum_processor_usage;
            
            //! ID of the CPU usage enumeration.
            long              enum_processor_usage_id;

            //! ID of the "PercentProcessorTime" property.
            long              id_processor_time;

            //! ID of the "Name" property.
            long              id_processor_name;

        protected:
            //! Cache property handles.
            virtual bool CacheImpl(IWbemObjectAccess *access_object);
        };

        /*! \brief Cache for network monitoring data.
         */
        class NetworkEnumCache : public PerformanceCounterEnumCache
        {
        public:
            //! Create new cache object.
            NetworkEnumCache(void);

            //! Free used resources.
            virtual ~NetworkEnumCache(void);

            //! Add object to the refresher.
            virtual bool AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services);

            //! Free resources.
            virtual void Release(void);

            //! Query data.
            virtual bool QueryData(LoadSample &target);

            //! Query names or all interfaces.
            bool QueryNames(HardwareDescription &target);

        private:
            //! Enumerator for Win32_PerfRawData_Tcpip_NetworkInterface or Win32_PerfFormattedData_Tcpip_NetworkInterface.
            IWbemHiPerfEnum   *enum_network;

            //! ID of the network enumerator.
            long              enum_network_id;

            //! ID of the "BytesReceivedPerSec" property.
            long              id_bytes_received;

            //! ID of the "BytesSentPerSec" property.
            long              id_bytes_sent;

            //! ID of the "Name" property.
            long              id_name;

        protected:
            //! Cache property handles.
            virtual bool CacheImpl(IWbemObjectAccess *access_object);
        };

        /*! \brief Cache for memory monitoring data.
         */
        class MemoryEnumCache : public PerformanceCounterEnumCache
        {
        public:
            //! Create new cache object.
            MemoryEnumCache(void);

            //! Free used resources.
            virtual ~MemoryEnumCache(void);

            //! Add object to the refresher.
            virtual bool AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services);

            //! Free resources.
            virtual void Release(void);

            //! Query data.
            virtual bool QueryData(LoadSample &target);

            //! Get total size of physcial memory in bytes.
            unsigned long long GetSize();

        private:
            //! Enumerator for Win32_PerfRawData_PerfOS_Memory or Win32_PerfFormattedData_PerfOS_Memory classes.
            IWbemHiPerfEnum   *enum_memory_usage;

            //! ID of the memory enumerator.
            long              enum_memory_usage_id;

            //! ID of the "AvailableBytes" property.
            long              id_available;

        protected:
            //! Cache property handles.
            virtual bool CacheImpl(IWbemObjectAccess *access_object);
        };

        /*! \brief Cache for system related counters.
         */
        class SystemEnumCache : public PerformanceCounterEnumCache
        {
        public:
            //! Create new cache object.
            SystemEnumCache(void);

            //! Free used resources.
            virtual ~SystemEnumCache(void);

            //! Add object to the refresher.
            virtual bool AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services);

            //! Free resources.
            virtual void Release(void);

            //! Query data.
            virtual bool QueryData(LoadSample &target);

        private:
            //! Enumerator for Win32_PerfRawData_PerfOS_System or Win32_PerfFormattedData_PerfOS_System.
            IWbemHiPerfEnum   *enum_system;

            //! ID of the system enumerator.
            long              enum_system_id;

            //! ID of the "Processes" property.
            long              id_processes;

            //! ID of the "ProcessorQueueLength" property.
            long              id_processor_queue;

        protected:
            //! Cache property handles.
            virtual bool CacheImpl(IWbemObjectAccess *access_object);
        };

        /*! \brief Cache for drive releated data.
         */
        class DriveEnumCache : public PerformanceCounterEnumCache
        {
        public:
            //! Create new cache object.
            DriveEnumCache(void);

            //! Free used resources.
            virtual ~DriveEnumCache(void);

            //! Add object to the refresher.
            virtual bool AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services);

            //! Free resources.
            virtual void Release(void);

            //! Query data.
            virtual bool QueryData(LoadSample &target);

            //! Query names of all drives.
            bool QueryNames(HardwareDescription &target);

        private:
            //! Enumerator for Win32_PerfRawData_PerfDisk_PhysicalDisk or Win32_PerfFormattedData_PerfDisk_PhysicalDisk.
            IWbemHiPerfEnum   *enum_drives;

            //! ID of the drive enumerator.
            long              enum_drives_id;

            //! ID of the "DiskWriteBytesPerSec" property.
            long              id_disk_write_bytes;

            //! ID of the "DiskReadBytesPerSec" property.
            long              id_disk_read_bytes;

            //! ID of the "Name" property.
            long              id_disk_name;

        protected:
            //! Cache property handles.
            virtual bool CacheImpl(IWbemObjectAccess *access_object);
        };

        //! CPU data cache.
        ProcessorEnumCache    processor;

        //! Memory data cache.
        MemoryEnumCache       memory;

        //! Network data cache.
        NetworkEnumCache      network;

        //! Drive data cache.
        DriveEnumCache        drive;

        //! System data cache.
        SystemEnumCache       system;

        //! Prevent copying of the monitor.
        LoadMonitor(const LoadMonitor &) { }

        //! Log info message to the output.
        void LogInfo(const char *message) const;

        //! Log error message to the output.
        void LogError(const char *message) const;

        //! Get current time in 100 ns intervals since Windows epoch.
        unsigned long long GetSampleTime() const;
        
        /*! \brief Get CPU TSC.
         *
         *  \return Timestamp counter for the CPU on which current thread is running.
         */
        unsigned long long GetTSC(void) const
        {
            __asm
            {
#ifdef SERIALIZE_TSC
                mov     eax, 0
                cpuid
#endif
                rdtsc
            }
        }
    };
}

#endif
