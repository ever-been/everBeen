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
 *  \date 2. 10. 2006
 *
 *  \brief Implementation of methods from LoadMonitor class.
 */

// Disable warnings about deprecated "unsecure" functions from STL in Visual Studio 2005+.
#if _MSC_VER >= 1400
#define _SCL_SECURE_NO_DEPRECATE 1
#endif

#define _WIN32_DCOM

#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <windows.h>
#include <Wbemidl.h>
#include <comdef.h>
#include <jni.h>
#include "LoadMonitor.h"
#include "../common/String.h"
#include "../common/LoadSample.h"
#include "../common/HardwareDescription.h"

#pragma comment(lib, "wbemuuid.lib")

using namespace std;

namespace lm
{
    /*! \brief Build string containing current time.
     *
     *  \return String with time in format "YYYY/MM/DD hh:mm:ss.msec"
     */
    std::string TimeString(void)
    {
        SYSTEMTIME l_time;

        GetLocalTime(&l_time);

        stringstream stream;

        stream << l_time.wYear << '/';

        stream.width(2);
        stream.fill('0');

        stream << l_time.wMonth << setw(1) << '/' << setw(2)
               << l_time.wDay << setw(1) << ' ' << setw(2)
               << l_time.wHour << setw(1) << ':' << setw(2)
               << l_time.wMinute << setw(1) << ':' << setw(2)
               << l_time.wSecond << setw(1) << '.' << setw(3)
               << l_time.wMilliseconds;

        return stream.str();
    }

    /*!
     */
    LoadMonitor::LoadMonitor(void) :
    services(NULL),
    refresher(NULL)
    {
    }


    /*!
     */
    LoadMonitor::~LoadMonitor(void)
    {
        //Terminate();
    }

