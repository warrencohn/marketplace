package edu.umich.marketplace.eof;

import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreAssistance;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEC;

/**
 * The "ApplicationModel" manages the structures that do not change (or change slowly) during the period that a user
 * might be logged on -- that is, they are READ-ONLY in this context.  If the real world changes, then these structures
 * will be refreshed.  These include the "category" information, the undeleted/unexpired (active) adverts and their
 * relationships to categories.  As noted, when necessary the information managed by this model can be updated from the
 * database (driven by some external event - time, notification, user action, ...), otherwise there are no trips to the
 * database.  All the information from the database remains in the ApplicationModel's EOEditingContext, but no inserts,
 * deletes, or changes should happen.
 *
 * NOTE: How to make ec actually read-only?
 * NOTE: Should I use SharedEditingContext?
 *
 * @author gavin
 */

//TODO ?
//NSNotificationCenter.defaultCenter().addObserver(this,
//new NSSelector("objectsChangedInStore", new Class[]{NSNotification.class}),
//EOObjectStore.ObjectsChangedInStoreNotification, null); // getApplicationModel().getEditingContext().rootObjectStore());


public class ApplicationModel {
	private static final Logger 	logger = Logger.getLogger(ApplicationModel.class);

	private static ApplicationModel applicationModel;

	private EOEditingContext		_ec;

	private NSArray<Category> 		_topCategories;		// the major categories and ...
	private NSArray<Category> 		_endCategories;		// the minor categories from all the above

	private NSArray<Advert> 		_activeAdverts;		// active : undeleted; expiry => now (and posting <= now)

	private ApplicationModel() {
		logger.trace("+++ constructor");
		_ec = ERXEC.newEditingContext();
	}

	public static synchronized ApplicationModel getApplicationModel() {
		if (applicationModel == null)
			applicationModel = new ApplicationModel();	// it's OK, we can call this constructor
		return applicationModel;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		logger.error("*** You can't clone the singleton ApplicationModel.");
		throw new CloneNotSupportedException("You can't clone the singleton ApplicationModel");
	}

// ----------------------------------------------------------------------------------------------------------------
	
	public void initApplicationModel() {
		logger.trace("--> initApplicationModel()");

		getTopCategories();
		logger.trace("... found " + _topCategories.count() + " top categories");

		getEndCategories();
		logger.trace("... found " + _endCategories.count() + " end categories");

		refreshActiveAdverts();
	}
	

// ----------------------------------------------------------------------------------------------------------------
//    AAA      DDDD      V     V     EEEE     RRRR      TTTTTT      SSS
//   A   A     D   D     V     V     E        R   R       TT       S
//   AAAAA     D   D      V   V      EEE      RRRR        TT        SSS
//   A   A     D   D       V V       E        R R         TT           S
//   A   A     DDDD         V        EEEE     R  RR       TT       SSSS
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	public NSArray<Advert> getActiveAdverts() {
		logger.trace("--> getActiveAdverts()");
		if (null == _activeAdverts) {
			_activeAdverts = new NSArray<Advert>(Advert.fetchActive(_ec, new NSTimestamp()));
		}
		return _activeAdverts;
	}

	/**
	 * Sets an ads expiration date to one week from now
	 */

	/**
	 * Deletes ads in adList by calling deleteAd(). This not actually remove the ads from the database; rather, it sets
	 * the ads' deleted bits to true and their expiration dates to now.
	 */

	/**
	 * This method copies "numberOfAdverts" adverts starting at the end of the active array and places them into a new
	 * array.  The active adverts were sorted in ascending number (essentially ascending date), so the recent adverts
	 * are sorted in descending date order -- perfect for the summary display (which is exactly what this array is for).
	 *
	 * @param numberOfAdverts the number of active ads to obtain
	 */

	private NSArray<Advert> getRecentAdverts(int numberOfAdverts) {
		logger.trace("--> getRecentAdverts()");

		final NSMutableArray<Advert>		recentAdverts = new NSMutableArray<Advert>();
		final NSArray<Advert>				activeAdverts = getActiveAdverts();

		if (activeAdverts.count() > 0) {
			final ListIterator<Advert> e = activeAdverts.listIterator(activeAdverts.size());
			for (int i = 0; i < numberOfAdverts; i++) {
				if (e.hasPrevious()) {
					recentAdverts.addObject(e.previous());
				}
				else {
					break;
				}
			}
		}
		return recentAdverts.immutableClone();
	}

