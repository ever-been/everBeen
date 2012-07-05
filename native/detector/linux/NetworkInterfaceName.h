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

/*! \file NetworkInterfaceName.h
 *  \author Branislav Repcek
 *  \date 12. 12. 2006
 * 
 *  \brief Translate HW id of the iface to the name.
 */

#ifndef NETWORK_INTERFACE_NAME_INCLUDED
#define NETWORK_INTERFACE_NAME_INCLUDED

#include "../common/UnicodeString.h"

namespace hwdet
{
    //! Get name of the interface type by its HW ID.
    String GetInterfaceTypeByID(unsigned short id);
}

#endif
