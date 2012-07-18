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

/*! \file WMIHelper.cpp
    \author Branislav Repcek
    \date 10. 12. 2005

    \brief Implementation of WMI helper routines.
*/

#include <windows.h>
#include <comdef.h>
#include <wbemidl.h>
#include "WMIHelper.h"
#include "../Common/UnicodeString.h"

using namespace std;

namespace hwdet
{

    /*! Convert value stored in variant to string representation.
     *
     *  \param var Variant to convert
     *  \return Textual representation of variant value. Returns "unknown" when variant is empty or value is undefined or has 
     *          unsupported type
     *
     *  \note It does not support date, time, array types.
     */
    String VariantToString(const VARIANT &var)
    {
        OStringStream stream;

        switch (var.vt)
        {
		    default:
            case VT_NULL:                                           // no value
            case VT_EMPTY:                                          // no value
            case VT_EMPTY | VT_BYREF:                               // bad variant
                return String(TXT("(unknown)"));

            case VT_UI1:                                            // char
                stream << (long) var.bVal;
                break;

            case VT_UI1 | VT_BYREF:                                 // Reference to char
                stream << (long) *var.pbVal;
                break;

            case VT_UI2:                                            // 16 bit unsigned int
                stream << var.uiVal;
                break;

            case VT_UI2 | VT_BYREF:                                 // Reference to 16 bit unsigned int
                stream << *var.puiVal;
                break;

            case VT_UI4:                                            // 32 bit unsigned int
                stream << var.ulVal;
                break;

            case VT_UI4 | VT_BYREF:                                 // Reference to 32 bit unsigned int
                stream << *var.pulVal;
                break;

            case VT_UI8:                                            // 64 bit unsigned int
                stream << var.ullVal;
                break;

            case VT_UI8 | VT_BYREF:                                 // Reference to 64 bit unsigned int
                stream << *var.pullVal;
                break;

            case VT_UINT:                                           // Unsigned int
                stream << var.uintVal;
                break;

            case VT_UINT | VT_BYREF:                                // Reference to unsigned int
                stream << *var.puintVal;
                break;

            case VT_INT:                                            // int
                stream << var.intVal;
                break;

            case VT_INT | VT_BYREF:                                 // Reference to int
                stream << *var.puintVal;
                break;

            case VT_I1:                                             // char
                stream << var.cVal;
                break;

            case VT_I1 | VT_BYREF:                                  // reference to char
                stream << *var.pcVal;
                break;

            case VT_I2:                                             // 16 bit int
                stream << var.iVal;
                break;

            case VT_I2 | VT_BYREF:                                  // Pointer to 16 bit int
                stream << *var.piVal;
                break;

            case VT_I4:                                             // 32 bit int
                stream << var.lVal;
                break;

            case VT_I4 | VT_BYREF:                                  // Pointer to 32 bit int
                stream << *var.plVal;
                break;

            case VT_I8:                                             // 64 bit int
                stream << var.llVal;
                break;

            case VT_I8 | VT_BYREF:                                  // Pointer to 64 bit int
                stream << *var.pllVal;
                break;

            case VT_R4:                                             // 32 bit real (float)
                stream << var.fltVal;
                break;

            case VT_R4 | VT_BYREF:                                  // Pointer to 32 bit real (float)
                stream << *var.pfltVal;
                break;

            case VT_R8:                                             // double
                stream << var.dblVal;
                break;

            case VT_R8 | VT_BYREF:                                  // Pointer to double
                stream << *var.pdblVal;
                break;

            case VT_BSTR:                                           // string (BSTR)
                {
    #ifndef UNICODE
                    Char    str[1024] = {0};

                    WideCharToMultiByte(CP_ACP, 0, var.bstrVal, -1, str, 1024, NULL, NULL);

                    return String(str);
    #else
                    return String(var.bstrVal);
    #endif
                }

            case VT_BSTR | VT_BYREF:                                // Pointer to string (BSTR)
                {
    #ifndef UNICODE
                    Char    str[1024] = {0};

                    WideCharToMultiByte(CP_ACP, 0, *var.pbstrVal, -1, str, 1024, NULL, NULL);

                    return String(str);
    #else
                    return String(*var.pbstrVal);
    #endif
                }

            case VT_BOOL:                                           // BOOL
                if (var.boolVal == 0)
                {
                    return String(TXT("false"));
                }
                else
                {
                    return String(TXT("true"));
                }
                break;

            case VT_BOOL | VT_BYREF:                                // Pointer to BOOL
                if (*var.pboolVal == 0)
                {
                    return String(TXT("false"));
                }
                else
                {
                    return String(TXT("true"));
                }
                break;
        }

        return stream.str();
    }

