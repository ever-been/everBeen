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

/*! \file LoadMonitor.cpp
 *  \author Branislav Repcek
 *  \date 5. 12. 2006
 *
 *  \brief Linux port of the LoadMonitor class.
 */

#include <iostream>
#include <iomanip>
#include <string>
#include <sstream>
#include <fstream>
#include <utility>
#include <vector>
#include <utility>
#include <string.h>
#include <time.h>
#include <ctype.h>
#include <sys/time.h>
#include <sys/utsname.h>
#include <jni.h>
#include "LoadMonitor.h"
#include "../common/String.h"
#include "../common/HardwareDescription.h"
#include "../common/LoadSample.h"

using namespace std;

/*! \brief Calculate kernel version code from version numbers supplied by the <code>uname</code>.
 */
#define KERNEL_VERSION_CODE(major, minor, release) (0x10000 * (major) + 0x100 * (minor) + (release))

namespace lm
{
    /*! \brief Build string containing current time.
     *
     *  \return String with time in format "YYYY/MM/DD hh:mm:ss.msec"
     */
    string TimeString(void)
    {
        timeval cur_time;
        
        gettimeofday(&cur_time, NULL);
        
        tm *cur_time_c = localtime(&cur_time.tv_sec);
        
        stringstream stream;

        stream << cur_time_c->tm_year << '/';

        stream.width(2);
        stream.fill('0');

        stream << cur_time_c->tm_mon << setw(1) << '/' << setw(2)
               << cur_time_c->tm_mday << setw(1) << ' ' << setw(2)
               << cur_time_c->tm_hour << setw(1) << ':' << setw(2)
               << cur_time_c->tm_min << setw(1) << ':' << setw(2)
               << cur_time_c->tm_sec << setw(1) << '.' << setw(3)
                << (cur_time.tv_usec / 1000);

        return stream.str();
    }
    
    /*! \brief Convert time to the internal timestamp.
     *  
     *  Convert seconds since the Unix epoch and nanoseconds to the internal time format
     *  (number of 100 ns intervals since 1.1. 1601).
     *  
     *  \param secs Number of seconds since the beginning of the Unix epoch.
     *  \param nanos Nanoseconds since the beginning of the current second.
     * 
     *  \return Number of 100 ns intervals since 1.1. 1601.
     */
    unsigned long long ConvertTS(unsigned long long secs, unsigned long long nanos)
    {
        /* Number of seconds between 1.1.1601 and 1.1.1970.
         */
        const unsigned long long SECONDS_BETWEEN_EPOCHS = 11644473600ull;
        
        /* Number of 100 nanosecond intervals in one second.
         */
        const unsigned long long SECONDS_TO_100NS = 10000000ull;
        
        return (secs + SECONDS_BETWEEN_EPOCHS) * SECONDS_TO_100NS + (nanos / 100);
    }
    
    /*!
     */
    LoadMonitor::LoadMonitor(void)
    {
    }


    /*!
     */
    LoadMonitor::~LoadMonitor(void)
    {
    }

    /*! \return true on success, false otherwise.
     */
    bool LoadMonitor::Initialize(void)
    {
        return true;
    }
    
    /*! \return true on success, false otherwise.
     */
    bool LoadMonitor::Terminate(void)
    {
        return true;
    }
    
    /*! \return Sample containing data about current system utilization.
     */
    LoadSample LoadMonitor::TakeSample(void)
    {
        unsigned long long tsc = GetTSC();
        unsigned long long sample_time = LoadMonitor::GetSampleTime();
        
        LoadSample sample = LoadSample(sample_time, tsc);
        
        if (!memory.QueryData(sample))
        {
            LogError("Memory query failed.");
        }
        
        if (!network.QueryData(sample))
        {
            LogError("Network query failed.");
        }
        
        if (!drive.QueryData(sample))
        {
            LogError("Drive query failed.");
        }
        
        if (!system.QueryData(sample))
        {
            LogError("System query failed.");
        }
        
        if (!processor.QueryData(sample))
        {
            LogError("Processor query failed.");
        }
        
        return sample;
    }
    
