package org.bitbucket.es4gwt.shared.elastic.filter;

import java.util.Collection;

/**
 * Smart "or" filter. If it is passed only one filter, it will behave as if it was that filter
 * 
 * @author Mikael Couzic
 */
class Or extends BooleanOperator {

	Or(Collection<ElasticFilter> filters) {
		super(filters);
	}

	@Override
	String getOperatorName() {
		return "or";
	}

}
