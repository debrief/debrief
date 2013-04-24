package org.bitbucket.es4gwt.shared.elastic.filter;

import java.util.Collection;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.spec.FilterMode;

import com.google.common.collect.Lists;

/**
 * @author Mikael Couzic
 */
public class Filters {

	private Filters() {
	}

	public static ElasticFilter and(Collection<ElasticFilter> filters) {
		return new And(filters);
	}

	public static ElasticFilter and(ElasticFilter... filters) {
		return new And(Lists.newArrayList(filters));
	}

	public static ElasticFilter or(Collection<ElasticFilter> filters) {
		return new Or(filters);
	}

	public static ElasticFilter or(ElasticFilter... filters) {
		return new Or(Lists.newArrayList(filters));
	}

	public static ElasticFilter term(ElasticFacet facet, String facetValue) {
		return new Term(facet, facetValue);
	}

	public static ElasticFilter terms(ElasticFacet facet, Collection<String> facetValues) {
		return new Terms(facet, facetValues);
	}

	public static ElasticFilter allTerms(ElasticFacet facet, Collection<String> facetValues) {
		return new Terms(facet, facetValues, FilterMode.ALL_OF);
	}

	/**
	 * @param start
	 *            The formatted date representing the start of the range (for example : 2001-01-01 or 2011-12-31)
	 * @param end
	 *            The formatted date representing the end of the range (for example : 2001-01-01 or 2011-12-31)
	 */
	public static ElasticFilter dateRange(String start, String end) {
		return new DateRange(start, end);
	}

	/**
	 * @param date
	 *            The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public static ElasticFilter startBefore(String date) {
		return new StartBefore(date);
	}

	/**
	 * @param date
	 *            The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public static ElasticFilter endAfter(String date) {
		return new EndAfter(date);
	}
}