    /*! Connect to the WMI provider via COM and initialize all required resources. This will also add performance counters
     *  to the refresher. Error messages are written to the log file.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::Initialize(void)
    {
        HRESULT                 res = 0;
        IWbemLocator            *locator = NULL;
        IWbemConfigureRefresher *configure = NULL;
        BSTR                    wmi_namespace = SysAllocString(L"\\\\.\\root\\cimv2");


        LogInfo("Initializing Load Monitor.");

        if (wmi_namespace == NULL)
        {
            LogError("Out of memory.");
            goto error;
        }

        // Initialize COM.
        res = CoInitializeEx(NULL, COINIT_MULTITHREADED);
        if (FAILED(res))
        {
            LogError("CoInitializeEx failed.");
            goto error;
        }

        // Initialize COM security.
        res = CoInitializeSecurity(NULL, 
                                   -1, 
                                   NULL, 
                                   NULL, 
                                   RPC_C_AUTHN_LEVEL_NONE,
                                   RPC_C_IMP_LEVEL_IMPERSONATE,
                                   NULL,
                                   EOAC_NONE,
                                   0);
        if (FAILED(res))
        {
            LogError("CoInitializeSecurity failed.");
            goto error;
        }

        // Intialize locator.
        res = CoCreateInstance(CLSID_WbemLocator,
                               NULL,
                               CLSCTX_INPROC_SERVER,
                               IID_IWbemLocator,
                               (void **) &locator);
        if (FAILED(res))
        {
            LogError("CoCreateInstance failed.");
            goto error;
        }

        // Connect to local WMI server.
        res = locator->ConnectServer(wmi_namespace,
                                     NULL, // user name
                                     NULL, // password
                                     NULL, // locale
                                     0,    // security flags
                                     NULL, // authority
                                     NULL, // wbem context
                                     &services);
        if (FAILED(res))
        {
            LogError("Attempt to connect to the WMI failed.");
            goto error;
        }

        // Not needed anymore.
        locator->Release();
        locator = NULL;

        // Create refresher for the counters.
        res = CoCreateInstance(CLSID_WbemRefresher,
                               NULL,
                               CLSCTX_INPROC_SERVER,
                               IID_IWbemRefresher,
                               (void **) &refresher);
        if (FAILED(res))
        {
            LogError("Error creating WBEM refresher.");
            goto error;
        }

        // Find configurator for refresher.
        res = refresher->QueryInterface(IID_IWbemConfigureRefresher, (void **) &configure);
        if (FAILED(res))
        {
            LogError("Error configuring refresher.");
            goto error;
        }

        // Register CPU counters.
        if (!processor.AddToRefresher(configure, services))
        {
            LogError("Unable to add processor usage counters to the refresher.");
            goto error;
        }

        // Register network counters
        if (!network.AddToRefresher(configure, services))
        {
            LogError("Unable to add network usage counters to the refresher.");
            goto error;
        }

        // Register memory counters.
        if (!memory.AddToRefresher(configure, services))
        {
            LogError("Unable to add memory usage counters to the refresher.");
            goto error;
        }

        // Register system counters.
        if (!system.AddToRefresher(configure, services))
        {
            LogError("Unable to add system counters to the refresher.");
            goto error;
        }

        // Register drive usage counters.
        if (!drive.AddToRefresher(configure, services))
        {
            LogError("Unable to add drive usage counters to the refresher.");
            goto error;
        }

        configure->Release();
        configure = NULL;

        LogInfo("Load Monitor initialized successfully.");

        return true;

    error:
        // Something got screwy... clean up

        if (wmi_namespace != NULL)
        {
            SysFreeString(wmi_namespace);
            wmi_namespace = NULL;
        }

        if (locator != NULL)
        {
            locator->Release();
            locator = NULL;
        }

        if (configure != NULL)
        {
            configure->Release();
            configure = NULL;
        }

        Terminate();

        return false;
    }

    bool LoadMonitor::Terminate(void)
    {
        LogInfo("Load Monitor is terminating.");

        processor.Release();
        memory.Release();
        system.Release();
        network.Release();
        drive.Release();

        if (refresher != NULL)
        {
            refresher->Release();
            refresher = NULL;
        }

        if (services != NULL)
        {
            services->Release();
            services = NULL;
        }

        CoUninitialize();
        
        LogInfo("Load Monitor terminated successfully.");
        return true;
    }

    /*! Request new data from WMI provider and store it in LoadSample.
     *  
     *  \return Sample containing data queried from the system. If an error occured, sample may not contain all data. However it
     *          is quarantee, that it will contain at least timestamp and TSC.
     */
    LoadSample LoadMonitor::TakeSample(void)
    {
        unsigned long long sample_time = GetSampleTime();
        unsigned long long tsc = GetTSC();

        // Refresh data in counters.
        HRESULT hres = refresher->Refresh(0);

        if (FAILED(hres))
        {
            // Refresh failed, return empty sample.
            return LoadSample(sample_time, tsc);
        }
    
        LoadSample sample(sample_time, tsc);

        if (!processor.QueryData(sample))
        {
            LogError("CPU query failed.");
        }

        if (!network.QueryData(sample))
        {
            LogError("Network query failed.");
        }

        if (!system.QueryData(sample))
        {
            LogError("System query failed.");
        }

        if (!memory.QueryData(sample))
        {
            LogError("Memory query failed.");
        }

        if (!drive.QueryData(sample))
        {
            LogError("Drive query failed.");
        }

        return sample;
    }

    /*! \return HardwareDescription object with names of drives and network adapters that are being monitored by the library.
     */
    HardwareDescription LoadMonitor::GetHardwareDescription(void)
    {
        unsigned long long time = GetSampleTime();

        // Refresh data in counters.
        HRESULT hres = refresher->Refresh(0);

        if (FAILED(hres))
        {
            // Refresh failed, return empty sample.
            return HardwareDescription(time);
        }

        HardwareDescription description;

        description.time = time;

        if (!drive.QueryNames(description))
        {
            LogError("Drive name query failed.");
        }

        if (!network.QueryNames(description))
        {
            LogError("Adapter name query failed.");
        }

        description.processor_count = processor.GetCount();
        description.memory_size = memory.GetSize();

        return description;
    }

    /*! \return Current time in 100ns intervals since Windows epoch.
     */
    unsigned long long LoadMonitor::GetSampleTime() const
    {
        SYSTEMTIME         local_time;
        unsigned long long result = 0;

        GetLocalTime(&local_time);
        SystemTimeToFileTime(&local_time, (FILETIME *) &result);

        return result;
    }

