//
//  CoreConfiguration.java
//  CoreFramework
//
//  Created by Gavin Eadie on 7/1/05.
//  Copyright 2005 Ramsay Consulting. All rights reserved.
//

/**
 * CoreConfiguration.java -- Project CoreFramework
 */

package com.ramsayconz.wocore;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOResourceManager;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSPropertyListSerialization;

@SuppressWarnings("serial")
public class CoreConfiguration extends CombinedConfiguration implements NSKeyValueCodingAdditions {
    private static final Logger     logger = Logger.getLogger (CoreConfiguration.class);

    public CoreConfiguration(String fileName) {
        super ();

        WOResourceManager      	 	rm = WOApplication.application().resourceManager();
        java.net.URL            	configURL = rm.pathURLForResourceNamed (fileName, null, null);

        if (null != configURL) {
            try {
                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                builder.clearErrorListeners();
                builder.setURL(configURL);
                this.clear();
                this.addConfiguration((AbstractConfiguration)builder.getConfiguration());
            }
            catch (ConfigurationException x) {
                logger.error("configFactory.cfgException -- FAILURE: " + x);
            }
            catch (Exception x) {
                logger.error("configFactory.othException -- FAILURE: " + x);
            }
        }
        else
            logger.error("configFactory: missing resource file: " + fileName);
    }

    /**
     *  alphaDump
     */
    public void alphaDump (boolean sort) {        
        Vector<String>			allKeys = keySet();
        if (sort)
            Collections.sort (allKeys, String.CASE_INSENSITIVE_ORDER);

        if (!allKeys.isEmpty()) {
            logger.info ((sort) ? "+------------------------ alphabetized configuration dump ----" :
                                  "+------------------------------------- configuration dump ----");
        }
        
        for (String key : allKeys) {
        	logger.info ("|  " + key + " = " + getString(key));
        }
        logger.info ("+-------------------------------------------------------------");    
    }

    @SuppressWarnings("unchecked")
	private Vector<String> keySet() {
        Vector<String> 			newKeys = new Vector<String>();
        
        Iterator<String> 		oldKeys = getKeys();
        while (oldKeys.hasNext()) {
        	String 				key = oldKeys.next();
            if (!newKeys.contains(key)) {
                newKeys.add(key);
            }
        }
        
        return newKeys;
    }

    
    /**
     *	CoreConfiguration accessors --------------------------------------------------
     *
     *		Boolean <- getBoolean(key, default_value) --
     *		Integer <- getInteger(key, default_value) --
     *              <- getNSArray(key)
     *              <- getNSDictionary(key)
     */

/*    
    public Vector getVector(String key, String def) {
        Vector ve = new Vector();
        String vString = getString(key, def);
		StringTokenizer st = new StringTokenizer (vString, " ,\t\r\n");
		while (st.hasMoreTokens())
			ve.addElement (st.nextToken());
        return ve;
    }
*/    
    public boolean getBoolean(String key, String def) {
        return NSPropertyListSerialization.booleanForString (getString(key, def));
    }
    
    public int getInteger(String key, String def) {
        return NSPropertyListSerialization.intForString (getString(key, def));
    }
    
    @SuppressWarnings("unchecked")
	public NSArray<String> getNSArray(String key, String def) {
        return NSPropertyListSerialization.arrayForString (getString(key, def));
    }
    
    @SuppressWarnings("unchecked")
	public NSDictionary<String, ?> getNSDictionary(String key, String def) {
        return NSPropertyListSerialization.dictionaryForString (getString(key, def));
    }

/* ---------------------------------------- ... implements NSKeyValueCodingAdditions - */

    public Object valueForKey(java.lang.String key) {
        String      v = this.getString(key);
        logger.debug("valueForKey(" + key + ")");
        if (null == v) { 
            logger.fatal("valueForKey(" + key + ")");
            throw new NSKeyValueCodingAdditions.UnknownKeyException ("CoreConfiguration missing valueForKey(" + key + ")", null, key);
        }
		return v;
    }
    
    public void takeValueForKey(java.lang.Object object, java.lang.String key) {
        logger.debug("takeValueForKey(" + (String)object + ", " + key + ")");
        throw new java.lang.UnsupportedOperationException("cannot set properties with takeValueForKey");
    }

    public Object valueForKeyPath(java.lang.String key) {
        String      v = this.getString(key);
        logger.debug("valueForKeyPath(" + key + ")");
        if (null == v) { 
            logger.fatal("valueForKeyPath(" + key + ")");
            throw new NSKeyValueCodingAdditions.UnknownKeyException ("CoreConfiguration missing valueForKeyPath(" + key + ")", null, key);
        }
		return v;
    }
    
    public void takeValueForKeyPath(java.lang.Object object, java.lang.String key) {
        logger.debug("takeValueForKeyPath(" + (String)object + ", " + key + ")");
        throw new java.lang.UnsupportedOperationException("cannot set properties with takeValueForKeyPath");
    }
    
//    public static boolean canAccessFieldsDirectly()  {
//        logger.debug("canAccessFieldsDirectly()");
//        return true;
//    }
}
