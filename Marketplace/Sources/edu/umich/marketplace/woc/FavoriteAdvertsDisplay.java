package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableArray;

import edu.umich.marketplace.eof.Advert;

public class FavoriteAdvertsDisplay extends GenericAdvertDisplay {
	private static final Logger		logger = Logger.getLogger(GenericAdvertDisplay.class);

	public FavoriteAdvertsDisplay(WOContext context) {
        super(context);
    }

    public WOComponent removeFromFavorites() {
		logger.trace("--> removeFromFavorites()");

		if ((userSessionModel.getCheckedAdverts() == null) || (userSessionModel.getCheckedAdverts().count() == 0)) {
			setAlertMessage("Please select one or more ads to remove. You can select an ad by clicking on the box to its left.");
			return null;
		}

		for (final Advert advert : userSessionModel.getCheckedAdverts()) {
			userSessionModel.subFromFavorites(advert);
		}

		userSessionModel.setCheckedAdverts(new NSMutableArray<Advert>());
		userSessionModel.prepareFavoriteAdvertsDisplay();			// reset index

		return null;
	}
}