    /*! \return Description of the hardware parts that are being monitored by the monitor.
     */
    HardwareDescription LoadMonitor::GetHardwareDescription(void)
    {
        unsigned long long sample_time = LoadMonitor::GetSampleTime();
        
        HardwareDescription description;
        
        description.time = sample_time;
        
        if (!memory.QueryData(description))
        {
            LogError("Memory info query failed.");
        }
        
        if (!network.QueryData(description))
        {
            LogError("Network info query failed.");
        }
        
        if (!drive.QueryData(description))
        {
            LogError("Drive info query failed.");
        }
        
        if (!system.QueryData(description))
        {
            LogError("System info query failed.");
        }
        
        if (!processor.QueryData(description))
        {
            LogError("Processor info query failed.");
        }
        
        return description;
    }
    
    /*! This method uses clock_gettime function as a primary source of the time information.
     *  This function is available only on newer kernels (2.6) with sufficiently new libc
     *  (version 6 works fine).
     *
     *  \return Number of 100 ns intervals since 1.1. 1601.
     */
    unsigned long long LoadMonitor::GetSampleTime(void)
    {
        timespec tspec;
        
        int res = clock_gettime(CLOCK_REALTIME, &tspec);
        
        if (res == 0)
        {
            return ConvertTS(tspec.tv_sec, tspec.tv_nsec);
        }
        else
        {
            // most precise timer failed, try lower precision one
            timeval tval;
            int res2 = gettimeofday(&tval, NULL);
            
            if (res2 == 0)
            {
                return ConvertTS(tval.tv_sec, tval.tv_usec * 1000);
            }
            else
            {
                // okay, failed too, try this one instead
                time_t t = time(NULL);
                return ConvertTS(t, 0);
            }
        }
    }
    
    /*! \param message Message to write out.
     */
    void LoadMonitor::LogInfo(const char *message) const
    {
#if LOG_ENABLED == 1
        cout << TimeString() << " INFO " << message << endl;
#endif
    }
    
    /*! \param message Message to write out.
     */
    void LoadMonitor::LogError(const char *message) const
    {
#if LOG_ENABLED == 1
        cout << TimeString() << " ERROR " << message << endl;
#endif
    }
    
    /*! 
     */
    LoadMonitor::StatsProvider::StatsProvider(void)
    {
    }
    
    /*!
     */
    LoadMonitor::StatsProvider::~StatsProvider(void)
    {
    }
    
    /*! This will read all lines from the text file into given vector.
     * 
     *  \param file_name Name of the file to read.
     *  \param target Vector which will receive lines read from the file. Note that lines
     *         are added at the end of the vector and data from vector are not removed automatically.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::StatsProvider::ReadLines(const char *file_name, 
        std::vector< std::string > &target) const
    {
        ifstream file_in;
    
        file_in.open(file_name, ios_base::in);
    
        if (!file_in.is_open())
        {
            return false;
        }
    
        std::string line;
    
        while (!file_in.eof())
        {
            getline(file_in, line);
        
            if (file_in.bad())
            {
                file_in.close();
                return false;
            }
        
            target.push_back(line);
        }
    
        file_in.close();
    
        return true;
    }
            
    /*! Retrieve value of the column with given index. Columns can be separated by custom
     *  delimiter characters. Value will be automatically converted to the target type.
     * 
     *  \param line String which contains requested value.
     *  \param index Index of the column which contains given value. First column has index of 1.
     *  \param start_at Index of the first character (or some delimiter directly before) 
     *         of the first column.
     *  \param delims Delimiters that are between columns. Each character of this string is threated
     *         as a delimiter.
     * 
     *  \param result Reference to the variable which will receive value of the given column.
     *         Value is automatically converted to the target type via the stream operator >>.
     *         If an error occured during conversion or if column with given index does not
     *         exist, original value remains unchanged.
     * 
     *  \return true on success, false if field with given index does not exist or if
     *          an error occured while converting string to the value of target type.
     */
    template< typename T >
    bool LoadMonitor::StatsProvider::GetFieldValue(const std::string &line, const size_t index, 
        T &result, const size_t start_at, const char *delims) const
    {
        size_t field_index = 0;
        bool in_field = false;
        size_t pos = start_at;
    
        for ( ; pos < line.size(); ++pos)
        {
            if (strchr(delims, line[pos]) == NULL)
            {
                if (!in_field)
                {
                    in_field = true;
                    ++field_index;
                    if (field_index == index)
                    {
                        break;
                    }
                    if (field_index > index)
                    {
                        return false;
                    }
                }
            }
            else
            {
                in_field = false;
            }
        }
    
        if (field_index == index)
        {
            stringstream stream;

            for ( ; (pos < line.size()) && (!isspace(line[pos])); ++pos)
            {
                stream << line[pos];
            }
        
            stream >> result;
            return !stream.fail();
        }
    
        return false;
    }
            
