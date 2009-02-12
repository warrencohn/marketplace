package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class CategoryAdvertsDisplay extends GenericAdvertDisplay {
	private static final Logger 	logger = Logger.getLogger(CategoryAdvertsDisplay.class);

	public CategoryAdvertsDisplay(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}

	public String getPanelTitle() {
		return "you are viewing the <" + sess.getUserSessionModel().getDisplayCategory().getLongName() + "> adverts.";
	}
}