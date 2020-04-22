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

import MWC.GUI.hasPropertyListeners;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public interface AbstractConfiguration extends hasPropertyListeners {

	public static String AREA_PROPERTY = "AREA";

	public static String PERIOD_PROPERTY = "PERIOD";

	public static String TREE_MODEL = "TREE_MODEL";

	public static String SEARCH_PROPERTY = "SEARCH";

	public void addDatafileTypeFilter(final TypeDomain newType);

	public void apply() throws Exception;

	public void doImport();

	public boolean doTestQuery() throws SQLException;

	public WorldArea getCurrentArea();

	public Collection<TypeDomain> getDatafileTypeFilters();

	public WorldLocation getDefaultBottomRight();

	public WorldLocation getDefaultTopLeft();

	public String getFilter();

	String getSearch();

	public TimePeriod getTimePeriod();

	public TreeNode getTreeModel();

	public void removeDatafileTypeFilter(final TypeDomain typeToRemove);

	public void setArea(final WorldArea newArea);

	public void setCurrentViewport();

	public void setFilter(final String _newFilter);

	public void setPepysConnectorBridge(final PepysConnectorBridge _bridge);

	void setSearch(final String _newSearch);

	public void setTimePeriod(final TimePeriod newPeriod);

	public void updateTree();
}
