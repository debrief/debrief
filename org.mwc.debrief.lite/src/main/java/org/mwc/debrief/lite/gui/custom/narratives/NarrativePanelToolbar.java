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
package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.mwc.debrief.lite.gui.LiteStepControl;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

public class NarrativePanelToolbar extends JPanel {

	/**
	 * Generated Serial Version ID.
	 */
	private static final long serialVersionUID = 349058868680231476L;

	public static final String ACTIVE_STATE = "ACTIVE";

	public static final String INACTIVE_STATE = "INACTIVE";

	public static final String STATE_PROPERTY = "STATE";

	public static final String NARRATIVES_PROPERTY = "NARRATIVES";

	public static final String NARRATIVES_REMOVE_COMPLETE_LAYER = "REMOVE_LAYER";

	private String _state = INACTIVE_STATE;

	private final LiteStepControl _stepControl;

	private final List<JComponent> componentsToDisable = new ArrayList<>();

	/**
	 * Maybe this should be inside the abstract model.
	 */
	private final DefaultTableModel _narrativeListModel = new DefaultTableModel() {

		/**
		 *
		 */
		private static final long serialVersionUID = -3080607575902259924L;

		@Override
		public boolean isCellEditable(final int row, final int column) {
			return false;
		}

	};

	/**
	 * This should go inside the model too.
	 */
	private final TableRowSorter<DefaultTableModel> _narrativeListSorter = new TableRowSorter<DefaultTableModel>(
			_narrativeListModel);

	private final JTable _narrativeList = new JTable();

	private final AbstractNarrativeConfiguration _model;

