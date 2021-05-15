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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.preferences.PrefsPage;
import org.mwc.debrief.model.utils.OSUtils;
import org.mwc.debrief.pepys.model.bean.Comment;
import org.mwc.debrief.pepys.model.bean.Contact;
import org.mwc.debrief.pepys.model.bean.Datafile;
import org.mwc.debrief.pepys.model.bean.Platform;
import org.mwc.debrief.pepys.model.bean.Sensor;
import org.mwc.debrief.pepys.model.bean.SensorType;
import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.bean.custom.CommentFastMode;
import org.mwc.debrief.pepys.model.bean.custom.ContactFastMode;
import org.mwc.debrief.pepys.model.bean.custom.StateFastMode;
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

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;
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

	private boolean splitByDafile = false;

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
		switch (getAlgorithmType()) {
		case FAST_MODE_STORED_PROC:
			currentItems = TreeUtils.buildStructureFastMode(this);
			break;

		case FAST_MODE:
			// currentItems = TreeUtils.buildStructure(this);
			currentItems = TreeUtils.buildStructureFastMode(this);
			break;

		case LEGACY:
			currentItems = TreeUtils.buildStructure(this);
			break;

		default:
			currentItems = TreeUtils.buildStructure(this);
			break;
		}
		updateTree();
		setSearch("");

		if (_pSupport != null) {
			Application.logError2(ToolParent.INFO, "We are going to trigger the apply listeners for change of model",
					null);
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
	public int doImport() throws Exception {
		if (_bridge == null) {
			/**
			 * In case we don't have a bridge to Full Debrief, it means we are probably
			 * running an unit test (or the deattached version, then simply do a mockup
			 * import process (print to sout) :)
			 */
			return doImportProcessMockup(treeModel);
		} else {
			/*
			 * ok, we are now sure that we can import to Debrief, but if we are in fast
			 * model we need to populate the missing values
			 */

			if (QUERY_STRATEGY.FAST_MODE == getAlgorithmType()
					|| QUERY_STRATEGY.FAST_MODE_STORED_PROC == getAlgorithmType()) {
				// Let's populate it
				return importFastMode(treeModel);
			} else {

				int total = 0;
				/**
				 * Import process receives a filter method which is used to confirm if the node
				 * is going to be imported to Debrief.
				 *
				 * I am using it to import first all the NON Contacts nodes, and after that I
				 * import only the Contact nodes. It will ensure that we will have already all
				 * the related tracks.
				 */
				total += doImport(treeModel, new InternTreeItemFiltering() {

					@Override
					public boolean isAcceptable(final TreeStructurable _item) {
						return !(_item instanceof Contact);
					}
				});
				total += doImport(treeModel, new InternTreeItemFiltering() {

					@Override
					public boolean isAcceptable(final TreeStructurable _item) {
						return _item instanceof Contact;
					}
				});
				return total;
			}
		}
	}

	private int doImport(final TreeNode treeModel, final InternTreeItemFiltering filter) {
		int total = 0;
		// I have created this boolean because we have can several items imported in the
		// same
		// data file
		boolean imported = false;
		if (treeModel.isChecked()) {
			for (final TreeStructurable item : treeModel.getItems()) {
				if (filter.isAcceptable(item)) {
					imported = true;
					item.doImport(_bridge.getLayers(), isSplitByDatafile());
				}
			}
		}
		if (imported) {
			++total;
		}
		for (final TreeNode child : treeModel.getChildren()) {
			total += doImport(child, filter);
		}
		return total;
	}

	private int doImportProcessMockup(final TreeNode treeModel) {
		int total = 0;
		if (treeModel.isChecked()) {
			++total;
			for (final TreeStructurable item : treeModel.getItems()) {
				System.out.println("Importing " + treeModel.getName() + " -> " + item);
			}
		}
		for (final TreeNode child : treeModel.getChildren()) {
			total += doImportProcessMockup(child);
		}
		return total;
	}

	@Override
	public boolean doTestQuery() throws SQLException {
		return databaseConnection.doTestQuery(new Class[] { Contact.class, State.class, Comment.class });
	}

	@Override
	/**
	 * Method that returns the algorithm to build the tree We are going to use (only
	 * for now) the fast mode for Postgres, and Legacy Mode for SQLite. It is open
	 * to future improvements.
	 */
	public QUERY_STRATEGY getAlgorithmType() {
		if (databaseConnection != null) {
			if (databaseConnection instanceof SqliteDatabaseConnection) {
				return QUERY_STRATEGY.LEGACY;
			} else if (databaseConnection instanceof PostgresDatabaseConnection) {
				final boolean useStoredProcedures = CorePlugin.getDefault().getPreferenceStore()
						.getBoolean(PrefsPage.PreferenceConstants.PEPYS_USE_STORED_FUNCTIONS);
				if (useStoredProcedures) {
					return QUERY_STRATEGY.FAST_MODE_STORED_PROC;
				} else {
					return QUERY_STRATEGY.FAST_MODE;
				}
			}
		}
		// LEGACY AS DEFAULT.
		return QUERY_STRATEGY.LEGACY;
	}

	@Override
	public String getCommentQuery(final QUERY_STRATEGY algorithType) {
		switch (algorithType) {
		case FAST_MODE:
			return "/comments.sql";

		case FAST_MODE_STORED_PROC:
			return "/commentsProc.sql";

		default:
			return null;
		}
	}

	@Override
	public String getContactQuery(final QUERY_STRATEGY algorithType) {
		switch (algorithType) {
		case FAST_MODE:
			return "/contacts.sql";

		case FAST_MODE_STORED_PROC:
			return "/contactsProc.sql";

		default:
			return null;
		}
	}

	@Override
	public WorldArea getCurrentArea() {
		return currentArea;
	}

	@Override
	public String getCurrentAreaAsParameter() {
		final WorldArea currentArea = getCurrentArea();
		if (currentArea != null) {

			final WorldLocation topLeft = currentArea.getTopLeft();
			final WorldLocation bottomRight = currentArea.getBottomRight();
			final WorldLocation topRight = currentArea.getTopRight();
			final WorldLocation bottomLeft = currentArea.getBottomLeft();

			final String polygonArea = getDatabaseConnection().getSRID() + "POLYGON((" + topLeft.getLong() + " "
					+ topLeft.getLat() + "," + bottomLeft.getLong() + " " + bottomLeft.getLat() + ","
					+ bottomRight.getLong() + " " + bottomRight.getLat() + "," + topRight.getLong() + " "
					+ topRight.getLat() + "," + topLeft.getLong() + " " + topLeft.getLat() + "))";
			return polygonArea;
		} else {
			return null;
		}
	}

	@Override
	public SearchTreeResult getCurrentSearchTreeResultModel() {
		return currentSearchTreeResult;
	}

	/**
	 * Returns the current time period in an array of 2 elements Start, End
	 *
	 * @return
	 */
	public String[] getCurrentTimePeriodAsParameter() {
		final String[] answer = { null, null };
		final SimpleDateFormat sqlDateFormat = new SimpleDateFormat(SqliteDatabaseConnection.SQLITE_DATE_FORMAT);
		final TimePeriod timePeriod = getTimePeriod();
		if (timePeriod != null) {
			if (timePeriod.getStartDTG() != null && timePeriod.getStartDTG().getDate() != null) {
				answer[0] = (sqlDateFormat.format(timePeriod.getStartDTG().getDate()));
			} else {
				answer[0] = (null);
			}
			if (timePeriod.getEndDTG() != null && timePeriod.getEndDTG().getDate() != null) {
				answer[1] = (sqlDateFormat.format(timePeriod.getEndDTG().getDate()));
			} else {
				answer[1] = (null);
			}
		} else {
			answer[0] = (null);
			answer[1] = (null);
		}
		return answer;
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
	public String getMeasurementQuery(final QUERY_STRATEGY algorithType) {
		switch (algorithType) {
		case FAST_MODE:
			return "/measurements.sql";

		case FAST_MODE_STORED_PROC:
			return "/measurementsProc.sql";

		default:
			return null;
		}
	}

	@Override
	public SearchTreeResult getNextSearch() {
		final SearchTreeResult result = getSearch(1);
		updateResultUI(result);
		return result;
	}

	@Override
	public QueryParameterAccumulator getParameterAccumulator() {
		if (getAlgorithmType() == QUERY_STRATEGY.FAST_MODE) {
			return new QueryParameterAccumulator() {
				final StringBuilder builder = new StringBuilder();

				@Override
				public void addPart(final Object o) {
					builder.append(o);
					builder.append(",");
				}

				@Override
				public Object getAccumulated() {
					if (builder.length() > 0) {
						builder.setLength(builder.length() - 1);
					}
					return builder.toString();
				}
			};
		} else if (getAlgorithmType() == QUERY_STRATEGY.FAST_MODE_STORED_PROC) {
			return new QueryParameterAccumulator() {
				final ArrayList<Object> accumulator = new ArrayList<>();

				@Override
				public void addPart(final Object o) {
					accumulator.add(o);
				}

				@Override
				public Object getAccumulated() {
					return accumulator;
				}
			};
		}
		return null;
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
	public String getStateQuery(final QUERY_STRATEGY algorithType) {
		if (getAlgorithmType() == QUERY_STRATEGY.FAST_MODE) {
			return "/states.sql";
		} else if (getAlgorithmType() == QUERY_STRATEGY.FAST_MODE_STORED_PROC) {
			return "/statesProc.sql";
		}
		return null;
	}

	@Override
	public TimePeriod getTimePeriod() {
		return currentPeriod;
	}

	@Override
	public TreeNode getTreeModel() {
		return treeModel;
	}

	/**
	 *
	 * @param treeModel
	 * @throws IOException
	 * @throws SQLException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	private int importFastMode(final TreeNode treeModel)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, SQLException {
		final ArrayList<State> selectedStates = new ArrayList<State>();
		final ArrayList<Comment> selectedComments = new ArrayList<Comment>();
		final ArrayList<Contact> selectedContacts = new ArrayList<Contact>();
		final HashMap<String, String> datafilesNames = new HashMap<String, String>();

		int total = 0;
		populateSelectedDatafiles(treeModel, selectedStates, selectedComments, selectedContacts);

		total += populateFastModeStates(selectedStates, datafilesNames);
		total += populateFastModeComments(selectedComments);
		total += populateFastModeContacts(selectedContacts);
		return total;
	}

	@Override
	public boolean isSplitByDatafile() {
		return splitByDafile;
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

		// Here we are going to try to load the environmental variable,
		// and if it not found, we try the default configuration

		final String envVariable = getEnvironmentVariable();
		final LoaderOption option;
		if (envVariable == null) {
			option = new LoaderOption(LoaderType.DEFAULT_FILE, DatabaseConnection.DEFAULT_DATABASE_FILE);
		} else {
			option = new LoaderOption(LoaderType.ENV_VARIABLE, getEnvironmentVariable());
		}

		ConfigurationReader.loadDatabaseConfiguration(databaseConfiguration,

				new LoaderOption[] { option });

		loadDatabaseConfiguration(databaseConfiguration);
	}

	private int populateFastModeComments(final ArrayList<Comment> selectedComments)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, SQLException {
		if (selectedComments.isEmpty()) {
			return 0;
		} else {
			final String query = OSUtils.readFile(CommentFastMode.class, getCommentQuery(getAlgorithmType()));

			final List<Object> parameters = new ArrayList<>();

			// Let's add the time period filter
			final String[] periodFilter = getCurrentTimePeriodAsParameter();
			parameters.add(periodFilter[0]);
			parameters.add(periodFilter[1]);

			parameters.add(getFilter());

			// Let's create ids.
			final QueryParameterAccumulator platformAccumulator = getParameterAccumulator();
			final QueryParameterAccumulator sourceAccumulator = getParameterAccumulator();

			for (final Comment comment : selectedComments) {
				platformAccumulator.addPart(comment.getPlatform().getPlatform_id());
				sourceAccumulator.addPart(comment.getDatafile().getDatafile_id());
			}

			// sensor id
			parameters.add(null);
			// source id
			parameters.add(sourceAccumulator.getAccumulated());

			parameters.add(platformAccumulator.getAccumulated());

			final List<CommentFastMode> list = getDatabaseConnection().listAll(CommentFastMode.class, query,
					parameters);

			int total = 0;
			// Now we complete
			for (final CommentFastMode comment : list) {
				final Comment currentComment = new Comment();
				final Platform platform = new Platform();

				currentComment.setComment_id(comment.getComment_id());
				currentComment.setComment_type_id(comment.getComment_type_name());
				currentComment.setContent(comment.getContent());
				currentComment.setTime(comment.getTime());
				currentComment.setPlatform(platform);
				platform.setName(comment.getPlatform_name());

				currentComment.doImport(_bridge.getLayers(), isSplitByDatafile());
				++total;
			}
			return total;
		}

	}

	private int populateFastModeContacts(final ArrayList<Contact> selectedContacts)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, SQLException {
		if (selectedContacts.isEmpty()) {
			return 0;
		} else {
			final String query = OSUtils.readFile(ContactFastMode.class, getContactQuery(getAlgorithmType()));

			final List<Object> parameters = new ArrayList<>();

			// Let's add the time period filter
			final String[] periodFilter = getCurrentTimePeriodAsParameter();
			parameters.add(periodFilter[0]);
			parameters.add(periodFilter[1]);

			// Let's add the area filter
			parameters.add(getCurrentAreaAsParameter());

			// Let's create ids.
			final QueryParameterAccumulator sensorAccumulator = getParameterAccumulator();
			final QueryParameterAccumulator sourceAccumulator = getParameterAccumulator();
			final QueryParameterAccumulator platformAccumulator = getParameterAccumulator();

			for (final Contact contact : selectedContacts) {
				sensorAccumulator.addPart(contact.getSensor().getSensor_id());
				sourceAccumulator.addPart(contact.getDatafile().getDatafile_id());
				platformAccumulator.addPart(contact.getPlatform().getPlatform_id());
			}

			parameters.add(sensorAccumulator.getAccumulated());
			parameters.add(sourceAccumulator.getAccumulated());
			parameters.add(platformAccumulator.getAccumulated());

			final List<ContactFastMode> list = getDatabaseConnection().listAll(ContactFastMode.class, query,
					parameters);

			int total = 0;
			// Now we complete
			for (final ContactFastMode contact : list) {
				final Contact currentContact = new Contact();
				final Sensor sensor = new Sensor();
				final Platform platform = new Platform();
				final Datafile datafile = new Datafile();

				currentContact.setContact_id(contact.getContact_id());
				currentContact.setTime(contact.getTime());
				currentContact.setName(contact.getName());
				currentContact.setSensor(sensor);
				currentContact.setDatafile(datafile);

				platform.setName(contact.getPlatform_name());

				sensor.setName(contact.getSensor_name());
				sensor.setPlatform(platform);

				datafile.setReference(contact.getReference());

				currentContact.setBearing(contact.getBearing());
				currentContact.setLocation(contact.getLocation());

				currentContact.doImport(_bridge.getLayers(), isSplitByDatafile());
				++total;
			}
			return total;
		}
	}

	private int populateFastModeStates(final ArrayList<State> selectedStates,
			final HashMap<String, String> datafilesNames)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, SQLException {

		// we have't selected anything
		if (selectedStates.isEmpty()) {
			return 0;
		} else {
			final String query = OSUtils.readFile(ContactFastMode.class, getStateQuery(getAlgorithmType()));
			final List<Object> parameters = new ArrayList<>();

			// Let's add the time period filter
			final String[] periodFilter = getCurrentTimePeriodAsParameter();
			parameters.add(periodFilter[0]);
			parameters.add(periodFilter[1]);

			// Let's add the area filter
			parameters.add(getCurrentAreaAsParameter());

			// Let's create ids.
			final QueryParameterAccumulator sensorAccumulator = getParameterAccumulator();
			final QueryParameterAccumulator sourceAccumulator = getParameterAccumulator();
			final QueryParameterAccumulator platformAccumulator = getParameterAccumulator();

			for (final State state : selectedStates) {
				sensorAccumulator.addPart(state.getSensor().getSensor_id());
				sourceAccumulator.addPart(state.getDatafile().getDatafile_id());
				platformAccumulator.addPart(state.getPlatform().getPlatform_id());

				datafilesNames.put(state.getDatafile().getDatafile_id(), state.getDatafile().getReference());
			}

			parameters.add(sensorAccumulator.getAccumulated());
			parameters.add(sourceAccumulator.getAccumulated());
			parameters.add(platformAccumulator.getAccumulated());

			final List<StateFastMode> list = getDatabaseConnection().listAll(StateFastMode.class, query, parameters);

			int total = 0;
			// Now we complete
			for (final StateFastMode stateFastMode : list) {
				final State currentState = new State();
				final Sensor sensor = new Sensor();
				final Platform platform = new Platform();
				final SensorType sensorType = new SensorType();
				final Datafile datafile = new Datafile();

				if (stateFastMode.getReference() != null) {
					datafile.setReference(stateFastMode.getReference());
				} else {
					datafile.setReference(datafilesNames.get(stateFastMode.getSourceid()));
				}
				currentState.setSensor(sensor);
				sensor.setSensorType(sensorType);
				sensor.setPlatform(platform);
				currentState.setDatafile(datafile);

				currentState.setState_id(stateFastMode.getStateId());
				currentState.setTime(stateFastMode.getTime());

				currentState.getSensor().setName(stateFastMode.getSensorName());
				currentState.getPlatform().setName(stateFastMode.getPlatformName());
				currentState.setLocation(stateFastMode.getLocation());
				currentState.setCourse(stateFastMode.getCourse());
				currentState.setSpeed(stateFastMode.getSpeed());
				currentState.setHeading(stateFastMode.getHeading());
				currentState.doImport(_bridge.getLayers(), isSplitByDatafile());
				++total;
			}
			return total;
		}
	}

	private void populateSelectedDatafiles(final TreeNode treeModel, final ArrayList<State> selectedStates,
			final ArrayList<Comment> selectedComments, final ArrayList<Contact> selectedContacts) {
		if (treeModel.isChecked()) {
			for (final TreeStructurable item : treeModel.getItems()) {
				if (item instanceof State) {
					selectedStates.add((State) item);
				} else if (item instanceof Comment) {
					selectedComments.add((Comment) item);
				} else if (item instanceof Contact) {
					selectedContacts.add((Contact) item);
				}
			}
		}

		for (final TreeNode child : treeModel.getChildren()) {
			populateSelectedDatafiles(child, selectedStates, selectedComments, selectedContacts);
		}
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
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, HIGHLIGHT_PROPERTY, null, node);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void setPepysConnectorBridge(final PepysConnectorBridge _bridge) {
		this._bridge = _bridge;
	}

	@Override
	public void setSearch(final String _newSearch) {
		Application.logError2(ToolParent.INFO, "Setting search to " + _newSearch, null);
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
	public void setSplitByDataile(final boolean splitByDatafile) {
		this.splitByDafile = splitByDatafile;
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
		Application.logError2(ToolParent.INFO, "Updating Tree from the model calculated", null);
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
		Application.logError2(ToolParent.INFO, "Starting validate process - Model Configuration", null);
		if (!currentPeriod.isConsistent()) {
			throw new PepsysException("Date validation", "The Start date-time must be before the End date-time");
		}
	}
}
