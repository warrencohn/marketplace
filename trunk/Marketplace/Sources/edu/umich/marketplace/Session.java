package edu.umich.marketplace;

//----------------------------------------------------------------------------------------------------------------
// Session.java
// Project MarketPlace
//
// Created by phayes on Sat Jun 01 2002
//----------------------------------------------------------------------------------------------------------------

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

import edu.umich.marketplace.eof.UserSessionModel;
import edu.umich.marketplace.woc.ErrorPageDefault;
import edu.umich.marketplace.woc.ErrorPageFriend;
import edu.umich.marketplace.woc.ErrorPageWicked;
import er.extensions.appserver.ERXBrowser;

public class Session extends com.ramsayconz.wocore.CoreSession {
	private static final Logger			logger = Logger.getLogger(Session.class);

	private Application 				_app = (Application)WOApplication.application();
	private UserSessionModel			_userSessionModel;

	private ERXBrowser            	 	_browser = null;
	private String						_uniqname = null;		// Cache the users uniqname

	private double						_timeoutMinutes = 2;


	public Session() {
		super();
		logger.trace("-----+ constructor");
	}

    @Override
	public void sessionWillAwake() {
		super.sessionWillAwake();
		logger.trace("-->--> sessionWillAwake");

		if (null == _userSessionModel) {
			getUserSessionModel();
			application.addSession(this, sessionID());
		}

		if (_timeoutMinutes != (timeOut() / 60)) {
			logger.info("  ... session timeout changed from: " + (timeOut() / 60) +
					                           " minutes to: " + _timeoutMinutes + " minutes");
			setTimeOut(_timeoutMinutes * 60);
		}
		logger.trace("<--<-- sessionWillAwake");
	}

    public UserSessionModel getUserSessionModel() {
		if (null == _userSessionModel) {
			logger.trace("--> getUserSessionModel");
			_userSessionModel = new UserSessionModel();
		}
		return _userSessionModel;
	}

	/**
	 * If we have a virgin session awakening (no user), we need to do several things to it so it will be useful ...
	 */
	@Override
	public void awake () {
		super.awake ();
		logger.trace("-->--> awake()");

		sessionInfo.takeValueForKey(true, "SesInUse");

		if (null == _userSessionModel.getAuthor()) {		// we have no user in play yet, but someone knocked on the door
			_timeoutMinutes = Session.properties.getInt("session.timeout.asleepMinutes", "5");
	 		sessionInfo.takeValueForKey(_userSessionModel.processAuthorLogin(context().request()), "uniqname");
	 		if (_userSessionModel.isAuthorLoggedIn()) {		// they've got a uniqname, and aren't 'wicked'
	 			_uniqname = _userSessionModel.getAuthor().uniqname();
	 	 		sessionInfo.takeValueForKey(_uniqname, "uniqname");
	 			logger.info("     | awake() <-- SUCCESS: identity is: " + _uniqname);

	 			_userSessionModel.initUserSessionModel();
	 		}
	 		else if (_userSessionModel.isAuthorFriend()) {	// they've an '@' in their identity ... bad
	 			logger.error("     | awake() <-- FAILURE: identity cantains '@' (friend acount)");
	 			return;
	 		}
	 		else if (_userSessionModel.isAuthorWicked()) {	// they've been 'wicked'
	 			logger.error("     | awake() <-- FAILURE: identity is on The List ('wicked' user)");
	 			return;
	 		}
	 		else {
	 			logger.error("     | awake() <-- FAILURE: identity could not be established");
	 			return;
	 		}
		}
		else {											// already have a good user in play ...
			incHitCount();
			if (getHitCount() == 1) {					// ... and has clicked in the application once
				_timeoutMinutes = Session.properties.getInt("session.timeout.activeMinutes", "30");
			}
			logger.trace("User Hit Count: " + getHitCount());
		}

		if (null == _browser) {
			_browser = browser();
			logger.trace("Browser Record: " + _browser);
		}
	}

	@Override
	public void appendToResponse(WOResponse response, WOContext context) {

		if (_userSessionModel.isAuthorLoggedIn()) {
			super.appendToResponse(response, context);
			return;
		}

		WOComponent			errorPage;
		
		if (_userSessionModel.isAuthorFriend()) {
			errorPage = _app.pageWithName(ErrorPageFriend.class.getName(), context);
			response.setContent(errorPage.generateResponse().content());
			return;
		}

		if (_userSessionModel.isAuthorWicked()) {
			errorPage = _app.pageWithName(ErrorPageWicked.class.getName(), context);
			response.setContent(errorPage.generateResponse().content());
			return;
		}

		errorPage = _app.pageWithName(ErrorPageDefault.class.getName(), context);
		response.setContent(errorPage.generateResponse().content());
	}

	@Override
	public void sleep() {
		if (! _userSessionModel.isAuthorLoggedIn()) {
 			logger.info("     | sleep() <-- FAILURE: nobody logged in so terminate.");
 			terminate();
		}
		sessionInfo.takeValueForKey(false, "SesInUse");
		logger.trace("<--<-- sleep()");
	}
	/**
	 * We override this method so we can null out instance variables when the session terminates.
	 */
	@Override
	public void terminate() {
		logger.trace("-->--> terminate()  [id=" + sessionID() + "] for " + _uniqname);

		_userSessionModel = null;
		_app = null;
		super.terminate();
	}

//----------------------------------------------------------------------------------------------------------------

	//
	// group access checking ...
	//

	public WOComponent getProtectedPage(String uniqname, String pageName, NSArray<String> uniqnames, WOContext context) {
		if ((pageName != null) && (pageName.length() > 0)) {
			if ((uniqnames != null) && uniqnames.containsObject(uniqname)) {
				logger.info("    getProtectedPage: " + uniqname + " has access to page " + pageName);
				return WOApplication.application().pageWithName(pageName, context);
			}
			logger.warn("    getProtectedPage: " + uniqname + " has no access to page " + pageName);
			final ErrorPageDefault errorPage = (ErrorPageDefault)WOApplication.application().pageWithName(ErrorPageDefault.class.getName(), context);
			errorPage.setErrorMessage("You ("+ uniqname + ") don't have access to the page: " + pageName);
			return errorPage;
		}
		logger.error("    getProtectedPage: called with no pagename");
		return null;
	}
}