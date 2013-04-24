package org.bitbucket.es4gwt.shared.spec;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Mikael Couzic
 */
class FacetFilters implements Iterable<Entry<ElasticFacet, Collection<String>>> {

	private final Multimap<ElasticFacet, String> filters = ArrayListMultimap.create();

	void with(ElasticFacet facet, String filterValue) {
		filters.put(facet, filterValue);
	}

	@Override
	public int hashCode() {
		return filters.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof FacetFilters) {
			FacetFilters that = (FacetFilters) object;
			return this.filters.equals(that.filters);
		}
		return false;
	}

	boolean isEmpty() {
		return filters.isEmpty();
	}

	@Override
	public Iterator<Entry<ElasticFacet, Collection<String>>> iterator() {
		return filters.asMap().entrySet().iterator();
	}

	public Multimap<ElasticFacet, String> asMultimap() {
		return filters;
	}

}
