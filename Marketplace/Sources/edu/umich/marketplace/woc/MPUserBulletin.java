package edu.umich.marketplace.woc;

//----------------------------------------------------------------------------------------------------------------
// MPUserBulletin.java: Class file for WO Component 'MPUserBulletin'
// Project Marketplace
//
// Created by gavin on 12/10/06
//----------------------------------------------------------------------------------------------------------------

import com.ramsayconz.wocore.CoreApplication;
import com.webobjects.appserver.WOContext;

public class MPUserBulletin extends MPComponent {

    public MPUserBulletin(WOContext context) {
        super(context);
    }

    public String getUserBulletinPanelString() {
        return CoreApplication.properties.getProperty("userBulletinPanelString");
    }

    public String getUserBulletinPanelStyle() {
        return CoreApplication.properties.getProperty("userBulletinPanelStyle");
    }

    public boolean isUserBulletinPanelDisplay() {
        final String      	bulletinString = getUserBulletinPanelString();
        final String		bulletinDisplay = CoreApplication.properties.getProperty("userBulletinReveal").toUpperCase();

        return (bulletinDisplay.startsWith("NONE")) ? false : (sess.getHitCount() < 3) &&
        	((bulletinString != null) && (bulletinString.length() > 0) &&
		     (bulletinDisplay.startsWith("ALL") || 
		     (bulletinDisplay.startsWith("FRIEND") && sess.getUserSessionModel().isAuthorFriend())));
    }
}