    /*! Retrieve objects from enumerator and store result in newly allocated buffer. You have to free resulting buffer by yourself (with
     *  ClearEnumObjects method). Note that resulting buffer may be bigger than actual number of objects that will be stored in it.
     *
     *  \param wbem_enum Enumerator in which objects are currently stored.
     *  \param object_count Pointer to variable which will receive number of objects that were stored in the enumerator. This cannot be NULL.
     *  \param access_objects Pointer to an array of the IWbemObjectAccess pointers. This array will receive objects that were stored
     *         in enumerator. Array with correct size will be automatically allocated.
     *
     *  \return Value which indicated status of the call.
     *  \retval WBEM_S_NO_ERROR Call was successful.
     *  \retval WBEM_S_TIMEDOUT Enumerator was busy.
     *  \retval WBEM_E_NOT_FOUND Provider was not able to retrieve requested instance.
     *  \retval WBEM_E_PROVIDER_NOT_CAPABLE Provider was not able to rerieve specific instances.
     *  \retval WBEM_E_FAILED Internal failure, but the operation was valid.
     *  \retval WBEM_E_BUFFER_TOO_SMALL Buffer allocation failed.
     */
    HRESULT LoadMonitor::PerformanceCounterEnumCache::GetEnumObjects(IWbemHiPerfEnum *wbem_enum, 
                                                                     size_t *object_count, 
                                                                     IWbemObjectAccess ***access_objects)
    {
        // Default size of the buffer to which data will be copied
        const size_t DEFAULT_ACCESS_OBJECT_COUNT = 8;

        IWbemObjectAccess **access = new IWbemObjectAccess*[DEFAULT_ACCESS_OBJECT_COUNT];
        HRESULT           result = 0;
        ULONG             objects = DEFAULT_ACCESS_OBJECT_COUNT;
        ULONG             returned = 0;

        SecureZeroMemory(access, sizeof(IWbemObjectAccess *) * objects);

        // Get objects from the enumerator
        result = wbem_enum->GetObjects(0, objects, access, &returned);
        objects = returned;

        // Test if our pre-allocated buffer was big enuogh.
        if (result == WBEM_E_BUFFER_TOO_SMALL)
        {
            // nope -> we need bigger buffer
            delete []access;

            // Create new array and clear it.
            access = new IWbemObjectAccess*[returned];
            SecureZeroMemory(access, sizeof(IWbemObjectAccess *) * returned);

            // Query data again.
            result = wbem_enum->GetObjects(0, DEFAULT_ACCESS_OBJECT_COUNT, access, &returned);
            objects = returned;

            if (result != WBEM_S_NO_ERROR)
            {
                // If failed again
                *object_count = 0;
                *access_objects = NULL;

                delete []access;

                return result;
            }
        }
        else if (result != WBEM_S_NO_ERROR)
        {
            // something else failed
            return result;
        }
        
        *access_objects = access;
        *object_count = objects;

        return WBEM_S_NO_ERROR;
    }

    /*! \param access_objects Array containing pointers to the access objects that should be cleared. Note that this array will
     *         be deleted.
     *  \param object_count Number of objects stored in array.
     */
    void LoadMonitor::PerformanceCounterEnumCache::ClearEnumObjects(IWbemObjectAccess **access_objects, size_t object_count)
    {
        if (access_objects == NULL)
        {
            return;
        }

        for (size_t i = 0; i < object_count; ++i)
        {
            access_objects[i]->Release();
        }

        delete []access_objects;
    }

    /*!
     */
    LoadMonitor::ProcessorEnumCache::ProcessorEnumCache(void) :
    enum_processor_usage(NULL),
    enum_processor_usage_id(0),
    id_processor_time(0),
    id_processor_name(0)
    {
    }

    /*!
     */
    LoadMonitor::ProcessorEnumCache::~ProcessorEnumCache(void)
    {
        //Release();
    }

