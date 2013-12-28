package com.ramsayconz.wocore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSProperties;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation._NSUtilities;

// import org.apache.log4j.Level;

@SuppressWarnings("unqualified-field-access")
public final class NSLog {
    
    //------------------------------------------------------------------------
    
    public static class _DevNullPrintStream extends PrintStream {
        
        public OutputStream originalOutputStream;

		@Override
		protected void setError() {
		}

		@Override
		public boolean checkError() {
			return false;
		}

		@Override
		public void close() {
		}

		@Override
		public void flush() {
		}

		@Override
		public void print(boolean flag) {
		}

		@Override
		public void print(char c1) {
		}

		@Override
		public void print(char ac[]) {
		}

		@Override
		public void print(double d1) {
		}

		@Override
		public void print(float f1) {
		}

		@Override
		public void print(int j) {
		}

		@Override
		public void print(long l1) {
		}

		@Override
		public void print(Object obj1) {
		}

		@Override
		public void print(String s1) {
		}

		@Override
		public void println() {
		}

		@Override
		public void println(boolean flag) {
		}

		@Override
		public void println(char c) {
		}

		@Override
		public void println(char ac[]) {
		}

		@Override
		public void println(double d) {
		}

		@Override
		public void println(float f) {
		}

		@Override
		public void println(int i) {
		}

		@Override
		public void println(long l) {
		}

		@Override
		public void println(Object obj) {
		}

		@Override
		public void println(String s) {
		}

		@Override
		public void write(int i) {
		}

		@Override
		public void write(byte abyte0[], int i, int j) {
		}

		public _DevNullPrintStream(OutputStream os) {
			super(os);
			this.originalOutputStream = null;
			this.originalOutputStream = os;
		}

		public _DevNullPrintStream(OutputStream os, boolean aBOOL) {
			super(os, aBOOL);
			this.originalOutputStream = null;
			this.originalOutputStream = os;
		}
    }

    // ------------------------------------------------------------------------

    public static class PrintStreamLogger extends Logger {
        
        protected String                    _prefixInfo;
        protected PrintStream               _stream;
		private static final String         _defaultFormatString = "yyyy-M-d H:m:s ";
		private static final NSTimeZone     _tz;
        private long                        _lastVerboseLogTime;
        private String                      _lastTimestampText;
        
        public void _setPrefixInfo(String s) {
            this._prefixInfo = s;
        }
        
		protected String _verbosePrefix() {
            String threadName = Thread.currentThread().getName();
            NSTimestamp now = new NSTimestamp();
            long offset = now.getTime();
            String abbr = _tz.abbreviationForTimestamp(now);
            String prefixInfo = _prefixInfo;
            StringBuffer sb = new StringBuffer(50);
            synchronized (this) {
				if (offset - _lastVerboseLogTime >= 1000L) {
					SimpleDateFormat formatter = new SimpleDateFormat(_defaultFormatString);
					formatter.setTimeZone(_tz);
                    _lastTimestampText = formatter.format(now);
                    _lastVerboseLogTime = offset;
                }
            }
            if (prefixInfo != null) {
                sb.append("<");
                sb.append(_prefixInfo);
                sb.append(">");
            }
            sb.append("[");
            sb.append(_lastTimestampText);
            sb.append(abbr != null ? abbr : _tz.getID());
            sb.append("] <");
            sb.append(threadName);
            sb.append("> ");
            return sb.toString();
        }
        
        @Override
		public synchronized void appendln() {
			if (isEnabled) {
                synchronized (_stream) {
                    _stream.println();
                    _stream.flush();
                }
        }
		}
        
        @Override
		public synchronized void appendln(Throwable aValue) {
			if (isEnabled) {
                synchronized (_stream) {
                    StringBuffer stackTrace = new StringBuffer();
					if (isVerbose) {
                        stackTrace.append(_verbosePrefix());
					}
                    stackTrace.append(NSLog.throwableAsString(aValue));
                    _stream.println(stackTrace.toString());
                    _stream.flush();
                }
        }
		}
        
        @Override
		public synchronized void appendln(Object aValue) {
			if (isEnabled) {
				synchronized (_stream) {
					if (isVerbose) {
                        _stream.println((new StringBuilder()).append(_verbosePrefix()).append(aValue).toString());
          }
          else {
                        _stream.println(aValue);
					}
					_stream.flush();
				}
                }
        }
        
