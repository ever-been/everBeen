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

/*! \file NetworkAdapter.cpp
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Implementation of NetworkAdapter class.
 */

#include "NetworkAdapter.h"
#include "UnicodeString.h"

using namespace std;

namespace hwdet
{
    /*! \param adapter_name Name of the network adapter.
     *  \param vendor_name Name of the vendor.
     *  \param mac_addr String with MAC address.
     *  \param adapter_type String with description of adapter type.
     */
    NetworkAdapter::NetworkAdapter(const String &adapter_name, const String &vendor_name, 
                                   const String &mac_addr, const String &adapter_type) :
    XMLElement(TXT("networkAdapter"))
    {
        AddSubNode((adap_name = new XMLValueElement< String >(TXT("name"), adapter_name)));
        AddSubNode((vendor = new XMLValueElement< String >(TXT("vendor"), vendor_name)));
        AddSubNode((type = new XMLValueElement< String >(TXT("adapterType"), adapter_type)));
        AddSubNode((mac = new XMLValueElement< String >(TXT("macAddress"), mac_addr)));
    }
    
    /*! \return String with adapter name.
     */
    String NetworkAdapter::GetName(void) const
    {
        return adap_name->GetNodeValue();
    }
    
    /*! \return String with vendor name.
     */
    String NetworkAdapter::GetVendor(void) const
    {
        return vendor->GetNodeValue();
    }
    
    /*! \return String containing MAC address of the adapter.
     */
    String NetworkAdapter::GetMACAddress(void) const
    {
        return mac->GetNodeValue();
    }
    
    /*! \return Type of the adapter.
     */
    String NetworkAdapter::GetType(void) const
    {
        return type->GetNodeValue();
    }
} // namespace hwdet
