package edu.umich.marketplace;

//----------------------------------------------------------------------------------------------------------------
//Application.java
//Project MarketPlace
//
//Created by phayes on Sat Jun 01 2002
//----------------------------------------------------------------------------------------------------------------

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import com.ramsayconz.wocore.CoreApplication;
import com.ramsayconz.wocore.CoreAssistance;
import com.ramsayconz.wocore.CoreHelpRepo;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation._NSUtilities;

import edu.umich.marketplace.eof.ApplicationModel;
import er.extensions.appserver.ERXApplication;
import er.extensions.eof.ERXEC;

public class Application extends com.ramsayconz.wocore.CoreApplication {
	private static final Logger 		logger = Logger.getLogger(Application.class);

	private EOEditingContext    	    _applicationEC;
	public boolean                  	_handleExceptions;
	private ApplicationModel			_applicationModel;

	public ApplicationModel getApplicationModel() {
		_applicationModel = ApplicationModel.getApplicationModel();
		return _applicationModel;
	}

//----------------------------------------------------------------------------------------------------------------

	public static void main(String argv[]) {
		ERXApplication.main(argv, Application.class);
	}

	/**
	 */
	public Application() {
		super();
		logger.info("+++ Welcome to " + this.name() + " on WebObjects version: " + CoreAssistance.webObjectsVersion());

		NSNotificationCenter.defaultCenter().addObserver(this,
				new NSSelector<Object>("applicationAdvertsAreStale", new Class[] { NSNotification.class }),
				edu.umich.marketplace.eof.UserSessionModel.AdvertsAreStale, null);

		NSNotificationCenter.defaultCenter().addObserver(this,
				new NSSelector<Object>("applicationUserLogInNotice", new Class[] { NSNotification.class }),
				edu.umich.marketplace.eof.UserSessionModel.UserLogInNotice, null);
	}

	@Override
	public void awake() {
		super.awake();
    	logger.info("\n\n");
		logger.trace("--> awake()");

		getApplicationModel().checkClickCountModulus(); 	// increment the application's total click count
	}

    @Override
	public void sleep() {
    	super.sleep();
		logger.trace("--> sleep()");
    }

	// -----------------------------------------------------------------------------------------------

	public EOEditingContext getApplicationEC() {
		return _applicationEC;
	}

//----------------------------------------------------------------------------------------------------------------
//  N   N      OOO      TTTTTT     III     FFFF     III      CCC      AAA      TTTTTT     III      OOO      N   N
//  NN  N     O   O       TT        I      F         I      C        A   A       TT        I      O   O     NN  N
//  N N N     O   O       TT        I      FFF       I      C        AAAAA       TT        I      O   O     N N N
//  N  NN     O   O       TT        I      F         I      C        A   A       TT        I      O   O     N  NN
//  N   N      OOO        TT       III     F        III      CCC     A   A       TT       III      OOO      N   N
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	/**
	 * Called when the application posts the notification that it has finished initialing. We do some application setup
	 * here, instead of in the constructor, because it's Really Bad if the constructor fails.
	 *
	 * ... gets the "ApplicationModel" and initializes it. (MODERN
	 *
	 * {@link WOApplication#ApplicationWillFinishLaunchingNotification}. This method calls subclasses'
	 * {@link #willFinishLaunchingApplication} method.
	 */
	@Override
	public void applicationWillFinishLaunching() {
		super.applicationWillFinishLaunching();

		logger.trace("--> applicationWillFinishLaunching");

		CoreApplication.properties.alphaDump(true);
		setCachingEnabled(CoreApplication.properties.getBoolean("productionDeploy", "FALSE"));
		setSessionTimeOut(new Integer(CoreApplication.properties.getInt("sessionTimeout", "3600")));

		_handleExceptions = CoreApplication.properties.getBoolean("handleExceptions", "TRUE");
		String helpFile = CoreApplication.properties.getString("applicationHelpFile","");

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

		startTenMinuteTimerTask();
		startDayChangeTimerTask();
//		startNotifierTimerTask();
		
		logger.trace("<-- applicationWillFinishLaunching");
	}

