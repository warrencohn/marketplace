package edu.umich.marketplace.woc;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

public class GenericAdvertDisplay extends MPComponent {

    public static final String		_EmptyHotlist    = "You currently have no ads in your favorites.",
    								_EmptyMyAds      = "You do not have any currently posted ads.",
                                    _EmptyRecentAds  = "There are no recent ads.",
                                    _EmptySearch     = "Found no matches.";


	public GenericAdvertDisplay(WOContext context) {
		super(context);
	}

	@Override
	public void appendToResponse(WOResponse response, WOContext context) {
		super.appendToResponse(response, context);
		resetAlertMessage();							// reset any messages after display
	}

// ----------------------------------------------------------------------------------------------------------------

    protected String                alertMessage;		// any message for the Panel alert box

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String newValue) {
        alertMessage = newValue;
    }

    public void resetAlertMessage() {
        alertMessage = null;
    }
}