package edu.umich.marketplace.eof;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.ramsayconz.wocore.CoreAssistance;
import com.ramsayconz.wocore.CoreSession;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORequest;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSTimestamp;

import edu.umich.marketplace.DirectAction;
import edu.umich.marketplace.EndCategoryMessages;
import edu.umich.marketplace.woc.ActiveAdvertsDisplay;
import edu.umich.marketplace.woc.AdvertCreationPanel;
import edu.umich.marketplace.woc.AuthorAdvertsDisplay;
import edu.umich.marketplace.woc.AuthorContactPanel;
import edu.umich.marketplace.woc.CategoryAdvertsDisplay;
import edu.umich.marketplace.woc.FavoriteAdvertsDisplay;
import edu.umich.marketplace.woc.FoundAdvertsDisplay;
import edu.umich.marketplace.woc.RecentAdvertsDisplay;
import er.extensions.eof.ERXEC;

/**
 * This class provides (a) the process to log a user onto the application and (b) session level storage for this user.
 * The store contains:
 *
 * (1) _myAdverts: these are the favorites that the user authored which are still available to be renewed or deleted.
 *
 * (2) _favorites: these are the favorites that the user wants to keep a note off, it is a subset of the active ads.
 *
 * (3) _endCategories: these are the categories that favorites can be associated with. This is an unchanging set but it a
 * copy is maintained in the session store to be in the same editing context as any favorites that might be created
 * because such favorites need to be related to their category. [NOTE: This is not the proper way to do this -- much
 * better would be to keep the categories in the application's store and make that store a sharedEditingContext but
 * I'm nervous about how to do that, so "waste memory, save brain" ...].
 *
 * (4) _author: the current user owning the associated session.
 *
 * @author gavin
 */
public class UserSessionModel extends Object {
	private static final Logger 	logger = Logger.getLogger(UserSessionModel.class);

	private EOEditingContext		_ec;

//----------------------------------------------------------------------------------------------------------------

	public static final String		AdvertsAreStale = "AdvertsAreStale";	// for notification
	public static final String		UserLogInNotice = "UserLogInNotice";	// for notification

	public UserSessionModel() {
		super();
		_ec = getEditingContext();
		_author = null;
		_loginState = LoginState.NO_USER_YET;
		logger.trace("<-- UserSessionModel created");
	}

	public EOEditingContext getEditingContext() {
		if (null == _ec) {
			_ec = ERXEC.newEditingContext();
			_ec.setSharedEditingContext(null);
			_ec.setUndoManager(null); 				//NOTE: ok ???
		}
		return _ec;
	}

	public void saveChanges() {
		_ec.saveChanges();
	}

	public void initUserSessionModel() {
		logger.trace("--> initUserSessionModel()");
		queryAuthorAdverts();
		logger.info("... queryAuthorAdverts() found " + _authorAdverts.count() + " adverts");

		queryFavoriteAdverts();
		logger.info("... queryFavoriteAdverts() found " + _favoriteAdverts.count() + " adverts");

		getEndCategories();
		logger.info("... queryEndCategories() found " + _endCategories.count() + " categories");

		CoreAssistance.prettyPrintEOEditingContext(getEditingContext());
		_ecm = new EndCategoryMessages();
		logger.info(" ~  prettyprint " + _ecm);
	}

//----------------------------------------------------------------------------------------------------------------
//     AAA      U   U     TTTTTT     H  H      OOO      RRRR       SSS
//    A   A     U   U       TT       H  H     O   O     R   R     S
//    AAAAA     U   U       TT       HHHH     O   O     RRRR       SSS
//    A   A     U   U       TT       H  H     O   O     R R           S
//    A   A      UUU        TT       H  H      OOO      R  RR     SSSS
//
//----------------------------------------------------------------- http://patorjk.com/software/taag/ 'alphabet'--

	private Author					_author = null;

	public Author getAuthor() {
		return _author;
	}

	public enum LoginState {
		NO_USER_YET, LOGGED_IN, FAILED, ISFRIEND, ISWICKED, ERROR
	};

	private LoginState				_loginState;

