package edu.umich.marketplace;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreApplication;

public class ColorPalette {
	private static final Logger 	logger = Logger.getLogger (ColorPalette.class);

	private static ColorPalette 	colorPalatteRef;
	private static String			_mainPanelColor, _sidePanelColor, _editPanelColor, _helpPanelColor,
                                	_alertColor, _errorColor, _darkAccentColor, _lightAccentColor,
                                	_instructionsColor, _bannerBackgroundColor, _broadcastTextColor;

	private static final String		ITCS_Orange = "#FFCC00",
                                	ITCS_Red = "#FFCCCC",
                                	ITCS_White = "#ffffff",
                                	ITCS_Lt_Yellow = "#ffffcc",
                                	ITCS_Dk_Yellow = "#ffff9d",
                                	ITCS_Lt_Blue = "#3366AA",
                                	ITCS_Md_Blue = "#ffffcc",
                                	ITCS_Dk_Blue = "#003399";

	private ColorPalette() {
		_mainPanelColor = CoreApplication.properties.getProperty("marketplaceApp.mainPanelColor", ITCS_Dk_Yellow);
		_sidePanelColor = CoreApplication.properties.getProperty("marketplaceApp.sidePanelColor", ITCS_Lt_Yellow);
		_editPanelColor = CoreApplication.properties.getProperty("marketplaceApp.editPanelColor", ITCS_Md_Blue);
		_helpPanelColor = CoreApplication.properties.getProperty("marketplaceApp.helpPanelColor", ITCS_White);
		_alertColor = CoreApplication.properties.getProperty("marketplaceApp.alertColor", ITCS_Orange);
		_errorColor = CoreApplication.properties.getProperty("marketplaceApp.errorColor", ITCS_Red);
		_darkAccentColor = CoreApplication.properties.getProperty("marketplaceApp.darkAccentColor", ITCS_Dk_Blue);
		_lightAccentColor = CoreApplication.properties.getProperty("marketplaceApp.lightAccentColor", ITCS_Lt_Blue);
		_instructionsColor = CoreApplication.properties.getProperty("marketplaceApp.instructionsColor", "#000066");
		
		_bannerBackgroundColor = CoreApplication.properties.getProperty("bannerBgColor", "#3366cc");
		_broadcastTextColor = CoreApplication.properties.getProperty("broadcastTextColor", "#9a2f27");
	}

	public static synchronized ColorPalette getColorPalette() {
		if (null == colorPalatteRef) {
			colorPalatteRef = new ColorPalette();
		}
		return colorPalatteRef;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		logger.error("*** You can't clone the singleton ColorPalette.");
		throw new CloneNotSupportedException("You can't clone the singleton ColorPalette");					// that'll teach 'em
	}
	
	public String getMainPanelColor() {
		return _mainPanelColor;
	}

	public String getSidePanelColor() {
		return _sidePanelColor;
	}

	public String getEditPanelColor() {
		return _editPanelColor;
	}
	
	public String getHelpPanelColor() {
		return _helpPanelColor;
	}

	public String getAlertColor() {
		return _alertColor;
	}

	public String getErrorColor() {
		return _errorColor;
	}

	public String getLightAccentColor() {
		return _lightAccentColor;
	}

	public String getDarkAccentColor() {
		return _darkAccentColor;
	}

	public String getInstructionsColor() {
		return _instructionsColor;
	}
	
	public String getBannerBackgroundColor() {
		return _bannerBackgroundColor;
	}
	
	public String getBroadcastTextColor() {
		return _broadcastTextColor;
	}
	
	public String getColorForObject (String objectType) {
		if (objectType.equals ("edit")) {
			return getEditPanelColor();
		} else if (objectType.equals ("main")) {
			return getMainPanelColor();
		} else if (objectType.equals ("side")) {
			return getSidePanelColor();
		} else if (objectType.equals ("alert")) {
			return getAlertColor();
		} else if (objectType.equals ("error")) {
			return getErrorColor();
		} else if (objectType.equals ("outline")) {
			return getLightAccentColor();
		} else if (objectType.equals ("lightAccent")) {
			return getLightAccentColor();
		} else if (objectType.equals ("darkAccent")) {
			return getDarkAccentColor();
		} else if (objectType.equals ("instruction")) {
			return getInstructionsColor();
		}

		return "";
	}
}