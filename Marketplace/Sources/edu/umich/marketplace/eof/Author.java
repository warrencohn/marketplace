package edu.umich.marketplace.eof;

import org.apache.log4j.Logger;

public class Author extends _Author {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Author.class);

	/**
	 * toString should emit a 'standard' appearance for Advert, Author, and Category
	 *
	 * |- Author   | uniqname | ...
	 * 0         1         2         3         4         5         6         7         8
	 * 012345678901234567890123456789012345678901234567890123456789012345678901234567890...
	 */
	
	public String getAuthorEmail() {
		return uniqname() + "@umich.edu";
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("[ Author | ");
		sb.append(this.uniqname()).append(" | ").append(this.previousVisit());
		return sb.append(" ]").toString();
	}
}
