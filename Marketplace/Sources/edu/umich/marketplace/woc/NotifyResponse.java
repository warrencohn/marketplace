//
// NotifyResponse.java: Class file for WO Component 'ProbeResponse'
// Project ITCSFoundation
//
// Created by gavin on 2/1/07
//

package edu.umich.marketplace.woc;
 
import com.ramsayconz.wocore.woc.CoreStatelessComponent;
import com.webobjects.appserver.WOContext;

public class NotifyResponse extends CoreStatelessComponent {
	
	public String 			responseString;			// accessed via k-v-c

    public NotifyResponse(WOContext context) {
        super(context);
    }
}
