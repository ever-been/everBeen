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

/*! \file BasicFeatures.cpp
 *  \author Branislav Repcek
 *  \date 5. 12. 2005
 *
 *  \brief Implementation of BasicFeatures class.
 */

#include "BasicFeatures.h"
#include "UnicodeString.h"

using namespace std;

namespace hwdet
{
    /*!
     */
    BasicFeatures::BasicFeatures(void) :
    XMLElement(TXT("basicInfo"))
    {
        AddSubNode((name = new XMLValueElement< String >(TXT("name"), TXT("unknown"))));
        AddSubNode((vendor = new XMLValueElement< String >(TXT("vendor"), TXT("unknown"))));
        AddSubNode((architecture = new XMLValueElement< String >(TXT("arch"), TXT("unknown"))));
    }
    
    /*! \param os_name Name of the OS.
     *  \param os_vendor Name of the OS vendor.
     *  \param os_architecture Architecture OS is running on.
     */
    BasicFeatures::BasicFeatures(const String &os_name, const String &os_vendor, const String &os_architecture) :
    XMLElement(TXT("basicInfo"))
    {
        AddSubNode((name = new XMLValueElement< String >(TXT("name"), os_name)));
        AddSubNode((vendor = new XMLValueElement< String >(TXT("vendor"), os_vendor)));
        AddSubNode((architecture = new XMLValueElement< String >(TXT("arch"), os_architecture)));
    }
    
    /*! \return Name of the OS.
     */
    String BasicFeatures::GetName(void) const
    {
        return name->GetNodeValue();
    }
    
    /*! \return OS vendor name.
     */
    String BasicFeatures::GetVendor(void) const
    {
        return vendor->GetNodeValue();
    }
    
    /*! \return OS architecture identification.
     */
    String BasicFeatures::GetArchitecture(void) const
    {
        return architecture->GetNodeValue();
    }

} // namespace hwdet