    /*! Retrieve name of the current line. Name is string that does not contain white-space
     *  characters at the beginning or at the end (white-spaces in the middle are allowed).
     * 
     *  \param line String which contains data and name.
     *  \param end String which marks end of the field name. Default value is ":" which is
     *         suitable for files in the /proc directory.
     *  \param start_at First character of the name. May be whitespace. Default value is 0.
     * 
     *  \return Pair containing name of the field and index of the first character after the
     *          separator string after name.
     */
    std::pair< std::string, size_t > 
    LoadMonitor::StatsProvider::GetFieldName(const std::string &line, 
        const std::string &end, const size_t start_at) const
    {
        stringstream stream;
    
        size_t name_start = start_at;
        size_t name_end = line.find(end, start_at);
        size_t value_pos = name_end + end.size();
    
        if (name_end == line.npos)
        {
            name_end = line.size();
            value_pos = line.npos;
        }    
    
        for ( ; (name_start < name_end) && (isspace(line[name_start])); ++name_start) ;
        for (--name_end ; (name_end > name_start) && (isspace(line[name_end])); --name_end) ;
    
        return make_pair(line.substr(name_start, name_end - name_start + 1), value_pos);
    }
    
    /*! \param str String to test.
     *  \param what String to search for.
     * 
     *  \return true if string begins with given substring, false otherwise.
     */
    bool LoadMonitor::StatsProvider::BeginsWith(const std::string &str, const std::string &what) const
    {
        if (str.size() < what.size())
        {
            return false;
        }
        
        size_t i = 0;
        
        while ((str[i] == what[i]) && (i < what.size())) ++i;
        
        return (str[i] == what[i]) || (i == what.size());
    }
    
    /*! \return Version code of the kernel. Code can be calculated as 
     *          <code>0x10000 * major + 0x100 * minor + release</code>, where
     *          <i>major</i>, <i>minor</i> and <i>release</i> are version
     *          numbers as determined by <code>uname</code>.
     *          Zero is returned if the <code>uname</code> call or string 
     *          conversion failed.
     */
    int LoadMonitor::StatsProvider::GetKernelVersionCode(void)
    {
        utsname uts;
        
        if (uname(&uts) == -1)
        {
            // Call failed -> exit.
            return 0;
        }
        
        int major = 0;
        int minor = 0;
        int release = 0;
        
        if (sscanf(uts.release, "%d.%d.%d", &major, &minor, &release) < 3)
        {
            // Unable to parse all three fields -> exit
            return 0;
        }
        
        return KERNEL_VERSION_CODE(major, minor, release);
    }

    
    /*!
     */
    LoadMonitor::MemoryStats::MemoryStats(void)
    {
    }
    
    /*!
     */
    LoadMonitor::MemoryStats::~MemoryStats(void)
    {
    }
    
