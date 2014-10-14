/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.gndmanager.views;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.mwc.debrief.gndmanager.views.io.SearchModel.MatchList;

public interface ManagerView
{
	public static interface FacetList
	{
		public ArrayList<String> getSelectedItems();
		public void setItems(ArrayList<String> items, boolean keepSelection);
	}
	
	public static class ResultItem
	{
		private final URL _url;
		private final String _name;
		public ResultItem(final URL url, final String name)
		{
			_url = url;
			_name = name;
		}
		public URL getURL()
		{
			return _url;
		}
		public String getName()
		{
			return _name;
		}
		public String toString()
		{
			return getName();
		}
	}

	public static interface Listener
	{
		public void doSearch();
		public void doReset();
		public void doImport(ArrayList<String> items);
		public void doConnect();
	}
	
	public void setListener(Listener listener);
	public FacetList getPlatforms();
	public FacetList getPlatformTypes();
	public FacetList getTrials();
	public String getFreeText();
	void setResults(MatchList res);
	public void setFoxus();
	void enableControls(boolean enabled);
	public ISelectionProvider getSelectionProvider();

}
