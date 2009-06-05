package edu.umich.marketplace.eof;

import java.util.Date;

import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;

public class Advert extends _Advert {
	private static Logger logger = Logger.getLogger(Advert.class);

	@Override
	public void setIsDeleted(String value) {
		super.setIsDeleted((value.length() == 1) && value.equalsIgnoreCase("N") ? "N" : "Y");
	}

	public boolean isBooleanDeleted() {
		return (super.isDeleted().equalsIgnoreCase("Y"));
	}

	@Override
	public void setItemLink(String link) {
		if (!(link.startsWith("http://") || link.startsWith("https://") || link.startsWith("ftp://"))) {
			super.setItemLink("http://" + link);
		}
		else {
			super.setItemLink(link);
		}
	}

	public static Advert createAdvert(EOEditingContext editingContext, Author author, Category category) {
		logger.trace("--> createAdvert(" + author.uniqname() + ", " + (null == category ? "null" : category.getLongName()) +")");
		return Advert.createAdvert(editingContext, author.uniqname(), "N", author, category);
	}

	public boolean isExpired() {
    	final Date		now = new Date();
        return (expiryDate() != null) && now.after(expiryDate());
	}

	public boolean hasURL() {
        return (itemLink() != null) && (itemLink().trim().length() > 0);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("[ Advert | ");
		sb.append("#" + number() + " | ").append(author().uniqname()).append(" | ").append(itemTitle());
		return sb.append(" ]").toString();
	}


    public boolean isTitleValid() {
        return (itemTitle() != null) && (itemTitle().trim().length() > 0) && (itemTitle().length() < 100);
    }

    //  null URL is valid, but if its not null then perform validation.
    //  for length & "wacky" characters ### need to add later.
    //
    public boolean isURLValid() {
        if (hasURL()) {
			fixStartOfURL();
		}
        return !hasURL() || (hasURL() && (itemLink().length() < 100));
    }

    private void fixStartOfURL() {
        if (! (itemLink().startsWith ("http://") ||
        	   itemLink().startsWith ("https://") ||
        	   itemLink().startsWith ("ftp://"))) {
            setItemLink("http://" + itemLink());
        }
    }

    public boolean getIsBodyValid() {
        return (itemDescription() != null) && 
               (itemDescription().trim().length() > 0) &&
               (itemDescription().length() < 1999);
    }

    public boolean hasPrice() {
        return (itemPrice() != null) && 
        	   (itemPrice() != 0.0D);
    }

    public boolean getIsPriceValid() {
        return (itemPrice() == null) ||
               (itemPrice() >= 0.0D);
    }
}