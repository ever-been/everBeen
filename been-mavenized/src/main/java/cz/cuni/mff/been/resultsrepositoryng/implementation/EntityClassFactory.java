/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.resultsrepositoryng.implementation;

import java.util.UUID;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.CodeVisitor;
import org.objectweb.asm.Constants;
import org.objectweb.asm.attrs.Annotation;
import org.objectweb.asm.attrs.RuntimeVisibleAnnotations;

import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;

/**
 * Generates entity classes from dataset descriptor
 * (internally uses ASM to generate new classes' bytecode).
 * 
 * @author Jan Tattermusch
 *
 */
class EntityClassFactory {
	
	/**
	 * Package to which will be the new entity classes generated
	 */
	public static final String ENTITY_CLASS_PACKAGE = "cz/cuni/mff/been/resultsrepositoryng/implementation/dynamicentity";
	
	/**
	 * Base class name for all entity classes (generated classes will extend this class
	 */
	public static final String ENTITY_SUPERCLASS = "cz/cuni/mff/been/resultsrepositoryng/implementation/DynamicEntity";
	
	/**
	 * Classloader that remembers all generated classes until it's reset.
	 */
	private static EntityClassLoader entityClassLoader = new EntityClassLoader();
	
	
	/**
	 * Based on dataset definition, generate class containing appropriate fields.
	 * Also adds hibernate annotations to class and fields to make it accessible
	 * using hibernate.
	 * Resulting class is automatically registered by entity class loader.
	 *
	 * @param dataset dataset definition
	 * @return generated class
	 */
	public static Class<?> generateEntityClass(RRDataset dataset) {
		String className = getEntityClassName(dataset.getName());
		String tableName = getEntityTableName(dataset.getAnalysis(), dataset.getName());
		byte[] bytecode = generateBytecode(className, tableName, dataset.getDatasetDescriptor());
		
		Class<?> clazz = entityClassLoader.loadClass(bytecode);
		return clazz;
		
	}

	/**
	 * Returns class loader that remembers all generated entity classes since
	 * its last reset. 
	 * @return entity class loader
	 */
	public static ClassLoader getEntityClassLoader() {
		return entityClassLoader;
	}
	
	/**
	 * Resets entity class loader (trash all generated classes)
	 */
	public static void resetEntityClassLoader() {
		entityClassLoader = new EntityClassLoader();
	}
	
	/**
	 * Generates name of entity class from dataset name.
	 * To prevent class name clash when trying to reuse the 
	 * same dataset name again, a random UUID suffix is 
	 * added to entity class name.
	 *  
	 * @param datasetName dataset name
	 * @return entity class name
	 */
	private static String getEntityClassName(String datasetName) {
		String uuidString = UUID.randomUUID().toString();
		uuidString = uuidString.replace('-', '_');
		return datasetName + '_' + uuidString;
	}
	
	/**
	 * Generates name of table to store given entity.
	 * Has to return the same table name everytime called
	 * with the same arguments.
	 * @param analysisName name of analysis to which dataset belongs to
	 * @param datasetName dataset name
	 * @return entity table name
	 */
	private static String getEntityTableName(String analysisName, String datasetName) {
		return analysisName + "_" + datasetName;
	}
	
	/**
	 * Generates entity class' bytecode (fields and annotation)
	 * @param className name of entity's class
	 * @param tableName name of entity's table
	 * @param descriptor dataset descriptor
	 * @return class' bytecode
	 */
	@SuppressWarnings("unchecked")
	private static byte[] generateBytecode(String className, String tableName,  DatasetDescriptor descriptor) {
		ClassWriter cw = new ClassWriter(false);
		//cw.visvisitAnnotation
		
		cw.visit( Constants.V1_5 , Constants.ACC_PUBLIC,
			ENTITY_CLASS_PACKAGE + "/" + className, // class name 
		    ENTITY_SUPERCLASS, // super class
		    null,               // interfaces
		    className + ".java");   // source file
		
		
		/* Add javax.persistence.Entity annotation */
		RuntimeVisibleAnnotations attr = new RuntimeVisibleAnnotations();
		Annotation entityAnnotation = new Annotation("Ljavax/persistence/Entity;");
		attr.annotations.add(entityAnnotation);
			
		Annotation tableAnnotation = new Annotation("Ljavax/persistence/Table;");
		tableAnnotation.add("name", tableName);
		attr.annotations.add(tableAnnotation);
			
		cw.visitAttribute(attr);
		
		
		{
			/* Add constructor */
			CodeVisitor cv;
			cv = cw.visitMethod(Constants.ACC_PUBLIC, "<init>", "()V", null, null);
			cv.visitVarInsn(Constants.ALOAD, 0);
			cv.visitMethodInsn(Constants.INVOKESPECIAL, ENTITY_SUPERCLASS, "<init>", "()V");
			cv.visitInsn(Constants.RETURN);
			cv.visitMaxs(1, 1);
		}	
		
		for(String tagName : descriptor.tags()) {
			visitTag(cw, tableName, tagName, descriptor.get(tagName), descriptor.idTags().contains(tagName));
		}
		
		
		{
			/* add special purpose serial field */
			RuntimeVisibleAnnotations at = new RuntimeVisibleAnnotations();
			Annotation idAnnotation = new Annotation("Ljavax/persistence/Id;");
			at.annotations.add(idAnnotation);
			Annotation indexAnnotation = getIndexAnnotation(tableName, DynamicEntity.SERIAL_FIELD_NAME);
			at.annotations.add(indexAnnotation);
		
			cw.visitField( Constants.ACC_PUBLIC, 
					DynamicEntity.SERIAL_FIELD_NAME,  
					"Ljava/lang/Long;",  
					null, 
					at);
			
		}
		cw.visitEnd();
		return cw.toByteArray();
		
	}
	
