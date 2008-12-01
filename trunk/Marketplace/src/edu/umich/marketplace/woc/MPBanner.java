package edu.umich.marketplace.woc;

//----------------------------------------------------------------------------------------------------------------
// MPBanner.java: Class file for WO Component 'MPBanner'
// Project MarketplaceApp
//
// Created by phayes on Mon Sep 09 2002
//----------------------------------------------------------------------------------------------------------------

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class MPBanner extends MPComponent {
	private static final Logger 	logger = Logger.getLogger (MPBanner.class);

	public MPBanner(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}
}