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
 *  \date 5. 12. 2006
 *
 *  \brief Header files for the LoadMonitor class from Linux port of the Load Monitor.
 */

/*! \mainpage notitle
 *
 *  <center><h1>Load Monitor for Linux</h1></center>
 *
 *  <p>This library collects various data about computer utilization on hosts running Linux. Most of the data is read from
 *  various files in proc pseudo-filesystem.</p>
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
 *    <li>Kernel 2.5.70 (will work on older, but with limited functionality). Probably does not work on 2.4 kernels (untested).</li>
 *    <li>librt version 1 for real-time high-resolution clock.</li>
 *  </ul>
 *  <p>Code has been developed with G++ 4.1.0 (32-bit) with libstdc++ version 6.0. Should also work with libstdc++ 5, but of course
 *  requires recompilation.
 *  To compile library you need to have JAVA_HOME set to the directory in which you have Sun JAVA JDK installed. Code has been
 *  tested with JDK version 1.5.0_09.</p>
 *  <br>
 *  <br>
 *  Load Monitor library is part of the <a href="http://been.objectweb.org/">BEEN</a> project developed for
 *  Charles University in Prague, <a href="http://dsrg.mff.cuni.cz/">Distributed Systems Research Group</a>.
 */

#ifndef LOAD_MONITOR_INCLUDED
#define LOAD_MONITOR_INCLUDED

#include <string>
#include <utility>
#include "../common/LoadSample.h"
#include "../common/HardwareDescription.h"

/* Change this to modify how logging behaves. If this is set to 0, logging is disabled. Value of 1 means, that logs are written to
 * the standard output. Default value is 1.
 */
#define LOG_ENABLED 1

#include <string>
#include <vector>
#include "../common/LoadSample.h"
#include "../common/HardwareDescription.h"

/*! \brief Load Monitor namespace.
 *
 *  This namespace contains all classes that are specific to the Load Monitor implementation.
 */
namespace lm
{
    /*! \brief Collects data about current system utilization.
     * 
     *  This class collects all data about system utilization. Most of the data are read from
     *  the files in /proc directory. Kernel 2.5.70 or newer is required for all features to
     *  work. On older kernels some data may not be available.
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
        //! Prevent copying of the monitor.
        LoadMonitor(const LoadMonitor &) { }

        /*! \brief Helper class which provides base for all stats providers.
         */
        class StatsProvider
        {
        public:
            //! Constructor.
            StatsProvider(void);
            
            //! Destructor.
            virtual ~StatsProvider(void);
            
            /*! Query data and store them in given sample.
             * 
             *  \param target Sample which will receive data collected by this provider.
             * 
             *  \return true on success, false otherwise.
             */
            virtual bool QueryData(LoadSample &target) = 0;
            
            /*! Query data and store their descriptions.
             * 
             *  \param target Structure which will receive data collected by this provider.
             * 
             *  \return true on success, false otherwise.
             */
            virtual bool QueryData(HardwareDescription &target) = 0;
            
        protected:
            //! Read lines from text file.
            bool ReadLines(const char *file_name, std::vector< std::string > &target) const;
            
            //! Extract value of given column from the line.
            template< typename T >
            bool GetFieldValue(const std::string &line, const size_t index, 
                    T &result, const size_t start_at = 0, const char *delim = " \t") const;
            
            //! Extract name of the field.
            std::pair< std::string, size_t > GetFieldName(const std::string &line, 
                    const std::string &end = ":", const size_t start_at = 0) const;
                    
            //! Test if string begins with another string.
            bool BeginsWith(const std::string &str, const std::string &what) const;
            
            //! Get version code of the current kernel.
            static int GetKernelVersionCode(void);
            
        private:
            //! Empty private copy-ctor to prevent copying.
            StatsProvider(const StatsProvider &) { }
        };
        
        /*! \brief Collects data about memory usage.
         */
        class MemoryStats : public StatsProvider
        {
        public:
            //! Constructor.
            MemoryStats(void);
            
            //! Destructor.
            virtual ~MemoryStats(void);
            
            //! Collect data and store them in given sample.
            virtual bool QueryData(LoadSample &target);
            
            //! Collect data and store their descriptions.
            virtual bool QueryData(HardwareDescription &target);
        };
        
        /*! \brief Collects data about processor usage.
         */
        class ProcessorStats : public StatsProvider
        {
        public:
            //!Constructor.
            ProcessorStats(void);
            
            //! Destructor.
            virtual ~ProcessorStats(void);
        
            //! Query data and store them in the sample.
            virtual bool QueryData(LoadSample &target);
            
