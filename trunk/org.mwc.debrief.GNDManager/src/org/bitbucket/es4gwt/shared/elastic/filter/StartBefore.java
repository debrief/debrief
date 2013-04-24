package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Mikael Couzic
 */
class StartBefore implements ElasticFilter {

	private final String date;

	/**
	 * @param dateString
	 *            The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public StartBefore(String date) {
		checkNotNull(date);
		this.date = date + "T23:59:59";
	}

	@Override
	public String toRequestString() {
		return "{\"range\":{\"start\":{\"lte\":\"" + date + "\"}}}";
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
