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

/*! \file CheckedResult.h
 *  \author Branislav Repcek
 *  \date 11. 12. 2005
 *
 *  \brief Header file for template class CheckedResult.
 */

#ifndef CHECKED_RESULT_INCLUDED
#define CHECKED_RESULT_INCLUDED

namespace hwdet
{

    /*! \brief Provides means to return two values from function - call status and return value.
     *
     *  This class is provided to simplify work with function which return two values. One of values is always error
     *  state and the other one is return value.
     */
    template< typename T >
    class CheckedResult
    {
    public:
        /*! \brief Constructor.
         *
         *  \param call_success <code>true</code> if call to function wass successfull, <code>false</code> otherwise.
         *  \param retval Return value of the function.
         */
        CheckedResult(bool call_success, const T &retval) :
        successfull(call_success),
        value(retval)
        {
        }

        /*! \brief Constructor.
         *
         *  This ctor should be used to report call failuers since it does not require any value.
         *
         *  \param call_success <code>true</code> if call to function wass successfull, <code>false</code> otherwise.
         */
        CheckedResult(bool call_success = false) :
        successfull(call_success)
        {
        }

        /*! \brief Copy constructor.
         *
         *  \param r1 Result to be copied to this.
         */
        CheckedResult(const CheckedResult &r1) :
        successfull(r1.successfull),
        value(r1.value)
        {
        }

        /*! \brief Destructor.
         */
        ~CheckedResult(void)
        {
        }

        /*! \brief Test whether call succeded or not.
         *
         *  \return <code>true</code> if call succeded, <code>false</code> otherwise.
         */
        bool Succeeded(void) const
        {
            return successfull;
        }

        /*! \brief Test whether call failed or not.
         *
         *  \return <code>true</code> if call failed, <code>true</code> otherwise.
         */
        bool Failed(void) const
        {
            return !successfull;
        }

        /*! \brief Get function return value.
         *
         *  \return Value.
         */
        T &GetValue(void) const
        {
            return (T &) value;
        }

    private:
        //!< Was call successfull?
        bool successfull;

        //!< Return value.
        T    value;
    };
}

#endif