        @Override
		public void flush() {
            _stream.flush();
        }
        
        public PrintStream printStream() {
            return _stream;
        }
        
        public void setPrintStream(PrintStream aStream) {
			if (aStream != null) {
                _stream = aStream;
        }
		}
        
		static  {
			try {
				_tz = NSTimeZone.systemTimeZone();
			}
			catch (Exception e) {
				System.err.println("unable to initialize NSLog");
				e.printStackTrace(System.err);
				throw new RuntimeException(e);
			}
		}

        public PrintStreamLogger() {
            this(System.out);
        }
        
        public PrintStreamLogger(PrintStream ps) {
            _prefixInfo = null;
            if (ps == null) {
                throw new IllegalArgumentException((new StringBuilder()).
                                                   append("<").
                                                   append(getClass().getName()).
                                                   append("> java.io.PrintStream argument must be non-null").toString());
            }
			_stream = ps;
			_lastVerboseLogTime = NSTimestamp.DistantPast.getTime();
			return;
        }
    }
    
    //------------------------------------------------------------------------
    
	public static class Log4JLogger extends Logger {
        
        protected org.apache.log4j.Logger   logger;
        protected org.apache.log4j.Level    logLevel;
        
        public Log4JLogger() {
        }
        
        public Log4JLogger(org.apache.log4j.Logger aLogger, int level) {
            if (aLogger == null) {
                throw new IllegalArgumentException((new StringBuilder()).
                                                   append("<").
                                                   append(getClass().getName()).
                                                   append("> org.apache.log4j.Logger parameter cannot be null.").toString());
            }
			logger = aLogger;
			setAllowedDebugLevel(level);
			return;
        }

		protected static int convertLog4JLevelToNSLogLevel(int aLog4JDebugLevel) {
            int nsLogDebugLevel;
            switch (aLog4JDebugLevel) {
                case org.apache.log4j.Level.OFF_INT:
                    nsLogDebugLevel = DebugLevelOff;
                    break;

                case org.apache.log4j.Level.FATAL_INT:
                case org.apache.log4j.Level.ERROR_INT:
                case org.apache.log4j.Level.WARN_INT:
                    nsLogDebugLevel = DebugLevelCritical;
                    break;
                
                case org.apache.log4j.Level.INFO_INT:
                    nsLogDebugLevel = DebugLevelInformational;
                    break;
                
                case org.apache.log4j.Level.DEBUG_INT:
                case org.apache.log4j.Level.TRACE_INT:
                case org.apache.log4j.Level.ALL_INT:
                    nsLogDebugLevel = DebugLevelDetailed;
                    break;
                    
                default:
                    throw new IllegalArgumentException((new StringBuilder()).
                                                       append("<NSLog> Invalid Log4J debug level: ").
                                                       append(aLog4JDebugLevel).toString());
            }
            return nsLogDebugLevel;
        }
        
        protected static int convertNSLogLevelToLog4JLevel(int anNSLogDebugLevel) {
            int log4jDebugLevel;
      switch (anNSLogDebugLevel) {
                case DebugLevelOff:
                    log4jDebugLevel = org.apache.log4j.Level.OFF_INT;
                    break;
                    
                case DebugLevelCritical:
                    log4jDebugLevel = org.apache.log4j.Level.ERROR_INT;
                    break;
                    
                case DebugLevelInformational:
                    log4jDebugLevel = org.apache.log4j.Level.INFO_INT;
                    break;
                    
                case DebugLevelDetailed:
                    log4jDebugLevel = org.apache.log4j.Level.DEBUG_INT;
                    break;
                    
                default:
                    throw new IllegalArgumentException((new StringBuilder()).
                                                       append("<NSLog> Invalid NSLog debug level: ").
                                                       append(anNSLogDebugLevel).toString());
            }
            return log4jDebugLevel;
        }
                
        @Override
		public int allowedDebugLevel() {
            org.apache.log4j.Level level = logger.getLevel();       //### this gets the REAL logger internal level, not our 'logLevel'
            return level != null ? convertLog4JLevelToNSLogLevel(level.toInt()) : DebugLevelOff; //### changed
        }
        
