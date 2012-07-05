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

/*! \file FileParser.cpp
 *  \author Branislav Repcek
 *  \date 11. 12. 2006
 * 
 *  \brief Helper function to parse text files.
 */

#include <iostream>
#include <utility>
#include <vector>
#include "../common/UnicodeString.h"

using namespace std;

namespace hwdet
{

    /*! This will read all lines from the text file into given vector.
     * 
     *  \param file_name Name of the file to read.
     *  \param lines Vector which will receive lines read from the file. Note that lines
     *         are added at the end of the vector and data from vector are not removed automatically.
     * 
     *  \return true on success, false otherwise.
     */
    bool ReadLines(const char *file_name, vector< String > &lines)
    {
        IFStream file_in;
    
        file_in.open(file_name, ios_base::in);
    
        if (!file_in.is_open())
        {
            return false;
        }
    
        String line;
    
        while (!file_in.eof())
        {
            getline(file_in, line);
        
            if (file_in.bad())
            {
                file_in.close();
                return false;
            }
        
            lines.push_back(line);
        }
    
        file_in.close();
    
        return true;
    }

    /*! Retrieve name of the current line. Name is string that does not contain white-space
     *  characters at the beginning or at the end (white-spaces in the middle are allowed).
     * 
     *  \param line String which contains data and name.
     *  \param ends_at Pointer to the variable which will receive index of the first character
     *         after the separator string. Set this to NULL if you don't need this information.
     *  \param end String which marks end of the field name. Default value is ":" which is
     *         suitable for most of the files in the /proc directory.
     *  \param start_at First character of the name. May be whitespace. Default value is 0.
     * 
     *  \return String containing name of the field.
     */
    String GetFieldName(const String &line, size_t *ends_at,
                        const String &end, const size_t start_at)
    {
        if (line.size() == 0)
        {
            if (ends_at)
            {
                *ends_at = 0;
            }
            
            return TXT("");
        }
        
        StringStream stream;

        size_t name_start = start_at;
        size_t name_end = line.find(end, start_at);
        size_t value_pos = name_end + end.size();

        if (name_end == line.npos)
        {
            name_end = line.size();
            value_pos = line.npos;
        }    

        for ( ; (name_start < name_end) && (isspace(line[name_start])); ++name_start) ;
        for (--name_end ; (name_end > name_start) && (isspace(line[name_end])); --name_end) ;

        if (ends_at)
        {
            *ends_at = value_pos;
        }
        
        return line.substr(name_start, name_end - name_start + 1);
    }

    /*! \param str String to test.
     *  \param what String to search for.
     * 
     *  \return true if string begins with given substring, false otherwise.
     */
    bool BeginsWith(const String &str, const String &what)
    {
        if (str.size() < what.size())
        {
            return false;
        }
        
        size_t i = 0;
        
        while ((str[i] == what[i]) && (i < what.size())) ++i;
        
        return (str[i] == what[i]) || (i == what.size());
    }
}
