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

/*! \file XMLFileOutput.h
 *  \author Branislav Repcek
 *  \date 1. 12. 2005
 *
 *  \brief Classes which represent elements in XML files.
 */

#ifndef XML_FILE_OUTPUT_INCLUDED
#define XML_FILE_OUTPUT_INCLUDED

#include <vector>
#include "UnicodeString.h"

namespace hwdet
{

    //! Default encoding of the output.
#ifdef UNICODE
    const Char DefaultEncoding[] = TXT("UTF-16");
#else
#ifdef _WINDOWS
    const Char DefaultEncoding[] = TXT("WINDOWS-1252");
#else
    const Char DefaultEncoding[] = TXT("US-ASCII");
#endif
#endif

    /*! \brief Convert given value to the string.
     *
     *  \param value Value to convert.
     *  \return String representing given value.
     */
    template< typename T > String inline ConvertToXMLString(const T &value)
    {
        StringStream stream;

        stream << value;

        return stream.str();
    }

    /*! \brief Convert string to XML-friendly version (all special chars are escaped).
     *
     *  \param value String to convert.
     *  \return XML-friendly version of the input string.
     */
    String inline ConvertToXMLString(const String &value)
    {
        StringStream stream;

        const String chars = TXT("&\'\"<>");
        const String reps[] = {TXT("&amp;"), TXT("&apos;"), TXT("&quot;"), TXT("&lt;"), TXT("&gt;")};

        String::const_iterator end = value.end();

        for (String::const_iterator it = value.begin(); it != end; ++it)
        {
            size_t index = chars.find(*it);
            if (index == chars.npos)
            {
                stream << (Char) *it;
            }
            else
            {
                stream << reps[index];
            }
        }

        return stream.str();
    }

    /*! \brief Basic class for all nodes in XML file.
     *
     *  This class creates XML tree-like file structure of nodes. Each node has a name and a list of child nodes.
     */
    class XMLElement
    {
    public:
        /*! \brief Default constructor.
         *
         *  Create empty node (with no name).
         */
        XMLElement(void) :
        name(TXT("(undefined)")),
        depth(0),
        parent(NULL) 
        {
        }

        /*! \brief Constructor with node init.
         *
         *  \param elem_name Name of current node.
         */
        XMLElement(const Char *elem_name) :
        name(elem_name),
        depth(0),
        parent(NULL) 
        {
        }

        /*! \brief Constructor with node init.
         *
         *  \param elem_name Name of current node.
         */
        XMLElement(const String &elem_name) :
        name(elem_name),
        depth(0),
        parent(NULL) 
        {
        }

        /*! \brief Virtual destructor.
         *
         *  This destructor will destroy all child nodes. You do not need to freem them by yourself.
         */
        virtual ~XMLElement(void)
        {
            std::vector< XMLElement * >::const_iterator end = nodes.end();

            for (std::vector< XMLElement * >::const_iterator it = nodes.begin(); it != end; ++it)
            {
                delete *it;
            }
        }

        /*! \brief Get name of node.
         *
         *  \return Name of current node.
         */
        virtual String GetNodeName(void) const
        {
            return name;
        }

        /*! \brief Add sub-node to the current node.
         *
         *  Adds new sub-node to the end of the sub-node list.
         *
         *  \param new_node Node to add. If this is NULL, nothing is done.
         */
        void AddSubNode(XMLElement *new_node)
        {
            if (new_node == NULL)
            {
                return;
            }

            new_node->SetNodeParent(this);
            nodes.push_back(new_node);
        }

        /*! \brief Get sub-node at given position.
         *
         *  \param index Position of sub-node within list of sub-nodes. First sub-node has index of zero, last has index getSubNodeCount() - 1.
         *  \return Requested node of NULL if index is too large.
         */
        XMLElement *GetSubNode(const size_t index) const
        {
            if (index < nodes.size())
            {
                return nodes[index];
            }
            else
            {
                return NULL;
            }
        }

