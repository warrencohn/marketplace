package edu.umich.marketplace.woc;

import com.ramsayconz.wocore.CoreApplication;
import com.webobjects.appserver.WOContext;

public class OtherResourcesPanel extends MPComponent {

    public OtherResourcesPanel(WOContext context) {
        super(context);
    }

    public String getResourcesPanelTitle() {
        return CoreApplication.properties.getProperty ("resourcesPanelTitle");
    }

    public String getResourcesPanelString() {
        return CoreApplication.properties.getProperty ("resourcesPanelString");
    }
}