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

/*! \file RPMPackager.h
 *  \author Branislav Repcek
 *  \date 15. 12. 2006
 *
 *  \brief Handler for RPM package manager.
 */

#ifndef RPM_PACKAGER_INCLUDED
#define RPM_PACKAGER_INCLUDED

#include <stdio.h>
#include "../common/MessageReporter.h"
#include "../common/XMLFileOutput.h"
#include "Packager.h"
#include "ForkAndPipe.h"

namespace hwdet
{
    /*! \brief Adapter for the RPM command.
     * 
     *  This class provides means to query applications installed via the RPM package manager.
     */
    class RPMPackager : public Packager
    {
    public:
        //! Create new packager.
        RPMPackager(MessageReporter *parent_reporter);
        
        //! Free used resources.
        virtual ~RPMPackager(void);
        
        //! Is packager supported?
        virtual bool IsSupported(void);
        
        //! Query data from package manager.
        virtual bool DetectSoftware(XMLElement *products);
        
    private:
        
        /*! \brief Execs RPM command and parses its output.
         */
        class RPMExec : public ForkAndPipe
        {
        public:
            //! Constructor.
            RPMExec(MessageReporter *parent_reporter, XMLElement *output);
            
            //! Destructor.
            virtual ~RPMExec(void);
            
            //! Parent process (parse data from RPM).
            virtual void ParentProcess(pid_t child_pid, FILE *file);
            
            //! Child process (exec RPM).
            virtual void ChildProcess(void);
            
        private:
            //! Products node which will receive data.
            XMLElement *output_node;
        };
        
        //! Is packager supported?
        bool is_supported;
    };
}

#endif
