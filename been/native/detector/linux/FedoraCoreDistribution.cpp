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

/*! \file FedoraCoreDistribution.cpp
 *  \author Branislav Repcek
 *  \date 15. 12. 2006
 *
 *  \brief Implementation of the Fedora Core distribution handler.
 */

#include <vector>
#include "../common/UnicodeString.h"
#include "../common/MessageReporter.h"
#include "../common/XMLFileOutput.h"
#include "../common/BasicFeatures.h"
#include "../common/OperatingSystem.h"
#include "LinuxAdvancedFeatures.h"
#include "Distribution.h"
#include "FedoraCoreDistribution.h"
#include "RPMPackager.h"
#include "FileParser.h"
#include "SystemHelper.h"

using namespace std;

//! Vendor of the distro.
const hwdet::String VENDOR_NAME = TXT("Red Hat, Inc.");

namespace hwdet
{
    /*! This will also register all packagers available for Fedora. Currently only RPM
     *  handler is available.
     *  Distribution name is set to value of the DISTRIBUTION_NAME.
     * 
     *  \param parent_reporter Message reporter which will receive all messages generated
     *         by this class.
     */
    FedoraCoreDistribution::FedoraCoreDistribution(MessageReporter *parent_reporter) :
    Distribution(TXT("FedoraCoreDistribution"), parent_reporter),
    fitness(0.0f)
    {
        // If we are on the Fedora, there's fedora-release file in the /etc
        if (FileTestPermissions("/etc/fedora-release", PERMISSIONS_READ))
        {
            AddPackager(new RPMPackager(this));
            
            vector< String > lines;
            bool res = ReadLines("/etc/fedora-release", lines);
            
            if (res && (lines.size() >= 1))
            {
                fitness = 1.0f;
                full_name = lines[0];
                
                size_t rel_pos = lines[0].find(TXT("release"));
                
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
    FedoraCoreDistribution::~FedoraCoreDistribution(void)
    {
    }
    
    /*! \return This will return 1.0f if we are running on the Fedora Core, otherwise
     *          it will return 0.0f.
     * 
     *  \sa Distribution::GetFitness(void)
     */
    float FedoraCoreDistribution::GetFitness(void)
    {
        return fitness;
    }
    
    /*! \param root_node Node which will receive details about the OS.
     * 
     *  \return true on success, false otherwise.
     */
    bool FedoraCoreDistribution::DetectOS(XMLElement *root_node)
    {
        ReportMessage("Detecting OS properties.");
            
        BasicFeatures *basic = new BasicFeatures(full_name,
                                                 VENDOR_NAME, 
                                                 uts_machine);
        LinuxAdvancedFeatures *advanced = new LinuxAdvancedFeatures(TXT("Fedora Core"),
                                                                    version,
                                                                    uts_release, 
                                                                    uts_version, 
                                                                    kernel_version_parsed);
            
        root_node->AddSubNode(new OperatingSystem(basic, advanced));
            
        return true;
    }
}
