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

/*! \file LinuxAdvancedFeatures.h
 *  \author Branislav Repcek
 *  \date 2. 2. 2006
 *
 *  \brief Header file for LinuxAdvancedFeatures class.
 */ 

#ifndef LINUX_ADVANCED_FEATURES_INCLUDED
#define LINUX_ADVANCED_FEATURES_INCLUDED

#include "../common/UnicodeString.h"
#include "../common/AdvancedFeatures.h"
#include "../common/XMLFileOutput.h"

namespace hwdet
{

    /*! \brief Storage for Linux specific info.
     */
    class LinuxAdvancedFeatures : public AdvancedFeatures
    {
    public:
        //! Default ctor.
        LinuxAdvancedFeatures(void);
        
        //! Constructor.
        LinuxAdvancedFeatures(const String &Distribution, const String &DistroVersion,
            const String &Release, const String &Version, const String &Kernel);

        //! Get name of the distribution.
        String GetDistributionName(void) const;

        //! Get release string.
        String GetRelease(void) const;

        //! Get version string.
        String GetVersion(void) const;
        
        //! Get kernel version string.
        String GetKernelVersion(void) const;

        //! Get distribution version string.
        String GetDistributionVersion(void) const;

    private:
        //! Kernel implementation.
        XMLValueElement< String > *distribution;
        
        //! Kernel release.
        XMLValueElement< String > *release;
        
        //! Kernel version string (long format).
        XMLValueElement< String > *version;
        
        //! Target machine name.
        XMLValueElement< String > *distro_version;
        
        //! Kernel version string (parsed).
        XMLValueElement< String > *kernel_version;
    };
}

#endif
