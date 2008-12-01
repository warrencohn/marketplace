package edu.umich.marketplace.woc;

import com.ramsayconz.wocore.CoreApplication;
import com.webobjects.appserver.WOContext;

public class UsagePolicyPanel extends MPComponent {

	public UsagePolicyPanel(WOContext context) {
		super(context);
	}

	public String getAppropriateUsePanelTitle() {
		return CoreApplication.properties.getProperty("appropriateUsePanelTitle");
	}

	public String getAppropriateUsePanelContent() {
		return CoreApplication.properties.getProperty("appropriateUsePanelContent");
	}
}