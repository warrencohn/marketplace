package edu.umich.marketplace;

import org.apache.log4j.Logger;

import com.webobjects.foundation.NSArray;

import edu.umich.marketplace.eof.Advert;
import edu.umich.marketplace.eof.Category;

public class EndCategoryMessages {
	private static final Logger 	logger = Logger.getLogger(EndCategoryMessages.class);

	private final NSArray<String>	 		_messageCategories;	// list of categories which have associated broadcast messages
	private final NSArray<String>	 		_messageTitles;		// list of the panel titles for the broadcast categories
	private final NSArray<String>	 		_messageTexts;		// list of the broadcast messages for the broadcast categories
	private final NSArray<String>	 		_messageStyle;		// list of the text colors for the broadcast categories

	public EndCategoryMessages() {
		super();
		_messageCategories = Session.getSesProps().getNSArray("messageCategories", "");
		_messageTitles = Session.getSesProps().getNSArray("messageTitles", "");
		_messageTexts = Session.getSesProps().getNSArray("messageTexts", "");
		_messageStyle = Session.getSesProps().getNSArray("messageStyle", "");
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
		return ((_messageCategories.count() > 0) ? _messageCategories.containsObject(category.getLongName()) : false);
	}

	public String getMessageTitleForCategory(Category category) {
		return (isMessageForCategory(category) && (_messageTitles.count() > 0) ?
				_messageTitles.objectAtIndex(getBroadcastIndexForCategory(category)) : "");
	}

	public String getMessageTextForCategory(Category category) {
		return (isMessageForCategory(category) && (_messageTexts.count() > 0) ?
				_messageTexts.objectAtIndex(getBroadcastIndexForCategory(category)) : "");
	}

	public String getMessageStyleForCategory(Category category) {
		return (isMessageForCategory(category) && (_messageStyle.count() > 0) ?
				_messageStyle.objectAtIndex(getBroadcastIndexForCategory(category)) : "") ;
	}

//----------------------------------------------------------------------------------------------------------------

	private int getBroadcastIndexForCategory(Category category) {
		return _messageCategories.indexOfObject(category.getLongName());
	}

//----------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		final StringBuffer		sb = new StringBuffer("EndCategoryMessages:");
		sb.append("\n_messageCategories: " + _messageCategories).
		   append("\n    _messageTitles: " + _messageTitles).
		   append("\n     _messageTexts: " + _messageTexts).
		   append("\n    _messageColors: " + _messageStyle);
		return sb.toString();
	}
}