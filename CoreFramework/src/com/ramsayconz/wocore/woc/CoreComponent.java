//
//  CoreComponent.java
//  CoreFramework
//
//  Created by Gavin Eadie on 8/12/05.
//  Copyright (c) 2005 Ramsay Consulting. All rights reserved.
//

package com.ramsayconz.wocore.woc;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreConfiguration;
import com.ramsayconz.wocore.CoreProperties;
import com.ramsayconz.wocore.CoreSession;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

@SuppressWarnings("serial")
public class CoreComponent extends ERXComponent {
    private static final Logger     logger = Logger.getLogger (CoreComponent.class);

    public CoreApplication          application = (CoreApplication)WOApplication.application();
    public CoreSession              session = (CoreSession)session();

    public CoreProperties           properties = CoreApplication.properties;
    public CoreConfiguration        configuration = CoreApplication.configuration;

    public CoreComponent(WOContext context) {
        super(context);
        logger.trace("+++ constructor");
   }
}