    /*!
     */
    void LoadMonitor::ProcessorEnumCache::Release(void)
    {
        if (enum_processor_usage)
        {
            enum_processor_usage->Release();
            enum_processor_usage = NULL;
            enum_processor_usage_id = 0;
            id_processor_time = 0;
            id_processor_name = 0;
        }
    }

    /*! \param access_object Object which contains all properties that need to be cached.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::ProcessorEnumCache::CacheImpl(IWbemObjectAccess *access_object)
    {
        CIMTYPE cim_type;

        if (FAILED(access_object->GetPropertyHandle(L"PercentProcessorTime", &cim_type, &id_processor_time)))
        {
            return false;
        }

        if (FAILED(access_object->GetPropertyHandle(L"Name", &cim_type, &id_processor_name)))
        {
            return false;
        }

        return true;
    }

    /*! \param configure Configure refresher which will be used to add required objects to the refresher.
     *  \param services WMI service provider.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::ProcessorEnumCache::AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services)
    {
        HRESULT hr = configure->AddEnum(services,
                                        L"Win32_PerfFormattedData_PerfOS_Processor",
                                        0,
                                        NULL,
                                        &enum_processor_usage,
                                        &enum_processor_usage_id);

        return SUCCEEDED(hr);
    }

    /*! Query data after it has been refreshed. This will collect all data from enumerators previously registered with refresher
     *  via AddToRefresher method and store resulting data in provided load sample. If data are stored in array (e.g. CPU usage
     *  on multi-processor machines), arrays will be automatically allocated. If query fails, sample will remain unmodified.
     *
     *  \param target Reference to the load sample which will receive data.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::ProcessorEnumCache::QueryData(LoadSample &target)
    {
        const long CPU_NAME_SIZE = 128;

        IWbemObjectAccess **processor_access = NULL;
        size_t            processors = 0;
        WCHAR             processor_name[CPU_NAME_SIZE] = {0};
        long              bytes_read = 0;

        if (FAILED(GetEnumObjects(enum_processor_usage, &processors, &processor_access)))
        {
            ClearEnumObjects(processor_access, processors);
            return false;
        }

        if ((processors <= 1) || !Cache(processor_access[0]))
        {
            ClearEnumObjects(processor_access, processors);
            return false;
        }

        for (size_t i = 0; i < processors; ++i)
        {
            unsigned long long cpu_time;

            if (FAILED(processor_access[i]->ReadPropertyValue(id_processor_name, CPU_NAME_SIZE, &bytes_read, (BYTE *) processor_name)))
            {
                continue;
            }

            // skip "_Total" instance
            if (wcscmp(L"_Total", processor_name) == 0)
            {
                continue;
            }

            if (FAILED(processor_access[i]->ReadQWORD(id_processor_time, &cpu_time)))
            {
                target.processor_usage.push_back(0);
                continue;
            }

            target.processor_usage.push_back(static_cast< jshort >(cpu_time));
        }

        ClearEnumObjects(processor_access, processors);

        return true;
    }

    /*! \return Number of processors installed on the computer.
     */
    size_t LoadMonitor::ProcessorEnumCache::GetCount()
    {

        IWbemObjectAccess **processor_access = NULL;
        size_t            processors = 0;

        if (FAILED(GetEnumObjects(enum_processor_usage, &processors, &processor_access)))
        {
            ClearEnumObjects(processor_access, processors);
            return 1; // At least one CPU
        }

        if ((processors <= 1) || !Cache(processor_access[0]))
        {
            ClearEnumObjects(processor_access, processors);
            return 1; // At least one CPU
        }

        ClearEnumObjects(processor_access, processors);

        // Do not count _Total instance
        return processors - 1;
    }

    /*!
     */
    LoadMonitor::NetworkEnumCache::NetworkEnumCache(void) :
    enum_network(NULL),
    enum_network_id(0),
    id_bytes_received(0),
    id_bytes_sent(0),
    id_name(0)
    {
    }

    /*!
     */
    LoadMonitor::NetworkEnumCache::~NetworkEnumCache(void)
    {
        //Release();
    }

