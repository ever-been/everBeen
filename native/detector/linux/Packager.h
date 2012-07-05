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

/*! \file Packager.h
 *  \author Branislav Repcek
 *  \date 15. 12. 2005
 *
 *  \brief Header for the Packager class.
 */

#ifndef PACKAGER_INCLUDED
#define PACKAGER_INCLUDED

#include "../common/UnicodeString.h"
#include "../common/XMLFileOutput.h"
#include "../common/MessageReporter.h"

namespace hwdet
{
    /*! \brief Base class for all packagers.
     */
    class Packager : public MessageReporter
    {
    public:
        /*! \brief Ctor.
         * 
         *  \param name Name of the packager. This name will be used to identify log messages
         *         and does not need to be same as the real name of tha actual Linux package
         *         manager.
         *  \param parent_reporter Message reporter which will receive all messages generated
         *         by this class.
         */
        Packager(const String &name, MessageReporter *parent_reporter) :
        MessageReporter(name, parent_reporter)
        {
        }
        
        /*! \brief Destructor.
         */
        virtual ~Packager(void)
        {
        }
        
        /*! \brief Is packager supported?
         * 
         *  This should check if the package manager this class is written for is installed
         *  on the system.
         * 
         *  \return true is package manager is present, false otherwise.
         */
        virtual bool IsSupported(void) = 0;
        
        /*! \brief Query data from package manager.
         * 
         *  \return true on success, false otherwise.
         */
        virtual bool DetectSoftware(XMLElement *products) = 0;
    };
}

#endif
