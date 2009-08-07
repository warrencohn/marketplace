//
//  CoreAssistance.java
//  MiniFramework
//
//  Created by Gavin Eadie on 4/3/05.
//  Copyright (c) 2005 Ramsay Consulting. All rights reserved.
//

package com.ramsayconz.wocore;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.webobjects.appserver.WOMailDelivery;
import com.webobjects.appserver.xml.WOXMLCoder;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;

/**
 * The CoreAssistance class provides static utility methods.
 * 
 * @author gavin
 *
 */

public class CoreAssistance {
    private static final Logger		logger = Logger.getLogger (CoreAssistance.class);
    private static final String 	INFO_PLIST_FILENAME = "Info.plist";

    private static WOMailDelivery 	_woMail = WOMailDelivery.sharedInstance();
    private static String			_localHostName;

    public CoreAssistance () {
        super ();
    }
    
    /**
     * Return a dictionary of all of the "Info.plist" files, keyed by framework name, from all the 
     * frameworks loaded by the application.  This method attempts to parse the contents of the 
     * "Info.plist" files in every framework bundles for the application.  If the file cannot be 
     * found for any framework, an empty dictionary is set.
     *
     * @return		array of Info.plist contents from all frameworks (and not application)
     */
    
    @SuppressWarnings("unchecked")
	static public NSDictionary<String, NSDictionary<String, ?>> frameworkInfoPlists() {
        NSMutableDictionary<String, NSDictionary<String, ?>>	
        							infoPlists = new NSMutableDictionary<String, NSDictionary<String, ?>>();
        
        // *** Application first
        
        NSBundle					appBundle = NSBundle.mainBundle();
        logger.info("App framework: " + appBundle.name() + 
                    "; PrincipalClass=" + ((null == appBundle.principalClass()) ? 
                        	               "none" : appBundle.principalClass().getName()) + 
                                           (appBundle.isJar() ? " [jar]" : ""));
        
        // *** then all the frameworks
        
        for (NSBundle framework : (NSArray<NSBundle>)NSBundle.frameworkBundles()) {
            NSDictionary<String,?>	infoPlist = null;           	// dictionary for this framework
            String          		infoPlistPath = framework.
            							resourcePathForLocalizedResourceNamed(INFO_PLIST_FILENAME, null);
            
            if (infoPlistPath != null && infoPlistPath.length() > 0) {	// If the path is not null
                byte[]				infoPlistBytes = framework.bytesForResourcePath(infoPlistPath);
                String				infoPlistString = new String (infoPlistBytes);
                infoPlist = (NSDictionary<String, Object>) NSPropertyListSerialization.propertyListFromString (infoPlistString);
                logger.info("Got framework: " + framework.name() + 
                            "; ShortVersion=" + infoPlist.objectForKey("CFBundleShortVersionString") +
                            "; PrincipalClass=" + ((null == framework.principalClass()) ? 
                                                    "none" : framework.principalClass().getName()) +
                                                    (framework.isJar() ? " [jar]" : ""));
            }
            else {                                                      // Otherwise the plist is empty
                logger.warn("No '" + INFO_PLIST_FILENAME + "' found for framework: " + framework.name() );
                infoPlist = NSDictionary.EmptyDictionary;
            }
            infoPlists.setObjectForKey(infoPlist, framework.name());
        }
        return infoPlists.immutableClone();
    }
    
    /**
     * returns an XML string of an object graph rooted at the object toArchive
     *
     * @param toArchive     Object to archive into XML
     * @return				String of XML
     */
    static public String xmlForObject( Object toArchive ) {
        String name = toArchive.getClass().getName();
        return WOXMLCoder.coder().encodeRootObjectForKey(toArchive, name);
    }
    
    static public DateTime todayBegin() {
    	logger.trace("--> todayBegin()");
    	return (new DateTime()).withTime(0, 0, 0, 0);
    }
    
    static public DateTime todayClose() {
    	logger.trace("--> todayClose()");
		return (new DateTime()).withTime(23, 59, 59, 999);
    }
    
//----------------------------------------------------------------------------------------------------------------
//   EEEE     M   M      AA      III     L    
//   E        MM MM     A  A      I      L    
//   EEE      M M M     AAAA      I      L    
//   E        M   M     A  A      I      L    
//   EEEE     M   M     A  A     III     LLLL 
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

    private static NSArray<String> 			_mailToForFailures;		// Email To: for failure events.
    
    static private NSArray<String> getmailToForFailures() {
		if (_mailToForFailures == null)
			_mailToForFailures = CoreApplication.properties.getNSArray("mailToForFailures", 
																	   "(marketplace-developer@umich.edu)");
		return _mailToForFailures;
	}

    private static NSArray<String> 			_mailToForWatchers;		// Email To: for monitoring staff.

    static private NSArray<String> getmailToForWatchers() {
        if (_mailToForWatchers == null)
        	_mailToForWatchers = CoreApplication.properties.getNSArray("mailToForWatchers", 
        			   												   "(marketplace-support@umich.edu)");
        return _mailToForWatchers;
    }

    private static NSArray<String> 			_mailToForDeveloper;		// Email To: for monitoring staff.

    static private NSArray<String> getmailToForDeveloper() {
        if (_mailToForDeveloper == null)
        	_mailToForDeveloper = CoreApplication.properties.getNSArray("mailToForDeveloper", 
        																"(marketplace-developer@umich.edu)");
        return _mailToForDeveloper;
    }

    private static String 					_mailFromMarketplace;		// Email From: for all messages.

    protected static String getMailFromMarketplace() {
        if (_mailFromMarketplace == null)
        	_mailFromMarketplace = System.getProperty ("mailFromMarketplace", 
        											   "marketplace-support@umich.edu");

        return _mailFromMarketplace;
    }

