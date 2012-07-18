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

/*! \file Distribution.h
 *  \author Branislav Repcek
 *  \date 14. 12. 2006
 *  
 *  \brief Distribution class.
 */

#ifndef DISTRIBUTION_INCLUDED
#define DISTRIBUTION_INCLUDED

#include <stdio.h>
#include <sys/utsname.h>
#include "../common/UnicodeString.h"
#include "../common/XMLFileOutput.h"
#include "../common/MessageReporter.h"
#include "Packager.h"
#include "DefaultPackager.h"

/*! Set this to non-zero value to enable automatic destruction of the packagers registered
 *  by the user. Set this to zero if you use custom allocator or if you want to keep
 *  packagers after the distribution class is destroyed.
 */
#define AUTO_FREE_PACKAGERS 1

/*! \brief Calculate kernel version code from version numbers supplied by the <code>uname</code>.
 */
#define KERNEL_VERSION_CODE(major, minor, release) (0x10000 * (major) + 0x100 * (minor) + (release))

namespace hwdet
{
    /*! \brief Distribution specific code.
     * 
     *  This class handles all distribution specific tasks - that is, OS detection
     *  and detection of installed software.
     */
    class Distribution : public MessageReporter
    {
    public:
        /*! \brief Construtor.
         * 
         *  Creates distribution with given name. Name is only informational and
         *  is provided only as a means to differentiate log messages.
         *  This will also call <code>uname</code> and initialize all const fields
         *  with values returned by the call.
         * 
         *  \param distro_name Name used to identify log messages from this class 
         *         (this does not need to be real name of the distribution).
         *  \param parent_reporter Reporter that will receive messages reported by this class.
         *         Set to NULL to disable message dispatch (messages will be written directly
         *         to the stdout).
         */
        Distribution(const String &distro_name, MessageReporter *parent_reporter) :
        MessageReporter(distro_name, parent_reporter),
        kernel_major(0),
        kernel_minor(0),
        kernel_release(0),
        kernel_version_code(0),
        uts_name(TXT("(unknown)")),
        uts_release(TXT("(unknown)")),
        uts_version(TXT("(unknown)")),
        uts_machine(TXT("(unknown)")),
        kernel_version_parsed(TXT("(unknown)")),
        default_packager(new DefaultPackager(this))
        {
            utsname uts;
        
            if (uname(&uts) == -1)
            {
                // Call failed -> exit.
                return;
            }
        
            if (sscanf(uts.release, "%d.%d.%d", &kernel_major, &kernel_minor, &kernel_release) < 3)
            {
                // Unable to parse all three fields.
                kernel_major = kernel_minor = kernel_release = kernel_version_code = 0;
            }
            else
            {
                kernel_version_code = KERNEL_VERSION_CODE(kernel_major, kernel_minor, kernel_release);
                uts_name = String(uts.sysname);
                uts_release = String(uts.release);
                uts_version = String(uts.version);
                uts_machine = String(uts.machine);
                
                StringStream stream;
                stream << kernel_major << TXT(".") << kernel_minor << TXT(".") << kernel_release;
                
                kernel_version_parsed = stream.str();
            }
        }
        
        /*! \brief Free all used resources.
         * 
         *  This will automatically free all packagers registered by the user.
         */
        virtual ~Distribution(void)
        {
#if AUTO_FREE_PACKAGERS != 0
            for (std::vector< Packager * >::iterator it = packagers.begin(); 
                 it != packagers.end();
                 ++it)
            {
                delete *it;
            }
#endif
            delete default_packager;
        }
        
        /*! \brief Get name of the distribution.
         * 
         *  \return Name which is used to identify log messages produced by this class.
         *          This is not neccessarily name of the actual Linux distro.
         */
        String GetName(void) const
        {
            return GetName();
        }
        