	public NSArray<Advert> getRecentAdverts() {				// used in "RecentAdvertsDisplay" component
		return getRecentAdverts(100);
	}


//----------------------------------------------------------------------------------------------------------------
//  	CCC      AAA      TTTTTT     EEEE      GGG       OOO      RRRR      III     EEEE      SSS
//     C        A   A       TT       E        G         O   O     R   R      I      E        S
//     C        AAAAA       TT       EEE      G  GG     O   O     RRRR       I      EEE       SSS
//     C        A   A       TT       E        G   G     O   O     R R        I      E            S
//      CCC     A   A       TT       EEEE      GGG       OOO      R  RR     III     EEEE     SSSS
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	public NSArray<Category> getTopCategories() {
		logger.trace("--> getTopCategories()");
		if (_topCategories == null) {
			_topCategories = new NSMutableArray<Category>(Category.fetchTopCategories(_ec)).immutableClone();
			logger.trace("<-- getTopCategories() fetched from db");
		}
		return _topCategories;
	}

	public NSArray<Category> getEndCategories() {
		logger.trace("--> getEndCategories()");
		if (_endCategories != null)
			return _endCategories;

		NSMutableArray<Category> endCategories = new NSMutableArray<Category>();
		for (Category category : getTopCategories()) {
			endCategories.addAll(category.fetchSubCategories(_ec));
		}

		logger.trace("<-- getEndCategories() fetched from db");
		_endCategories = endCategories.immutableClone();
		return _endCategories;
	}

	/**
	 * Gathers the ads for each end category and caches them in the category. We're doing this rather than using a
	 * category.ads() relationship because there can be many inactive ads in each category, and we don't want to
	 * unnecessarily fetch these from the database.
	 */
	private void distributeCategoryAdverts() {
		logger.trace("--> distributeCategoryAdverts()");

		final NSMutableArray<Advert> advertArray = new NSMutableArray<Advert>(getActiveAdverts());

		for (final Category endCategory : getEndCategories()) {
			final EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("category = %@", new NSArray(endCategory));
			final NSArray<Advert> catAdverts = EOQualifier.filteredArrayWithQualifier(advertArray, qualifier);
//			logger.trace("... distributeCategoryAdverts: end category: '" + category.getLongName() + "' [" + catAdverts.count() + "] ads");
			endCategory.setEndCategoryActiveAdverts(catAdverts);
			advertArray.removeObjectsInArray(catAdverts);
		}
		if (advertArray.count() > 0) {
			logger.error("*** distributeCategoryAdverts has " + advertArray.count() + " adverts left over after distribution");
		}

		for (final Category topCategory : getTopCategories()) {
			int			activeAdvertCount = 0;
			for (final Category subCategory : topCategory.fetchSubCategories(_ec)) {
				final int		increment = subCategory.getActiveAdvertCount();
//				logger.trace("... distributeCategoryAdverts: sub category: '" + subCategory.getLongName() + "' adds [" + increment + "] ads");
				activeAdvertCount = activeAdvertCount + increment;
			}
			logger.trace("<-- getSubCategories() fetched from db");
			topCategory.setTopCategoryActiveAdvertCount(activeAdvertCount);
//			logger.trace("... distributeCategoryAdverts: top category: '" + category.getLongName() + "' [" + category.getActiveAdvertCount() + "] ads");
		}
	}


//----------------------------------------------------------------------------------------------------------------
//    SSS      EEEE      AAA      RRRR       CCC     H  H     III     N   N      GGG
//   S         E        A   A     R   R     C        H  H      I      NN  N     G
//    SSS      EEE      AAAAA     RRRR      C        HHHH      I      N N N     G  GG
//       S     E        A   A     R R       C        H  H      I      N  NN     G   G
//   SSSS      EEEE     A   A     R  RR      CCC     H  H     III     N   N      GGG
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	/**
	 * This method returns an array of ads that match the terms in 'terms' in either their titles or bodyText. First it
	 * breaks the terms up into an array, skipping stop-words. Then it builds a qualifier of the form: ((title
	 * caseInsensitiveLike 'term1' and title caseInsensitiveLike 'term2' and etc.) OR ((bodyText caseInsensitiveLike
	 * 'term1' and bodyText caseInsensitiveLike 'term2' and etc.) It then filters adList in memory and returns the
	 * results.
	 * 
	 * @param terms
	 *            a list of terms, separated by spaces
	 * @param adList
	 *            the list of ads in which to search
	 * @return an array of Adverts matching terms in adList
	 */
	public NSArray<Advert> findAdsMatchingTerms(String terms) {
		final NSArray<String> 		termArray = searchTermArray(terms);
		NSArray<Advert> 			adContentMatches = searchOnContent(termArray, getActiveAdverts(), true);

		logger.trace("... findAdsMatchingTerms(...)" + adContentMatches);

		return adContentMatches;
/*
//		if (session.isAdWatch()) {
//			adContentMatches = searchOnContent(termArray, null, false);
//		}
//		else {
//			adContentMatches = searchOnContent(termArray, adList, true);
//		}

//		if (!session.isAdWatch() || (adContentMatches != null && adContentMatches.count() > 0)) {
//			return adContentMatches;
//		}

		NSArray adNumberMatches = searchOnNumber ((String) termArray.objectAtIndex (0));

		if (adNumberMatches != null && adNumberMatches.count() > 0) {
			return adNumberMatches;
		}

		NSArray adUniqnameMatches = searchOnUniqname ((String) termArray.objectAtIndex (0));

		if (adUniqnameMatches != null && adUniqnameMatches.count() > 0) {
			return adUniqnameMatches;
		}

		return null;
*/
	}

