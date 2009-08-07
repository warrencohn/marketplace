// FrameworkInitializer.java $Revision: 1.0 $
//
// Copyright (c) 2002-2003 Red Shed Software. All rights reserved.
// by Jonathan 'Wolf' Rentzsch (jon at redshed dot net)

package com.ramsayconz.wocore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

/**
 * Encapsulates the logic necessary to register a static method that gets called upon WOApplication initialization. 
 * To use, put code like this into your class:
 * <PRE>
 *  import com.ramsayconz.wocore.FrameworkInitializer;
 *         
 *      class MyFrameworkClass {
 *         public static void initializeOnNotification() {
 *         System.out.println("framework initialized!");
 *      }
 *
 *      private static final FrameworkInitializer 
 *               _frameworkInitializer = new FrameworkInitializer(MyFrameworkClass.class);
 *  }</PRE>
 * Then, set your framework's "NSPrincipalClass" Info.plist entry to "MyFrameworkClass".
 */
public class FrameworkInitializer {
    private static final Logger     logger = Logger.getLogger(FrameworkInitializer.class);
    
	@SuppressWarnings("unchecked")
	protected Class					_aClass;			// The class containing the static initialization method
   	protected String 				_initializerName;	// The name of the static initialization method

	/**
	 * Registers and calls <CODE>aClass</CODE>'s static
	 * <CODE>initializerName</CODE> method at WOApplication initialization time.
	 * 
	 * @param aClass
	 *            The class to invoke <CODE>initializerName</CODE> on when the
	 *            WOApplication is initialized.
	 * @param initializerName
	 *            The name of the static method to invoke.
	 * @param initializeAfterAppConstruction
	 *            If true, the initialization method is called upon the
	 *            <CODE>ApplicationDidFinishLaunchingNotification</CODE>
	 *            notification. Otherwise, it's called upon the
	 *            <CODE>ApplicationWillFinishLaunchingNotification</CODE>
	 *            notification.
	 */
	@SuppressWarnings("unchecked")
	public FrameworkInitializer(Class aClass, String initializerName, boolean initializeAfterAppConstruction) {
		this._aClass = aClass;
		this._initializerName = initializerName;
		NSNotificationCenter.defaultCenter().addObserver(this,
					new NSSelector<Object>("initializeOnNotification", new Class[] { NSNotification.class }),
					initializeAfterAppConstruction ? WOApplication.ApplicationDidFinishLaunchingNotification
				                                   : WOApplication.ApplicationWillFinishLaunchingNotification, null);

		if (Boolean.getBoolean("FrameworkInitializerDebug")) {
			System.out.println("*** FrameworkInitializer: Will call " + aClass.getName() + "." + initializerName
					+ (initializeAfterAppConstruction ? " AFTER Application construction"
							                          : " BEFORE Application construction"));
		}
	}

    /**
	 * Convenience constructor. 
	 * Calls <CODE>FrameworkInitializer(aClass, initializerName, initializeAfterAppConstruction)</CODE>
	 * with <CODE>initializerName</CODE> set to <CODE>"initializeFromFramework"</CODE> and
	 * <CODE>initializeAfterAppConstruction</CODE> set to false.
	 *
	 * @param aClass The class to invoke initializeFromFramework() on when the WOApplication is fully initialized.
	 */
   	
	@SuppressWarnings("unchecked")
	public FrameworkInitializer(Class aClass) {
		this(aClass, "initializeFromFramework", false);
	}

	/**
	 * Ignore -- implementation detail. This is the stub to translate the instance method into a static method.
	 */
	@SuppressWarnings("unchecked")
	public void initializeOnNotification(NSNotification notification)
					throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method initMethod = null;
		try {
			initMethod = this._aClass.getMethod(this._initializerName, (Class[])null);
		}
		catch (NoSuchMethodException x) {
			logger.fatal("*** initializeOnNotification ", x);
		}

		if (Boolean.getBoolean("FrameworkInitializerDebug")) {
			logger.info("calling: " + initMethod);
		}

		try {
			initMethod.invoke(this._aClass, (Object[])null);
		}
		catch (IllegalAccessException x1) {
			logger.fatal("*** initializeOnNotification ", x1);
		}
		catch (InvocationTargetException x2) {
			logger.fatal("*** initializeOnNotification ", x2);
		}
		catch (IllegalArgumentException x3) {
			logger.fatal("*** initializeOnNotification (IllegalArgumentException)", x3);
		}
		catch (Exception x99) {
			logger.fatal("*** initializeOnNotification ", x99);
		}
	}
}
