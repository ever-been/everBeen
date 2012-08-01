/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.clinterface.adapters;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.HostsModule.Errors;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;
import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.ObjectRestriction;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeFactory;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface;
import cz.cuni.mff.been.hostmanager.database.RSLRestriction;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;
import cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition;
import cz.cuni.mff.been.jaxb.AbstractSerializable;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.group.Group;
import cz.cuni.mff.been.jaxb.group.Host;
import cz.cuni.mff.been.jaxb.group.Hosts;
import cz.cuni.mff.been.jaxb.group.MetaDesc;
import cz.cuni.mff.been.jaxb.properties.Alias;
import cz.cuni.mff.been.jaxb.properties.ChangedString;
import cz.cuni.mff.been.jaxb.properties.Complex;
import cz.cuni.mff.been.jaxb.properties.Properties;
import cz.cuni.mff.been.jaxb.properties.Remove;
import cz.cuni.mff.been.jaxb.properties.RemoveIndexed;
import cz.cuni.mff.been.jaxb.properties.Restriction;
import cz.cuni.mff.been.jaxb.properties.Simple;
import cz.cuni.mff.been.jaxb.properties.TheObject;
import cz.cuni.mff.been.jaxb.properties.Tree;

/**
 * A bunch of static methods for data structure conversion.
 * 
 * @author Andrej Podzimek
 */
public final class HostDataConvertor {
	
	/**
	 * This interface unifies setting of subtrees and properties for various levels of the tree.
	 * 
	 * @author Andrej Podzimek
	 */
	private static interface SCSetter {
		
		/**
		 * Simple setter.
		 * 
		 * @param simple The Simple object to set in either {@code Properties} or {@code Tree}.
		 */
		void setSimple( Simple simple );
		
		/**
		 * Complex setter.
		 * 
		 * @param complex The Complex object to set in either {@code Properties} or {@code Tree}.
		 */
		void setComplex( Complex complex );
	}
	
	/**
	 * A wrapper for the Properties class.
	 * 
	 * @author Andrej Podzimek
	 */
	private static class PropertiesWrapper implements SCSetter {
		
		/** A tree root instance. */
		Properties properties;
		
		/**
		 * Initializes a new wrapper for the supplied Properties instance.
		 * 
		 * @param properties The Properties instance to handle the calls.
		 */
		PropertiesWrapper( Properties properties ) {
			this.properties = properties;
		}

		@Override
		public void setComplex( Complex complex ) {
			properties.setComplex( complex );
		}

		@Override
		public void setSimple( Simple simple ) {
			properties.setSimple( simple );
		}
	}
	
	/**
	 * A wrapper for the Tree class.
	 * 
	 * @author Andrej Podzimek
	 */
	private static class TreeWrapper implements SCSetter {
		
		/** A tree root instance. */
		Tree tree;
		
		/**
		 * Initializes a new wrapper for the supplied Tree instance.
		 * 
		 * @param tree The Tree instance to handle the calls.
		 */
		TreeWrapper( Tree tree ) {
			this.tree = tree;
		}

		@Override
		public void setComplex( Complex complex ) {
			tree.setComplex( complex );
		}

		@Override
		public void setSimple( Simple simple ) {
			tree.setSimple( simple );
		}
	}
	