	/**
	 * Called when the application posts the notification that it has finished post-initialization code and is
	 * about to become a serving WebObjects application. We just drop a long marker into the log ...
	 */
	@Override
	public void applicationDidFinishLaunching() {
		super.applicationDidFinishLaunching();

		new er.extensions.jdbc.ERXJDBCConnectionAnalyzer("Marketplace");
		getApplicationModel().initApplicationModel();

		logger.trace("<-> applicationDidFinishLaunching");
		logger.info("\n--------------------------------------------------------------------------------" +
                      "---------------------------------------------------------------------------------");
	}

    @Override
	public void sessionDidCreate(String sessionID) {
		logger.trace("--> sessionDidCreate: " + sessionID);
    }

	/**
	 * The application receives the notification that a user has logged in.
	 */

	public void applicationUserLogInNotice(NSNotification n) {
		logger.trace("!-- applicationUserLogInNotice: " + n);
	}

	public void applicationAdvertsAreStale(NSNotification n) {
		logger.trace("!-- applicationAdvertsAreStale: " + n);
		getApplicationModel().initApplicationModel();
	}

	public void editingContextWillChange(NSNotification n) {
		logger.trace("!-- editingContextWillChange: " + n);
	}

	public void objectsChangedInStore(NSNotification n) {
		logger.trace("!-- objectsChangedInStore: " + n);
	}


//----------------------------------------------------------------------------------------------------------------
//    EEEE     X   X      CCC     EEEE     PPPP      TTTTTT     III      OOO      N   N      SSS
//    E         X X      C        E        P   P       TT        I      O   O     NN  N     S
//    EEE        X       C        EEE      PPPP        TT        I      O   O     N N N      SSS
//    E         X X      C        E        P           TT        I      O   O     N  NN         S
//    EEEE     X   X      CCC     EEEE     P           TT       III      OOO      N   N     SSSS
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	/**
	 * For when user backtracks too far - NOTE: do NOT change the API of this method - it overrides a method in
	 * WOComponent; if the API changes, this method will not be called at the appropriate time, and the user will be
	 * exposed to the default WebObjects error page rather than a nice error page from us!!!
	 */

	@Override
	public WOResponse handlePageRestorationErrorInContext(WOContext aContext) {
		return (_handleExceptions) ? ErrorHandler.handlePageRestorationError(aContext) :
									 super.handlePageRestorationErrorInContext(aContext);
	}

	/**
	 * For when a session times out - NOTE: do NOT change the API of this method - it overrides a method in WOComponent;
	 * if the API changes, this method will not be called at the appropriate time, and the user will be exposed to the
	 * default WebObjects error page rather than a nice error page from us!!!
	 */

	@Override
	public WOResponse handleSessionRestorationErrorInContext (WOContext aContext) {
		return (_handleExceptions) ? ErrorHandler.handleSessionRestorationError (aContext) :
									 super.handleSessionRestorationErrorInContext (aContext);
	}

	/**
	 * For uncaught exceptions - NOTE: do NOT change the API of this method - it overrides a method in WOComponent;
	 * if the API changes, this method will not be called at the appropriate time, and the user will be exposed to
	 * a stack trace dump rather than a nice error page from us!!!
	 */

	@Override
	public WOResponse handleException(Exception x, WOContext aContext) {
		logger.error("*** handleException", x);
		return (_handleExceptions) ? ErrorHandler.handleError(x, aContext, "Re-enter " + name()) :
									 super.handleException(x, aContext);
	}

