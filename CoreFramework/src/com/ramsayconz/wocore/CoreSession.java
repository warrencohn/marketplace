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
import com.webobjects.foundation.NSMutableDictionary;
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
	private static final long 			serialVersionUID = 2137928448342423580L;
	private static final Logger     	logger = Logger.getLogger (CoreSession.class);

	public static CoreProperties    	properties = null;
	protected CoreConfiguration 		configuration = null;

	protected CoreApplication       application = (CoreApplication)WOApplication.application();
	protected NSMutableDictionary<String, Object> 	sessionInfo = null;


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

        CoreSession.properties = new CoreProperties(System.getProperties());
}

	@Override
	public void awake() {
		super.awake();

		if (!SessionInfoDict.any_SessionInfo(sessionID())) {
			logger.error("     | unknown sessionID :" + sessionID() + ".  We are terminating this session.");
			terminate();
		}
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

			if (null == this.sessionInfo) {
				this.sessionInfo = new NSMutableDictionary<String, Object>();
				this.sessionInfo.takeValueForKey(0, "HitCount");
				SessionInfoDict.add_SessionInfo(sessionID(), this.sessionInfo);
			}

			sessionWillAwake();
			this.application.addSession(this, sessionID());
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
			this.application.addSession(this, sessionID());
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
			this.application.addSession(this, sessionID());
		}
	}    

    /**
	 * Override this to perform session timeout processing. (optional)
	 */
    public void sessionDidTimeOut() { }
    
    @Override
	public void terminate() {
		logger.trace("-->--> terminate()  [id=" + sessionID() + "]");

		this.application.delSession(sessionID());
		SessionInfoDict.log_SessionInfo();
		SessionInfoDict.sub_SessionInfo(sessionID());
		super.terminate();
    }
    
	//----------------------------------------------------------------------------------------------------------------

	/**
	 * Finds the session in SessionInfo with key sessionID and +1 its count of actions.
	 */
	protected void incHitCount() {
		final Integer clicks = getHitCount();
		if (clicks != null) {
			this.sessionInfo.takeValueForKey(clicks+1, "HitCount");
		}
	}

	/**
	 * Returns a count of the number of times the session indicated by sessionID has been through the request-response
	 * loop. Used to set the session timeout to a sensible length after the user has clicked at east once in Marketplace
	 * and may be used to stop showing a splash panel after a couple of hits.
	 *
	 * @return the number of actions recorded for sessionID
	 */
	public Integer getHitCount() {
		return (Integer) this.sessionInfo.valueForKey("HitCount");
	}
}