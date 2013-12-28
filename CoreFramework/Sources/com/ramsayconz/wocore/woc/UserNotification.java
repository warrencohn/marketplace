//
// UserNotification.java: Class file for WO Component 'UserNotification'
// Project CoreFramework
//
// Created by gavin on 10/25/06
//

package com.ramsayconz.wocore.woc;
 
import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

@SuppressWarnings("serial")
public class UserNotification extends WOComponent {
    static Logger               logger = Logger.getLogger (UserNotification.class);

    public String 				notificationSummary;
    public String 				notificationDetails;
    public String 				notificationBackCol;
    public String 				notificationTextCol;
    public String 				notificationTextSiz;

    public UserNotification(WOContext context) {
        super(context);
        logger.trace("+++ constructor");
    }
    
    @SuppressWarnings("unqualified-field-access")
	public boolean getNotificationDetailsExist() {
        return (null != notificationDetails && notificationDetails.length() > 0);
    }

    @SuppressWarnings("unqualified-field-access")
    public boolean getNotificationSummaryExist() {
        return (null != notificationSummary && notificationSummary.length() > 0);
    }
}
