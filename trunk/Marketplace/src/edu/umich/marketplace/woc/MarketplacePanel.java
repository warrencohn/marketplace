package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

public class MarketplacePanel extends MPComponent {
	@SuppressWarnings("unused")
	private static final Logger 	logger = Logger.getLogger (MarketplacePanel.class);

	public String 					aMessage;

	private String					_panelTitle = "";
	private String					_panelToolTip = "";

	public static final String 		_SidePanelColor = "#ffffcc";	//  Default Color (Light Yellow)

	public MarketplacePanel(WOContext context) {
		super(context);
        logger.trace ("+++ constructor");
	}

	public boolean hasPanelTitle() {
		_panelTitle = (String) valueForBinding("panelTitle");
		return ((_panelTitle != null) && (_panelTitle.length() > 0));
	}

	public boolean hasHelp() {
		_panelToolTip = (String) valueForBinding("panelToolTip");
		return ((_panelToolTip != null) && (_panelToolTip.length() > 0));
	}

	public String getPanelType() {
		if (valueForBinding("panelType") != null) {
			return (String) valueForBinding("panelType");
		}
		return "side";
	}

	public String getMainPanelColor() {
		final String color = app.getColors().getColorForObject(getPanelType());
		return ((color == null) ? _SidePanelColor : color);
	}

	public boolean isContainer() {
		String		literalContent = (String) valueForBinding("panelText");
		return (literalContent == null || literalContent.length() == 0);
	}

	public String getLiteralContent() {
		return (String) valueForBinding("panelText");
	}

// ---- Alert methods -----------------------------------------------------------------------------

	public String getAlertMessage() {
		return (String) valueForBinding("alertMessage");
	}

	public boolean hasAlertMessage() {
		return ((getAlertMessage() != null) && (getAlertMessage().length() > 0))
				|| ((getAlertMessages() != null) && (getAlertMessages().count() > 0));
	}

	public NSArray getAlertMessages() {
		return (NSArray) valueForBinding("alertMessages");
	}

	public String getAlertColor() {
		return app.getColors().getAlertColor();
	}

	// ---- Error methods -----------------------------------------------------------------------------

	public String getErrorMessage() {
		return (String) valueForBinding("errorMessage");
	}

	public boolean hasErrorMessage() {
		return ((getErrorMessage() != null) && (getErrorMessage().length() > 0))
				|| ((getErrorMessages() != null) && (getErrorMessages().count() > 0));
	}

	public NSArray getErrorMessages() {
		return (NSArray) valueForBinding("errorMessages");
	}

	public String getErrorColor() {
		return app.getColors().getErrorColor();
	}
}