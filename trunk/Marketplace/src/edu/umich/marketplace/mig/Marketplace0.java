package edu.umich.marketplace.mig;

import com.webobjects.eoaccess.EOModel;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import edu.umich.marketplace.eof.Category;
import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationTable;
import er.extensions.migration.ERXModelVersion;
import er.extensions.migration.IERXPostMigration;

public class Marketplace0 extends ERXMigrationDatabase.Migration implements IERXPostMigration {
	@Override
	public NSArray<ERXModelVersion> modelDependencies() {
		return null;
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable advertTable = database.newTableNamed("ADVERT");
		advertTable.newIntegerColumn("OID_AUTHOR", false);
		advertTable.newStringColumn("AUTH_UNIQNAME", 8, false);
		advertTable.newIntegerColumn("OID_AD_CATEGORY", false);
		advertTable.newTimestampColumn("AD_EXP_DATE", true);
		advertTable.newIntegerColumn("OID", false);
		advertTable.newStringColumn("AD_DELETED", 1, false);
		advertTable.newStringColumn("MP_AD_TEXT", 1999, true);
		advertTable.newStringColumn("AD_URL", 100, true);
		advertTable.newDoubleColumn("MP_AD_PRICE", 2, 8, true);
		advertTable.newStringColumn("MP_TITLE", 100, true);
		advertTable.newTimestampColumn("LAST_MODIFIED", true);
		advertTable.newIntegerColumn("AD_NUMBER", true);
		advertTable.newTimestampColumn("MP_AD_POST_DATE", true);
		advertTable.create();
	 	advertTable.setPrimaryKey("OID");

		ERXMigrationTable hotLinkTable = database.newTableNamed("HOTLINK");
		hotLinkTable.newIntegerColumn("OID_AD", false);
		hotLinkTable.newIntegerColumn("OID_USER", false);
		hotLinkTable.create();
	 	hotLinkTable.setPrimaryKey("OID_USER", "OID_AD");

		ERXMigrationTable authorTable = database.newTableNamed("AUTHOR");
		authorTable.newIntegerColumn("OID", false);
		authorTable.newTimestampColumn("MOST_RECENT_VISIT", false);
		authorTable.newStringColumn("UNIQUE_NAME", 40, false);
		authorTable.create();
	 	authorTable.setPrimaryKey("OID");

		ERXMigrationTable categoryTable = database.newTableNamed("CATEGORY");
		categoryTable.newIntegerColumn("OID", false);
		categoryTable.newStringColumn("CATEGORY_NAME", 50, false);
		categoryTable.newIntegerColumn("OID_PARENT", true);
		categoryTable.create();
	 	categoryTable.setPrimaryKey("OID");

		advertTable.addForeignKey("OID_AUTHOR", "AUTHOR", "OID");
		advertTable.addForeignKey("OID_AD_CATEGORY", "CATEGORY", "OID");
		hotLinkTable.addForeignKey("OID_AD", "ADVERT", "OID");
		hotLinkTable.addForeignKey("OID_USER", "AUTHOR", "OID");
		categoryTable.addForeignKey("OID_PARENT", "CATEGORY", "OID");
	}

	public void postUpgrade(EOEditingContext editingContext, EOModel model)
			throws Throwable {
		Category	baseCategory = Category.createCategory(editingContext, 1000, "Category One");
		editingContext.saveChanges();

		Category	sub1Category = Category.createCategory(editingContext, 1010, "Sub Category One");
		sub1Category.setParentID(baseCategory.id());

		baseCategory = Category.createCategory(editingContext, 2000, "Category Two");
		editingContext.saveChanges();

		sub1Category = Category.createCategory(editingContext, 2010, "Sub Category One");
		sub1Category.setParentID(baseCategory.id());
		sub1Category = Category.createCategory(editingContext, 2020, "Sub Category Two");
		sub1Category.setParentID(baseCategory.id());

		baseCategory = Category.createCategory(editingContext, 3000, "Category Three");
		editingContext.saveChanges();

		baseCategory = Category.createCategory(editingContext, 4000, "Category Four");
		editingContext.saveChanges();

	}

	@Override
	public void downgrade(EOEditingContext editingContext,
			ERXMigrationDatabase database) throws Throwable {
		// DO NOTHING
	}
}