    /*!
     */
    void LoadMonitor::NetworkEnumCache::Release(void)
    {
        if (enum_network)
        {
            enum_network->Release();
            enum_network = NULL;
            id_bytes_received = 0;
            id_bytes_sent = 0;
            id_name = 0;
        }
    }

    /*! \param access_object Object which contains all properties that need to be cached.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::NetworkEnumCache::CacheImpl(IWbemObjectAccess *access_object)
    {
        CIMTYPE cim_type;

        if (FAILED(access_object->GetPropertyHandle(L"BytesReceivedPerSec", &cim_type, &id_bytes_received)))
        {
            return false;
        }

        if (FAILED(access_object->GetPropertyHandle(L"BytesSentPerSec", &cim_type, &id_bytes_sent)))
        {
            return false;
        }

        if (FAILED(access_object->GetPropertyHandle(L"Name", &cim_type, &id_name)))
        {
            return false;
        }

        return true;
    }

    /*! \param configure Configure refresher which will be used to add required objects to the refresher.
     *  \param services WMI service provider.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::NetworkEnumCache::AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services)
    {
        HRESULT hr = configure->AddEnum(services,
                                        L"Win32_PerfFormattedData_Tcpip_NetworkInterface",
                                        0,
                                        NULL,
                                        &enum_network,
                                        &enum_network_id);

        return SUCCEEDED(hr);
    }

    /*! Query data after it has been refreshed. This will collect all data from enumerators previously registered with refresher
     *  via AddToRefresher method and store resulting data in provided load sample. If data are stored in array (e.g. CPU usage
     *  on multi-processor machines), arrays will be automatically allocated. If query fails, sample will remain unmodified.
     *
     *  \param target Reference to the load sample which will receive data.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::NetworkEnumCache::QueryData(LoadSample &target)
    {
        IWbemObjectAccess **network_access;
        size_t            networks = 0;

        if (FAILED(GetEnumObjects(enum_network, &networks, &network_access)))
        {
            ClearEnumObjects(network_access, networks);
            return false;
        }

        if (networks > 0)
        {
            if (!Cache(network_access[0]))
            {
                ClearEnumObjects(network_access, networks);
                return false;
            }

            for (size_t i = 0; i < networks; ++i)
            {
                DWORD bytes_received;
                DWORD bytes_sent;

                if (FAILED(network_access[i]->ReadDWORD(id_bytes_received, &bytes_received)))
                {
                    bytes_received = 0;
                }

                if (FAILED(network_access[i]->ReadDWORD(id_bytes_sent, &bytes_sent)))
                {
                    bytes_sent = 0;
                }

                target.network_bytes_in.push_back(static_cast< jint >(bytes_received));
                target.network_bytes_out.push_back(static_cast< jint >(bytes_sent));
            }
        }
        else
        {
            target.ReallocNetworkData(0);
        }

        ClearEnumObjects(network_access, networks);

        return true;
    }

    /*! Query names of all network adapters that are monitored by the Load Monitor library.
     *
     *  \param target HardwareDescription object that will receive names of network interfaces.
     *
     *  \return true on success, false otherwise,
     */
    bool LoadMonitor::NetworkEnumCache::QueryNames(HardwareDescription &target)
    {
        // Length of the adapter name string, 200 characters should be enough
        const long ADAPTER_NAME_LENGTH = 200;

        IWbemObjectAccess **network_access;
        size_t            networks = 0;
        WCHAR             adapter_name[ADAPTER_NAME_LENGTH] = {0};

        if (FAILED(GetEnumObjects(enum_network, &networks, &network_access)))
        {
            ClearEnumObjects(network_access, networks);
            return false;
        }

        if (networks > 0)
        {
            if (!Cache(network_access[0]))
            {
                ClearEnumObjects(network_access, networks);
                return false;
            }

            for (size_t i = 0; i < networks; ++i)
            {
                long bytes_read = 0;

                if (FAILED(network_access[i]->ReadPropertyValue(id_name, sizeof(WCHAR) * ADAPTER_NAME_LENGTH, 
                           &bytes_read, (BYTE *) adapter_name)))
                {
                    continue;
                }

                target.adapters.push_back(adapter_name);
            }

            ClearEnumObjects(network_access, networks);
        }

        return true;
    }

