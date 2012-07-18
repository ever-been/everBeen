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

/*! \file CompileTimeCheck.h
 *  \author Branislav Repcek
 *  \date 8. 10. 2006
 *
 *  \brief Macro with compile-time check.
 */

#ifndef COMPILE_TIME_CHECK_INCLUDED
#define COMPILE_TIME_CHECK_INCLUDED

namespace lm
{
    /*! \brief Utility namespace.
     */
    namespace util
    {
        /*! \brief Base template for checker.
         */
        template< bool >
        struct CompileTimeChecker;

        /*! \brief Specialization of the base template for true.
         */
        template< >
        struct CompileTimeChecker< true >
        {
        };
    } // namespace lm::util

/* This will test if given expression evaluates to true. If yes, it will compile
 * as an empty statement without side-effect. If not, it will create error message
 * about undefined (or incomplete) type which cannot be used. Name of the type that
 * will be reported in message will be ERROR_message (that is, concatenation of
 * the message parameter with the ERROR_ string). Therefore message parameter has
 * to be valid C++ identifier.
 */
#define COMPILE_TIME_CHECK(expression, message)                             \
{                                                                           \
    ::lm::util::CompileTimeChecker< (expression) > ERROR_##message;         \
    (void) ERROR_##message;                                                 \
}

} // namespace lm

#endif
