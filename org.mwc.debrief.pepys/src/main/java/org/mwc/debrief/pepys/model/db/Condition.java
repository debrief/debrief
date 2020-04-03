package org.mwc.debrief.pepys.model.db;

public class Condition {
	private final String conditionQuery;

	public Condition(final String conditionQuery) {
		super();
		this.conditionQuery = conditionQuery;
	}

	public String getConditionQuery() {
		return conditionQuery;
	}

}
