package edu.umich.marketplace;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreAssistance;

public class MarketplaceAction {
	private static final Logger 		logger = Logger.getLogger (MarketplaceAction.class);

    protected Boolean 					_testingThisApp;		// if TRUE, send the mail to ....
    protected SimpleDateFormat 			_dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
    protected StringBuffer 				_actionLog = new StringBuffer();	// log aggregation

    /**
	 * @return true if we are testing the app and sending mail notifications to a test user instead of the real users.
	 */
    protected Boolean isTestingNotifications() {
        if (_testingThisApp == null) {
        	_testingThisApp = CoreApplication.properties.getBoolean("testingNotification", "FALSE");
        }
        return _testingThisApp;
    }
    
    /**
     * Sends a transcript (_logMailText) of this run to 'Developer'.
     */
    protected void mailActionLog() {
        String subject = "[Marketplace] Notifier log for " + _dateFormat.format(new Date());
        CoreAssistance.mailToDeveloper(subject, _actionLog.toString());
        logger.info("Notification log mailed to developer.");
    }
}