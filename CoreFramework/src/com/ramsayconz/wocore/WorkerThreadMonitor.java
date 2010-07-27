package com.ramsayconz.wocore;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

/**
 * This class is a poor substitute for better ways of fixing deadlocking problems. It is for use only in desperate
 * times, like when you need a few hours of sleep while fixing the real problem or while you are waiting for Apple to
 * release a patch. OK, consider yourself warned!<br/> <br/> What this class does is to watch the requests as they are
 * dispatched and keep track of the ones that have not yet returned a response. Periodically it examines these as yet
 * unfinished requests and calculates how long each has been running. If one has been running more than a set amount of
 * time then it is assumed that the thread has deadlocked. As there is no recovery for this, the application terminates
 * immediately. In a nut-shell, this is an automated way of doing kill -9 on the instance.<br/> <br/> The
 * WorkerThreadMonitor is configured via values in the system properties:<br/>
 * <b>WorkerThreadMonitor.MonitorForDeadlock</b><br/> Default: true<br/> Set to true to enable deadlock checking,
 * anything else to disable.<br/> <br/> <b>WorkerThreadMonitor.SecondsForDeadlock</b><br/> Default: 180<br/> The number
 * of seconds that a request can be working before it is assumed that it has deadlocked.<br/> <br/>
 * <b>WorkerThreadMonitor.SecondsBetweenChecks</b><br/> Default: 60<br/> The number of seconds between checks for
 * deadlocks. This should be kept lower than WorkerThreadMonitor.SecondsForDeadlock.<br/> <br/>
 * <b>WorkerThreadMonitor.DeadlockWatchWindowStart</b><br/> Default: 00:00<br/> The time of day, in 24 hour format, when
 * deadlock detection should be started. This, along with WorkerThreadMonitor.DeadlockWatchWindowEnd can be used to
 * restrict deadlock detection to certain hours of the day, like when you are sleeping for instance. The rest of the
 * time you can let them happen normally and examine the thread dumps etc. when the application is in a deadlocked
 * state. The time format must be HH:MM, no spaces allowed and always five characters.<br/> <br/>
 * <b>WorkerThreadMonitor.DeadlockWatchWindowEnd</b><br/> Default: 23:59<br/> See
 * WorkerThreadMonitor.DeadlockWatchWindowStart for a description<br/> <br/>
 * 
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights reserved. This software is published under
 *         the terms of the Educational Community License (ECL) version 1.0, a copy of which has been included with this
 *         distribution in the LICENSE.TXT file.
 */
public class WorkerThreadMonitor extends TimerTask {
    private static final Logger     logger = Logger.getLogger (WorkerThreadMonitor.class);

    public static final String      MONITOR_FOR_DEADLOCK = "WorkerThreadMonitor.MonitorForDeadlock";
    public static final String      DEFAULT_MONITOR_FOR_DEADLOCK = "TRUE";
    
    public static final String      SECONDS_FOR_DEADLOCK = "WorkerThreadMonitor.SecondsForDeadlock";
    public static final String      DEFAULT_SECONDS_FOR_DEADLOCK = "180";

    public static final String      SECONDS_BETWEEN_CHECKS = "WorkerThreadMonitor.SecondsBetweenChecks";
    public static final String      DEFAULT_SECONDS_BETWEEN_CHECKS = "60";

    public static final String      DEADLOCK_WATCH_WINDOW_START = "WorkerThreadMonitor.DeadlockWatchWindowStart";
    public static final String      DEFAULT_DEADLOCK_WATCH_WINDOW_START = "00:00";
    
    public static final String      DEADLOCK_WATCH_WINDOW_END = "WorkerThreadMonitor.DeadlockWatchWindowEnd";
    public static final String      DEFAULT_DEADLOCK_WATCH_WINDOW_END = "23:59";

    protected static final long     STARTUP_DELAY = 120 * 1000;
    
    protected int                   watchWindowStartHours = -1;
    protected int                   watchWindowStartMinutes = -1;
    protected int                   watchWindowEndHours = -1;
    protected int                   watchWindowEndMinutes = -1;
    protected long                  maximumMilliSecondsToWait = -1;
    protected Timer                 deadlockCheckTimer;
    protected NSMutableDictionary<String, Object>   
    								runningThreads = new NSMutableDictionary<String, Object>();

