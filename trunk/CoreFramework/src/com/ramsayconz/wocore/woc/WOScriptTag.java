//WOScriptTag.java: Class file for WO Component 'WOScriptTag'
//Project CoreFramework

package com.ramsayconz.wocore.woc;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver._private.WOConstantValueAssociation;
import com.webobjects.appserver._private.WOHTMLURLValuedElement;
import com.webobjects.foundation.NSDictionary;

public class WOScriptTag extends WOHTMLURLValuedElement {
//  private static final Logger     logger = Logger.getLogger (WOScriptTag.class);

	public WOScriptTag(String name, NSDictionary<String, WOAssociation> assocationsDictionary, WOElement template) {
		super("script", assocationsDictionary, template);
		if (this._associations.objectForKey("type") == null) {
			this._associations.setObjectForKey(new WOConstantValueAssociation("application/javascript"), "type");
		}
	}

	@Override
	protected String urlAttributeName() {
		return "src";
	}
}