package edu.umich.marketplace;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreAssistance;
import com.ramsayconz.wocore.CoreProperties;
import com.webobjects.appserver.WOApplication;

public class SessionProperties {
	private static final Logger 		logger = Logger.getLogger (SessionProperties.class);
	
	private WOApplication       		_app = WOApplication.application();
	private static CoreProperties		_coreSesProps = null;
	private static final String     	SES_PROP = "session.properties";

	/**
	 * Extracts SESSION level properties from several files, in this sequence:
	 *
	 *
	 *      ... [1] ".../Resources/base-session.properties"
	 *      ... [2]	".../Resources/<h>-session.properties"
	 *      ... [3]	".../Resources/<a>-<h>-session.properties"
	 *      ... [4] "~/session.properties"
	 *
	 *	 once accumulated, these properties are written to a session properties table
	 *	 which is accessed via:   getSessionProps()
	 */

	private SessionProperties (String applicationName) {
		String          hostName = CoreAssistance.getLocalHostName();
		String          thisName = applicationName.toLowerCase();
		String          fileName;
		InputStream     inStream;

		logger.trace("-->--> readSessionProperties()");
		
		_coreSesProps = new CoreProperties();

		//      ... [1] "Resources/base-session.properties"

		fileName = "base-" + SES_PROP;
		inStream = _app.resourceManager().inputStreamForResourceNamed (fileName, null, null);
		if (null == inStream) {
			logger.warn("     | propfile: .../Resources/" + fileName + " missing");
		}
		else {
			_coreSesProps.moreProperties(inStream, false);
			logger.info("     | propfile: .../Resources/" + fileName + " exists and read");
		}

		//      ... [2]	"Resources/<h>-session.properties"

		fileName = hostName + "-" + SES_PROP;
		inStream = _app.resourceManager().inputStreamForResourceNamed (fileName, null, null);
		if (null == inStream) {
			logger.warn("     | propfile: .../Resources/" + fileName + " missing");
		}
		else {
			_coreSesProps.moreProperties(inStream, false);
			logger.info("     | propfile: .../Resources/" + fileName + " exists and read");
		}

		//      ... [3]	"Resources/<a>-<h>-session.properties"

		fileName = thisName + "-" + hostName + "-" + SES_PROP;
		inStream = _app.resourceManager().inputStreamForResourceNamed (fileName, null, null);
		if (null == inStream) {
			logger.warn("     | propfile: .../Resources/" + fileName + " missing");
		}
		else {
			_coreSesProps.moreProperties(inStream, false);
			logger.info("     | propfile: .../Resources/" + fileName + " exists and read");
		}

		//      ... [4] "~/session.properties"

		fileName = System.getProperty ("user.home") + File.separator + SES_PROP;
		try {
			_coreSesProps.moreProperties(new CoreProperties (fileName));
			logger.info("     | propfile:   ~/" + SES_PROP + " exists and read");
		}
		catch (Exception x) {
			logger.warn("     | propfile:   " + fileName + " missing");
		}
	}

	public static synchronized CoreProperties getSessionProperties(String applicationName) {
		if (null == _coreSesProps) {
			new SessionProperties(applicationName);
		}
		return _coreSesProps;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		logger.error("*** You can't clone the singleton SessionProperties.");
		throw new CloneNotSupportedException("You can't clone the singleton SessionProperties");
	}
}