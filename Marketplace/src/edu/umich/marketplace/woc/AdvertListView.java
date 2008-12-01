package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSRange;
import com.webobjects.foundation.NSSelector;

import edu.umich.marketplace.eof.Advert;
import edu.umich.marketplace.eof.UserSessionModel;

public class AdvertListView extends MPComponent {
	private static final Logger		logger = Logger.getLogger(AdvertListView.class);

	private NSMutableArray<Advert> 	_shortenedAdverts = null;			// list of shortened adverts
	private NSArray<Advert>         _displayedAdverts;					// the complete list of adverts

	public Advert				  	thisAdvert;							// the advert repetition iterator

	public AdvertListView(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}

	public NSArray<Advert> getDisplayedAdverts() {
		logger.trace("--> getDisplayedAdverts()");
		final Object binding = valueForBinding ("list");
		logger.trace("--- getDisplayedAdverts() -- 'list' binding type is " + binding.getClass().getName());
		if (binding instanceof NSArray) {
			_displayedAdverts = (NSArray<Advert>)binding;
			_displayedAdverts = sortAdverts(_displayedAdverts, _sortMode);
		}
		if (binding instanceof Advert) {
			_displayedAdverts = new NSArray<Advert>((Advert)binding);
		}

		NSRange 					thisRange; 				// Range's take a start & a length.
		final int					advertsPerBatch = userSessionModel.getNumberOfAdsPerBatch();

		if (getAdvertIndex() > _displayedAdverts.count()) {
			logger.info("reseting curAdInd to 0 to avoid bad NSRange(" + getAdvertIndex() + ", "
					+ (_displayedAdverts.count() - getAdvertIndex()) + ")");
			setAdvertIndex(0);
		}

		if ((getAdvertIndex() + advertsPerBatch) > _displayedAdverts.count()) {
			thisRange = new NSRange(getAdvertIndex(),(_displayedAdverts.count() - getAdvertIndex()));
		} else {
			thisRange = new NSRange(getAdvertIndex(), advertsPerBatch);
		}

		return _displayedAdverts.subarrayWithRange(thisRange);
	}

	public NSMutableArray<Advert> getShortenedAdverts() {
		if (_shortenedAdverts == null) {
			_shortenedAdverts = new NSMutableArray<Advert>();
		}
		return _shortenedAdverts;
	}

	public void setShortenedAdverts(NSMutableArray<Advert> shortenedAdverts) {
		_shortenedAdverts = shortenedAdverts;
	}
	
	public boolean isMoreThanOneAdvert() {
		return getDisplayedAdverts().count() > 1;
	}

	public boolean isExpanded() {
		return ! getShortenedAdverts().containsObject(thisAdvert);
	}

	public boolean showCategoryName() {
		return false; //TODO
	}

	public WOComponent swapShowHideOne() {
		if (isExpanded()) {
			logger.trace("--> swapShowHideOne() - hide: " + thisAdvert.itemTitle());
			_shortenedAdverts.addObject(thisAdvert);
		}
		else {
			logger.trace("--> swapShowHideOne() - show: " + thisAdvert.itemTitle());
			_shortenedAdverts.removeObject(thisAdvert);
		}

		return null;
	}

//----------------------------------------------------------------------------------------------------------------
//  BBBB       AAA      TTTTTT      CCC     H   H     EEEE     RRRR
//  B   B     A   A       TT       C        H   H     E        R   R
//  BBBB      AAAAA       TT       C        HHHHH     EEE      RRRR
//  B   B     A   A       TT       C        H   H     E        R R
//  BBBB      A   A       TT        CCC     H   H     EEEE     R  RR
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	private int 					_advertIndex = 0; 		// array index of the current ad

	public int getAdvertIndex() {
		logger.info("<-- getCurrentAdIndex(" + _advertIndex + ")");
		return _advertIndex;
	}

	public void setAdvertIndex(int newAdvertIndex) {
		logger.info("--> setCurrentAdIndex(" + newAdvertIndex + ")");
		_advertIndex = newAdvertIndex;
	}


