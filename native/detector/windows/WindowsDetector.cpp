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

/*! \file WindowsDetector.cpp
 *  \author Branislav Repcek
 *  \date 13. 12. 2005
 *
 *  \brief Main detector routines for Windows.
 */

#define _WIN32_DCOM 

#include <windows.h>
#include <wbemidl.h>
#include <comdef.h>
#include "../Common/UnicodeString.h"
#include "../Common/Detector.h"
#include "../Common/CheckedResult.h"
#include "../Common/XMLFileOutput.h"
#include "../Common/BasicFeatures.h"
#include "../Common/AdvancedFeatures.h"
#include "../Common/OperatingSystem.h"
#include "../Common/Processor.h"
#include "../Common/DiskDrive.h"
#include "../Common/DiskPartition.h"
#include "../Common/Memory.h"
#include "../Common/Product.h"
#include "../Common/NetworkAdapter.h"
#include "WindowsAdvancedFeatures.h"
#include "WMIHelper.h"
#include "WindowsDetector.h"
#include "Registry.h"

#pragma comment(lib, "wbemuuid.lib")
#pragma comment(lib, "kernel32.lib")

#pragma warning(push)
#pragma warning(disable: 4127) // Disable "conditional expression is constant" warning.

/* Uncomment this if you want to use WMI to detect installed applications. Other way uses registry and is typically
 * able to detect more products.
 */
//#define OLD_DETECT_PRODUCTS

/* Uncomment this to detect CD/DVD drives and include them in the listing of drives present on the host.
 */
#define DETECT_CDROM_DRIVES

/* Uncomment this to have detector detect also tape drives.
 */
#define DETECT_TAPE_DRIVES

using namespace std;

namespace hwdet
{

    //! Convert number to string with specified length.
    String LeadToN(int number, size_t length = 2, Char lead_char = TXT('0'));

    /*!
     */
    WindowsDetector::WindowsDetector(void) :
    Detector(TXT("WindowsDetector"), ::hwdet::DefaultEncoding),
    Services(NULL)
    {
    }

    /*!
     */
    WindowsDetector::~WindowsDetector(void)
    {
        Destroy();
    }

