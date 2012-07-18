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

/*! \file GentooFindPackager.h
 *  \author Branislav Repcek
 *  \date 16. 12. 2006
 *
 *  \brief Header file for "fake" Gentoo packager.
 */ 

#ifndef GENTOO_FIND_PACKAGER
#define GENTOO_FIND_PACKAGER

#include <stdio.h>
#include "../common/MessageReporter.h"
#include "../common/XMLFileOutput.h"
#include "Packager.h"
#include "ForkAndPipe.h"

namespace hwdet
{
    /*! \brief Fake packager adapter for Gentoo.
     * 
     *  This class executes find and searches for all files in the portage database.
     *  Database is assumed to be in <code>/var/db/pkg</code> directory.
     */
    class GentooFindPackager : public Packager
    {
    public:
        //! Create new packager.
        GentooFindPackager(MessageReporter *parent_reporter);
        
        //! Free used resources.
        virtual ~GentooFindPackager(void);
        
        //! Is packager supported?
        virtual bool IsSupported(void);
        
        //! Query data from package manager.
        virtual bool DetectSoftware(XMLElement *products);
        
    private:
        
        /*! \brief Exec find command and parse its output.
         */
        class FindExec : public ForkAndPipe
        {
        public:
            //! Constructor.
            FindExec(MessageReporter *parent_reporter, XMLElement *output);
            
            //! Destructor.
            virtual ~FindExec(void);
            
            //! Parent process (parse data from Equery).
            virtual void ParentProcess(pid_t child_pid, FILE *file);
            
            //! Child process (exec Equery).
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