        @Override
		public void setAllowedDebugLevel(int aDebugLevel) {
            int loggerDebugLevel = convertNSLogLevelToLog4JLevel(aDebugLevel);
            logLevel = org.apache.log4j.Level.toLevel(loggerDebugLevel);
            logger.setLevel(logLevel);                              //### <--- new
        }
        
        public org.apache.log4j.Logger log4jLogger() {
            return logger;
        }
        
        public void setLog4jLogger(org.apache.log4j.Logger aLogger) {
			if (aLogger != null) {
				logger = aLogger;
			}
        }

        @Override
		public void appendln() {
            appendln("");
        }
        
        @Override
		public void appendln(Object aValue) {
            logger.log(logLevel, aValue);
        }
        
        @Override
		public void flush() {
        }
    }
    
    //------------------------------------------------------------------------
    
    public static abstract class Logger {
        
        protected int                       debugLevel;
        protected boolean                   isEnabled;
        protected boolean                   isVerbose;
        
        public Logger() {
            debugLevel = DebugLevelOff;
            isEnabled = true;
            isVerbose = true;
        }
        
        public int allowedDebugLevel() {
            return debugLevel;
        }
        
        public void setAllowedDebugLevel(int aDebugLevel) {
            if (aDebugLevel >= DebugLevelOff && aDebugLevel <= DebugLevelDetailed) {
                debugLevel = aDebugLevel;
            } else {
                throw new IllegalArgumentException((new StringBuilder()).
                                                   append("<").
                                                   append(getClass().getName()).
                                                   append("> Invalid debug level: ").
                                                   append(aDebugLevel).toString());
            }
        }

        public void appendln(boolean aValue) {
            appendln(aValue ? ((Object) (Boolean.TRUE)) : ((Object) (Boolean.FALSE)));
        }
        
        public void appendln(byte aValue) {
            appendln(new Byte(aValue));
        }
        
        public void appendln(byte aValue[]) {
            appendln(new String(aValue));
        }
        
        public void appendln(char aValue) {
            appendln(new Character(aValue));
        }
        
        public void appendln(char aValue[]) {
            appendln(new String(aValue));
        }
        
        public void appendln(double aValue) {
            appendln(new Double(aValue));
        }
        
        public void appendln(float aValue) {
            appendln(new Float(aValue));
        }
        
        public void appendln(int aValue) {
            appendln(_NSUtilities.IntegerForInt(aValue));
        }
        
        public void appendln(long aValue) {
            appendln(new Long(aValue));
        }
        
        public void appendln(short aValue) {
            appendln(new Short(aValue));
        }
        
        public void appendln(Throwable aValue) {
            appendln(NSLog.throwableAsString(aValue));
        }
        
        public abstract void appendln(Object obj);
        
        public abstract void appendln();
        
        public abstract void flush();
        
        public boolean isEnabled() {
            return isEnabled;
        }
        
        public void setIsEnabled(boolean aBool) {
            isEnabled = aBool;
        }
        
        public boolean isVerbose() {
            return isVerbose;
        }
        
        public void setIsVerbose(boolean aBool) {
            isVerbose = aBool;
        }
    }

    //------------------------------------------------------------------------
    
    public static final long     DebugGroupEnterpriseObjects = 1L << 1;
    public static final long            DebugGroupWebObjects = 1L << 2;
    public static final long DebugGroupApplicationGeneration = 1L << 3;          
    public static final long        DebugGroupMultithreading = 1L << 4;             
    public static final long             DebugGroupResources = 1L << 5;
    public static final long             DebugGroupArchiving = 1L << 6;
    public static final long            DebugGroupValidation = 1L << 7;
    public static final long        DebugGroupKeyValueCoding = 1L << 8;
    public static final long     DebugGroupComponentBindings = 1L << 9;
    public static final long            DebugGroupFormatting = 1L << 10;
    public static final long            DebugGroupQualifiers = 1L << 11;
    
