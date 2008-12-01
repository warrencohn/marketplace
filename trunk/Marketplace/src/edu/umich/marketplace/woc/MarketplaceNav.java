package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class MarketplaceNav extends edu.umich.marketplace.woc.MPComponent {
	private static final Logger 	logger = Logger.getLogger(MarketplaceNav.class);

	String			nameGreatGrandParent;

	public MarketplaceNav(WOContext context) {
		super(context);
	}

	@Override
	public void awake() {
		nameGreatGrandParent = ((null == parent()) || 
				                (null == parent().parent()) || 
				                (null == parent().parent().parent())) ? "" : parent().parent().parent().name();
		logger.trace("<-- awake() nameGreatGrandParent: " + nameGreatGrandParent);
	}
	
	public WOComponent returnToMain() {
		return pageWithName(Main.class.getName());
	}

	public WOComponent goToHelp() {
		return pageWithName(HelpPage.class.getName());
	}

	public boolean onMainPage() {
		return (0 < nameGreatGrandParent.indexOf("Main"));
	}
	
	public boolean onErrorPage() {
		return (0 < nameGreatGrandParent.indexOf("ErrorPage"));
	}

	public boolean onHelpPage() {
		return (0 < nameGreatGrandParent.indexOf("HelpPage"));
	}
	
	public boolean tryToReturn() {
		if (onErrorPage()) {
			logger.trace("--> tryToReturn() : " + ((ErrorPageDefault)parent().parent().parent()).getOkToResume());
			return ((ErrorPageDefault)parent().parent().parent()).getOkToResume();
		}
		else
			return true;
	}
}