	public static void handleAppException(String marker, Exception x, Session session) {
		logger.error("*** " + x.getClass().getName() + " in " + marker + ": ", x);
		ErrorHandler.emailTheDeveloper(x, session);
	}


//----------------------------------------------------------------------------------------------------------------
//	TTTTTT     III     M   M     EEEE     RRRR       SSS  
//	  TT        I      MM MM     E        R   R     S     
//	  TT        I      M M M     EEE      RRRR       SSS  
//	  TT        I      M   M     E        R R           S 
//	  TT       III     M   M     EEEE     R  RR     SSSS  
//
//--------------------------------------------------------------------------- http://patorjk.com/software/taag/ --

	public Timer startTenMinuteTimerTask() {
		logger.trace("--> startTenMinuteTimerTask()");
		Timer timer = new Timer("TenMinuteTimer");

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				logger.trace("- - -   t e n   m i n u t e   t i m e r   - - -");
				getApplicationModel().checkClickCountChanged();
			}
		}, 120000, 600000); // wait two minutes, then tick every ten minutes

		return timer;
	}
	
	public Timer startDayChangeTimerTask() {
		DateTime			startTime = (new DateTime()).plusDays(1).withTime(0, 1, 0, 0);
		logger.trace("--> startDayChangeTimerTask() .. will fire at: " + startTime.toString("MMMdd HH:mm:ss.SSS"));
		Timer timer = new Timer("DayChangeTimer");

		timer.scheduleAtFixedRate(new TimerTask() {			
			@Override
			public void run() {
				logger.trace("- - -   d a y   c h a n g e   t i m e r   - - -");
				getApplicationModel().refreshActiveAdverts();		// forget yesterday's adverts

			}
		}, new Date(startTime.getMillis()), DateTimeConstants.MILLIS_PER_DAY);

		return timer;
	}
/*
	public Timer startNotifierTimerTask() {
		DateTime			startTime = (new DateTime()).plusDays(1).withTime(3, 0, 0, 0);
		logger.trace("-->  startNotifierTimerTask() .. will fire at: " + startTime.toString("MMMdd HH:mm:ss.SSS"));
		Timer timer = new Timer("NotifierTimer");

		timer.scheduleAtFixedRate(new TimerTask() {			
			@Override
			public void run() {
				logger.trace("- - -   n o t i f i e r   t i m e r   - - -");
		        NotifyAuthors notify = new NotifyAuthors();
		        notify.doNotify();
		        joinAllThreads();       		// wait by joining all the SMTP threads
			}
		}, new Date(startTime.getMillis()), DateTimeConstants.MILLIS_PER_DAY);

		return timer;
	}
*/
	/*
	 * called from 'notify' direct action
	 */
	static String notifyPendingExpiries() {
        NotifyAuthors	notify = new NotifyAuthors();
        String 			responseString = notify.doNotify();
        
        joinAllThreads();       		// wait by joining all the SMTP threads

        return responseString;
	}
	
    //TODO
    //  This mechanism will need to be changed for WebObjects 5.4 because there
    //  are no worker threads in that version ... Aug12/06 - Gav
    //
    //TODO
    //  Actually, we're still OK in 5.4 ... (time to check 5.5 in Snow Leopard!)
    
    static private void joinAllThreads () {
        ThreadGroup     mainGroup = Thread.currentThread().getThreadGroup();        
        int             numThreads = mainGroup.activeCount();
        Thread[]        threads = new Thread[numThreads*2];
                        
        numThreads = mainGroup.enumerate(threads, false);
        for (int i=0; i<numThreads; i++) {
            Thread thread = threads[i];
            try {
                if (thread.isAlive() && 
                    thread.getName().toLowerCase().startsWith("thread-")) {
                    logger.info("--- " + thread.getName() + " mail thread joined, waiting to finish");
                    thread.join(5 * 1000);             // wait 30 seconds to finish
                }
                else
                    logger.info("--- " + thread.getName() + " not a mail thread, or already finished");
            } catch (Exception x) {
                logger.info("     Interrupted: ", x);
            }
        }
    }
    
    public String bannerBgColor() {
		return "..";
	}
}