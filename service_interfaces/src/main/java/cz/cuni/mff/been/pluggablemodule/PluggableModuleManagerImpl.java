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
package cz.cuni.mff.been.pluggablemodule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import cz.cuni.mff.been.hostruntime.TasksPortInterface;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.pmc.ClassPathItems;
import cz.cuni.mff.been.jaxb.pmc.Dependencies;
import cz.cuni.mff.been.jaxb.pmc.Dependency;
import cz.cuni.mff.been.jaxb.pmc.Java;
import cz.cuni.mff.been.jaxb.pmc.PluggableModuleConfiguration;
import cz.cuni.mff.been.pluggablemodule.jaxb.SelfContainedParser;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.utils.FileUtils;

/**
 * Provides the pluggable module functionality (basic work with pluggable 
 * modules - retrieving & instantiating).
 * 
 * @author Jan Tattermusch
 */
public class PluggableModuleManagerImpl implements PluggableModuleManager {

    /** Name of pluggable module's config file. */
    public static final String CONFIG_FILE_NAME = "config.xml";
    
    /** An XML parser for pluggable module configuration files. */
    private final BindingParser<PluggableModuleConfiguration> parser;
    
    /** Sets up a class logger */
    private final Logger logger = Logger.getLogger("task." + this.getClass().getCanonicalName());
    
    /** Registry of loaded modules */
    private final PluggableModuleRegistry registry = new PluggableModuleRegistry();
    
    private final String pluggableModulesPath;
    private final TasksPortInterface tasksPortInterface;
    
    /** ClassLoader used to load pluggable module classes */
    private final PluggableModuleClassLoader classLoader = new PluggableModuleClassLoader();

    /**
     * Creates a new pluggable module manager for a hostruntime.
     * Retrieved modules will be stored in given path.
     * Only one instance of pluggable module manager should be 
     * created in an aplication.
     * 
     * @param tasksPortInterface hostruntime
     * @param pluggableModulesPath path where to store extracted module packages.
     */
    public PluggableModuleManagerImpl(TasksPortInterface tasksPortInterface, String pluggableModulesPath)
    throws PluggableModuleException {
    	try {
    		parser = new SelfContainedParser();
    	} catch (JAXBException exception) {
    		throw new PluggableModuleException("Failed to initialize the XML parser", exception);
    	}
        this.tasksPortInterface = tasksPortInterface;
        this.pluggableModulesPath = pluggableModulesPath;
    }