    /*! Reads amount of the free memory from /proc/meminfo file.
     * 
     *  \param sample Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::MemoryStats::QueryData(LoadSample &sample)
    {
        vector< string > lines;
        bool res = ReadLines("/proc/meminfo", lines);
        
        if (!res)
        {
            return false;
        }
        
        for (vector< string >::const_iterator it = lines.begin(); it != lines.end(); ++it)
        {
            if (BeginsWith(*it, "MemFree"))
            {
                unsigned long long value = 0;
                bool r = GetFieldValue(*it, 2, value);
                
                if (r)
                {
                    // Convert from kB to B
                    sample.memory_free = value << 10;
                }
                
                return r;
            }
        }
        
        return false;
    }
    
    /*! Reads total size of the physical memory from /proc/meminfo file.
     * 
     *  \param description Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::MemoryStats::QueryData(HardwareDescription &description)
    {
        vector< string > lines;
        bool res = ReadLines("/proc/meminfo", lines);
        
        if (!res)
        {
            return false;
        }
        
        for (vector< string >::const_iterator it = lines.begin(); it != lines.end(); ++it)
        {
            if (BeginsWith(*it, "MemTotal"))
            {
                unsigned long long value = 0;
                bool r = GetFieldValue(*it, 2, value);
                
                if (r)
                {
                    // Value in the file in in kB, so convert it to B
                    description.memory_size = value << 10;
                }
                
                return r;
            }
        }
        
        return false;
    }
    
    /*! This will parse given string and extract all columns with numbers into the vector.
     *  
     *  \param line One line from the /proc/stat file which contains data about the CPU.
     */
    LoadMonitor::ProcessorStats::OneCpuStats::OneCpuStats(const string &line)
    {
        if (line.size() == 0)
        {
            return;
        }
        
        stringstream stream;
        
        stream << line;
        
        // Parse "name" of the cpu and throw it away
        {
            string name;
            stream >> name;
        }
        
        // Parse whole line
        while (!stream.fail())
        {
            long long column;

            // Get next number and store in cache
            stream >> column;
            if (!stream.fail())
            {
                stat_cache.push_back(column);
            }
        }
    }
    
    /*! Subtracts two caches. Works like n-dimensional vector subtraction.
     * 
     *  \param b Cache to subtract from this.
     * 
     *  \return Result of "vector" subtraction of the two cache items.
     */
    LoadMonitor::ProcessorStats::OneCpuStats
    LoadMonitor::ProcessorStats::OneCpuStats::operator -(const LoadMonitor::ProcessorStats::OneCpuStats &b) const
    {
        OneCpuStats result;
        
        vector< long long >::const_iterator a_end = stat_cache.end();
        vector< long long >::const_iterator b_end = b.stat_cache.end();
        vector< long long >::const_iterator a_it = stat_cache.begin();
        vector< long long >::const_iterator b_it = b.stat_cache.begin();
        
        for ( ; (a_it != a_end) && (b_it != b_end); ++a_it, ++b_it)
        {
            result.stat_cache.push_back(*a_it - *b_it);
        }
        
        return result;
    }
                
    /*! Sums all values stored in this cache.
     *
     *  \return Sum of all items in this cache.
     */
    long long LoadMonitor::ProcessorStats::OneCpuStats::Sum(void) const
    {
        long long result = 0;
        
        for (vector< long long >::const_iterator it = stat_cache.begin(); 
             it != stat_cache.end(); 
             ++it)
        {
            result += *it;
        }
        
        return result;
    }
    
    /*! This will parse /proc/stat file and determine number of processors. It is assumed,
     *  that this number does not change while monitor is running (well, hot-swap processors
     *  are not that common :) ).
     */
    LoadMonitor::ProcessorStats::ProcessorStats(void)
    {
        vector< string > lines;
        bool res = ReadLines("/proc/stat", lines);
        
        if (!res || (lines.size() <= 1))
        {
            return;
        }
        
        // Parse lines from the second one up (first line is global, we don't care about that).
        for (vector< string >::const_iterator it = lines.begin() + 1; it != lines.end(); ++it)
        {
            if (!BeginsWith(*it, "cpu"))
            {
                // Read all cpus -> job done
                break;
            }
            
            // Parse data and add to the cache.
            cpus.push_back(OneCpuStats(*it));
        }
    }
    
    /*!
     */
    LoadMonitor::ProcessorStats::~ProcessorStats(void)
    {
    }

