/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.common.rsl;

/**
 * Interface representing a node in the RSL property tree (see below for a full
 * description of the property tree). All interfaces representing particular
 * node types are derived from it. RSL clients (modules using RSL for querying
 * on their data) should not implement this interface, but its descendants.
 *
 * RSL property tree is a structure, on which the RSL expressions are evaluated.
 * Each RSL client supplies its own implementation of the property tree
 * interfaces, which encapsulates structure of the client's internal database.
 * 
 * We will describe the property tree on an example: a family. Suppose there are
 * four members in the family: mother, father and two children. For each person,
 * we want to know the name and age. This leads to following structure of the
 * database:
 * 
 * <pre>mother.name
 * mother.age
 * father.name
 * father.age
 * children[0].name
 * children[0].age
 * children[1].name
 * children[1].age</pre>
 * 
 * The property tree for this strucutre has a root, which is a <em>container
 * property</em> (<code>ContainerProperty</code> class instance). It contains
 * three named properties: <code>mother</code>, <code>father</code> and
 * <code>children</code>.
 * 
 * Properties <code>mother</code> and <code>father</code> are similar: both are
 * <em>container properties</em>, which contain properties <code>name</code> and
 * <code>age</code>. These are <em>simple properties</em>
 * (<code>SimpleProperty</code> class instances) and each containts a value --
 * in the case of <code>age</code> it is an <code>Integer</code>, in the case of
 * <code>Name</code> it is a <code>String</code>.
 * 
 * Property <code>children</code> is an <em>array property</em>
 * (<code>ArrayProperty</code> class instance). It contains container
 * properties, referenced byt integer index. Each inner container property is
 * similar in the structure to the <code>mother</code> and <code>father</code>
 * properties.
 * 
 * Example above is simple, but it gives an idea of the property tree structure.
 * Of course, container and array propertis can be nested to arbitrary depth and
 * the property tree could be pretty complex.
 * 
 * @author David Majda
 */
public interface Property {
	/* No methods. See descendants of this interface for something more
	 * interesting :-)
	 */
}
