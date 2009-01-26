package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreAssistance;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import edu.umich.marketplace.eof.Advert;

public class AuthorContactPanel extends GenericAdvertDisplay {
	private static final Logger 	logger = Logger.getLogger(AuthorContactPanel.class);

	public boolean 					previewMode = false;
	public String 					messageBody;

	public AuthorContactPanel(WOContext context) {
        super(context);
		logger.trace("+++ constructor");
    }

	public WOComponent cancelMessage() {
		sess.getUserSessionModel().popAdvertViewerName();
		previewMode = false;
		messageBody = null;
		return null;
	}

	public WOComponent editMessage() {
		previewMode = false;
		return null;
	}

	public WOComponent previewMessage() {
		if ((messageBody != null) && (messageBody.length() > 0) && (getEmailSubject() != null) &&
		                           (mailRecipients() != null) && (mailRecipients().count() > 0)) {
			previewMode = true;
			resetAlertMessage();
		}
		else {
			setAlertMessage ("Your message could not be sent - please try again.");
		}

		return null;
	}

	public WOComponent sendMessage() {
		if (CoreAssistance.sendMail(mailRecipients(), senderEmail(), null, getEmailSubject(), messageBody)) {
			messageBody = null;
			previewMode = false;
			sess.getUserSessionModel().popAdvertViewerName();
		}
		else {
			setAlertMessage ("Your message could not be sent - please try again.");
		}
		return null;
	}

	public Advert getSelectedAd() {
		return sess.getUserSessionModel().getSelectedAdvert();
	}

	private NSArray<String> mailRecipients() {
		return new NSArray<String>(sess.getUserSessionModel().getSelectedAdvert().author().getAuthorEmail());
	}

	private String senderEmail() {
		return sess.getUserSessionModel().getAuthor().getAuthorEmail();
	}

	public String getEmailSubject() {
		return "Your " + app.name() + " advert '" + sess.getUserSessionModel().getSelectedAdvert().itemTitle() + "'";
	}
}