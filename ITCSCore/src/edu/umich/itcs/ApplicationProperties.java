package edu.umich.itcs;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreAssistance;
import com.ramsayconz.wocore.CoreProperties;
import com.webobjects.appserver.WOApplication;

public class ApplicationProperties {
	private static final Logger 		logger = Logger.getLogger(ApplicationProperties.class);

	private WOApplication       		app = WOApplication.application();
	private static CoreProperties		_coreAppProps = null;
	private static final String         APP_PROP = "application.properties";	// base-name of properties
	private static final String         BUILD_PROPS_FILE = "deploy.properties";	// build time and version

	//------------------------------------------------------------------------------
	// Properties
	//
	//	We use three separate sets of properties:
	//
	//		System Properties -- this is the normal set of system properties
	//		that the Java environment provides, PLUS any properties gathered
	//		from the following files (WebObjects just does this for you):
	//
	//		+-----------------------------------+-----------------------------------+
	//		| from Application Resources        | from Users Home Directory         |
	//		|      Framework Resources          |                                   |
	//		+-----------------------------------+-----------------------------------+
	//		| "Properties"                      | "WebObjects.Properties"           |
	//		+-----------------------------------+-----------------------------------+
	//
	//		PLUS properties from the command line ... which may be set in Monitor
	//
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	//
	//		Application Properties -- these are properties read at application
	//		start time from the following files.  The order in which they are read
	//      is specified by the [n] mark, and property values already set will be
	//      over-ridden if defined in a later file:
	//
	//		+-----------------------------------+-----------------------------------+
	//		| from Application Resources        | from Users Home Directory         |
	//		+-----------------------------------+-----------------------------------+
	//	[1] | "base-application.properties"     | "~/application.properties" [5]    |
	//	[2]	| "<h>-application.properties"      |                                   |
	//	[3]	| "<a>-<h>-application.properties"  |                                   |
	//	[4]	| "deploy.properties"               |                                   |
	//		+-----------------------------------+-----------------------------------+
	//
	//	NB: Here, and for sessions, the file "<h>-application|session.properties"
	//		is actually named with the first part of the internet address of the
	//		host the code is running on ... "canute-application.properties" will
	//		be read when code runs on host <canute.ns.itd.umich.edu>
	//
	//	NB: For applications, the file "<a>-<h>-application.properties" is named
	//		with a=the "Monitor application name" and h=the first part of the internet 
	//      address of the host the code is running on ...
	//      for example, "marketplace-decaf-application.properties" will be read when 
	//      Marketplace runs on host <decaf.ds.itd.umich.edu>
	//
	//	NB:	"host-application.properties" is being deprecated 			### gav|aug15/03
	//	NB:	"host-application.properties" is gone                       ### gav|jul18/05
	//  NB: "build.properties" <-- "deploy.properties" (ant conflict)   ### gav|sep11/07
	//
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	//
	//		Session Properties -- these are properties read at session start
	//		time from the following files:
	//
	//		+-----------------------------------+-----------------------------------+
	//		| Application Resources             | Users Home Directory              |
	//		+-----------------------------------+-----------------------------------+
	//	[1]	| "base-session.properties"         | "~/session.properties" [3]        |
	//	[2]	| "<h>-session.properties"          |                                   |
	//		+-----------------------------------+-----------------------------------+
	//
	//	NB: For sessions, any file called "<a>-<h>-session.properties" is not used.
	//
	//------------------------------------------------------------------------------

	private ApplicationProperties (String applicationName) {
		String          hostName = CoreAssistance.getLocalHostName();
		String          thisName = applicationName.toLowerCase();
		String          fileName;
		InputStream     inStream;
		
		logger.trace("--> readApplicationProperties()");

		_coreAppProps = new CoreProperties();

		//      ... [1] "Resources/base-application.properties"

		fileName = "base-" + APP_PROP;
		inStream = app.resourceManager().inputStreamForResourceNamed (fileName, null, null);
		if (null == inStream) {
			logger.warn("  | propfile: .../Resources/" + fileName + " missing");
		}
		else {
			_coreAppProps.moreProperties(inStream, false);
			logger.info("  | propfile: .../Resources/" + fileName + " exists and read");
		}

		//      ... [2]	"Resources/<h>-application.properties"

		fileName = hostName + "-" + APP_PROP;
		inStream = app.resourceManager().inputStreamForResourceNamed (fileName, null, null);
		if (null == inStream) {
			logger.warn("  | propfile: .../Resources/" + fileName + " missing");
		}
		else {
			_coreAppProps.moreProperties(inStream, false);
			logger.info("  | propfile: .../Resources/" + fileName + " exists and read");
		}

		//      ... [3]	"Resources/<a>-<h>-application.properties"

		fileName = thisName + "-" + hostName + "-" + APP_PROP;
		inStream = app.resourceManager().inputStreamForResourceNamed (fileName, null, null);
		if (null == inStream) {
			logger.warn("  | propfile: .../Resources/" + fileName + " missing");
		}
		else {
			_coreAppProps.moreProperties(inStream, false);
			logger.info("   | propfile: .../Resources/" + fileName + " exists and read");
		}

		//      ... [4]	"Resources/deploy.properties"

		fileName = BUILD_PROPS_FILE;
		inStream = app.resourceManager().inputStreamForResourceNamed (fileName, null, null);
		if (null == inStream) {
			logger.warn("  | propfile: .../Resources/" + fileName + " missing");
		}
		else {
			_coreAppProps.moreProperties(inStream, false);
			logger.info("  | propfile: .../Resources/" + fileName + " exists and read");
		}

		//      ... [5] "~/application.properties"

		fileName = System.getProperty ("user.home") + File.separator + APP_PROP;
		try {
			_coreAppProps.moreProperties(new CoreProperties (fileName));
			logger.info("  | propfile:   ~/" + APP_PROP + " exists and read");
		}
		catch (Exception x) {
			logger.warn("  | propfile:   " + fileName + " missing");
		}
	}

	public static synchronized CoreProperties getApplicationProperties(String applicationName) {
		if (null == _coreAppProps) {
			new ApplicationProperties(applicationName);
		}
		return _coreAppProps;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		logger.error("*** You can't clone the singleton ApplicationProperties.");
		throw new CloneNotSupportedException("You can't clone the singleton ApplicationProperties");	// that'll teach 'em
	}
}