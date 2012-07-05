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

/*! \file Registry.cpp
 *  \author Branislav Repcek
 *  \date 25. 4. 2006
 *
 *  \brief Implementation of function to simplify registry access.
 */

#include <windows.h>
#include <vector>
#include <map>
#include <sstream>
#include "../Common/UnicodeString.h"
#include "Registry.h"

using namespace std;

namespace hwdet
{
    
    /*! Open registry key with the specified name. Key has to be sub-key of the key already open.
     *
     *  \param hkey Handle to already opened registry key. This can be key opened either with OpenRegistryKey function 
     *              or it can be one of HKEY_CLASSES_ROOT, HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE, HKEY_USERS.
     *  \param keyName Name of the sub-key to open. Names are not case sensitive.
     *  \param result Reference to variable which receives handle to the key opened.
     *  \param access Access rights requested. Default value is read-only access. More about access rights can be found in MSDN
     *                http://msdn.microsoft.com/library/default.asp?url=/library/en-us/sysinfo/base/registry_key_security_and_access_rights.asp
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool OpenRegistryKey(const HKEY hkey, const String &keyName, HKEY &result, const REGSAM access)
    {
        LONG res = RegOpenKeyEx(hkey, keyName.c_str(), 0, access, &result);

        return res == ERROR_SUCCESS;
    }

    /*! Close registry key.
     *
     *  \param hkey Handle to the key to close.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool CloseRegistryKey(const HKEY hkey)
    {
        LONG res = RegCloseKey(hkey);

        return res == ERROR_SUCCESS;
    }

    /*! Get list of names of all sub keys of given key.
     *
     *  \param hkey Handle to already opened registry key. This can be key opened either with OpenRegistryKey function 
     *              or it can be one of HKEY_CLASSES_ROOT, HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE, HKEY_USERS.
     *  \param result Reference to the vector of string which will receive names of sub keys of given key. All data 
     *              contained in vector before executing this function will be erased.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool EnumerateSubKeys(const HKEY hkey, std::vector< String > &result)
    {
        DWORD subKeys(0);
        DWORD maxSubKeyLen(0);

        LONG res = RegQueryInfoKey(hkey,                            // key handle
                                   NULL,                            // class name pointer (not needed)
                                   NULL,                            // buffer size for class name (not needed)
                                   NULL,                            // reserved
                                   &subKeys,                        // number of sub-keys
                                   &maxSubKeyLen,                   // length of the longest name of the sub-key (without terminating nul)
                                   NULL,                            // longest name of class (not needed)
                                   NULL,                            // number of values (not needed)
                                   NULL,                            // longest value name (not needed)
                                   NULL,                            // longest value data (not needed)
                                   NULL,                            // security descriptor of the key (not needed)
                                   NULL);                           // last write time (not needed)

        if (res == ERROR_SUCCESS)
        {
            result.clear();

            TCHAR *subKeyName = new TCHAR[maxSubKeyLen + 1];
            DWORD nameLen(maxSubKeyLen + 1);

            for (DWORD i = 0; i < subKeys; ++i)
            {
                ZeroMemory((PVOID) subKeyName, sizeof(TCHAR) * (maxSubKeyLen + 1));

                // query next sub-key
                res = RegEnumKeyEx(hkey,                            // key handle
                                   i,                               // sub-key index
                                   subKeyName,                      // name of the sub-key
                                   &nameLen,                        // length of name buffer
                                   NULL,                            // reserved
                                   NULL,                            // class name (not needed)
                                   NULL,                            // class name length (not needed)
                                   NULL);                           // last write time (not needed)

                if (res == ERROR_SUCCESS)
                {
                    result.push_back(String(subKeyName));
                } 
                else if (res == ERROR_NO_MORE_ITEMS)
                {
                    // no more items, exit
                    break;
                }

                nameLen = maxSubKeyLen + 1;
            }

            delete subKeyName;

            return true;
        } 
        else 
        {
            // unable to query key data
            return false;
        }
    }

    /*! Get list of names of all values of specified key.
     *
     *  \param hkey Handle to already opened registry key. This can be key opened either with OpenRegistryKey function 
     *         or it can be one of HKEY_CLASSES_ROOT, HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE, HKEY_USERS.
     *  \param result Reference to the vector which receives names of values of specified key. All data contained in vector
     *         prior executing this function will be removed.
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool EnumerateValues(const HKEY hkey, std::vector< String > &result)
    {
        DWORD values(0);
        DWORD valueNameLen(0);

        LONG res = RegQueryInfoKey(hkey,                            // key handle
                                   NULL,                            // class name pointer (not needed)
                                   NULL,                            // buffer size for class name (not needed)
                                   NULL,                            // reserved
                                   NULL,                            // number of sub-keys (not needed)
                                   NULL,                            // length of the longest name of the sub-key (without terminating nul) (not needed)
                                   NULL,                            // longest name of class (not needed)
                                   &values,                         // number of values
                                   &valueNameLen,                   // longest value name
                                   NULL,                            // longest value data (not needed)
                                   NULL,                            // security descriptor of the key (not needed)
                                   NULL);                           // last write time (not needed)

        if (res == ERROR_SUCCESS)
        {
            result.clear();

            TCHAR *valName = new TCHAR[valueNameLen + 1];
            DWORD valNameLen(valueNameLen + 1);

            for (DWORD i = 0; i < values; ++i)
            {
                ZeroMemory((PVOID) valName, sizeof(TCHAR) * (valueNameLen + 1));

                // query nex value
                res = RegEnumValue(hkey,                            // key handle
                                   i,                               // value index
                                   valName,                         // value name
                                   &valNameLen,                     // length of name
                                   NULL,                            // reserved
                                   NULL,                            // value type (not needed)
                                   NULL,                            // value data (not needed)
                                   NULL);                           // data length (not needed)

                if (res == ERROR_SUCCESS)
                {
                    result.push_back(String(valName));
                }
                else if (res == ERROR_NO_MORE_ITEMS) 
                {
                    // no more items, exit loop
                    break;
                }

                valNameLen = valueNameLen + 1;
            }

            delete valName;

            return true;
        }
        else 
        {
            // unable to query key data
            return false;
        }
    }

    /*! Convert array of BYTEs to the string based on the type of data contained in array.
     *
     *  \param data Pointer to block of memory which contains data returned by registry data query functions.
     *  \param type Type of the data in the array. Following types are supported:
     *              REG_BINARY, REG_DWORD, REG_EXPAND_SZ, REG_MULTI_SZ, REG_NONE, REG_QWORD, REG_SZ.
     *  \param dataLength Length of the memory block with the data.
     *
     *  \return String representation of the buffer. REG_SZ, REG_EXPAND, REG_DWORD, REG_QWORD values are converted
     *          to strings directly. No expansion of env. variables is done for REG_EXPAND values. REG_BINARY values 
     *          are converted to the string containing hexadecimal represantation of each byte. REG_MULTI_SZ variables
     *          are converted to one long string with each sub-string on the separate line. Note that buffer containing
     *          REG_MULTI_SZ value is modified during execution of the function.
     */
    String RegistryValueToString(BYTE *data, DWORD type, DWORD dataLength)
    {
        switch (type)
        {
            case REG_BINARY:
                {
                    const Char digits[] = TXT("0123456789abcdef");

                    OStringStream stream;

                    // convert data to hexadecimal string, each BYTE is represented by 2 hex digits
                    for (size_t i = 0; i < dataLength; ++i)
                    {
                        stream << digits[data[i] >> 4] << digits[data[i] % 16] << (i == dataLength - 1 ? "" : " ");
                    }

                    return stream.str();
                }

            case REG_DWORD:
                {
                    OStringStream stream;
                    stream << *((DWORD *) data);

                    return stream.str();
                }

            case REG_EXPAND_SZ:
                // variables that may occur in the data are not expanded
                return String((Char *) data);

            case REG_MULTI_SZ:
                {
                    Char *data_ptr = (Char *) data;

                    // walk through the data (data string is terminated by double nul)
                    // Last two bytes are double nul, no need to check that
                    for (size_t k = 0; k < dataLength / sizeof(Char) - 1; ++k)
                    {
                        // replace every nul character by semicolon
                        if (data_ptr[k] == 0)
                        {
                            data_ptr[k] = TXT(';');
                        }
                    }

                    return String((Char *) data);
                }

            case REG_NONE:
                return String(TXT("(no value)"));

            case REG_QWORD:
                {
                    OStringStream stream;

                    stream << *((LONGLONG *) data);

                    return stream.str();
                }

            case REG_SZ:
                return String((Char *) data);

            default:
                return String(TXT("(unknown value type)"));
        }
    }