	/**
	 * Translates the alias definitions used by the Host Manager to a JAXB-based representation.
	 * 
	 * @param alias A software alias from the Host Manger.
	 * @return A JAXB-represented software alias.
	 * @throws ModuleSpecificException When XML integrity errors are detected.
	 */
	public static Alias aliasToAlias( SoftwareAliasDefinition alias )
	throws ModuleSpecificException {
		Alias result;
		String value;
		ChangedString string;
		RestrictionInterface restriction;
		
		result = Factory.PROPERTIES.createAlias();	
		result.setName( alias.getAliasName() );
		
		string = Factory.PROPERTIES.createChangedString();
		string.setValue( alias.getResultName() );
		result.setProduct( string );
		
		if ( null != ( value = alias.getResultVendor() ) ) {
			if ( !value.isEmpty() ) {
				string = Factory.PROPERTIES.createChangedString();
				string.setValue( value );
				result.setVendor( string );
			}
		}
		
		if ( null != ( value = alias.getResultVersion() ) ) {
			if ( !value.isEmpty() ) {
				string = Factory.PROPERTIES.createChangedString();
				string.setValue( value );
				result.setVersion( string );
			}
		}
		
		restriction = alias.getAppRestriction();
		if ( restriction instanceof ObjectRestriction ) {											// Awful!
			result.setAppRestriction( translateRestriction( (ObjectRestriction) restriction ) );
		} else if ( restriction instanceof RSLRestriction ) {										// Blah!
			result.setAppRestriction( translateRestriction( (RSLRestriction) restriction ) );
		} else {
			throw new ModuleSpecificException( Errors.INTG_ALIAS );
		}
		
		restriction = alias.getOsRestriction();
		if ( null != restriction ) {
			if ( restriction instanceof ObjectRestriction ) {										// Awful!
				result.setOSRestriction( translateRestriction( (ObjectRestriction) restriction ) );
			} else if ( restriction instanceof RSLRestriction ) {									// Blah!
				result.setOSRestriction( translateRestriction( (RSLRestriction) restriction ) );
			} else {
				throw new ModuleSpecificException( Errors.INTG_ALIAS );
			}
		}
		
		return result;
	}
	
	/**
	 * Translates a JAXB-based alias representation to the form used by the Host Manager.
	 * 
	 * @param alias A JAXB-based software alias definition.
	 * @return A software alias definition used by the Host Manager.
	 * @throws ModuleSpecificException When XML integrity problems are detected.
	 */
	public static SoftwareAliasDefinition aliasToAlias( Alias alias )
	throws ModuleSpecificException {
		SoftwareAliasDefinition result;
		Restriction restriction;
		
		restriction = alias.getAppRestriction();
		try {
			if ( restriction.isSetRSL() ) {
				try {
					result = new SoftwareAliasDefinition(
						alias.getName(),
						alias.getProduct().getValue(),
						alias.getVendor().getValue(),
						alias.getVersion().getValue(),
						null,
						translateRestriction( restriction.getRSL() )
					);
				} catch ( IllegalArgumentException exception ) {
					throw new ModuleSpecificException( Errors.MALF_RSL_APP, exception );
				}
			} else {
				result = new SoftwareAliasDefinition(
					alias.getName(),
					alias.getProduct().getValue(),
					alias.getVendor().getValue(),
					alias.getVersion().getValue(),
					null,
					translateRestriction( restriction.getObject() )
				);
			}
		} catch ( InvalidArgumentException exception ) {
			throw new ModuleSpecificException( Errors.MALF_ALIAS, exception );
		}
		
		if ( alias.isSetOSRestriction() ) {
			restriction = alias.getOSRestriction();
			if ( restriction.isSetRSL() ) {
				try {
					result.setOsRestriction( translateRestriction( restriction.getRSL() ) );
				} catch ( IllegalArgumentException exception ) {
					throw new ModuleSpecificException( Errors.MALF_RSL_OS, exception );
				}
			} else {
				result.setOsRestriction( translateRestriction( restriction.getObject() ) );
			}
		}
		
		return result; 
	}
	
