package org.bitbucket.es4gwt.shared.spec;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

/**
 * @author Mikael Couzic
 */

public class SearchDate {

	private final Date date;
	private final String dateString;

	/**
	 * @param dateString
	 *            The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public SearchDate(Date date, String dateString) {
		checkNotNull(date);
		checkNotNull(dateString);
		this.date = date;
		this.dateString = dateString;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof SearchDate) {
			SearchDate that = (SearchDate) object;
			return this.date.getYear() == that.date.getYear() && this.date.getMonth() == that.date.getMonth()
					&& this.date.getDate() == that.date.getDate();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int hashCode() {
		return date.getDate() + date.getMonth() * 100 + date.getYear() * 10000;
	}

	@Override
	public String toString() {
		return dateString;
	}

	public Date asDate() {
		return (Date) date.clone();
	}
}
