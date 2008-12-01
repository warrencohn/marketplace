package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class RecentAdvertsDisplay extends GenericAdvertDisplay {
	private static final Logger 	logger = Logger.getLogger(AuthorContactPanel.class);

	public RecentAdvertsDisplay(WOContext context) {
		super(context);
	}
}