    /*! This will parse /proc/stat file and extract data about all processors. If at least
     *  one sample has been taken prior this call, statistics will be calculated.
     * 
     *  \param sample Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::ProcessorStats::QueryData(LoadSample &sample)
    {
        vector< string > lines;
        bool res = ReadLines("/proc/stat", lines);
        
        if (!res || (lines.size() <= 1))
        {
            return false;
        }
        
        if (cpus.size() == 0)
        {
            return true;
        }
        
        // Parse lines from the second one up (first line is global, we don't care about that).
        size_t cpu_index = 0;
        for (vector< string >::const_iterator it = lines.begin() + 1; 
             it != lines.end(); 
             ++it, ++cpu_index)
        {
            if (!BeginsWith(*it, "cpu"))
            {
                // Read all cpus -> job done
                break;
            }
            
            // Parse current line.
            OneCpuStats current_stats = OneCpuStats(*it);
            OneCpuStats stats_delta = current_stats - cpus[cpu_index];
            float sum = static_cast< float >(stats_delta.Sum());
            
            if (sum < 1.0f)
            {
                sum = 1.0f;
            }
            
            float scaling = 100.0f / sum;
            float idle = scaling * stats_delta.stat_cache[OneCpuStats::CPU_IDLE_COLUMN];
            short busy = static_cast< short >(100.0f - idle);
            
            if (busy < 0)
            {
                busy = 0;
            }
            else if (busy > 100)
            {
                busy = 100;
            }
            
            sample.processor_usage.push_back(static_cast< jshort >(busy));
            cpus[cpu_index] = current_stats;
        }
        
        return true;
    }
    
    /*! Reads data about the processors from the /proc/stat file.
     * 
     *  \param description Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::ProcessorStats::QueryData(HardwareDescription &description)
    {
        description.processor_count = cpus.size();
        
        return true;
    }
    
    /*! Parse one line that is formatted exactly as interface line from the /proc/net/dev
     *  file.
     * 
     *  \param line String containing one line from the file.
     */
    LoadMonitor::NetworkStats::OneInterfaceStats::OneInterfaceStats(const string &line)
    {
        std::fill(name, name + INTERFACE_NAME_LENGTH, 0);
        std::fill(recv, recv + RECV_FIELDS, 0);
        std::fill(send, send + SEND_FIELDS, 0);
        
        if (line.size() == 0)
        {
            return;
        }
        
        size_t i = 0;
        size_t name_len = 0;
        
        // Read all chars until first colon, whitespaces are ignored
        // This reads name of the interface
        while ((i < 15) && (line[i] != ':'))
        {
            if (!isspace(line[i]))
            {
                name[name_len] = line[i];
                ++name_len;
            }
            
            ++i;
        }
        
        // Now parse rest of the line.
        stringstream stream;
        stream << line.substr(i + 1);
        if (stream.fail())
        {
            return;
        }
        
        for (size_t j = 0; j < RECV_FIELDS; ++j)
        {
            stream >> recv[j];
            if (stream.fail())
            {
                recv[j] = 0;
                return;
            }
        }
        
        for (size_t j = 0; j < SEND_FIELDS; ++j)
        {
            stream >> send[j];
            if (stream.fail())
            {
                send[j] = 0;
                return;
            }
        }
    }
    
    /*!
     */
    LoadMonitor::NetworkStats::NetworkStats(void)
    {
    }
    
    /*!
     */
    LoadMonitor::NetworkStats::~NetworkStats(void)
    {
    }

