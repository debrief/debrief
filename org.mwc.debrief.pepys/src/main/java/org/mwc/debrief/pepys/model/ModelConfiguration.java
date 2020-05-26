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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

import org.mwc.debrief.pepys.model.bean.Comment;
import org.mwc.debrief.pepys.model.bean.Contact;
import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.PostgresDatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.db.config.LoaderOption;
import org.mwc.debrief.pepys.model.db.config.LoaderOption.LoaderType;
import org.mwc.debrief.pepys.model.tree.TreeNode;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;
import org.mwc.debrief.pepys.model.tree.TreeUtils;
import org.mwc.debrief.pepys.model.tree.TreeUtils.SearchTreeResult;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class ModelConfiguration implements AbstractConfiguration {

	interface InternTreeItemFiltering {
		boolean isAcceptable(final TreeStructurable _item);
	}

	public static String getEnvironmentVariable() {
		return System.getenv(DatabaseConnection.CONFIG_FILE_ENV_NAME);
	}

	private PropertyChangeSupport _pSupport = null;

	private final ArrayList<TypeDomain> currentDatatype = new ArrayList<TypeDomain>();

	private WorldArea currentArea = null;

	private TimePeriod currentPeriod = new BaseTimePeriod();

	private final TreeNode treeModel = new TreeNode(TreeNode.NodeType.ROOT, "", null);

	private PepysConnectorBridge _bridge;

	private Collection<TreeStructurable> currentItems;

	private String filterText = "";

	private String searchText = "";

	private int totalMatches = 0;

	private int currentMatch = 0;

	private TreeNode highlightedNode = null;

	private SearchTreeResult currentSearchTreeResult = null;

	private SearchTreeResult[] searchResults = null;

	// When the user press the button search several times
	// the model should be able to recognize it needs to find
	// a following element.
	// However, in case that the user selected a specific node
	// let's search from there
	// Saul.
	public boolean searchFromUser = true;

	private int treeOrderIndex = 0;

	private DatabaseConnection databaseConnection;

	public ModelConfiguration() {
		final Calendar twentyYearsAgoCal = Calendar.getInstance();
		twentyYearsAgoCal.add(Calendar.YEAR, -20);

		currentPeriod.setStartDTG(new HiResDate(twentyYearsAgoCal.getTime()));

		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (SEARCH_PROPERTY.equals(evt.getPropertyName())) {
					searchResults = TreeUtils.buildTreeSearchMap(getSearch(), getTreeModel());
					getHereSearch();
				}
			}
		});
	}

	@Override
	public void addDatafileTypeFilter(final TypeDomain newType) {
		currentDatatype.add(newType);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport == null) {
			_pSupport = new PropertyChangeSupport(this);
		}
		_pSupport.addPropertyChangeListener(l);
	}

	@Override
	public void apply() throws Exception {

		validate();
		currentItems = TreeUtils.buildStructure(this);
		updateTree();
		setSearch("");

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, TREE_MODEL, null, treeModel);
			_pSupport.firePropertyChange(pce);
		}
	}

	private void buildTreeOrder(final TreeNode treeModel, final HashMap<TreeNode, Integer> treeOrder) {
		treeOrder.put(treeModel, treeOrderIndex++);
		for (final TreeNode child : treeModel.getChildren()) {
			buildTreeOrder(child, treeOrder);
		}
	}

	private int calculateCurrentValue(final TreeNode highlightedNode, final int delta) {
		final HashMap<TreeNode, Integer> treeOrder = new HashMap<TreeNode, Integer>();
		treeOrderIndex = 0;
		buildTreeOrder(getTreeModel(), treeOrder);
		/*
		 * int closest = 0; int closestDifference = 1 << 30; // This could be improved
		 * doing a binary search. // But It will not reduce the linear order of the
		 * calculation....
		 *
		 * for (int i = 0; i < searchResults.length * 2; i++) { final int currentOrder =
		 * treeOrder.get(searchResults[i % searchResults.length].getItem()); if
		 * (Math.abs(currentOrder - desired) < closestDifference) { closestDifference =
		 * Math.abs(currentOrder - desired); closest = i; } } return closest;
		 */
		final int desired = treeOrder.get(highlightedNode);
		if (delta < 0) {
			// We are going back, so, let's search the closest item AFTER
			// the selected item by the user.
			for (int i = searchResults.length - 1; i >= 0; i--) {
				if (treeOrder.get(searchResults[i].getItem()) < desired) {
					return i;
				}
			}
			return searchResults.length - 1;
		} else {
			// It is the same, but the opposite but for next search
			for (int i = 0; i < searchResults.length; i++) {
				if (treeOrder.get(searchResults[i].getItem()) > desired) {
					return i;
				}
			}
			return 0;
		}
	}

	@Override
	public void doImport() {
		if (_bridge == null) {
			/**
			 * In case we don't have a bridge to Full Debrief, it means we are probably
			 * running an unit test (or the deattached version, then simply do a mockup
			 * import process (print to sout) :)
			 */
			doImportProcessMockup(treeModel);
		} else {
			/**
			 * Import process receives a filter method which is used to confirm if the node
			 * is going to be imported to Debrief.
			 *
			 * I am using it to import first all the NON Contacts nodes, and after that I
			 * import only the Contact nodes. It will ensure that we will have already all
			 * the related tracks.
			 */
			doImport(treeModel, new InternTreeItemFiltering() {

				@Override
				public boolean isAcceptable(final TreeStructurable _item) {
					return !(_item instanceof Contact);
				}
			});
			doImport(treeModel, new InternTreeItemFiltering() {

				@Override
				public boolean isAcceptable(final TreeStructurable _item) {
					return _item instanceof Contact;
				}
			});
		}
	}

	private void doImport(final TreeNode treeModel, final InternTreeItemFiltering filter) {
		if (treeModel.isChecked()) {
			for (final TreeStructurable item : treeModel.getItems()) {
				if (filter.isAcceptable(item)) {
					item.doImport(_bridge.getLayers());
				}
			}
		}
		for (final TreeNode child : treeModel.getChildren()) {
			doImport(child, filter);
		}
	}

	private void doImportProcessMockup(final TreeNode treeModel) {
		if (treeModel.isChecked()) {
			for (final TreeStructurable item : treeModel.getItems()) {
				System.out.println("Importing " + treeModel.getName() + " -> " + item);
			}
		}
		for (final TreeNode child : treeModel.getChildren()) {
			doImportProcessMockup(child);
		}
	}

	@Override
	public boolean doTestQuery() throws SQLException {
		return databaseConnection.doTestQuery(new Class[] { Contact.class, State.class, Comment.class });
	}

	@Override
	public WorldArea getCurrentArea() {
		return currentArea;
	}

	@Override
	public SearchTreeResult getCurrentSearchTreeResultModel() {
		return currentSearchTreeResult;
	}

	@Override
	public DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}

	@Override
	public Collection<TypeDomain> getDatafileTypeFilters() {
		return currentDatatype;
	}

	@Override
	public WorldLocation getDefaultBottomRight() {
		return new WorldLocation(-45, 80, 0);
	}

	@Override
	public WorldLocation getDefaultTopLeft() {
		return new WorldLocation(65, -125, 0);
	}

	@Override
	public String getFilter() {
		return filterText;
	}

	@Override
	public SearchTreeResult getHereSearch() {
		final SearchTreeResult result = getSearch(0);
		updateResultUI(result);
		return result;
	}

	@Override
	public SearchTreeResult getNextSearch() {
		final SearchTreeResult result = getSearch(1);
		updateResultUI(result);
		return result;
	}

	@Override
	public SearchTreeResult getPreviousSearch() {
		final SearchTreeResult result = getSearch(-1);
		updateResultUI(result);
		return result;
	}

	@Override
	public String getSearch() {
		return searchText;
	}

	public SearchTreeResult getSearch(final int delta) {
		if (searchResults != null) {
			if (searchResults.length == 0) {
				setSearchResults(0, 0);
			} else {
				int currentValue = currentMatch;
				if (highlightedNode != null && searchFromUser) {
					currentValue = calculateCurrentValue(highlightedNode, delta) - delta;
				}
				int desiredValue = currentValue + delta; // Ok, we are looking the next one.
				desiredValue += searchResults.length; // Circular Search
				desiredValue %= searchResults.length;
				setSearchResults(desiredValue, searchResults.length);
				searchFromUser = false;
				return searchResults[desiredValue];
			}
		} else {
			setSearchResults(-1, -1);
		}
		return null;
	}

	@Override
	public String getSearchResultsText() {
		if (currentMatch < 0 || totalMatches < 0) {
			return "";
		} else if (totalMatches == 0) {
			return "Not Found";
		} else {
			return (currentMatch + 1) + " / " + totalMatches;
		}
	}

	@Override
	public TimePeriod getTimePeriod() {
		return currentPeriod;
	}

	@Override
	public TreeNode getTreeModel() {
		return treeModel;
	}

	@Override
	public void loadDatabaseConfiguration(final DatabaseConfiguration _configuration)
			throws FileNotFoundException, PropertyVetoException, IOException, PepsysException {
		final HashMap<String, String> category = _configuration.getCategory(DatabaseConnection.CONFIGURATION_TAG);
		if (category != null && category.containsKey(DatabaseConnection.CONFIGURATION_DATABASE_TYPE)) {
			final String databaseType = category.get(DatabaseConnection.CONFIGURATION_DATABASE_TYPE);
			if (databaseType != null) {
				final DatabaseConnection conn;
				switch (databaseType) {
				case DatabaseConnection.POSTGRES:
					conn = new PostgresDatabaseConnection();
					break;
				case DatabaseConnection.SQLITE:
					conn = new SqliteDatabaseConnection();
					break;
				default:
					conn = null;
					break;
				}
				if (conn != null) {
					conn.initializeInstance(_configuration);
				}
				databaseConnection = conn;
			}
		}
	}

	@Override
	public void loadDatabaseConfiguration(final InputStream configurationFile)
			throws PropertyVetoException, IOException, PepsysException {
		final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

		ConfigurationReader.loadDatabaseConfiguration(databaseConfiguration, configurationFile);

		loadDatabaseConfiguration(databaseConfiguration);
	}

	@Override
	public void loadDefaultDatabaseConfiguration() throws PropertyVetoException, IOException, PepsysException {
		final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

		ConfigurationReader.loadDatabaseConfiguration(databaseConfiguration,

				new LoaderOption[] { new LoaderOption(LoaderType.ENV_VARIABLE, getEnvironmentVariable()),
						new LoaderOption(LoaderType.DEFAULT_FILE, DatabaseConnection.DEFAULT_DATABASE_FILE) });

		loadDatabaseConfiguration(databaseConfiguration);
	}

	@Override
	public void removeDatafileTypeFilter(final TypeDomain typeToRemove) {
		currentDatatype.remove(typeToRemove);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport != null) {
			_pSupport.removePropertyChangeListener(l);
		}
	}

	@Override
	public void searchFromUser(final boolean _search) {
		this.searchFromUser = _search;
	}

	@Override
	public void setArea(final WorldArea newArea) {
		final WorldArea oldArea = currentArea;
		currentArea = newArea;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, AREA_PROPERTY, oldArea,
					currentArea);
			_pSupport.firePropertyChange(pce);
		}
	}

	public void setCurrentSearchTreeResult(final SearchTreeResult currentSearchTreeResult) {
		this.currentSearchTreeResult = currentSearchTreeResult;
	}

	@Override
	public void setCurrentViewport() {
		if (_bridge != null) {
			setArea(_bridge.getCurrentArea());
		} else {
			final WorldArea demoArea = new WorldArea(getDefaultTopLeft(), getDefaultBottomRight());
			setArea(demoArea);
		}
	}

	@Override
	public void setFilter(final String _newFilter) {
		filterText = _newFilter;
	}

	@Override
	public void setHighlightedElement(final TreeNode node) {
		this.highlightedNode = node;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, HIGHLIGHT_PROPERTY,
					null, node);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void setPepysConnectorBridge(final PepysConnectorBridge _bridge) {
		this._bridge = _bridge;
	}

	@Override
	public void setSearch(final String _newSearch) {
		final String oldSearch = searchText;
		searchText = _newSearch;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, SEARCH_PROPERTY, oldSearch,
					searchText);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void setSearchResults(final int current, final int total) {
		final String oldResults = getSearchResultsText();
		this.currentMatch = current;
		this.totalMatches = total;
		final String newResults = getSearchResultsText();

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, SEARCH_RESULT_PROPERTY, oldResults,
					newResults);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void setTimePeriod(final TimePeriod newPeriod) {
		final TimePeriod oldPeriod = currentPeriod;
		currentPeriod = newPeriod;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, PERIOD_PROPERTY, oldPeriod,
					currentPeriod);
			_pSupport.firePropertyChange(pce);
		}
	}

	public void updateResultUI(final SearchTreeResult searchResult) {
		if (searchResult != null) {
			setCurrentSearchTreeResult(searchResult);
			setHighlightedElement(searchResult.getItem());
		}
	}

	@Override
	public void updateTree() {
		if (currentItems != null) {
			TreeUtils.buildStructure(currentItems.toArray(new TreeStructurable[] {}), getTreeModel());

			if (_pSupport != null) {
				final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, TREE_MODEL, null, treeModel);
				_pSupport.firePropertyChange(pce);
			}
		}
	}

	@Override
	public void validate() throws Exception {
		if (!currentPeriod.isConsistent()) {
			throw new PepsysException("Date validation", "The Start date-time must be before the End date-time");
		}
	}
}
