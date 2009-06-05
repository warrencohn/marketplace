// $LastChangedRevision: 5773 $ DO NOT EDIT.  Make changes to Category.java instead.
package edu.umich.marketplace.eof;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Category extends  EOGenericRecord {
	public static final String ENTITY_NAME = "Category";

	// Attributes
	public static final String ID_KEY = "id";
	public static final String NAME_KEY = "name";
	public static final String PARENT_ID_KEY = "parentID";

	// Relationships
	public static final String ADVERTS_KEY = "adverts";
	public static final String PARENT_KEY = "parent";

  private static Logger LOG = Logger.getLogger(_Category.class);

  public Category localInstanceIn(EOEditingContext editingContext) {
    Category localInstance = (Category)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Integer id() {
    return (Integer) storedValueForKey("id");
  }

  public void setId(Integer value) {
    if (_Category.LOG.isDebugEnabled()) {
    	_Category.LOG.debug( "updating id from " + id() + " to " + value);
    }
    takeStoredValueForKey(value, "id");
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_Category.LOG.isDebugEnabled()) {
    	_Category.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public Integer parentID() {
    return (Integer) storedValueForKey("parentID");
  }

  public void setParentID(Integer value) {
    if (_Category.LOG.isDebugEnabled()) {
    	_Category.LOG.debug( "updating parentID from " + parentID() + " to " + value);
    }
    takeStoredValueForKey(value, "parentID");
  }

  public edu.umich.marketplace.eof.Category parent() {
    return (edu.umich.marketplace.eof.Category)storedValueForKey("parent");
  }

  public void setParentRelationship(edu.umich.marketplace.eof.Category value) {
    if (_Category.LOG.isDebugEnabled()) {
      _Category.LOG.debug("updating parent from " + parent() + " to " + value);
    }
    if (value == null) {
    	edu.umich.marketplace.eof.Category oldValue = parent();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "parent");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "parent");
    }
  }
  
  public NSArray<edu.umich.marketplace.eof.Advert> adverts() {
    return (NSArray<edu.umich.marketplace.eof.Advert>)storedValueForKey("adverts");
  }

  public NSArray<edu.umich.marketplace.eof.Advert> adverts(EOQualifier qualifier) {
    return adverts(qualifier, null, false);
  }

  public NSArray<edu.umich.marketplace.eof.Advert> adverts(EOQualifier qualifier, boolean fetch) {
    return adverts(qualifier, null, fetch);
  }

  public NSArray<edu.umich.marketplace.eof.Advert> adverts(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<edu.umich.marketplace.eof.Advert> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(edu.umich.marketplace.eof.Advert.CATEGORY_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = edu.umich.marketplace.eof.Advert.fetchAdverts(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = adverts();
      if (qualifier != null) {
        results = (NSArray<edu.umich.marketplace.eof.Advert>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<edu.umich.marketplace.eof.Advert>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToAdvertsRelationship(edu.umich.marketplace.eof.Advert object) {
    if (_Category.LOG.isDebugEnabled()) {
      _Category.LOG.debug("adding " + object + " to adverts relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "adverts");
  }

  public void removeFromAdvertsRelationship(edu.umich.marketplace.eof.Advert object) {
    if (_Category.LOG.isDebugEnabled()) {
      _Category.LOG.debug("removing " + object + " from adverts relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "adverts");
  }

  public edu.umich.marketplace.eof.Advert createAdvertsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Advert");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "adverts");
    return (edu.umich.marketplace.eof.Advert) eo;
  }

  public void deleteAdvertsRelationship(edu.umich.marketplace.eof.Advert object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "adverts");
    editingContext().deleteObject(object);
  }

  public void deleteAllAdvertsRelationships() {
    Enumeration objects = adverts().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteAdvertsRelationship((edu.umich.marketplace.eof.Advert)objects.nextElement());
    }
  }


  public static Category createCategory(EOEditingContext editingContext, Integer id
, String name
) {
    Category eo = (Category) EOUtilities.createAndInsertInstance(editingContext, _Category.ENTITY_NAME);    
		eo.setId(id);
		eo.setName(name);
    return eo;
  }

  public static NSArray<Category> fetchAllCategories(EOEditingContext editingContext) {
    return _Category.fetchAllCategories(editingContext, null);
  }

  public static NSArray<Category> fetchAllCategories(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Category.fetchCategories(editingContext, null, sortOrderings);
  }

  public static NSArray<Category> fetchCategories(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Category.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Category> eoObjects = (NSArray<Category>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Category fetchCategory(EOEditingContext editingContext, String keyName, Object value) {
    return _Category.fetchCategory(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Category fetchCategory(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Category> eoObjects = _Category.fetchCategories(editingContext, qualifier, null);
    Category eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Category)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Category that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Category fetchRequiredCategory(EOEditingContext editingContext, String keyName, Object value) {
    return _Category.fetchRequiredCategory(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Category fetchRequiredCategory(EOEditingContext editingContext, EOQualifier qualifier) {
    Category eoObject = _Category.fetchCategory(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Category that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Category localInstanceIn(EOEditingContext editingContext, Category eo) {
    Category localInstance = (eo == null) ? null : (Category)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<edu.umich.marketplace.eof.Category> fetchSubCategories(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("subCategories", "Category");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Category>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Category> fetchSubCategories(EOEditingContext editingContext,
	edu.umich.marketplace.eof.Category parentBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("subCategories", "Category");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(parentBinding, "parent");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Category>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Category> fetchTopCategories(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("topCategories", "Category");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Category>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Category> fetchTopCategories(EOEditingContext editingContext)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("topCategories", "Category");
    return (NSArray<edu.umich.marketplace.eof.Category>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
}
