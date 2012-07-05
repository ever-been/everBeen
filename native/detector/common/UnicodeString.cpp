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

/*! \file UnicodeString.cpp
 *  \date 26. 4. 2006
 *  \author Branislav Repcek
 *
 *  \brief Source file which simplifies making unicode/non-unicode programs.
 */

#ifdef _WINDOWS
#include <windows.h>
#endif

#include "UnicodeString.h"
#include <iostream>

namespace hwdet
{

#ifdef UNICODE
    std::wostream &Cout = std::wcout;
    std::wostream &Cerr = std::wcerr;
    std::wistream &Cin = std::wcin;
#else
    std::ostream &Cout = std::cout;
    std::ostream &Cerr = std::cerr;
    std::istream &Cin = std::cin;
#endif

#ifdef _WINDOWS
    /*! \param unicode Unicode string to convert.
     *
     *  \return ANSI version of the input Unicode string.
     */
    std::string Win_UnicodeToANSI(std::wstring unicode)
    {
        char *str = new char[unicode.length() + 1];

        ZeroMemory((void *) str, unicode.length() + 1);
        WideCharToMultiByte(CP_ACP, 0, unicode.c_str(), -1, str, (int) unicode.length() + 1, NULL, NULL);

        std::string result(str);

        delete str;

        return result;
    }

    /*! \param ansi ANSI string to convert.
     *
     *  \return Unicode version of the input ANSI string.
     */
    std::wstring Win_ANSIToUnicode(std::string ansi)
    {
        wchar_t *str = new wchar_t[ansi.length() + 1];

        ZeroMemory((void *) str, sizeof(wchar_t) * (ansi.length() + 1));

        MultiByteToWideChar(CP_ACP, MB_PRECOMPOSED, ansi.c_str(), -1, str, (int) ansi.length() + 1);

        std::wstring result(str);

        delete str;

        return result;
    }
#endif

} // namespace hwdet
