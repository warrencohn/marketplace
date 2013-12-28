package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class ActiveAdvertsDisplay extends GenericAdvertDisplay {
	private static final Logger		logger = Logger.getLogger(ShowHideAllSwapper.class);

	public ActiveAdvertsDisplay(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}
}