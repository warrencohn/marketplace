package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * This reusable component displays "expand all" and "collapse all" links, and enables the user to expand or collapse
 * all elements in a list at once.
 */

public class ShowHideAllSwapper extends MPComponent {
	private static final Logger		logger = Logger.getLogger(ShowHideAllSwapper.class);

	private String			  		_showLabel;						// text for "show all" link
	private String			  		_hideLabel;						// text for "hide all" link

	private NSMutableArray	  		_visibleItems;					// the set of currently shown items

	public ShowHideAllSwapper(WOContext context) {
		super(context);
	}

	/** @return the same page with all items expanded */
	public WOComponent hideAll() {
		getVisibleItems().addObjectsFromArray((NSArray) valueForBinding("workingItems"));
		setVisibleItems(getVisibleItems());
		return null;
	}

	/** @return the same page with all items collapsed */
	public WOComponent showAll() {
		getVisibleItems().removeAllObjects();						// empty the "visible" list
		setVisibleItems(getVisibleItems());
		return context().page();
	}

	public String getShowLabel() {
		if (_showLabel == null) {
			_showLabel = (String) valueForBinding("showLabel");
			if ((_showLabel == null) || (_showLabel.length() == 0)) {
				_showLabel = "show all";
			}
		}
		return _showLabel;
	}

	public String getHideLabel() {
		if (_hideLabel == null) {
			_hideLabel = (String) valueForBinding("hideLabel");
			if ((_hideLabel == null) || (_hideLabel.length() == 0)) {
				_hideLabel = "hide all";
			}
		}
		return _hideLabel;
	}

	private NSMutableArray getVisibleItems() {
		if (_visibleItems == null) {
			_visibleItems = (NSMutableArray) valueForBinding("visibleItems");
			logger.trace("--> getVisibleItems().count(): " + _visibleItems.count());
			if (_visibleItems == null) {
				setVisibleItems(new NSMutableArray());
			}
		}
		return _visibleItems;
	}

	/**
	 * Sets the internal value for openItems to newValue, and sets the binding "openItems" to newValue.
	 */
	private void setVisibleItems(NSMutableArray newValue) {
		logger.trace("--> setVisibleItems(...)");
		_visibleItems = newValue;
		setValueForBinding(newValue, "visibleItems");
	}
}