	@SuppressWarnings("unused")
	private NSArray<Advert> searchOnNumber(String term) {
		Double number = null;

		try {
			number = Double.valueOf(term);
		}
		catch (final Exception ex) {
			// not a number
			return null;
		}

		final NSMutableDictionary dict = new NSMutableDictionary();
		dict.takeValueForKey(number, Advert.NUMBER_KEY);

		return Advert.fetchForNumber(_ec, dict);
	}

	@SuppressWarnings("unused")
	private NSArray<Advert> searchOnUniqname(String term) {
		final NSMutableDictionary dict = new NSMutableDictionary();
		dict.takeValueForKey(term, Advert.AUTHOR_UNIQNAME_KEY);
		dict.takeValueForKey(new NSTimestamp(), Advert.EXPIRY_DATE_KEY);

		return Advert.fetchForUniqname(_ec, dict);
	}

	private NSArray<Advert> searchOnContent(NSArray<String> searchTermArray, NSArray<Advert> adList, boolean searchWithinCategory) {
		String oper = EOQualifier.stringForOperatorSelector(EOQualifier.QualifierOperatorCaseInsensitiveLike);

		NSMutableArray<EOQualifier> 	titleArray = new NSMutableArray<EOQualifier>();
		NSMutableArray<EOQualifier> 	descrArray = new NSMutableArray<EOQualifier>();

		for (String searchTerm : searchTermArray) {
			NSArray<String> term = new NSArray<String>("*" + searchTerm + "*");
			titleArray.addObject(EOQualifier.qualifierWithQualifierFormat(Advert.ITEM_TITLE_KEY + " " + oper + " %@", term));
			descrArray.addObject(EOQualifier.qualifierWithQualifierFormat(Advert.ITEM_DESCRIPTION_KEY + " " + oper + " %@", term));
		}

		logger.trace("... searchOnContent(...) titleArray: " + titleArray);
		logger.trace("... searchOnContent(...) descrArray: " + descrArray);
		
		EOAndQualifier 					titleQual = new EOAndQualifier(titleArray);
		EOAndQualifier 					descrQual = new EOAndQualifier(descrArray);

		logger.trace("... searchOnContent(...) titleQual: " + titleQual);
		logger.trace("... searchOnContent(...) descrQual: " + descrQual);

		EOOrQualifier titleOrDescrQual = new EOOrQualifier(new NSArray<EOQualifier>(new EOAndQualifier[] { titleQual, descrQual }));

		logger.trace("... searchOnContent(...) titleOrDescrQual: " + titleOrDescrQual);

		if (searchWithinCategory) {
			return EOQualifier.filteredArrayWithQualifier(adList, titleOrDescrQual);
		}

		DateTime sevenDaysAgoBegin = CoreAssistance.todayBegin().minusDays(7);

		NSMutableArray<EOQualifier> 	notExpiredArray = new NSMutableArray<EOQualifier>();
		notExpiredArray.addObject(EOQualifier.qualifierWithQualifierFormat(
				Advert.IS_DELETED_KEY + " <> %@", new NSArray<String>(new String[] { "Y" })));
		notExpiredArray.addObject(EOQualifier.qualifierWithQualifierFormat(
				Advert.EXPIRY_DATE_KEY + " >= %@", new NSArray<NSTimestamp>(new NSTimestamp[] { new NSTimestamp(sevenDaysAgoBegin.getMillis()) })));

		EOAndQualifier 					notExpiredQual = new EOAndQualifier(notExpiredArray);
		EOAndQualifier 					finalQual = new EOAndQualifier(new NSArray<EOQualifier>(
																			new EOQualifier[] {notExpiredQual, titleOrDescrQual }));

		return EOQualifier.filteredArrayWithQualifier(getActiveAdverts(), finalQual);
	}