	public String processAuthorLogin(WORequest request) {
		logger.trace("--> processAuthorLogin(...)");
		if (_author == null) {
			String	authorUniqname = request.headerForKey("REMOTE_USER");		// it's a case-independent key
			if (null == authorUniqname) {										// generate uniqname locally
				final java.util.Random generator = new java.util.Random();
				final NSArray<String> authorNames = CoreSession.properties.getNSArray("session.auth.accept", "()");
				logger.trace("... processAuthorLogin(...) : author options: " + authorNames);
				authorUniqname = authorNames.objectAtIndex(generator.nextInt(authorNames.count()));

				logger.info("    uniqname = <" + authorUniqname + "> from session properties file");
			}
			else {
				logger.info("    uniqname = <" + authorUniqname + "> from 'REMOTE_USER' header");
			}

			/* -------------------------------------------------------------------------------- */

			_loginState = LoginState.FAILED;

			if (CoreAssistance.isStringInArray(authorUniqname, CoreSession.properties.getNSArray("session.auth.reject", "()"))) {
				logger.error("    (" + authorUniqname + ") <-- REJECT (Wicked List)");
				_loginState = LoginState.ISWICKED;
				return "";
			}

			if ((authorUniqname.indexOf("@") > 0)) {
				logger.error("    (" + authorUniqname + ") <-- REJECT (Friend Account)");
				_loginState = LoginState.ISFRIEND;
				return "";
			}

			/* -------------------------------------------------------------------------------- */

			final NSArray<Author> authors = Author.fetchByUniqname(getEditingContext(), authorUniqname);
			switch (authors.count()) {
			case 0: 																	// .. not in the database
				logger.info("    (" + authorUniqname + ") not in the database.");
				_author = Author.createAuthor(getEditingContext(), new NSTimestamp(), authorUniqname);
				getEditingContext().insertObject(_author);
				break;

			case 1: 																	// .. in the database once
				logger.info("    (" + authorUniqname + ") in the database once.");
				_author = authors.objectAtIndex(0);
				break;

			default: 																	// .. in the db too often
				logger.error("    (" + authorUniqname + ") in the db " + authors.count() + " times.");
				_author = authors.objectAtIndex(0);
			}

			_author.setPreviousVisit(new NSTimestamp());
			getEditingContext().saveChanges();

			_loginState = LoginState.LOGGED_IN;
			logger.info("    (" + _author + ") <-- ACCEPT (ITCSSession.LOGGED_IN)");

			NSNotificationCenter.defaultCenter().postNotification(UserSessionModel.UserLogInNotice, this);
		}

		return _author.uniqname();
	}

    public boolean isAuthorLoggedIn() {
		return (LoginState.LOGGED_IN == _loginState);
	}

	public boolean isAuthorFriend() {
		return (LoginState.ISFRIEND == _loginState);
	}

    public boolean isAuthorWicked() {
		return (LoginState.ISWICKED == _loginState);
	}

//----------------------------------------------------------------------------------------------------------------

	private String 						_directActionName;		// the DA by which the user entered

	public String getDirectActionName() {
		return _directActionName;
	}

	public void setDirectActionName (String newValue) {
		_directActionName = newValue;
	}

	public boolean isAuthorAdmin() {
		return (getDirectActionName() != null) && getDirectActionName().equals (DirectAction.AD_WATCH_ACTION);
    }

// ----------------------------------------------------------------------------------------------------------------
//     AAA      DDDD      V     V     EEEE     RRRR      TTTTTT      SSS
//    A   A     D   D     V     V     E        R   R       TT       S
//    AAAAA     D   D      V   V      EEE      RRRR        TT        SSS
//    A   A     D   D       V V       E        R R         TT           S
//    A   A     DDDD         V        EEEE     R  RR       TT       SSSS
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	private NSMutableArray<Advert>			_authorAdverts;			// adverts that the user authored (from db)

	public void queryAuthorAdverts() {
		logger.trace("--> queryAuthorAdverts()");
		_authorAdverts = new NSMutableArray<Advert>(Advert.fetchForAuthor(_ec, _author, new NSTimestamp()));
	}

	public NSArray<Advert> getAuthorAdverts() {
		logger.trace("--> getAuthorAdverts()");
		if (null == _authorAdverts) {
			queryAuthorAdverts();
		}
		return _authorAdverts.immutableClone();
	}

	public void createAuthorAdvertTest() {
		logger.trace("--> createAuthorAdvertTest()");
		final Advert advert = Advert.createAdvert(_ec, _author, null);

		advert.setItemTitle("TEST - DomainID=99999");
		advert.setItemDescription("TEST - Description Description Description Description Description Description");
		advert.setItemPrice(12D);
		advert.setItemLink("http://test.org/TEST");
		advert.setPostedDate(new NSTimestamp());
		advert.setModifyDate(new NSTimestamp());
		advert.setExpiryDate(new NSTimestamp((new DateTime()).plusDays(7).getMillis()));
		advert.setCategoryRelationship(getEndCategories().objectAtIndex(16));
		_ec.saveChanges();

		advert.setNumber((Integer)EOUtilities.primaryKeyForObject(_ec, advert).valueForKey("id"));
		_ec.saveChanges();

		addIntoAuthorAdverts(advert);
	}

