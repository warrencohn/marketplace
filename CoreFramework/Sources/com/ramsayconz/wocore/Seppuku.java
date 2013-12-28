// Seppuku.java $Revision: 1.1 $
//
// Copyright (c) 2003 Red Shed Software. All rights reserved.
// by Jonathan 'Wolf' Rentzsch (jon at redshed dot net)
// 
// Tue Jan 14 2003 wolf: Created.

package com.ramsayconz.wocore;

import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

/**
 * Seppuku performs suicide on the entire application instance, via a Runtime.getRuntime().exit(1) if the time 
 * between a request and its corresponding response exceeds "SeppukuDeadlockedInterval" (default 5 minutes).  
 * This condition is polled for every "SeppukuPollingInterval" (default, 1 minute).  This capability is currently
 * enabled when NSPrincipleClass for this framework is "com.ramsayconz.wocore.Seppuku".
 * 
 * Compare this with WorkerThreadMonitor which has the same purpose and a similar mechanism, but is enabled via 
 * explicit construction by the Application.  It is disabled if property "WorkerThreadMonitor.MonitorForDeadlock"
 * is FALSE.
 * 
 * @author gavin
 */
public class Seppuku {
    static final Logger     		logger = Logger.getLogger(Seppuku.class);
    private static final Seppuku    _singleton = new Seppuku();
    @SuppressWarnings("rawtypes")
	protected static ThreadLocal	_currentRequest = new ThreadLocal();

    private static final long 		_kOneSecond = 1000;
    private static final long 		_kOneMinute = 60 * _kOneSecond;

    static long 		pollingInterval = Long.getLong("SeppukuPollingInterval", _kOneMinute).longValue();
    static long 		deadlockedInterval = Long.getLong("SeppukuDeadlockedInterval", 5 * _kOneMinute).longValue();
    
    static {
    	new FrameworkInitializer(Seppuku.class);
    }

    public static void initializeFromFramework() {
		if (pollingInterval > 0) {
			NSNotificationCenter.defaultCenter().addObserver(_singleton, 
							new NSSelector<Object>("willDispatchRequest",new Class[] { NSNotification.class }),
							WOApplication.ApplicationWillDispatchRequestNotification, null);
			NSNotificationCenter.defaultCenter().addObserver(_singleton,
							new NSSelector<Object>("didDispatchRequest", new Class[] { NSNotification.class }),
							WOApplication.ApplicationDidDispatchRequestNotification, null);
			SeppukuMonitor.create();
			logger.info("!-- initializeFromFramework : Seppuku running ...");
		} else {
			logger.info("!-- initializeFromFramework : Seppuku disabled (pollInterval = zero)");
		}
	}

    // Outstanding request list management stuff.
    @SuppressWarnings("unchecked")
	public void willDispatchRequest(NSNotification notification) {
        WORequest request = (WORequest) notification.object();
        _currentRequest.set(request);
        SeppukuMonitor.addRequest(request);
    }

    public void didDispatchRequest(NSNotification notification) {
        WORequest request = (WORequest) _currentRequest.get();
        SeppukuMonitor.subRequest(request);
    }

    // Monitoring thread stuff.
    private static class SeppukuMonitor extends Thread {
        private static WeakHashMap<WORequest, Long> 		
        								_requests = new WeakHashMap<WORequest, Long>();

        public static void create() {
            SeppukuMonitor seppukuMonitor = new SeppukuMonitor();
            seppukuMonitor.setName("SeppukuThread");
            seppukuMonitor.setDaemon(true);
            seppukuMonitor.start();
        }

        @Override
		public void run() {
            logger.info("   SeppukuPollingInterval: " + pollingInterval/_kOneSecond + " seconds");
            logger.info("SeppukuDeadlockedInterval:  " + deadlockedInterval/_kOneMinute + " minutes");

            try {
                for (;;) {
                    synchronized (this) {
                        wait(pollingInterval);
                    }
                    logger.trace("SeppukuMonitor polling");
                    long now = System.currentTimeMillis();
                    synchronized (_requests) {
                        logger.trace("Poll: " + _requests.size() + " known active requests");

                        for (Long value : _requests.values()) {
                            long then = value.longValue();

                            if ((now - then) >= deadlockedInterval) {
                                logger.fatal("*** Deadlock detected, killing this instance");
                                Runtime.getRuntime().exit(1);
                            }
                        }
                    }
                }
            } 
            catch (InterruptedException x) { }
            logger.error("SeppukuMonitor exiting");
        }

        public static void addRequest(WORequest request) {
            synchronized (_requests) {
                _requests.put(request, new Long(System.currentTimeMillis()));
                if (logger.isDebugEnabled())
                    logger.debug("+++: " + Thread.currentThread().getName() + " : " + request);
            }
        }

        public static void subRequest(WORequest request) {
            synchronized (_requests) {
                _requests.remove(request);
                if (logger.isDebugEnabled())
                    logger.debug("---: " + Thread.currentThread().getName() + " : " + request);
            }
        }
    }
}
