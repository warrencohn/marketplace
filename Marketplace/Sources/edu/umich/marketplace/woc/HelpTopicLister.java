package edu.umich.marketplace.woc;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreHelpRepo;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;


public class HelpTopicLister extends MPComponent {
	private static final Logger 	logger = Logger.getLogger (HelpTopicLister.class);

	private NSArray<String> 		_allQs = null;
	private NSArray<String> 		_allAs = null;

	public String					topic;
	public Integer					index;

    public HelpTopicLister(WOContext context) {
        super(context);
    }

	public NSArray<String>			getTopics() {
		if (null == _allQs) {
			final Object	items = CoreHelpRepo.getHelpRepo().get("TOPICS");
			if ((items != null) && (items instanceof NSMutableDictionary)) {
				final NSMutableArray<String> qKeys = new NSMutableArray<String>();
				final NSMutableArray<String> aKeys = new NSMutableArray<String>();
				for (final String key : ((NSDictionary<String, String>) items).allKeys()) {
					if (key.startsWith("M")) {
						qKeys.add(key);
					}
					if (key.startsWith("T")) {
						aKeys.add(key);
					}
				}

				try {
					_allQs = ((NSDictionary<String, String>) items).objectsForKeys(
							qKeys.sortedArrayUsingComparator(
									NSComparator.AscendingCaseInsensitiveStringComparator), "");
					_allAs = ((NSDictionary<String, String>) items).objectsForKeys(
							aKeys.sortedArrayUsingComparator(
									NSComparator.AscendingCaseInsensitiveStringComparator), "");
				}
				catch (final NSComparator.ComparisonException x) {
					logger.error("?   Help Topics sort failed: ", x);
				}

				logger.trace("~~~ finish: " + _allQs);
			}
			else {
				logger.error("?   Help Topics missing?");
				_allQs = new NSArray<String>("NO HELP TOPICS AVAILABLE");
			}
		}
		return _allQs;
	}

	public String getTopicKey() {
		return "T" + index.toString();
	}

	public String getTopicVal() {
		return _allAs.get(index);
	}

	public String getOnClick() {
		return "onclick=" + getTopicKey() + "()" + " class='btn' style='cursor:hand'";
	}
}