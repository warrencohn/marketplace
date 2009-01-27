package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import edu.umich.marketplace.eof.Advert;
import edu.umich.marketplace.eof.Category;

public class AdvertCreationPanel extends GenericAdvertDisplay {
	private static final Logger 	logger = Logger.getLogger(AdvertCreationPanel.class);

	@SuppressWarnings("unused")
	private boolean 				_postClickedOnce = false;
	private boolean 				_firstTimeOnPage = true;
	public boolean 					_pagePreviewMode = false;
	private Advert					_newAdvert = null;

	public Category 				iteratedCategory;
	public Category 				selectedCategory;
	public String 					iteratedPostWhen;
	public String 					selectedPostWhen;

	public NSArray<String> postWhenPopupList = new NSArray<String> (new String[] {
		"today", "tomorrow", "2 days", "3 days", "4 days", "5 days", "6 days", "7 days"
	});

	public AdvertCreationPanel(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}

	public Advert getNewAdvert() {
		if (null == _newAdvert) {
			_newAdvert = sess.getUserSessionModel().createAuthorAdvert();
		}
		return _newAdvert;
	}
	
	public WOComponent cancelAd() {
		logger.trace("--> cancelAd()");

		sess.getUserSessionModel().cancelAuthorAdvert(_newAdvert);
		sess.getUserSessionModel().popAdvertViewerName();

		resetPage();
		return null;
	}

	/**
	 * This method sets previewMode back to false and so returns the edit page.
	 *
	 * @return the edit ad page
	 */
	public WOComponent editAd() { // re-edit ad
		logger.trace("--> editAd()");

		_pagePreviewMode = false;
		return null;
	}

	/**
	 * This method makes sure that the user has entered data for all the required fields, and then returns the preview
	 * page for the new ad. It sets firstTimeOnPage to false so that the validation methods will know to really check
	 * the data. Then, if no category is selected, or if the ad is not considered to be ready for publication, it sets
	 * the alertMessage. If the ad is valid, it sets the posting and expiring date for the ad and shows the user the
	 * preview page.
	 *
	 * @return the preview page for the new ad
	 */
	public WOComponent previewAd() {
		logger.trace("--> previewAd()");

		_firstTimeOnPage = false;

		if (selectedCategory == null) {
			setAlertMessage("Please select a category for your ad.");
			return null;
		}

		if (getIsValidTitle() && getIsValidBody() && getIsPriceValid() && getIsURLValid()) {
			logger.info("Ad is valid, setting posting, modify and expiry dates ... ");

			int index = postWhenPopupList.indexOfObject(selectedPostWhen);
			_newAdvert.setPostedDate(new NSTimestamp((new DateTime()).plusDays(index).getMillis()));
			_newAdvert.setModifyDate(new NSTimestamp((new DateTime()).plusDays(index).getMillis()));
			_newAdvert.setExpiryDate(new NSTimestamp((new DateTime()).plusDays(index+7).getMillis()));

			_pagePreviewMode = true;
			resetAlertMessage();
		}
		else {
			setAlertMessage("There is a problem with your ad - please correct the problems listed below.");
		}

		return null;
	}

	/**
	 * This method saves the new advert and makes all the necessary updates to our various caches. First, it attempts to
	 * save the ad. If that succeeds, it updates the timestamp on the ITCSSharedEntity for ads in this domain so other
	 * instances of this app will know to fetch the new ad. Then it restores the original infoViewer and updates the
	 * adList, if necessary. Finally, it restores this component's ivars to their original state.
	 */
	public WOComponent postAd() {
		logger.trace("--> postAd()");

		_newAdvert.setCategoryRelationship(selectedCategory);
		sess.getUserSessionModel().insertAuthorAdvert(_newAdvert);
		sess.getUserSessionModel().popAdvertViewerName();

		resetPage();
		return null;
	}

	public String getPanelTitleString() {
		return (_pagePreviewMode) ? "preview your advert" : "place an advert";
	}

	private void resetPage() {
		_newAdvert = null;
		iteratedPostWhen = null;
		selectedPostWhen = null;
		iteratedCategory = null;
		selectedCategory = null;
		_postClickedOnce = false;
		_firstTimeOnPage = true;
		_pagePreviewMode = false;
	}
	
	public boolean getPreviewMode() {
		return _pagePreviewMode;
	}

	public boolean getIsValidTitle() {
		return _firstTimeOnPage || _newAdvert.isTitleValid();
	}

	public boolean getIsURLValid() { // null is ok
		return _firstTimeOnPage || _newAdvert.isURLValid();
	}

	public boolean getIsValidBody() {
		return _firstTimeOnPage || _newAdvert.getIsBodyValid();
	}

	public boolean getIsPriceValid() {
		return _firstTimeOnPage || _newAdvert.getIsPriceValid();
	}
}