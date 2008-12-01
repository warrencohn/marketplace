package edu.umich.marketplace.woc;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class AdminInstructionsPanel extends MPComponent {
	public AdminInstructionsPanel(WOContext context) {
		super(context);
	}

	public WOComponent bringAdsUpToDate() {
		app.getApplicationModel().refreshActiveAdverts();
		return null;
	}
}