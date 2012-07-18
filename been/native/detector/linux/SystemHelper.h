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

/*! \file SystemHelper.h
 *  \author Branislav Repcek
 *  \date 13. 12. 2006
 *  
 *  \brief System helper functions.
 */

#ifndef SYSTEM_HELPER_INCLUDED
#define SYSTEM_HELPER_INCLUDED

#include <unistd.h>
#include "../common/UnicodeString.h"

namespace hwdet
{
    //! Read file permission bit.
    const int PERMISSIONS_READ = R_OK;
    
    //! Write file permission bit.
    const int PERMISSIONS_WRITE = W_OK;
    
    //! Execute file permission bit.
    const int PERMISSIONS_EXECUTE = X_OK;
    
    //! Get name of an error with given error number.
    String GetErrorName(int error_id);
    
    //! Get name of an error.
    String GetLastErrorName(void);
    
    //! Get error number.
    int GetLastErrorID(void);
    
    //! Test if given file exists.
    bool FileExists(const char *filename);
    
    //! Test permissions of the file.
    bool FileTestPermissions(const char *filename, int flags);
}

#endif
