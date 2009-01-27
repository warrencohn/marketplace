package edu.umich.marketplace;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreHelpRepo;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation._NSUtilities;

import er.extensions.eof.ERXEC;


public class ITCSApplication extends CoreApplication {
	private static final Logger 	logger = Logger.getLogger (ITCSApplication.class);
/*
    static {
		logger.info ("ITCSApplication static initializer -- fired from NSPrincipalClass");
    }
*/
	private EOEditingContext        _applicationEC;
	private ColorPalette			_colorPalette;
	public boolean                  _handleExceptions;

	public ITCSApplication() {
		super();
		logger.trace("+++ constructor");
	}

	@Override
	public void applicationWillFinishLaunching() {
		super.applicationWillFinishLaunching();
		logger.trace("--> applicationWillFinishLaunching()");

		CoreApplication.properties.moreProperties(ApplicationProperties.getApplicationProperties(this.name()));		
		CoreApplication.properties.alphaDump(true);

		setCachingEnabled(CoreApplication.properties.getBoolean("productionDeploy", "FALSE"));
		setSessionTimeOut(new Integer(CoreApplication.properties.getInt("sessionTimeout", "3600")));

		_handleExceptions = CoreApplication.properties.getBoolean("handleExceptions", "TRUE");
		String helpFile = CoreApplication.properties.getString("applicationHelpFile","");

		_colorPalette = ColorPalette.getColorPalette();

		logger.trace(EOModelGroup.defaultGroup());								// check we got them all ?

		_applicationEC = ERXEC.newEditingContext();
		_applicationEC.setUndoManager(null);

        _helpRepo = new CoreHelpRepo(this.name());
        _helpRepo.LoadHelpData(resourceManager().inputStreamForResourceNamed(helpFile, null, null));
		logger.trace("HelpRepo=" + _helpRepo.prettyPrint());

        logger.warn(" ~             JavaMail: " + 
        		((_NSUtilities.classWithName("javax.mail.Session") == null) ? "not " : "") + "available.");
        logger.warn(" ~            SMTP host: " + SMTPHost());
		logger.warn(" ~ directConnectEnabled: " + isDirectConnectEnabled());
		logger.warn(" ~       cachingEnabled: " + isCachingEnabled());

		logger.trace("<-- applicationWillFinishLaunching()");
	}
	
	// -----------------------------------------------------------------------------------------------

	public EOEditingContext getApplicationEC() {
		return _applicationEC;
	}

	// -----------------------------------------------------------------------------------------------

	public ColorPalette getColors() {
		return _colorPalette;
	}

	public String getBannerBgColor() {						// used in binding
		return _colorPalette.getBannerBackgroundColor();
	}

	public String getDarkAccentColor() {
		return _colorPalette.getDarkAccentColor();
	}

	public String getLightAccentColor() {
		return _colorPalette.getLightAccentColor();
	}

	public String getInstructionsColor() {
		return _colorPalette.getInstructionsColor();
	}
}