    /*! Converts variant to 64 bit unsigned integer. Floating point values are not rounded, just integer part is returned. For string
     *  variants it will try to convert it using _atoi64()
     *
     *  \param var Variant to convert.
     *  \return Integer representation of value in variant. 0 if variant was empty or contained undefined value or value with unsupported type or
     *          error occured during conversion from string variants.
     *
     *  \note Function does not support date/time/array variants.
     */
    unsigned __int64 VariantToUInt(const VARIANT &var)
    {
        unsigned __int64 result = 0;

        switch (var.vt)
        {
            case VT_NULL:                                           // No data
            case VT_EMPTY: 
            case VT_EMPTY | VT_BYREF:                               // Empty reference = invalid data
                break;

            case VT_UI1:                                            // char
                result = (unsigned __int64) var.bVal;
                break;

            case VT_UI1 | VT_BYREF:                                 // Reference to char
                result = (unsigned __int64) *var.pbVal;
                break;

            case VT_UI2:                                            // 16 bit unsigned int
                result = (unsigned __int64) var.uiVal;
                break;

            case VT_UI2 | VT_BYREF:                                 // Reference to 16 bit unsigned int
                result = (unsigned __int64) *var.puiVal;
                break;

            case VT_UI4:                                            // 32 bit unsigned int
                result = (unsigned __int64) var.ulVal;
                break;

            case VT_UI4 | VT_BYREF:                                 // Reference to 32 bit unsigned int
                result = (unsigned __int64) *var.pulVal;
                break;

            case VT_UI8:                                            // 64 bit unsigned int
                result = (unsigned __int64) var.ullVal;
                break;

            case VT_UI8 | VT_BYREF:                                 // Reference to 64 bit unsigned int
                result = (unsigned __int64) *var.pullVal;
                break;

            case VT_UINT:                                           // Unsigned int
                result = (unsigned __int64) var.uintVal;
                break;

            case VT_UINT | VT_BYREF:                                // Reference to unsigned int
                result = (unsigned __int64) *var.puintVal;
                break;

            case VT_INT:                                            // int
                result = (unsigned __int64) var.intVal;
                break;

            case VT_INT | VT_BYREF:                                 // Reference to int
                result = (unsigned __int64) *var.puintVal;
                break;

            case VT_I1:                                             // char
                result = (unsigned __int64) var.cVal;
                break;

            case VT_I1 | VT_BYREF:                                  // reference to char
                result = (unsigned __int64) *var.pcVal;
                break;

            case VT_I2:                                             // 16 bit int
                result = (unsigned __int64) var.iVal;
                break;

            case VT_I2 | VT_BYREF:                                  // Pointer to 16 bit int
                result = (unsigned __int64) *var.piVal;
                break;

            case VT_I4:                                             // 32 bit int
                result = (unsigned __int64) var.lVal;
                break;

            case VT_I4 | VT_BYREF:                                  // Pointer to 32 bit int
                result = (unsigned __int64) *var.plVal;
                break;

            case VT_I8:                                             // 64 bit int
                result = (unsigned __int64) var.llVal;
                break;

            case VT_I8 | VT_BYREF:                                  // Pointer to 64 bit int
                result = (unsigned __int64) *var.pllVal;
                break;

            case VT_R4:                                             // 32 bit real (float)
                result = (unsigned __int64) var.fltVal;
                break;

            case VT_R4 | VT_BYREF:                                  // Pointer to 32 bit real (float)
                result = (unsigned __int64) *var.pfltVal;
                break;

            case VT_R8:                                             // double
                result = (unsigned __int64) var.dblVal;
                break;

            case VT_R8 | VT_BYREF:                                  // Pointer to double
                result = (unsigned __int64) *var.pdblVal;
                break;

            case VT_BSTR:                                           // string (BSTR)
    #ifndef UNICODE
                {
                    Char    str[64] = {0};
                                
                    WideCharToMultiByte(CP_ACP, 0, var.bstrVal, -1, str, 64, NULL, NULL);

                    if (str[0] != 0)
                    {
                        result = _atoi64(str);
                    }
                }
    #else
                {
                    StringStream stream;

                    stream << var.bstrVal;

                    stream >> result;
                }
    #endif
                break;

            case VT_BSTR | VT_BYREF:                                // Pointer to string (BSTR)
    #ifndef UNICODE
                {
                    Char    str[64] = {0};
                                
                    WideCharToMultiByte(CP_ACP, 0, *var.pbstrVal, -1, str, 64, NULL, NULL);

                    if (str[0] != 0)
                    {
                        result = _atoi64(str);
                    }
                }
    #else
                {
                    StringStream stream;

                    stream << *var.pbstrVal;

                    stream >> result;
                }
    #endif
                break;

            case VT_BOOL:                                           // BOOL
                result = (unsigned __int64) var.boolVal;
                break;

            case VT_BOOL | VT_BYREF:                                // Pointer to BOOL
                result = (unsigned __int64) *var.pboolVal;
                break;
        }

        return result;
    }

    /*! Get value of property with given name and convert it to string. All types values stored in VARIANTs can be
     *  converted to string.
     *
     *  \param object Object from which value should be retrieved.
     *  \param property_name Name of property.
     *  \return CheckedResult class containing requested value converted to string and report about operation success/failure.
     */
    CheckedResult< String > GetValueAsString(IWbemClassObject *object, const LPCWSTR property_name)
    {
        if (object == NULL)
        {
            return CheckedResult< String >(false, TXT(""));
        }

        VARIANT value;
        bool    success = false;
        String  ret_val(TXT(""));

        VariantInit(&value);

        HRESULT hres = object->Get(property_name, 0, &value, NULL, NULL);

        if (SUCCEEDED(hres))
        {
            ret_val = VariantToString(value);
            success = true;
        }

        VariantClear(&value);

        return CheckedResult< String >(success, ret_val);
    }

    /*! Get value of property with given name and convert it to integer. This method will attempt to convert all value types to int,
     *  but for strings it should not be used.
     *
     *  \param object Object from which property value should be retrieved.
     *  \param property_name Name of property.
     *  \return CheckedResult class containing requested value converted to unsigned 64 bit integer and report about operation success/failure.
     */
    CheckedResult< unsigned __int64 > GetValueAsUInt(IWbemClassObject *object, const LPCWSTR property_name)
    {
        if (object == NULL)
        {
            return CheckedResult< unsigned __int64 >(false, 0);
        }

        unsigned __int64 ret_val(0);
        VARIANT          value;
        bool             success = false;

        VariantInit(&value);

        HRESULT hres = object->Get(property_name, 0, &value, NULL, NULL);

        if (SUCCEEDED(hres))
        {
            ret_val = VariantToUInt(value);
            success = true;
        }

        VariantClear(&value);

        return CheckedResult< unsigned __int64 >(success, ret_val);
    }

} // namespace hwdet
