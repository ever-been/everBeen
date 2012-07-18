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

/*! \file WindowsAdvancedFeatures.h
 *  \author Branislav Repcek
 *  \date 5. 12. 2005
 *
 *  \brief Header file for AdvancedFeatures class.
 */

#ifndef WINDOWS_ADVANCED_FEATURES_INCLUDED
#define WINDOWS_ADVANCED_FEATURES_INCLUDED

#include "../Common/UnicodeString.h"
#include "../Common/AdvancedFeatures.h"
#include "../Common/XMLFileOutput.h"

namespace hwdet
{

    /*! \brief Storage for all OS data specific to Windows.
     */
    class WindowsAdvancedFeatures : public AdvancedFeatures
    {
    public:
        //! Constructor.
        WindowsAdvancedFeatures(const String &os_version, const String &build_type, const String &service_pack, 
                                const String &windows_dir, const String &system_dir, unsigned int encryption_level);

        //! Get version of Windows.
        String GetVersion(void) const;

        //! Get build type.
        String GetBuildType(void) const;

        //! Get version of service pack.
        String GetServicePackVersion(void) const;

        //! Get directory path to Windows installation.
        String GetWindowDirectory(void) const;

        //! Get directory path to Windows system directory.
        String GetSystemDirectory(void) const;

        //! Get encryption strength.
        unsigned int GetEncryptionLevel(void) const;

    private:

        //! OS version.
        XMLValueElement< String >       *version;

        //! Build type.
        XMLValueElement< String >       *build;

        //! Service pack version.
        XMLValueElement< String >       *sp_version;

        //! Windows directory.
        XMLValueElement< String >       *win_dir;

        //! System directory.
        XMLValueElement< String >       *sys_dir;

        //! Encryption strength.
        XMLValueElement< unsigned int > *encryption;
    };
}

#endif
