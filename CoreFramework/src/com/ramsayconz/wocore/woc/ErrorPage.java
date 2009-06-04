//
// ErrorPage.java: Class file for WO Component 'ErrorPage'
// Project CoreFramework
//
// Created by gavin on 11/13/05
//

package com.ramsayconz.wocore.woc;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

@SuppressWarnings("unqualified-field-access")
public class ErrorPage extends CoreStatelessComponent {
    private static final Logger     logger = Logger.getLogger (TimeoutPage.class);

    private Exception               _exception;
    public StackTraceElement        thisSTE;

    public ErrorPage(WOContext context) {
        super(context);
        logger.fatal("*** FATAL");
    }

    public void setException(Exception x) {
        _exception = x;
    }
    
    public String getExceptionMessage() {
        return _exception.toString();
    }
    
    public NSArray<StackTraceElement> getExceptionStacktrace () {
        return new NSArray<StackTraceElement> (_exception.getStackTrace());
    }
}
