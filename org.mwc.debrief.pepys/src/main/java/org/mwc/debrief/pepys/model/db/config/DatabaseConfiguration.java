package org.mwc.debrief.pepys.model.db.config;

import java.util.HashMap;

public class DatabaseConfiguration {

	public HashMap<String, HashMap<String, String>> categories = new HashMap<String, HashMap<String, String>>();

	public DatabaseConfiguration() {

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
