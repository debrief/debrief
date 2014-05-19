package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.*;

/**
 * @author Mikael Couzic
 */
class DateRange implements ElasticFilter {

	private final ElasticFilter after;
	private final ElasticFilter before;

	public DateRange(String early, String late) {
		checkNotNull(early);
		checkNotNull(late);
		this.after = endAfter(early);
		this.before = startBefore(late);
	}

	@Override
	public String toRequestString() {
		return and(after, before).toRequestString();
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
