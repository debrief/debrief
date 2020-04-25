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

package org.mwc.debrief.pepys.model;

import java.sql.SQLException;
import java.util.Collection;

import org.mwc.debrief.pepys.model.tree.TreeNode;
import org.mwc.debrief.pepys.model.tree.TreeUtils.SearchTreeResult;

import MWC.GUI.hasPropertyListeners;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public interface AbstractConfiguration extends hasPropertyListeners {

	static String AREA_PROPERTY = "AREA";

	static String PERIOD_PROPERTY = "PERIOD";

	static String TREE_MODEL = "TREE_MODEL";

	static String SEARCH_PROPERTY = "SEARCH";

	static String HIGHLIGHT_PROPERTY = "HIGHTLIGHT";

	static String SEARCH_RESULT_PROPERTY = "SEARCH_RESULT";

	void addDatafileTypeFilter(final TypeDomain newType);

	void apply() throws Exception;

	void doImport();

	boolean doTestQuery() throws SQLException;

	WorldArea getCurrentArea();

	SearchTreeResult getCurrentSearchTreeResultModel();

	Collection<TypeDomain> getDatafileTypeFilters();

	WorldLocation getDefaultBottomRight();

	WorldLocation getDefaultTopLeft();

	String getFilter();

	SearchTreeResult getHereSearch();

	SearchTreeResult getNextSearch();

	SearchTreeResult getPreviousSearch();

	String getSearch();

	String getSearchResultsText();

	TimePeriod getTimePeriod();

	TreeNode getTreeModel();

	void removeDatafileTypeFilter(final TypeDomain typeToRemove);

	void searchFromUser(final boolean _search);

	void setArea(final WorldArea newArea);

	void setCurrentViewport();

	void setFilter(final String _newFilter);

	void setHighlightedElement(final TreeNode node);

	void setPepysConnectorBridge(final PepysConnectorBridge _bridge);

	void setSearch(final String _newSearch);

	void setSearchResults(final int current, final int total);

	void setTimePeriod(final TimePeriod newPeriod);

	void updateTree();
}