	/**
	 * Modifies a software alias definition using a JAXB-based template and the {code changed}
	 * attributes.
	 * 
	 * @param template The template with changed parts of the software alias.
	 * @param alias The software alias definition to modify.
	 * @throws ModuleSpecificException When XML integrity problems are detected.
	 */
	public static void modifyAlias( Alias template, SoftwareAliasDefinition alias )
	throws ModuleSpecificException {
		ChangedString value;
		Restriction restriction;
		
		alias.setAliasName( template.getName() );
		if ( ( value = template.getProduct() ).isChanged() ) {										// Can't be null.
			alias.setResultName( value.getValue() );
		}
		if ( template.isSetVendor() ) {																// Can never be null.
			if ( ( value = template.getVendor() ).isChanged() ) {
				alias.setResultVendor( value.getValue() );
			}
		} else {
			alias.setResultVendor( null );
		}
		if ( template.isSetVersion() ) {															// Can never be null.
			if ( ( value = template.getVersion() ).isChanged() ) {
				alias.setResultVersion( value.getValue() );
			}
		} else {
			alias.setResultVersion( null );
		}
		
		restriction = template.getAppRestriction();													// Can never be null.
		if ( restriction.isChanged() ) {
			if ( restriction.isSetRSL() ) {
				try {
					alias.setAppRestriction( translateRestriction( restriction.getRSL() ) );
				} catch ( IllegalArgumentException exception ) {
					throw new ModuleSpecificException( Errors.MALF_RSL_APP, exception );
				}
			} else {
				alias.setAppRestriction( translateRestriction( restriction.getObject() ) );
			}
		}

		if ( template.isSetOSRestriction() ) {														// Caution! Can be null.
			restriction = template.getOSRestriction();
			if ( restriction.isChanged() ) {
				if ( restriction.isSetRSL() ) {
					try {
						alias.setAppRestriction( translateRestriction( restriction.getRSL() ) );
					} catch ( IllegalArgumentException exception ) {
						throw new ModuleSpecificException( Errors.MALF_RSL_OS, exception );
					}
				} else {
					alias.setAppRestriction( translateRestriction( restriction.getObject() ) );
				}
			}
		} else {
			alias.setOsRestriction( null );
		}
	}
	
	/**
	 * Translates a host group representation used by the Host Manager to a JAXB-based form.
	 * 
	 * @param group The group to translate.
	 * @return A JAXB-based instance representing the group.
	 */
	public static Group groupToGroup( HostGroup group ) {
		Group result;
		String value;
		Hosts hosts;
		Host host;
		List< Host > list;
		
		result = Factory.GROUP.createGroup();
		result.setName( group.getName() );
		if ( null != ( value = group.getDescription() ) ) {
			MetaDesc desc;
			
			desc = Factory.GROUP.createMetaDesc();
			desc.setValue( value );
			result.setDesc( desc );
		}
		if ( null != ( value = group.getMetadata() ) ) {
			MetaDesc meta;
			
			meta = Factory.GROUP.createMetaDesc();
			meta.setValue( value );
			result.setMeta( meta );
		}
		hosts = Factory.GROUP.createHosts();
		list = hosts.getHost();
		for ( String name : group.getAllHosts() ) {
			host = Factory.GROUP.createHost();
			host.setName( name );
			list.add( host );
		}
		result.setHosts( hosts );
		
		return result;
	}
	
	/**
	 * Translates a JAXB-based host group representation to a form used by the Host Manager.
	 * 
	 * @param group The JAXB-based group to translate.
	 * @param hostManager A reference to the Host Manager for RSL queries.
	 * @return A host group instance suitable for the Host Manager.
	 * @throws RemoteException When a call to the Host Manager fails.
	 * @throws HostManagerException Whe something bad happens in the Host Manager.
	 * @throws ModuleSpecificException When illegal data is encountered in RSL.
	 */
	public static HostGroup groupToGroup( Group group, HostManagerInterface hostManager ) throws
		RemoteException,
		HostManagerException,
		ModuleSpecificException
	{
		HostGroup result;
		Hosts hosts;
		
		result = new HostGroup( group.getName() );
		if ( group.isSetDesc() ) {
			result.setDescription( group.getDesc().getValue() );
		}
		if ( group.isSetMeta() ) {
			result.setMetadata( group.getMeta().getValue() );
		}
		
		hosts = group.getHosts();
		if ( hosts.isSetRSL() ) {
			for ( HostInfoInterface matchingHost : rslToHosts( hostManager, hosts.getRSL() ) ) {
				result.addHost( matchingHost.getHostName() );
			}
		} else {
			for ( Host host : hosts.getHost() ) {
				result.addHost( host.getName() );
			}
		}
		
		return result;
	}
	
