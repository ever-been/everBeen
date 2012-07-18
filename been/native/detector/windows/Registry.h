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

/*! \file Registry.h
 *  \author Branislav Repcek
 *  \date 25. 4. 2006
 *
 *  \brief Header file with registry access functions.
 */

#ifndef REGISTRY_INCLUDED
#define REGISTRY_INCLUDED

#include <windows.h>
#include <vector>
#include <map>
#include "../Common/UnicodeString.h"

namespace hwdet
{
    //! Open specified registry key.
    bool OpenRegistryKey(const HKEY hkey, const String &keyName, HKEY &result, const REGSAM access = KEY_READ);

    //! Close registry key.
    bool CloseRegistryKey(const HKEY hkey);

    //! Build list of all subkeys of specified key.
    bool EnumerateSubKeys(const HKEY hkey, std::vector< String > &result);

    //! Build list of all values of specified key.
    bool EnumerateValues(const HKEY hkey, std::vector< String > &result);

    //! Get specified value as a string.
    bool GetValueAsString(const HKEY hkey, const String &valueName, String &result);

    //! Build map containing all values of specified key as strings.
    bool GetSubKeyValueMap(const HKEY hkey, const String &subKeyName, std::map< String, String > &result);
}

#endif