package edu.umich.marketplace;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreSession;
import com.webobjects.foundation.NSArray;

import edu.umich.marketplace.eof.Advert;
import edu.umich.marketplace.eof.Category;

public class EndCategoryMessages {
	private static final Logger 	logger = Logger.getLogger(EndCategoryMessages.class);

	private final NSArray<String>	 	_categoriesMsg;	// list of categories which have associated broadcast messages
	private final NSArray<String>	 	_catMsgTitle;	// list of the panel titles for the broadcast categories
	private final NSArray<String>		_catMsgTexts;	// list of the broadcast messages for the broadcast categories
	private final NSArray<String>	 	_catMsgStyle;	// list of the text colors for the broadcast categories

	public EndCategoryMessages() {
		super();
		_categoriesMsg = CoreSession.properties.getNSArray("session.categoriesWithAlerts", "");
		_catMsgTitle = CoreSession.properties.getNSArray("session.catAlertTitles", "");
		_catMsgTexts = CoreSession.properties.getNSArray("session.catAlertTexts", "");
		_catMsgStyle = CoreSession.properties.getNSArray("session.catAlertStyles", "");
	}

	public boolean anyMessagesForAds(NSArray<Advert> ads) {
		logger.trace("--> anyBroadcastMessagesForAds()");
		for (final Advert ad : ads) {
			if (isMessageForCategory(ad.category())) {
				return true;
			}
		}
		return false;
	}

	public boolean isMessageForCategory(Category category) {
		return ((_categoriesMsg.count() > 0) ? _categoriesMsg.containsObject(category.getLongName()) : false);
	}

	public String getMessageTitleForCategory(Category category) {
		return (isMessageForCategory(category) && (_catMsgTitle.count() > 0) ?
				_catMsgTitle.objectAtIndex(getBroadcastIndexForCategory(category)) : "");
	}

	public String getMessageTextForCategory(Category category) {
		return (isMessageForCategory(category) && (_catMsgTexts.count() > 0) ?
				_catMsgTexts.objectAtIndex(getBroadcastIndexForCategory(category)) : "");
	}

	public String getMessageStyleForCategory(Category category) {
		return (isMessageForCategory(category) && (_catMsgStyle.count() > 0) ?
				_catMsgStyle.objectAtIndex(getBroadcastIndexForCategory(category)) : "") ;
	}

//----------------------------------------------------------------------------------------------------------------

	private int getBroadcastIndexForCategory(Category category) {
		return _categoriesMsg.indexOfObject(category.getLongName());
	}

//----------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		final StringBuffer		sb = new StringBuffer("EndCategoryMessages:");
		sb.append("\n_session.categoriesWithAlerts: " + _categoriesMsg).
		   append("\n    _session.catAlertTitles: " + _catMsgTitle).
		   append("\n     _session.catAlertTexts: " + _catMsgTexts).
		   append("\n    _messageColors: " + _catMsgStyle);
		return sb.toString();
	}
}