    public static final long                    DebugGroupIO = 1L << 13;
    public static final long                DebugGroupTiming = 1L << 14;
    public static final long                 DebugGroupModel = 1L << 15;
    public static final long        DebugGroupDatabaseAccess = 1L << 16;
    public static final long         DebugGroupSQLGeneration = 1L << 17;
    public static final long         DebugGroupUserInterface = 1L << 18;
    public static final long          DebugGroupAssociations = 1L << 19;
    public static final long           DebugGroupControllers = 1L << 20;
    public static final long                 DebugGroupRules = 1L << 21;
    public static final long            DebugGroupDeployment = 1L << 22;
    public static final long               DebugGroupParsing = 1L << 23;
    public static final long            DebugGroupReflection = 1L << 24;
    public static final long       DebugGroupRequestHandling = 1L << 25;
    public static final long            DebugGroupComponents = 1L << 26;
    public static final long           DebugGroupJSPServlets = 1L << 27;
    public static final long           DebugGroupWebServices = 1L << 28;
    
    public static final int                    DebugLevelOff = 0;
    public static final int               DebugLevelCritical = 1;
    public static final int          DebugLevelInformational = 2;
    public static final int               DebugLevelDetailed = 3;
    
    public static final String         _D2WTraceRuleFiringEnabledKey = "D2WTraceRuleFiringEnabled";
    public static final String  _D2WTraceRuleModificationsEnabledKey = "D2WTraceRuleModificationsEnabled";

    private static final String                EOAdaptorDebugEnabled = "EOAdaptorDebugEnabled";
    
    private static final String                        NSDebugGroups = "NSDebugGroups";
    private static final String                         NSDebugLevel = "NSDebugLevel";
    
    private static final int                     debugGroupMaxBitPos = 63;
    private static final int                     debugGroupMinBitPos = 0;
    
    private static final char                    debugGroupRangeChar = ':';
    private static final int                                notFound = -1;
    
    public static volatile Logger           debug;
    public static volatile Logger           err;
    public static volatile Logger           out;
    
    private static volatile long                         debugGroups = 0L;
    private static volatile boolean         PRIVATE_DEBUGGING_ENABLED;
    private static volatile boolean                     _inInitPhase = false;
    public static volatile String             _WODebuggingEnabledKey = "Undefined";
    
    static {
        debug = new PrintStreamLogger(System.err);
        err = new PrintStreamLogger(System.err);
        out = new PrintStreamLogger(System.out);
        
        debug.setIsVerbose(true);
        err.setIsVerbose(true);
        out.setIsVerbose(false);
        
        _initDebugDefaults();
    }
    
    private NSLog() {
    }
    
    public static void _conditionallyLogPrivateException(Throwable t) {
        if (_debugLoggingAllowedForLevel(DebugLevelDetailed))
            debug.appendln(t);
    }
    
    public static boolean _debugLoggingAllowedForGroups(long aDebugGroups) {
        return PRIVATE_DEBUGGING_ENABLED && debugLoggingAllowedForGroups(aDebugGroups);
    }
    
    public static boolean _debugLoggingAllowedForLevel(int aDebugLevel) {
        return PRIVATE_DEBUGGING_ENABLED && debugLoggingAllowedForLevel(aDebugLevel);
    }
    
    public static boolean _debugLoggingAllowedForLevelAndGroups(int aDebugLevel, long aDebugGroups) {
        return PRIVATE_DEBUGGING_ENABLED && debugLoggingAllowedForLevelAndGroups(aDebugLevel, aDebugGroups);
    }
    
