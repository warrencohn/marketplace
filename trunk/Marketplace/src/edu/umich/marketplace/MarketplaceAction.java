package edu.umich.marketplace;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreAssistance;

public class MarketplaceAction {
	private static final Logger 		logger = Logger.getLogger (MarketplaceAction.class);

    protected StringBuffer 				_actionLog = new StringBuffer();	// log aggregation

    private SimpleDateFormat 			_dateFormat = 			// default format for Date string output
        			new SimpleDateFormat(System.getProperty("mpn.dateFormat", "EEEE, MMMM d, yyyy"));

    private Boolean 					_testingThisApp;		// if TRUE, send the mail to a test user
    private String 						_testMailAddress;		// the 'test' mail notifications recipient
    private static final String 		TEST_MAIL_ADDRESS = "gavin@umich.edu";

    /**
     * Sends a transcript (_logMailText) of this run to 'Developer'.
     */
    protected void mailActionLog() {
        String subject = "[Marketplace] Notifier log for " + _dateFormat.format(new Date());
        CoreAssistance.mailToDeveloper(subject, _actionLog.toString());
    }

    /**
	 * @return true if we are testing the app and sending mail notifications to a test user instead of the real users.
	 */
    protected Boolean getTestingThisApp() {
        if (_testingThisApp == null) {
        	_testingThisApp = CoreApplication.properties.getBoolean("mpn.testApplication", "FALSE");
        }
        return _testingThisApp;
    }

    protected String getTestMailAddress() {
        if (_testMailAddress == null)
            _testMailAddress = CoreApplication.properties.getString("mpn.testMailAddress", TEST_MAIL_ADDRESS);
        return _testMailAddress;
    }
}