    /**
     * Designated constructor
     */
    public WorkerThreadMonitor() {
        super();
        
        // Check if we should be running or not
        Boolean shouldMonitor = new Boolean(System.getProperty(MONITOR_FOR_DEADLOCK, DEFAULT_MONITOR_FOR_DEADLOCK));
        if (!shouldMonitor.booleanValue()) {
            logger.info("Disabling WorkerThreadMonitor...");
            return;
        }
        
        // Register for notification at start and end of Request-Response loop
        NSNotificationCenter.defaultCenter().addObserver(this, 
                new NSSelector<Object>("willDispatchRequest", new Class[] {NSNotification.class}),
                						WOApplication.ApplicationWillDispatchRequestNotification, null);
        NSNotificationCenter.defaultCenter().addObserver(this, 
                new NSSelector<Object>("didDispatchRequest", new Class[] {NSNotification.class}),
                						WOApplication.ApplicationDidDispatchRequestNotification, null);
        
        String watchWindowStart = validated24HourTimeProperty(DEADLOCK_WATCH_WINDOW_START, DEFAULT_DEADLOCK_WATCH_WINDOW_START);

        NSArray<String> hoursAndMinutes = NSArray.componentsSeparatedByString(watchWindowStart, ":");
        this.watchWindowStartHours = Integer.parseInt(hoursAndMinutes.objectAtIndex(0));
        this.watchWindowStartMinutes = Integer.parseInt(hoursAndMinutes.lastObject());
        
        String watchWindowEnd = validated24HourTimeProperty(DEADLOCK_WATCH_WINDOW_END, DEFAULT_DEADLOCK_WATCH_WINDOW_END);
        hoursAndMinutes = NSArray.componentsSeparatedByString(watchWindowEnd, ":");
        this.watchWindowEndHours = Integer.parseInt(hoursAndMinutes.objectAtIndex(0));
        this.watchWindowEndMinutes = Integer.parseInt(hoursAndMinutes.lastObject());

        String maxSecondsToWait = validatedIntegerProperty(SECONDS_FOR_DEADLOCK, DEFAULT_SECONDS_FOR_DEADLOCK);       
        this.maximumMilliSecondsToWait = Integer.parseInt(maxSecondsToWait) * 1000;
        
        String secondsBetweenChecks = validatedIntegerProperty(SECONDS_BETWEEN_CHECKS, DEFAULT_SECONDS_BETWEEN_CHECKS);
        long millisecondsBetweenChecks = Integer.parseInt(secondsBetweenChecks) * 1000;

        this.deadlockCheckTimer = new Timer(true);
        this.deadlockCheckTimer.scheduleAtFixedRate(this, STARTUP_DELAY, millisecondsBetweenChecks);
        
        logger.info("Starting up WorkerThreadMonitor...");
        logger.info("  checking for deadlock between the hours of " + 
                this.watchWindowStartHours + ":" + this.watchWindowStartMinutes + " and " + 
                this.watchWindowEndHours + ":" + this.watchWindowEndMinutes);
        logger.info("  checking for deadlock every " + (millisecondsBetweenChecks/1000) + " seconds");
        logger.info("  deadlock will be assumed after " + (this.maximumMilliSecondsToWait/1000) + " seconds");
    }
    
    /**
     * This method is called when a new request is dispatched.  The name of the WOWorkerThread is 
     * recorded along with the starting time.
     * 
     * @param notification NSNotification with WORequest as notification object
     */
    public void willDispatchRequest(NSNotification notification) {
        runningThreads().takeValueForKey(new NSTimestamp(), Thread.currentThread().getName());
    }
    
    /**
     * This method is called when a request has been dispatched.  The record for this WOWorkerThread is 
     * discarded.
     * 
     * @param notification NSNotification with WORequest as notification object
     */
    public void didDispatchRequest(NSNotification notification) {
        runningThreads().removeObjectForKey(Thread.currentThread().getName());
    }

    /**
     * Returns the dictionary, keyed on thread name, of the WOWorkerThreads actively dispatching 
     * requests.  This is synchronized as both the thread running the WOWorkerThread monitor and 
     * WOWorkerThreads can call this method.
     * 
     * @return the dictionary, keyed on thread name, of the WOWorkerThreads actively dispatching 
     * requests
     */
    public synchronized NSMutableDictionary<String, ?> runningThreads() {
        return this.runningThreads;
        /** ensure [valid_result] Result != null;  **/
    }
    