    private static void _initDebugDefaults() {
        System.out.println("=== NSLog._initDebugDefaults()");
        try {
            String value = NSProperties.getProperty("NSPrivateDebuggingEnabled");
            PRIVATE_DEBUGGING_ENABLED = NSPropertyListSerialization.booleanForString(value);
            
            value = NSProperties.getProperty(NSDebugLevel);
            if (value != null) {
                int parsedValue = parseIntValueFromString(value);
                setAllowedDebugLevel(parsedValue);                          // DP7/A410 (debug. removed)
            }
            
            value = NSProperties.getProperty(EOAdaptorDebugEnabled);
            if (NSPropertyListSerialization.booleanForString(value)) {
                if (allowedDebugLevel() < DebugLevelInformational)          // DP7/A410 (debug. removed)
                    setAllowedDebugLevel(DebugLevelInformational);          // DP7/A410 (debug. removed)
				allowDebugLoggingForGroups(DebugGroupDatabaseAccess);
            }
            
            value = NSProperties.getProperty(_WODebuggingEnabledKey);
            if (NSPropertyListSerialization.booleanForString(value)) {
                if (allowedDebugLevel() < DebugLevelInformational)          // DP7/A410 (debug. removed)
                    setAllowedDebugLevel(DebugLevelInformational);          // DP7/A410 (debug. removed)
                allowDebugLoggingForGroups(DebugGroupWebObjects);
            }
            
            value = NSProperties.getProperty(_D2WTraceRuleFiringEnabledKey);
            if (NSPropertyListSerialization.booleanForString(value)) {
                if (allowedDebugLevel() < DebugLevelDetailed)               // DP7/A410 (debug. removed)
                    setAllowedDebugLevel(DebugLevelDetailed);               // DP7/A410 (debug. removed)
                allowDebugLoggingForGroups(DebugGroupRules);
            }
            
            value = NSProperties.getProperty(_D2WTraceRuleModificationsEnabledKey);
            if (NSPropertyListSerialization.booleanForString(value)) {
                if (debug.allowedDebugLevel() < DebugLevelDetailed)
                    debug.setAllowedDebugLevel(DebugLevelDetailed);
                allowDebugLoggingForGroups(DebugGroupApplicationGeneration);
            }
            
            value = NSProperties.getProperty(NSDebugGroups);
            if (value != null && value.length() > 0) {
                long parsedLongValue = 0L;
                Object plistValue = null;
                try {
                    plistValue = NSPropertyListSerialization.propertyListFromString(value);
                }
                catch (RuntimeException e) { }
                
                if (plistValue == null) {
                    try {
                        parsedLongValue = Long.parseLong(value);
                    }
                    catch (NumberFormatException nfe) {
                        err.appendln((new StringBuilder()).append("<NSLog> Unable to parse a property list from the following string -- using default instead!  String: ").append(value).toString());
                    }                    
                }
                else if (plistValue instanceof String) {
                    String unparsedValue = (String)plistValue;
                    if (unparsedValue.indexOf(':') == notFound && unparsedValue.toLowerCase().equals(unparsedValue.toUpperCase())) {      //### non-alpha test !!
                        try {
                            parsedLongValue = Long.parseLong(unparsedValue);
                        }
                        catch(NumberFormatException e) {
                            err.appendln((new StringBuilder()).append("<NSLog> Unable to parse a long value from the following string: \"").append(unparsedValue).append("\"").toString());
                        }
                    }
                    else
                        parsedLongValue = parseLongValueFromString(unparsedValue);
                } 
                else if (plistValue instanceof NSArray) {
                    @SuppressWarnings("rawtypes")
					NSArray debugGroupBitIDs = (NSArray)plistValue;
                    int count = debugGroupBitIDs.count();
                    for(int i = 0; i < count; i++) {
                        Object debugGroupBitID = debugGroupBitIDs.objectAtIndex(i);
                        if (debugGroupBitID instanceof String)
                            parsedLongValue |= parseLongValueFromString((String)debugGroupBitID);
                        else
                            err.appendln((new StringBuilder()).append("<NSLog> Unable to parse an NSArray member of type '").append(debugGroupBitID.getClass().getName()).append("' -- skipping that value!  member: ").append(debugGroupBitID.toString()).toString());
                    }
                    
                } 
                else {
                    err.appendln((new StringBuilder()).append("<NSLog> Unable to parse an NSArray or String from the following string -- using default instead!  String: ").append(value).toString());
                }
                
                if (parsedLongValue == 0L)
                    setAllowedDebugGroups(0xff7fffffL);                 // DP7/A410 was (0xffffffffL);
                else
                    setAllowedDebugGroups(parsedLongValue);
            } 
            else if (debug.allowedDebugLevel() > 0 && debugGroups == 0L)
                setAllowedDebugGroups(0xff7fffffL);                    // DP7/A410 was (0xffffffffL);
        }
        catch(SecurityException exception) { }
    }
    
    public static void _setInInitPhase(boolean flag) {
		if (!flag && _inInitPhase) {
			_initDebugDefaults();
		}
        _inInitPhase = flag;
    }
    
