package edu.umich.marketplace;

//----------------------------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------------------------

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreAssistance;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSMutableArray;

import edu.umich.marketplace.woc.ErrorPageDefault;

public class ErrorHandler {
	private static final 			Logger logger = Logger.getLogger(ErrorHandler.class);

	public static WOResponse handlePageRestorationError(WOContext context) {
		final ErrorPageDefault 		errorPage = (ErrorPageDefault) WOApplication.application().
																		pageWithName(ErrorPageDefault.class.getName(), context);
		logger.fatal("    handlePageRestorationError");

		errorPage.setLinkBackToApplication(adaptorPrefixAndAppName(context));
		context.session().terminate();

		return errorPage.generateResponse();
	}

	public static WOResponse handleSessionRestorationError(WOContext context) {
		final ErrorPageDefault 		errorPage = (ErrorPageDefault) WOApplication.application()
																		.pageWithName(ErrorPageDefault.class.getName(), context);
		logger.error("    handleSessionRestorationError");

		errorPage.setLinkBackToApplication(adaptorPrefixAndAppName(context));
		context.session().terminate();

		return errorPage.generateResponse();
//		return makeUserReauthenticate(context).generateResponse(); // TODO change to session timeout error
	}

	public static WOResponse handleError(Throwable x, WOContext context, String linkText) {
		emailTheDeveloper(x, (Session) context.session());

		final ErrorPageDefault 		errorPage = (ErrorPageDefault) WOApplication.application().
																		pageWithName(ErrorPageDefault.class.getName(), context);
		logger.error("    handleError");

		errorPage.setLinkBackToApplication(adaptorPrefixAndAppName(context));
		errorPage.setLinkTextToApplication(linkText);

		return errorPage.generateResponse();
	}

	private static String adaptorPrefixAndAppName(WOContext aContext) {
		String prefixAndAppName = aContext.request().adaptorPrefix();
		final NSMutableArray<String> names = (NSMutableArray<String>) aContext.request().headers().objectForKey("SCRIPT_FILENAME");

		if ((names != null) && (names.count() > 0)) {
			logger.info("we found at least one prefix from SCRIPT_FILENAME");
			String temp = names.objectAtIndex(0);

			if (temp.indexOf(".woa") > 0) {
				temp = temp.substring(0, temp.indexOf(".woa"));
			}

			prefixAndAppName = temp;
			logger.info("the prefix is: " + prefixAndAppName);
		}
		else {
			logger.info("didn't find SCRIPT_FILENAME");
		}

		return prefixAndAppName;
	}

	public static void emailTheDeveloper(Throwable x, Session session) {
		final StringBuffer body = new StringBuffer("Encountered ").
				append(session == null ? "application" : "session").append(" level error").
				append(" with the following info:\n").append("\n-        user: ").
				append(session == null ? "NO SESSION/NO USER" : session.getUserSessionModel().getAuthor().uniqname()).
				append("\n-        port: ").append(WOApplication.application().port()).append("\n\n");

		body.append(session == null ? "NO SESSION" : session.toString()).append("\n\n");
		body.append(CoreAssistance.throwableAsString(x));

		final StringBuffer subject = new StringBuffer("Encountered ").
				append(session == null ? "application" : "session").append(" level error in ").
				append(WOApplication.application().name()).append(" on port ").
				append(WOApplication.application().port());
		
		CoreAssistance.mailToDeveloper(subject.toString(), body.toString());
	}
}