	/**
	 * Adds public field to given class.
	 * 
	 * Adds appropriate annotation according to field's data type.
	 * 
	 * @param classWriter
	 */
	private static void visitTag(ClassWriter classWriter, String tableName, String fieldName, DataHandle.DataType type, boolean isInKey) {
		Attribute columnAttr = getAnnotationAttr("Ljavax/persistence/Column;");
		Attribute lobAttr = getLobAttr();
		Attribute idIndexAttr = getIndexAttr(tableName,fieldName);
		String signature = type.getJavaSignature();
		
		Attribute attr;
		if (isInKey) {
			attr = idIndexAttr;
		} else
		if (type.isPersistSerialized()) {
			/* fields that cannot be represented directly by a basic java type
			 * will be saved as serialized data handles.
			 */
			attr = lobAttr;
		} else {
			attr = columnAttr;
		}
		
		classWriter.visitField( Constants.ACC_PUBLIC, 
				fieldName,  
				signature,  
				null, 
				attr);
	}
	
	/**
	 * Returns bytecode attribute containing annotation of given type
	 * @param signature annotation type's signature
	 * @return bytecode attribute
	 */
	@SuppressWarnings("unchecked")
	private static Attribute getAnnotationAttr(String signature) {
		RuntimeVisibleAnnotations attr = new RuntimeVisibleAnnotations();
		Annotation annotation = new Annotation(signature);
		attr.annotations.add(annotation);
		return attr;
	}
	
	/**
	 * Returns bytecode attribute with @Index annotation.
	 */
	@SuppressWarnings("unchecked")
	private static Attribute getIndexAttr(String tableName, String fieldName) {
		RuntimeVisibleAnnotations attr = new RuntimeVisibleAnnotations();
		Annotation columnAnnotation = new Annotation("Ljavax/persistence/Column;");
		attr.annotations.add(columnAnnotation);
		Annotation indexAnnotation = getIndexAnnotation(tableName, fieldName);
		attr.annotations.add(indexAnnotation);
		return attr;
	}
	
//	/**
//	 * Returns bytecode attribute with @Id annotation.
//	 */
//	@SuppressWarnings("unchecked")
//	private static Attribute getIdAttr(String tableName, String fieldName) {
//		RuntimeVisibleAnnotations attr = new RuntimeVisibleAnnotations();
//		Annotation idAnnotation = new Annotation("Ljavax/persistence/Id;");
//		attr.annotations.add(idAnnotation);
//		return attr;
//	}
	
	/**
	 * Returns bytecode attribute with @Lob and @Column annotations.
	 */
	@SuppressWarnings("unchecked")
	private static Attribute getLobAttr() {
		RuntimeVisibleAnnotations attr = new RuntimeVisibleAnnotations();
		Annotation lobAnnotation = new Annotation("Ljavax/persistence/Lob;");
		attr.annotations.add(lobAnnotation);
		Annotation columnAnnotation = new Annotation("Ljavax/persistence/Column;");
		columnAnnotation.add("length", DynamicEntity.LOB_MAX_LENGTH);
		attr.annotations.add(columnAnnotation);
		return attr;
	}
	
	private static Annotation getIndexAnnotation(String tableName, String fieldName) {
		Annotation indexAnnotation = new Annotation("Lorg/hibernate/annotations/Index;");
		indexAnnotation.add("name", DynamicEntity.INDEX_NAME_PREFIX +"_"+tableName +"_" + fieldName);
		return indexAnnotation;
	}
	
	/**
	 * Helper class loader,
	 * builds Class instance from bytecode. 
	 * 
	 * @author Jan Tattermusch
	 *
	 */
	private static class EntityClassLoader extends ClassLoader {
		
		public Class<?> loadClass(byte[] bytecode) {
			Class<?> result = this.defineClass(null, bytecode, 0, bytecode.length, null);
			this.resolveClass(result);
			return result;
		}
	}

}
