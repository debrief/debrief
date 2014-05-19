package org.bitbucket.es4gwt.shared.elastic.query;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Mikael Couzic
 */

class Field implements ElasticQuery {

	private final String requestString;

	Field(String fieldName, String fieldValue) {
		checkNotNull(fieldName);
		checkNotNull(fieldValue);
		requestString = "{\"field\":{\"" + fieldName + "\":\"" + fieldValue + "\"}}";
	}

	@Override
	public String toRequestString() {
		return requestString;
	}

}
