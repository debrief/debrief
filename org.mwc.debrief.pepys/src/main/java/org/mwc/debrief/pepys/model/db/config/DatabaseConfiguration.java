package org.mwc.debrief.pepys.model.db.config;

import java.util.HashMap;

public class DatabaseConfiguration {

	private final HashMap<String, HashMap<String, String>> categories = new HashMap<String, HashMap<String, String>>();

	public DatabaseConfiguration() {

	}

	public void clear() {
		categories.clear();
	}

	public HashMap<String, String> getCategory(final String str) {
		if (str != null) {
			if (!categories.containsKey(str)) {
				categories.put(str, new HashMap<String, String>());
			}
			return categories.get(str);
		}
		return null;
	}
}
