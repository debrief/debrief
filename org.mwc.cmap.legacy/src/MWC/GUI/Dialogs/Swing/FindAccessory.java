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

package MWC.GUI.Dialogs.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * A threaded file search accessory for JFileChooser.
 * <P>
 * Presents JFileChooser users with a tabbed panel interface for specifying file
 * search criteria including (1) search by name, (2) search by date of
 * modification, and (3) search by file content. Finded are performed "in the
 * background" with found files displayed dynamically as they are found. Only
 * one search can be active at a time. FindResults are displayed in a scrolling
 * list within a results tab panel.
 * <P>
 * Findes are performed asynchronously so the user can continue browsing the
 * file system. The user may stop the search at any time. Accepting or
 * cancelling the file chooser or closing the dialog window will automatically
 * stop a search in progress.
 * <P>
 * The starting folder of the search (the search base) is displayed at the top
 * of the accessory panel. The search base dsiplay will not change while a
 * search is running. Thes search base display will change to reflect the
 * current directory of JFileChooser when a search is not running.
 * <P>
 * Changing the search options does not affect a search in progress.
 *
 * @author Ken Klinner, kklinner@opiom.com Editor: Klaus Berg: added hide border
 *         constructor
 * @version 1.1, 2000/06/26
 */
class FindAccessory extends JPanel implements Runnable, PropertyChangeListener, ActionListener, FindProgressCallback {
	/**
	 * Convenience class for adding action objects to the control panel.
	 */
	class FindAction extends AbstractAction {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Construct a search control action currently implements
		 * FindAccesory.ACTION_START and FindAccessory.ACTION_STOP.
		 *
		 * @param text command
		 * @param icon button icon
		 */
		FindAction(final String text, final Icon icon) {
			super(text, icon);
		}

		/**
		 * Invoke FindAction's action() method.
		 *
		 * @param e action event
		 */
		@Override
		public void actionPerformed(final ActionEvent e) {
			action(e.getActionCommand());
		}
	}

	/**
	 * Find controls panel displays default action components for starting and
	 * stopping a search. Also displays the search progress in the form of a text
	 * display indicating the number of items found and the total number of items
	 * encountered in the search.
	 */
	class FindControls extends JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		protected JLabel searchDirectory = null;
		protected JLabel progress = null;

		/**
		 * Construct a simple search control panel with buttons for starting and
		 * stopping a search and a simple display for search progress.
		 */
		FindControls(final FindAction find, final FindAction stop, final boolean recurse) {
			super();
			setLayout(new BorderLayout());

			final JToolBar tools = new JToolBar();
			tools.setFloatable(false);
			tools.add(actionStart = new FindAction(ACTION_START, null));
			tools.add(actionStop = new FindAction(ACTION_STOP, null));
			add(tools, BorderLayout.WEST);

			progress = new JLabel("", SwingConstants.RIGHT);

			// So that frequent updates will appear smooth
			progress.setDoubleBuffered(true);

			progress.setForeground(Color.black);
			progress.setFont(new Font("Helvetica", Font.PLAIN, 9));
			add(progress, BorderLayout.EAST);
		}