	/**
	 * Modifies a group instance used by the host manager using a JAXB-based group template.
	 * 
	 * @param template The template to read from.
	 * @param group The group to modify.
	 * @param hostManager A reference to the Host Manager.
	 * @throws RemoteException When a call to the Host Manger fails.
	 * @throws HostManagerException When something bad happens in the Host Manager.
	 * @throws ModuleSpecificException When illegal data is encountered in RSL.
	 */
	public static void modifyGroup(
		Group template,
		HostGroup group,
		HostManagerInterface hostManager
	) throws
		RemoteException,
		ModuleSpecificException,
		HostManagerException
	{
		MetaDesc value;
		Hosts hosts;
		
		group.setName( template.getName() );
		if ( template.isSetDesc() ) {
			if ( ( value = template.getDesc() ).isChanged() ) {
				group.setDescription( value.getValue() );
			}
		} else {
			group.setDescription( null );
		}
		if ( template.isSetMeta() ) {
			if ( ( value = template.getMeta() ).isChanged() ) {
				group.setMetadata( value.getValue() );
			}
		} else {
			group.setMetadata( null );
		}
		hosts = template.getHosts();
		if ( hosts.isChanged() ) {
			group.removeAllHosts();
			if ( hosts.isSetRSL() ) {
				for ( HostInfoInterface matchingHost : rslToHosts( hostManager, hosts.getRSL() ) ) {
					group.addHost( matchingHost.getHostName() );
				}
			} else {
				for ( Host host : hosts.getHost() ) {
					group.addHost( host.getName() );
				}
			}
		}
	}
	
	/**
	 * Converts a property tree object used in Host Manager's data structures to a JAXB-based form.
	 * 
	 * @param objtree The object tree to convert.
	 * @return The JAXB_based object representing the property tree.
	 * @throws ModuleSpecificException When XML integrity problems are detected.
	 */
	public static Properties objectToProperties( PropertyTreeReadInterface objtree )
	throws ModuleSpecificException {
		Properties result;
		
		result = Factory.PROPERTIES.createProperties();
		translateSimpleComplex( objtree, new PropertiesWrapper( result ) );
		return result;																				// <remove> ignored here.
	}
	
	/**
	 * Modifies a property tree object used in Host Manager's data structures based on the supplied
	 * JAXB-based template.
	 * 
	 * @param template The template to read from.
	 * @param objtree The property tree to modify.
	 * @throws ModuleSpecificException When errors are detected.
	 */
	public static void modifyProperties(
		Properties template,
		PropertyTreeInterface objtree
	) throws ModuleSpecificException {
		StringBuilder errors;
		
		errors = new StringBuilder();
		if ( template.isSetSimple() ) {
			modifySimple( errors, template.getSimple(), objtree );
		}
		if ( template.isSetComplex() ) {
			modifyComplex( errors, template.getComplex(), objtree );
		}
		if ( 0 < errors.length() ) {
			throw new ModuleSpecificException( Errors.FAIL_MOD_PROP, errors );
		}
	}
	
	/**
	 * Transcodes a RSL restriction to its JAXB-based representation.
	 * 
	 * @param restriction A restriction from the Host Manager.
	 * @return A JAXB-based restriction object.
	 */
	private static Restriction translateRestriction( RSLRestriction restriction ) {
		Restriction result;
		
		result = Factory.PROPERTIES.createRestriction();
		result.setRSL( restriction.getRSLCondition() );
		
		return result;
	}
	
	/**
	 * Transcodes a JAXB-based RSL restriction (in fact just a RSL String) to a restriction
	 * used by the Host Manager.
	 * 
	 * @param rsl An RSL string representning the restriction.
	 * @throws IllegalArgumentException When the RSL parser fails.
	 * @return A restriction object used by the Host Manager.
	 */
	private static RSLRestriction translateRestriction( Condition rsl )
	throws IllegalArgumentException {
		return new RSLRestriction( rsl );
	}
	
