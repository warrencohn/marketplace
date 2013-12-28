// $LastChangedRevision$ DO NOT EDIT.  Make changes to Advert.java instead.
package edu.umich.marketplace.eof;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Advert extends  EOGenericRecord {
	public static final String ENTITY_NAME = "Advert";

	// Attributes
	public static final String AUTHOR_UNIQNAME_KEY = "authorUniqname";
	public static final String EXPIRY_DATE_KEY = "expiryDate";
	public static final String IS_DELETED_KEY = "isDeleted";
	public static final String ITEM_DESCRIPTION_KEY = "itemDescription";
	public static final String ITEM_LINK_KEY = "itemLink";
	public static final String ITEM_PRICE_KEY = "itemPrice";
	public static final String ITEM_TITLE_KEY = "itemTitle";
	public static final String MODIFY_DATE_KEY = "modifyDate";
	public static final String NUMBER_KEY = "number";
	public static final String POSTED_DATE_KEY = "postedDate";

	// Relationships
	public static final String AUTHOR_KEY = "author";
	public static final String CATEGORY_KEY = "category";
	public static final String FOLLOWERS_KEY = "followers";

  private static Logger LOG = Logger.getLogger(_Advert.class);

  public Advert localInstanceIn(EOEditingContext editingContext) {
    Advert localInstance = (Advert)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String authorUniqname() {
    return (String) storedValueForKey("authorUniqname");
  }

  public void setAuthorUniqname(String value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating authorUniqname from " + authorUniqname() + " to " + value);
    }
    takeStoredValueForKey(value, "authorUniqname");
  }

  public NSTimestamp expiryDate() {
    return (NSTimestamp) storedValueForKey("expiryDate");
  }

  public void setExpiryDate(NSTimestamp value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating expiryDate from " + expiryDate() + " to " + value);
    }
    takeStoredValueForKey(value, "expiryDate");
  }

  public String isDeleted() {
    return (String) storedValueForKey("isDeleted");
  }

  public void setIsDeleted(String value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating isDeleted from " + isDeleted() + " to " + value);
    }
    takeStoredValueForKey(value, "isDeleted");
  }

  public String itemDescription() {
    return (String) storedValueForKey("itemDescription");
  }

  public void setItemDescription(String value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating itemDescription from " + itemDescription() + " to " + value);
    }
    takeStoredValueForKey(value, "itemDescription");
  }

  public String itemLink() {
    return (String) storedValueForKey("itemLink");
  }

  public void setItemLink(String value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating itemLink from " + itemLink() + " to " + value);
    }
    takeStoredValueForKey(value, "itemLink");
  }

  public Double itemPrice() {
    return (Double) storedValueForKey("itemPrice");
  }

  public void setItemPrice(Double value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating itemPrice from " + itemPrice() + " to " + value);
    }
    takeStoredValueForKey(value, "itemPrice");
  }

  public String itemTitle() {
    return (String) storedValueForKey("itemTitle");
  }

  public void setItemTitle(String value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating itemTitle from " + itemTitle() + " to " + value);
    }
    takeStoredValueForKey(value, "itemTitle");
  }

  public NSTimestamp modifyDate() {
    return (NSTimestamp) storedValueForKey("modifyDate");
  }

  public void setModifyDate(NSTimestamp value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating modifyDate from " + modifyDate() + " to " + value);
    }
    takeStoredValueForKey(value, "modifyDate");
  }

  public Integer number() {
    return (Integer) storedValueForKey("number");
  }

  public void setNumber(Integer value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating number from " + number() + " to " + value);
    }
    takeStoredValueForKey(value, "number");
  }

  public NSTimestamp postedDate() {
    return (NSTimestamp) storedValueForKey("postedDate");
  }

  public void setPostedDate(NSTimestamp value) {
    if (_Advert.LOG.isDebugEnabled()) {
    	_Advert.LOG.debug( "updating postedDate from " + postedDate() + " to " + value);
    }
    takeStoredValueForKey(value, "postedDate");
  }

  public edu.umich.marketplace.eof.Author author() {
    return (edu.umich.marketplace.eof.Author)storedValueForKey("author");
  }

  public void setAuthorRelationship(edu.umich.marketplace.eof.Author value) {
    if (_Advert.LOG.isDebugEnabled()) {
      _Advert.LOG.debug("updating author from " + author() + " to " + value);
    }
    if (value == null) {
    	edu.umich.marketplace.eof.Author oldValue = author();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "author");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "author");
    }
  }
  
  public edu.umich.marketplace.eof.Category category() {
    return (edu.umich.marketplace.eof.Category)storedValueForKey("category");
  }

  public void setCategoryRelationship(edu.umich.marketplace.eof.Category value) {
    if (_Advert.LOG.isDebugEnabled()) {
      _Advert.LOG.debug("updating category from " + category() + " to " + value);
    }
    if (value == null) {
    	edu.umich.marketplace.eof.Category oldValue = category();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "category");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "category");
    }
  }
  
  public NSArray<edu.umich.marketplace.eof.Author> followers() {
    return (NSArray<edu.umich.marketplace.eof.Author>)storedValueForKey("followers");
  }

  public NSArray<edu.umich.marketplace.eof.Author> followers(EOQualifier qualifier) {
    return followers(qualifier, null);
  }

  public NSArray<edu.umich.marketplace.eof.Author> followers(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<edu.umich.marketplace.eof.Author> results;
      results = followers();
      if (qualifier != null) {
        results = (NSArray<edu.umich.marketplace.eof.Author>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<edu.umich.marketplace.eof.Author>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }
  
  public void addToFollowersRelationship(edu.umich.marketplace.eof.Author object) {
    if (_Advert.LOG.isDebugEnabled()) {
      _Advert.LOG.debug("adding " + object + " to followers relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "followers");
  }

  public void removeFromFollowersRelationship(edu.umich.marketplace.eof.Author object) {
    if (_Advert.LOG.isDebugEnabled()) {
      _Advert.LOG.debug("removing " + object + " from followers relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "followers");
  }

  public edu.umich.marketplace.eof.Author createFollowersRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Author");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "followers");
    return (edu.umich.marketplace.eof.Author) eo;
  }

  public void deleteFollowersRelationship(edu.umich.marketplace.eof.Author object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "followers");
    editingContext().deleteObject(object);
  }

  public void deleteAllFollowersRelationships() {
    Enumeration objects = followers().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteFollowersRelationship((edu.umich.marketplace.eof.Author)objects.nextElement());
    }
  }


  public static Advert createAdvert(EOEditingContext editingContext, String authorUniqname
, String isDeleted
, edu.umich.marketplace.eof.Author author, edu.umich.marketplace.eof.Category category) {
    Advert eo = (Advert) EOUtilities.createAndInsertInstance(editingContext, _Advert.ENTITY_NAME);    
		eo.setAuthorUniqname(authorUniqname);
		eo.setIsDeleted(isDeleted);
    eo.setAuthorRelationship(author);
    eo.setCategoryRelationship(category);
    return eo;
  }

  public static NSArray<Advert> fetchAllAdverts(EOEditingContext editingContext) {
    return _Advert.fetchAllAdverts(editingContext, null);
  }

  public static NSArray<Advert> fetchAllAdverts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Advert.fetchAdverts(editingContext, null, sortOrderings);
  }

  public static NSArray<Advert> fetchAdverts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Advert.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Advert> eoObjects = (NSArray<Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Advert fetchAdvert(EOEditingContext editingContext, String keyName, Object value) {
    return _Advert.fetchAdvert(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Advert fetchAdvert(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Advert> eoObjects = _Advert.fetchAdverts(editingContext, qualifier, null);
    Advert eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Advert)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Advert that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Advert fetchRequiredAdvert(EOEditingContext editingContext, String keyName, Object value) {
    return _Advert.fetchRequiredAdvert(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Advert fetchRequiredAdvert(EOEditingContext editingContext, EOQualifier qualifier) {
    Advert eoObject = _Advert.fetchAdvert(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Advert that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Advert localInstanceIn(EOEditingContext editingContext, Advert eo) {
    Advert localInstance = (eo == null) ? null : (Advert)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchActive(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("active", "Advert");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchActive(EOEditingContext editingContext,
	NSTimestamp expiryDateBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("active", "Advert");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(expiryDateBinding, "expiryDate");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForAuthor(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forAuthor", "Advert");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForAuthor(EOEditingContext editingContext,
	edu.umich.marketplace.eof.Author authorBinding,
	NSTimestamp expiryDateBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forAuthor", "Advert");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(authorBinding, "author");
    bindings.takeValueForKey(expiryDateBinding, "expiryDate");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForCategory(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forCategory", "Advert");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForCategory(EOEditingContext editingContext,
	edu.umich.marketplace.eof.Category categoryBinding,
	NSTimestamp expiryDateBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forCategory", "Advert");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(categoryBinding, "category");
    bindings.takeValueForKey(expiryDateBinding, "expiryDate");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForNumber(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forNumber", "Advert");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForNumber(EOEditingContext editingContext,
	Integer numberBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forNumber", "Advert");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(numberBinding, "number");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForUniqname(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forUniqname", "Advert");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchForUniqname(EOEditingContext editingContext,
	String authorUniqnameBinding,
	NSTimestamp expiryDateBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("forUniqname", "Advert");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(authorUniqnameBinding, "authorUniqname");
    bindings.takeValueForKey(expiryDateBinding, "expiryDate");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchToExpire(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("toExpire", "Advert");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Advert> fetchToExpire(EOEditingContext editingContext,
	NSTimestamp beginDateBinding,
	NSTimestamp closeDateBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("toExpire", "Advert");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(beginDateBinding, "beginDate");
    bindings.takeValueForKey(closeDateBinding, "closeDate");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Advert>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
}
