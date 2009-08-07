package com.ramsayconz.wocore;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.webobjects.foundation.NSMutableDictionary;

public class SessionInfoDict {
	private static final Logger 	logger = Logger.getLogger (SessionInfoDict.class);

	// ------------------------------------------------------------------------------------------------
	// Session Management -- There is only ever ONE of these structures
	// ------------------------------------------------------------------------------------------------

	/**
	 * A dictionary of dictionaries.
	 * 
	 * The outer-level dictionary is indexed by SessionID.  The values are inner-level dictionaries, 
	 * containing session information.
	 * 
	 * <pre>
	 * _sessionInfoDict
	 *    +------------------------+----------------------------------------+
	 *    | 5sZ3Nre2wDQwg4ZELliIk0 | +------------------------+-----------+ |
	 *    |                        | |              "Session" |       xxx | |
	 *    |                        | +------------------------+-----------+ |
	 *    |                        | |             "uniqname" |     gavin | |
	 *    |                        | +------------------------+-----------+ |
	 *    |                        | |             "HitCount" |        21 | |
	 *    |                        | +------------------------+-----------+ |
	 *    +------------------------+----------------------------------------+
	 *    | lMt61bekKPZGUrKHpk18I0 | +------------------------+-----------+ |
	 *    |                        | |              "Session" |       xxx | |
	 *    |                        | +------------------------+-----------+ |
	 *    |                        | |             "uniqname" |  biterboy | |
	 *    |                        | +------------------------+-----------+ |
	 *    |                        | |             "HitCount" |        21 | |
	 *    |                        | +------------------------+-----------+ |
	 *    +------------------------+----------------------------------------+
	 *</pre>
	 */
	private static NSMutableDictionary<String, NSMutableDictionary<String, Object>> 
						_sessionInfoDict = new NSMutableDictionary<String, NSMutableDictionary<String, Object>>();

	public SessionInfoDict() {
		super();
	}

	/**
	 * Adds sessionInfo to the entry in _sessionInfoDict keyed by sessionID. If there isn't already an entry 
	 * in _sessionInfoDict for sessionID, it creates one and then adds sessionInfo to that dictionary.
	 * 
	 * @param sessionID the string key to index the new entry
	 * @param sessionInfo the sessionInfo NSDictionary we want to record
	 */
	public static void add_SessionInfo (String sessionID, NSMutableDictionary<String, Object> sessionInfo) {
		logger.info("--> add_SessionInfo(" + sessionID + ") : " + sessionInfo +"]");
		_sessionInfoDict.setObjectForKey(sessionInfo, sessionID);
	}
	
	/**
	 * Removes the entry in _sessionInfoDict indexed by sessionID.
	 * 
	 * @sessionID the id of the session to remove
	 */
	public static void sub_SessionInfo(String sessionID) {
		logger.info("--> sub_SessionInfo(" + sessionID + ")");
		_sessionInfoDict.removeObjectForKey(sessionID);
	}

	/**
	 * Id there an entry in _sessionInfoDict indexed by sessionID.
	 * 
	 * @sessionID the id of the session to test for
	 */
	public static boolean any_SessionInfo(String sessionID) {
		logger.info("--> any_SessionInfo(" + sessionID + ")");
		return (null == sessionID) || _sessionInfoDict.containsKey(sessionID);
	}

	/**
	 * PrettyPrint the SessionInfo structure ..
	 */
	public static void log_SessionInfo() {
		if (logger.isTraceEnabled()) {
			NSMutableDictionary<String, Object> sessionInfo;
			boolean				anyContent = false;
			StringBuffer		sb = new StringBuffer("PrettyPrint the SessionInfoDict\n");
			for (String outerKey : _sessionInfoDict.allKeys()) {
				anyContent = true;
				sb.append("+------------------------+----------------------------------+\n");
				sessionInfo = _sessionInfoDict.objectForKey(outerKey);
				boolean			firstTime = true;
				for (String innerKey : sessionInfo.allKeys()) {
					anyContent = true;
					sb.append("| " + (firstTime ? outerKey : "                      ") + " | " + innerKey + " :").
                              append(StringUtils.leftPad(sessionInfo.objectForKey(innerKey).toString(), 22) + " |\n");
					firstTime = false;
				}
				sb.append("+------------------------+----------------------------------+\n");
			}
			if (anyContent) {
				logger.trace(sb.toString());
			}
			else {
				logger.trace(sb.append("-------------------------------------------").toString());
			}
		}
	}
}