	public Advert createAuthorAdvert() {
		logger.trace("--> createAuthorAdvert()");
		CoreAssistance.prettyPrintEOEditingContext(_ec);
		final Advert advert = Advert.createAdvert(_ec, _author, null);
		CoreAssistance.prettyPrintEOEditingContext(_ec);
		return advert;
	}

	public void insertAuthorAdvert(Advert advert) {
		logger.trace("--> insertAuthorAdvert(...)");
		CoreAssistance.prettyPrintEOEditingContext(_ec);
		_ec.saveChanges();
		advert.setNumber((Integer)EOUtilities.primaryKeyForObject(_ec, advert).valueForKey("id"));
		CoreAssistance.prettyPrintEOEditingContext(_ec);
		_ec.saveChanges();
		addIntoAuthorAdverts(advert);
	}
	
	public void cancelAuthorAdvert(Advert advert) {
		CoreAssistance.prettyPrintEOEditingContext(_ec);
		_ec.revert();
		CoreAssistance.prettyPrintEOEditingContext(_ec);
	}

	public void addIntoAuthorAdverts(Advert advert) {
		logger.trace("--> addIntoAuthorAdverts(...)");
		_authorAdverts.addObject(advert);

	    NSNotificationCenter.defaultCenter().postNotification(UserSessionModel.AdvertsAreStale, this);
	}

	public void subFromAuthorAdverts(Advert advert) {
		logger.trace("--> subFromAuthorAdverts(...)");
		//NOTE db action here !!
		_authorAdverts.removeObject(advert);

	    NSNotificationCenter.defaultCenter().postNotification(UserSessionModel.AdvertsAreStale, this);
	}

//----------------------------------------------------------------------------------------------------------------

	private NSArray<Advert> 			_categoryAdverts;

	public void queryCategoryAdverts() {
		logger.trace("--> queryCategoryAdverts()");
		_categoryAdverts = Advert.fetchForCategory(_ec, getDisplayCategory(), new NSTimestamp());
	}

	public NSArray<Advert> getCategoryAdverts() {
		logger.trace("--> getCategoryAdverts()");
		if (null == _categoryAdverts) {
			queryCategoryAdverts();
		}
		return _categoryAdverts;
	}

//----------------------------------------------------------------------------------------------------------------

	private NSMutableArray<Advert> _favoriteAdverts;						// adverts are the user's hotlist (from db)

	public void queryFavoriteAdverts() {
		logger.trace("--> queryFavoriteAdverts()");
		
		DateTime sevenDaysAgoBegin = CoreAssistance.todayBegin().minusDays(7);

		EOQualifier 		qualifier = EOQualifier.qualifierWithQualifierFormat(
				Advert.EXPIRY_DATE_KEY + " >= %@", 
				new NSArray<NSTimestamp>(new NSTimestamp[] { new NSTimestamp(sevenDaysAgoBegin.getMillis()) }));
		_favoriteAdverts = _author.favorites(qualifier).mutableClone();
	}

	public NSArray<Advert> getFavoriteAdverts() {
		logger.trace("--> getFavorites()");
		if (null == _favoriteAdverts) {
			queryFavoriteAdverts();
		}
		return _favoriteAdverts.immutableClone();
	}

	/**
	 * Adds an advert to the author's favorites.
	 *
	 * @param advert
	 *            the ad to add to the user's favorites
	 */
	public void addIntoFavorites(Advert advert) {
		logger.trace("--> addIntoFavorites()");
		_author.addToFavoritesRelationship(advert);
		getEditingContext().saveChanges();
		_favoriteAdverts.addObject(advert);
//--    NSNotificationCenter.defaultCenter().postNotification(UserSessionModel.AdvertsAreStale, this);
	}

	public void subFromFavorites(Advert advert) {
		logger.trace("--> subFromFavorites()");
//TODO -- some database action here, surely
		_favoriteAdverts.removeObject(advert);
//--    NSNotificationCenter.defaultCenter().postNotification(UserSessionModel.AdvertsAreStale, this);
	}

	/**
	 * Removes deleted and expired ads from the the author's favorites.
	 */
	public void cleanUpFavorites() {
		logger.trace("--> cleanUpFavorites()");
		if ((null != _favoriteAdverts) && (_favoriteAdverts.count() > 0)) {
			for (final Advert advert : _favoriteAdverts) {
				if (advert.isBooleanDeleted() || advert.isExpired()) {
					subFromFavorites(advert);
				}
			}
		}
	}

//----------------------------------------------------------------------------------------------------------------