	 private static final NSArray<String> ignoreWords = new NSArray<String>(new String[] { "and",
			"or", "not", "the", "a", "an", "of", "for", "at", "to", "from", "until", "which",
			"with", "in", "on", "by", "through", "here", "around", "near", "out", "this", "that",
			"anything", "something", "nothing", "if", "so", "also", "but", "only", "each", "has",
			"is", "be", "been", "was", "have", "are", "will", "am", "can", "would", "could",
			"should", "anyone", "someone", "noone", "it", "i", "you", "me", "him", "her", "them",
			"our", "who", "whom", "my", "mine", "yourself", "myself", "himself", "herself",
			"themselves", "ourselves" });


	 public static NSArray<String> searchTermArray(String termString) {
		logger.trace("--> searchTermArray(...)");

		final NSMutableArray<String> stringArray = new NSMutableArray<String>();

		if (termString != null) {
			termString = termString.toLowerCase();
			termString = termString.replace('.', ' ').replace('-', ' ').replace(',', ' ').replace('!', ' ').replace(',', ' ');
			termString = termString.replace('?', ' ').replace(':', ' ').replace('/', ' ').replace('(', ' ').replace(')', ' ');
			final NSArray<String> termArray = NSArray.componentsSeparatedByString(termString, " ");

			for (final String term : termArray) {
				if (!term.equals("") && !term.equals(" ") && !ignoreWords.containsObject(term)) {
					stringArray.addObject(term);
				}
			}
		}

		return stringArray.immutableClone();
	}

// ----------------------------------------------------------------------------------------------------------------
//     M   M      AAA      N   N      AAA       GGG      EEEE     M   M     EEEE     N   N     TTTTTT
//	   MM MM     A   A     NN  N     A   A     G         E        MM MM     E        NN  N       TT
//	   M M M     AAAAA     N N N     AAAAA     G  GG     EEE      M M M     EEE      N N N       TT
//	   M   M     A   A     N  NN     A   A     G   G     E        M   M     E        N  NN       TT
//	   M   M     A   A     N   N     A   A      GGG      EEEE     M   M     EEEE     N   N       TT
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	/**
	 * Keep count of how many times we've been through the request-response loop since we last checked to see whether
	 * the MarketPlace ads have changed. Each "_actionsTillUpdate" times we will check to see if the app needs to update
	 * core data and we'll reset the time we did it ... The Session keeps track of how many times we've been through
	 * its request-response loop also so that we can adjust the session timeout and take down any splash panel after 
	 * we're sure that someone is on board.
	 */
	private long 			_activeClickCount = 1;					// active click count ...
	private long 			_storedClickCount = 1;					// stored click count ...
	private final int 		_actionModulusInt = CoreApplication.properties.getInt("actionRepeatFactor", "4");

	@SuppressWarnings("unused")
	private NSTimestamp 	_lastUpdatedTime = new NSTimestamp();	// and note last time we did it ...

	public void refreshActiveAdverts() {
		logger.trace("--> refreshActiveAdverts()");

		_activeAdverts = new NSArray<Advert>(Advert.fetchActive(_ec, new NSTimestamp()));
		logger.trace("... found " + _activeAdverts.count() + " active adverts on refresh");

		distributeCategoryAdverts();
//		CoreAssistance.prettyPrintEOEditingContext(_ec);
		
	    _storedClickCount = _activeClickCount;
		_lastUpdatedTime = new NSTimestamp();
	}

	public void checkClickCountModulus() {
		_activeClickCount++;

		if (0 == (_activeClickCount % _actionModulusInt)) {
			logger.trace("    transactionCount % _actionsTillUpdate = zero");
    	    refreshActiveAdverts();
		}
	}
	
	public void checkClickCountChanged() {
		if (_activeClickCount > _storedClickCount) {
    	    refreshActiveAdverts();
		}
	}
}