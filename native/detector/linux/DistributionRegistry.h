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

/*! \file DistributionRegistry.h
 *  \author Branislav Repcek
 *  \date 14. 12. 2006
 *  
 *  \brief Distribution registry.
 */

#ifndef DISTRIBUTION_REGISTRY_INCLUDED
#define DISTRIBUTION_REGISTRY_INCLUDED

#include <vector>
#include "Distribution.h"
#include "DefaultDistribution.h"

/*! Set this to non-zero value to enable automatic destruction of the distributions registered.
 *  Set this to zero if you use custom allocator or if you want to keep distribution after 
 *  the registry class is destroyed.
 */
#define AUTO_FREE_DISTRIBUTIONS 1

namespace hwdet
{
    /*! \brief Manages list of all distribution specific detector classes.
     * 
     *  This class maintains registry of all user-defined handlers for specific distributions
     *  of Linux. It can select handler that is most suitable for current operating system.
     *  Note that handler registration is not automatic and you have to register handlers
     *  via the Register method.
     */
    class DistributionRegistry
    {
    public:
        /*! \brief Create new registry.
         * 
         *  \param parent_reporter Reported used by the DefaultDistribution class
         *         to dispatch messages.
         */
        DistributionRegistry(MessageReporter *parent_reporter) :
        default_distro(new DefaultDistribution(parent_reporter))
        {
        }
        
        //! Destroy registry.
        ~DistributionRegistry(void)
        {
#if AUTO_FREE_DISTRIBUTIONS != 0
            // Destroy all registered distros.
            for (std::vector< Distribution * >::iterator it = registry.begin();
                 it != registry.end();
                 ++it)
            {
                delete (*it);
            }
#endif
            delete default_distro;
        }
        
        /*! \brief Register new distro handler.
         * 
         *  \param distro Distribution to register.
         */
        void Register(Distribution *distro)
        {
            if (distro)
            {
                registry.push_back(distro);
            }
        }
        
        /*! \brief Get best handler for current OS.
         * 
         *  This will pick most suitable distribution handler for current system.
         *  Best handler is picked based on the value of its fitness function
         *  (as determined by the Distribution::GetFitness method). Highest fitness is
         *  best. For more detailed description of the fitness function, see documentation
         *  for the Distribution class.
         *  Note that this will always search all registered distros.
         * 
         *  \return Pointer to the most suitable distribution handler. If no suitable handler
         *          is found, instance of the DefaultDistribution is returned. You should
         *          not free returned pointer by yourself!
         */
        Distribution *GetBestFit(void)
        {
            Distribution *current_best = default_distro;
            float        best_fitness = default_distro->GetFitness();
            
            for (std::vector< Distribution * >::iterator it = registry.begin();
                 it != registry.end();
                 ++it)
            {
                // Is this one better than the current best?
                if ((*it)->GetFitness() > best_fitness)
                {
                    // Yep -> update current best
                    best_fitness = (*it)->GetFitness();
                    current_best = *it;
                }
            }
            
            return current_best;
        }
        
    private:
        //! List of registered distribution handlers.
        std::vector< Distribution * > registry;
        
        //! Default distribution.
        DefaultDistribution           *default_distro;
        
        /*! \brief Private copy ctor.
         */
        DistributionRegistry(const DistributionRegistry &) { }
        
        /*! \brief Undefined to prevent copying.
         * 
         *  \return Copy of this (not :).
         */
        DistributionRegistry operator =(const DistributionRegistry &);
    };
}

#endif
