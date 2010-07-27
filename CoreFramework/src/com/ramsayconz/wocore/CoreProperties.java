package com.ramsayconz.wocore;

//
//  CoreProperties.java
//  CoreFramework
//
//  Created by Gavin Eadie on 6/25/05.
//  Copyright 2005 Ramsay Consulting. All rights reserved.
//

/**
 * CoreProperties.java -- Project CoreFramework
 * 
 *   CoreProperties is, essentially, java.util.Properties with Key Value Coding.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSPropertyListSerialization;

/**
 * CoreProperties is a superclass of the regular Java Properties class with the addition of the
 * WebObjects key-value coding interface.  This allows WebObjects components to access properties
 * without explicit access methods.
 * 
 * @author gavin
 */
public class CoreProperties extends Properties implements NSKeyValueCoding {
	/**
	 * 
	 */
	private static final long 		serialVersionUID = -7874906029089504346L;
	private static final Logger     logger = Logger.getLogger (CoreProperties.class);
    
    /**
     *	CoreProperties constructors --------------------------------------------------
     *
     *		CoreProperties() -- creates an empty CoreProperties table
     *		CoreProperties(props) -- creates a CoreProperties table with given Properties
     *		CoreProperties(file) -- creates a new CoreProperties table from file
     *		CoreProperties(string) -- creates a new CoreProperties table from file 'string'
     */
    public CoreProperties() {
        super ();
    }
    
    public CoreProperties(Properties props) {
        super ();
        moreProperties(props);
    }
    
    public CoreProperties(String fileName) {
        super ();
        try {
            moreProperties (new BufferedInputStream(new FileInputStream(new File(fileName))));
        }
        catch (Exception x) {
            logger.error("     CoreProperties(" + fileName + ") error: " + x);
        }
    }
    
    public CoreProperties(File file) {
        super ();
        try {
            moreProperties (new BufferedInputStream(new FileInputStream(file)));
        }
        catch (Exception x) {
            logger.error("     CoreProperties(" + file.getName() + ") error: " + x);
        }
    }
    
    /**
     *	CoreProperties methods --------------------------------------------------
     *
     *		moreProperties(props) -- add to this CoreProperties from other props
     *		moreProperties(stream) -- add to this CoreProperties from stream
     */
    public void moreProperties(Properties props) {
        if (props != null) {
            Enumeration<?> e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                setProperty(key, props.getProperty(key));
            }
        }
    }

    public void moreProperties(InputStream propStream) {
    	moreProperties(propStream, true);
    }

    public void moreProperties(InputStream propStream, boolean closeStream) {
    	try {
    		propStream.mark(99999);
            load(propStream);
            if (closeStream) {
            	propStream.close();
            }
            else {
            	propStream.reset();
            }
    	}
        catch (Exception x) {
            logger.error("     CoreProperties(InputStream) error: " + x);
        }
    }
    
    /**
     *	CoreProperties accessors --------------------------------------------------
     *
     *		String <- getString(key, [default_value]) --
     *		int <- getInteger(key, default_value) --
     *		boolean <- getBoolean(key, default_value) --
     *		vector <- getVector(key, default_value) --
     */
    public String getString (String key) {
        return getProperty(key);
    }
    
    public String getString (String key, String def) {
        return getProperty(key, def);
    }
    
    public boolean getBoolean(String key) {
		return NSPropertyListSerialization.booleanForString(getProperty(key));
	}

	public boolean getBoolean(String key, String def) {
		return NSPropertyListSerialization.booleanForString(getProperty(key, def));
	}

	public int getInt(String key, String def) {
		return NSPropertyListSerialization.intForString(getProperty(key, def));
	}

	/**
	 * This WILL return an NSArray ...
	 * 
	 * property is empty ==> array.count() == 0 (empty array) 
	 * property fails parse ==> array.count() == 0 (empty array)
	 * property parse succeeds ==> array.count() is whatever the parse gives
	 * property not empty, parse succeeds, but array is empty ==> array with property as the one element.
	 * 
	 * The property string must be in the 'standard' format: ("xxxxx", "yyyyy", "zzzzz") ...
	 * 
	 * @param key
	 * @param def
	 * @return NSArray<String>
	 */
    @SuppressWarnings("unchecked")
	public NSArray<String> getNSArray(String key, String def) {
    	NSArray<String>					array;
    	String							propText = getProperty(key, def);
    	if (propText.length() == 0) {
    		return new NSArray<String>();
    	}
    	try {
    		array = NSPropertyListSerialization.arrayForString(propText);
    		if (array.count() > 0) {
    			return array;
    		}
    		return new NSArray<String>(propText);
    	}
    	catch (Exception x) {
    		return new NSArray<String>();
    	}
    }

    @SuppressWarnings("unchecked")
	public NSDictionary<String, ?> getNSDictionary(String key, String def) {
        return NSPropertyListSerialization.dictionaryForString (getProperty(key, def));
    }
    
    public Vector<String> getVector(String key, String def) {
        Vector<String>				ve = new Vector<String>();
		StringTokenizer 			st = new StringTokenizer (getProperty(key, def), " ,\t\r\n");
		while (st.hasMoreTokens())
			ve.addElement (st.nextToken());
        return ve;
    }
    
    /**
     *
     */

    public void alphaDump(boolean sort) {
        Vector<String>		      	allKeys = allStringKeys();
        if (sort)
            Collections.sort (allKeys, String.CASE_INSENSITIVE_ORDER);
        
        if (!allKeys.isEmpty()) {
            logger.info ((sort) ? "+--------------------------- alphabetized properties dump ----" :
                                  "+---------------------------------------- properties dump ----");
        }

        for (String key : allKeys) {
            logger.info ("|  " + key + " = " + getProperty(key));
        }
        logger.info ("+-------------------------------------------------------------");
    }

    private Vector<String> allStringKeys() {
    	Vector<String>				stringKeys = new Vector<String>(200);
    	Iterator<Object>			objectKeys = keySet().iterator();
    	while (objectKeys.hasNext()) {
    		Object objectKey = objectKeys.next();
    		if (objectKey instanceof String) {
    			stringKeys.add((String)objectKey);
			}
    	}
    	return stringKeys;
    }

    /* ----------------------------------------------------------------------------------- */

    public Object valueForKey(java.lang.String key) {
        logger.trace("valueForKey(" + key + ")");
        String      v = getProperty(key);
        if (null == v) { 
            logger.fatal("valueForKey(" + key + ")");
            throw new NSKeyValueCoding.UnknownKeyException ("CoreProperties missing valueForKey(" + key + ")", null, key);
        }
		return v;
    }
    
    public void takeValueForKey(java.lang.Object object, java.lang.String key) {
        logger.trace("takeValueForKey(" + (String)object + ", " + key + ")");
        throw new java.lang.UnsupportedOperationException("cannot set properties with takeValueForKey");
    }
    
    public Object valueForKeyPath(java.lang.String key) {
        logger.trace("valueForKeyPath(" + key + ")");
        String      v = getProperty(key);
        if (null == v) { 
            logger.fatal("valueForKeyPath(" + key + ")");
            throw new NSKeyValueCoding.UnknownKeyException ("CoreProperties missing valueForKeyPath(" + key + ")", null, key);
        }
		return v;
    }

    public void takeValueForKeyPath(java.lang.Object object, java.lang.String key) {
        logger.trace("takeValueForKeyPath(" + (String)object + ", " + key + ")");
        throw new java.lang.UnsupportedOperationException("cannot set properties with takeValueForKeyPath");
    }
}
