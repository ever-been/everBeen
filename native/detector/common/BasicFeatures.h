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

/*! \file BasicFeatures.h
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Header file for BasicFeatures class.
 */

#ifndef BASIC_FEATURES_INCLUDED
#define BASIC_FEATURES_INCLUDED

#include "XMLFileOutput.h"
#include "UnicodeString.h"

namespace hwdet
{

    /*! \brief Storage class for common OS features. These features are supported by all detectors.
     */
    class BasicFeatures : public XMLElement
    {
    public:
        //! Default ctor.
        BasicFeatures(void);
        
        //! Constructor.
        BasicFeatures(const String &os_name, const String &os_vendor, const String &os_architecture);

        //! Get system name.
        String GetName(void) const;

        //! Get OS vendor name.
        String GetVendor(void) const;

        //! Get architecture identification.
        String GetArchitecture(void) const;

    private:

        //! Name of system.
        XMLValueElement< String > *name;

        //! OS vendor name.
        XMLValueElement< String > *vendor;

        //! OS architecture identification.
        XMLValueElement< String > *architecture;
    };
}

#endif