	private NSArray<Advert> 		_foundAdverts;

	public void searchFoundAdverts() {
		logger.trace("--> searchFoundAdverts()");
		_foundAdverts = new NSMutableArray<Advert>();
	}

	public NSArray<Advert> getFoundAdverts() {
		logger.trace("--> getFoundAdverts()");
		if (null == _foundAdverts) {
			searchFoundAdverts();
		}
		return _foundAdverts.immutableClone();
	}
	
	public void setFoundAdverts(NSArray<Advert> foundAdverts) {
		_foundAdverts = foundAdverts;
	}

//----------------------------------------------------------------------------------------------------------------

	private NSMutableArray<Advert> _checkedAdverts = new NSMutableArray<Advert>();

	public NSArray<Advert> getCheckedAdverts() {
		logger.trace("--> getCheckedAdverts()");
		return _checkedAdverts.immutableClone();
	}

	public void setCheckedAdverts(NSArray<Advert> advertArray) {
		logger.trace("--> setCheckedAdverts()");
		_checkedAdverts = advertArray.mutableClone();
	}

	public void addIntoCheckedAdverts(Advert advert) {
		logger.trace("--> addIntoCheckedAdverts()");
		_checkedAdverts.addObject(advert);
	}

	public void subFromCheckedAdverts(Advert advert) {
		logger.trace("--> subFromCheckedAdverts()");
		_checkedAdverts.removeObject(advert);
	}

//----------------------------------------------------------------------------------------------------------------
//     CCC      AAA      TTTTTT     EEEE      GGG       OOO      RRRR      III     EEEE      SSS
//    C        A   A       TT       E        G         O   O     R   R      I      E        S
//    C        AAAAA       TT       EEE      G  GG     O   O     RRRR       I      EEE       SSS
//    C        A   A       TT       E        G   G     O   O     R R        I      E            S
//     CCC     A   A       TT       EEEE      GGG       OOO      R  RR     III     EEEE     SSSS
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	private NSArray<Category> 	_endCategories;		// setIsTopCategory(false) is NOT called, 
													// since the Session use is only to make
													// the relationship with adverts.

	public NSArray<Category> getEndCategories() {
		logger.trace("--> getEndCategories()");
		if (_endCategories != null)
			return _endCategories;

		NSMutableArray<Category> endCategories = new NSMutableArray<Category>();
		for (Category category : Category.fetchTopCategories(_ec)) {
			logger.trace("--- getEndCategories() : top count = " + category.getLongName());
			endCategories.addAll(category.fetchSubCategories(_ec));
		}

		_endCategories = endCategories.immutableClone();
		logger.trace("<-- getEndCategories() : count = " + _endCategories.count());
		return _endCategories;
	}

	private EndCategoryMessages _ecm;

	public EndCategoryMessages getEndCategoryMessages() {
		return _ecm;
	}

//----------------------------------------------------------------------------------------------------------------

	private Category			_displayCategory;

	public Category getDisplayCategory() {
		logger.trace("--> getDisplayCategory()");
		return _displayCategory;
	}

	public void setDisplayCategory(Category category) {
		logger.trace("--> setDisplayCategory(...)");
		_displayCategory = category;
		_categoryAdverts = null;
	}

//----------------------------------------------------------------------------------------------------------------

	public boolean isBroadcastMessageForCategory() {
		return getEndCategoryMessages().isMessageForCategory(getDisplayCategory());
	}

	public String getBroadcastMessageForCategory() {
		return getEndCategoryMessages().getMessageTextForCategory(getDisplayCategory());
	}

	public String getBroadcastTitleForCategory() {
		return getEndCategoryMessages().getMessageTitleForCategory(getDisplayCategory());
	}

	public String getBroadcastTextStyleForCategory() {
		return getEndCategoryMessages().getMessageStyleForCategory(getDisplayCategory());
	}

	public boolean getIsBroadcastMessageForAdCategory(Advert advert) {
		return getEndCategoryMessages().isMessageForCategory(advert.category());
	}

	public boolean getAreBroadcastMessagesForAds() {
		return false; // getEndCategoryMessages().anyMessagesForAds(ads);
	}
	

//----------------------------------------------------------------------------------------------------------------
//    V     V     III     EEEE     W     W     EEEE     RRRR       SSS
//    V     V      I      E        W     W     E        R   R     S
//     V   V       I      EEE      W  W  W     EEE      RRRR       SSS
//      V V        I      E         W W W      E        R R           S
//       V        III     EEEE       W W       EEEE     R  RR     SSSS
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	private String				_viewerName, _pushedViewerName;

