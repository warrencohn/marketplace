package edu.umich.marketplace;

//----------------------------------------------------------------------------------------------------------------
// DirectAction.java
// Project MarketPlace
//
// Created by phayes on Sat Jun 01 2002
//----------------------------------------------------------------------------------------------------------------

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreDirectAction;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORedirect;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSArray;

import edu.umich.marketplace.woc.ErrorPageDefault;
import edu.umich.marketplace.woc.MPCatEdit;
import edu.umich.marketplace.woc.Main;
import edu.umich.marketplace.woc.NotifyResponse;

public class DirectAction extends CoreDirectAction {
	private static final Logger 	logger = Logger.getLogger (DirectAction.class);

    public static final String  	AD_WATCH_ACTION = "adWatch";
    private static final String  	CAT_EDIT_ACTION = "catEdit";


    public DirectAction(WORequest aRequest) {
        super(aRequest);
    }

    @Override
	public WOActionResults probeAction() {
        logger.info("=== [[ PROBE ]] =======================================");
        return pageWithName("ProbeResponse");
    }

    @Override
	public WOActionResults logoutAction() {
		logger.info("=== [[ LOGOUT ]] ======================================");

		final WORedirect 			redirect = (WORedirect) pageWithName(WORedirect.class.getName());
		redirect.setUrl("https://weblogin.umich.edu/cgi-bin/logout?http://marketplace.umich.edu");
		return redirect;
	}

    public WOActionResults murderAction() {
        logger.info("=== [[ MURDER ]] ======================================");
        logger.warn("+++ terminating with System.exit(0) ...");
        System.exit(0);
        return null;
    }

    public WOActionResults notifyAction() {
        logger.info("=== [[ NOTIFY ]] ======================================");
        NotifyResponse		notifyResponse = (NotifyResponse) pageWithName(NotifyResponse.class.getName());
        notifyResponse.takeValueForKey(Application.notifyPendingExpiries(), "responseString");
        return notifyResponse;
    }

    public WOActionResults adWatchAction() {
        logger.info("=== [[ ADWATCH ]] =====================================");

        final Session             	session = (Session)session();
        final NSArray<String>      	adminGroup = CoreApplication.properties.getNSArray("adWatch.adminGroup", "()");
		final Object				result = session.getProtectedPage(session.getUserSessionModel().getAuthor().uniqname(), 
														Main.class.getName(), adminGroup, context());

		if (result instanceof WOComponent) {
			session.getUserSessionModel().setDirectActionName(AD_WATCH_ACTION);
			return (WOActionResults) result;
		}

		return setupErrorPage("");
	}

    public WOActionResults catEditAction() {
        logger.info("=== [[ CATEDIT ]] =====================================");

        final Session              	session = (Session)session();
        final NSArray<String>     	adminGroup = CoreApplication.properties.getNSArray("catEdit.adminGroup", "()");
		final Object 				result = session.getProtectedPage(session.getUserSessionModel().getAuthor().uniqname(), 
														MPCatEdit.class.getName(), adminGroup, context());

		if (result instanceof WOComponent) {
			logger.info("catEditAction component is instanceof WOComponent");
			session.getUserSessionModel().setDirectActionName(CAT_EDIT_ACTION);
			return (WOActionResults) result;
		}
		else {
			logger.info("catEditAction component is type: " + result.getClass().getName());
		}
		
		return setupErrorPage("");
	}

    /**
	 * This method overrides a method in WODirectAction so we can catch bad direct action requests and present an error
	 * page instead. We call super.performActionNamed to try to find a method of the form xxxxAction(), in this class. 
	 * If that fails, display an error page stating that the requested service was not found.
	 * 
	 * @param anActionName -
	 *            name of the action to perform
	 */
    @Override
	public WOActionResults performActionNamed(String anActionName) {
		WOActionResults results = null;

		try {
			results = super.performActionNamed(anActionName);
		}
		catch (final Exception e) {
			logger.error("*** exception in super.performActionNamed(" + anActionName + "); didn't find action method");
		}

		if (results == null) {
			results = setupErrorPage(anActionName);
		}

		return results;
	}

    /**
	 * @return an ITCSErrorPage with the given action name and error type.
	 */
    public WOActionResults setupErrorPage(String anActionName) {
		final ErrorPageDefault errorPage = (ErrorPageDefault) pageWithName(ErrorPageDefault.class.getName());
		errorPage.setPageTitle("Missing Action Method");
		errorPage.setPanelTitle("Action method '" + anActionName + "' doesn't exist");
		errorPage.setErrorMessage("Didn't find action method: " + anActionName);
		errorPage.setOkToResume(true);
		return errorPage;
	}
}
