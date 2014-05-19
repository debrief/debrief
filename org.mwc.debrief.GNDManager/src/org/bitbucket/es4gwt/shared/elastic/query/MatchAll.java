package org.bitbucket.es4gwt.shared.elastic.query;


/**
 * @author Mikael Couzic
 */

class MatchAll implements ElasticQuery {

	MatchAll() {
	}

	@Override
	public String toRequestString() {
		return "{\"match_all\":{}}";
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
