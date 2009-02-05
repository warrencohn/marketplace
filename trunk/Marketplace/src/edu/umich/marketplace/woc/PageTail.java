package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreAssistance;
import com.webobjects.appserver.WOContext;

public class PageTail extends MPComponent {
	private static final Logger 	logger = Logger.getLogger (PageTail.class);

	private String 					_organizationMail; 		// for the footer


	public PageTail(WOContext context) {
		super(context);
		logger.trace("+++ constructor");
	}

	public String getOrganizationMail() {
		if (_organizationMail == null) {
			_organizationMail = CoreApplication.properties.getProperty("organizationMail", "");
			if (_organizationMail.indexOf("mailto") < 0) {
				_organizationMail = "mailto:" + _organizationMail;
			}
		}
		return _organizationMail;
	}

	public String getCopyrightYear() {
		return "2002-" + (new DateTime()).toString("Y");
	}


    public String organizationName = CoreApplication.properties.getProperty ("organizationName", "");
    public String organizationLink = CoreApplication.properties.getProperty ("organizationLink", "");

	public String  appVersion   = CoreApplication.properties.getString("buildVersion", "N.M");
	public String  svnVersion   = CoreApplication.properties.getString("svnVersion", "<<svn>>");
	public String  appBuildTime = CoreApplication.properties.getString("buildTimeString", "<<dev>>");
	public String  appStartTime = CoreApplication.properties.getString("startTimeString", "<<tim>>");
	public String  appID		= CoreApplication.properties.getString("applicationPort", "0");
	public String  appInstance  = CoreApplication.properties.getString("applicationInstance", "<<app>>");

    private String 		_fontColor;

    /**
	 * Derives the font color for the text of this component. Tries to read it from the binding; if that fails, assigns
	 * it to the default, white.
	 */
    public String getFontColor() {
        if (_fontColor == null) {
            _fontColor = (String)valueForBinding ("fontColor");
            if ((_fontColor == null) || (_fontColor.length() == 0)) {
				_fontColor = "white";
			}
        }
        return _fontColor;
    }

    public void setFontColor(String color) {
    	_fontColor = color;
    }

    public String getAppHostname() {
		return CoreAssistance.getLocalHostName();
    }

    public String getUniqname() {
    	return (null == sess.getUserSessionModel().getAuthor() ? "" : sess.getUserSessionModel().getAuthor().uniqname());
    }
}