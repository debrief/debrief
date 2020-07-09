/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.gndmanager.views;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.mwc.debrief.gndmanager.views.io.SearchModel.MatchList;

public interface ManagerView {
	public static interface FacetList {
		public ArrayList<String> getSelectedItems();

		public void setItems(ArrayList<String> items, boolean keepSelection);
	}

	public static interface Listener {
		public void doConnect();

		public void doImport(ArrayList<String> items);

		public void doReset();

		public void doSearch();
	}

	public static class ResultItem {
		private final URL _url;
		private final String _name;

		public ResultItem(final URL url, final String name) {
			_url = url;
			_name = name;
		}

		public String getName() {
			return _name;
		}

		public URL getURL() {
			return _url;
		}

		@Override
		public String toString() {
			return getName();
		}
	}

	void enableControls(boolean enabled);

	public String getFreeText();

	public FacetList getPlatforms();

	public FacetList getPlatformTypes();

	public ISelectionProvider getSelectionProvider();

	public FacetList getTrials();

	public void setFoxus();

	public void setListener(Listener listener);

	void setResults(MatchList res);

}