            //! Query data descriptions and store them.
            virtual bool QueryData(HardwareDescription &target);
            
        private:
            
            /*! \brief Data read from /proc/stat for on CPU.
             * 
             *  This stores all numbers for one processor that are in the /proc/stat file.
             *  Each cpu line looks like this:
             *  <br>
             *    cpu? USER NICE SYSTEM IDLE IOWAIT IRQ SOFTIRQ
             *  <br>
             *  where ? is index of the cpu (or space if line represents global stats)
             *  and all USER ... SOFTIRQ are times that given processor spent doing tasks
             *  associated with given column. Number of columns is different between
             *  different kernels, but first four field are supported always.
             *  All fields are in USER_HZ, which usually is 1/100th of a second (but may be
             *  different and I know of no sane way of determining that value).
             *  We are usually interested only in the fourth column which counts time spent
             *  in the idle thread, but we still need to consider other columns when calculating
             *  CPU usage.
             */
            class OneCpuStats // well, 3rd level nested type, soo coooool ;) and there's more...
            {                 // this should probably be redesigned, but I don't care anymore
            public:
                //! Empty default ctor.
                OneCpuStats(void) { }
                
                //! Parse line from the file.
                OneCpuStats(const std::string &line);
                
                //! Subtract corresponding values from two caches.
                OneCpuStats operator -(const OneCpuStats &b) const;
                
                //! Sum values in this cache.
                long long Sum(void) const;
                
                //! Index of the column with "idle time".
                static const size_t CPU_IDLE_COLUMN = 3;
                
                //! Data from file.
                std::vector< long long > stat_cache;
            };
            
            //! Cached stats for each cpu from previous run.
            std::vector< OneCpuStats > cpus;
        };
        
        /*! \brief Collects data about network traffic on the computer.
         */
        class NetworkStats : public StatsProvider
        {
        public:
            //! Constructor.
            NetworkStats(void);
            
            //! Destructor.
            virtual ~NetworkStats(void);
            
            //! Collect data and store them in the sample.
            virtual bool QueryData(LoadSample &target);
            
            //! Collect data and store their descriptions.
            virtual bool QueryData(HardwareDescription &target);
            
        private:
            /*! \brief Stores data read from /proc/net/dev file for one interface.
             * 
             *  This class stores all data available for one interface in the file /proc/net/dev.
             */
            class OneInterfaceStats
            {
            public:
                //! Parse data from string.
                OneInterfaceStats(const std::string &line);
                
                //! Number of fields available for incomming data.
                static const size_t RECV_FIELDS = 8;
                
                //! Number of fields available for outgoing data.
                static const size_t SEND_FIELDS = 8;
                
                //! Length of the interface name.
                static const size_t INTERFACE_NAME_LENGTH = 16;
                
                //! Interface name.
                char      name[INTERFACE_NAME_LENGTH];
                
                //! Stats about data received on given interface.
                long long recv[RECV_FIELDS];
                
                //! Stats about data sent on given interface.
                long long send[SEND_FIELDS];
                
                //! Index of "number of bytes received" field.
                static const size_t RECV_BYTES = 0;
                
                //! Index of "number of packets received" field.
                static const size_t RECV_PACKETS = 1;
                
                //! Index of "number of receive errors" field.
                static const size_t RECV_ERRORS = 2;
                
                //! Index of "number of drops" field.
                static const size_t RECV_DROPS = 3;
                
                //! Index of "FIFO" field.
                static const size_t RECV_FIFO = 4;
                
                //! Index of "frame count" field.
                static const size_t RECV_FRAME = 5;
                
                //! Index of "compressed" field.
                static const size_t RECV_COMPRESSED = 6;
                
                //! Index of "multicast" field.
                static const size_t RECV_MULTICAST = 7;
            
                //! Index of "number of bytes sent" field.
                static const size_t SEND_BYTES = 0;
                
                //! Index of "number of packets sent" field.
                static const size_t SEND_PACKETS = 1;
                
                //! Index of "number of send errors" field.
                static const size_t SEND_ERRORS = 2;
                
                //! Index of "number of drops" field.
                static const size_t SEND_DROPS = 3;
                
                //! Index of "FIFO" field.
                static const size_t SEND_FIFO = 4;
                
                //! Index of "collision count" field.
                static const size_t SEND_COLLISION = 5;
                
                //! Index of "carrier" field.
                static const size_t SEND_CARRIER = 6;
                
                //! Index of "compressed" field.
                static const size_t SEND_COMPRESSED = 7;
            };
            
            //! Timestamp of the last measurement.
            unsigned long long last_time;
            