	/**
	 * Transcodes an object restriction to its JAXB-based representation.
	 * 
	 * @param restriction A restriction from the Host Manager.
	 * @return A JAXB-based restriction object.
	 * @throws ModuleSpecificException When XML integrity problems are detected.
	 */
	private static Restriction translateRestriction( ObjectRestriction restriction )
	throws ModuleSpecificException {
		Restriction result;
		TheObject object;
		
		result = Factory.PROPERTIES.createRestriction();
		object = Factory.PROPERTIES.createTheObject();
		object.setPath( restriction.getObjectPath() );
		pairsToItems( restriction.getProperties(), object.getItems() );
		result.setObject( object );
		return result;
	}
	
	/**
	 * Translates a JAXB-based object restriction to a restriction used by the Host Manager.
	 * 
	 * @param object A JAXB-based instance representing the restriction.
	 * @return A restriction object used by the Host Manager.
	 * @throws ModuleSpecificException
	 */
	private static ObjectRestriction translateRestriction( TheObject object )
	throws ModuleSpecificException {
		ArrayList< NameValuePair > pairs;
		
		pairs = new ArrayList< NameValuePair >();
		for ( AbstractSerializable item : object.getItems() ) {
			pairs.add( HostNVPConvertor.itemToNVP( item ) );
		}
		return new ObjectRestriction(
			object.getPath(),
			pairs.toArray( new NameValuePair[ pairs.size() ] )
		);
	}
	
	/**
	 * Modifies a property tree object used in Host Manager's data structures based on the supplied
	 * JAXB-based template.
	 * 
	 * @param errors A string builder where error messages can be appended.
	 * @param template The template to read from, a subtree of the properties element.
	 * @param objtree The property tree to modify.
	 */
	private static void modifyTree(
		StringBuilder errors,
		Tree template,
		PropertyTreeInterface objtree
	) {
		if ( template.isSetSimple() ) {
			modifySimple( errors, template.getSimple(), objtree );
		}
		if ( template.isSetComplex() ) {
			modifyComplex( errors, template.getComplex(), objtree );
		}
	}
	
	/**
	 * Modifies the simple (flat) properties in a properties object. Removal requests are handled
	 * first, then new properties are added.
	 * 
	 * @param errors A string builder where error messages can be appended.
	 * @param simple A JAXB-base representation of flat (NVP) properties.
	 * @param objtree The property tree whose properties will be modified.
	 */
	private static void modifySimple(
		StringBuilder errors,
		Simple simple,
		PropertyTreeInterface objtree
	) {
		if ( simple.isSetRemove() ) {
			for ( Remove remove : simple.getRemove() ) {
				try {
					objtree.removeProperty( remove.getName() );
				} catch ( ValueNotFoundException exception ) {
					unknownProperty( errors, remove.getName(), exception.getMessage() );
				} catch ( InvalidArgumentException exception ) {
					illegalProperty( errors, remove.getName(), exception.getMessage() );
				}
			}
		}
		
		if ( simple.isSetProperties() ) {
			NameValuePair nvp;
			
			for ( AbstractSerializable property : simple.getProperties() ) {
				try {
					nvp = HostNVPConvertor.itemToNVP( property );
					try {
						objtree.putProperty( nvp );													// put(). Might be overwriting.
					} catch ( InvalidArgumentException exception ) {
						illegalProperty( errors, nvp.getName(), exception.getMessage() );
					}
				} catch ( ModuleSpecificException exception ) {
					knownException( errors, exception );
				}
			}
		}
	}
	