        /*! \brief Get fitness value for current distro.
         * 
         *  Fitness value is floating-point number in the closed interval [0, 1] which
         *  determines how suitable is this class for current operating system. Value of
         *  zero means that current OS is not supported by this class at all. Value of
         *  one means that current OS is fully supported by the class.
         *  You should return 1 only if you are also able to query applications installed
         *  on the current system (that is, you have at least one non-default packager
         *  that can be used on surrent computer set-up).
         *  Fitness value is used to pick most suitable distribution handler from the
         *  distribution registry (see DistributionRegistry class).
         * 
         *  \return Real number from the closed interval [0, 1] which determines how
         *          suitable is this class for current set-up.
         */
        virtual float GetFitness(void) = 0;
        
        /*! \brief Detect properties of the installed OS.
         * 
         *  This method detects properties of the operating system currently running and
         *  stores them in the sub-node of given XML file node.
         * 
         *  \param root_node Root node of the XML file. You should create new OperatingSystem
         *         subnode which contains all data you have collected about the OS.
         * 
         *  \return true on success, false otherwise.
         * 
         *  \note Use ReportMessage methods to write all messages. Do not use cout/cerr to
         *        write to the standard/error outputs.
         */
        virtual bool DetectOS(XMLElement *root_node) = 0;
        
        /*! \brief Query installed software.
         * 
         *  This method will query all software installed on the system using appropriate
         *  packager and store all data in the given XML file node.
         *  Suitable packager handler is chosen with the GetPackager method.
         * 
         *  \param products_node Node which will receive all application data.
         * 
         *  \return true on success, false otherwise.
         */
        virtual bool DetectSoftware(XMLElement *products_node)
        {
            if (!products_node)
            {
                ReportMessage("Invalid products node.");
                return false;
            }
            
            Packager *current_packager = GetPackager();
            
            return current_packager->DetectSoftware(products_node);
        }
        
        /*! \brief Add new packager.
         * 
         *  This will register new packager that can be used to query software installed
         *  on the computer. You should always register packagers for given distribution
         *  so that the official one is the first one registered and optional packagers
         *  come later.
         * 
         *  \param packager Packager to add to the list of the packagers.
         */
        void AddPackager(Packager *packager)
        {
            if (packager)
            {
                packagers.push_back(packager);
            }
        }
                
        /*! \brief Find suitable packager.
         * 
         *  This will search list of packagers and pick the first one that is suitable for
         *  current system set-up. Packager is suitable is its method IsSupported returns
         *  true. Packagers are searched in the order they were registered, therefore you
         *  can "prioritize" certain packager by registering as the first one.
         * 
         *  \return Pointer to the packager that is suitable for current set-up. 
         *          If no such packager is found, default one will be returned 
         *          (see DefaultPackager class). You must not free returned pointer by
         *          yourself!
         */
        Packager *GetPackager(void)
        {
            // Find first match.
            for (std::vector< Packager * >::iterator it = packagers.begin();
                 it != packagers.end();
                 ++it)
            {
                if ((*it)->IsSupported())
                {
                    return *it;
                }
            }
            
            // No match found, return default packager.
            return default_packager;
        }
        
    protected:
        //! Major version of the kernel.
        int kernel_major;
        
        //! Minor version of the kenrle.
        int kernel_minor;
        
        //! Releae number of the kernel.
        int kernel_release;
        
        //! Kernel version code.
        int kernel_version_code;
        
        //! Name of the system as determined by <code>uname</code>.
        String uts_name;
        
        //! Release of the system as determined by <code>uname</code>.
        String uts_release;
        
        //! Version of the system as determined by <code>uname</code>.
        String uts_version;
        
        //! Machine type as determined by <code>uname</code>.
        String uts_machine;
        
        //! Parsed version of the kernel.
        String kernel_version_parsed;
        
    private:
        //! List of currently registered packagers.
        std::vector< Packager * > packagers;
        
        //! Default packager.
        DefaultPackager           *default_packager;
    };
}

#endif