    private static int parseIntValueFromString(String aDebugLevel) {
        int parsedIntValue = DebugLevelOff;
        if (aDebugLevel.charAt(0) >= '0' && aDebugLevel.charAt(0) <= '9') {
            try {
                parsedIntValue = Integer.parseInt(aDebugLevel);
            }
            catch(NumberFormatException e) {
                err.appendln((new StringBuilder()).append("<NSLog> Unable to parse Integer value from the following string -- skipping!  String: ").append(aDebugLevel).toString());
            }
        } else {
            int delimiter = aDebugLevel.lastIndexOf('.');
            if (delimiter == notFound) {
                err.appendln((new StringBuilder()).append("<NSLog> The given symbol is not in the form of [className].[fieldName] -- skipping!  String: ").append(aDebugLevel).toString());
            } else {
                String className = aDebugLevel.substring(0, delimiter);
                String fieldName = aDebugLevel.substring(delimiter + 1);
                @SuppressWarnings("rawtypes")
				Class specifiedClass = _NSUtilities.classWithName(className);
                if (specifiedClass == null)
                    err.appendln((new StringBuilder()).append("<NSLog> The given symbol does not indicate a loaded class -- skipping!  String: ").append(aDebugLevel).append("; class: ").append(className).append("; field: ").append(fieldName).toString());
                else
                    try {
                        Field specifiedField = specifiedClass.getField(fieldName);
                        if (specifiedField.getType() == Integer.TYPE)
                            parsedIntValue = specifiedField.getInt(null);
                        else
                            err.appendln((new StringBuilder()).append("<NSLog> The given symbol does not indicate an int value -- skipping!  String: ").append(aDebugLevel).append("; class: ").append(className).append("; field: ").append(fieldName).toString());
                    }

                catch(IllegalAccessException e) {
                    err.appendln((new StringBuilder()).append("<NSLog> The underlying constructor for the specified class is inaccessible, perhaps because it isn't public -- skipping!  String: ").append(aDebugLevel).append("; class: ").append(className).append("; field: ").append(fieldName).toString());
                }
                
                catch(NullPointerException e) {
                    err.appendln((new StringBuilder()).append("<NSLog> The specified field of the specified class is an instance field, not a class field -- skipping!  String: ").append(aDebugLevel).append("; class: ").append(className).append("; field: ").append(fieldName).toString());
                }
                
                catch(ExceptionInInitializerError e) {
                    err.appendln((new StringBuilder()).append("<NSLog> The specified class failed during class initialization.  Perhaps the class has a circular dependency on NSLog itself, or else there's a general problem initializing that class at this time -- skipping!  String: ").append(aDebugLevel).append("; class: ").append(className).append("; field: ").append(fieldName).toString());
                }
                
                catch(NoSuchFieldException e) {
                    err.appendln((new StringBuilder()).append("<NSLog> Unable to find the specified field for the loaded class, because it doesn't exist or because of Java security restrictions -- skipping!  String: ").append(aDebugLevel).append("; class: ").append(className).append("; field: ").append(fieldName).toString());
                }
                
                catch(SecurityException e) {
                    err.appendln((new StringBuilder()).append("<NSLog> Unable to gather the specified field due to Java security restrictions -- skipping!  String: ").append(aDebugLevel).append("; class: ").append(className).append("; field: ").append(fieldName).toString());
                }
            }
        }
        return parsedIntValue;
    }
    
