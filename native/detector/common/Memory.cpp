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

/*! \file Memory.cpp
 *  \author Branislav Repcek
 *  \date 4. 12. 2005
 *
 *  \brief Implementation of Memory class.
 */

#include "Memory.h"
#include "UnicodeString.h"

using namespace std;

namespace hwdet
{
    /*!
     */
    Memory::Memory(void) :
    XMLElement(TXT("memory"))
    {
        AddSubNode((physical_memory = new XMLValueElement< unsigned long long >(TXT("physicalMemorySize"), 0)));
        AddSubNode((virtual_memory = new XMLValueElement< unsigned long long >(TXT("virtualMemorySize"), 0)));
        AddSubNode((swap = new XMLValueElement< unsigned long long >(TXT("swapSize"), 0)));
        AddSubNode((pagefile = new XMLValueElement< unsigned long long >(TXT("pagingFileSize"), 0)));
    }

    /*! \param physical_mem_size Size of physical memory in bytes.
     *   \param virtual_mem_size Size of virtual memory in bytes.
     *  \param swap_size Size of swap space in bytes.
     *  \param page_file_size Size of paging file(s) in bytes.
     */
    Memory::Memory(unsigned long long physical_mem_size, unsigned long long virtual_mem_size, 
                unsigned long long swap_size, unsigned long long page_file_size) :
    XMLElement(TXT("memory"))
    {
        AddSubNode((physical_memory = new XMLValueElement< unsigned long long >(TXT("physicalMemorySize"), physical_mem_size)));
        AddSubNode((virtual_memory = new XMLValueElement< unsigned long long >(TXT("virtualMemorySize"), virtual_mem_size)));
        AddSubNode((swap = new XMLValueElement< unsigned long long >(TXT("swapSize"), swap_size)));
        AddSubNode((pagefile = new XMLValueElement< unsigned long long >(TXT("pagingFileSize"), page_file_size)));
    }
    
    /*! \return Size of RAM in bytes.
     */
    unsigned long long Memory::GetPhysicalMemorySize(void) const
    {
        return physical_memory->GetNodeValue();
    }
    
    /*! \param value New size of the RAM in bytes.
     */
    void Memory::SetPhysicalMemorySize(unsigned long long value)
    {
        physical_memory->SetNodeValue(value);
    }
    
    /*! \return Size of swap file in bytes.
     */
    unsigned long long Memory::GetSwapSize(void) const
    {
        return swap->GetNodeValue();
    }
    
    /*! \param value New size of the swap space in bytes.
     */
    void Memory::SetSwapSize(unsigned long long value)
    {
        swap->SetNodeValue(value);
    }
    
    /*! \return Size of virtual memory in bytes.
     */
    unsigned long long Memory::GetVirtualMemorySize(void) const
    {
        return virtual_memory->GetNodeValue();
    }
    
    /*! \param value New size of the virtual memory in bytes.
     */
    void Memory::SetVirtualMemorySize(unsigned long long value)
    {
        virtual_memory->SetNodeValue(value);
    }
    
    /*! \return Size of paging file in bytes.
     */
    unsigned long long Memory::GetPageFileSize(void) const
    {
        return pagefile->GetNodeValue();
    }

    /*! \param value New size of the paging files in bytes.
     */
    void Memory::SetPageFileSize(unsigned long long value)
    {
        pagefile->SetNodeValue(value);
    }
} // namespace hwdet
