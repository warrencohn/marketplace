package com.ramsayconz.wocore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Help provides access to HTML help panels for the MarketPlace application (to start with)
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

public class CoreHelpRepo {
	private static final Logger logger = Logger.getLogger(CoreHelpRepo.class);

	private static NSMutableDictionary<String, Object> _helpRepo = new NSMutableDictionary<String, Object>();

	public CoreHelpRepo(String helpMainName) {
		super();
		logger.trace("+++ constructor for " + helpMainName + " help");
	}

	public void LoadHelpData(InputStream is) {
		logger.trace("--- : LoadHelpData InputStream=" + is);
		NSMutableDictionary<String, String> topics = new NSMutableDictionary<String, String>();
		
		Pattern keyValueLine = Pattern.compile("^(\\S*)\\s*(.*)");		// "KKK   VVVVVVV"
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String inRecord, key = null;
			StringBuffer value = new StringBuffer();
			
			while ((inRecord = in.readLine()) != null) {				// read a line
				Matcher m = keyValueLine.matcher(inRecord);				// match it to the pattern
				if (m.matches()) {										// if it matches
					if (m.group(1).startsWith("//") || m.group(1).startsWith("--")) {
						continue;
					}
					if (m.group(1).length() > 0) {						// and if there is a "key"
						if (key != null) {								// if we have a key-val in process
							topics.put(key, value.toString());			// save it and
							value = new StringBuffer();					// prepare a new "val"
						}
						key = m.group(1);								// and remember the new "key"
						if (m.group(2).length() == 0) {
							topics = new NSMutableDictionary<String, String>();
							_helpRepo.put(m.group(1), topics);
							key = null;
							continue;
						}
					}
					if (m.group(2).length() > 0) {						// and if there is a "val"
						value.append(" ").append(m.group(2));			// append any "val" text 
					}
				}
			}
			if (key != null) {
				topics.put(key, value.toString().trim());
			}
			in.close();
		} catch (Exception x) {
			logger.error("!-- : LoadHelpData failed: ", x);
		}
	}
	
	public static NSDictionary<String, Object> getHelpRepo() {
		return _helpRepo.immutableClone();
	}

	@SuppressWarnings("unchecked")
	public String prettyPrint() {
		StringBuffer 	sb = new StringBuffer("\n");
		
		sb.append("+- HelpRepo ---+------------------------------------------------\n");
		
		for (String topic : _helpRepo.allKeys()) {
			sb.append("| " + StringUtils.leftPad(topic, 12) + " | +------------------+---------------------------\n");
			
			Object 		items = _helpRepo.get(topic);
			if (items instanceof NSMutableDictionary) {
				NSMutableDictionary<String,String> new_name = (NSMutableDictionary<String,String>) items;
				for (String key : new_name.allKeys()) {
					sb.append("|              | | " + StringUtils.leftPad(key, 16) + " | " + new_name.get(key) + "\n");
				}
			}
			
			sb.append("|              | +------------------+---------------------------\n");
		}
		sb.append("+--------------+------------------------------------ HelpRepo --\n");

		return sb.toString();
	}
}