    /*!
     */
    LoadMonitor::MemoryEnumCache::MemoryEnumCache(void) :
    enum_memory_usage(NULL),
    enum_memory_usage_id(0),
    id_available(0)
    {
    }

    /*!
     */
    LoadMonitor::MemoryEnumCache::~MemoryEnumCache(void)
    {
        //Release();
    }

    /*!
     */
    void LoadMonitor::MemoryEnumCache::Release(void)
    {
        if (enum_memory_usage)
        {
            enum_memory_usage->Release();
            enum_memory_usage = NULL;
            enum_memory_usage_id = 0;
            id_available = 0;
        }
    }

    /*! \param access_object Object which contains all properties that need to be cached.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::MemoryEnumCache::CacheImpl(IWbemObjectAccess *access_object)
    {
        CIMTYPE cim_type;

        return SUCCEEDED(access_object->GetPropertyHandle(L"AvailableBytes", &cim_type, &id_available));
    }

    /*! \param configure Configure refresher which will be used to add required objects to the refresher.
     *  \param services WMI service provider.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::MemoryEnumCache::AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services)
    {
        HRESULT hr = configure->AddEnum(services,
                                        L"Win32_PerfFormattedData_PerfOS_Memory",
                                        0,
                                        NULL,
                                        &enum_memory_usage,
                                        &enum_memory_usage_id);

        return SUCCEEDED(hr);
    }

    /*! Query data after it has been refreshed. This will collect all data from enumerators previously registered with refresher
     *  via AddToRefresher method and store resulting data in provided load sample. If data are stored in array (e.g. CPU usage
     *  on multi-processor machines), arrays will be automatically allocated. If query fails, sample will remain unmodified.
     *
     *  \param target Reference to the load sample which will receive data.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::MemoryEnumCache::QueryData(LoadSample &target)
    {
        IWbemObjectAccess  **memory_access = NULL;
        unsigned long long available = 0;
        size_t             object_count = 0;

        if (FAILED(GetEnumObjects(enum_memory_usage, &object_count, &memory_access)))
        {
            ClearEnumObjects(memory_access, object_count);
            return false;
        }

        if (object_count < 1)
        {
            ClearEnumObjects(memory_access, object_count);
            return false;
        }

        if (!Cache(memory_access[0]))
        {
            ClearEnumObjects(memory_access, object_count);
            return false;
        }

        if (FAILED(memory_access[0]->ReadQWORD(id_available, &available)))
        {
            available = 0;
        }

        target.memory_free = available;

        ClearEnumObjects(memory_access, object_count);

        return true;
    }

    /*! Gets total size of physical memory in bytes. This method does not use WMI, just pure WinAPI (GlobalMemoryStatusEx).
     *
     *  \return Size of physical memory in bytes.
     */
    unsigned long long LoadMonitor::MemoryEnumCache::GetSize()
    {

        MEMORYSTATUSEX memory_status;

        SecureZeroMemory((void *) &memory_status, sizeof(MEMORYSTATUSEX));

        memory_status.dwLength = sizeof(MEMORYSTATUSEX);

        if (GlobalMemoryStatusEx(&memory_status) == FALSE)
        {
            return 0;
        }
        else
        {
            return static_cast< unsigned long long >(memory_status.ullTotalPhys);
        }
    }

    /*!
     */
    LoadMonitor::SystemEnumCache::SystemEnumCache(void) :
    enum_system(NULL),
    enum_system_id(0),
    id_processes(0),
    id_processor_queue(0)
    {
    }

    /*!
     */
    LoadMonitor::SystemEnumCache::~SystemEnumCache(void)
    {
        //Release();
    }

    /*!
     */
    void LoadMonitor::SystemEnumCache::Release(void)
    {
        if (enum_system)
        {
            enum_system->Release();
            enum_system = NULL;
            enum_system_id = 0;
            id_processes = 0;
            id_processor_queue = 0;
        }
    }