        /*! \brief Get number of sub-nodes of current node.
         *
         *  \return Number of current node's sub-nodes.
         */
        size_t GetSubNodeCount(void) const
        {
            return nodes.size();
        }

        /*! \brief Get string representing current node.
         *
         *  Converts all data within current node (including sub-nodes) to strings and returns concatenation.
         *
         *  \return String containing all node's data.
         */
        virtual String GetNodeValueString(void)
        {
            StringStream stream;

            std::vector< XMLElement * >::const_iterator end = nodes.end();

            for (std::vector< XMLElement * >::const_iterator it = nodes.begin(); it != end; ++it)
            {
                (*it)->AppendToStream(stream);
            }

            return stream.str();
        }

        /*! \brief Append string representing current node to the given stream.
         *
         *  \param stream Output stream to which current node's data will be appended.
         */
        virtual void AppendToStream(OStream &stream) const
        {
            for (size_t i = 0; i < depth; ++i)
            {
                stream << TXT("\t");
            }

            stream << TXT("<") << GetNodeName() << TXT(">") << std::endl;
            
            std::vector< XMLElement * >::const_iterator end = nodes.end();
            for (std::vector< XMLElement * >::const_iterator it = nodes.begin(); it != end; ++it)
            {
                (*it)->AppendToStream(stream);
            }
            
            for (size_t i = 0; i < depth; ++i)
            {
                stream << TXT("\t");
            }

            stream << TXT("</") << GetNodeName() << TXT(">") << std::endl;
        }

    protected:

        //! Name of the node.
        String                     name;

        //! List of child nodes.
        std::vector< XMLElement* > nodes;

        //! Depth of current node (it is used to align output in stream).
        size_t                     depth;

        //! Parent nodes.
        XMLElement                 *parent;

        /*! \brief Set depth of current node.
         *
         *  This will set depth of current node to given depth and update depths of all nodes in subtree accordingly.
         *
         *  \param nd New depth of current node.
         */
        void SetNodeDepth(size_t nd)
        {
            depth = nd;
            std::vector< XMLElement * >::iterator end = nodes.end();

            for (std::vector< XMLElement * >::iterator it = nodes.begin(); it != end; ++it)
            {
                (*it)->SetNodeDepth(depth + 1);
            }
        }

        /*! \brief Set parent node of current node.
         *
         *  This method will also update depths of surrent subtree.
         *
         *  \param e New parent node.
         */
        void SetNodeParent(XMLElement *e)
        {
            parent = e;

            if (e != NULL)
            {
                SetNodeDepth(e->depth + 1);
            }
            else
            {
                SetNodeDepth(0);
            }
        }
    };

    /*! \brief Node in XML file which contains value of given type.
     */
    template< typename T >
    class XMLValueElement : public XMLElement
    {
    public:
        /*! \brief Constructor with initialization.
         *
         *  \param elem_name Name of element.
         *  \param elem_value Value assigned to the element.
         */
        XMLValueElement(const Char *elem_name, const T elem_value) :
        XMLElement(elem_name),
        value(elem_value) 
        {
        }

        /*! \brief Constructor with initialization.
         *
         *  \param elem_name Name of element.
         *  \param elem_value Value assigned to the element.
         */
        XMLValueElement(const String &elem_name, const T elem_value) :
        XMLElement(elem_name),
        value(elem_value) 
        {
        }

        /*! \brief Destructor.
         */
        virtual ~XMLValueElement(void)
        {
        }

        /*! \brief Get value assigned to the current node.
         *
         *  \return Value assigned to the node.
         */
        T GetNodeValue(void) const
        {
            return value;
        }

        /*! Set value of current node.
         *
         *  \param new_value New value which will be assigned to the node.
         */
        void SetNodeValue(T new_value)
        {
            value = new_value;
        }

        /*! \brief Get string representign value of current node.
         *
         *  \return String representign value of current node.
         */
        String GetNodeValueString(void) const
        {
            return ConvertToXMLString(value);
        }

