package edu.umich.itcs.woc;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class GenerateARuntimeErrorForTesting extends WOComponent {

    public GenerateARuntimeErrorForTesting(WOContext context) {
        super(context);
    }

    public WOComponent generateError() {
		Integer object = null;
		object.intValue();
        return null;
    }

}
