/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.gndmanager.views.io;

import java.io.IOException;
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
		public String getId();
		public String getName();
		public String getPlatform();
		public String getTrial();
		public String getPlatformType();
	}

	MatchList getAll(String indexURL, String dbURL) throws IOException;

	MatchList getMatches(String indexURL, String dbURL, ManagerView view) throws IOException;
}