    /*! Initialize WMI. This will connect to the WMI server on local computer and connect to the ROOT\\CIMV2 WMI namespace.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::Initialize(void)
    {
        HRESULT hres;                                               // Result of API calls.

        // Initialize COM interface
        hres = CoInitializeEx(NULL, COINIT_MULTITHREADED);

        if (FAILED(hres))
        {
            ReportMessage(TXT("COM initialization failed."));
            return false;
        }

        // Set general COM security levels
        hres =  CoInitializeSecurity(NULL,                          // Security descriptor
                                     -1,                            // COM authentication
                                     NULL,                          // Authentication services
                                     NULL,                          // Reserved
                                     RPC_C_AUTHN_LEVEL_DEFAULT,     // Default authentication 
                                     RPC_C_IMP_LEVEL_IMPERSONATE,   // Default Impersonation  
                                     NULL,                          // Authentication info
                                     EOAC_NONE,                     // Additional capabilities 
                                     NULL);                         // Reserved

        if (FAILED(hres))
        {
            ReportMessage(TXT("Failed to initialize security."));
            CoUninitialize();
            return false;
        }

        IWbemLocator    *Locator = NULL;                            // Locator service

        hres = CoCreateInstance(CLSID_WbemLocator,                  // Class id (locator class)
                                0,                                  // Not aggregate object
                                CLSCTX_INPROC_SERVER,               // Context (INPROC_SERVER = dll which runs in same process as caller)
                                IID_IWbemLocator,                   // Interface reference
                                (void**) &Locator);                 // result

        if (FAILED(hres))
        {
            ReportMessage(TXT("Failed to create Locator service."));
            CoUninitialize();
            return false;
        }

        // Connect to WMI through the IWbemLocator::ConnectServer method
        // Connect to the root\cimv2 namespace with the current user and obtain pointer Services to make IWbemServices calls.
        hres = Locator->ConnectServer(bstr_t(L"ROOT\\CIMV2"),       // Connect to CIMV2 namespace of WMI
                                      NULL,                         // User Name (NULL = curren user)
                                      NULL,                         // User Password (NULL = current)
                                      0,                            // Locale, current
                                      NULL,                         // Security flags
                                      0,                            // Authority
                                      0,                            // Context object
                                      &Services);                   // IWbemServices proxy

        if (FAILED(hres))
        {
            ReportMessage(TXT("Could not connect to WMI."));
            Locator->Release();
            CoUninitialize();
            return false;
        }

        Locator->Release();

        // Set security levels on the proxy
        hres = CoSetProxyBlanket(Services,                          // Indicates the proxy to set
                                 RPC_C_AUTHN_WINNT,                 // Authentication service (xxx_WINNT = use NTLMSSP (Windows NT LAN Manager Security Support Provider))
                                 RPC_C_AUTHZ_NONE,                  // Authorization service (xxx_NONE = no authorization performed on server)
                                 NULL,                              // Server principal name
                                 RPC_C_AUTHN_LEVEL_CALL,            // Authentication level (xxx_CALL = auth only at the beginning of each call)
                                 RPC_C_IMP_LEVEL_IMPERSONATE,       // Impersonation level (xxx_IMPERSONATE = server can impersonate client's security context)
                                 NULL,                              // Client identity handle
                                 EOAC_NONE);                        // Proxy capabilities (NONE = no flags set)

        if (FAILED(hres))
        {
            ReportMessage(TXT("Unable to set proxy blanket."));
            Services->Release();
            CoUninitialize();
            return false;
        }

        ReportMessage(TXT("Detector initialization successfull."));

        return true;
    }

    /*! Run all detection routines and save output into XMLOutputFile.
     *    
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::Detect(void)
    {
        output.ClearRootNode();

        root_node = new XMLElement(TXT("hostInfo"));
        output.SetRootNode(root_node);

        {
            TCHAR   Name[256] = {0};
            DWORD   Len = 256;

            GetComputerName(Name, &Len);

            root_node->AddSubNode(new XMLValueElement< String >(TXT("hostName"), String(Name)));

            SYSTEMTIME stime;

            GetLocalTime(&stime);

            OStringStream stream;

            stream << stime.wYear << TXT('/') << LeadToN(stime.wMonth) << TXT('/') << LeadToN(stime.wDay);
            root_node->AddSubNode(new XMLValueElement< String >(TXT("lastCheckDate"), stream.str()));

            stream.str(TXT(""));

            stream << LeadToN(stime.wHour) << TXT(':') << LeadToN(stime.wMinute) << TXT('.') << LeadToN(stime.wSecond);
            root_node->AddSubNode(new XMLValueElement< String >(TXT("lastCheckTime"), stream.str()));

            root_node->AddSubNode(new XMLValueElement< String >(TXT("detector"), String(TXT("hwdet3_windows"))));
        }

        bool result = DetectOS() & DetectCPU() & DetectMemory() & DetectDrives() & DetectNetwork() & DetectProducts();

        ReportMessage(TXT("Detector finished successfully."));

        return result;
    }

    /*! Disconnect from WMI and delete all generated data.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::Destroy(void)
    {
        if (Services != NULL) {

            Services->Release();
            Services = NULL;
            CoUninitialize();
        }

        delete root_node;
        root_node = NULL;

        return true;
    }

    /*! Detect properties of the operating system.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectOS(void)
    {
        HRESULT                 hres;
        IEnumWbemClassObject    *Enumerator = NULL;
        IWbemClassObject        *OperatingSystem = NULL;
        IWbemClassObject        *ComputerSystem = NULL;

        ReportMessage(TXT("Detecting operating system features."));

        // find ComputerSystem class
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // query language
                                   bstr_t(TXT("SELECT * FROM Win32_ComputerSystem")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,   // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator

        if (SUCCEEDED(hres))
        {
            ULONG returned = 0;                                     // number of classes returned
            
            // get one class from enumerator. There should always be only one.
            hres = Enumerator->Next(WBEM_INFINITE, 1, &ComputerSystem, &returned);

            if ((returned == 0) || (FAILED(hres)))
            {
                // no class found, report error
                Enumerator->Release();
                ComputerSystem->Release();

                return false;
            }
        }
        else
        {
            ReportMessage(TXT("Error in query for Win32_ComputerSystem."));
            return false;
        }

        Enumerator->Release();

        CheckedResult< String > Arch(GetValueAsString(ComputerSystem, L"SystemType"));
        TestAndReport(Arch, TXT("Unable to determine computer architecture."));
        ComputerSystem->Release();

        // find OperatingSystem class
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // query language
                                   bstr_t(TXT("SELECT * FROM Win32_OperatingSystem")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,   // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator

        if (SUCCEEDED(hres))
        {
            ULONG returned = 0;                                     // number of classes returned

            // get one class from enumerator. There should always be only one.
            hres = Enumerator->Next(WBEM_INFINITE, 1, &OperatingSystem, &returned);

            if ((returned == 0) || (FAILED(hres)))
            {
                // no class found, report error
                Enumerator->Release();
                OperatingSystem->Release();
                ReportMessage(TXT("Unable to find Win32_OperatingSystem WMI class."));

                return false;
            }
        }
        else
        {
            ReportMessage(TXT("Error in query for Win32_OperatingSystem."));
            return false;
        }

        Enumerator->Release();

        CheckedResult< String > Name(GetValueAsString(OperatingSystem, L"Caption"));
        CheckedResult< String > Vendor(GetValueAsString(OperatingSystem, L"Manufacturer"));
        CheckedResult< String > SPMajor(GetValueAsString(OperatingSystem, L"ServicePackMajorVersion"));
        CheckedResult< String > SPMinor(GetValueAsString(OperatingSystem, L"ServicePackMinorVersion"));
        CheckedResult< String > SysDir(GetValueAsString(OperatingSystem, L"SystemDirectory"));
        CheckedResult< String > WinDir(GetValueAsString(OperatingSystem, L"WindowsDirectory"));
        CheckedResult< String > Version(GetValueAsString(OperatingSystem, L"Version"));
        CheckedResult< String > Build(GetValueAsString(OperatingSystem, L"BuildType"));
        CheckedResult< unsigned __int64 > Encryption(GetValueAsUInt(OperatingSystem, L"EncryptionLevel"));

        TestAndReport(Name, TXT("Unable to determine OS name."));
        TestAndReport(Vendor, TXT("Unable to determine OS vendor."));
        TestAndReport(SPMajor, TXT("Unable to determine service pack version."));
        TestAndReport(SPMinor, TXT("Unable to determine service pack version."));
        TestAndReport(SysDir, TXT("Unable to determine system directory."));
        TestAndReport(WinDir, TXT("Unable to determine Windows directory."));
        TestAndReport(Version, TXT("Unable to determine version of Windows."));
        TestAndReport(Build, TXT("Unable to determine build type."));
        TestAndReport(Encryption, TXT("Unable to determine encrytpion level."));

        OperatingSystem->Release();

        String architecture;

        if (Arch.GetValue() == TXT("X86-based PC"))
        {
            architecture = TXT("x86");
        }
        else if (Arch.GetValue() == TXT("64-bit Intel PC")) 
        {
            architecture = TXT("ia64");
        }
        else if (Arch.GetValue() == TXT("MIPS-based PC")) 
        {
            architecture = TXT("mips");
        }
        else if (Arch.GetValue() == TXT("Alpha-based PC")) 
        {
            architecture = TXT("alpha");
        }
        else if (Arch.GetValue() == TXT("Power PC")) 
        {
            architecture = TXT("power");
        }
        else if (Arch.GetValue() == TXT("SH-x PC")) 
        {
            architecture = TXT("shx");
        }
        else if (Arch.GetValue() == TXT("StrongARM PC")) 
        {
            architecture = TXT("strongarm");
        }
        else if (Arch.GetValue() == TXT("64-bit Alpha PC")) 
        {
            architecture = TXT("alpha64");
        }
        else if (Arch.GetValue() == TXT("X86-Nec98 PC"))
        {
            architecture = TXT("x86nec");
        }
        else
        {
            architecture = TXT("(unknown)");
        }

        BasicFeatures *basic = new BasicFeatures(Name.GetValue(), Vendor.GetValue(), architecture);
        WindowsAdvancedFeatures *advanced = new WindowsAdvancedFeatures(Version.GetValue(), 
                                                                        Build.GetValue(), 
                                                                        SPMajor.GetValue() + String(TXT(".")) + SPMinor.GetValue(),
                                                                        WinDir.GetValue(), 
                                                                        SysDir.GetValue(), 
                                                                        (unsigned int) Encryption.GetValue());

        hwdet::OperatingSystem *os = new hwdet::OperatingSystem(basic, advanced);

        root_node->AddSubNode(os);

        return true;
    }

    /*! Detect CPU features.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectCPU(void)
    {
        HRESULT              hres;
        IEnumWbemClassObject *Enumerator = NULL;
        XMLElement           *Processors = new XMLElement(TXT("processors"));

        root_node->AddSubNode(Processors);

        ReportMessage(TXT("Detecting CPU features."));

        // get all processors
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // query language
                                   bstr_t(TXT("SELECT * FROM Win32_Processor")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator
        if (SUCCEEDED(hres))
        {
            while (true)
            {
                IWbemClassObject *ClassObject = NULL;
                ULONG            returned = 0;

                hres = Enumerator->Next(WBEM_INFINITE, 1, &ClassObject, &returned);

                if (hres == WBEM_S_FALSE)
                {
                    break;
                }

                if (FAILED(hres))
                {
                    ReportMessage(TXT("Unable to query next processor."));
                    break;
                }

                CheckedResult< String > Name(GetValueAsString(ClassObject, L"Name"));
                CheckedResult< String > Vendor(GetValueAsString(ClassObject, L"Manufacturer"));
                CheckedResult< unsigned __int64 > Speed(GetValueAsUInt(ClassObject, L"CurrentClockSpeed"));
                CheckedResult< unsigned __int64 > Cache(GetValueAsUInt(ClassObject, L"L2CacheSize"));

                ClassObject->Release();

                TestAndReport(Name, TXT("Unable to determine CPU model name."));
                TestAndReport(Vendor, TXT("Unable to determine CPU vendor."));
                TestAndReport(Speed, TXT("Unable to determine CPU speed."));
                TestAndReport(Cache, TXT("Unable to determine size of L2 cache."));

                Processor *processor = new Processor(Name.GetValue(), Vendor.GetValue(), 
                                                     (unsigned long) Cache.GetValue(), (unsigned long) Speed.GetValue());

                Processors->AddSubNode(processor);
            }

            Enumerator->Release();

        }
        else
        {
            ReportMessage(TXT("Unable to enumerate processors."));
            return false;
        }

        return true;
    }

    /*! Detect features of memory sub-system.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectMemory(void)
    {
        IEnumWbemClassObject *Enumerator = NULL;
        HRESULT              hres;
        IWbemClassObject     *OperatingSystem = NULL;
        unsigned __int64     physicalMemory = 0;

        ReportMessage(TXT("Detecting memory properties."));

        // get info about physical memory
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),          // WQL language
                                   bstr_t(TXT("SELECT * FROM Win32_PhysicalMemory")), // query
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, // flags
                                   NULL,                        // No context
                                   &Enumerator);                // Output enumerator

        if (SUCCEEDED(hres))
        {
            // enumerate all physical memory devices and sum their sizes
            while (true)
            {
                IWbemClassObject *ClassObject = NULL;
                ULONG            returned = 0;

                hres = Enumerator->Next(WBEM_INFINITE, 1, &ClassObject, &returned);

                if (hres == WBEM_S_FALSE)
                {
                    break;
                }

                if (FAILED(hres))
                {
                    ReportMessage(TXT("Unable to enumerate properties of physical memory."));
                    break;
                }

                CheckedResult< unsigned __int64 > PhysSize(GetValueAsUInt(ClassObject, L"Capacity")); // in B

                ClassObject->Release();

                if (TestAndReport(PhysSize, TXT("Unable to determine size of physical memory device.")))
                {
                    physicalMemory += PhysSize.GetValue();
                }
            }

            Enumerator->Release();
        }
        else
        {
            ReportMessage(TXT("Unable to find Win32_PhysicalMemory WMI object."));
            return false;
        }

        // get info about virtual memory, swap and paging files
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // WQL language
                                   bstr_t(TXT("SELECT * FROM Win32_OperatingSystem")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator

        if (SUCCEEDED(hres))
        {
            ULONG returned = 0;

            hres = Enumerator->Next(WBEM_INFINITE, 1, &OperatingSystem, &returned);

            if ((returned == 0) || (FAILED(hres)))
            {
                ReportMessage(TXT("Unable to determine OS properties."));
            }

            Enumerator->Release();
        }
        else
        {
            ReportMessage(TXT("Unable to find Win32_OperatingSystem WMI object."));
            return false;
        }

        
        CheckedResult< unsigned __int64 > Swap(GetValueAsUInt(OperatingSystem, L"TotalSwapSpaceSize")); // in KB
        CheckedResult< unsigned __int64 > Virtual(GetValueAsUInt(OperatingSystem, L"TotalVirtualMemorySize")); // in KB
        CheckedResult< unsigned __int64 > Page(GetValueAsUInt(OperatingSystem, L"SizeStoredInPagingFiles")); // in KB

        OperatingSystem->Release();

        TestAndReport(Swap, TXT("Unable to determine size of swap file."));
        TestAndReport(Virtual, TXT("Unable to determine size of virtual memory."));
        TestAndReport(Page, TXT("Unable to determine size of paging files."));

        Memory *memory = new Memory(physicalMemory, Virtual.GetValue() << 10, Swap.GetValue() << 10, Page.GetValue() << 10);

        root_node->AddSubNode(memory);

        return true;
    }

    /*! Detect all drives present on the system.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectDrives(void)
    {
        XMLElement *Drives = new XMLElement(TXT("diskDrives"));

        root_node->AddSubNode(Drives);

        // Detect properties of the BEEN disk - that is, the drive on which BEEN installation is located.
        // To find out which drive it is, we use value of the BEEN_HOME environment variable.
        {
    #ifdef UNICODE
            Char *been_home_var = ::_wgetenv(L"BEEN_HOME");
    #else
            Char *been_home_var = getenv("BEEN_HOME");
    #endif

            if (been_home_var != NULL)
            {
                String been_home(been_home_var);

                ULARGE_INTEGER free_bytes;
                ULARGE_INTEGER total_bytes;

                BOOL res = GetDiskFreeSpaceEx(been_home.c_str(), &free_bytes, &total_bytes, NULL);

                if (res == TRUE)
                {
                    XMLElement *beenDisk = new XMLElement(TXT("beenDisk"));

                    beenDisk->AddSubNode(new XMLValueElement< String >(TXT("path"), been_home));
                    beenDisk->AddSubNode(new XMLValueElement< unsigned long long >(TXT("size"), unsigned long long(total_bytes.QuadPart)));
                    beenDisk->AddSubNode(new XMLValueElement< unsigned long long >(TXT("freeSpace"), unsigned long long(free_bytes.QuadPart)));

                    root_node->AddSubNode(beenDisk);
                }
                else
                {
                    ReportMessage(TXT("Unable to determine properties of BEEN_DISK."));
                }
            }
            else
            {
                ReportMessage(TXT("BEEN_HOME environment variable is not defined."));
            }
        }

        return DetectHDD(Drives)
    #ifdef DETECT_CDROM_DRIVES
               && DetectCDRom(Drives) 
    #endif
    #ifdef DETECT_TAPE_DRIVES
               && DetectOtherDrives(Drives)
    #endif
               ;
    }

    /*! Enumerate all hard-drives installed on the computer.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectHDD(XMLElement *drives)
    {
        IEnumWbemClassObject *Enumerator = NULL;
        HRESULT              hres;

        ReportMessage(TXT("Detecting hard drives."));

        // find all hard drives
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),               // query language
                                   bstr_t(TXT("SELECT * FROM Win32_DiskDrive")), // Query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator

        if (SUCCEEDED(hres))
        {
            while (true)
            {
                IWbemClassObject *DriveObject = NULL;
                ULONG            returned = 0;

                hres = Enumerator->Next(WBEM_INFINITE, 1,&DriveObject, &returned);

                if (hres == WBEM_S_FALSE)
                {
                    break;
                }

                if (FAILED(hres))
                {
                    ReportMessage(TXT("Unable to query next drive."));
                    break;
                }

                CheckedResult< String > Device(GetValueAsString(DriveObject, L"DeviceID"));
                CheckedResult< String > Model(GetValueAsString(DriveObject, L"Model"));
                //CheckedResult< String > Media(GetValueAsString(DriveObject, L"MediaType"));
                CheckedResult< unsigned __int64 > Size(GetValueAsUInt(DriveObject, L"Size"));

                DriveObject->Release();

                TestAndReport(Device, TXT("Unable to determine device name for the drive."));
                TestAndReport(Model, TXT("Unable to determine model name of the drive."));
                //TestAndReport(Media, TXT("Unable to determine media type."));
                TestAndReport(Size, TXT("Unable to determine size of the drive."));

                DiskDrive *drive = new DiskDrive(Size.GetValue(), Model.GetValue(), /*Media.GetValue()*/String(TXT("HDD")), Device.GetValue());

                drives->AddSubNode(drive);

                DetectPartitions(drive);
            }

            Enumerator->Release();
        }
        else
        {
            ReportMessage(TXT("Unable to query hard-drives."));
            return false;
        }

        return true;
    }

    /*! Enumerate partitions on the given drive.
     *
     *  \param disk_drive Disk drive into which data about partitions will be inserted.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectPartitions(DiskDrive *disk_drive)
    {
        HRESULT hres;
        
        String query_text;

        // Generate query text.
        {
            OStringStream stream;

            stream << TXT("ASSOCIATORS OF {Win32_DiskDrive.DeviceID=\"");

            const String &str = disk_drive->GetDeviceName();

            for (size_t i = 0; i < str.length(); ++i)
            {
                if (str[i] == TXT('\\'))
                {
                    stream << TXT("\\\\");
                }
                else
                {
                    stream << str[i];
                }
            }

            stream << TXT("\"} WHERE AssocClass=Win32_DiskDriveToDiskPartition");

            query_text = stream.str();
        }

        IEnumWbemClassObject *Partitions = NULL;

        hres = Services->ExecQuery(bstr_t(TXT("WQL")),
                                   bstr_t(query_text.c_str()),
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,
                                   NULL,
                                   &Partitions);
        
        if (SUCCEEDED(hres))
        {
            while (true)
            {
                IWbemClassObject *PartitionObject = NULL;
                ULONG            returned = 0;

                hres = Partitions->Next(WBEM_INFINITE, 1, &PartitionObject, &returned);

                if (FAILED(hres) || (returned == 0))
                {
                    break;
                }

                if ((SUCCEEDED(hres)) && (returned > 0))
                {
                    CheckedResult< String > PartDevice(GetValueAsString(PartitionObject, L"DeviceID"));
                    //CheckedResult< unsigned __int64 > PartSize(GetValueAsUInt(PartitionObject, L"Size"));

                    PartitionObject->Release();

                    //TestAndReport(PartSize, TXT("Unable to determine size of the partition."));

                    if (PartDevice.Succeeded())
                    {
                        String partition_query;

                        {
                            OStringStream stream;
                            stream << TXT("ASSOCIATORS OF {Win32_DiskPartition.DeviceID=\"") << PartDevice.GetValue()
                                   << TXT("\"} WHERE AssocClass=Win32_LogicalDiskToPartition");
                            partition_query = stream.str();
                        }

                        IEnumWbemClassObject *LogDiskEnumerator = NULL;

                        hres = Services->ExecQuery(bstr_t(TXT("WQL")),
                                                   bstr_t(partition_query.c_str()),
                                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,
                                                   NULL,
                                                   &LogDiskEnumerator);

                        if (SUCCEEDED(hres))
                        {
                            IWbemClassObject *LogDisk = NULL;

                            hres = LogDiskEnumerator->Next(WBEM_INFINITE, 1, &LogDisk, &returned);
                            LogDiskEnumerator->Release();

                            if ((SUCCEEDED(hres)) && (returned > 0))
                            {
                                CheckedResult< String > Name(GetValueAsString(LogDisk, L"Name"));
                                CheckedResult< String > FS(GetValueAsString(LogDisk, L"FileSystem"));
                                CheckedResult< unsigned __int64 > Size(GetValueAsUInt(LogDisk, L"Size"));
                                CheckedResult< unsigned __int64 > FreeSpace(GetValueAsUInt(LogDisk, L"FreeSpace"));

                                LogDisk->Release();

                                TestAndReport(Name, TXT("Unable to determine partition's name."));
                                TestAndReport(FS, TXT("Unable to determine partition file system."));
                                TestAndReport(Size, TXT("Unable to determine size of the partition."));
                                TestAndReport(FreeSpace, TXT("Unable to determine size of free space on the partition."));

                                DiskPartition *part = new DiskPartition(PartDevice.GetValue(), 
                                                                        Name.GetValue(), 
                                                                        FS.GetValue(), 
                                                                        Size.GetValue(), 
                                                                        FreeSpace.GetValue());

                                disk_drive->AddSubNode(part);
                            } 
                            else 
                            {
                                ReportMessage(TXT("Unable to query logical disk."));
                            }
                        } 
                        else 
                        {
                            ReportMessage(TXT("Unable to enumerate logical disks."));
                        }
                    } 
                    else 
                    {
                        ReportMessage(TXT("Unable to determina partition's device."));
                    }
                } 
                else 
                {
                    ReportMessage(TXT("Unable to query for next partition."));
                }
            }

            Partitions->Release();
        }
        else 
        {
            ReportMessage(TXT("Unable to query for drive partitions."));
            return false;
        }

        return true;
    }

    /*! Enumerate all CD/DVD drives.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectCDRom(XMLElement *drives)
    {
        IEnumWbemClassObject *Enumerator = NULL;
        HRESULT              hres;

        ReportMessage(TXT("Detecting CD_ROM drives."));

        // detect all cd/dvd drives
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // query language
                                   bstr_t(TXT("SELECT * FROM Win32_CDROMDrive")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator

        if (SUCCEEDED(hres))
        {
            while (true)
            {
                IWbemClassObject *ClassObject = NULL;
                ULONG            returned = 0;

                hres = Enumerator->Next(WBEM_INFINITE, 1, &ClassObject, &returned);

                if (hres == WBEM_S_FALSE)
                {
                    break;
                }

                if (FAILED(hres))
                {
                    ReportMessage(TXT("Unable to query next CD/DVD drive."));
                    Enumerator->Release();
                    return false;
                }

                CheckedResult< String > Device(GetValueAsString(ClassObject, L"DeviceID"));
                CheckedResult< String > Name(GetValueAsString(ClassObject, L"Name"));
                
                ClassObject->Release();

                TestAndReport(Device, TXT("Unable to determine device id of the CD-ROM drive."));
                TestAndReport(Name, TXT("Unable to determine model name of the CD-ROM drive."));

                DiskDrive *cdrom = new DiskDrive(0, Name.GetValue(), String(TXT("CD-ROM")), Device.GetValue());

                drives->AddSubNode(cdrom);
            }

            Enumerator->Release();
        }
        else 
        {
            ReportMessage(TXT("Unable to enumerate CD/DVD drives."));
            return false;
        }

        return true;
    }

    /*! Enumerate tape drives installed.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectOtherDrives(XMLElement *drives)
    {
        IEnumWbemClassObject *Enumerator = NULL;
        HRESULT              hres;

        ReportMessage(TXT("Detecting other drives."));

        // detect all tape drives
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // query language
                                   bstr_t(TXT("SELECT * FROM Win32_TapeDrive")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, //falgs
                                   NULL,                            //no context
                                   &Enumerator);                    // output enumerator
        
        if (SUCCEEDED(hres))
        {
            while (true)
            {
                IWbemClassObject *ClassObject = NULL;
                ULONG            returned = 0;

                hres = Enumerator->Next(WBEM_INFINITE, 1, &ClassObject, &returned);

                if (hres == WBEM_S_FALSE)
                {
                    break;
                }

                if (FAILED(hres))
                {
                    ReportMessage(TXT("Unable to query next tape drive."));
                    Enumerator->Release();
                    return false;
                }

                CheckedResult< String > Device(GetValueAsString(ClassObject, L"DeviceID"));
                CheckedResult< String > Name(GetValueAsString(ClassObject, L"Name"));
                //CheckedResult< String > Media(GetValueAsString(ClassObject, L"MediaType"));
                CheckedResult< unsigned __int64 > Size(GetValueAsUInt(ClassObject, L"Size"));

                ClassObject->Release();

                TestAndReport(Device, TXT("Unable to determine device name of the tape drive."));
                TestAndReport(Name, TXT("Unable to determine model name of the tape drive."));
                //TestAndReport(Media, TXT("Unable to determine media type in tape drive."));
                TestAndReport(Size, TXT("Unable to determine size of the tape drive."));

                DiskDrive *tape = new DiskDrive(Size.GetValue(), Name.GetValue(), String(TXT("Tape")), Device.GetValue());

                drives->AddSubNode(tape);
            }

            Enumerator->Release();
        }
        else 
        {
            ReportMessage(TXT("Unable to enumerate tape drives."));
            return false;
        }

        return true;
    }

    /*! Enumerates all network adapters present on the system. Virtual and disabled adapters are not processed.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectNetwork(void)
    {
        IEnumWbemClassObject *Enumerator = NULL;
        HRESULT              hres;
        XMLElement           *Adapters = new XMLElement(TXT("network"));

        root_node->AddSubNode(Adapters);

        ReportMessage(TXT("Detecting network adapters."));

        // get all adapters
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // query language
                                   bstr_t(TXT("SELECT * FROM Win32_NetworkAdapter")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,   // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator

        if (SUCCEEDED(hres))
        {
            while (true)
            {
                IWbemClassObject *ClassObject = NULL;
                ULONG            returned = 0;

                hres = Enumerator->Next(WBEM_INFINITE, 1, &ClassObject, &returned);

                if (hres == WBEM_S_FALSE)
                {
                    break;
                }

                if (FAILED(hres))
                {
                    ReportMessage(TXT("Unable to query next network adapter."));
                    break;
                }

                {
                    CheckedResult< unsigned __int64 > Status(GetValueAsUInt(ClassObject, L"StatusInfo"));
                    TestAndReport(Status, TXT("Unable to determine status of the adapter."));

                    // Test whether adapter is enabled, disabled adapters are skipped
                    if (Status.Succeeded() && (Status.GetValue() == 0x04)) {
                        continue;
                    }
                }

                CheckedResult< String > Name(GetValueAsString(ClassObject, L"Name"));
                CheckedResult< String > Vendor(GetValueAsString(ClassObject, L"Manufacturer"));
                CheckedResult< String > MAC(GetValueAsString(ClassObject, L"MACAddress"));
                CheckedResult< String > Type(GetValueAsString(ClassObject, L"AdapterType"));

                ClassObject->Release();

                TestAndReport(Name, TXT("Unable to determine netowrk adapter name."));
                TestAndReport(Vendor, TXT("Unable to determine network adapter vendor."));
                TestAndReport(MAC, TXT("Unable to determine network adapter MAC address."));
                TestAndReport(Type, TXT("Unable to determine network adapter type."));

                NetworkAdapter *adapter = new NetworkAdapter(Name.GetValue(), Vendor.GetValue(), MAC.GetValue(), Type.GetValue());

                Adapters->AddSubNode(adapter);
            }

            Enumerator->Release();

        } 
        else 
        {
            ReportMessage(TXT("Unable to find Win32_NetworkAdapter WMI objects."));
            return false;
        }

        return true;
    }

    /*! Enumerates all applications installed on the system. It will detect only applications which are visible in
     *  Control Panel->Add/Remove Programs applet.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::DetectProducts(void)
    {
        XMLElement *Products = new XMLElement(TXT("installedProducts"));

        root_node->AddSubNode(Products);

        ReportMessage(TXT("Detecting installed software."));

    #ifdef OLD_DETECT_PRODUCTS
        // old algorithm to detect products. It is able to detect only applications installed in "All users" and current
        // user profile.

        IEnumWbemClassObject    *Enumerator = NULL;
        HRESULT                 hres;

        // find all product classes
        hres = Services->ExecQuery(bstr_t(TXT("WQL")),              // query language
                                   bstr_t(TXT("SELECT * FROM Win32_Product")), // query text
                                   WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,   // flags
                                   NULL,                            // No context
                                   &Enumerator);                    // Output enumerator

        if (SUCCEEDED(hres))
        {
            while (true)
            {
                IWbemClassObject *ClassObject = NULL;
                ULONG            returned = 0;

                hres = Enumerator->Next(WBEM_INFINITE, 1, &ClassObject, &returned);

                if (hres == WBEM_S_FALSE)
                {
                    break;
                }

                if (FAILED(hres))
                {
                    ReportMessage(TXT("Unable to query next product."));
                    break;
                }

                CheckedResult< String > Name(GetValueAsString(ClassObject, L"Name"));
                CheckedResult< String > Version(GetValueAsString(ClassObject, L"Version"));
                CheckedResult< String > Vendor(GetValueAsString(ClassObject, L"Vendor"));

                ClassObject->Release();

                TestAndReport(Name, TXT("Unable to determine product name."));
                TestAndReport(Version, TXT("Unable to determine product version."));
                TestAndReport(Vendor, TXT("Unable to determine product vendor."));

                Product *product = new Product(Name.GetValue(), Vendor.GetValue(), Version.GetValue());

                Products->AddSubNode(product);
            }

            Enumerator->Release();
        }
        else 
        {
            ReportMessage(TXT("Unable to query for Win32_Product WMI classes."));
            return false;
        }

        return true;
    #else
        // Newer algorithm to query for installed applications. It should be able to detect more than the old one.
        // It uses Windows Installer records stored in registry in HKEY_LOCAL_MACHINE\Microsoft\Windows\CurrentVersion\Uninstall
        // key and its subkeys.

        HKEY uninstallKey(0);                                       // handle to "uninstall" registry key.

        // Open registry key with uninstall information
        bool result = OpenRegistryKey(HKEY_LOCAL_MACHINE, TXT("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall"), uninstallKey);

        if (!result) 
        {
            ReportMessage(TXT("Unable to open uninstall registry key."));
            return false;
        }

        vector< String > appKeys;                                   // this will hold list of sub-keys in uninstall key

        result = EnumerateSubKeys(uninstallKey, appKeys);           // enumerate subkeys (d'oh...)

        if (!result) 
        {
            ReportMessage(TXT("Unable to enumerate subkeys of the uninstall key."));
            CloseRegistryKey(uninstallKey);
            return false;
        }

        // Go through all sub-keys of uninstall key. Each sub-key corresponds to one application (there may be keys that do
        // not correspond to any application).
        for (vector< String >::const_iterator it = appKeys.begin(); it != appKeys.end(); ++it) 
        {
            map< String, String > values;

            // get map of all values of the current subkey
            result = GetSubKeyValueMap(uninstallKey, *it, values);

            if (result) 
            {
                // test whether we can get name of the application, if not, do nothing
                if (values.find(TXT("DisplayName")) != values.end())
                {
                    // name's there, so get it
                    String name = values[TXT("DisplayName")];
                    String vendor(TXT("(unknown)"));
                    String version(TXT("(unknown)"));

                    // let's see if we can get vendor's name
                    if (values.find(TXT("Publisher")) != values.end())
                    {
                        // yup, it's there
                        vendor = values[TXT("Publisher")];
                    }

                    // and now test for version
                    if (values.find(TXT("DisplayVersion")) != values.end())
                    {
                        version = values[TXT("DisplayVersion")];
                    } 
                    else 
                    {
                        // no DisplayVersion value, we can try using minor and major version numbers, but
                        // they are often not there :(
                        if (values.find(TXT("VersionMajor")) != values.end()) 
                        {
                            version = values[TXT("VersionMajor")];
                            if (values.find(TXT("VersionMinor")) != values.end())
                            {
                                version += String(TXT(".")) + values[TXT("VersionMinor")];
                            }
                        }
                    }

                    // create and add new product to the list
                    Product *product = new Product(name, vendor, version);

                    Products->AddSubNode(product);
                }
            }
        }

        return true;
    #endif
    }

    /*! \return String containing data retrieved by detector.
     */
    String WindowsDetector::GetOutputString(void) const
    {
        OStringStream stream;
        WriteToStream(stream);
        return stream.str();
    }

    /*! \param stream Stream to which output file should be written.
     *   \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool WindowsDetector::WriteToStream(OStream &stream) const 
    {
        output.Write(stream);
        return true;
    }

    /*! Convert number to string and add leading characters so that resulting string has at least given length.
     *
     *  \param number Number to convert.
     *  \param length Requested length of resulting string. If number has more digits than requested length, no characters are added.
     *  \param lead_char Character which should be added to the begining of the string.
     *
     *  \return String with converted number.
     */
    String LeadToN(int number, size_t length, Char lead_char)
    {
        OStringStream stream;

        stream << number;

        String lead;
        String num = stream.str();

        if (num.length() < length)
        {
            for (size_t i = 0; i < length - num.length(); ++i)
            {
                lead += lead_char;
            }
        }

        return lead + stream.str();
    }

} // namespace hwdet

#pragma warning(pop)
