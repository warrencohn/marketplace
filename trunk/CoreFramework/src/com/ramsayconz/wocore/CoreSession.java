package com.ramsayconz.wocore;

//
//  CoreSession.java
//  MiniFramework
//
//  Created by Gavin Eadie on Sun Mar 07 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOSession;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

import er.extensions.appserver.ERXSession;

/**
 *  CoreSession
 */

public class CoreSession extends ERXSession {
    /**
	 * 
	 */
	private static final long 		serialVersionUID = 2137928448342423580L;
	private static final Logger     logger = Logger.getLogger (CoreSession.class);
    protected CoreApplication       application = (CoreApplication)WOApplication.application();

    public CoreSession () {
        super ();
        logger.trace("-----+ constructor");

        NSNotificationCenter.defaultCenter().addObserver(this,
                new NSSelector<Object>("sessionWillAwake", new Class[] { NSNotification.class }), 
                ERXSession.SessionWillAwakeNotification, null);

        NSNotificationCenter.defaultCenter().addObserver(this,
                new NSSelector<Object>("sessionDidCreate", new Class[] { NSNotification.class }), 
                WOSession.SessionDidCreateNotification, null);

        NSNotificationCenter.defaultCenter().addObserver(this,
                new NSSelector<Object>("sessionDidTimeOut", new Class[] { NSNotification.class }), 
                WOSession.SessionDidTimeOutNotification, null);
    }

    /* ----------------------------------------------------------------------------------- */

    /**
	 * Called when the session posts the notification "SessionDidCreateNotification". Note, since this notification
	 * reports EVERY session creation, we need to check that the one we react to is our own.
	 * 
	 * This method calls subclasses' {@link #didCreateSession} method.
	 * 
	 * @param n -
	 *            the Session instance.
	 */    
    public final void sessionWillAwake(NSNotification n) {
		if (((WOSession)n.object()).sessionID().equals(sessionID())) {
			logger.trace("!-- " + ERXSession.SessionWillAwakeNotification + " [" + ((WOSession)n.object()).sessionID() + "]");
			sessionWillAwake();
			application.addSession(this, sessionID());
		}
	}    

    /**
	 * Override this to perform session initialization. (optional)
	 */
    public void sessionWillAwake() { }
    
    /**
	 * Called when the session posts the notification "SessionDidCreateNotification". Note, since this notification
	 * reports EVERY session creation, we need to check that the one we react to is our own.
	 * 
	 * This method calls subclasses' {@link #didCreateSession} method.
	 * 
	 * @param n -
	 *            the Session instance.
	 */    
    public final void sessionDidCreate(NSNotification n) {
		if (((WOSession)n.object()).sessionID().equals(sessionID())) {
			logger.trace("!-- " + WOSession.SessionDidCreateNotification + " [" + ((WOSession)n.object()).sessionID() + "]");
			sessionDidCreate();
			application.addSession(this, sessionID());
		}
	}    

    /**
	 * Override this to perform session initialization. (optional)
	 */
    public void sessionDidCreate() { }
    
    /**
     * Called when the session posts the notification "SessionDidTimeOutNotification".
     * This method calls subclasses' {@link #didTimeOutSession} method.
     *
     * @param n - the Session instance.
     */    
    public final void sessionDidTimeOut(NSNotification n) {
		if (n.object().equals(sessionID())) {
			logger.trace("!-- " + WOSession.SessionDidTimeOutNotification + " [" + n.object() + "]");
			sessionDidTimeOut();
			application.addSession(this, sessionID());
		}
	}    

    /**
	 * Override this to perform session timeout processing. (optional)
	 */
    public void sessionDidTimeOut() { }
    
    @Override
	public void terminate() {
        application.delSession(sessionID());
        super.terminate();
    }
}