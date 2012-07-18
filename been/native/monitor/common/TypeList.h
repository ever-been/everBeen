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

/*! \file TypeList.h
 *  \author Branislav Repcek
 *  \date 7. 10. 2006
 *
 *  \brief Templates and defines which provide API to work with Type Lists.
 */

#ifndef TYPE_LIST_INCLUDED
#define TYPE_LIST_INCLUDED

//! Namespace with TypeList definition and support functions.
namespace tl
{

    /*! \brief Null type which is used to mark end of the TypeList.
     */
    class NullType
    {
    };

    /*! \brief TypeList template class.
     *
     *  This class contains two types: Head and Tail. To create longer TypeLists use another TypeList as a Tail. NullType should 
     *  always be the last type in the list.
     */
    template< typename H, class T >
    class TypeList
    {
    public:
        //! Head.
        typedef H Head;

        //! Tail.
        typedef T Tail;
    };

    /*
     * ListLength - calculate length of the list.
     */

    /*! \brief Calculate length of the TypeList.
     *
     *  Usage: ListLength< type_list >::Length
     */
    template< class List >
    class ListLength;

    /*! \brief Calculate length of the TypeList.
     */
    template< class H, class T >
    class ListLength< TypeList< H, T > >
    {
    public:
        enum
        {
            //! Recursively calculate length of the list.
            Length = 1 + ListLength< T >::Length
        };
    };

    /*! \brief Calculate length of the TypeList.
     */
    template< >
    class ListLength< NullType >
    {
    public:
        enum
        {
            //! Empty list.
            Length = 0
        };
    };

    /*
     * Get - Select element from TypeList based on its index.
     */

    /*! \brief Select i-th type from TypeList.
     *
     */
    template< class List, size_t i > 
    class Get;

    /*! \brief Select i-th type from TypeList.
     */
    template< class H, class T >
    class Get< TypeList< H, T >, 0 >
    {
    public:
        //! Resulting type.
        typedef H Result;
    };

    /*! \brief Select i-th type from TypeList.
     */
    template< class H, class T, size_t i >
    class Get< TypeList< H, T >, i >
    {
    public:
        //! Resulting type.
        typedef typename Get< T, i - 1 >::Result Result;
    };

    /*
     * IndexOf - Find if given type is in TypeList and return its index.
     */

    /*! \brief Index of type in TypeList.
     *
     *  Usage: ::tl::IndexOf< List, TypeToSearch >::value
     */
    template< class Tlist, class T > class IndexOf;

    /*! \brief Index of type in TypeList.
     *
     *  Specialization for end of the list. Returns -1 in <code>value</code>.
     */
    template< class T >
    struct IndexOf< NullType, T >
    {
        enum
        {
            //! Not found -> set result to -1.
            value = -1 
        };
    };

    /*! \brief Index of type in TypeList.
     *
     *  Specialization for case where element we are looking for is head of the list. Returns 0 in <code>value</code>.
     */
    template< class T, class Tail >
    struct IndexOf< TypeList< T, Tail >, T >
    {
        enum
        {
            //! Type we are looking for is head -> set result to 0.
            value = 0
        };
    };

    /*! \brief Index of type in TypeList.
     *
     *  General case. Recursive "call" to the specialized versions. Index of type is returned in <code>value</code>.
     */
    template< class Head, class Tail, class T >
    struct IndexOf< TypeList< Head, Tail >, T >
    {
    private:
        enum
        {
            //! Recurisevely search tail.
            temp = IndexOf< Tail, T >::value
        };

    public:
        enum
        {
            //! Index of the type we are looking for.
            value = (temp == - 1) ? -1 : temp + 1
        };
    };

} // namespace tl

//! Create TypeList containing one type.
#define CREATE_TYPE_LIST_01(T01) ::tl::TypeList< T01, ::tl::NullType >

//! Create TypeList containing two types.
#define CREATE_TYPE_LIST_02(T01, T02) ::tl::TypeList< T01, CREATE_TYPE_LIST_01(T02) >

//! Create TypeList containing three types.
#define CREATE_TYPE_LIST_03(T01, T02, T03) ::tl::TypeList< T01, CREATE_TYPE_LIST_02(T02, T03) >

//! Create TypeList containing four types.
#define CREATE_TYPE_LIST_04(T01, T02, T03, T04) ::tl::TypeList< T01, CREATE_TYPE_LIST_03(T02, T03, T04) >

//! Create TypeList containing five types.
#define CREATE_TYPE_LIST_05(T01, T02, T03, T04, T05) \
            ::tl::TypeList< T01, CREATE_TYPE_LIST_04(T02, T03, T04, T05) >

//! Create TypeList containing six types.
#define CREATE_TYPE_LIST_06(T01, T02, T03, T04, T05, T06) \
            ::tl::TypeList< T01, CREATE_TYPE_LIST_05(T02, T03, T04, T05, T06) >

//! Create TypeList containing seven types.
#define CREATE_TYPE_LIST_07(T01, T02, T03, T04, T05, T06, T07) \
            ::tl::TypeList< T01, CREATE_TYPE_LIST_06(T02, T03, T04, T05, T06, T07) >

//! Create TypeList containing eight types.
#define CREATE_TYPE_LIST_08(T01, T02, T03, T04, T05, T06, T07, T08) \
            ::tl::TypeList< T01, CREATE_TYPE_LIST_07(T02, T03, T04, T05, T06, T07, T08) >

//! Create TypeList containing nine types.
#define CREATE_TYPE_LIST_09(T01, T02, T03, T04, T05, T06, T07, T08, T09) \
            ::tl::TypeList< T01, CREATE_TYPE_LIST_08(T02, T03, T04, T05, T06, T07, T08, T09) >

//! Create TypeList containing ten types.
#define CREATE_TYPE_LIST_10(T01, T02, T03, T04, T05, T06, T07, T08, T09, T10) \
            ::tl::TypeList< T01, CREATE_TYPE_LIST_09(T02, T03, T04, T05, T06, T07, T08, T09, T10) >

//! Create TypeList containing 11 types.
#define CREATE_TYPE_LIST_11(T01, T02, T03, T04, T05, T06, T07, T08, T09, T10, T11) \
    ::tl::TypeList< T01, CREATE_TYPE_LIST_10(T02, T03, T04, T05, T06, T07, T08, T09, T10, T11) >

//! Create TypeList containing 12 types.
#define CREATE_TYPE_LIST_12(T01, T02, T03, T04, T05, T06, T07, T08, T09, T10, T11, T12) \
    ::tl::TypeList< T01, CREATE_TYPE_LIST_11(T02, T03, T04, T05, T06, T07, T08, T09, T10, T11, T12) >

#endif
