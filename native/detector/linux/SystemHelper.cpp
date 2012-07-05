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

/*! \file SystemHelper.cpp
 *  \author Branislav Repcek
 *  \date 12. 12. 2006
 *  
 *  \brief System helper functions.
 */

#include <errno.h>
#include <unistd.h>
#include <string.h>
#include "../common/UnicodeString.h"
#include "SystemHelper.h"

namespace hwdet
{
    /*! \param error_id Error number (value of the <i>errno</i>).
     * 
     *  \return String containing name of the error.
     */
    String GetErrorName(int error_id)
    {
        return String(strerror(error_id));
    }
    
    /*! \return String with name of the last error as stored in the <code>errno</code> 
     *          global variable.
     */
    String GetLastErrorName(void)
    {
        return GetErrorName(errno);
    }
    
    /*! \return Id number of the last error as stored in the <code>errno</code> global.
     */
    int GetLastErrorID(void)
    {
        return errno;
    }
    
    /*! \param filename Name of the file to test.
     * 
     *  \return true if given file exists, false otherwise.
     */
    bool FileExists(const char *filename)
    {
        return access(filename, F_OK) == 0;
    }
    
    /*! \param filename Name of the file to test.
     *  \param flags Mask containing one or more of the PERMISSION_READ, PERMISSION_WRITE, 
     *         PERMISSION_EXECUTE flags which specify permissions to test. In addition, file
     *         existence is checked.
     * 
     *  \return <code>true</code> if file exists and all specified permissions are present, 
     *          <code>false</code> otherwise.
     */
    bool FileTestPermissions(const char *filename, int flags)
    {
        return access(filename, flags) == 0;
    }
}