    /* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.PluggableModuleMan#getModule(cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor)
	 */
    public PluggableModule getModule(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {

        PluggableModule module;
        if (registry.isRegistered(moduleDescriptor)) {
            module = registry.getModule(moduleDescriptor);
        } else {
            module = loadModule(moduleDescriptor);
        }
        return module;
    }


    /* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.PluggableModuleMan#loadModule(cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor)
	 */
    public PluggableModule loadModule(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {

        return loadModule(moduleDescriptor, new HashSet<PluggableModuleDescriptor>() );
    }

    /**
     * Gets pluggable module package from software repository (or package cache),
     * adds its jars to system classpath and instatiates an appropriate
     * class that extends pluggable module.
     * Method also resolves dependencies by recursively calling itself. For detecting
     * circular dependencies, <code>loadingSet</code> structure is used.
     * @param moduleDescriptor descriptor of module to load
     * @param loadingSet set of pluggable descriptors which are waiting for its prerequisites.
     * @return instance of requested pluggable module
     * @throws cz.cuni.mff.been.pluggablemodule.PluggableModuleException
     */
    private PluggableModule loadModule(PluggableModuleDescriptor moduleDescriptor, Set<PluggableModuleDescriptor> loadingSet) throws PluggableModuleException {

        if (registry.isRegistered(moduleDescriptor)) {
            throw new PluggableModuleException("Module with descriptor " + moduleDescriptor + " is already registered.");
        }

        extractModule(moduleDescriptor);

        PluggableModuleConfiguration config = readModuleConfiguration(moduleDescriptor);

        if (!loadingSet.contains(moduleDescriptor)) {
            loadingSet.add(moduleDescriptor);
        } else {
            throw new PluggableModuleException("Circular dependency detected when loading "+ moduleDescriptor.getName() + " module.");
        }
        
        if (config.isSetDependencies()) {
        	Dependencies dependencies = config.getDependencies();
        	if (dependencies.isSetDependency()) {
        		for (Dependency dependency : dependencies.getDependency()) {
        			PluggableModuleDescriptor descriptor = new PluggableModuleDescriptor(dependency);
        			if (registry.isRegistered(descriptor)) {
        				continue;
        			} else {
        				logger.info(
        					"Module \"" + dependency.getModuleName() +
        					"\" will be loaded as a prerequisite."
        				);
        				loadModule(descriptor, loadingSet);
        			}
        		}
        	}
        }

        Class<PluggableModule> moduleClass = loadModulesClasses(moduleDescriptor, config);

        PluggableModule module = initializeModule(moduleClass);

        loadingSet.remove(moduleDescriptor);

        registry.registerModule(moduleDescriptor, module);

        logger.info("Module \"" + moduleDescriptor.getName() + "\" loaded successfully.");

        return module;
    }

    /**
     * Extends system classpath by items specified
     * in module's configuration file.
     *
     * @param moduleDescriptor pluggable module descriptor
     * @param config module's configuration
     * @return class object representing module's main class.
     * @throws cz.cuni.mff.been.pluggablemodule.PluggableModuleException when something goes wrong.
     */
    @SuppressWarnings("unchecked")
    private Class<PluggableModule> loadModulesClasses(PluggableModuleDescriptor moduleDescriptor, PluggableModuleConfiguration config) throws PluggableModuleException {
    	Java java = config.getJava();																// Mandatory, must be set.
    	try {
    		if (java.isSetClasspathItems()) {
    			ClassPathItems classPathItems = java.getClasspathItems();
    			if (classPathItems.isSetClasspathItem()) {
    				for (String filename : classPathItems.getClasspathItem()) {
    					File file = new File(getModulePath(moduleDescriptor) + File.separator + filename);
    					if (!file.exists()) {
    						throw new PluggableModuleException(
    							"Tried to add nonexistent file to system classpath."
    						);
    					}
    					loadJarFile(file);
    				}
    			}
    		}
        } catch (PluggableModuleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PluggableModuleException("Error loading pluggable module's classes.", ex);
        }

        Class<PluggableModule> moduleClass;
        try {
            moduleClass = (Class<PluggableModule>) classLoader.loadClass(java.getMainClass());
        } catch (Exception ex) {
            throw new PluggableModuleException("Error loading pluggable module's main class.", ex);
        }
        return moduleClass;
    }
    
    /**
     * Adds jar's URL to classloader managed by this object.
     * @throws MalformedURLException 
     * 
     */
    private void loadJarFile(File file) throws MalformedURLException {
    	
    		String fileURL = "file:" + file.getAbsolutePath();
    		classLoader.addURL( new URL(fileURL) );	
        
    }

    /**
     * Creates instance of pluggable module and runs doStart on it.
     *
     * @param moduleClass module main class' class object
     * @return initialized pluggable module
     */
    private PluggableModule initializeModule(Class<PluggableModule> moduleClass) throws PluggableModuleException {
        PluggableModule module;
        try {
        	Constructor<PluggableModule> constructor = moduleClass.getConstructor( new Class<?> [] {PluggableModuleManager.class } );
            module = constructor.newInstance(new Object[] { this });
        	//module = moduleClass.newInstance(  );
            module.doStart();
        } catch (Exception ex) {
            throw new PluggableModuleException("Error initializing pluggable module.", ex);
        }
        return module;
    }

    /** 
     * Extract module to an appropriate path.
     * @param moduleDescriptor module to extract
     */
    private void extractModule(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {
        try {
            String modulePath = getModulePath(moduleDescriptor);

            mkdirsIfNotExists(modulePath);

            tasksPortInterface.extractPackage(moduleDescriptor.getName(),moduleDescriptor.getVersion(), modulePath, PackageType.MODULE);

            chmodFilesRecursively(modulePath);
        } catch (Exception ex) {
            throw new PluggableModuleException("Error retrieving package for module \"" + moduleDescriptor.getName() + "\".", ex);
        }
    }

    /**
     * Returns a directory path where module with descriptor name should be stored.
     * @param moduleDescriptor module's descriptor
     * @return path where to extract module files
     */
    private String getModulePath(PluggableModuleDescriptor moduleDescriptor) {
        return pluggableModulesPath + File.separator + moduleDescriptor.getName() + '-' + moduleDescriptor.getVersion();
    }

    /**
     * Returns a file object pointing to module's configuration file.
     * @param moduleDescriptor module descriptor
     * @return file with module's configuration.
     */
    private File getModuleConfigFile(PluggableModuleDescriptor moduleDescriptor) {
        String path = getModulePath(moduleDescriptor) + File.separator + CONFIG_FILE_NAME;
        return new File(path);
    }

    /**
     * Creates specified directory, if it does not already exist, including any
     * necessary but nonexistent parent directories.
     * 
     * @param dir directory to create
     * @throws IOException if the directory does not exist and can not be created 
     */
    private void mkdirsIfNotExists(String dir) throws IOException {
        File f = new File(dir);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                throw new IOException("Can not create directory \"" + dir + "\".");
            }
        }
    }

    /**
     * Sets unix mode -rwxr--r-- for directory and all its descendants recursively
     * 
     * @param dir directory
     * @throws cz.cuni.mff.been.pluggablemodule.PluggableModuleException if error occurs.
     */
    private void chmodFilesRecursively(String dir) throws PluggableModuleException {
        try {
        	FileUtils.recursiveChmod(new File(dir), "-rwxr--r--");
        } catch (IOException e) {
            throw new PluggableModuleException("Error setting access rights on pluggable module files.", e);
        }
    }

    /**
     * Reads module configuration from file.
     * @param moduleDescriptor pluggable module descriptor
     * @return pluggable module configuration object
     * @throws cz.cuni.mff.been.pluggablemodule.PluggableModuleException when something goes wrong.
     */
    private PluggableModuleConfiguration readModuleConfiguration(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {
        PluggableModuleConfiguration config;
        try {
            File configFile = getModuleConfigFile(moduleDescriptor);
            config = parser.parse(configFile);
        } catch (Exception ex) {
            throw new PluggableModuleException("Error reading pluggable module's configuration", ex);
        }
        return config;
    }

    /* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.PluggableModuleMan#isModuleLoaded(cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor)
	 */
    public boolean isModuleLoaded(PluggableModuleDescriptor moduleDescriptor) {
        return registry.isRegistered(moduleDescriptor);
    }
    
    /* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.PluggableModuleMan#getClassLoader()
	 */

	public ClassLoader getClassLoader() {
		return classLoader;
	}

}
