//
// CoreVersionCode.java: Class file for WO Component 'CoreVersionCode'
// Project CoreFramework
//
// Created by gavin on 5/15/06
//

package com.ramsayconz.wocore.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

@SuppressWarnings("serial")
public class CoreVersionCode extends CoreComponent {
    private static final Logger     logger = Logger.getLogger (CoreVersionCode.class);

    public CoreVersionCode (WOContext context) {
        super (context);
        logger.trace("+++ constructor");
    }

    public String getVersionCode () {
    	return this.application.getConfProp ("application.build.version", "<< no version code >>");
    }
}
