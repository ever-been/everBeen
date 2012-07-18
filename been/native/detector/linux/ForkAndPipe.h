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

/*! \file ForkAndPipe.h
 *  \author Branislav Repcek
 *  \date 12. 12. 2006
 *  
 *  \brief Header file for ForkAndPipe class.
 */
 
#ifndef FORK_AND_PIPE_INCLUDED
#define FORK_AND_PIPE_INCLUDED

#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdio.h>
#include "../common/UnicodeString.h"
#include "../common/MessageReporter.h"

namespace hwdet
{
    /*! \brief Fork process and automatically create pipes.
     */
    class ForkAndPipe : public MessageReporter
    {
    public:
        //! Constructor.
        ForkAndPipe(const String &rep_name, MessageReporter *parent_reporter = NULL);
            
        //! Destructor.
        virtual ~ForkAndPipe(void);
            
        //! Fork current process and create appropriate pipes.
        bool Run(void);
            
        /*! \brief Method called in parent process.
         * 
         *  This method is called in parent process (parent is process from which run has been 
         *  called). In this method you can read output produced by the child process(es) using
         *  pipe descriptors created in the run method. You do not need to wait for the child to
         *  terminate, it is done automatically.
         */
        virtual void ParentProcess(pid_t child_pid, FILE *file) = 0;
            
        /*! \brief Method called in the child process.
         * 
         *  This method is called in the child process. You can exec any process you want in it
         *  and output will be sent through the pipe to the parent process.
         */
        virtual void ChildProcess(void) = 0;
            
        //! \brief Error handler.
        virtual void OnError(int error_code, const String &message);
            
    protected:
        
        /*! \brief Get PID of the child process created.
         * 
         *  \return PID of the child process created.
         */
        pid_t GetChildPID(void) const
        {
            return pid;
        }
            
        /*! \brief Wait for children process to end.
         * 
         *  This will wait until child process terminates. Do NOT call this in the child
         *  process. You should not call this in parent either since it is called automatically
         *  when parent method ends.
         */
        void WaitForChildProcess(void)
        {
            while (wait((int *) 0) != pid) ;
        }
        
        /*! \brief Get error code after fork.
         *  
         *  Call this to get error code after the fork has been called. Note that this does not
         *  represent current error code, so it will not change if other error occurs.
         */
        int GetForkError(void) const
        {
            return error_num;
        }
            
        //! Test if an error occured and output appropriate message.
        bool AutoError(int result, const String &message = "");
        
    private:
        //! Pipe descriptors.
        int     pipe_desc[2];
        
        //! PID of the created child process.
        pid_t   pid;
        
        //! Error number (after fork).
        int     error_num;
        
        //! Empty copy ctor, this class should not be copied.
        ForkAndPipe(const ForkAndPipe &) : MessageReporter(TXT(""), NULL) { }
        
        //! Private assignment, never defined -> prevents copying.
        ForkAndPipe &operator =(const ForkAndPipe &);
    };
}

#endif
