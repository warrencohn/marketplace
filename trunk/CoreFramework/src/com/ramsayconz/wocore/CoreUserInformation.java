package com.ramsayconz.wocore;

import org.apache.log4j.Logger;

/**
 * <b>User</b> provides access to primitive information about a logged in user, if the application has them.
 * <pre>
 *                 +-----------+------------------------+
 *                 | "Subject" | ....                   |
 *                 +-----------+------------------------+
 *                 | "Topics"  | +---------+----------+ |
 *                 |           | | "Key-A" | .....    | |
 *                 |           | +---------+----------+ |
 *                 |           | | "Key-B" | ....     | |
 *                 |           | +---------+----------+ |
 *                 |           | | "Key-C" | ......   | |
 *                 |           | +---------+----------+ |
 *                 |           | | "Key-D" | ..       | |
 *                 |           | +---------+----------+ |
 *                 |           | | "....." | . . . .  | |
 *                 |           | +---------+----------+ |
 *                 |           | | "Key-Z" | ....     | |
 *                 |           | +--------------------+ |
 *                 +-----------+------------------------+
 * </pre>
 * @author gavin
 */

public class CoreUserInformation {
	private static final Logger 		logger = Logger.getLogger(CoreUserInformation.class);

	public CoreUserInformation() {

	}

	public String getLoginID() {
		logger.trace("--> getLoginID()");
		return "LoginID";
	}
	
	public void authenticate() {
		
	}
}