    /**
     * This method is the core of the deadlock check.  It enumerates all the active threads in 
     * runningThreads() and if one has gone on too long the application is terminated.
     */
    @Override
	public void run() {
        if (withinWatchWindow()) {				// If we are in the checking window ...
            synchronized (this.runningThreads) {		// inhibit collection modification while ...
                long now = new NSTimestamp().getTime();
                for (String threadName : runningThreads().keySet()) {
                    if (hasExceededCutoff(now, (NSTimestamp)runningThreads().objectForKey(threadName))) {
                        logger.info("Killing Application, deadlock assumed for " + threadName);
                        System.exit(1);
                    }
                }
            }
        } 
    }
    
    /**
     * Returns <code>true</code> if the current time is in the window of when deadlocks should be detected.
     * 
     * @return <code>true</code> if the current time is in the window of when deadlocks should be detected
     */
    protected boolean withinWatchWindow() {
        GregorianCalendar now = new GregorianCalendar();
        int hourOfDay = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        
        boolean isAfterStart = (hourOfDay > this.watchWindowStartHours) || 
                              ((hourOfDay == this.watchWindowStartHours) && (minute >= this.watchWindowStartMinutes));
        boolean isBeforeEnd = (hourOfDay < this.watchWindowEndHours) || 
                             ((hourOfDay == this.watchWindowEndHours) && (minute <= this.watchWindowEndMinutes));        
        // The or might seem a bit odd but it is what works with the sliding window:
        return isAfterStart || isBeforeEnd;
    }

    /**
     * Returns <code>true</code> if the difference between now and the start time is greater than
     * the number of seconds before deadock is assumed.
     * 
     * @param now the current time or a close approximation of it
     * @param startTime the time that a request was dispatched
     * 
     * @return <code>true</code> if startTime - now > the number of seconds before deadock is assumed 
     */
    protected boolean hasExceededCutoff(long now, NSTimestamp startTime) {
        long millisecondRunning = now - startTime.getTime();
        return (millisecondRunning > this.maximumMilliSecondsToWait);
    }
    
   /** 
    * Utility method for dealing with properties in 24 hour time format.  If the named property is 
    * not present or malformed the default is returned.
    * 
    * @param propertyName the name of the system property to look for
    * @param defaultTimeString the default to use if the named property is not present or malformed
    *  
    * @return either the value for the property or the default
    */
    public String validated24HourTimeProperty(String propertyName, String defaultTimeString) {
        String propertyValue = System.getProperty(propertyName, defaultTimeString);
        return isValid24HourTime(propertyValue) ? propertyValue : defaultTimeString;
    }
    
    public static boolean isValid24HourTime(String timeString) {
        if ((timeString == null) ||
            (timeString.trim().length() != 5) ||
            ( ! timeString.substring(2, 3).equals(":")) ||
            ( ! (Character.isDigit(timeString.charAt(0)) && 
                 Character.isDigit(timeString.charAt(1)) && 
                 Character.isDigit(timeString.charAt(3)) && 
                 Character.isDigit(timeString.charAt(4)))) ) {
            return false;
        }
        
        int hours = Integer.parseInt(timeString.substring(0, 2));
        int minutes = Integer.parseInt(timeString.substring(3, 5));
        if ((hours < 0) || (hours > 23) || (minutes < 0) || (minutes > 59)) {
            return false;
        }
        
        return true;
    }

    /** 
     * Utility method for dealing with integer properties.  If the named property is not present or 
     * malformed the default is returned.
     * 
     * @param propertyName the name of the system property to look for
     * @param defaultValue the default to use if the named property is not present or malformed
     * @return either the value for the property or the default
     */
    public String validatedIntegerProperty(String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName, defaultValue);
        return (isInteger(propertyValue) ? propertyValue : defaultValue).trim();
    }
    
    public static boolean isInteger(String aString) {
        boolean isInteger = true;
        try {
            Integer.valueOf(aString.trim());
        }
        catch (NumberFormatException e) {
            isInteger = false;
        }
        return isInteger;
    }
}