	public WOComponent nextBatch() {
		logger.info("--> nextBatch()");
		setAdvertIndex(getAdvertIndex() + userSessionModel.getNumberOfAdsPerBatch());
		return null;
	}

	public WOComponent prevBatch() {
		logger.info("--> prevBatch()");
		setAdvertIndex(getAdvertIndex() - userSessionModel.getNumberOfAdsPerBatch());
		return null;
	}

	public Integer 						batchCursor;

	public WOComponent goToBatchNumber() {
		logger.info("--> goToBatchNumber(" + batchCursor + ")");
		setAdvertIndex((batchCursor.intValue() - 1) * userSessionModel.getNumberOfAdsPerBatch());
		return null;
	}

    // --------------------------------------------------------------
    // Utilities
    // --------------------------------------------------------------

    /**
     * @return true if there is more than one batch of ads
     */
    public boolean isBatchOverflow() {
        return _displayedAdverts.count() > userSessionModel.getNumberOfAdsPerBatch();
    }

    /**
	 * This method determines if thisBatchIndex is the current batch. It calculates the message range for
	 * thisBatchIndex, and then determines whether the currentAdIndex falls within that range.
	 *
	 * @return true if thisBatchIndex is the current batch
	 */
	public boolean getIsCurrentBatch() {
		if (batchCursor == null) {
			return false;
		}

		final int batchAlphaIndex = (batchCursor.intValue() - 1) * userSessionModel.getNumberOfAdsPerBatch();
		final int batchOmegaIndex = (batchCursor.intValue() * userSessionModel.getNumberOfAdsPerBatch()) - 1;

		return (getAdvertIndex() >= batchAlphaIndex) && (getAdvertIndex() <= batchOmegaIndex);
	}

	/**
     * @return true if there's no previous batch
     */
    public boolean isNoPrevBatch() {
        return (getAdvertIndex() < userSessionModel.getNumberOfAdsPerBatch());
    }

    /**
     * @return true if there's no next batch
     */
    public boolean isNoNextBatch() {
        final int numLeft = _displayedAdverts.count() - getAdvertIndex();
        return (numLeft <= userSessionModel.getNumberOfAdsPerBatch());
    }

    // --------------------------------------------------------------
    // Accessor methods
    // --------------------------------------------------------------

    /**
     * This method returns an array of batch numbers, based on the user's choice of how
     * many ads to display at once, and how many ads are in adList.
     *
     * @return an array of Integer
     */
    public NSArray<Integer> getBatchCursors() {
        int 				batchNum = 0;
        final NSMutableArray<Integer> array = new NSMutableArray<Integer>();

        while ((batchNum * userSessionModel.getNumberOfAdsPerBatch()) < _displayedAdverts.count()) {
			array.addObject(new Integer (++batchNum));
		}

        return array.immutableClone();
    }

	public boolean canPostToAuthor() {
		logger.trace("--> getShowPostToAuthor()");
		return (hasBinding("canPostToAuthor")) ? ((String)valueForBinding ("canPostToAuthor")).equalsIgnoreCase("true") : false;
	}

    public WOComponent postToAuthor() {
		logger.trace("--> postToAuthor()");
		userSessionModel.prepareAuthorContactPanel(thisAdvert);
		return null;
	}

    public boolean canAddToFavorites() {
		return (hasBinding("canAddToFavorites")) ? ((String)valueForBinding ("canAddToFavorites")).equalsIgnoreCase("true") : false;
    }

    public WOComponent addToFavorites() {
		logger.trace("--> addToFavorites()");
		userSessionModel.addIntoFavorites(thisAdvert.localInstanceIn(userSessionModel.getEditingContext()));
		return null;
	}

	public boolean showCheckbox() {
		return (hasBinding("showCheckbox")) ? ((String)valueForBinding ("showCheckbox")).equalsIgnoreCase("true") : false;
	}

	//TODO 	showBatcher should return false if there are fewer ads than 'adsPerBatch' or,
	//		if not that, then where ever it's needed to stop an empty batcher on the page.
	