    static public boolean mailToFailures(String subject, String body) {
		logger.info("--> mailToFailures");
		return sendMail(getmailToForFailures(), getMailFromMarketplace(), "", subject, body);
	}

    static public boolean mailToWatchers(String subject, String body) {
		logger.info("--> mailToWatchers");
		return sendMail(getmailToForWatchers(), getMailFromMarketplace(), "", subject, body);
	}
        
    static public boolean mailToDeveloper(String subject, String body) {
		logger.info("--> mailToDeveloper");
		return sendMail(getmailToForDeveloper(), getMailFromMarketplace(), "", subject, body);
	}

    static public boolean mailToDeveloper(Throwable exception, String subject, String body) {
		logger.info("--> mailToDeveloper");
		return sendMail(getmailToForDeveloper(), getMailFromMarketplace(), "", subject, body);
	}

    static public boolean mailToIndividual(String recipient, String subject, String body) {
		logger.info("--> mailToIndividual");
		return sendMail(new NSArray<String>(recipient), getMailFromMarketplace(), "", subject, body);
	}

    /**
     * Sends a mail message to mailTo(s) from mailFrom about subject, with a body of 'body'.
     *
     * @param to email addresses to mail the message to (NSArray)
     * @param fm email address to mail the message from
     * @param ry email address for "Reply To"
     * @param re the messages's subject
     * @param bd the messages's body
     */
    static public boolean sendMail(NSArray<String> to, String fm, String ry, String re, String bd) {
    	logger.trace("--> sendMail to: " + to + " from: " + fm);
    	boolean					mailSuccess = false;
		if (to != null && fm != null && to.count() > 0) {
			try {
				String message = _woMail.composePlainTextEmail(fm, to, null, re, bd, false);	// no "bcc:"

				if (ry != null && ry.length() > 0) {
					message = message.replaceFirst("From:", "Reply-To: " + ry + "\nFrom:");
				}

				_woMail.sendEmail(message);
		       logger.trace("\n------------------------------------------------------------------------\n" + message +
		                    "\n------------------------------------------------------------------------");
		       
		       mailSuccess = true;
			} catch (Exception x) {
				logger.error("*** WOMailDelivery-JavaMail error", x);
			}
		}
    	logger.trace("<-- sendMail " + ((mailSuccess) ? "success" : "failure" ));
		return mailSuccess;
	}
    
//----------------------------------------------------------------------------------------------------------------
    
    /**
	 * emit a stacktrace and cause for the given exception
	 * 
	 * @param x
	 * @return
	 */
	public static String throwableAsString(Throwable x) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		x.printStackTrace(pw);
		return sw.toString();
	}
	
	/**
	 * Returns an <CODE>Iterator</CODE> of all loaded <CODE>EOEntity</CODE>s. This doesn't really belong in here, 
	 * but it's just too handy.
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<EOEntity> entityEnumerator(EOEditingContext ec) {
		NSMutableArray<EOEntity> entityArray = new NSMutableArray<EOEntity>(8);
		for (EOModel model : EOUtilities.modelGroup(ec).models()) {
			for (EOEntity entity : model.entities()) {
				entityArray.addObject(entity);
			}
		}
		return entityArray.iterator();
	}
	
	/**
	 * Returns first part of the internet address of the machine running this program.
	 * @return a String with the local host's truncated name
	 */
	public static String getLocalHostName() {
		if (null == _localHostName) {
			try {
				_localHostName = java.net.InetAddress.getLocalHost().toString();

				int idx = _localHostName.indexOf(".");
				_localHostName = ((idx != -1) ? _localHostName.substring(0, idx) : "*unknown*");
			}
			catch (java.net.UnknownHostException x) {
				logger.error("   | UnknownHostException: ", x);
			}
			_localHostName = _localHostName.toLowerCase();
			logger.debug("<-- getLocalHostName: <" + _localHostName + ">");
		}
		return _localHostName;
	}
	
	public static String webObjectsVersion () {
        NSDictionary<String,NSDictionary<String,?>> frameworkInfos = frameworkInfoPlists();
        NSDictionary<String,?>    					webObjectsInfo = frameworkInfos.objectForKey("JavaWebObjects");
        String          webObjectsVers = (String)webObjectsInfo.objectForKey("CFBundleShortVersionString");
        return (webObjectsVers.equals("") ? "5.1, or less" : webObjectsVers);
    }

	
	@SuppressWarnings("unchecked")
	public static void prettyPrintEOEditingContext(EOEditingContext ec) {
		logger.trace("+-- EOEditingContext --" + (ec.hasChanges() ? " hasChanges " : "------------") + 
												"--------------------------------------------------");
		logger.trace("| registeredObjects() : " + ec.registeredObjects().count());
		prettyPrintNSArray(ec.registeredObjects());
		logger.trace("+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		logger.trace("|   insertedObjects() : " + ec.insertedObjects().count());
		prettyPrintNSArray(ec.insertedObjects());
		logger.trace("+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		logger.trace("|    deletedObjects() : " + ec.deletedObjects().count());
		prettyPrintNSArray(ec.deletedObjects());
		logger.trace("+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		logger.trace("|    updatedObjects() : " + ec.updatedObjects().count());
		prettyPrintNSArray(ec.updatedObjects());
		logger.trace("+-------------------------------------------------------------- EOEditingContext --");
	}

	private static void prettyPrintNSArray(NSArray<EOEnterpriseObject> array) {
		for (EOEnterpriseObject eo : array) {
			logger.trace("|   " + eo.toString());
		}
	}

    /**
     * return yes/no for the given string in the given NSArray<String>.
     */
	public static boolean isStringInArray(String targetString, NSArray<String> stringArray) {
		return ((stringArray != null) && stringArray.containsObject(targetString));
	}
}