    /*! \param access_object Object which contains all properties that need to be cached.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::SystemEnumCache::CacheImpl(IWbemObjectAccess *access_object)
    {
        CIMTYPE cim_type;

        if (FAILED(access_object->GetPropertyHandle(L"Processes", &cim_type, &id_processes)))
        {
            return false;
        }

        if (FAILED(access_object->GetPropertyHandle(L"ProcessorQueueLength", &cim_type, &id_processor_queue)))
        {
            return false;
        }

        return true;
    }

    /*! \param configure Configure refresher which will be used to add required objects to the refresher.
     *  \param services WMI service provider.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::SystemEnumCache::AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services)
    {
        HRESULT hr = configure->AddEnum(services,
                                        L"Win32_PerfFormattedData_PerfOS_System",
                                        0,
                                        NULL,
                                        &enum_system,
                                        &enum_system_id);

        return SUCCEEDED(hr);
    }

    /*! Query data after it has been refreshed. This will collect all data from enumerators previously registered with refresher
     *  via AddToRefresher method and store resulting data in provided load sample. If data are stored in array (e.g. CPU usage
     *  on multi-processor machines), arrays will be automatically allocated. If query fails, sample will remain unmodified.
     *
     *  \param target Reference to the load sample which will receive data.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::SystemEnumCache::QueryData(LoadSample &target)
    {
        IWbemObjectAccess **system_access = NULL;
        DWORD             processes = 0;
        DWORD             queue_length = 0;
        size_t            object_count = 0;

        if (FAILED(GetEnumObjects(enum_system, &object_count, &system_access)))
        {
            ClearEnumObjects(system_access, object_count);
            return false;
        }

        if (object_count < 1)
        {
            ClearEnumObjects(system_access, object_count);
            return false;
        }

        if (!Cache(system_access[0]))
        {
            ClearEnumObjects(system_access, object_count);
            return false;
        }

        if (FAILED(system_access[0]->ReadDWORD(id_processes, &processes)))
        {
            processes = 0;
        }

        if (FAILED(system_access[0]->ReadDWORD(id_processor_queue, &queue_length)))
        {
            queue_length = 0;
        }

        target.process_count = processes;
        target.processor_queue = queue_length;

        ClearEnumObjects(system_access, object_count);

        return true;
    }

    /*!
     */
    LoadMonitor::DriveEnumCache::DriveEnumCache(void) :
    enum_drives(NULL),
    enum_drives_id(0),
    id_disk_write_bytes(0),
    id_disk_read_bytes(0),
    id_disk_name(0)
    {
    }

    /*!
     */
    LoadMonitor::DriveEnumCache::~DriveEnumCache(void)
    {
        //Release();
    }

    /*!
     */
    void LoadMonitor::DriveEnumCache::Release(void)
    {
        if (enum_drives)
        {
            enum_drives->Release();
            enum_drives = 0;
            enum_drives_id = 0;
            id_disk_write_bytes = 0;
            id_disk_read_bytes = 0;
            id_disk_name = 0;
        }
    }