        /*! \brief Append string representing current node to the given stream.
         *
         *  \param stream Output stream to which current node's data will be appended.
         */
        virtual void AppendToStream(OStream &stream) const
        {
            for (size_t i = 0; i < depth; ++i)
            {
                stream << TXT("\t");
            }

            stream << TXT("<") << GetNodeName() << TXT(">") << GetNodeValueString() << 
                      TXT("</") << GetNodeName() << TXT(">") << std::endl;
        }

    protected:
        //! Value of the node.
        T           value;
    };

    /*! \brief Class which represent simple XML files. This class Only allows writing to such files (no parsing).
     */
    class XMLOutputFile
    {
    public:
        /*! \brief Constructor.
         *
         *  Create XML file with given encoding.
         *
         *  \param enc Encoding of the file. If NULL, no encoding information is written to the output file.
         */
        XMLOutputFile(const Char *enc = NULL) :
        root_node(NULL),
        encoding(TXT("")) 
        {
            if (enc != NULL)
            {
                encoding = String(enc);
            }
        }

        /*! \brief Constructor.
         *
         *  Create XML file with given encoding and root node.
         *
         *  \param enc Encoding of the file.
         *  \param root_elem Root node of the file.
         */
        XMLOutputFile(XMLElement *root_elem, const Char *enc = DefaultEncoding) :
        root_node(root_elem),
        encoding(enc)
        {
        }

        /*! \brief Destructor.
         *
         *  Memory used by the root node of the XML file is NOT freed.
         */
        ~XMLOutputFile(void)
        {
        }

        /*! \brief Write data to the output stream.

            \param stream Stream to which file will be written.
            \return <code>true</code> if successfull, <code>false</code> otherwise.
        */
        bool Write(OStream &stream) const
        {
            if (encoding != String(TXT("")))
            {
                stream << TXT("<?xml version=\"1.0\" encoding=\"") << encoding << TXT("\"?>") << std::endl;
            }
            else
            {
                stream << TXT("<?xml version=\"1.0\"?>") << std::endl;
            }

            root_node->AppendToStream(stream);

            return true;
        }

        /*! \brief Write data to given external file.
         *
         *  \param file_name Name of file to which data should be written. File will be created it it does not exists. 
         *         Existing file will be overwritten.
         *
         *  \return <code>true</code> if successfull, <code>false</code> otherwise.
         */
        bool Write(String file_name) const
        {
            if (root_node == NULL)
            {
                return false;
            }

            OFStream file;

#if defined(_MSC_VER) && (_MSC_VER < 1400) && defined(UNICODE) && defined(_WINDOWS)
            // this is provided for older versions of Visual Studio which do not support wide char
            // strings as file names
            file.open(Win_UnicodeToANSI(file_name).c_str(), std::ios_base::out);
#else
            file.open(file_name.c_str(), std::ios_base::out);
#endif

            if (file.is_open())
            {
			    if (encoding != String(TXT("")))
                {
				    file << TXT("<?xml version=\"1.0\" encoding=\"") << encoding << TXT("\"?>") << std::endl;
				}
                else
                {
				    file << TXT("<?xml version=\"1.0\"?>") << std::endl;
				}

                root_node->AppendToStream(file);
                file.close();

                return true;
            }

            return false;
        }

        /*! \brief Set root node to given node.
         *
         *  \param elem New root node of the file.
         */
        void SetRootNode(XMLElement *elem)
        {
            root_node = elem;
        }

        /*! \brief Remove root node and free used memory.
         */
        void ClearRootNode(void)
        {
            delete root_node;
            root_node = NULL;
        }
		
        /*! \brief Get character encoding of the output.
         *
         *  \return String with name of the output character encoding. Empty string if no encoding has been specified.
         */
        String GetEncoding(void) const
        {
            return encoding;
        }

    private:
        //! Root node of the file.
        XMLElement  *root_node;

        //! Encoding.
        String      encoding;
    };

} // namespace hwdet

#endif