		/**
		 * Display search progress as a text field "no. of matches / total searched".
		 *
		 * @param matches1 number of items found
		 * @param total1   number of items investigated
		 */
		public void showProgress(final int matches1, final int total1) {
			if (progress == null)
				return;
			progress.setText(String.valueOf(matches1) + "/" + String.valueOf(total1));
		}

	}

	/**
	 * Displays the full path of the search starting folder.
	 */
	class FindFolder extends JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		protected JLabel searchDirectory = null;

		FindFolder() {
			super();
			setLayout(new BorderLayout());

			// Directory
			searchDirectory = new JLabel();
			searchDirectory.setForeground(Color.black);
			searchDirectory.setFont(new Font("Helvetica", Font.PLAIN, 9));
			add(searchDirectory);
		}

		/**
		 * Display the full path of the specified folder.
		 */
		public void setFindDirectory(final File f) {
			if (searchDirectory == null)
				return;
			if (f != null)
				searchDirectory.setText(f.getAbsolutePath());
			else
				searchDirectory.setText(null);
		}

	}

	/**
	 * Appears as a special pane within the FindOptions tabbed panel. The only one
	 * that does not generate a FindFilter.
	 */
	class FindResults extends JPanel {
		/**
		 * Convenience class for rendering cells in the results list.
		 */
		@SuppressWarnings("rawtypes")
		class FindResultsCellRenderer extends JLabel implements ListCellRenderer {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			FindResultsCellRenderer() {
				setOpaque(true);
			}

			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				int idx = index;
				if (idx == -1) {
					// This shouldn't happen since we won't be using this
					// renderer in a combo box
					final int selected = list.getSelectedIndex();
					if (selected == -1)
						return this;
					else
						idx = selected;
				}

				setBorder(new EmptyBorder(1, 2, 1, 2));
				setFont(new Font("Helvetica", Font.PLAIN, 10));

				// show absolute path of file
				final File file = (File) model.elementAt(idx);
				setText(file.getAbsolutePath());

				// selection characteristics
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(Color.white);
					setForeground(Color.black);
				}
				return this;
			}

		}

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("rawtypes")
		protected DefaultListModel model = null;

		@SuppressWarnings("rawtypes")
		protected JList fileList = null;

		/**
		 * Construct a search results pane with a scrollable list of files. When an item
		 * is double-clicked the FindAccessory controller will be instructed to select
		 * the file in the parent JFileChooser's item display.
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		FindResults() {
			super();
			setLayout(new BorderLayout());

			model = new DefaultListModel();
			fileList = new JList(model);
			fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fileList.setCellRenderer(new FindResultsCellRenderer());
			add(fileList, BorderLayout.CENTER);

			// Double click listener
			final MouseListener mouseListener = new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() == 2) {
						try {
							final int index = fileList.locationToIndex(e.getPoint());
							final File f = (File) model.elementAt(index);
							goTo(f);
						} catch (final Throwable err) {
							// no file under the mouse: nothing to go to
						}
					}
				}
			};
			fileList.addMouseListener(mouseListener);
		}

		/**
		 * Add a file to the results list.
		 *
		 * @param f file found
		 */
		@SuppressWarnings("unchecked")
		public void append(final File f) {
			if (f == null)
				return;
			model.addElement(f);
		}

		/**
		 * Clear all items from the results list.
		 */
		public void clear() {
			if (model != null) {
				model.removeAllElements();
				invalidate();
				repaint();
			}
		}
	}

	/**
	 * Contains a collecton of search options displayed as tabbed panes and at least
	 * one pane for displaying the search results. Each options tab pane is a user
	 * interface for sprecifying the search criteria and a factory for a FindFilter
	 * to implement the acceptance function. By making the search option pane
	 * responsible for generating a FindFilter object, the programmer can easily
	 * extend the search capabilities without modifying the controlling search
	 * engine.
	 */
	class FindTabs extends JTabbedPane {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		protected String TAB_NAME = "Name";
		protected String TAB_DATE = "Date";
		protected String TAB_CONTENT = "Content";
		protected String TAB_RESULTS = "Found";

		protected FindResults resultsPanel = null;
		protected JScrollPane resultsScroller = null;

		/**
		 * Construct a search tabbed pane with tab panels for seach by filename, search
		 * by date, search by content and search results.
		 */
		FindTabs() {
			super();

			setForeground(Color.black);
			setFont(new Font("Helvetica", Font.BOLD, 10));

			// Add search-by-name panel
			addTab(TAB_NAME, new FindByName());

			// Add search-by-date panel
			addTab(TAB_DATE, new FindByDate());

			// Add search-by-content panel
			addTab(TAB_CONTENT, new FindByContent());

			// Add results panel
			resultsScroller = new JScrollPane(resultsPanel = new FindResults());

			// so that updates will be smooth
			resultsPanel.setDoubleBuffered(true);
			resultsScroller.setDoubleBuffered(true);

			addTab(TAB_RESULTS, resultsScroller);
		}

		/**
		 * Adds the specified file to the results list.
		 *
		 * @param f file to add to results list
		 */
		public void addFoundFile(final File f) {
			if (resultsPanel != null)
				resultsPanel.append(f);
		}

		/**
		 * Prepares the panel for a new search by clearing the results list, bringing
		 * the results tab panel to the front and generating an array of search filters
		 * for each search options pane that implements the FindFilterFactory interface.
		 *
		 * @return array of FindFilters to be used by the controlling search engine
		 */
		public FindFilter[] newFind() {
			// Clear the results display
			if (resultsPanel != null)
				resultsPanel.clear();

			// Fix the width of the scrolling results panel so the layout
			// managers don't try to make it too wide for JFileChooser
			final Dimension dim = resultsScroller.getSize();
			resultsScroller.setMaximumSize(dim);
			resultsScroller.setPreferredSize(dim);

			// Return an array of FindFilters
			final Vector<FindFilter> filters = new Vector<FindFilter>();
			for (int i = 0; i < getTabCount(); i++) {
				try {
					final FindFilterFactory fac = (FindFilterFactory) getComponentAt(i);
					final FindFilter f = fac.createFindFilter();
					if (f != null)
						filters.addElement(f);
				} catch (final Throwable e) {
					// The FindResults pane does not implement FindFilterFactory
				}
			}
			if (filters.size() == 0)
				return null;
			final FindFilter[] filterArray = new FindFilter[filters.size()];
			for (int i = 0; i < filterArray.length; i++) {
				filterArray[i] = filters.elementAt(i);
			}
			return filterArray;
		}

		/**
		 * Bring the search results tab panel to the front.
		 */
		public void showFindResults() {
			if (resultsScroller != null)
				setSelectedComponent(resultsScroller);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Label for this accessory.
	 */
	static public final String ACCESSORY_NAME = " Find ";
	/**
	 * Default max number of found items. Prevents overloading results list.
	 */
	static public final int DEFAULT_MAX_SEARCH_HITS = 500;

	/**
	 * Find start action name
	 */
	static public final String ACTION_START = "Start";

	/**
	 * Find stop action name
	 */
	static public final String ACTION_STOP = "Stop";

	/**
	 * Parent JFileChooser component
	 */
	protected JFileChooser chooser = null;

	protected FindAction actionStart = null;

	protected FindAction actionStop = null;

	/**
	 * This version of FindAccesory supports only one active search thread
	 */
	protected Thread searchThread = null;

	/**
	 * Set to true to stop current search
	 */
	protected boolean killFind = false;

	/**
	 * Displays full path of search base
	 */
	protected FindFolder pathPanel = null;

	/**
	 * Find options with results list
	 */
	protected FindTabs searchTabs = null;

	/**
	 * Find controls with progress display
	 */
	protected FindControls controlPanel = null;

	/**
	 * Number of items inspected by current/last search
	 */
	protected int total = 0;

	/**
	 * Number of items found by current/last search
	 */
	protected int matches = 0;

	/**
	 * Max number of found items to prevent overloading the results list.
	 */
	protected int maxMatches = DEFAULT_MAX_SEARCH_HITS;

	/**
	 * Construct a search panel with start and stop actions and "attach" it to the
	 * specified JFileChooser component. Calls register() to establish FindAccessory
	 * as a PropertyChangeListener of JFileChooser.
	 *
	 * @param parent JFileChooser containing this accessory
	 */
	public FindAccessory(final JFileChooser parent) {
		this(ACCESSORY_NAME);
		chooser = parent;
		register(chooser);
	}

	/**
	 * Construct a search panel with start and stop actions and "attach" it to the
	 * specified JFileChooser component. Calls register() to establish FindAccessory
	 * as a PropertyChangeListener of JFileChooser. Sets maximum number of found
	 * items to limit the load in the results list.
	 *
	 * @param c   JFileChooser containing this accessory
	 * @param max Max number of items for results list. Find stops when max number
	 *            of items found.
	 */
	public FindAccessory(final JFileChooser c, final int max) {
		this(c);
		setMaxFindHits(max);
	}

	// Bg:
	/**
	 * Construct a search panel with start and stop actions and "attach" it to the
	 * specified JFileChooser component. Calls register() to establish FindAccessory
	 * as a PropertyChangeListener of JFileChooser. Specify a title for the border
	 * or just say no to a TitleBorder.
	 *
	 * @param parent         JFileChooser containing this accessory
	 * @param titleForBorder the string the should appear as the title in the
	 *                       accessory panel's TitleBorder or null if we should use
	 *                       a LineBorder(Color.black) instead
	 */
	public FindAccessory(final JFileChooser parent, final String titleForBorder) {
		this(titleForBorder);
		chooser = parent;
		register(chooser);
	}

	/**
	 * Construct a search panel with start and stop actions, option panes and a
	 * results list pane that can display up to DEFAULT_MAX_SEARCH_HITS items.
	 */
	public FindAccessory(final String titleForBorder) {
		super();

		if (titleForBorder != null) {
			setBorder(new TitledBorder(titleForBorder));
		} else {
			setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new LineBorder(Color.black)));
		}
		setLayout(new BorderLayout());

		actionStart = new FindAction(ACTION_START, null);
		actionStop = new FindAction(ACTION_STOP, null);

		add(pathPanel = new FindFolder(), BorderLayout.NORTH);
		add(searchTabs = new FindTabs(), BorderLayout.CENTER);
		add(controlPanel = new FindControls(actionStart, actionStop, true), BorderLayout.SOUTH);

		updateFindDirectory();
	}

	/**
	 * @param file    file to pass to each filter's accept method
	 * @param filters array of selection criteria
	 * @return true if specified file matches each filter's selection criteria
	 */
	protected boolean accept(final File file, final FindFilter[] filters) {
		if (file == null)
			return false;
		if (filters == null)
			return false;

		for (int i = 0; i < filters.length; i++) {
			if (!filters[i].accept(file, this))
				return false;
		}
		return true;
	}

	/**
	 * Invoked by FindAction objects to start and stop searches.
	 */
	public void action(final String command) {
		if (command == null)
			return;
		if (command.equals(ACTION_START))
			start();
		else if (command.equals(ACTION_STOP))
			stop();
	}

	/**
	 * Called by JFileChooser when the user provokes an action like "cancel" or
	 * "open". Listens for APPROVE_SELECTION and CANCEL_SELECTION action and stops
	 * the current search, if there is one.
	 *
	 * @param e ActionEvent from parent JFileChooser.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if (command == null)
			return; // Can this happen? Probably not. Call me paranoid.
		if (command.equals(JFileChooser.APPROVE_SELECTION))
			quit();
		else if (command.equals(JFileChooser.CANCEL_SELECTION))
			quit();
	}

	/**
	 * Returns maximum capacity of results list.
	 *
	 * @return Max capacity of results list.
	 */
	public int getMaxFindHits() {
		return maxMatches;
	}

	/**
	 * Set parent's current directory to the parent folder of the specified file and
	 * select the specified file. This method is invoked when the user double clicks
	 * on an item in the results list.
	 *
	 * @param f File to select in parent JFileChooser
	 */
	public void goTo(final File f) {
		if (f == null)
			return;
		if (!f.exists())
			return;
		if (chooser == null)
			return;

		// Make sure that files and directories can be displayed
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// Make sure that parent file chooser will show the type of file
		// specified
		final javax.swing.filechooser.FileFilter filter = chooser.getFileFilter();
		if (filter != null) {
			if (!filter.accept(f)) {
				// The current filter will not display the specified file.
				// Set the file filter to the built-in accept-all filter (*.*)
				final javax.swing.filechooser.FileFilter all = chooser.getAcceptAllFileFilter();
				chooser.setFileFilter(all);
			}
		}

		// Tell parent file chooser to display contents of parentFolder.
		// Prior to Java 1.2.2 setSelectedFile() did not set the current
		// directory the folder containing the file to be selected.
		final File parentFolder = f.getParentFile();
		if (parentFolder != null)
			chooser.setCurrentDirectory(parentFolder);

		// Nullify the current selection, if any.
		// Why is this necessary?
		// Emperical evidence suggests that JFileChooser gets "sticky" (i.e. it
		// does not always relinquish the current selection). Nullifying the
		// current selection seems to yield better results.
		chooser.setSelectedFile(null);

		// Select the file
		chooser.setSelectedFile(f);

		// Refresh file chooser display.
		// Is this really necessary? Testing on a variety of systems with
		// Java 1.2.2 suggests that this helps. Sometimes it doesn't work,
		// but it doesn't do any harm.
		chooser.invalidate();
		chooser.repaint();
	}

	/**
	 * @return true if a search is currently running
	 */
	public boolean isRunning() {
		if (searchThread == null)
			return false;
		return searchThread.isAlive();
	}

	/**
	 * Begins a new search by resetting the <b>total</b> and <b>matches</b> progress
	 * variables and retrieves the search filter array from the options panel. Each
	 * tab in the options panel is responsible for generating a FindFilter based on
	 * its current settings.
	 *
	 * @return Array of search filters from the options panel.
	 */
	protected FindFilter[] newFind() {

		total = matches = 0;
		updateProgress();

		if (searchTabs != null)
			return searchTabs.newFind();
		return null;
	}

	/**
	 * Called by JFileChooser when a property changes. FindAccessory listens for
	 * DIRECTORY_CHANGED_PROPERTY and updates the path component to display the full
	 * path of the current JFileChooser directory. When a search is in progress the
	 * path component is <b>not</b> updated - the path component will display the
	 * starting point of the current search.
	 *
	 * @param e PropertyChangeEvent from parent JFileChooser.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		final String prop = e.getPropertyName();
		if (prop.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
			updateFindDirectory();
		}
	}

	/**
	 * Stop the current search and unregister in preparation for parent shutdown.
	 */
	public void quit() {
		stop();
		unregister(chooser);
	}

	/**
	 * Add this component to the specified JFileChooser's list of property change
	 * listeners and action listeners.
	 *
	 * @param c parent JFileChooser
	 */
	protected void register(final JFileChooser c) {
		if (c == null)
			return;
		c.addPropertyChangeListener(this);
		c.addActionListener(this);
	}

	/**
	 * Called by FindFilter to report progress of a search. Purely a voluntary
	 * report. This really should be implemented as a property change listener.
	 * Percentage completion = (current/total)*100.
	 *
	 * @param filter  FindFilter reporting progress
	 * @param file    file being searched
	 * @param current current "location" of search
	 * @param total1  expected maximum value of current
	 * @return true to continue search, false to abort
	 */
	@Override
	public boolean reportProgress(final FindFilter filter, final File file, final long current, final long total1) {
		return !killFind;
	}

	/**
	 * Find thread
	 */
	@Override
	public void run() {
		if (searchThread == null)
			return;
		if (Thread.currentThread() != searchThread)
			return;
		try {
			actionStart.setEnabled(false);
			actionStop.setEnabled(true);
			runFind(chooser.getCurrentDirectory(), newFind());
		} catch (final InterruptedException e) {
			// interrupted: carry on
		} finally {
			actionStart.setEnabled(true);
			actionStop.setEnabled(false);
			searchThread = null;
		}
	}

	/**
	 * Recursive search beginning at folder <b>base</b> for files and folders
	 * matching each filter in the <b>filters</b> array. To interrupt set
	 * <b>killFind</b> to true. Also stops when number of search hits (matches)
	 * equals <b>maxMatches</b>.
	 * <P>
	 * <b>Note:</b> Convert this to a nonrecursive search algorithm on systems where
	 * stack space might be limited and/or the search hierarchy might be very deep.
	 *
	 * @param base    starting folder of search
	 * @param filters matches must pass each filters in array
	 * @throws InterruptedException if thread is interrupted
	 */
	protected void runFind(final File base, final FindFilter[] filters) throws InterruptedException {
		if (base == null)
			return;
		if (!base.exists())
			return; // Not likely to happen
		if (filters == null)
			return;

		if (killFind)
			return;
		File folder = null;
		if (base.isDirectory())
			folder = base;
		else
			folder = base.getParentFile();

		final File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			total++;
			if (accept(files[i], filters)) {
				matches++;
				searchTabs.addFoundFile(files[i]);
			}
			updateProgress();
			if (killFind)
				return;
			Thread.sleep(0);

			if (files[i].isDirectory())
				runFind(files[i], filters);
			if ((maxMatches > 0) && (matches >= maxMatches)) {
				return;// stopgap measure so that we don't overload
			}
		}
	}

	/**
	 * Sets maximum capacity of the results list. Find stops when max number of
	 * items found.
	 *
	 * @param max Max capacity of results list.
	 */
	public void setMaxFindHits(final int max) {
		maxMatches = max;
	}

	/**
	 * Start a search. The path display will show the starting folder of the search.
	 * Finds are recursive and will span the entire folder hierarchy below the base
	 * folder. The user may continue to browse with JFileChooser.
	 */
	public synchronized void start() {
		if (searchTabs != null)
			searchTabs.showFindResults();
		updateFindDirectory();
		killFind = false;
		if (searchThread == null) {
			searchThread = new Thread(this);
		}
		if (searchThread != null)
			searchThread.start();
	}

	/**
	 * Stop the active search.
	 */
	public synchronized void stop() {
		killFind = true;
	}

	/**
	 * Remove this component from the specified JFileChooser's list of property
	 * change listeners and action listeners.
	 *
	 * @param c parent JFileChooser
	 */
	protected void unregister(final JFileChooser c) {
		if (c == null)
			return;
		c.removeActionListener(this);
		c.removePropertyChangeListener(this);
	}

	/**
	 * Displays the absolute path to the parent's current directory if and only if
	 * there is no active search.
	 */
	public void updateFindDirectory() {
		if (isRunning())
			return;
		if (chooser == null)
			return;
		if (pathPanel == null)
			return;
		final File f = chooser.getCurrentDirectory();
		pathPanel.setFindDirectory(f);
	}

	/**
	 * Display progress of running search.
	 */
	protected void updateProgress() {
		controlPanel.showProgress(matches, total);
	}

}
