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
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import edu.umich.marketplace.eof.Category;
import er.extensions.eof.ERXEC;

public class MPCatEdit extends MPComponent {
	private static final Logger logger = Logger.getLogger (MPCatEdit.class);

    private Category              	_baseCategory;              // or "domain"
    private NSArray<Category>     	_topCategories;             // upper categories only
    private NSArray<Category>     	_subCategories;             // lower categories only

    public NSArray<Category> 	  	allCategories;              // all categories, except domain
    public Category               	oneCategory;

    public String               	newTopCatName;
    public Category              	iteratedTop;
    public Category               	selectedTop;

    public String                   newSubCatName;
    public Category               	iteratedSub;
    public Category               	selectedSub;

    private final EOEditingContext  _ec = ERXEC.newEditingContext();
    private EOFetchSpecification    _fetchSpec;

    public String                   errorMessage = "";

    public MPCatEdit(WOContext context) {
        super(context);
        logger.trace("MPCatEdit CONSTRUCTOR ...");

        _fetchSpec = EOModelGroup.defaultGroup().fetchSpecificationNamed("UMCategories0", "Category");
        final NSArray<Category>       	nsa = _ec.objectsWithFetchSpecification(_fetchSpec);
        logger.info("FS[UMCategories0] : " + nsa.valueForKey("name"));

        if (nsa.count() == 1) {
			_baseCategory = nsa.objectAtIndex(0);
		}
		else {
            errorMessage = "Got zero or more than one 'UM' domain ...";
            logger.error("Got zero or more than one 'UM' domain - BAD!");
        }
        fillEditingContext();
    }

    @Override
	public void awake() {
        errorMessage = "";
    }

    public NSArray<Category> getTopCategories() {
        _fetchSpec = EOModelGroup.defaultGroup().fetchSpecificationNamed("UMCategories1", "Category");
        _topCategories = _ec.objectsWithFetchSpecification(_fetchSpec);
        logger.info("FS[UMCategories1] : " + _topCategories.valueForKey("name"));
        return _topCategories;
    }

    public NSArray<Category> getSubCategories() {
        final NSMutableArray          args = new NSMutableArray();

        args.removeAllObjects();
        args.addObject(selectedTop.id());

        final EOQualifier             myQualifier = EOQualifier.qualifierWithQualifierFormat("oidParent = %@", args);

        _fetchSpec = new EOFetchSpecification("Category", myQualifier, null);
        _subCategories = _ec.objectsWithFetchSpecification(_fetchSpec);
        logger.info("FS[UMCategoriesX] : " + _subCategories.valueForKey("name"));
        return _subCategories;
    }

    // --------------------------------------------------------------------------------

    public WOComponent selCategory() {
        return null;
    }

    public WOComponent addCategory() {
        final Category                  newCategory = new Category();

        newCategory.setName(newTopCatName);
//        newCategory.setDomainRelationship(_baseCategory);
        newCategory.setParentRelationship(_baseCategory);
        logger.info("newCategory : " + newCategory);

        _ec.insertObject(newCategory);

    //  ec.saveChanges();
	//	application.bringAdsUpToDate();
        return null;
    }

    /**
     *  for now, because it disrupts the applications state too much to have basic EOs
     *    suddenly vanish without trace, and because we've not catered for the impact
     *    of creating orphaned ads, we don't delete anything.
     */

    public WOComponent delCategory() {
        fillEditingContext();

        if (selectedTop != null) {
            _ec.deleteObject(selectedTop);
        }

    //  ec.saveChanges();
	//	application.bringAdsUpToDate();
        selectedTop = null;                 // to obscure SubCats List after Delete ...
        return null;
    }

    // --------------------------------------------------------------------------------

    public WOComponent selSubCategory() {
        return null;
    }

    public WOComponent addSubcategory() {
        final Category                  newCategory = new Category();

        newCategory.setName(newSubCatName);
//        newCategory.setDomainRelationship(_baseCategory);
        newCategory.setParentRelationship(selectedTop);
        logger.info("newCategory : " + newCategory);

        _ec.insertObject(newCategory);

    //  ec.saveChanges();
	//	application.bringAdsUpToDate();
        return null;
    }

    /**
     *  for now, because it disrupts the applications state too much to have basic EOs
     *    suddenly vanish without trace, and because we've not catered for the impact
     *    of creating ads with deleted parents, etc, we don't delete anything.
     */

    public WOComponent delSubcategory() {
        fillEditingContext();
        if (selectedSub != null) {
            _ec.deleteObject(selectedSub);
        }

    //  ec.saveChanges();
        selectedSub = null;                 // why ???
	//	application.bringAdsUpToDate();
        return null;
    }

    // --------------------------------------------------------------------------------

    private void fillEditingContext () {
        _fetchSpec = EOModelGroup.defaultGroup().fetchSpecificationNamed("UMCategories2", "Category");
        allCategories = _ec.objectsWithFetchSpecification(_fetchSpec);
        logger.info("FS[UMCategories2] : " + allCategories.valueForKey("name"));
    }

    public boolean getIsTopSelected() {
        return (selectedTop != null);
    }

}