    /*! \param access_object Object which contains all properties that need to be cached.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::DriveEnumCache::CacheImpl(IWbemObjectAccess *access_object)
    {
        CIMTYPE cim_type;

        if (FAILED(access_object->GetPropertyHandle(L"DiskWriteBytesPerSec", &cim_type, &id_disk_write_bytes)))
        {
            return false;
        }

        if (FAILED(access_object->GetPropertyHandle(L"DiskReadBytesPerSec", &cim_type, &id_disk_read_bytes)))
        {
            return false;
        }

        if (FAILED(access_object->GetPropertyHandle(L"Name", &cim_type, &id_disk_name)))
        {
            return false;
        }

        return true;
    }

    /*! \param configure Configure refresher which will be used to add required objects to the refresher.
     *  \param services WMI service provider.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::DriveEnumCache::AddToRefresher(IWbemConfigureRefresher *configure, IWbemServices *services)
    {
        HRESULT hr = configure->AddEnum(services,
                                        L"Win32_PerfFormattedData_PerfDisk_PhysicalDisk",
                                        0,
                                        NULL,
                                        &enum_drives,
                                        &enum_drives_id);

        return SUCCEEDED(hr);
    }

    /*! Query data after it has been refreshed. This will collect all data from enumerators previously registered with refresher
     *  via AddToRefresher method and store resulting data in provided load sample. If data are stored in array (e.g. CPU usage
     *  on multi-processor machines), arrays will be automatically allocated. If query fails, sample will remain unmodified.
     *
     *  \param target Reference to the load sample which will receive data.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::DriveEnumCache::QueryData(LoadSample &target)
    {
        const long DISK_NAME_SIZE = 160;

        IWbemObjectAccess **drive_access = NULL;
        size_t            object_count = 0;
        WCHAR             drive_name[DISK_NAME_SIZE] = {0};
        long              bytes_read = 0;

        if (FAILED(GetEnumObjects(enum_drives, &object_count, &drive_access)))
        {
            ClearEnumObjects(drive_access, object_count);
            return false;
        }

        if ((object_count < 0) || !Cache(drive_access[0]))
        {
            ClearEnumObjects(drive_access, object_count);
            return false;
        }

        for (size_t i = 0; i < object_count; ++i)
        {
            unsigned long long write_bytes;
            unsigned long long read_bytes;

            if (FAILED(drive_access[i]->ReadPropertyValue(id_disk_name, sizeof(WCHAR) * DISK_NAME_SIZE, &bytes_read, (BYTE *) drive_name)))
            {
                continue;
            }

            if (wcscmp(L"_Total", drive_name) == 0)
            {
                continue;
            }

            if (FAILED(drive_access[i]->ReadQWORD(id_disk_write_bytes, &write_bytes)))
            {
                write_bytes = 0;
            }

            if (FAILED(drive_access[i]->ReadQWORD(id_disk_read_bytes, &read_bytes)))
            {
                read_bytes = 0;
            }

            target.disk_read_bytes.push_back(static_cast< jlong >(read_bytes));
            target.disk_write_bytes.push_back(static_cast< jlong >(write_bytes));
        }

        ClearEnumObjects(drive_access, object_count);

        return true;
    }

    /*! Query names of all disk drives that are being monitored by the Load Monitor.
     *
     *  \param target HardwareDescription object that will receive names.
     *
     *  \return true on success, false otherwise.
     */
    bool LoadMonitor::DriveEnumCache::QueryNames(HardwareDescription &target)
    {
        const long DISK_NAME_SIZE = 160;

        IWbemObjectAccess **drive_access = NULL;
        size_t            object_count = 0;
        WCHAR             drive_name[DISK_NAME_SIZE] = {0};

        if (FAILED(GetEnumObjects(enum_drives, &object_count, &drive_access)))
        {
            ClearEnumObjects(drive_access, object_count);
            return false;
        }

        if (object_count > 0)
        {
            if (!Cache(drive_access[0]))
            {
                ClearEnumObjects(drive_access, object_count);
                return false;
            }

            for (size_t i = 0; i < object_count; ++i)
            {
                long bytes_read = 0;

                if (FAILED(drive_access[i]->ReadPropertyValue(id_disk_name, sizeof(WCHAR) * DISK_NAME_SIZE, &bytes_read, (BYTE *) drive_name)))
                {
                    continue;
                }

                // Skip _Total instance with sum of the data.
                if (wcscmp(L"_Total", drive_name) == 0)
                {
                    continue;
                }

                target.drives.push_back(drive_name);
            }

            ClearEnumObjects(drive_access, object_count);
        }

        return true;
    }

    /*! Writes info message to the standard output (if logging is enabled). Message is prepended with current time and date
     *  and text "INFO". Each message is written on a new line.
     * 
     *  \param message Message to write to the output.
     */
    void LoadMonitor::LogInfo(const char *message) const
    {
#if LOG_ENABLED == 1
        cout << TimeString() << " INFO " << message << endl;
#endif
    }

    /*! Writes error message to the standard output (if logging is enabled). Message is prepended with current time and date
     *  and text "ERROR". Each message is written on a new line.
     * 
     *  \param message Message to write to the output.
     */
    void LoadMonitor::LogError(const char *message) const
    {
#if LOG_ENABLED == 1
        cout << TimeString() << " ERROR " << message << endl;
#endif
    }
}
