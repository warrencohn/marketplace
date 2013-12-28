package com.ramsayconz.wocore.woc;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

@SuppressWarnings("serial")
public class CauseRuntimeFault extends WOComponent {

    public CauseRuntimeFault(WOContext context) {
        super(context);
    }

    @SuppressWarnings("null")
	public WOComponent generateError() {
		Integer object = null;
		object.intValue();
        return null;
    }

}
