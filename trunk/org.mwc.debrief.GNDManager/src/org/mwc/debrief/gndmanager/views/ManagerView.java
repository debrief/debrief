package org.mwc.debrief.gndmanager.views;

import java.net.URL;
import java.util.ArrayList;

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
		public ResultItem(URL url, String name)
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

}
