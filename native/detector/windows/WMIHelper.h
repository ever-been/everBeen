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

/*! \file WMIHelper.h
 *  \author Branislav Repcek
 *  \date 10. 12. 2005
 *
 *  \brief Header file with declarations of functions which simplify work with WMI.
 */

#ifndef WMI_HELPER_INCLUDED
#define WMI_HELPER_INCLUDED

#include <windows.h>
#include <comdef.h>
#include <wbemidl.h>
#include "../Common/UnicodeString.h"
#include "../Common/CheckedResult.h"

namespace hwdet 
{
    //! Convert variant to string
    String VariantToString(const VARIANT &var);

    //! Convert variant to 64-bit unsigned int
    unsigned __int64 VariantToUInt(const VARIANT &var);

    //! Retrieve value as string.
    CheckedResult< String > GetValueAsString(IWbemClassObject *object, const LPCWSTR property_name);

    //! Retrieve value as integer.
    CheckedResult< unsigned __int64 > GetValueAsUInt(IWbemClassObject *object, const LPCWSTR property_name);
}

#endif
