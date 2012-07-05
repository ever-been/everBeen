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

/*! \file FileParser.h
 *  \author Branislav Repcek
 *  \date 11. 12. 2006
 * 
 *  \brief Helper routines for text file parsing.
 */

#ifndef FILE_PARSER_INCLUDED
#define FILE_PARSER_INCLUDED

#include <vector>
#include <utility>
#include <sstream>
#include <string>
#include <string.h>
#include <ctype.h>
#include "../common/UnicodeString.h"

namespace hwdet
{
    
    //! Read all lines from the text file.
    bool ReadLines(const char *file_name, std::vector< String > &lines);
    
    //! Read name of the filed on the current line.
    String GetFieldName(const String &line, size_t *ends_at = NULL,
                        const String &end = TXT(":"), const size_t start_at = 0);
    
    //! Test if one string begins with another.
    bool BeginsWith(const String &str, const String &what);
    
    /*! Retrieve value of the column with given index. Columns can be separated by custom
     *  delimiter characters. Value will be automatically converted to the target type.
     * 
     *  \param line String which contains requested value.
     *  \param index Index of the column which contains given value. First column has index of 1.
     *  \param start_at Index of the first character (or some delimiter directly before) 
     *         of the first column.
     *  \param delims Delimiters that are between columns. Each character of this string is threated
     *         as a delimiter.
     * 
     *  \param result Reference to the variable which will receive value of the given column.
     *         Value is automatically converted to the target type via the stream operator >>.
     *         If an error occured during conversion or if column with given index does not
     *         exist, original value remains unchanged.
     * 
     *  \return true on success, false if field with given index does not exist or if
     *          an error occured while converting string to the value of target type.
     */
    template< typename T >
    bool GetFieldValue(const std::string &line, const size_t index, T &result, 
                       const size_t start_at = 0, const char *delims = "\t ")
    {
        size_t field_index = 0;
        bool in_field = false;
        size_t pos = start_at;

        for ( ; pos < line.size(); ++pos)
        {
            if (strchr(delims, line[pos]) == NULL)
            {
                if (!in_field)
                {
                    in_field = true;
                    ++field_index;
                    if (field_index == index)
                    {
                        break;
                    }
                    if (field_index > index)
                    {
                        return false;
                    }
                }
            }
            else
            {
                in_field = false;
            }
        }

        if (field_index == index)
        {
            std::stringstream stream;

            for ( ; (pos < line.size()) && (!isspace(line[pos])); ++pos)
            {
                stream << line[pos];
            }

            stream >> result;
            return !stream.fail();
        }

        return false;
    }
}

#endif
