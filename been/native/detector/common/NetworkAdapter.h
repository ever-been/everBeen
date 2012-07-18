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

/*! \file NetworkAdapter.h
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Header file for NetworkAdapter class.
 */

#ifndef NETWORK_ADAPTER_INCLUDED
#define NETWORK_ADAPTER_INCLUDED

#include "XMLFileOutput.h"
#include "UnicodeString.h"

namespace hwdet
{
    /*! \brief Storage for all network related data.
     */
    class NetworkAdapter : public XMLElement
    {
    public:
        //! Constructor.
        NetworkAdapter(const String &adapter_name, const String &vendor_name, 
                       const String &mac_addr, const String &adapter_type);

        //! Get adapter's name.
        String GetName(void) const;

        //! Get vendor name.
        String GetVendor(void) const;

        //! Get MAC address.
        String GetMACAddress(void) const;

        //! Get type of the adapter.
        String GetType(void) const;

    private:

        //! Adapter name.
        XMLValueElement< String > *adap_name;

        //! Adapter vendor.
        XMLValueElement< String > *vendor;

        //! Adapter type.
        XMLValueElement< String > *type;

        //! MAC address.
        XMLValueElement< String > *mac;
    };
}

#endif
