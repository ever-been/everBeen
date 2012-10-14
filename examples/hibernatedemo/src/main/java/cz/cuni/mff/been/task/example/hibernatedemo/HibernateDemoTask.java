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
package cz.cuni.mff.been.task.example.hibernatedemo;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;
import cz.cuni.mff.been.pluggablemodule.hibernate.HibernatePluggableModule;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;


/**
 * <p>Demonstrates using of pluggable modules.</p>
 * 
 * @author Jan Tattermusch
 */
public class HibernateDemoTask extends Job {

    /**
     * Allocates a new <code>PluggableModuleDemoTask</code> object.
     * 
     * @throws TaskInitializationException
     */
    public HibernateDemoTask() throws TaskInitializationException {
        super();
    }

    @Override
    protected void checkRequiredProperties() throws TaskException {

    }
	
    @Override
    protected void run() throws TaskException {

        PluggableModuleManager manager = this.getPluggableModuleManager();
        
        
        DerbyPluggableModule derbyModule = null;
        HibernatePluggableModule hibernateModule = null;
        
        try {
        	derbyModule = 
        		(DerbyPluggableModule) manager.getModule( new PluggableModuleDescriptor( "derby", "2.1.0") );
        	hibernateModule = 
        		(HibernatePluggableModule) manager.getModule( new PluggableModuleDescriptor( "hibernate", "2.1.0") );
        	
        } catch (PluggableModuleException ex) {
            throw new TaskException("Error loading pluggable module.", ex);
        }
        
        try {       
            derbyModule.startEngine("", false);
            
            /* database is initialized by hbm2dll.auto = update,
             * so this line is not needed. */
            //derbyModule.setupDatabase("hibernatedemo", createSetupScriptInputStream() );
        } catch (Exception ex) {
        	throw new TaskException("Error setting up database.", ex);
        }   
        
        //logInfo("Database \"hibernatedemo\" initialized");        
        
        SessionFactory sessionFactory = hibernateModule.createSessionFactory( 
        		"jdbc:derby:hibernatedemo", new String[] { "cz.cuni.mff.been.task.example.hibernatedemo.ExampleEntity" }
        );
        
        Session session = sessionFactory.openSession();
        
        
        ExampleEntity entity1 = new ExampleEntity();
        entity1.setId(new Long(1));
        entity1.setName("entity1");
        
        /* saves entity to database */
        session.merge(entity1);
        
        ExampleEntity entity2 = new ExampleEntity();
        entity2.setId(new Long(2));
        entity2.setName("entity2");
        
        /* saves entity to database */
        session.merge(entity2);
        
        logInfo("2 entities saved to database");
        
        session.flush();
        
        Query query = session.createQuery("from ExampleEntity order by id");
        List<?> results = query.list();
        
        if (results.size() != 2) {
        	throw new TaskException("Failed to load saved data");
        }
        
        if (!((ExampleEntity) results.get(0)).getName().equals("entity1")) {
        	throw new TaskException("Loaded data differ from saved.");
        }
        
        if (!((ExampleEntity) results.get(1)).getName().equals("entity2")) {
        	throw new TaskException("Loaded data differ from saved.");
        }
        
        logInfo("Saved data checked.");
        
        session.close();
        sessionFactory.close();
        
        try {
        	derbyModule.stopEngine();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }

    }
    
    /*
    private InputStream createSetupScriptInputStream() {
    	String setupScript = 
    		"create table ExampleEntity ("+
    		" id BIGINT PRIMARY KEY," +
    		" name VARCHAR(100) );\n";
    		
    	try {
    		return new ByteArrayInputStream( setupScript.getBytes("UTF-8") );
    	} catch(Exception e) {
    		return null;
    	}
    }*/
}
