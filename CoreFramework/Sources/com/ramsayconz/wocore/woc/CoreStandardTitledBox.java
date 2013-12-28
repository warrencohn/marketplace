//
// CoreStandardTitledBox.java: Class file for WO Component 'CoreStandardTitledBox'
// Project CoreFramework
//
// Created by gavin on 4/21/07
//

package com.ramsayconz.wocore.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;

@SuppressWarnings("serial")
public class CoreStandardTitledBox extends CoreStatelessComponent {
    private static final Logger     logger = Logger.getLogger (CoreStandardTitledBox.class);
    
    public CoreStandardTitledBox(WOContext context) {
        super(context);
        logger.trace("+++ constructor");
    }
    
    /**
     * Returns the boxHead string from the bindings, or "" if missing.
     *
     * @return      boxHead from the bindings
     */
    
    public String getBoxHead() {
        Object      boxHead = valueForBinding("boxHead");            
        logger.info("*** boxHead binding is " + boxHead);
        if (null == boxHead) {
            boxHead = "<<< boxHead UNDEFINED >>>";
        }
        
        if (!(boxHead instanceof String)) {
            logger.warn("*** boxHead binding is a " + boxHead.getClass().getName() + ".");
            boxHead = boxHead.toString();
        }
        
        return (String)boxHead;
    }

}
