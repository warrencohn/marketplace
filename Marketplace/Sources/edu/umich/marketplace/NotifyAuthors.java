//
// NotifyAuthors.java
// Project MarketplaceNotifier
//
// Created by phayes on Thu Jun 13 2002
//  Edited by Gavin Eadie - July+August 2005
//

package edu.umich.marketplace;

import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreAssistance;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import edu.umich.marketplace.eof.Advert;

public class NotifyAuthors extends MarketplaceAction {
	private static final Logger 		logger = Logger.getLogger (NotifyAuthors.class);

    private EOEditingContext 			_ec = new EOEditingContext();
    
    public NotifyAuthors() {
    }

    /**
     * Fetches ads that are about to expire and sends email to their authors notifying them that the ads are 
     * about to expire.  When finished, e-mails the address in mailToForErrors a transcript of the entire 
     * application run.
     */
    public String doNotify() {
    	String		responseString;
    	
    	if (! ((new DateTime()).getDayOfWeek() == DateTimeConstants.SATURDAY ||
    		   (new DateTime()).getDayOfWeek() == DateTimeConstants.SUNDAY)) {
            NSArray<Advert> 		adsToExpire = fetchAdsToExpire();

            if (adsToExpire != null && adsToExpire.count() > 0) {		// group ads to email same author together
                NSDictionary<String, NSMutableArray<Advert>>
                					adsByAuthor = getAdsByAuthor(adsToExpire);

                try {
                    notifyAllAuthors(adsByAuthor);
                    responseString = "success -- email sent for " + adsToExpire.count() + " notifications.";
                }
                catch (Exception e) {
                	responseString = "failure -- exception trying to send notification email.";
                }
            }
            else {
            	responseString = "success -- found no ads about to expire.";
                _actionLog.append(responseString + "\n");
                logger.info(responseString);
            }
        }
        else {
        	responseString = "success -- not running because it's Saturday or Sunday.";
            _actionLog.append(responseString);
        }

    	mailActionLog();
    	
    	return responseString;
    }

    /**
     * Creates a dictionary whose keys are uniqueNames and whose objects are arrays of ads by those authors.
     *
     * @param ads - the adds to group by author
     *
     * @return a dictionary of NSMutableArrays of MPAds indexed by String
     */
    private NSMutableDictionary<String, NSMutableArray<Advert>> getAdsByAuthor (NSArray<Advert> ads) {

        NSMutableDictionary<String, NSMutableArray<Advert>>	
        						dict = new NSMutableDictionary<String, NSMutableArray<Advert>>();

        for (Advert ad : ads) {
            String uniqueName = null;

            if (ad.authorUniqname() != null)
                uniqueName = ad.authorUniqname();

            if (uniqueName == null) {
                _actionLog.append("we have a null author for ad [#").append(ad.number()).append("] ").append(ad.itemTitle()).append("\n");
                logger.error("we have a null author for ad [#" + ad.number() + "] " + ad.itemTitle());
            }
            else {
                if (dict.objectForKey (uniqueName) == null) {
                    dict.setObjectForKey (new NSMutableArray<Advert> (ad), uniqueName);
                }
                else {
                    NSMutableArray<Advert> array = dict.objectForKey (uniqueName);
					array.addObject(ad);
                }
            }
        }
        return dict;
    }

    //---------------------------------------------------------------------------
    // Fetch methods
    //---------------------------------------------------------------------------

