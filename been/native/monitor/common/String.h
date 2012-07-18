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

/*! \file String.h
 *  \author Branislav Repcek
 *  \date 5. 10. 2006
 *
 *  \brief Some typedefs for strings.
 */

#ifndef STRING_INCLUDED
#define STRING_INCLUDED

#include <string>

namespace lm
{
#ifdef WIN32
    //! String.
    typedef ::std::wstring String;

    //! Wide string stream.
    typedef ::std::wstringstream StringStream;

#else
    //! String.
    typedef ::std::string String;

    //! String stream.
    typedef ::std::stringstream StringStream;
#endif
}

#endif
