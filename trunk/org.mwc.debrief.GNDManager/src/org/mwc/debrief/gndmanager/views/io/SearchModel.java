package org.mwc.debrief.gndmanager.views.io;

import java.util.ArrayList;

import org.mwc.debrief.gndmanager.views.ManagerView;


public interface SearchModel
{
	public static interface MatchList
	{
		public Facet getFacet(String name);
		public int getNumMatches();
		public Match getMatch(int index);
	}
	
	public static interface Facet
	{
		public int size();
		public String getName(int index);
		public int getCount(int index);
		public ArrayList<String> toList();
	}
	
	public static interface Match
	{
		public String getName();
		public String getPlatform();
		public String getTrial();
		public String getId();
	}

	MatchList getAll();

	MatchList getMatches(ManagerView view);
}
