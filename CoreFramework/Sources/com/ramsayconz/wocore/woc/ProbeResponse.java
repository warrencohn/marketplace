//
// ProbeResponse.java: Class file for WO Component 'ProbeResponse'
// Project ITCSFoundation
//
// Created by gavin on 2/1/07
//

package com.ramsayconz.wocore.woc;
 
import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreProperties;
import com.webobjects.appserver.WOContext;

public class ProbeResponse extends CoreStatelessComponent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2994665350772126609L;
	public CoreProperties           properties = CoreApplication.properties;

    public ProbeResponse(WOContext context) {
        super(context);
    }

}