	private final PropertyChangeListener enableDisableButtonsListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			if (STATE_PROPERTY.equals(event.getPropertyName())) {
				final boolean isActive = ACTIVE_STATE.equals(event.getNewValue());
				for (final JComponent component : componentsToDisable) {
					component.setEnabled(isActive);
				}
			}
		}
	};

	private final PropertyChangeListener updatingNarrativesListener = new PropertyChangeListener() {

		public void calculateDifferences(final NarrativeWrapper narrativeWrapper, final Set<NarrativeEntry> toRemove,
				final Set<NarrativeEntry> toAdd) {
			if (_model.getRegisteredNarrativeWrapper().contains(narrativeWrapper)) {
				final Set<NarrativeEntry> newEntries = new TreeSet<>();
				final Enumeration<Editable> items = narrativeWrapper.elements();
				while (items.hasMoreElements()) {
					final Editable thisE = items.nextElement();
					if (((NarrativeEntry) thisE).getVisible())
						newEntries.add((NarrativeEntry) thisE);
				}
				for (final NarrativeEntry currentEntry : _model.getCurrentNarrativeEntries(narrativeWrapper)) {
					if (!newEntries.contains(currentEntry)) {
						toRemove.add(currentEntry);
					}
				}
				for (final NarrativeEntry newEntry : newEntries) {
					if (!_model.getCurrentNarrativeEntries(narrativeWrapper).contains(newEntry)) {
						toAdd.add(newEntry);
					}
				}
			} else {
				_model.addNarrativeWrapper(narrativeWrapper);
				final Enumeration<Editable> items = narrativeWrapper.elements();
				while (items.hasMoreElements()) {
					final Editable thisE = items.nextElement();
					final NarrativeEntry newEntry = (NarrativeEntry) thisE;
					if (newEntry.getVisible()) {
						toAdd.add((NarrativeEntry) thisE);
					}
				}

			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (NARRATIVES_PROPERTY.equals(evt.getPropertyName())
					|| NarrativeEntry.VISIBILITY_CHANGE.equals(evt.getPropertyName())) {
				updateNarratives(evt.getSource());
			} else if (NARRATIVES_REMOVE_COMPLETE_LAYER.equals(evt.getPropertyName())) {
				removeCompleteNarrativeLayer((NarrativeWrapper) evt.getNewValue());
			}

			if (_narrativeListModel.getRowCount() > 0) {
				setState(ACTIVE_STATE);
			} else {
				setState(INACTIVE_STATE);
			}
		}

		public void removeCompleteNarrativeLayer(final NarrativeWrapper wrapperRemoved) {
			final Enumeration<Editable> iteratorToRemove = wrapperRemoved.elements();
			while (iteratorToRemove.hasMoreElements()) {
				final Editable thisE = iteratorToRemove.nextElement();
				for (int i = 0; i < _narrativeListModel.getRowCount(); i++) {
					final NarrativeEntryItem currentItem = (NarrativeEntryItem) _narrativeListModel.getValueAt(i, 0);
					if (currentItem.getEntry().equals(thisE)) {
						_narrativeListModel.removeRow(i);
						break;
					}
				}
			}
			_model.removeNarrativeWrapper(wrapperRemoved);
		}

		public void updateNarrativeEntry(final Object layerChanged) {
			final NarrativeEntry entry = (NarrativeEntry) layerChanged;
			if (entry.getVisible()
					&& (entry.getNarrativeWrapper() == null || entry.getNarrativeWrapper().getVisible())) {
				// We are adding a new narraty entry.
				final NarrativeEntryItem entryItem = new NarrativeEntryItem(entry, _model);

				_narrativeListModel.addRow(new NarrativeEntryItem[] { entryItem });
				if (entry.getNarrativeWrapper() != null) {
					_model.registerNewNarrativeEntry(entry.getNarrativeWrapper(), entry);
				}
				_narrativeListSorter.sort();
			} else {
				if (entry.getNarrativeWrapper() != null) {
					_model.unregisterNarrativeEntry(entry.getNarrativeWrapper(), entry);
				}
				for (int i = 0; i < _narrativeListModel.getRowCount(); i++) {
					final NarrativeEntryItem currentItem = (NarrativeEntryItem) _narrativeListModel.getValueAt(i, 0);
					if (currentItem.getEntry().equals(entry)) {
						_narrativeListModel.removeRow(i);
						break;
					}
				}
			}
		}

		public void updateNarratives(final Object layerChanged) {
			if (layerChanged instanceof NarrativeWrapper) {
				updateNarrativeWrapper(layerChanged);
			} else if (layerChanged instanceof NarrativeEntry) {
				updateNarrativeEntry(layerChanged);
			}
		}

		public void updateNarrativeWrapper(final Object layerChanged) {
			final NarrativeWrapper narrativeWrapper = (NarrativeWrapper) layerChanged;

			final Set<NarrativeEntry> toRemove = new TreeSet<>();
			final Set<NarrativeEntry> toAdd = new TreeSet<>();
			// Check difference
			calculateDifferences(narrativeWrapper, toRemove, toAdd);

			for (final NarrativeEntry entry : toAdd) {
				final NarrativeEntryItem entryItem = new NarrativeEntryItem(entry, _model);
				_narrativeListModel.addRow(new NarrativeEntryItem[] { entryItem });
				_model.registerNewNarrativeEntry(narrativeWrapper, entry);

			}
			for (final NarrativeEntry entry : toRemove) {
				for (int i = 0; i < _narrativeListModel.getRowCount(); i++) {
					final NarrativeEntryItem currentItem = (NarrativeEntryItem) _narrativeListModel.getValueAt(i, 0);
					if (currentItem.getEntry().equals(entry)) {
						_narrativeListModel.removeRow(i);
						break;
					}
				}
			}
			// Sort it.

			_narrativeListSorter.sort();
		}
	};

	private final ArrayList<PropertyChangeListener> stateListeners;

	public NarrativePanelToolbar(final LiteStepControl stepControl, final AbstractNarrativeConfiguration model) {
		super(new FlowLayout(FlowLayout.LEFT));

		this._narrativeList.setModel(_narrativeListModel);

		this._narrativeListModel.addColumn("");
		final TableColumn column = this._narrativeList.getColumnModel().getColumn(0);
		final NarrativeEntryItemRenderer narrativeEntryItemRenderer = new NarrativeEntryItemRenderer(model);
		column.setCellRenderer(narrativeEntryItemRenderer);
		this._narrativeList.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				model.setPanelWidth(_narrativeList.getWidth());
			}

		});

		this._narrativeList.setRowSorter(_narrativeListSorter);

		/**
		 * Initialize the sorter
		 */
		final List<RowSorter.SortKey> sortKeys = new ArrayList<>();

		final int columnIndexToSort = 0;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));
		this._narrativeListSorter.setComparator(columnIndexToSort, new Comparator<NarrativeEntryItem>() {

			@Override
			public int compare(final NarrativeEntryItem o1, final NarrativeEntryItem o2) {
				return o1.compareTo(o2);
			}
		});
		this._narrativeListSorter.setSortKeys(sortKeys);

		this._stepControl = stepControl;
		this._model = model;
		init();

		stateListeners = new ArrayList<>(Arrays.asList(enableDisableButtonsListener, updatingNarrativesListener));

		model.setRepaintMethod(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				_narrativeList.repaint();
				return null;
			}
		});

		this._model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (NarrativeConfigurationModel.NARRATIVE_HIGHLIGHT.equals(evt.getPropertyName())) {
					final int currentlySelectedItemIndex = _narrativeList.getSelectedRow();
					final NarrativeEntryItem itemToCompare = new NarrativeEntryItem((NarrativeEntry) evt.getNewValue(),
							_model);
					for (int i = 0; i < _narrativeList.getRowCount(); i++) {
						if (currentlySelectedItemIndex != i) {

							final int finalCtr = i;
							final NarrativeEntry actualEntry = ((NarrativeEntryItem) (_narrativeList.getValueAt(i, 0)))
									.getEntry();
							if (actualEntry.equals(itemToCompare.getEntry())) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										_narrativeList.getSelectionModel().setSelectionInterval(finalCtr, finalCtr);
										_narrativeList.scrollRectToVisible(
												new Rectangle(_narrativeList.getCellRect(finalCtr, 0, true)));
									}
								});
								break;
							}
						}
					}
					// _model.repaintView();
				}
			}
		});

		this._narrativeList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final int selectedRow = _narrativeList.getSelectedRow();
				final NarrativeEntryItem entryItem = (NarrativeEntryItem) _narrativeList.getValueAt(selectedRow, 0);
				_stepControl.changeTime(entryItem.getEntry().getDTG());
				_narrativeList.repaint();
			}
		});
		setState(INACTIVE_STATE);
	}

	protected void checkNewNarratives(final Layers layers) {
		final Enumeration<Editable> elem = layers.elements();
		final Set<NarrativeWrapper> loadedNarratives = new TreeSet<>();
		while (elem.hasMoreElements()) {
			final Editable nextItem = elem.nextElement();
			if (nextItem instanceof NarrativeWrapper && ((NarrativeWrapper) nextItem).getVisible()) {
				final NarrativeWrapper newNarrative = (NarrativeWrapper) nextItem;
				loadedNarratives.add(newNarrative);
				if (!_model.getRegisteredNarrativeWrapper().contains(nextItem)) {
					_model.addNarrativeWrapper(newNarrative);
					newNarrative.setNarrativeViewerListener(updatingNarrativesListener);
					newNarrative.getSupport().addPropertyChangeListener(NARRATIVES_PROPERTY,
							updatingNarrativesListener);
					// newNarrative.getInfo().fireChanged(nextItem, NARRATIVES_PROPERTY, null,
					// nextItem);
					newNarrative.getSupport().firePropertyChange(NARRATIVES_PROPERTY, null, nextItem);
				}
			}
		}

		for (final NarrativeWrapper narrativeWrappersInPanel : _model.getRegisteredNarrativeWrapper()) {
			// Some items has been removed.
			if (!loadedNarratives.contains(narrativeWrappersInPanel)) {
				notifyListenersStateChanged(narrativeWrappersInPanel, NARRATIVES_REMOVE_COMPLETE_LAYER, null,
						narrativeWrappersInPanel);
			}
		}
		_narrativeListSorter.sort();
	}

	/*
	 * private JButton createCommandButton(final String command, final String image)
	 * { final ImageIcon icon = Utils.getIcon(image); final JButton button = new
	 * JButton(icon); button.setToolTipText(command); return button; }
	 */

	private void createDataListeners() {
		if (_stepControl != null && _stepControl.getLayers() != null) {
			final DataListener registerNarrativeListener = new DataListener() {

				@Override
				public void dataExtended(final Layers theData) {
					checkNewNarratives(theData);
				}

				@Override
				public void dataModified(final Layers theData, final Layer changedLayer) {
					checkNewNarratives(theData);
					// notifyListenersStateChanged(changedLayer, NARRATIVES_PROPERTY, null,
					// changedLayer);
				}

				@Override
				public void dataReformatted(final Layers theData, final Layer changedLayer) {
					checkNewNarratives(theData);
					// notifyListenersStateChanged(changedLayer, NARRATIVES_PROPERTY, null,
					// changedLayer);
				}
			};
			_stepControl.getLayers().addDataExtendedListener(registerNarrativeListener);
			_stepControl.getLayers().addDataModifiedListener(registerNarrativeListener);
			_stepControl.getLayers().addDataReformattedListener(registerNarrativeListener);
		}
	}

	private JToggleButton createJToggleButton(final String command, final String image) {
		final ImageIcon icon = Utils.getIcon(image);
		final JToggleButton button = new JToggleButton(icon);
		button.setToolTipText(command);
		return button;
	}

	private JComboBox<String> createTracksComboFilter(final JSelectTrackFilter selectTrack) {
		final JComboBox<String> tracksFilterLabel = new JComboBox<>(new String[] { "Sources" });
		tracksFilterLabel.setEnabled(true);
		tracksFilterLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (tracksFilterLabel.isEnabled()) {
					// Get the event source
					final Component component = (Component) e.getSource();

					selectTrack.show(component, 0, 0);

					// Get the location of the point 'on the screen'
					final Point p = component.getLocationOnScreen();

					selectTrack.setLocation(p.x, p.y + component.getHeight());
				}
			}
		});
		return tracksFilterLabel;
	}

	private JComboBox<String> createTypeFilterCombo(final JSelectTrackFilter selectTrack,
			final JSelectTypeFilter typeFilter) {
		final JComboBox<String> typeFilterLabel = new JComboBox<>(new String[] { "Types" });
		typeFilterLabel.setEnabled(true);
		typeFilterLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (typeFilterLabel.isEnabled()) {
					// Get the event source
					final Component component = (Component) e.getSource();

					typeFilter.show(component, 0, 0);

					// Get the location of the point 'on the screen'
					final Point p = component.getLocationOnScreen();

					selectTrack.setLocation(p.x, p.y + component.getHeight());
				}
			}
		});
		return typeFilterLabel;
	}

	private JToggleButton createWrapButton() {
		final JToggleButton wrapTextButton = createJToggleButton("Wrap Text", "icons/16/wrap.png");
		wrapTextButton.setSelected(true);
		wrapTextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				_model.setWrapping(wrapTextButton.isSelected());
				_narrativeList.invalidate();
				_narrativeList.repaint();
			}
		});
		return wrapTextButton;
	}

	public JTable getNarrativeList() {
		return _narrativeList;
	}

	private void init() {
		final JSelectTrackFilter selectTrack = new JSelectTrackFilter(_model);

		final JComboBox<String> tracksFilterLabel = createTracksComboFilter(selectTrack);

		final JSelectTypeFilter typeFilter = new JSelectTypeFilter(_model);
		final JComboBox<String> typeFilterLabel = createTypeFilterCombo(selectTrack, typeFilter);

		final JToggleButton wrapTextButton = createWrapButton();

		/*
		 * final JButton copyButton = createCommandButton("Copy Selected Entrey",
		 * "icons/16/copy_to_clipboard.png"); copyButton.addActionListener(new
		 * ActionListener() {
		 *
		 * @Override public void actionPerformed(final ActionEvent e) {
		 * System.out.println("Copy selected entry not implemented"); } });
		 *
		 * final JButton addBulkEntriesButton = createCommandButton("Add Bulk Entries",
		 * "icons/16/list.png"); addBulkEntriesButton.addActionListener(new
		 * ActionListener() {
		 *
		 * @Override public void actionPerformed(final ActionEvent e) {
		 * System.out.println("Add Bulk Entries not implemented"); } });
		 *
		 * final JButton addSingleEntryButton = createCommandButton("Add Single Entry",
		 * "icons/16/add.png"); addBulkEntriesButton.addActionListener(new
		 * ActionListener() {
		 *
		 * @Override public void actionPerformed(final ActionEvent e) {
		 * System.out.println("Add single entry not implemented"); } });
		 */

		/*
		 * add(tracksFilterLabel); add(typeFilterLabel);
		 */
		add(wrapTextButton);
		/*
		 * add(copyButton); add(addBulkEntriesButton); add(addSingleEntryButton);
		 */

		componentsToDisable.addAll(Arrays.asList(new JComponent[] { tracksFilterLabel, typeFilterLabel,
				wrapTextButton/*
								 * , copyButton, addBulkEntriesButton, addSingleEntryButton
								 */ }));

		createDataListeners();
	}

	private void notifyListenersStateChanged(final Object source, final String property, final Object oldValue,
			final Object newValue) {
		for (final PropertyChangeListener event : stateListeners) {
			event.propertyChange(new PropertyChangeEvent(source, property, oldValue, newValue));
		}
	}

	public void setState(final String newState) {
		final String oldState = _state;
		this._state = newState;

		if (newState != null && !newState.equals(oldState)) {
			notifyListenersStateChanged(this, STATE_PROPERTY, oldState, newState);
		}
	}

}
