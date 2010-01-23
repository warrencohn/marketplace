package edu.umich.marketplace.woc;

//----------------------------------------------------------------------------------------------------------------
// MPComponent.java
// Project MarketPlace
//
// Created by phayes on Mon Jun 03 2002
//----------------------------------------------------------------------------------------------------------------

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreHelpRepo;
import com.ramsayconz.wocore.CoreProperties;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

import edu.umich.marketplace.Application;
import edu.umich.marketplace.ColorPalette;
import edu.umich.marketplace.Session;
import edu.umich.marketplace.eof.UserSessionModel;

public class MPComponent extends WOComponent {
	private static final Logger 	logger = Logger.getLogger (MPComponent.class);

    public Application           	app = (Application)WOApplication.application();
    public Session 		            sess = (Session)session();
    public UserSessionModel			userSessionModel = sess.getUserSessionModel();
    public CoreProperties           properties = CoreApplication.properties;

    public MPComponent(WOContext context) {
        super(context);
    }

    /**
     * We override this method and return false; we'll extract our own bindings when we need them
     * and avoid extracting them multiple times during the request/response loop, as is the default.
     */
    @Override
	public boolean synchronizesVariablesWithBindings() {
        return false;
    }

    // --------------------------------------------------------------------------------
    // Action methods
    // --------------------------------------------------------------------------------

    /**
	 * @return a reference to this component for Panel
	 */
    public WOComponent thisComponent() {
        return this;
    }

   	public String getToolTipText() {
		logger.trace("--> getToolTipText() component name: " + this.getClass().getSimpleName());
		
		final Object			items = CoreHelpRepo.getHelpRepo().get("PANELS");
		String					toolTipText = "";
		if ((items != null) && (items instanceof NSMutableDictionary)) {
			toolTipText = (String) ((NSDictionary)items).valueForKey(this.getClass().getSimpleName().trim());
			logger.trace("--> getToolTipText() component text: " + toolTipText);
		}

		return toolTipText;
	}

	//----------------------------------------------------------------------------------------------------------------

    public String getLightAccentColor()  {
        return ColorPalette._liteColor;
    }

	public boolean getBannerDisplayImageOnHelp() {
		return CoreApplication.properties.getBoolean ("bannerDisplayImageOnHelp", "TRUE");
	}

    public boolean getBannerDisplayAppName() {
        return CoreApplication.properties.getBoolean("bannerDisplayAppName", "TRUE");
    }

	public String getBannerImagePath() {
		return CoreApplication.properties.getProperty ("bannerImagePath");
	}

	public String getBannerImageFramework() {
		return CoreApplication.properties.getProperty ("bannerImageFramework");
	}

	public String getBannerImageWidth() {
		return CoreApplication.properties.getProperty ("bannerImageWidth");
	}

	public String getBannerImageHeight() {
		return CoreApplication.properties.getProperty ("bannerImageHeight");
	}

	public String getBannerTableStyle() {
		return "style=\"border-bottom: 4px solid " + ColorPalette._liteColor + ";\"";
	}

	public String getBannerBgColor() {
		return ColorPalette._backColor;
	}

    public boolean isBannerImage() {
        return (getBannerImagePath() != null) && ((getBannerImagePath().length()) > 0) &&
               (getBannerImageFramework() != null) && (getBannerImageFramework().length() > 0) &&
               (getBannerImageWidth() != null) && (getBannerImageWidth().length() > 0) &&
               (getBannerImageHeight() != null) && (getBannerImageHeight().length() > 0);
    }
}