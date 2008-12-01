package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import edu.umich.marketplace.Application;
import edu.umich.marketplace.Session;
import edu.umich.marketplace.eof.UserSessionModel;

public class MarketplaceView extends WOComponent {
	private static final Logger		logger = Logger.getLogger(Session.class);

    protected Application           app = (Application)WOApplication.application();
    protected Session               sess = (Session)session();
    protected UserSessionModel		userSessionModel = sess.getUserSessionModel();

	public MarketplaceView(WOContext context) {
		super(context);
	}

    /**
     * We override this method and return false; we'll extract our own bindings when we need them
     * and avoid extracting them multiple times during the request/response loop, as is the default.
     */
    @Override
	public boolean synchronizesVariablesWithBindings() {
        return false;
    }
}