	/**
	 * Modifies the complex (tree-like) (sub)properties in a properties object. Modification
	 * (and adding) requests are handled first. Then the algorithm proceeds to removal requests.
	 * When indices are present, deletion proceeds in the decreasing order so that the index
	 * consolidation algorithm inside the propert tree doesn't cause unexpected results.
	 *
	 * @param errors A string builder where error messages can be appended.
	 * @param complex Subtrees of a JAXB-based property tree.
	 * @param objtree The property tree representation used by the Host Manager.
	 */
	private static void modifyComplex(
		StringBuilder errors,
		Complex complex,
		PropertyTreeInterface objtree
	) {
		if ( complex.isSetSubtrees() ) {
			for ( Tree subtemplate : complex.getSubtrees() ) {										// First add and/or modify.
				if ( subtemplate.isSetIndex() ) {													// Modify.
					try {
						modifyTree(
							errors,
							subtemplate,
							objtree.getObject( subtemplate.getName(), (int) subtemplate.getIndex() )
						);
					} catch ( ValueNotFoundException exception ) {
						unknownObject( errors, subtemplate.getName(), exception.getMessage() );
					} catch ( InvalidArgumentException exception ) {
						illegalObject( errors, subtemplate.getName(), exception.getMessage() );
					}
				} else {																			// Add.
					translateProperties( errors, subtemplate, objtree );							// HERE! This calls addObject().
				}
			}
		}
		
		if ( complex.isSetRemove() ) {																// Then remove.
			TreeMap< String, TreeSet< Integer > > removalMap;
			TreeSet< Integer > indexSet;
			String name;
			
			removalMap = new TreeMap< String, TreeSet< Integer > >();
			for ( RemoveIndexed remove : complex.getRemove() ) {									// Just prepare the ordering.
				if ( remove.isSetIndex() ) {
					name = remove.getName();
					indexSet = removalMap.get( name );
					if ( null == indexSet ) {
						indexSet = new TreeSet< Integer >();
						indexSet.add( (int) remove.getIndex() );									// Dirty cast.
						removalMap.put( name, indexSet );
					} else {
						indexSet.add( (int) remove.getIndex() );									// Dirty cast.
					}
				} else {																			// Delete where no index.
					try {
						objtree.removeAllOfType( remove.getName() );
					} catch ( ValueNotFoundException exception ) {
						unknownObject( errors, remove.getName(), exception.getMessage() );
					} catch ( InvalidArgumentException exception ) {
						illegalObject( errors, remove.getName(), exception.getMessage() );
					}
				}
			}
			for ( Entry< String, TreeSet< Integer > > entry : removalMap.entrySet() ) {				// Now delete indexed items.
				for ( int idx : entry.getValue().descendingSet() ) {
					try {
						objtree.removeObject( entry.getKey(), idx );
					} catch ( ValueNotFoundException exception ) {
						unknownObject( errors, entry.getKey() + "," + idx, exception.getMessage() );
					} catch ( InvalidArgumentException exception ) {
						illegalObject( errors, entry.getKey() + "," + idx, exception.getMessage() );
					}
				}
			}
		}
	}
	
	/**
	 * Creates a new property tree from a subtree of the JAXB-based representation. This method
	 * does not have any public counterpart. (Simply said, there is no simple way to create
	 * a root properties object from the JAXB representation.)
	 * 
	 * @param errors A string builder where error messages can be appended.
	 * @param template The JAXB-based template to read from.
	 * @param objparent Parent of the new property tree.
	 */
	private static void translateProperties(
		StringBuilder errors,
		Tree template,
		PropertyTreeInterface objparent
	) {
		PropertyTreeInterface objtree;
		
		errors = null;
		objtree = PropertyTreeFactory.create( template.getName(), objparent );						// HERE! This calls addObject().
		
		if ( template.isSetSimple() ) {
			Simple simple;
			
			simple = template.getSimple();
			if ( simple.isSetRemove() ) {
				propertyRemoval( errors, template.getName() );
			}
			if ( simple.isSetProperties() ) {
				NameValuePair nvp;
				
				for ( AbstractSerializable property : simple.getProperties() ) {
					try {
						nvp = HostNVPConvertor.itemToNVP( property );
						try {
							objtree.putProperty( nvp );
						} catch ( InvalidArgumentException exception ) {							// Ugly, but has to be that way.
							illegalObject( errors, nvp.getName(), exception.getMessage() );
						}
					} catch ( ModuleSpecificException exception ) {
						knownException( errors, exception );
					}
				}
			}
		}
		
		if ( template.isSetComplex() ) {
			Complex complex;
			
			complex = template.getComplex();
			if ( complex.isSetRemove() ) {
				subtreeRemoval( errors, template.getName() );
			}
			if ( complex.isSetSubtrees() ) {
				for ( Tree subtemplate : complex.getSubtrees() ) {
					translateProperties( errors, subtemplate, objtree );							// HERE! This calls addObject().
				}
			}
		}
	}
	
