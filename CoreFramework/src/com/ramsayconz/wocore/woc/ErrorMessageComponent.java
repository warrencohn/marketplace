package com.ramsayconz.wocore.woc;

import com.ramsayconz.wocore.CoreApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

@SuppressWarnings("unqualified-field-access")
public class ErrorMessageComponent extends WOComponent {

    /** The URL for a link back into the application. */
    private String _appURL;

    /** The text to display in a link back to the application. */
    private String _linkText;

    /** A text fragment to incorporate into our error message,
     *  or the entire error message itself. */
    private String _message;

    //----------------------------------------------------------------
    // Inherited methods
    //----------------------------------------------------------------
    
    public ErrorMessageComponent (WOContext context) {
        super (context);
    }

    //----------------------------------------------------------------
    // Bindings methods
    //----------------------------------------------------------------

    public String getBugsEmailAddress() {
        return CoreApplication.properties.getProperty ("bugReportMailAddress");
    }

    public String getBugsMailTo() {
        if (getBugsEmailAddress() != null) {
			return "mailto:" + getBugsEmailAddress();
		}
        return null;
    }

    /** Returns the URL for a link back into the application. */
	public String getAppURL() {
        if (_appURL == null) {
            if (valueForBinding ("appURL") != null) {
                _appURL = (String)valueForBinding ("appURL");
            } else {
				_appURL = "";
			}
        }
        return _appURL;
    }

    /** Returns the text to display in a link back to the application. */
    public String getLinkText() {
        if (_linkText == null) {
            if (valueForBinding ("linkText") != null) {
                _linkText = (String)valueForBinding ("linkText");
            } else {
				_linkText = "";
			}
        }
        return _linkText;
    }

    /** Returns the text fragment to incorporate into our 
     *  error message, or the entire error message itself. */
    public String getMessage() {
        if (_message == null) {
            if (valueForBinding ("message") != null) {
                _message = (String)valueForBinding ("message");
            } else {
				_message = "";
			}
        }
        return _message;
    }

}
