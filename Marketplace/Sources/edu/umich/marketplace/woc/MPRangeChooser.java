package edu.umich.marketplace.woc;
//
// MPRangeChooser.java: Class file for WO Component 'MPRangeChooser'
// Project MarketPlace
//
// Created by phayes on Wed Jun 05 2002
//

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

public class MPRangeChooser extends MPComponent {
	private static final Logger 	logger = Logger.getLogger (MPRangeChooser.class);

    public Integer 					aBatch;
    public Integer 					selectedBatch;

	public NSArray<Integer> pageBatchPopupList = new NSArray<Integer> (new Integer[] {
		new Integer (10), new Integer (25), new Integer (50), new Integer (100)
	});

    public MPRangeChooser(WOContext context) {
        super(context);
    }

    /**
     * This method sets the number of ads per page to the page
     * batch the user selected.
     * @return the same page with the new page batch in effect
     */
    public WOComponent changeNumberOfAdsPerBatch() {
		logger.trace("changeNumberOfAdsPerBatch(" + getSelectedBatch() + ")");

		sess.getUserSessionModel().setNumberOfAdsPerBatch(getSelectedBatch().intValue());

		return context().page();
	}

	/**
	 * @return the page batch number the user selected from pageBatchChoices
	 */
	public Integer getSelectedBatch() {
		if (selectedBatch == null) {
			selectedBatch = new Integer(sess.getUserSessionModel().getNumberOfAdsPerBatch());
		}
		return selectedBatch;
	}
}