	/**
	 * Translates a properties object into a subtree of the JAXB-based property structure.
	 * 
	 * @param objtree The property tree object from the Host Manager.
	 * @return A JAXB-based property subtree representation.
	 * @throws ModuleSpecificException When XML integrity problems or other failures are detected.
	 */
	private static Tree translateProperties( PropertyTreeReadInterface objtree )
	throws ModuleSpecificException {
		Tree result;
		
		result = Factory.PROPERTIES.createTree();
		result.setName( objtree.getTypeName() );
		result.setIndex( objtree.getIndex() );
		translateSimpleComplex( objtree, new TreeWrapper( result ) );
		return result;																				// <remove> ignored here!
	}
	
	/**
	 * Generates JAXB-based objects representing simple (flat) and complex (tree-like) attributes
	 * of a property tree.
	 * 
	 * @param objtree The property tree to read from.
	 * @param result A wrapper containing the JAXB-based tree or subtree to set the properties on.
	 * @throws ModuleSpecificException When XML integrity problems or other failures are detected.
	 */
	private static void translateSimpleComplex( PropertyTreeReadInterface objtree, SCSetter result )
	throws ModuleSpecificException {
		if ( 0 < objtree.getPropertyCount() ) {
			Simple simple;
			
			simple = Factory.PROPERTIES.createSimple();
			pairsToItems( objtree.getProperties(), simple.getProperties() );
			result.setSimple( simple );
		}
		if ( 0 < objtree.getObjectCount() ) {
			Complex complex;
			List< Tree > subtrees;
			
			complex = Factory.PROPERTIES.createComplex();
			subtrees = complex.getSubtrees();
			for ( PropertyTreeReadInterface object : objtree.getObjects() ) {
				subtrees.add( translateProperties( object ) );
			}
			result.setComplex( complex );
		}
	}
	
	/**
	 * Translares a RSL restriction to an array of hosts that meet the restriction.
	 * 
	 * @param hostManager A reference to the Host Manager.
	 * @param rsl The RSL condition in String form.
	 * @return An array of hosts that meet the restriction.
	 * @throws RemoteException When the call to the Host Manager fails.
	 * @throws HostManagerException When something bad happens in the Host Manager.
	 */
	private static HostInfoInterface[] rslToHosts( HostManagerInterface hostManager, Condition rsl )
	throws
		RemoteException,
		HostManagerException,
		ModuleSpecificException
	{
		try {
			return hostManager.queryHosts( new RSLRestriction[] { new RSLRestriction( rsl ) } );
		} catch ( IllegalArgumentException exception ) {
			throw new ModuleSpecificException( Errors.MALF_RSL_GROUP, exception );					// RSLRestriction throws this.
		} catch ( ValueNotFoundException exception ) {
			throw new ModuleSpecificException( Errors.UNKN_PROP, exception );
		} catch ( ValueTypeIncorrectException exception ) {
			throw new ModuleSpecificException( Errors.INVD_TYPE, exception );
		}
	}
	
