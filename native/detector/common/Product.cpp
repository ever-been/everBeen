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

/*! \file Product.cpp
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Implementtation of Produc class.
 */

#include "UnicodeString.h"
#include "Product.h"

using namespace std;

namespace hwdet
{
    /*! \param app_name Name of the application/product.
     *  \param vendor_name Name of application's vendor.
     *  \param version_str Version string.
     */
    Product::Product(const String &app_name, const String &vendor_name, const String &version_str) :
    XMLElement(TXT("product"))
    {
        AddSubNode((prod_name = new XMLValueElement< String >(TXT("name"), app_name)));
        AddSubNode((vendor = new XMLValueElement< String >(TXT("vendor"), vendor_name)));
        AddSubNode((version = new XMLValueElement< String >(TXT("version"), version_str)));
    }
    
    /*! \return Name of the product.
     */
    String Product::GetName(void) const
    {
        return prod_name->GetNodeValue();
    }
    
    /*! \return Vendor name.
     */
    String Product::GetVendor(void) const
    {
        return vendor->GetNodeValue();
    }
    
    /*! \return Version string.
     */
    String Product::GetVersion(void) const
    {
        return version->GetNodeValue();
    }
} // namespace hwdet
