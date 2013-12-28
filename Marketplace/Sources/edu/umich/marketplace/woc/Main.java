package edu.umich.marketplace.woc;

//----------------------------------------------------------------------------------------------------------------
// Main.java: Class file for WO Component 'Main'
// Project MarketPlace
//
// Created by phayes on Sat Jun 01 2002
//----------------------------------------------------------------------------------------------------------------

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class Main extends MPComponent {
	private static final Logger 		logger = Logger.getLogger (Main.class);

    public Main(WOContext context) {
        super(context);
        logger.trace ("+++ constructor");
    }
}