    private static long parseLongValueFromString(String aDebugGroup) {
		long parsedLongValue = 0L;
		int rangeDelimiter = aDebugGroup.indexOf(debugGroupRangeChar);
		if (rangeDelimiter == notFound) {
			if (aDebugGroup.charAt(0) >= '0' && aDebugGroup.charAt(0) <= '9') {
				try {
					int bitPosition = Integer.parseInt(aDebugGroup);
					if (bitPosition < debugGroupMinBitPos || bitPosition > debugGroupMaxBitPos)
						err.appendln((new StringBuilder()).append(
								"<NSLog> Invalid literal bit position -- skipping!  String: ").append(aDebugGroup)
								.toString());
					else
						parsedLongValue = 1L << bitPosition;
				} catch (NumberFormatException e) {
					err.appendln((new StringBuilder()).append(
							"<NSLog> Unable to parse Integer value from the following string -- skipping!  String: ")
							.append(aDebugGroup).toString());
				}
			} else {
				int delimiter = aDebugGroup.lastIndexOf('.');
				if (delimiter == notFound) {
					err
							.appendln((new StringBuilder())
									.append(
											"<NSLog> The given symbol is not in the form of [className].[fieldName] -- skipping!  String: ")
									.append(aDebugGroup).toString());
				} else {
					String className = aDebugGroup.substring(0, delimiter);
					String fieldName = aDebugGroup.substring(delimiter + 1);
					@SuppressWarnings("rawtypes")
					Class specifiedClass = _NSUtilities.classWithName(className);
					if (specifiedClass == null)
						err.appendln((new StringBuilder()).append(
								"<NSLog> The given symbol does not indicate a loaded class -- skipping!  String: ")
								.append(aDebugGroup).append("; class: ").append(className).append("; field: ").append(
										fieldName).toString());
					else
						try {
							Field specifiedField = specifiedClass.getField(fieldName);
							if (specifiedField.getType() == Long.TYPE)
								parsedLongValue = specifiedField.getLong(null);
							else
								err
										.appendln((new StringBuilder())
												.append(
														"<NSLog> The given symbol does not indicate a long value -- skipping!  String: ")
												.append(aDebugGroup).append("; class: ").append(className).append(
														"; field: ").append(fieldName).toString());
						} catch (IllegalAccessException e) {
							err
									.appendln((new StringBuilder())
											.append(
													"<NSLog> The underlying constructor for the specified class is inaccessible, perhaps because it isn't public -- skipping!  String: ")
											.append(aDebugGroup).append("; class: ").append(className).append(
													"; field: ").append(fieldName).toString());
						} catch (NullPointerException e) {
							err
									.appendln((new StringBuilder())
											.append(
													"<NSLog> The specified field of the specified class is an instance field, not a class field -- skipping!  String: ")
											.append(aDebugGroup).append("; class: ").append(className).append(
													"; field: ").append(fieldName).toString());
						} catch (ExceptionInInitializerError e) {
							err
									.appendln((new StringBuilder())
											.append(
													"<NSLog> The specified class failed during class initialization.  Perhaps the class has a circular dependency on NSLog itself, or else there's a general problem initializing that class at this time -- skipping!  String: ")
											.append(aDebugGroup).append("; class: ").append(className).append(
													"; field: ").append(fieldName).toString());
						} catch (NoSuchFieldException e) {
							err
									.appendln((new StringBuilder())
											.append(
													"<NSLog> Unable to find the specified field for the loaded class, because it doesn't exist or because of Java security restrictions -- skipping!  String: ")
											.append(aDebugGroup).append("; class: ").append(className).append(
													"; field: ").append(fieldName).toString());
						} catch (SecurityException e) {
							err
									.appendln((new StringBuilder())
											.append(
													"<NSLog> Unable to gather the specified field due to Java security restrictions -- skipping!  String: ")
											.append(aDebugGroup).append("; class: ").append(className).append(
													"; field: ").append(fieldName).toString());
						}
				}
			}
		} else {
			int highEndOfRange = debugGroupMaxBitPos;
			int lowEndOfRange = debugGroupMinBitPos;
			if (rangeDelimiter == 0) {
				String literal = aDebugGroup.substring(1);
				try {
					highEndOfRange = Integer.parseInt(literal);
				} catch (NumberFormatException e) {
					err
							.appendln((new StringBuilder())
									.append(
											"<NSLog> Unable to parse Integer value from the following high string value -- skipping!  String: ")
									.append(aDebugGroup).append("; high: ").append(literal).toString());
					highEndOfRange = -1;
				}
			} else if (rangeDelimiter == aDebugGroup.length() - 1) {
				String literal = aDebugGroup.substring(0, rangeDelimiter);
				try {
					lowEndOfRange = Integer.parseInt(literal);
				} catch (NumberFormatException e) {
					err
							.appendln((new StringBuilder())
									.append(
											"<NSLog> Unable to parse Integer value from the following low string value -- skipping!  String: ")
									.append(aDebugGroup).append("; low: ").append(literal).toString());
					lowEndOfRange = -1;
				}
			} else {
				String literal1 = aDebugGroup.substring(0, rangeDelimiter);
				String literal2 = aDebugGroup.substring(rangeDelimiter + 1);
				try {
					lowEndOfRange = Integer.parseInt(literal1);
					highEndOfRange = Integer.parseInt(literal2);
				} catch (NumberFormatException e) {
					err
							.appendln((new StringBuilder())
									.append(
											"<NSLog> Unable to parse Integer value from one or both of the following string values -- skipping!  String: ")
									.append(aDebugGroup).append("; low: ").append(literal1).append("; high: ").append(
											literal2).toString());
					lowEndOfRange = -1;
					highEndOfRange = -1;
				}
			}

			if (highEndOfRange != -1 && lowEndOfRange != -1) {
				if (highEndOfRange < lowEndOfRange)
					err
							.appendln((new StringBuilder())
									.append(
											"<NSLog> Invalid range: low end value is greater than the high end value -- skipping!  String: ")
									.append(aDebugGroup).append("; low: ").append(lowEndOfRange).append("; high: ")
									.append(highEndOfRange).toString());
				else {
					if (lowEndOfRange < debugGroupMinBitPos || lowEndOfRange > debugGroupMaxBitPos
							|| highEndOfRange < debugGroupMinBitPos || highEndOfRange > debugGroupMaxBitPos) {
						err
								.appendln((new StringBuilder())
										.append(
												"<NSLog> One or both of these literal bit positions are invalid (must be in range 0-63) -- skipping!  String: ")
										.append(aDebugGroup).append("; low: ").append(lowEndOfRange).append("; high: ")
										.append(highEndOfRange).toString());
					} else {
						for (int i = lowEndOfRange; i <= highEndOfRange; i++)
							parsedLongValue |= 1L << i;

					}
				}
			}
		}
		return parsedLongValue;
	}

