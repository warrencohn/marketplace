//
// WODiv.java: Class file for WO Component 'WODiv'
// Project CoreFramework
//
// Created by gavin on 4/21/07
//

package com.ramsayconz.wocore.woc;

import java.text.MessageFormat;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSDictionary;

public class WODiv extends WODynamicElement {
    private static final Logger     	logger = Logger.getLogger (WODiv.class);
    
    private static final String     	TAG = "div";
	private static final String 		STYLE_KEY = "style";
	private static final String 		PARAM_KEY = "param";

    NSDictionary<String, WOAssociation>	_associations;
    WOElement                       	_children;
    
    public WODiv(String aName, NSDictionary<String, WOAssociation> anAssociationsDict, WOElement anElement) {
        super(aName, null, anElement);

        // WODiv: name=com.ramsayconz.wocore.woc.WODiv,
        //        dict={class = <com.webobjects.appserver._private.WOConstantValueAssociation: value=The class of the element>;
        //                style = <com.webobjects.appserver._private.WOConstantValueAssociation: value=color : #f00;>; 
        //                   id = <com.webobjects.appserver._private.WOConstantValueAssociation: value=testcontent>;
        //                title = <com.webobjects.appserver._private.WOConstantValueAssociation: value=tool tip>; }, 
        //          elem=<com.webobjects.appserver.parser.woml.WOHTMLBareStringReabable>"
        
        this._associations = new NSDictionary<String, WOAssociation>(anAssociationsDict.mutableClone());
        this._children = anElement;

        logger.trace((new StringBuilder()).append("name=").append(aName).
                                           append("; dict=").append(this._associations).
                                           append("; elem=").append(this._children).toString());

    }
    
    @Override
	public void appendToResponse(WOResponse aResponse, WOContext aContext) {
        aResponse.appendContentString("<" + TAG);
        if (this._associations != null) {
        //  appendAttributesToResponse(aResponse, aContext);
            appendStyleToResponse(aResponse, aContext);
        }
        aResponse.appendContentCharacter('>');
        if (this._children != null) {
            this._children.appendToResponse(aResponse, aContext);
        } else {
        	logger.error("*** " + TAG + " WITH NO CONTENT -");
            aResponse.appendContentString("- " + TAG + " WITH NO CONTENT -");
        }
        aResponse.appendContentString("</" + TAG + ">");
    }
    
    @Override
	public WOActionResults invokeAction(WORequest aRequest, WOContext aContext) {
        return null;
    }
    
    @Override
	public String toString() {
        return (new StringBuilder()).append("<" + TAG).append(super.toString()).append("/>").toString();
    }    

    /**
     * 
     * @param aResponse
     * @param aContext
     */
    @SuppressWarnings("unused")
	private void appendAttributesToResponse(WOResponse aResponse, WOContext aContext) {
        WOComponent 					aComponent = aContext.component();

        for (String thisKey : this._associations.keySet()) {
        	Object 				thisVal = this._associations.objectForKey(thisKey).valueInComponent(aComponent);
            
            logger.trace("  KEY=" + thisKey);
            logger.trace("VALUE=" + thisVal + " (" + thisVal.getClass().getName() + ")");
            
            if (thisVal != null) {
                String 					description = thisVal.toString();
                aResponse._appendTagAttributeAndValue(thisKey, description, true);
            }
        }
    }

    private void appendStyleToResponse(WOResponse aResponse, WOContext aContext) {
        WOComponent 			aComponent = aContext.component();

        if (this._associations.containsKey(STYLE_KEY)) {
        	String 				styleValue = this._associations.objectForKey(STYLE_KEY).valueInComponent(aComponent).toString();       
        	Vector<Object> 		params = new Vector<Object>();

        	for (String thisKey : this._associations.keySet()) {
            	if (thisKey.startsWith(PARAM_KEY)) {
					int index = -1;

					try {
						index = Integer.parseInt(thisKey.substring(PARAM_KEY.length()));
					}
					catch (Exception e) { }

					if (index != -1) {
						Object 	paramValue = this._associations.objectForKey(thisKey).valueInComponent(aComponent);
						if (paramValue == null) {
							paramValue = "";
						}
						_insertBindingIntoArrayAtIndex(paramValue, params, index);
					}
            	}
            }
            aResponse._appendTagAttributeAndValue(STYLE_KEY, MessageFormat.format(styleValue, params.toArray()), false);
        }
    }
    
	/**
	 * Method to insert the specified binding into the array at the appropriate index.  Since we
	 * have no way of knowing what the index value specified was, or if it is within the bounds
	 * of the current array, we must check the value against the array size.  If the insertion
	 * point is past the array size, we add empty strings into the array until the array is large
	 * enough to handle the insert.
	 *
	 * @param paramValue    the binding to insert
	 * @param array         the array to insert the string into
	 * @param index         the index at which to insert the binding into the array
	 */

	private void _insertBindingIntoArrayAtIndex (Object paramValue, Vector<Object> vector, int index) {
		if ((paramValue != null) && (vector != null)) {
			int limit = index + 1;
			if (limit > vector.size()) {
				vector.setSize(limit);
			}
			vector.set (index, paramValue);
		}
	}
}
