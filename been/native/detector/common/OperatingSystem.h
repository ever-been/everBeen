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

/*! \file OperatingSystem.h
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Header file for OperatingSystem class.
 */

#ifndef OPERATING_SYSTEM_INCLUDED
#define OPERATING_SYSTEM_INCLUDED

#include "XMLFileOutput.h"
#include "UnicodeString.h"

namespace hwdet
{

    class BasicFeatures;
    class AdvancedFeatures;

    /*! \brief This class stores all OS specific data.
     *
     *  OperatingSystem class has two sub-nodes. One represents features detected on all OSes, the other node is different
     *  for different operating systems.
     */
    class OperatingSystem : public XMLElement
    {
    public:
        /*! \brief Constructor.
         *
         *  \param basic Features common to all operating systems.
         *  \param advanced System specific features. These are different for each supported detector.
         */
        OperatingSystem(BasicFeatures *basic, AdvancedFeatures *advanced) : 
        XMLElement(TXT("operatingSystem"))
        {
            AddSubNode((XMLElement *) basic);
            AddSubNode((XMLElement *) advanced);
        }
    };
}

#endif
