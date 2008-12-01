package edu.umich.marketplace.eof;

import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

/**
 * +-----------------+------------
 * | key - par - dom | name...
 * +-----------------+------------
 * | 001 - 003 - 091 | SUB
 * +-----------------+------------
 * | 002 - 003 - 091 | SUB
 * +-----------------+------------
 * | 003 - 091 - 091 | TOP
 * +-----------------+------------
 * | ... - ... - ... | ...
 * +-----------------+------------
 * | 091 - ___ - ___ | TOP
 * +-----------------+------------
 * 
 * @author gavin
 *
 */
public class Category extends _Category {
	@SuppressWarnings("unused")
	private static final Logger 	logger = Logger.getLogger(Category.class);

	private NSArray<Advert> 		_endCategoryActiveAdverts = new NSArray<Advert>();
	private int 					_topCategoryActiveAdvertCount = 0;
	private boolean					_isTopCategory = true;
	private NSArray<Category>		_subCategories;

    public NSArray<Advert> getEndCategoryActiveAdverts() {
		return _endCategoryActiveAdverts;
	}

	public void setEndCategoryActiveAdverts(NSArray<Advert> adverts) {
		_endCategoryActiveAdverts = adverts;
	}

	public int getActiveAdvertCount() {
		return (_isTopCategory ? _topCategoryActiveAdvertCount : _endCategoryActiveAdverts.count());
	}

	public void setTopCategoryActiveAdvertCount(int count) {
		_topCategoryActiveAdvertCount = count;
	}


	public void setIsTopCategory(boolean isTopCategory) {
		_isTopCategory = isTopCategory;
	}
	
	public boolean isTopCategory() {
		return _isTopCategory;
	}


	public NSArray<Category> fetchSubCategories(EOEditingContext editingContext) {
		_subCategories = _isTopCategory ? Category.fetchSubCategories(editingContext, this) : new NSArray<Category>();
		return _subCategories;
	}
	
	public NSArray<Category> getSubCategories() {
		return _subCategories;
	}

	public String getLongName() {
		return (_isTopCategory ? ("TOP : " + name()) : (parent().name() + " : " + name()));
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("[ Category | <");
		sb.append(parentID()).append("><").append(id()).append("> | ").append(getLongName());
		return sb.append(" ]").toString();
	}
}