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

package org.mwc.debrief.lite.gui.custom.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.AbstractSelection;
import org.mwc.debrief.lite.gui.custom.SimpleEditablePropertyPanel;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3;
import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Tools.Tote.Calculations.atbCalc;
import Debrief.Tools.Tote.Calculations.bearingCalc;
import Debrief.Tools.Tote.Calculations.bearingRateCalc;
import Debrief.Tools.Tote.Calculations.courseCalc;
import Debrief.Tools.Tote.Calculations.depthCalc;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Tools.Tote.Calculations.relBearingCalc;
import Debrief.Tools.Tote.Calculations.speedCalc;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.JFreeChart.BearingRateFormatter;
import MWC.GUI.JFreeChart.CourseFormatter;
import MWC.GUI.JFreeChart.DepthFormatter;
import MWC.GUI.JFreeChart.NewFormattedJFreeChart;
import MWC.GUI.JFreeChart.RelBearingFormatter;
import MWC.GUI.JFreeChart.formattingOperation;
import MWC.GenericData.WatchableList;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import MWC.Utilities.XYPlot.XYPlotUtilities;

public class GraphPanelToolbar extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 8529947841065977007L;

	public static final String ACTIVE_STATE = "ACTIVE";

	public static final String INACTIVE_STATE = "INACTIVE";

	public static final String STATE_PROPERTY = "STATE";

	public static final String TRACKS_PROPERTY = "TRACKS";

	private ShowTimeVariablePlot3 _xytool;

	private final LiteStepControl _stepControl;

	private final List<JComponent> componentsToDisable = new ArrayList<>();

	private AbstractTrackConfiguration selectTrackModel;

	private String _state = INACTIVE_STATE;

	private final SimpleEditablePropertyPanel _xyPanel;

	private static final String SHOW_CROSSHAIR = "Show the crosshair on the graph";

	private static final String HIDE_CROSSHAIR = "Hide the crosshair from the graph (for printing)";

	private final String symbolOn = "icons/16/symbol_on.png";

	private final String symbolOff = "icons/16/symbol_off.png";

	private final JToggleButton showSymbolsButton;

	private final JToggleButton hideCrosshair;

	/**
	 * label showing the value at the crosshair
	 */
	private final XYTextAnnotation _crosshairValueText;

	private final DefaultComboBoxModel<CalculationHolder> operationComboModel = new DefaultComboBoxModel<CalculationHolder>();

	private final formattingOperation theFormatter = relBearingCalc.useUKFormat() ? new RelBearingFormatter() : null;

	private final CalculationHolder[] operations = new CalculationHolder[] {
			new CalculationHolder(new depthCalc(), new DepthFormatter(), false, 0),
			new CalculationHolder(new courseCalc(), new CourseFormatter(), false, 360),
			new CalculationHolder(new speedCalc(), null, false, 0),
			new CalculationHolder(new rangeCalc(), null, true, 0),
			new CalculationHolder(new bearingCalc(), null, true, 360),
			new CalculationHolder(new bearingRateCalc(), new BearingRateFormatter(), true, 180),
			new CalculationHolder(new relBearingCalc(), theFormatter, true, 180),
			new CalculationHolder(new atbCalc(), theFormatter, true, 180) };

	public final PropertyChangeListener enableDisableButtonsListener = new PropertyChangeListener() {

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

	public final PropertyChangeListener tracksChangedListeners = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (TRACKS_PROPERTY.equals(evt.getPropertyName())) {
				if (selectTrackModel.getTracks().isEmpty()) {
					operationComboModel.removeAllElements();
					setState(INACTIVE_STATE);
				} else {
					/**
					 * We are dealing with track list modifications. We need to show here only the
					 * options that make sense, deactivating the relative menu when we only have 1
					 * track.
					 */
					final boolean isRelative = selectTrackModel.isRelativeEnabled();
					for (final CalculationHolder operation : operations) {
						if (!isRelative && !operation.isARelativeCalculation()
								&& operationComboModel.getIndexOf(operation) < 0) {
							operationComboModel.addElement(operation);
						} else if (isRelative && operationComboModel.getIndexOf(operation) < 0) {
							operationComboModel.addElement(operation);
						} else if (!isRelative && operation.isARelativeCalculation()
								&& operationComboModel.getIndexOf(operation) >= 0) {
							operationComboModel.removeElement(operation);
						}
					}
				}
			}

		}
	};

	private final ArrayList<PropertyChangeListener> stateListeners;

	public GraphPanelToolbar(final LiteStepControl stepControl, final SimpleEditablePropertyPanel xyPanel) {
		super(new FlowLayout(FlowLayout.LEFT));
		_stepControl = stepControl;
		_xyPanel = xyPanel;

		showSymbolsButton = createJToggleButton("Show Symbols", symbolOff);
		hideCrosshair = createJToggleButton(SHOW_CROSSHAIR, "icons/16/fix.png");
		_crosshairValueText = new XYTextAnnotation(" ", 0, 0);
		_crosshairValueText.setTextAnchor(TextAnchor.TOP_LEFT);
		_crosshairValueText.setFont(Defaults.getFont());
		_crosshairValueText.setPaint(Color.black);
		_crosshairValueText.setBackgroundPaint(Color.white);
		init();

		stateListeners = new ArrayList<>(Arrays.asList(enableDisableButtonsListener, tracksChangedListeners));

		setState(INACTIVE_STATE);
	}

	/**
	 * show/hide the crosshair (and its value label) on the current chart, to match
	 * the toggle button
	 */
	private void applyCrosshair() {
		final boolean show = hideCrosshair.isSelected();
		hideCrosshair.setToolTipText(show ? HIDE_CROSSHAIR : SHOW_CROSSHAIR);

		if (_xytool == null || _xytool.getGeneratedJFreeChart() == null) {
			return;
		}

		final XYPlot plot = _xytool.getGeneratedJFreeChart().getXYPlot();
		plot.removeAnnotation(_crosshairValueText);
		if (show) {
			plot.setDomainCrosshairPaint(Color.GRAY);
			plot.setRangeCrosshairPaint(Color.GRAY);
			plot.setDomainCrosshairStroke(new BasicStroke(2));
			plot.setRangeCrosshairStroke(new BasicStroke(2));

			// the crosshair starts at the origin, which is rarely on the graph. Put it
			// somewhere the user can see it, they can click on the graph to move it
			if (!plot.getDomainAxis().getRange().contains(plot.getDomainCrosshairValue())) {
				plot.setDomainCrosshairValue(plot.getDomainAxis().getRange().getCentralValue(), false);
			}
			if (!plot.getRangeAxis().getRange().contains(plot.getRangeCrosshairValue())) {
				plot.setRangeCrosshairValue(plot.getRangeAxis().getRange().getCentralValue(), false);
			}
			updateCrosshairLabel(plot);
			plot.addAnnotation(_crosshairValueText);
		}
		plot.setDomainCrosshairVisible(show);
		plot.setRangeCrosshairVisible(show);
	}

	/**
	 * keep the crosshair label in the top-left corner, showing the crosshair value
	 *
	 * @return whether anything changed
	 */
	private boolean updateCrosshairLabel(final XYPlot plot) {
		boolean changed = false;
		// (some axes are inverted, depth for instance)
		final ValueAxis xAxis = plot.getDomainAxis();
		final ValueAxis yAxis = plot.getRangeAxis();
		final double x = xAxis.isInverted() ? xAxis.getUpperBound() : xAxis.getLowerBound();
		final double y = yAxis.isInverted() ? yAxis.getLowerBound() : yAxis.getUpperBound();
		if (_crosshairValueText.getX() != x) {
			_crosshairValueText.setX(x);
			changed = true;
		}
		if (_crosshairValueText.getY() != y) {
			_crosshairValueText.setY(y);
			changed = true;
		}

		final String value = MWC.Utilities.TextFormatting.GeneralFormat
				.formatOneDecimalPlace(plot.getRangeCrosshairValue());
		final String time = new GMTDateFormat("HHmm:ss").format(new Date((long) plot.getDomainCrosshairValue()));
		final String message = " [" + time + "," + value + "]";
		if (!message.equals(_crosshairValueText.getText())) {
			_crosshairValueText.setText(message);
			changed = true;
		}
		return changed;
	}

	/**
	 * It extracts the TrackWrapper objects, then it tries to assign it. If it
	 * contains the same values, it is not assigned and returns false. True when it
	 * is assigned.
	 *
	 * @param layers Layers of the session. We are extracting Tracks from it.
	 * @return true if it was actually assigned. If they are the same, they are not
	 *         assigned.
	 */
	private boolean assignTracks(final Layers layers) {
		final Enumeration<Editable> elem = layers.elements();
		final List<TrackWrapper> tracks = new ArrayList<>();
		while (elem.hasMoreElements()) {
			final Editable nextItem = elem.nextElement();
			if (nextItem instanceof TrackWrapper) {
				tracks.add((TrackWrapper) nextItem);
			}
		}

		final boolean assigned = selectTrackModel.setTracks(tracks);

		if (assigned) {
			notifyListenersStateChanged(this, TRACKS_PROPERTY, null, tracks);
		}

		return assigned;
	}

	private JButton createCommandButton(final String command, final String image) {
		final ImageIcon icon = getIcon(image);
		final JButton button = new JButton(icon);
		button.setToolTipText(command);
		return button;
	}

	private JToggleButton createJToggleButton(final String command, final String image) {
		final ImageIcon icon = getIcon(image);
		final JToggleButton button = new JToggleButton(icon);
		button.setToolTipText(command);
		return button;
	}

	private ImageIcon getIcon(final String image) {
		final URL imageIcon = getClass().getClassLoader().getResource(image);
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(imageIcon);
		} catch (final Exception e) {
			throw new IllegalArgumentException("Icon missing:" + image);
		}
		return icon;
	}

	protected void init() {

		final JComboBox<CalculationHolder> operationComboBox = new JComboBox<>(operationComboModel);
		operationComboBox.setSize(50, 20);
		operationComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent event) {
				selectTrackModel.setOperation((CalculationHolder) event.getItem());
			}
		});

		final List<TrackWrapper> tracks = new ArrayList<>();
		selectTrackModel = new JSelectTrackModel(tracks, (CalculationHolder) operationComboBox.getSelectedItem());

		// Re-renderer listener.
		selectTrackModel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent arg0) {
				updateXYPlot(operationComboBox);
			}
		});

		if (_stepControl != null && _stepControl.getLayers() != null) {
			final DataListener trackChangeListener = new DataListener() {

				@Override
				public void dataExtended(final Layers theData) {
					if (!assignTracks(theData)) {
						updateXYPlot(operationComboBox);
					}
				}

				@Override
				public void dataModified(final Layers theData, final Layer changedLayer) {
					if (!assignTracks(theData)) {
						updateXYPlot(operationComboBox);
					}
				}

				@Override
				public void dataReformatted(final Layers theData, final Layer changedLayer) {
					updateXYPlot(operationComboBox);
				}
			};
			_stepControl.getLayers().addDataModifiedListener(trackChangeListener);
			_stepControl.getLayers().addDataExtendedListener(trackChangeListener);
			_stepControl.getLayers().addDataReformattedListener(trackChangeListener);

		}

		final JSelectTrack selectTrack = new JSelectTrack(selectTrackModel);

		final JButton fixToWindowsButton = createCommandButton("Scale the graph to show all data",
				"icons/16/fit_to_win.png");
		fixToWindowsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				_xytool.getGeneratedChartPanel().getChart().getPlot().zoom(.0);
			}
		});

		// final JToggleButton switchAxesButton = createJToggleButton("Switch axes",
		// "icons/16/swap_axis.png");
		// switchAxesButton.addActionListener(new ActionListener()
		// {
		//
		// @Override
		// public void actionPerformed(final ActionEvent e)
		// {
		// if (switchAxesButton.isSelected())
		// {
		// _xytool.getGeneratedChartPanel().getChart().getXYPlot()
		// .setOrientation(PlotOrientation.HORIZONTAL);
		// }
		// else
		// {
		// _xytool.getGeneratedChartPanel().getChart().getXYPlot()
		// .setOrientation(PlotOrientation.VERTICAL);
		// }
		// }
		// });
		showSymbolsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final boolean isSelected = showSymbolsButton.isSelected();
				_xytool.getGeneratedJFreeChart().setShowSymbols(isSelected);
				updateSymbolButton(symbolOn, symbolOff, showSymbolsButton, isSelected);
			}
		});
		hideCrosshair.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				applyCrosshair();
			}
		});
		// final JToggleButton expandButton = createJToggleButton(
		// "Expand Period covered in sync with scenario time",
		// "icons/16/clock.png");
		// expandButton.addActionListener(new ActionListener()
		// {
		//
		// @Override
		// public void actionPerformed(final ActionEvent e)
		// {
		// _xytool.getGeneratedXYPlot().setGrowWithTime(expandButton.isSelected());
		// _xytool.getGeneratedChartPanel().invalidate();
		// }
		// });
		final JButton wmfButton = createCommandButton("Produce a WMF file of the graph", "icons/16/ex_2word_256_1.png");
		wmfButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					final File dir = fileChooser.getSelectedFile();

					_xytool.getGeneratedSwingPlot().doWMF(dir.getPath());
				}
			}
		});
		final JButton placeBitmapButton = createCommandButton("Place a bitmap image of the graph on the clipboard",
				"icons/16/copy_to_clipboard.png");
		placeBitmapButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				org.mwc.debrief.lite.util.ClipboardUtils.copyToClipboard(_xytool.getGeneratedChartPanel());
			}
		});
		final JButton copyGraph = createCommandButton("Copies the graph as a text matrix to the clipboard",
				"icons/16/export.png");
		copyGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final TimeSeriesCollection dataset = (TimeSeriesCollection) _xytool.getGeneratedXYPlot().getDataset();
				XYPlotUtilities.copyToClipboard(_xytool.getGeneratedChartPanel().getName(), dataset);
			}
		});
		final JButton propertiesButton = createCommandButton("Edit Chart Properties", "icons/16/properties.png");

		propertiesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				_xyPanel.addEditor(_xytool.getGeneratedJFreeChart().getInfo(), null);
			}
		});

		final JComboBox<String> selectTracksLabel = new JComboBox<>(new String[] { "Select Tracks" });
		selectTracksLabel.setEnabled(true);
		selectTracksLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {

			}

			@Override
			public void mouseEntered(final MouseEvent e) {

			}

			@Override
			public void mouseExited(final MouseEvent e) {

			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (selectTracksLabel.isEnabled()) {
					// Get the event source
					final Component component = (Component) e.getSource();

					selectTrack.show(component, 0, 0);

					// Get the location of the point 'on the screen'
					final Point p = component.getLocationOnScreen();

					selectTrack.setLocation(p.x, p.y + component.getHeight());
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {

			}
		});

		add(operationComboBox);
		add(selectTracksLabel);

		add(fixToWindowsButton);
		add(showSymbolsButton);
		add(hideCrosshair);
		add(wmfButton);
		add(placeBitmapButton);
		add(copyGraph);
		add(propertiesButton);

		componentsToDisable.addAll(Arrays.asList(new JComponent[] { fixToWindowsButton, showSymbolsButton,
				hideCrosshair, wmfButton, placeBitmapButton, copyGraph, propertiesButton }));
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

		notifyListenersStateChanged(this, STATE_PROPERTY, oldState, newState);
	}

	private void updateSymbolButton(final String symbolOn, final String symbolOff,
			final JToggleButton showSymbolsButton, final boolean isSelected) {
		showSymbolsButton.setIcon(new ImageIcon(isSelected ? getClass().getClassLoader().getResource(symbolOn)
				: getClass().getClassLoader().getResource(symbolOff)));
		showSymbolsButton.setToolTipText(isSelected ? "Hide Symbols" : "Show Symbols");

		if (isSelected != showSymbolsButton.isSelected()) {
			showSymbolsButton.setSelected(isSelected);
		}
	}

	private void updateXYPlot(final JComboBox<CalculationHolder> operationComboBox) {
		_xytool = new ShowTimeVariablePlot3(_xyPanel, _stepControl);
		final CalculationHolder operation = (CalculationHolder) operationComboBox.getSelectedItem();
		_xytool.setPreselectedOperation(operation);

		Vector<WatchableList> selectedTracksByUser = null;

		if (selectTrackModel != null && _stepControl != null && _stepControl.getStartTime() != null
				&& _stepControl.getEndTime() != null && operation != null) {
			_xytool.setPreselectedPrimaryTrack(selectTrackModel.getPrimaryTrack());
			final List<AbstractSelection<TrackWrapper>> tracks = selectTrackModel.getTracks();
			selectedTracksByUser = new Vector<>();
			for (final AbstractSelection<TrackWrapper> currentTrack : tracks) {
				if (currentTrack.isSelected()) {
					selectedTracksByUser.add(currentTrack.getItem());
				}
			}

			if (!selectedTracksByUser.isEmpty()
					&& (!operation.isARelativeCalculation() || selectTrackModel.getPrimaryTrack() != null)) {
				_xytool.setTracks(selectedTracksByUser);
				_xytool.setPeriod(_stepControl.getStartTime(), _stepControl.getEndTime());

				// _xytool
				_xytool.getData();

				_xytool.getGeneratedJFreeChart().addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						if (NewFormattedJFreeChart.SYMBOL_PROPERTY.equals(evt.getPropertyName())) {
							updateSymbolButton(symbolOn, symbolOff, showSymbolsButton, evt.getNewValue().equals(true));
						}
					}
				});

				final NewFormattedJFreeChart chart = _xytool.getGeneratedJFreeChart();
				chart.addProgressListener(new ChartProgressListener() {

					@Override
					public void chartProgress(final ChartProgressEvent event) {
						// the crosshair moves when the user clicks on the graph, and the axes
						// move on zoom. Keep the label in step with them
						if (event.getType() == ChartProgressEvent.DRAWING_FINISHED && hideCrosshair.isSelected()
								&& updateCrosshairLabel(chart.getXYPlot())) {
							chart.fireChartChanged();
						}
					}
				});

				// this is a new chart, so give it the settings the buttons are showing
				chart.setShowSymbols(showSymbolsButton.isSelected());
				applyCrosshair();

				setState(ACTIVE_STATE);
			} else {
				_xyPanel.reset();
			}
		}
	}
}
