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

/*! \file LinuxAdvancedFeatures.cpp
 *  \author Branislav Repcek
 *  \date 2. 2. 2006
 *
 *  \brief Implementation of LinuxAdvancedFeatures class.
 */

#include "../common/UnicodeString.h"
#include "../common/AdvancedFeatures.h"
#include "../common/XMLFileOutput.h"
#include "LinuxAdvancedFeatures.h"

using namespace std;

namespace hwdet
{
    /*!
     */
    LinuxAdvancedFeatures::LinuxAdvancedFeatures(void) :
    AdvancedFeatures()
    {
        AddSubNode((distribution = new XMLValueElement< String >(TXT("distribution"), TXT("unknown"))));
        AddSubNode((distro_version = new XMLValueElement< String >(TXT("distroVersion"), TXT("unknown"))));
        AddSubNode((release = new XMLValueElement< String >(TXT("release"), TXT("unknown"))));
        AddSubNode((version = new XMLValueElement< String >(TXT("version"), TXT("unknown"))));
        AddSubNode((kernel_version = new XMLValueElement< String >(TXT("kernelVersion"), TXT("unknown"))));
    }
    
    /*! \param Distribution Implementation string.
     *  \param DistroVersion Distribution version.
     *  \param Release OS release.
     *  \param Version OS version.
     *  \param Kernel Parsed kernel version (major.minor.release).
     */
    LinuxAdvancedFeatures::LinuxAdvancedFeatures(const String &Distribution, const String &DistroVersion,
            const String &Release, const String &Version, const String &Kernel) :
    AdvancedFeatures()
    {
        AddSubNode((distribution = new XMLValueElement< String >(TXT("distribution"), Distribution)));
        AddSubNode((distro_version = new XMLValueElement< String >(TXT("distroVersion"), DistroVersion)));
        AddSubNode((release = new XMLValueElement< String >(TXT("release"), Release)));
        AddSubNode((version = new XMLValueElement< String >(TXT("version"), Version)));
        AddSubNode((kernel_version = new XMLValueElement< String >(TXT("kernelVersion"), Kernel)));
    }

    /*! \return Distribution name string.
     */
    String LinuxAdvancedFeatures::GetDistributionName(void) const
    {
        return distribution->GetNodeValue();
    }

    /*! \return Release string.
     */
    String LinuxAdvancedFeatures::GetRelease(void) const 
    {
        return release->GetNodeValue();
    }

    /*! \return Kernel version string.
     */
    String LinuxAdvancedFeatures::GetVersion(void) const 
    {
        return version->GetNodeValue();
    }

    /*! \return Parsed kernel version.
     */
    String LinuxAdvancedFeatures::GetKernelVersion(void) const
    {
        return kernel_version->GetNodeValue();
    }
    
    /*! \return Distribution version string.
     */
    String LinuxAdvancedFeatures::GetDistributionVersion(void) const
    {
        return distro_version->GetNodeValue();
    }
} // namespace hwdet
