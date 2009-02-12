package edu.umich.marketplace.woc;

//----------------------------------------------------------------------------------------------------------------
// MPCatEdit.java: Class file for WO Component 'MPCatEdit'
// Project MarketplaceApp
//
// Created by gavin on 4/10/07
//----------------------------------------------------------------------------------------------------------------

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import edu.umich.marketplace.eof.Category;
import er.extensions.eof.ERXEC;

public class MPCatEdit extends MPComponent {
	private static final Logger logger = Logger.getLogger (MPCatEdit.class);

    private final EOEditingContext  _ec = ERXEC.newEditingContext();

    private NSArray<Category>     	_topCategories;             // all upper categories
    private NSArray<Category>     	_subCategories;             // all lower categories
    private NSArray<Category> 	  	_allCategories;              // all categories (top + sub)

    public Category               	oneCategory;

    public String               	newTopCatName;
    public Category              	iteratedTop;
    public Category               	selectedTop;

    public String                   newSubCatName;
    public Category               	iteratedSub;
    public Category               	selectedSub;

    public String                   errorMessage = "";

    public MPCatEdit(WOContext context) {
        super(context);
        logger.trace("MPCatEdit CONSTRUCTOR ...");
        getAllCategories();								// this caches all the categories
    }

    @Override
	public void awake() {
        errorMessage = "";
    }

    public NSArray<Category> getAllCategories() {
    	if (null == _allCategories) {
            _allCategories = Category.fetchAllCategories(_ec);
            logger.info("getAllCategories (fetches) : " + _allCategories.valueForKey("name"));
    	}
        return _allCategories;
    }
    
    public NSArray<Category> getTopCategories() {
    	if (null == _topCategories) {
    		_topCategories = Category.fetchTopCategories(_ec);
    		logger.info("getTopCategories (fetches) : " + _topCategories.valueForKey("name"));
    	}
        return _topCategories;
    }
    
    public NSArray<Category> getSubCategories() {
    	if (null == _topCategories) {
    		_subCategories = Category.fetchSubCategories(_ec, selectedTop);
    		logger.info("getSubCategories (fetches) : " + _subCategories.valueForKey("name"));
    	}
        return _subCategories;
    }

    // --------------------------------------------------------------------------------

    public WOComponent selTopCategory() {
        return null;
    }

    public WOComponent addTopCategory() {
        final Category                  newCategory = new Category();

        newCategory.setName(newTopCatName);
        newCategory.setParentID(0);
        logger.info("addTopCategory : " + newCategory);

        _ec.insertObject(newCategory);

    //  ec.saveChanges();
	//	application.bringAdsUpToDate();
        return null;
    }

    // --------------------------------------------------------------------------------

    public WOComponent addSubcategory() {
        final Category                  newCategory = new Category();

        newCategory.setName(newSubCatName);
        newCategory.setParentID(selectedTop.id());
        logger.info("addSubcategory : " + newCategory);

        _ec.insertObject(newCategory);

    //  ec.saveChanges();
	//	application.bringAdsUpToDate();
        return null;
    }

    // --------------------------------------------------------------------------------

    public boolean getIsTopSelected() {
        return (selectedTop != null);
    }

}