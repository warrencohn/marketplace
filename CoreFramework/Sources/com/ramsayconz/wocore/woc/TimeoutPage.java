//
// TimeoutPage.java: Class file for WO Component 'TimeoutPage'
// Project CoreFramework
//
// Created by gavin on 11/13/05
//

package com.ramsayconz.wocore.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class TimeoutPage extends CoreStatelessComponent {
	private static final long 		serialVersionUID = 1L;
    private static final Logger     logger = Logger.getLogger (TimeoutPage.class);

    public TimeoutPage(WOContext context) {
        super(context);
        logger.fatal("*** FATAL");
    }

}
