package edu.umich.marketplace;

import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;

public class CleanDatabase extends MarketplaceAction {
	private static final Logger 	logger = Logger.getLogger(CleanDatabase.class);

	private EOEditingContext		_ec	= new EOEditingContext();

	public CleanDatabase() {
	}

	public void doCleanupA() {
		logger.trace("--> doCleanupA()");

//		NSArray<Advert> adsToDelete = Advert.fetchLawAdverts(_ec);
//		logger.trace("... doCleanupA() : advert.count " + adsToDelete.count());
//		for (Advert advert : adsToDelete) {
//			_ec.deleteObject(advert);
//		}

		_ec.saveChanges();
	}

	public void doCleanupB() {
		logger.trace("--> doCleanupB()");

//		NSArray<Category> catsToDelete = Category.fetchLawCategories(_ec);
//		logger.trace("... doCleanupB() : advert.count " + catsToDelete.count());
//		for (Category cat : catsToDelete) {
//			_ec.deleteObject(cat);
//		}
		
		_ec.saveChanges();
	}
}