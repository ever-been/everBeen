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

/*! \file WindowsAdvancedFeatures.cpp
 *  \author Branislav Repcek
 *  \date 5. 12. 2005
 *
 *  \brief Implementation of AdvancedFeatures class.
 */

#include "../Common/UnicodeString.h"
#include "../Common/AdvancedFeatures.h"
#include "../Common/XMLFileOutput.h"
#include "WindowsAdvancedFeatures.h"

using namespace std;

namespace hwdet
{
    /*! \param os_version Version of Windows OS.
     *  \param build_type Build type identification.
     *  \param service_pack Version of SP installed.
     *  \param windows_dir Path to Windows directory.
     *  \param system_dir Path to system directory/
     *  \param encryption_level Strength of encryption in bits.
     */
    WindowsAdvancedFeatures::WindowsAdvancedFeatures(const String &os_version, const String &build_type, 
        const String &service_pack, const String &windows_dir, const String &system_dir, unsigned int encryption_level) :
    AdvancedFeatures()
    {
        AddSubNode((version = new XMLValueElement< String >(TXT("version"), os_version)));
        AddSubNode((build = new XMLValueElement< String >(TXT("buildType"), build_type)));
        AddSubNode((sp_version = new XMLValueElement< String >(TXT("servicePackVersion"), service_pack)));
        AddSubNode((win_dir = new XMLValueElement< String >(TXT("windowsDirectory"), windows_dir)));
        AddSubNode((sys_dir = new XMLValueElement< String >(TXT("systemDirectory"), system_dir)));
        AddSubNode((encryption = new XMLValueElement< unsigned int >(TXT("encryptionLevel"), encryption_level)));
    }

    /*! \return Version of the Windows. It has following format: major.minor.build.
     */
    String WindowsAdvancedFeatures::GetVersion(void) const
    {
        return version->GetNodeValue();
    }

    /*! \return Build type identification string.
     */
    String WindowsAdvancedFeatures::GetBuildType(void) const
    {
        return build->GetNodeValue();
    }

    /*! \return Version of the service pack installed. It is in form major.minor where major and minor are integers greater or equal than zero.
     */
    String WindowsAdvancedFeatures::GetServicePackVersion(void) const
    {
        return sp_version->GetNodeValue();
    }

    /*! \return Path to Windows installation directory.
     */
    String WindowsAdvancedFeatures::GetWindowDirectory(void) const
    {
        return win_dir->GetNodeValue();
    }

    /*! \return Path to system directory.
     */
    String WindowsAdvancedFeatures::GetSystemDirectory(void) const
    {
        return sys_dir->GetNodeValue();
    }

    /*! \return Enryption strength provided by the OS in bits.
     */
    unsigned int WindowsAdvancedFeatures::GetEncryptionLevel(void) const
    {
        return encryption->GetNodeValue();
    }
} // namespace hwdet
