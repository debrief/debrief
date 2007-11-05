package com.borlander.ianmayo.nviewer;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import MWC.TacticalData.NarrativeEntry;

public abstract class ColumnFilter {
	private final TreeSet<String> myAllowedValues = new TreeSet<String>();
	private final SortedSet<String> myAllowedValuesRO = Collections.unmodifiableSortedSet(myAllowedValues);

	public abstract String getFilterValue(NarrativeEntry entry);
	protected abstract void valuesSetChanged();

	public boolean accept(NarrativeEntry entry) {
		if (isEmpty()) {
			return true;
		}

		String value = getFilterValue(entry);
		return value != null && myAllowedValues.contains(value);
	}

	public boolean isEmpty() {
		return myAllowedValues.isEmpty();
	}

	public void clear() {
		if (!myAllowedValues.isEmpty()){
			myAllowedValues.clear();
			valuesSetChanged();
		}
	}

	public SortedSet<String> getAllowedValues() {
		return myAllowedValuesRO;
	}
	
	public void setAllowedValues(Collection<String> allowedValues){
		if (myAllowedValues.isEmpty() && allowedValues.isEmpty()){
			return;
		}
		myAllowedValues.clear();
		myAllowedValues.addAll(allowedValues);
		valuesSetChanged();
	}

}