            //! List of pairs of read-write bytes for each interface.
            std::vector< std::pair< long long, long long > > data;
        };
        
        /*! \brief Collects data about drive usage on the host.
         */
        class DriveStats : public StatsProvider
        {
        public:
            //! Constructor.
            DriveStats(void);
            
            //! Destructor.
            virtual ~DriveStats(void);
            
            //! Collect data and store them in the sample.
            virtual bool QueryData(LoadSample &target);
            
            //! Collect data and store their descriptions.
            virtual bool QueryData(HardwareDescription &target);
            
        private:
            /*! \brief Stores data about one disk drive from /proc/diskstats file.
             * 
             *  All field except ninth one (IOS_IN_PROGRESS) are cummulative since last
             *  boot.
             *  More details about the diskstats file can be found in the kernel sources
             *  in Documenttation/iostats.txt file.
             */
            class OneDriveStats
            {
            public:
                OneDriveStats(const std::string &line);
                
                //! Test if this drive is physical drive or partition.
                bool IsDriveDevice(void) const;
                
                //! Is this device connceted through the IDE bus?
                bool IsIDE(void) const;
                
                //! Is this device connected through the SCSI bus?
                bool IsSCSI(void) const;
                
                //! Major device number.
                int             major;
                
                //! Minor device number.
                int             minor;
                
                //! Length of the device name.
                static const size_t DRIVE_NAME_LENGTH = 16;
                
                //! Device name.
                char            name[DRIVE_NAME_LENGTH];
                
                //! Total number of data columns for each drive (2.6.0+).
                static const size_t COLUMN_COUNT = 11;
                
                //! Stats fields.
                unsigned long   stat_fields[COLUMN_COUNT];
                
                //! Size of the sector in bytes.
                static const long SECTOR_SIZE = 512;
                
                //! Total number of the reads.
                static const size_t READS_ISSUED_COUNT = 0;
                
                //! Total number of merged reads.
                static const size_t READS_MERGED = 1;
                
                //! Total number of sectors read.
                static const size_t READ_SECTORS = 2;
                
                //! Total time spent reading data from the drive.
                static const size_t READ_MILLISECONDS = 3;
                
                //! Total number of writes completed.
                static const size_t WRITES_COMPLETED = 4;
                
                //! Total number of merged writes.
                static const size_t WRITES_MERGED = 5;
                
                //! Total number of sectors written.
                static const size_t WRITE_SECTORS = 6;
                
                //! Total time spent writing.
                static const size_t WRITE_MILLISECONDS = 7;
                
                //! Number of IO operations in progress.
                static const size_t IOS_IN_PROGRESS = 8;
                
                //! Total time spent doing IO operations.
                static const size_t IO_MILLISECONDS = 9;
                
                //! Weighted time spent doing IO operations.
                static const size_t IO_WEIGHTED_MILLISECONDS = 10;
            };
            
            //! Kernel version code.
            int kernel_version;
            
            /*! \brief Stats for the IDE drives. 
             * 
             *  First item of the pair is number of sectors read, second is number of 
             *  sectors written.
             */
            std::vector< std::pair< long, long > > data;
            
            //! Timestamp of the last measurement.
            unsigned long long                     last_time;
        };
        
        /*! \brief Collects various data about current system.
         */
        class SystemStats : public StatsProvider
        {
        public:
            //! Constructor.
            SystemStats(void);
            
            //! Destructor.
            virtual ~SystemStats(void);
            
            //! Collect data and store them in the sample.
            virtual bool QueryData(LoadSample &target);
            
            //! Collect data and store their descriptions.
            virtual bool QueryData(HardwareDescription &);
        };
        
        //! Memory stats collector.
        MemoryStats     memory;
        
        //! Processor stats collector.
        ProcessorStats  processor;
        
        //! Network stats collector.
        NetworkStats    network;
        
        //! System stats collector.
        SystemStats     system;
        
        //! Drive stats collector.
        DriveStats      drive;
        
        //! Log info message to the output.
        void LogInfo(const char *message) const;

        //! Log error message to the output.
        void LogError(const char *message) const;

        //! Get current time in 100 ns intervals since Windows epoch.
        static unsigned long long GetSampleTime();
        
        /*! \brief Get CPU TSC.
         *
         *  \return Timestamp counter of the CPU on which current thread is running.
         */
        unsigned long long GetTSC(void) const
        {
            unsigned long long result = 0;
            
            asm volatile
            (
                "rdtsc"
                : "=A"(result)
            );
            
            return result;
        }
    }; // class LoadMonitor
    
} // namespace lm

#endif
