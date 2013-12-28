package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class HelpPage extends MPComponent {
	@SuppressWarnings("unused")
	private static final Logger 	logger = Logger.getLogger(HelpPage.class);

	public HelpPage(WOContext context) {
		super(context);
	}

	public String getPageTitle() {
		return app.name() + " - Help";
	}
}