package com.ramsayconz.wocore;

//
// CoreDirectAction.java
//
// Created by gavin on Tue Jun 01 2007
//

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.woc.ProbeResponse;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;

public class CoreDirectAction extends ERXDirectAction {
    private static final Logger     logger = Logger.getLogger (CoreDirectAction.class);
    
    public CoreDirectAction(WORequest aRequest) {
        super(aRequest);
    }
    
    public WOActionResults probeAction() {
        logger.trace("=== : probe");
        return pageWithName(ProbeResponse.class.getName());
    }
}