    /**
	 * Fetches the ads that are about to expire. Ads are considered to be "about to expire" if they are set to expire
	 * one business day from now.
	 * 
	 * NOTE: see documentation for getExpiringDateRange() for more detail on what "one business day from now" means.
	 * 
	 * @return an NSArray of MPAds that are to expire in one business day
	 */
	@SuppressWarnings("unchecked")
	private NSArray<Advert> fetchAdsToExpire() {
		NSArray<Advert> ads = null;

		/**
		 * Creates two Dates which are a begin time and and end time for the date which is one business day from now.
		 * 
		 * NB: If today is Thursday, this means we need to notify people whose ads will expire on Saturday and Sunday,
		 * as well as on Friday (assuming that not everyone reads e-mail over the weekend). Similarly, if today is
		 * Friday, we notify people whose ads expire on Monday, rather than over the weekend.
		 */

		int dayOfWeek = (new DateTime().getDayOfWeek());

		Date beginDate, closeDate;

		if (dayOfWeek == DateTimeConstants.THURSDAY) {
			beginDate = new Date((new DateTime()).plusDays(1).withTime(0, 0, 0, 1).getMillis()); 	// Friday morning
			closeDate = new Date((new DateTime()).plusDays(4).withTime(0, 0, 0, 1).getMillis()); 	// Monday morning
		}
		else if (dayOfWeek == DateTimeConstants.FRIDAY) {
			beginDate = new Date((new DateTime()).plusDays(3).withTime(0, 0, 0, 1).getMillis()); 	// Monday morning
			closeDate = new Date((new DateTime()).plusDays(4).withTime(0, 0, 0, 1).getMillis()); 	// Tuesday morning
		}
		else {
			beginDate = new Date((new DateTime()).plusDays(1).withTime(0, 0, 0, 1).getMillis()); 	// tomorrow morning
			closeDate = new Date((new DateTime()).plusDays(2).withTime(0, 0, 0, 1).getMillis()); 	// day after tomorrow morning
		}

		_actionLog.append("... ads in the range [").append(_dateFormat.format(beginDate)).
		             append("..").append(_dateFormat.format(closeDate)).append("]\n");
		logger.info("... select ads in the range [" + _dateFormat.format(beginDate) + " - "
				                                    + _dateFormat.format(closeDate) + "]");

		try {
			ads = Advert.fetchToExpire(_ec, new NSTimestamp(beginDate), new NSTimestamp(closeDate));
		}
		catch (Exception x) {
			_actionLog.append("Throwable in fetchAdsToExpire(): ").append(x.toString()).append("\n").append("\n");
			logger.error("Exception in fetchAdsToExpire(): ", x);

			String subject = "[Marketplace] Notifier database error";
			String body = "MarketplaceNotifier encountered exception:\n" + x.getStackTrace();

			CoreAssistance.mailToDeveloper(x, subject, body);
		}

		return ads;
	}

    //---------------------------------------------------------------------------
    // Notification methods
    //---------------------------------------------------------------------------

    /**
     * Sends email to all of the authors whose ads are "about to expire"  warning them that their ads 
     * will expire soon.
     *
     * @param adsByAuthor a dictionary of NSMutableArrays of MPAds indexed by uniqueName
     */
    private void notifyAllAuthors (NSDictionary<String, NSMutableArray<Advert>> adsByAuthor) {
        _actionLog.append("There are ").append(adsByAuthor.count()).append(" users to notify:\n");
        logger.info("There are " + adsByAuthor.count() + " users to notify:");

        for (String uniqueName : adsByAuthor.allKeys()) {
            NSArray<Advert> ads = adsByAuthor.objectForKey (uniqueName);
            notifyAuthor (uniqueName, ads);
        }
    }

    /**
     * Sends email to an author that his/her ad(s) is/are about to expire.  
     * Message is slightly different if author has more than one ad.
     *
     *  Aug29/05 - Gavin
     *      Can have messages go to prop("testNotifyAddress") for testing
     *      if prop("testingNotification") is TRUE (default FALSE)
     *
     * @param uniqueName the author to notify
     * @param ads the ads that are about to expire
     */
    private void notifyAuthor(String uniqueName, NSArray<Advert> ads) {
		String bodyText;

		if (ads.count() == 1) {
			Advert ad = ads.objectAtIndex(0);
			bodyText = "Your Marketplace ad [#" + ad.number() + "] '" + ad.itemTitle()
					+ "' will expire on " + _dateFormat.format(new Date(ad.expiryDate().getTime()))
					+ ".\n\n" + getRenewAdInstructions();
		}
		else {
			StringBuffer mailBody = new StringBuffer("You have these Marketplace advertisements that will expire soon:\n\n");

			for (Advert ad : ads) {
				mailBody.append("    [#" + ad.number() + "] '" + ad.itemTitle() + "', will expire on " +
						         _dateFormat.format(new Date(ad.expiryDate().getTime())) + "\n\n");
			}
			bodyText = mailBody.append(getRenewAdInstructions()).toString();
		}

		if (isTestingNotifications()) {
			CoreAssistance.mailToDeveloper("[Marketplace] Your advertisement will expire soon", bodyText);
		}
		else {
			CoreAssistance.mailToIndividual(uniqueName + "@umich.edu", "[Marketplace] Your advertisement will expire soon", bodyText);
		}
	}

    /**
     * @return some text about how to renew an ad in the Marketplace application
     */
    private String 				_renewAdInstructions;	// user instructions for renewal

    private String getRenewAdInstructions() {
        if (_renewAdInstructions == null || _renewAdInstructions.length() < 3)
            _renewAdInstructions = CoreApplication.properties.getString("renewalInstructions");
        return _renewAdInstructions;
    }
}