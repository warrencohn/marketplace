package edu.umich.marketplace.mig;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationTable;
import er.extensions.migration.ERXModelVersion;

public class Marketplace0 extends ERXMigrationDatabase.Migration {
	@Override
	public NSArray<ERXModelVersion> modelDependencies() {
		return null;
	}
  
	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
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

		ERXMigrationTable advertTable = database.newTableNamed("ADVERT");
		advertTable.newIntegerColumn("OID_AUTHOR", false);
		advertTable.newStringColumn("AUTH_UNIQNAME", 8, false);
		advertTable.newIntegerColumn("OID_AD_CATEGORY", false);
		advertTable.newIntegerColumn("OID_ROOT_AD_CATEGORY", false);
		advertTable.newTimestampColumn("AD_EXP_DATE", true);
		advertTable.newIntegerColumn("OID", false);
		advertTable.newStringColumn("AD_DELETED", 1, false);
		advertTable.newStringColumn("MP_AD_TEXT", 1999, true);
		advertTable.newStringColumn("AD_URL", 100, true);
		advertTable.newIntegerColumn("MP_AD_PRICE", true);
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

		categoryTable.addForeignKey("OID_PARENT", "CATEGORY", "OID");
		advertTable.addForeignKey("OID_AUTHOR", "AUTHOR", "OID");
		advertTable.addForeignKey("OID_AD_CATEGORY", "CATEGORY", "OID");
		advertTable.addForeignKey("OID_ROOT_AD_CATEGORY", "CATEGORY", "OID");
		hotLinkTable.addForeignKey("OID_AD", "ADVERT", "OID");
		hotLinkTable.addForeignKey("OID_USER", "AUTHOR", "OID");
	}

	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		// DO NOTHING
	}
}