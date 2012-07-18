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

/*! \file GentooDistribution.h
 *  \author Branislav Repcek
 *  \date 15. 12. 2006
 *
 *  \brief Header file for Gentoo handler.
 */ 

#ifndef GENTOO_DISTRIBUTION_INCLUDED
#define GENTOO_DISTRIBUTION_INCLUDED

#include "../common/MessageReporter.h"
#include "../common/XMLFileOutput.h"
#include "Distribution.h"

namespace hwdet
{
    /*! \brief Handler for the Gentoo distro.
     */
    class GentooDistribution : public Distribution
    {
    public:
        //! Create new Gentoo handler.
        GentooDistribution(MessageReporter *parent_reporter);
        
        //! Free resources.
        virtual ~GentooDistribution(void);
        
        //! Get fitness value for current distro.
        virtual float GetFitness(void);
        
        //! Detect properties of the installed OS.
        virtual bool DetectOS(XMLElement *root_node);
        
    private:
        //! Fitness for current OS.
        float  fitness;
        
        //! Full name of the distro.
        String full_name;
        
        //! FC version
        String version;
    };
}

#endif