	public boolean showBatcher() {
		if (hasBinding("showBatcher") && ((String)valueForBinding ("showBatcher")).equalsIgnoreCase("false")) {
			sess.getUserSessionModel().setNumberOfAdsPerBatch(999);
			return false;
		}
		else {
			sess.getUserSessionModel().setNumberOfAdsPerBatch(UserSessionModel.defaultNumberOfAdsPerBatch);
			return true;
		}
	}

    public boolean isAdChecked() {
        return userSessionModel.getCheckedAdverts().containsObject(thisAdvert);
    }

    public void setIsAdChecked(boolean newIsAdChecked) {
		if (newIsAdChecked) {
			userSessionModel.addIntoCheckedAdverts(thisAdvert);
		}
		else if (isAdChecked()) {
			userSessionModel.subFromCheckedAdverts(thisAdvert);
		}
	}

 // ----------------------------------------------------------------------------------------------------------------

	private String				_sortMode = DateDescending;

	public static final String DateAscending = "DateAscending",   DateDescending = "DateDescending",
							  PriceAscending = "PriceAscending", PriceDescending = "PriceDescending",
							   UserAscending = "UserAscending",   UserDescending = "UserDescending";

	public WOComponent sortByDate() {
		logger.trace("--> sortByDate()");

		_sortMode = (_sortMode.equals(DateAscending)) ? DateDescending : DateAscending;		//NOTE: check polarity

		return null;
	}

	public WOComponent sortByPrice() {
		logger.trace("--> sortByPrice()");

		_sortMode = (_sortMode.equals(PriceAscending)) ? PriceDescending : PriceAscending;

		return null;
	}

	public WOComponent sortByUser() {
		logger.trace("--> sortByUser()");

		_sortMode = (_sortMode.equals(UserAscending)) ? UserDescending : UserAscending;

		return null;
	}

    /**
	 * This method returns ads sorted by sortMode. It looks at sortMode to determine which key and sortFlag to pass into
	 * sortAdsBy(), and then calls that method.
	 *
	 * @param adverts
	 *            an NSArray of ads to be sorted
	 * @param sortMode
	 *            a code for how to sort
	 */
    public NSArray<Advert> sortAdverts(NSArray<Advert> adverts, String sortMode) {
		logger.trace("--> sortAdverts(" + sortMode + ")");
        if (sortMode.equals (DateAscending)) {
			return sortAdverts (adverts, Advert.EXPIRY_DATE_KEY, true);
		}

        if (sortMode.equals (DateDescending)) {
			return sortAdverts (adverts, Advert.EXPIRY_DATE_KEY, false);
		}

        if (sortMode.equals (PriceAscending)) {
			return sortAdverts (adverts, Advert.ITEM_PRICE_KEY, true);
		}

        if (sortMode.equals (PriceDescending)) {
			return sortAdverts (adverts, Advert.ITEM_PRICE_KEY, false);
		}

        if (sortMode.equals (UserAscending)) {
			return sortAdverts (adverts, Advert.AUTHOR_UNIQNAME_KEY, true);
		}

        if (sortMode.equals (UserDescending)) {
			return sortAdverts (adverts, Advert.AUTHOR_UNIQNAME_KEY, false);
		}

        return adverts;
    }

    /**
	 * Performs the actual sorting logic for all of the sorting methods.
	 *
	 * @param adverts
	 *            an NSArray of ads to be sorted
	 * @param key
	 *            the member name to sort on
	 * @param sortFlag
	 *            true = sort Ascending; false = sort Descending
	 */
    private NSArray<Advert> sortAdverts(NSArray<Advert> adverts, String key, boolean sortFlag) {
		logger.trace("--> sortAdverts(" + sortFlag + ")");
		final NSSelector compare = (sortFlag) ?
				EOSortOrdering.CompareCaseInsensitiveAscending : EOSortOrdering.CompareCaseInsensitiveDescending;

		return EOSortOrdering.sortedArrayUsingKeyOrderArray(adverts, new NSArray(new EOSortOrdering(key, compare)));
	}
}