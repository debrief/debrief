package org.bitbucket.es4gwt.shared.spec;

import static com.google.common.base.Preconditions.*;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;

import com.google.common.collect.Maps;

/**
 * @author Mikael Couzic
 */
public class SearchRequest {

	private static final String UNDEFINED = "Undefined";

	private final FacetFilters facetFilters = new FacetFilters();

	// Text search related fields
	private String text = UNDEFINED;
	private ElasticFacet[] textSearchParams;

	private Map<ElasticFacet, FilterMode> filterModes = Maps.newHashMap();
	private SearchDate start = UNDEFINED_DATE;
	private SearchDate end = UNDEFINED_DATE;

	public void fullTextSearch(String text, ElasticFacet[] textSearchParams) {
		checkNotNull(text);
		checkArgument(!text.isEmpty());
		checkNotNull(textSearchParams);
		this.text = text;
		checkNotNull(textSearchParams);
		checkArgument(textSearchParams.length > 0);
		this.textSearchParams = textSearchParams;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof SearchRequest) {
			SearchRequest that = (SearchRequest) object;
			return this.text.equals(that.text) && this.facetFilters.equals(that.facetFilters)
					&& equalFilterModes(that)
					&& this.start.equals(that.start)
					&& this.end.equals(that.end);
		}
		return false;
	}

	private boolean equalFilterModes(SearchRequest that) {
		if (this.filterModes.size() != that.filterModes.size())
			return false;
		for (Entry<ElasticFacet, FilterMode> entry : this.filterModes.entrySet()) {
			if (!entry.getValue().equals(that.getFilterMode(entry.getKey())))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int toReturn = facetFilters.hashCode() + text.hashCode() + start.hashCode() + (end.hashCode() * 100000);
		for (Entry<ElasticFacet, FilterMode> entry : this.filterModes.entrySet())
			toReturn += entry.getKey().hashCode() + entry.getValue().hashCode();
		return toReturn;
	}

	public void withFacetFilter(ElasticFacet facet, String filterValue) {
		checkNotNull(facet);
		checkNotNull(filterValue);
		facetFilters.with(facet, filterValue);
	}

	public void withFilterMode(ElasticFacet facet, FilterMode filterMode) {
		checkNotNull(facet);
		checkNotNull(filterMode);
		filterModes.put(facet, filterMode);
	}

	public void after(SearchDate start) {
		checkNotNull(start);
		checkArgument(start != UNDEFINED_DATE);
		this.start = start;
	}

	public void before(SearchDate end) {
		checkNotNull(end);
		checkArgument(end != UNDEFINED_DATE);
		this.end = end;
	}

	private static final SearchDate UNDEFINED_DATE = new SearchDate(new Date(0l), "1970-01-01") {

		@Override
		public boolean equals(Object object) {
			return this == object;
		}

		@Override
		public String toString() {
			return "UNDEFINED DATE";
		}
	};

	public boolean hasFacetFilters() {
		return !facetFilters.isEmpty();
	}

	FacetFilters getFacetFilters() {
		return facetFilters;
	}

	public String getText() {
		return text;
	}

	public ElasticFacet[] getTextSearchParams() {
		return textSearchParams;
	}

	public FilterMode getFilterMode(ElasticFacet facet) {
		return filterModes.get(facet);
	}

	public SearchDate getStartDate() {
		return start;
	}

	public SearchDate getEndDate() {
		return end;
	}

	public boolean isTextDefined() {
		return !text.equals(UNDEFINED) && !text.isEmpty();
	}

	public boolean isStartDateDefined() {
		return !start.equals(UNDEFINED_DATE);
	}

	public boolean isEndDateDefined() {
		return !end.equals(UNDEFINED_DATE);
	}

	public boolean isFiltered() {
		return !facetFilters.isEmpty() || isTextDefined() || isStartDateDefined() || isEndDateDefined();
	}

	public Iterator<Entry<ElasticFacet, Collection<String>>> getFacetFiltersIterator() {
		return facetFilters.iterator();
	}

	public Set<ElasticFacet> getFilterModeFacets() {
		return filterModes.keySet();
	}

}
