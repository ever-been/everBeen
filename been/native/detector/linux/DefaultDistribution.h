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

/*! \file DefaultDistribution.h
 *  \author Branislav Repcek
 *  \date 14. 12. 2005
 *
 *  \brief Header for the DefaultDistribution class.
 */

#ifndef DEFAULT_DISTRIBUTION_INCLUDED
#define DEFAULT_DISTRIBUTION_INCLUDED

#include "../common/UnicodeString.h"
#include "../common/MessageReporter.h"
#include "../common/BasicFeatures.h"
#include "../common/OperatingSystem.h"
#include "LinuxAdvancedFeatures.h"

namespace hwdet
{
    /*! \brief Default distro handler.
     * 
     *  This is generic handler that should work on all distributions. Since it is
     *  generic, it does have a lot of features. Actually, it detects only basic properties
     *  of the system as determined by the <code>uname</code> syscall.
     */
    class DefaultDistribution : public Distribution
    {
    public:
        //! Fitness of the default distribution handler.
        #define DEFAULT_DISTRO_FITNESS 0.3f
        
        /*! \brief Ctor.
         * 
         *  \param parent_reporter Reporter to which all messages are dispatched.
         */
        DefaultDistribution(MessageReporter *parent_reporter) :
        Distribution(TXT("DefaultDistribution"), parent_reporter)
        {
            // No packagers are registered.
        }
        
        /*! \brief Dtor.
         */
        virtual ~DefaultDistribution(void)
        {
        }
        
        /*! \brief Detect properties of the installed OS.
         * 
         *  Since this is default handler, it only parses data returned by the 
         *  <code>uname</code> syscall (which is actually called by the ancestor class
         *  in the constructor).
         * 
         *  \param root_node Root node of the XML file. This will create new OperatingSystem
         *         sub+node which will contain all data collected about the OS.
         * 
         *  \return Always true.
         */
        virtual bool DetectOS(XMLElement *root_node) 
        {
            ReportMessage("Detecting OS properties.");
            
            BasicFeatures *basic = new BasicFeatures(uts_name,
                                                     TXT("unknown"), 
                                                     uts_machine);
            LinuxAdvancedFeatures *advanced = new LinuxAdvancedFeatures(TXT("unknown"),
                                                                        TXT("unknown"),
                                                                        uts_release, 
                                                                        uts_version, 
                                                                        kernel_version_parsed);
            
            root_node->AddSubNode(new OperatingSystem(basic, advanced));
            
            return true;
        }
        
        /*! \return This will return fitness for default handler. This is never used, since
         *          default handler is a "catch-all" class.
         */
        virtual float GetFitness(void)
        {
            return DEFAULT_DISTRO_FITNESS;
        }
    };
}

#endif
