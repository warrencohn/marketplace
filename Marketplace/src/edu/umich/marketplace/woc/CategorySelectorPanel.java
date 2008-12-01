package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import edu.umich.marketplace.eof.Category;

public class CategorySelectorPanel extends MPComponent {
	private static final Logger			logger = Logger.getLogger(CategorySelectorPanel.class);

	private NSMutableArray<Category> 	_shortenedTopCategories = null;			// shortened top categories get/set as binding
	public Category				  		thisTopCategory, thisEndCategory;

	public CategorySelectorPanel(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}

	/**
	 * This method reversed the open/closed status of category.
	 *
	 * @return the same page with category open, if it was closed, and vice versa
	 */
	public WOComponent swapShowHideOne() {
		if (isExpanded()) {
			logger.trace("--> swapShowHideOne() - hide: " + thisTopCategory.name());
			getShortenedTopCategories().addObject(thisTopCategory);
		}
		else {
			logger.trace("--> swapShowHideOne() - show: " + thisTopCategory.name());
			getShortenedTopCategories().removeObject(thisTopCategory);
		}

		return null;
	}

	/**
	 * This method returns true if category has been expanded by the user.
	 * @return true if category has been expanded; false otherwise
	 */
	public boolean isExpanded() {
		return ! getShortenedTopCategories().containsObject(thisTopCategory);
	}

	public NSArray<Category> getTopCategories() {
		logger.trace("--> getTopCategories()");
		return app.getApplicationModel().getTopCategories();
	}
		
	/** This method returns the list of currently expanded categories.
	 * @return an array of expanded categories
	 */
	public NSMutableArray<Category> getShortenedTopCategories() {
		if (_shortenedTopCategories == null) {
			_shortenedTopCategories = new NSMutableArray<Category>();
			_shortenedTopCategories.addObjectsFromArray(getTopCategories());
		}
		return _shortenedTopCategories;
	}

//	public void setShortenedCategories(NSMutableArray<Category> newShortenedCategories) {
//		logger.trace("--> setExpandedCategories(...)");
//		_shortenedTopCategories = newShortenedCategories;
//	}

	/**
	 * This method displays the ads for category. It has the side effects of setting the current category, reseting the
	 * currentIndex and sortMode, and setting the openAds to be this category's ads.
	 *
	 * @return the same page with category's ads displayed
	 */
	public WOComponent viewAdsForCategory() {
		logger.trace("--> viewAdsForCategory() - thisEndCategory: " + thisEndCategory);
		
//		throw new Exception("Test Exception");

		sess.getUserSessionModel().setDisplayCategory(thisEndCategory);
		sess.getUserSessionModel().prepareCategoryAdvertsDisplay();

		return null;
	}
}