	public String getAdvertViewerName() {
		if (null == _viewerName) {
			_viewerName = RecentAdvertsDisplay.class.getName();
		}
		return _viewerName;
	}

	public void setAdvertViewerName(String advertViewerName) {
		_viewerName = advertViewerName;
	}

	public void popAdvertViewerName() {
		_viewerName = _pushedViewerName;
	}


	public WOComponent prepareActiveAdvertsDisplay() {
		logger.trace("--> prepareActiveAdvertsDisplay()");
		_viewerName = ActiveAdvertsDisplay.class.getName();

		return null;
	}

	public WOComponent prepareAuthorAdvertsDisplay() {
		logger.trace("--> prepareAuthorAdvertsDisplay()");
		_viewerName = AuthorAdvertsDisplay.class.getName();

		return null;
	}

	public WOComponent prepareCategoryAdvertsDisplay() {
		logger.trace("--> prepareCategoryAdvertsDisplay()");
		_viewerName = CategoryAdvertsDisplay.class.getName();

		return null;
	}

	public WOComponent prepareFavoriteAdvertsDisplay() {
		logger.trace("--> prepareFavoriteAdvertsDisplay()");
		_viewerName = FavoriteAdvertsDisplay.class.getName();

//		sess.setCurrentAdIndex(0);

		return null;
	}

	public WOComponent prepareFoundAdvertsDisplay() {
		logger.trace("--> prepareFoundAdvertsDisplay()");
		_viewerName = FoundAdvertsDisplay.class.getName();

		return null;
	}

	public WOComponent prepareRecentAdvertsDisplay() {
		logger.trace("--> prepareRecentAdvertsDisplay()");
		_viewerName = RecentAdvertsDisplay.class.getName();

		return null;
	}

	public WOComponent prepareAdvertCreationPanel() {
		logger.trace("--> prepareAdvertCreationPanel()");
		_pushedViewerName = _viewerName;
		logger.trace("--> prepareAdvertCreationPanel() before setting _viewerName");
		_viewerName = AdvertCreationPanel.class.getName();
		logger.trace("--> prepareAdvertCreationPanel() after setting _viewerName");

		return null;
	}

	public WOComponent prepareAuthorContactPanel(Advert selectedAdvert) {
		logger.trace("--> prepareAuthorContactPanel()");
		setSelectedAdvert(selectedAdvert);
		_pushedViewerName = _viewerName;
		_viewerName = AuthorContactPanel.class.getName();

		return null;
	}

    public boolean isTwoColumns() {
		return !(getAdvertViewerName().equals(AuthorContactPanel.class.getName()) ||
			  	 getAdvertViewerName().equals(AdvertCreationPanel.class.getName()));
	}

    private Advert			_selectedAdvert;

    public Advert getSelectedAdvert() {
    	return _selectedAdvert;
    }

    public void setSelectedAdvert(Advert selectedAdvert) {
    	_selectedAdvert = selectedAdvert;
    }


    public final static int				defaultNumberOfAdsPerBatch = 10;
	private int 						_numberOfAdsPerBatch = defaultNumberOfAdsPerBatch;

	/**
	 * This method returns the number of ads to display at once in the ad list panel.
	 *
	 * @return number of ads to show at one time
	 */
	public int getNumberOfAdsPerBatch() {
		return _numberOfAdsPerBatch;
	}

	public void setNumberOfAdsPerBatch(int newValue) {
		_numberOfAdsPerBatch = newValue;
	}

//----------------------------------------------------------------------------------------------------------------
//     SSS      EEEE      AAA      RRRR       CCC     H  H     III     N   N      GGG
//    S         E        A   A     R   R     C        H  H      I      NN  N     G
//     SSS      EEE      AAAAA     RRRR      C        HHHH      I      N N N     G  GG
//        S     E        A   A     R R       C        H  H      I      N  NN     G   G
//    SSSS      EEEE     A   A     R  RR      CCC     H  H     III     N   N      GGG
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	public String 					searchTerms;

	public WOComponent searchOnKeywords() {
		logger.trace("--> searchOnKeywords()");

		setFoundAdverts(findAdsMatchingTerms(searchTerms)) ;
		prepareFoundAdvertsDisplay();

		return null;
	}

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
		final NSArray<String> 		termArray = ApplicationModel.searchTermArray(terms);
		return searchOnContent(termArray, getDisplayCategory().getEndCategoryActiveAdverts());
	}

	private NSArray<Advert> searchOnContent(NSArray<String> searchTermArray, NSArray<Advert> categoryAdverts) {
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

		return EOQualifier.filteredArrayWithQualifier(categoryAdverts, titleOrDescrQual);
	}
}