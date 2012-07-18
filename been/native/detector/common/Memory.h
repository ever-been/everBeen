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

/*! \file Memory.h
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Header file for Memory class.
 */

#ifndef MEMORY_INCLUDED
#define MEMORY_INCLUDED

#include "XMLFileOutput.h"

namespace hwdet
{
    /*! \brief Storage class for all memory related data.
     */
    class Memory : public XMLElement
    {
    public:
        //! Default ctor.
        Memory();
        
        //! Constructor.
        Memory(unsigned long long physical_mem_size, unsigned long long virtual_mem_size, 
               unsigned long long swap_size, unsigned long long page_file_size);

        //! Get size of RAM.
        unsigned long long GetPhysicalMemorySize(void) const;

        //! Set RAM size.
        void SetPhysicalMemorySize(unsigned long long value);
        
        //! Get size of swap space.
        unsigned long long GetSwapSize(void) const;

        //! Set size of the swap space.
        void SetSwapSize(unsigned long long value);
        
        //! Get size of virtual memory.
        unsigned long long GetVirtualMemorySize(void) const;

        //! Set size of the virtual memory.
        void SetVirtualMemorySize(unsigned long long value);
        
        //! Get size of paging file(s).
        unsigned long long GetPageFileSize(void) const;
        
        //! Set size of the page file.
        void SetPageFileSize(unsigned long long value);

    private:

        //! Size of physical memory.
        XMLValueElement< unsigned long long > *physical_memory;

        //! Size of virtual memory.
        XMLValueElement< unsigned long long > *virtual_memory;

        //! Swap size.
        XMLValueElement< unsigned long long > *swap;

        //! Page file size.
        XMLValueElement< unsigned long long > *pagefile;
    };
}

#endif
