package edu.umich.marketplace.woc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import edu.umich.marketplace.eof.Advert;

public class AuthorAdvertsDisplay extends GenericAdvertDisplay {
	private static final Logger logger = Logger.getLogger(AuthorAdvertsDisplay.class);

	public AuthorAdvertsDisplay(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}

	public WOComponent renewAds() {
		logger.trace("--> renewAds()");

		if ((userSessionModel.getCheckedAdverts() == null) || (userSessionModel.getCheckedAdverts().count() == 0)) {
			setAlertMessage("Please select one or more ads to renew. You can select an ad by clicking on the box to its left.");
			return null;
		}

		for (final Advert advert : userSessionModel.getCheckedAdverts()) {
			advert.setExpiryDate(new NSTimestamp((new DateTime()).plusDays(7).getMillis()));
		}
		userSessionModel.saveChanges();

//TODO	app.timestampMutualData();
//        constructRenewedAdAlert (sess.getSelectedAds());
		userSessionModel.setCheckedAdverts(new NSMutableArray<Advert>());

		return null;
	}

	/**
	 * This method deletes the selected ads.  If it encounters an exception,
	 * it sets the alert message.  Otherwise, it updates the ITCSharedEntity
	 * object for MarketPlace in the current domain, and resets selectedAds.
	 * @return the same page, minus the deleted ads
	 */
	public WOComponent deleteAds() {
		logger.trace("Entering deleteAds()");

		if ((userSessionModel.getCheckedAdverts() == null) || (userSessionModel.getCheckedAdverts().count() == 0)) {
			setAlertMessage("Please select one or more ads to delete. You can select an ad by clicking on the box to its left.");
			return null;
		}

		for (final Advert advert : userSessionModel.getCheckedAdverts()) {
			advert.setIsDeleted("Y");
		}
		userSessionModel.saveChanges();

//TODO	app.timestampMutualData();
		userSessionModel.setCheckedAdverts(new NSMutableArray<Advert>());

		return null;
	}

	// --------------------------------------------------------------
	// Utilities
	// --------------------------------------------------------------

	@SuppressWarnings("unused")
	private void constructRenewedAdAlert(NSArray<Advert> checkedAds) {
		SimpleDateFormat sdf;
		final Date date = checkedAds.objectAtIndex(0).expiryDate();

		sdf = new SimpleDateFormat("EEE, MMM d, yyyy"); // eg: Wed, Jan 23, 2003
		final String dateStr = sdf.format(date);
		sdf = new SimpleDateFormat("h:mm a"); // eg: 12:04 pm
		final String timeStr = sdf.format(date);

		if (checkedAds.count() == 1) {
			setAlertMessage("We have renewed your ad.  It will now expire on " + dateStr + " at " + timeStr + ".");
		}
		else {
			setAlertMessage("We have renewed your ads.  They will all expire on " + dateStr + " at " + timeStr + ".");
		}
	}
}