	public static synchronized void allowDebugLoggingForGroups(long aDebugGroups) {
		debugGroups |= aDebugGroups;
	}
    
    /**
	 * @deprecated Method allowedDebugLevel is deprecated
	 */
    
    @Deprecated
	public static int allowedDebugLevel() {
        return debug.allowedDebugLevel();
    }
    
    public static boolean debugLoggingAllowedForGroups(long aDebugGroups) {
        return _inInitPhase || debug.allowedDebugLevel() > 0 && (debugGroups & aDebugGroups) != 0L;
    }
    
    public static boolean debugLoggingAllowedForLevel(int aDebugLevel) {
		return _inInitPhase && aDebugLevel <= 1 || aDebugLevel > 0 && aDebugLevel <= allowedDebugLevel();
	}

	public static boolean debugLoggingAllowedForLevelAndGroups(int aDebugLevel, long aDebugGroups) {
		return debugLoggingAllowedForLevel(aDebugLevel) && debugLoggingAllowedForGroups(aDebugGroups);
	}

	public static PrintStream printStreamForPath(String aPath) {
		PrintStream aStream = null;
		if (aPath != null) {
			File aFile = new File(aPath);
			try {
				aStream = new PrintStream(new FileOutputStream(aFile), true);
			} catch (IOException e) {
				aStream = null;
			}
		}
		return aStream;
	}

	public static synchronized void refuseDebugLoggingForGroups(long aDebugGroups) {
		debugGroups &= ~aDebugGroups;
	}
    
    public static void setAllowedDebugGroups(long aDebugGroups) {
        debugGroups = aDebugGroups;
    }
    
    /**
	 * @deprecated Method setAllowedDebugLevel is deprecated
	 */
    
    @Deprecated
	public static void setAllowedDebugLevel(int aDebugLevel) {
        debug.setAllowedDebugLevel(aDebugLevel);
    }
    
    public static void setDebug(Logger instance) {
		if (instance != null) {
			debug = instance;
		}
	}

	public static void setDebug(Logger instance, int aDebugLevel) {
		if (instance != null) {
			instance.setAllowedDebugLevel(aDebugLevel);
			debug = instance;
		}
	}

	public static void setErr(Logger instance) {
		if (instance != null) {
			err = instance;
		}
	}

	public static void setOut(Logger instance) {
		if (instance != null) {
			out = instance;
		}
	}
    
    public static String throwableAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
