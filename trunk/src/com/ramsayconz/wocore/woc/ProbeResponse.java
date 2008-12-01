//
// ProbeResponse.java: Class file for WO Component 'ProbeResponse'
// Project ITCSFoundation
//
// Created by gavin on 2/1/07
//

package com.ramsayconz.wocore.woc;
 
import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreProperties;
import com.webobjects.appserver.WOContext;

public class ProbeResponse extends CoreStatelessComponent {
	private static final Logger 	logger = Logger.getLogger(ProbeResponse.class);

    public CoreProperties           properties = CoreApplication.properties;

    public ProbeResponse(WOContext context) {
        super(context);
    }

}
