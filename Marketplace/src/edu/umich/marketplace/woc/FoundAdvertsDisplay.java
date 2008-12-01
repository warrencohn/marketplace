package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class FoundAdvertsDisplay extends GenericAdvertDisplay {
	private static final Logger 	logger = Logger.getLogger(AuthorContactPanel.class);

	public FoundAdvertsDisplay(WOContext context) {
		super(context);
	}
}