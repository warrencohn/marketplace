//
//  ResetManager.java
//  CoreFramework
//
//  Created by Gavin Eadie on 10/26/05.
//  Copyright (c) 2005 Ramsay Consulting. All rights reserved.
//

package com.ramsayconz.wocore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * This class provides functionality to enable the automatic reset of ivars in stateless components. Simply make sure
 * all your stateless component ivars that you wish to reset are protected, not primitive and start with underscore
 * character.
 * 
 * In your reset method simply call: ResetManager.reset(this);
 */

public class ResetManager {
    private static final Logger     logger = Logger.getLogger(ResetManager.class);
    
    /**
	 * A dictionary where each entry key is the name of a stateless component class and the value is an array of type
	 * Field that should be reset automatically.
	 */
    @SuppressWarnings("rawtypes")
	protected static NSMutableDictionary    _statelessComponentFieldLibrary = new NSMutableDictionary();
    
    @SuppressWarnings("rawtypes")
	public static NSMutableDictionary getStatelessComponentFieldLibrary() {
        return _statelessComponentFieldLibrary;
    }
    
    /** Sets the NSArray of Field for the component. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setFieldsForComponent(NSArray fields, WOComponent component) {
        _statelessComponentFieldLibrary.setObjectForKey(fields, component.getClass().getName());
    }
    
    /** @return NSArray of Field for the component. */
    @SuppressWarnings({ "rawtypes" })
	public static NSArray fieldsForComponent(WOComponent component) {
        NSArray array = (NSArray)_statelessComponentFieldLibrary.valueForKey( component.getClass().getName() );
        if (array == null) {
            array = fieldsToReset(component);           // Calculate the Field array
            setFieldsForComponent(array, component);    // Cache it
        }
        return array;
    }
    
    /** resets ivars in the component argument */
	@SuppressWarnings("unchecked")
	public static void reset(WOComponent component) {
		NSArray<Field> resettableFields = ResetManager.fieldsForComponent(component);
		if (logger.isDebugEnabled())
			logger.debug("Resetting component '" + component.getClass().getName()
					+ "' with resettable fields = "
					+ (resettableFields == null ? "null" : resettableFields.toString()));

		if (resettableFields != null) {
			for (Field aField : resettableFields) {
				try {
					if (aField.get(component) != null) {
						aField.set(component, null);
						if (logger.isDebugEnabled())
							logger.debug("Automatically reset " + aField.toString());
					}
				}
				catch (IllegalAccessException exception) {
					logger.fatal("Cannot access field '" + aField.toString() + "' in component '"
							+ component.getClass().getName() + "'.", exception);
				}
			}
		}
	}
    
    /**
	 * @return a list of field names that are deemed candidates for automatic stateless component reset. This is only
	 *         called once (lazily) per concrete component class. Fields are candidates if the following criteria
	 *         applies 1. the field name begins with underscore (_) 2. the field is a protected field 3. the field is
	 *         not static 4. the field is not abstract 5. the field is not final 6. the field is not a primitive type
	 *         The current object class hierarchy is examined for resettable fields up to and excluding this superclass
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static NSArray fieldsToReset(WOComponent component) {
		NSMutableArray fieldsToReset = new NSMutableArray();

		// Need this in case superclasses have the same fields
		// that may have been over-ridden by inheritance
		// and we don't need to check the same field twice
		NSMutableArray fieldsCheckedAlready = new NSMutableArray();

		// Get the class representing the subclass of the component
		Class classDef = component.getClass();

		if (logger.isDebugEnabled())
			logger.debug("Starting classDef = " + (classDef == null ? "null" : classDef.getName()));

		// We want to crawl up the class hierarchy and examine
		// all classes that are subclasses of WOComponent
		// to find fields that should be reset.
		while (!classDef.getName().equals("com.webobjects.appserver.WOComponent")) {
			Field[] fields = null;

			// Get the list of Field objects for the class
			try {
				fields = classDef.getDeclaredFields();
			}
			catch (SecurityException e) {
				logger.fatal("error accessing declared fields", e);
			}

			for (int i = 0; i < fields.length; i++) {
				Field aField = fields[i];

				String fieldName = aField.getName();

				// If checked already, ignore it (it could be an over-ridden field)
				if (!fieldsCheckedAlready.containsObject(fieldName)) {
					// Add it to the checked already list
					fieldsCheckedAlready.addObject(fieldName);

					// Get the field modifiers (private, public, protected, static, etc.)
					int modifiers = aField.getModifiers();

					if (logger.isDebugEnabled())
						logger.debug("Primitive check: aField.getType().isPrimitive() = "
								+ aField.getType().isPrimitive()
								+ " aField.getType().toString() = " + aField.getType().toString());

					// We want to reset the field automatically if
					// it begins with underscore, is protected, is not static/abstract/final
					if (fieldName.charAt(0) == '_' && Modifier.isProtected(modifiers)
							&& !Modifier.isStatic(modifiers) && !Modifier.isAbstract(modifiers)
							&& !Modifier.isFinal(modifiers) && !aField.getType().isPrimitive()) {

						// Candidate for resetting
						fieldsToReset.addObject(aField);

						if (logger.isDebugEnabled())
							logger.debug("Initializing resettable fields list: Added '" + fieldName
									+ "' (" + aField.toString() + ") to reset list of fields.");
					} // end if ( fieldName.charAt....
				} // end if ( !fieldsCheckedAlready ....
			} // end for ( int i ....

			// Crawl up to the superclass to check it's fields.
			classDef = classDef.getSuperclass();

			if (logger.isDebugEnabled())
				logger.debug("Next superclass classDef = "
						+ (classDef == null ? "null" : classDef.getName()));

		} // end while ( !class...

		return fieldsToReset.immutableClone();
	}
}