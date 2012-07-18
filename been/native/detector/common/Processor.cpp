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

/*! \file Processor.cpp
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 * 
 *  \brief Implementation of Processor class.
 */

#include "UnicodeString.h"
#include "Processor.h"

using namespace std;

namespace hwdet
{
    /*!
     */
    Processor::Processor(void) :
    XMLElement(TXT("processor"))
    {
        AddSubNode((model = new XMLValueElement< String >(TXT("model"), TXT("(unknown)"))));
        AddSubNode((vendor = new XMLValueElement< String >(TXT("vendor"), TXT("(unknown)"))));
        AddSubNode((speed = new XMLValueElement< unsigned long >(TXT("speed"), 0)));
        AddSubNode((cache = new XMLValueElement< unsigned long >(TXT("l2CacheSize"), 0)));
    }
    
    /*! \param model_name Model name of the CPU.
     *  \param vendor_name Vendor identification string (12 characters).
     *  \param cache_size Size of the L2 cache in kB.
     *  \param cpu_speed Speed of the processor in MHz.
     */
    Processor::Processor(const String &model_name, const String &vendor_name, unsigned long cache_size, unsigned long cpu_speed) :
    XMLElement(TXT("processor"))
    {
        AddSubNode((model = new XMLValueElement< String >(TXT("model"), model_name)));
        AddSubNode((vendor = new XMLValueElement< String >(TXT("vendor"), vendor_name)));
        AddSubNode((speed = new XMLValueElement< unsigned long >(TXT("speed"), cpu_speed)));
        AddSubNode((cache = new XMLValueElement< unsigned long >(TXT("l2CacheSize"), cache_size)));
    }
    
    /*! \return Model identification string. It is the string which is displayed during the POST of the computer.
     */
    String Processor::GetModelName(void) const
    {
        return model->GetNodeValue();
    }
    
    /*! \param value New model name of the processor.
     */
    void Processor::SetModelName(const String &value)
    {
        model->SetNodeValue(value);
    }
    
    /*! \return Identification string of the CPU vendor. This string is always 12 characters long.
     */
    String Processor::GetVendorName(void) const
    {
        return vendor->GetNodeValue();
    }
    
    /*! \param value New name of the CPU vendor.
     */
    void Processor::SetVendorName(const String &value)
    {
        vendor->SetNodeValue(value);
    }
    
    /*! \return Size of the integrated L2 cache in kB.
     */
    unsigned long Processor::GetCacheSize(void) const
    {
        return cache->GetNodeValue();
    }
    
    /*! \param value New size of the cache in kB.
     */
    void Processor::SetCacheSize(unsigned long value)
    {
        cache->SetNodeValue(value);
    }
    
    /*! \return Speed of the CPU in MHz.
     */
    unsigned long Processor::GetSpeed(void) const
    {
        return speed->GetNodeValue();
    }
    
    /*! \param value New speed of the processor in MHz.
     */
    void Processor::SetSpeed(unsigned long value)
    {
        speed->SetNodeValue(value);
    }

} // namespace hwdet