    /*! Build map containing all values of sub key of given key. Sort key of resulting map is value name.
     *
     *  \param hkey Handle to already opened registry key. This can be key opened either with OpenRegistryKey function 
     *         or it can be one of HKEY_CLASSES_ROOT, HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE, HKEY_USERS.
     *  \param subKeyName Name of sub-key to retrieve values from.
     *  \param result Reference to map which will receive names and data for all values in the specified key.
     *         Format of the data string for specific value depends on the type of data stored in value. 
     *         REG_SZ, REG_EXPAND, REG_DWORD, REG_QWORD values are converted to strings directly. 
     *         No expansion of environment variables is done for REG_EXPAND values. REG_BINARY values 
     *         are converted to the string containing hexadecimal represantation of each byte. REG_MULTI_SZ variables
     *         are converted to one long string with each sub-string on the separate line.
     *         All data contained in map prior executing this function will be erased.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool GetSubKeyValueMap(const HKEY hkey, const String &subKeyName, std::map< String, String > &result)
    {
        HKEY subKey(0);

        // open sub key
        if (!OpenRegistryKey(hkey, subKeyName, subKey))
        {
            return false;
        }

        DWORD values(0);
        DWORD valueNameLen(0);
        DWORD valueDataLen(0);

        // query info about sub key
        LONG res = RegQueryInfoKey(subKey,                          // key handle
                                   NULL,                            // class name pointer (not needed)
                                   NULL,                            // buffer size for class name (not needed)
                                   NULL,                            // reserved
                                   NULL,                            // number of sub-keys (not needed)
                                   NULL,                            // length of the longest name of the sub-key (without terminating nul) (not needed)
                                   NULL,                            // longest name of class (not needed)
                                   &values,                         // number of values
                                   &valueNameLen,                   // longest value name
                                   &valueDataLen,                   // longest value data
                                   NULL,                            // security descriptor of the key (not needed)
                                   NULL);                           // last write time (not needed)

        if (res == ERROR_SUCCESS)
        {
            result.clear();

            TCHAR *valName = new TCHAR[valueNameLen + 1];           // name of current value
            BYTE  *valData = new BYTE[valueDataLen + 1];            // value data
            DWORD valNameLen(valueNameLen + 1);                     // length of current name
            DWORD valDataLen(valueDataLen + 1);                     // length of current data
            DWORD valType(0);                                       // type of current value

            for (DWORD i = 0; i < values; ++i)
            {
                ZeroMemory((PVOID) valName, sizeof(TCHAR) * (valueNameLen + 1));
                ZeroMemory((PVOID) valData, valueDataLen + 1);

                // query nex value
                res = RegEnumValue(subKey,                          // key handle
                                   i,                               // value index
                                   valName,                         // value name
                                   &valNameLen,                     // length of name
                                   NULL,                            // reserved
                                   &valType,                        // value type
                                   valData,                         // value data
                                   &valDataLen);                    // data length

                if (res == ERROR_SUCCESS)
                {
                    result[String(valName)] = RegistryValueToString(valData, valType, valDataLen);
                }
                else if (res == ERROR_NO_MORE_ITEMS) 
                {
                    // no more items, exit loop
                    break;
                }

                // reset max lengths
                valNameLen = valueNameLen + 1;
                valDataLen = valueDataLen + 1;
            }

            delete valName;
            delete valData;

            CloseRegistryKey(subKey);

            return true;
        }
        else 
        {
            CloseRegistryKey(subKey);
            // unable to query key data
            return false;
        }
    }

    /*! Get specified value and convert it to string.
     *
     *  \param hkey Handle to already opened registry key. This can be key opened either with OpenRegistryKey function 
     *         or it can be one of HKEY_CLASSES_ROOT, HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE, HKEY_USERS.
     *  \param valueName Name of value to retrieve.
     *  \param result Reference to string variable which receives requested value. Format of the result depends on the
     *         type of data stored in value. REG_SZ, REG_EXPAND, REG_DWORD, REG_QWORD values are converted
     *         to strings directly. No expansion of env. variables is done for REG_EXPAND values. REG_BINARY values 
     *         are converted to the string containing hexadecimal represantation of each byte. REG_MULTI_SZ variables
     *         are converted to one long string with each sub-string on the separate line.
     *
     *  \return <code>true</code> on success, <code>false</code> otherwise.
     */
    bool GetValueAsString(const HKEY hkey, const String &valueName, String &result)
    {
        const DWORD START_DATA_SIZE = 1024;
        const DWORD DATA_INCREMENT = 1024;

        BYTE  *data = new BYTE[START_DATA_SIZE];
        DWORD dataSize(START_DATA_SIZE);
        DWORD valueType(0);
        LONG  res = 0;
         
        // loop while the buffer is not big enough to hold data stored in the value.
        while ((res = RegQueryValueEx(hkey, valueName.c_str(), NULL, &valueType, (LPBYTE) data, &dataSize)) == ERROR_MORE_DATA)
        {
            delete data;
            dataSize += DATA_INCREMENT;
            data = new BYTE[dataSize];
            memset((void *) data, 0, dataSize);
        }

        if (res == ERROR_SUCCESS)
        {
            result = RegistryValueToString(data, valueType, dataSize);

            return true;
        }
        else 
        {
            return false;
        }
    }

} // namespace hwdet