	/**
	 * Translates name-value pairs used in Host Manager's database to JAXB-based named items.
	 * 
	 * @param pairs The name-value pairs to translate.
	 * @param items A collection where the items should be added.
	 * @throws ModuleSpecificException When XML integrity errors and other failures are detected.
	 */
	private static void pairsToItems(
		NameValuePair[] pairs,
		Collection< AbstractSerializable > items
	) throws ModuleSpecificException {
		for ( NameValuePair pair : pairs ) {
			items.add( HostNVPConvertor.nvpToItem( pair ) );
		}
	}
	
	/**
	 * Translates name-value pairs used in Host Manager's database to JAXB-based named items.
	 * 
	 * @param pairs The name-value pairs to translate.
	 * @param items A collection where the items should be added.
	 * @throws ModuleSpecificException When XML integrity errors and other failures are detected.
	 */
	private static void pairsToItems(
		Iterable< NameValuePair > pairs,
		Collection< AbstractSerializable > items
	) throws ModuleSpecificException {
		for ( NameValuePair pair : pairs ) {
			items.add( HostNVPConvertor.nvpToItem( pair ) );
		}
	}
	
	/**
	 * Generates subtree removal error message.
	 * 
	 * @param builder A string builder, possibly null.
	 * @param identifier The subtree identifier to report.
	 */
	private static void subtreeRemoval( StringBuilder builder, String identifier ) {
		builder
		.append( '\n' ).append( "Subtree removal illegal in new object. (" ).append( identifier )
		.append( ')' );
	}
	
	/**
	 * Generates property removal error message.
	 * 
	 * @param builder A string builder, possibly null.
	 * @param identifier The property identifier to report.
	 */
	private static void propertyRemoval( StringBuilder builder, String identifier ) {
		builder
		.append( '\n' ).append( "Property removal illegal in new object. (" ).append( identifier )
		.append( ')' );
	}
	
	/**
	 * Generates unknown object name error message.
	 * 
	 * @param builder A string builder, possibly null.
	 * @param identifier The object identifier to report.
	 * @param message A message (possibly from an exception) to append.
	 */
	private static void unknownObject(
		StringBuilder builder,
		String identifier,
		String message
	) {
		builder
		.append( '\n' ).append( "Unknown object name. (" ).append( identifier ).append( ")\n> " )
		.append( message );
	}
	
	/**
	 * Generates unknown property name error message.
	 * 
	 * @param builder A string builder, possibly null.
	 * @param identifier The property identifier to report.
	 * @param message A message (possibly from an exception) to append.
	 */
	private static void unknownProperty(
		StringBuilder builder,
		String identifier,
		String message
	) {
		builder
		.append( '\n' ).append( "Unknown property name. (" ).append( identifier ).append( ")\n> " )
		.append( message );
	}
	
	/**
	 * Generates illegal object name error message.
	 * 
	 * @param builder A string builder, possibly null.
	 * @param identifier The object identifier to report.
	 * @param message A message (possibly from an exception) to append.
	 */
	private static void illegalObject(
		StringBuilder builder,
		String identifier,
		String message
	) {
		builder
		.append( '\n' ).append( "Illegal object name. (" ).append( identifier ).append( ")\n> " )
		.append( message );
	}
	
	/**
	 * Generates illegal property name error message.
	 * 
	 * @param builder A string builder, possibly null.
	 * @param identifier The property identifier to report.
	 * @param message A message (possibly from an exception) to append.
	 */
	private static void illegalProperty(
		StringBuilder builder,
		String identifier,
		String message
	) {
		builder
		.append( '\n' ).append( "Illegal property name. (" ).append( identifier ).append( ")\n> " )
		.append( message );
	}
	
	/**
	 * Generates a message based on a known ModuleSpecificException.
	 * 
	 * @param builder The string builder to use.
	 * @param exception The exception to take the error message from.
	 */
	private static void knownException(
		StringBuilder builder,
		ModuleSpecificException exception
	) {
		builder.append( '\n' ).append( exception.getMessage() );
	}
}
