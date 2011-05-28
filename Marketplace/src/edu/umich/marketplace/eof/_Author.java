// $LastChangedRevision$ DO NOT EDIT.  Make changes to Author.java instead.
package edu.umich.marketplace.eof;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Author extends  EOGenericRecord {
	public static final String ENTITY_NAME = "Author";

	// Attributes
	public static final String PREVIOUS_VISIT_KEY = "previousVisit";
	public static final String UNIQNAME_KEY = "uniqname";

	// Relationships
	public static final String ADVERTS_KEY = "adverts";
	public static final String FAVORITES_KEY = "favorites";

  private static Logger LOG = Logger.getLogger(_Author.class);

  public Author localInstanceIn(EOEditingContext editingContext) {
    Author localInstance = (Author)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp previousVisit() {
    return (NSTimestamp) storedValueForKey("previousVisit");
  }

  public void setPreviousVisit(NSTimestamp value) {
    if (_Author.LOG.isDebugEnabled()) {
    	_Author.LOG.debug( "updating previousVisit from " + previousVisit() + " to " + value);
    }
    takeStoredValueForKey(value, "previousVisit");
  }

  public String uniqname() {
    return (String) storedValueForKey("uniqname");
  }

  public void setUniqname(String value) {
    if (_Author.LOG.isDebugEnabled()) {
    	_Author.LOG.debug( "updating uniqname from " + uniqname() + " to " + value);
    }
    takeStoredValueForKey(value, "uniqname");
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
      EOQualifier inverseQualifier = new EOKeyValueQualifier(edu.umich.marketplace.eof.Advert.AUTHOR_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
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
    if (_Author.LOG.isDebugEnabled()) {
      _Author.LOG.debug("adding " + object + " to adverts relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "adverts");
  }

  public void removeFromAdvertsRelationship(edu.umich.marketplace.eof.Advert object) {
    if (_Author.LOG.isDebugEnabled()) {
      _Author.LOG.debug("removing " + object + " from adverts relationship");
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

  public NSArray<edu.umich.marketplace.eof.Advert> favorites() {
    return (NSArray<edu.umich.marketplace.eof.Advert>)storedValueForKey("favorites");
  }

  public NSArray<edu.umich.marketplace.eof.Advert> favorites(EOQualifier qualifier) {
    return favorites(qualifier, null);
  }

  public NSArray<edu.umich.marketplace.eof.Advert> favorites(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<edu.umich.marketplace.eof.Advert> results;
      results = favorites();
      if (qualifier != null) {
        results = (NSArray<edu.umich.marketplace.eof.Advert>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<edu.umich.marketplace.eof.Advert>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }
  
  public void addToFavoritesRelationship(edu.umich.marketplace.eof.Advert object) {
    if (_Author.LOG.isDebugEnabled()) {
      _Author.LOG.debug("adding " + object + " to favorites relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "favorites");
  }

  public void removeFromFavoritesRelationship(edu.umich.marketplace.eof.Advert object) {
    if (_Author.LOG.isDebugEnabled()) {
      _Author.LOG.debug("removing " + object + " from favorites relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "favorites");
  }

  public edu.umich.marketplace.eof.Advert createFavoritesRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Advert");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "favorites");
    return (edu.umich.marketplace.eof.Advert) eo;
  }

  public void deleteFavoritesRelationship(edu.umich.marketplace.eof.Advert object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "favorites");
    editingContext().deleteObject(object);
  }

  public void deleteAllFavoritesRelationships() {
    Enumeration objects = favorites().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteFavoritesRelationship((edu.umich.marketplace.eof.Advert)objects.nextElement());
    }
  }


  public static Author createAuthor(EOEditingContext editingContext, NSTimestamp previousVisit
, String uniqname
) {
    Author eo = (Author) EOUtilities.createAndInsertInstance(editingContext, _Author.ENTITY_NAME);    
		eo.setPreviousVisit(previousVisit);
		eo.setUniqname(uniqname);
    return eo;
  }

  public static NSArray<Author> fetchAllAuthors(EOEditingContext editingContext) {
    return _Author.fetchAllAuthors(editingContext, null);
  }

  public static NSArray<Author> fetchAllAuthors(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Author.fetchAuthors(editingContext, null, sortOrderings);
  }

  public static NSArray<Author> fetchAuthors(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Author.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Author> eoObjects = (NSArray<Author>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Author fetchAuthor(EOEditingContext editingContext, String keyName, Object value) {
    return _Author.fetchAuthor(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Author fetchAuthor(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Author> eoObjects = _Author.fetchAuthors(editingContext, qualifier, null);
    Author eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Author)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Author that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Author fetchRequiredAuthor(EOEditingContext editingContext, String keyName, Object value) {
    return _Author.fetchRequiredAuthor(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Author fetchRequiredAuthor(EOEditingContext editingContext, EOQualifier qualifier) {
    Author eoObject = _Author.fetchAuthor(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Author that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Author localInstanceIn(EOEditingContext editingContext, Author eo) {
    Author localInstance = (eo == null) ? null : (Author)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<edu.umich.marketplace.eof.Author> fetchByUniqname(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("ByUniqname", "Author");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Author>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<edu.umich.marketplace.eof.Author> fetchByUniqname(EOEditingContext editingContext,
	String uniqnameBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("ByUniqname", "Author");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(uniqnameBinding, "uniqname");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<edu.umich.marketplace.eof.Author>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
}
