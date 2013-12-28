//
// CoreStandardPage.java: Class file for WO Component 'CoreStandardPage'
// Project CoreFramework
//
// Created by gavin on 10/13/06
//

package com.ramsayconz.wocore.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

public class CoreStandardPage extends CoreComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4218676140418163782L;
	private static final Logger     logger = Logger.getLogger (CoreStandardPage.class);
    
    public CoreStandardPage(WOContext context) {
        super(context);
        logger.trace("+++ constructor");
    }
}