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

/*! \file GentooDistribution.cpp
 *  \author Branislav Repcek
 *  \date 15. 12. 2006
 *
 *  \brief Implementation of the Gentoo distribution handler.
 */

#include <vector>
#include "../common/UnicodeString.h"
#include "../common/MessageReporter.h"
#include "../common/XMLFileOutput.h"
#include "../common/BasicFeatures.h"
#include "../common/OperatingSystem.h"
#include "LinuxAdvancedFeatures.h"
#include "Distribution.h"
#include "GentooDistribution.h"
#include "EqueryPackager.h"
#include "GentooFindPackager.h"
#include "FileParser.h"
#include "SystemHelper.h"

using namespace std;

//! Vendor of the distro.
const hwdet::String VENDOR_NAME = TXT("Gentoo Foundation, Inc.");

namespace hwdet
{
    /*! Initialize adapter for the Gentoo distribution. This also registers package
     *  manager for the Gentoo (equery).
     * 
     *  \param parent_reporter Message reporter which will receive all messages generated
     *         by this class.
     */
    GentooDistribution::GentooDistribution(MessageReporter *parent_reporter) :
    Distribution(TXT("GentooDistribution"), parent_reporter),
    fitness(0.0f)
    {
        if (FileTestPermissions("/etc/gentoo-release", PERMISSIONS_READ))
        {
            // Note: correct way would be to have equery first, but it may take several
            // minutes to execute...
            AddPackager(new GentooFindPackager(this));
            AddPackager(new EqueryPackager(this));
                        
            vector< String > lines;
            bool res = ReadLines("/etc/gentoo-release", lines);
            
            if (res && (lines.size() >= 1))
            {
                fitness = 1.0f;
                full_name = lines[0];
            
                size_t rel_pos = lines[0].find(TXT("version"));
                
                if (rel_pos != lines[0].npos)
                {
                    bool cres = GetFieldValue(lines[0], 2, version, rel_pos);
                    
                    if (!cres)
                    {
                        version = TXT("(unknown)");
                    }
                }
            }
            else
            {
                fitness = 0.0f;
                full_name = uts_name;
            }
        }
    }
    
    /*!
     */
    GentooDistribution::~GentooDistribution(void)
    {
    }
    
    /*! \return This will return 1.0f if we are running on Gentoo, otherwise
     *          it will return 0.0f.
     * 
     *  \sa Distribution::GetFitness(void)
     */
    float GentooDistribution::GetFitness(void)
    {
        return fitness;
    }
    
    /*! \param root_node Node which will receive details about the OS.
     * 
     *  \return true on success, false otherwise.
     */
    bool GentooDistribution::DetectOS(XMLElement *root_node)
    {
        ReportMessage("Detecting OS properties.");
            
        BasicFeatures *basic = new BasicFeatures(full_name,
                                                 VENDOR_NAME, 
                                                 uts_machine);
        LinuxAdvancedFeatures *advanced = new LinuxAdvancedFeatures(TXT("Gentoo"),
                                                                    version,
                                                                    uts_release, 
                                                                    uts_version, 
                                                                    kernel_version_parsed);
            
        root_node->AddSubNode(new OperatingSystem(basic, advanced));
            
        return true;
    }
}
