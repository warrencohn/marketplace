package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class ErrorPageFriend extends ErrorPageDefault {
	private static final Logger		logger = Logger.getLogger(AdvertListView.class);

	public ErrorPageFriend(WOContext context) {
		super(context);
	}
}