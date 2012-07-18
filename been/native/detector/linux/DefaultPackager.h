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

/*! \file DefaultPackager.h
 *  \author Branislav Repcek
 *  \date 15. 12. 2005
 *
 *  \brief Header for the DefaultPackager class.
 */

#ifndef DEFAULT_PACKAGER_INCLUDED
#define DEFAULT_PACKAGER_INCLUDED

#include "../common/MessageReporter.h"
#include "Packager.h"

namespace hwdet
{
    /*! \brief Default packager which does not detect anything.
     */
    class DefaultPackager : public Packager
    {
    public:
        /*! \brief Constructor.
         * 
         *  \param parent_reporter Message reporter to which all messages will be dispatched.
         */
        DefaultPackager(MessageReporter *parent_reporter) :
        Packager(TXT("DefaultPackager"), parent_reporter)
        {
        }
        
        /*! \brief Is packager supported.
         * 
         *  \return Always true since this class does nothing.
         */
        virtual bool IsSupported(void)
        {
            return true;
        }
        
        /*! \brief Detect installed software.
         * 
         *  \return Always true since this does nothing.
         */
        virtual bool DetectSoftware(XMLElement *)
        {
            ReportMessage("Detecting installed software.");
            return true;
        }
    };
}

#endif
