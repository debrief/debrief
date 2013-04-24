package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Mikael Couzic
 */
class EndAfter implements ElasticFilter {

	private String date;

	/**
	 * @param date
	 *            The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public EndAfter(String date) {
		checkNotNull(date);
		this.date = date + "T00:00:00";
	}

	@Override
	public String toRequestString() {
		return "{\"range\":{\"end\":{\"gte\":\"" + date + "\"}}}";
	}

	@Override
	public String toString() {
		return toRequestString();
	}
}
