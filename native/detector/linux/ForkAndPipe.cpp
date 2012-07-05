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

/*! \file ForkAndPipe.cpp
 *  \author Branislav Repcek
 *  \date 13. 12. 2006
 *  
 *  \brief Implementation of the ForkAndPipe class.
 */
 
#include <iostream>
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include "../common/UnicodeString.h"
#include "../common/MessageReporter.h"
#include "SystemHelper.h"
#include "ForkAndPipe.h"
 
using namespace std;
 
namespace hwdet
{
    /*! \brief Write error message to error output.
     * 
     *  This will check if syscall completed successfully and in case of error
     *  output simple error message with error description to the error output stream.
     * 
     *  \param result Return value of the syscall. Call is assumed to be failed if this
     *         value is -1, so this method cannot be used for calls where -1 is valid
     *         return value.
     *  \param message Message to write alongside error description.
     */
    void AutoCerr(int result, const String &message)
    {
        if (result == -1)
        {
            int err_id = GetLastErrorID();
            cerr << TXT("ERROR: ") << message << " Description: " << 
                    GetErrorName(err_id) << TXT(".") << endl;
        }
    }
    
    /*! \param rep_name Name of the message reporter.
     *  \param parent_reporter Parent message reporter. If NULL, messages are output
     *         directly to the stdout.
     */
    ForkAndPipe::ForkAndPipe(const String &rep_name, MessageReporter *parent_reporter) :
    MessageReporter(rep_name, parent_reporter)
    {
        pipe_desc[0] = pipe_desc[1] = pid = 0;
        error_num = 0;
    }
    
    /*!
     */
    ForkAndPipe::~ForkAndPipe(void)
    {
    }
    
    /*! This will create new pipe and fork current process. In child it will call 
     *  method ChildProcess. In parent process, it will call ParentProcess method.
     *  In case of error, error handler is called.
     * 
     *  \return true on success, false otherwise.
     */
    bool ForkAndPipe::Run(void)
    {
        if (pipe(pipe_desc) == -1)
        {
            // Unable to create pipe descriptors.
            OnError(GetLastErrorID(), "Create pipe.");
            return false;
        }
        
        pid = fork();
        
        switch (pid)
        {
            case -1: // Error.
                error_num = GetLastErrorID();
                OnError(error_num, "Fork.");
                
                // Close pipe descriptors.
                AutoError(close(pipe_desc[0]), "Fork error: closing pipe read fd.");
                AutoError(close(pipe_desc[1]), "Fork error: closing pipe write fd.");
                break;
                
            case 0: // Child process.
                // Error are written to error stream so we the parent process does not
                // attempt to parse them.
                AutoCerr(close(1), "Child process: close(1).");
                AutoCerr(dup(pipe_desc[1]), "Child process: dup failed.");
                AutoCerr(close(pipe_desc[0]), "Child process: closing pipe read fd.");
                AutoCerr(close(pipe_desc[1]), "Child process: closing pipe write fd.");
        
                ChildProcess();
                break;
                
            default: // Parent process.
            {
                AutoError(close(0), "Parent process: close(0).");
                int read_desc = dup(pipe_desc[0]);
                AutoError(read_desc, "Parent process: dup failed.");
                AutoError(close(pipe_desc[0]), "Parent process: closing pipe read fd.");
                AutoError(close(pipe_desc[1]), "Parent process: closing pipe write fd.");
                    
                FILE *file;
    
                if ((file = fdopen(read_desc, "r")) == NULL)
                {
                    OnError(GetLastErrorID(), "Parent process: fdopen failed.");
                    return false;
                }
                    
                    
                ParentProcess(pid, file);
                AutoError(fclose(file), "Parent process: fclose failed.");
                    
                WaitForChildProcess();
    
                break;
            }
        }
        
        return true;
    }
    
    /*! Basic error handler. Does nothing but print error name to the stdout.
     * 
     *  \param error_code Error code (value of the errno).
     *  \param message Message that will be printed alongside the error name.
     *         If message is empty, only error name will be output.
     */
    void ForkAndPipe::OnError(int error_code, const String &message)
    {
        if (message.size() != 0)
        {
            ReportMessage(TXT(" ERROR: ") + message + TXT(" Description: ") + 
                    GetErrorName(error_code) + TXT("."));
        }
        else
        {
            ReportMessage(TXT(" ERROR: ") + GetErrorName(error_code) + TXT("."));
        }
    }
    
    /*! If an error occured calls OnError handler with given message.
     * 
     *  \param result Return value of the syscall or library fuction. Negative value
     *         means that the call failed.
     *  \param message Message that will be forwarded to the OnError call. Empty
     *         string means no message (default value).
     * 
     *  \return true if no error occured, false if an error occured prior to the call.
     */
    bool ForkAndPipe::AutoError(int result, const String &message)
    {
        if (result == -1)
        {
            OnError(GetLastErrorID(), message);
            return false;
        }
        else
        {
            return true;
        }
    }
}
