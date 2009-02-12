package edu.umich.marketplace.eof;

import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

/**
 * +-----------------+------------
 * | key - par - dom | name...
 * +-----------------+------------
 * | 001 - 003 - ... | SUB
 * +-----------------+------------
 * | 002 - 003 - ... | SUB
 * +-----------------+------------
 * | 003 - 091 - ... | TOP
 * +-----------------+------------
 * | ... - ... - ... | ...
 * +-----------------+------------
 * 
 * @author gavin
 *
 */
public class Category extends _Category {
    @SuppressWarnings("unused")
	private static final Logger     logger = Logger.getLogger (Category.class);

	private int 					_topCategoryActiveAdvertCount = 0;
	private NSArray<Advert> 		_subCategoryActiveAdverts = new NSArray<Advert>();
	private NSArray<Category>		_subCategories;

    public NSArray<Advert> getEndCategoryActiveAdverts() {
		return _subCategoryActiveAdverts;
	}

	public void setEndCategoryActiveAdverts(NSArray<Advert> adverts) {
		_subCategoryActiveAdverts = adverts;
	}

	public int getActiveAdvertCount() {
		return (isTopCategory() ? _topCategoryActiveAdvertCount : _subCategoryActiveAdverts.count());
	}

	public void setTopCategoryActiveAdvertCount(int count) {
		_topCategoryActiveAdvertCount = count;
	}

	public boolean isTopCategory() {		
		if (null == name() || name().equalsIgnoreCase("LAW") || name().equalsIgnoreCase("UM")) return false;
		return (null == parentID() || parentID() == 0 || parentID() == 91);
	}

	public NSArray<Category> fetchSubCategories(EOEditingContext editingContext) {
		_subCategories = isTopCategory() ? Category.fetchSubCategories(editingContext, this) : new NSArray<Category>();
		return _subCategories;
	}
	
	public NSArray<Category> getSubCategories() {
		return _subCategories;
	}

	public String getLongName() {
		return (null == parent() || isTopCategory() ? name() : parent().name() + " : " + name());
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("[ Category | <");
		return sb.append(id()).append("> | ").append(getLongName()).append(" ]").toString();
	}
}