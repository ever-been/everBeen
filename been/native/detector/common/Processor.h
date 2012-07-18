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

/*! \file Processor.h
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Header file for Processor class.
 */

#ifndef PROCESSOR_INCLUDED
#define PROCESSOR_INCLUDED

#include "XMLFileOutput.h"
#include "UnicodeString.h"

namespace hwdet
{

    /*! \brief Storage class for all CPU related data.
     */
    class Processor : public XMLElement
    {
    public:
        //! Default ctor.
        Processor(void);
        
        //! Constructor.
        Processor(const String &model_name, const String &vendor_name, unsigned long cache_size, unsigned long cpu_speed);

        //! Get model name of CPU.
        String GetModelName(void) const;
        
        //! Set new model name.
        void SetModelName(const String &value);

        //! Get ID of the vendor.
        String GetVendorName(void) const;

        //! Set new name of the vendor.
        void SetVendorName(const String &value);
        
        //! Get size of the L2 cache in kB.
        unsigned long GetCacheSize(void) const;

        //! Set new cache size.
        void SetCacheSize(unsigned long value);
        
        //! Get speed of the CPU in MHz.
        unsigned long GetSpeed(void) const;
        
        //! Set new speed.
        void SetSpeed(unsigned long value);

    private:

        //! Model name.
        XMLValueElement< String >        *model;

        //! Vendor ID string.
        XMLValueElement< String >        *vendor;

        //! Speed in MHz.
        XMLValueElement< unsigned long > *speed;

        //! L2 cache size.
        XMLValueElement< unsigned long > *cache;
    };
}

#endif
