//
// CoreBuildTime.java: Class file for WO Component 'CoreBuildTime'
// Project CoreFramework
//
// Created by gavin on 7/16/05
//

package com.ramsayconz.wocore.woc;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;

@SuppressWarnings("serial")
public class CoreBuildTime extends CoreStatelessComponent {
    private static final Logger     logger = Logger.getLogger (CoreBuildTime.class);
    
    public CoreBuildTime(WOContext context) {
        super(context);
        logger.trace ("+++ constructor");
    }
    
    public String getBuildTime () {
        return ((CoreApplication)WOApplication.application()).getConfProp ("application.buildTime", "<< no build time >>");
    }
}