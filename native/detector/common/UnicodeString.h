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

/*! \file UnicodeString.h
 *  \date 26. 4. 2006
 *  \author Branislav Repcek
 *
 *  \brief Header file which simplifies making unicode/non-unicode programs.
 */

#ifndef UNI_STRING_H_INCLUDED
#define UNI_STRING_H_INCLUDED

#include <string>
#include <sstream>
#include <fstream>

namespace hwdet
{

// Test if we are compiling unicode application
#if defined UNICODE && defined _WINDOWS
    // we are compiling as unicode

    //! Unicode string
    typedef ::std::wstring String;

    //! Unicode string stream
    typedef ::std::wstringstream StringStream;

    //! Unicode string input stream
    typedef ::std::wistringstream IStringStream;

    //! Unicode string output stream
    typedef ::std::wostringstream OStringStream;

    //! Unicode file stream
    typedef ::std::wfstream FStream;

    //! Unicode file input stream
    typedef ::std::wifstream IFStream;

    //! Unicode file output stream
    typedef ::std::wofstream OFStream;

    //! Unicode input stream
    typedef ::std::wistream IStream;

    //! Unicode output stream
    typedef ::std::wostream OStream;

    //! Unicode input/output stream
    typedef ::std::wiostream IOStream;

    //! Unicode standard output stream
    extern std::wostream &Cout;

    //! Unicode standard error stream
    extern std::wostream &Cerr;

    //! Unicode standard input stream
    extern std::wistream &Cin;

    //! Unicode character
    typedef wchar_t Char;

    //! Identify text as UNICODE
    #define TXT(text) L##text

#else
    // non-unicode compilation

    //! ASCII string
    typedef ::std::string String;

    //! ASCII string stream
    typedef ::std::stringstream StringStream;

    //! ASCII string input stream
    typedef ::std::istringstream IStringStream;

    //! ASCII string output stream
    typedef ::std::ostringstream OStringStream;

    //! ASCII file stream
    typedef ::std::fstream FStream;

    //! ASCII file input stream
    typedef ::std::ifstream IFStream;

    //! ASCII file output stream
    typedef ::std::ofstream OFStream;

    //! ASCII output stream
    typedef ::std::ostream OStream;

    //! ASCII input stream
    typedef ::std::istream IStream;

    //! ASCII input/output stream
    typedef ::std::iostream IOStream;

    //! ACSII standard output stream
    extern std::ostream &Cout;

    //! ASCII standard input stream
    extern std::ostream &Cerr;

    //! ASCII standard input stream
    extern std::istream &Cin;

    //! ASCII character
    typedef char Char;

    //! Identify text as ASCII
    #define TXT(text) text

#endif

#ifdef _WINDOWS
    // Following methods will work only on Windows.


    //! Convert Unicode string to the ANSI string.
    std::string Win_UnicodeToANSI(std::wstring unicode);

    //! Convert ANSI string to the Unicode.
    std::wstring Win_ANSIToUnicode(std::string ansi);
#endif

} // namespace hwdet

#endif