    /*! This methods read /proc/net/dev file and extracts data from the "read bytes" and
     *  "write bytes" columns. If at least one sample has been taken prior call,
     *  statistics for the time between two samples is calculated. Otherwise result is
     *  set to all zeroes for all interfaces.
     *  
     *  \param sample Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::NetworkStats::QueryData(LoadSample &sample)
    {
        unsigned long long current_time = LoadMonitor::GetSampleTime();
        vector< string > lines;
        bool res = ReadLines("/proc/net/dev", lines);
        
        if (!res)
        {
            return false;
        }
        
        if (lines.size() <= 2)
        {
            // No iface data.
            return true;
        }
        
        vector< string >::const_iterator end = lines.end();
        vector< string >::const_iterator it = lines.begin() + 2; // First two lines contain table header
        
        vector< pair< long long, long long > > current_data;
        
        for ( ; it != end; ++it)
        {
            if (it->size() > 4)
            {
                OneInterfaceStats current_iface(*it);
                    
                current_data.push_back(
                        make_pair< long long, long long >(
                        current_iface.recv[OneInterfaceStats::RECV_BYTES],
                        current_iface.send[OneInterfaceStats::SEND_BYTES]));
            }
        }

        if (current_data.size() == data.size())
        {
            // Time delta in ms.
            long long delta_time = (current_time - last_time) / 10000;
            
            if (delta_time < 1)
            {
                delta_time = 1;
            }
            
            for (size_t i = 0; i < data.size(); ++i)
            {
                long long recv_b = (1000ull * (current_data[i].first - data[i].first)) / delta_time;
                long long send_b = (1000ull * (current_data[i].second - data[i].second)) / delta_time;
                
                sample.network_bytes_in.push_back(static_cast< jint >(recv_b));
                sample.network_bytes_out.push_back(static_cast< jint >(send_b));
            }
        }
        else if (data.size() == 0)
        {
            // Create empty sample data.
            for (size_t i = 0; i < data.size(); ++i)
            {
                sample.network_bytes_in.push_back(0);
                sample.network_bytes_out.push_back(0);
            }
        }
        
        data.swap(current_data);
        last_time = current_time;
        
        return true;
    }
    
    /*! This will parse /proc/dev/net file and extract name of each interface mentioned
     *  in the file.
     * 
     *  \param description Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::NetworkStats::QueryData(HardwareDescription &description)
    {
        vector< string > lines;
        bool res = ReadLines("/proc/net/dev", lines);
        
        if (!res)
        {
            return false;
        }
        
        if (lines.size() <= 2)
        {
            // No data to parse.
            return true;
        }
        
        vector< string >::const_iterator end = lines.end();
        
        // Process all lines except first 2 with table headers
        for (vector< string >::const_iterator it = lines.begin() + 2; it != end; ++it)
        {
            if (it->size() > 4)
            {
                OneInterfaceStats current_iface(*it);
                description.adapters.push_back(string(current_iface.name));
            }
        }
        
        return true;
    }

    /*! \param line One line from the /proc/diskstats file.
     */
    LoadMonitor::DriveStats::OneDriveStats::OneDriveStats(const string &line)
    {
        fill(name, name + DRIVE_NAME_LENGTH, 0);
        fill(stat_fields, stat_fields + COLUMN_COUNT, 0);
        
        if (line.size() == 0)
        {
            return;
        }
        
        stringstream stream;
        stream << line;
        
        stream >> major >> minor >> name;
        for (size_t i = 0; (i < COLUMN_COUNT) && !stream.fail(); ++i)
        {
            stream >> stat_fields[i];
        }
    }
    
    /*! This will test if device that this object represents is device of the disk drive
     *  of if it is partition. Drive devices have names like "hda", "hdb", etc., while
     *  paritions have names like "hda0", "hda1", etc. That is, this method tests if name
     *  of the device ends with a number.
     * 
     *  \return true if name of the device does not end with a number, false otherwise.
     */
    bool LoadMonitor::DriveStats::OneDriveStats::IsDriveDevice(void) const
    {
        int len = strlen(name);
        
        if (len == 0)
        {
            return false;
        }
        else
        {
            return isalpha(name[len - 1]);
        }
    }
    
    /*! Test if device represented by this object is connected through the IDE bus. Note that
     *  this tests only for IDE disks, that is devices with names beginning with "hd".
     * 
     *  \return true if this device is an IDE disk, false otherwise.
     */
    bool LoadMonitor::DriveStats::OneDriveStats::IsIDE(void) const
    {
        int len = strlen(name);
        
        if (len < 2)
        {
            return false;
        }
        else
        {
            return ((name[0] == 'h') && (name[1] == 'd'));
        }
    }
    
    /*! Test if device represented by this object is connected through the SCSI bus. Note that
     *  this tests only for SCSI disks, that is devices with names beginning with "sd".
     * 
     *  \return true if this device is a SCSI disk, false otherwise.
     */
    bool LoadMonitor::DriveStats::OneDriveStats::IsSCSI(void) const
    {
        int len = strlen(name);
        
        if (len < 2)
        {
            return false;
        }
        else
        {
            return ((name[0] == 's') && (name[1] == 'd'));
        }
    }
    
    /*!
     */
    LoadMonitor::DriveStats::DriveStats(void) :
    kernel_version(0)
    {
        kernel_version = GetKernelVersionCode();
    }
    
    /*!
     */
    LoadMonitor::DriveStats::~DriveStats(void)
    {
    }

    /*! Reads data from the /proc/diskstats file. This file is available only on kernels
     *  newer than 2.5.69. For older kernels no data is available.
     * 
     *  \param sample Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::DriveStats::QueryData(LoadSample &sample)
    {
        if (kernel_version < KERNEL_VERSION_CODE(2, 5, 70))
        {
            // Too old kernel, no data to collect
            return true;
        }
        
        unsigned long long current_time = LoadMonitor::GetSampleTime();
        vector< string > lines;
        bool res = ReadLines("/proc/diskstats", lines);
        
        if (!res)
        {
            return false;
        }
        
        vector< string >::const_iterator end = lines.end();
        vector< pair< long, long > > current_values;
        
        for (vector< string >::const_iterator it = lines.begin(); it != end; ++it)
        {
            OneDriveStats current_disk(*it);
            
            /* Test if drive name is hd* or sd* and if minor device number is 0.
             * That is, we test if the drive is IDE or SCSI drive, but not partition.
             */
            if ((current_disk.IsIDE() || current_disk.IsSCSI()) && current_disk.IsDriveDevice())
            {
                current_values.push_back(
                        make_pair< long, long >(
                            current_disk.stat_fields[OneDriveStats::READ_SECTORS],
                            current_disk.stat_fields[OneDriveStats::WRITE_SECTORS]));
            }
        }
        
        if (current_values.size() == data.size())
        {
            // Time delta in ms.
            unsigned long long delta_time = (current_time - last_time) / 10000;
            
            if (delta_time < 1)
            {
                delta_time = 1;
            }
            
            for (size_t i = 0; i < data.size(); ++i)
            {
                long long reads = (current_values[i].first - data[i].first) * OneDriveStats::SECTOR_SIZE;
                long long writes = (current_values[i].second - data[i].second) * OneDriveStats::SECTOR_SIZE;
                long long reads_r = (1000ull * reads) / delta_time;
                long long writes_r = (1000ull * writes) / delta_time;
                
                sample.disk_read_bytes.push_back(static_cast< jlong >(reads_r));
                sample.disk_write_bytes.push_back(static_cast< jlong >(writes_r));
            }
        }
        else if (data.size() == 0) // Is this the first sample taken?
        {
            // Store all 0 for all drives
            sample.disk_read_bytes.resize(current_values.size(), 0);
            sample.disk_write_bytes.resize(current_values.size(), 0);
        }
        
        data.swap(current_values);
        last_time = current_time;
        
        return true;
    }
    
    /*! This will read names of all IDE drives from the /proc/diskstats file. Works on
     *  kernel newer than 2.5.69.
     * 
     *  \param description Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::DriveStats::QueryData(HardwareDescription &description)
    {
        if (kernel_version < KERNEL_VERSION_CODE(2, 5, 70))
        {
            // Too old kernel, no data to collect
            return true;
        }
        
        vector< string > lines;
        bool res = ReadLines("/proc/diskstats", lines);
        
        if (!res)
        {
            return false;
        }
        
        vector< string >::const_iterator end = lines.end();
        
        for (vector< string >::const_iterator it = lines.begin(); it != end; ++it)
        {
            OneDriveStats current_drive(*it);
            
            /* Test if drive name is hd* or sd* and if minor device number is 0.
             * That is, we test if the drive is IDE or SCSI drive, but not partition.
             */
            if ((current_drive.IsIDE() || current_drive.IsSCSI()) && current_drive.IsDriveDevice())
            {
                description.drives.push_back(string(current_drive.name));
            }
        }
        
        return true;
    }
    
    /*!
     */
    LoadMonitor::SystemStats::SystemStats(void)
    {
    }
    
    /*!
     */
    LoadMonitor::SystemStats::~SystemStats(void)
    {
    }

    /*! This will read /proc/loadavg file and retrieve number of processes.
     * 
     *  \param sample Structure which will receive data collected by this provider.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::SystemStats::QueryData(LoadSample &sample)
    {
        vector< string > lines;
        bool res = ReadLines("/proc/loadavg", lines);
        
        if (!res || (lines.size() < 1))
        {
            return false;
        }
        
        size_t processes = 0;
        bool res2 = GetFieldValue(lines[0], 5, processes, 0, " /\t");
        
        if (res2)
        {
            sample.process_count = processes;
        }
        
        return res2;
    }
    
    /*! This method does nothing.
     * 
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::SystemStats::QueryData(HardwareDescription &)
    {
        return true;
    }
} // namespace lm
