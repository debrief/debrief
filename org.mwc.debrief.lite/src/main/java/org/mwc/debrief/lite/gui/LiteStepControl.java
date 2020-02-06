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

package org.mwc.debrief.lite.gui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;

import org.mwc.debrief.lite.menu.DebriefRibbonTimeController;
import org.mwc.debrief.lite.properties.PropertiesDialog;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Tote.StepControl;
import Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.TimeProvider;

public class LiteStepControl extends StepControl {

	public static interface SliderControls {
		public HiResDate getToolboxEndTime();

		public HiResDate getToolboxStartTime();

		public void setEnabled(final boolean enabled);

		public void setToolboxEndTime(final HiResDate val);

		public void setToolboxStartTime(final HiResDate val);
	}

	public static interface TimeLabel {
		void setFontSize(int newSize);

		void setRange(long start, long end);

		void setValue(long time);

		void setValue(String text);
	}

	public static final String timeFormat = DateFormatPropertyEditor.DEFAULT_DATETIME_FORMAT;

	private final ToolParent parent;
	private SliderControls _slider;
	private TimeLabel _timeLabel;
	private Layers _layers;
	private UndoBuffer _undoBuffer;

	public LiteStepControl(final ToolParent _parent, final Session _theSession) {
		super(_parent, Color.black);
		this.parent = _parent;
		setDateFormat(timeFormat);
		_largeSteps = false;
	}

	@Override
	protected void doEditPainter() {
		final StepperListener painter = this.getCurrentPainter();
		if (painter instanceof Editable) {
			ToolbarOwner owner = null;
			final ToolParent parent = getParent();
			if (parent instanceof ToolbarOwner) {
				owner = (ToolbarOwner) parent;
			}

			final PropertiesDialog dialog = new PropertiesDialog(((Editable) painter).getInfo(), _layers, _undoBuffer,
					parent, owner, null);
			dialog.setSize(400, 500);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} else {
			MWC.GUI.Dialogs.DialogFactory.showMessage("Properties Editor", "Current Painter is not editable.");
		}
	}

	@Override
	protected void formatTimeText() {
		_timeLabel.setFontSize(_fontSize);
	}

	public Layers getLayers() {
		return this._layers;
	}

	public Debrief.GUI.Tote.Painters.PainterManager getPainterManager() {
		return _thePainterManager;
	}

	public ToolParent getParent() {
		return parent;
	}

	@Override
	protected PropertiesPanel getPropertiesPanel() {
		ToolbarOwner owner = null;
		final ToolParent parent = getParent();
		if (parent instanceof ToolbarOwner) {
			owner = (ToolbarOwner) parent;
		}

		final PropertiesDialog dialog = new PropertiesDialog(this.getDefaultHighlighter().getInfo(), _layers,
				_undoBuffer, parent, owner, null);
		dialog.setSize(400, 500);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		return null;
	}

	public PlotHighlighter getRectangleHighlighter() {
		for (final PlotHighlighter current : _myHighlighters) {
			if (current instanceof Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter.RectangleHighlight) {
				return current;
			}
		}
		return null;
	}

	public PlotHighlighter getSymbolHighlighter() {
		for (final PlotHighlighter current : _myHighlighters) {
			if (current instanceof Debrief.GUI.Tote.Painters.Highlighters.SymbolHighlighter) {
				return current;
			}
		}
		return null;
	}

	@Override
	public HiResDate getToolboxEndTime() {
		return _slider.getToolboxEndTime();
	}

	@Override
	public HiResDate getToolboxStartTime() {
		return _slider.getToolboxStartTime();
	}

	@Override
	protected void initForm() {
		/*
		 * This is not needed, because the implementation of the form initialization has
		 * been done in the Ribbon.
		 */

	}

	@Override
	protected void painterIsDefined() {
		// ok, ignore
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (evt.getPropertyName().equals(TimeProvider.TIME_CHANGED_PROPERTY_NAME)) {
			final HiResDate dtg = (HiResDate) evt.getNewValue();

			// hey, have we been set?
			changeTime(dtg);
		}

		if (evt.getPropertyName().equals(TimeProvider.PERIOD_CHANGED_PROPERTY_NAME)) {
			final TimePeriod period = (TimePeriod) evt.getNewValue();

			// check we have a time period
			if (period != null) {
				_slider.setToolboxStartTime(period.getStartDTG());
				_slider.setToolboxEndTime(period.getEndDTG());

				setStartTime(period.getStartDTG());
				setEndTime(period.getEndDTG());

				_timeLabel.setRange(period.getStartDTG().getDate().getTime(), period.getEndDTG().getDate().getTime());

				// we should probably enable the slider
				_slider.setEnabled(true);
			} else {
				// we should probably disable the slider
				_slider.setEnabled(false);
			}
		}
	}

	@Override
	public void reset() {
		// let the parent do it's stuff
		super.reset();

		_slider.setEnabled(false);
		_timeLabel.setValue(timeFormat);
	}

	public void setLayers(final Layers _layers) {
		this._layers = _layers;
	}

	public void setSliderControls(final SliderControls slider) {
		_slider = slider;
	}

	public void setTimeLabel(final TimeLabel label) {
		_timeLabel = label;
	}

	@Override
	public void setToolboxEndTime(final HiResDate val) {
		_slider.setToolboxEndTime(val);
	}

	@Override
	public void setToolboxStartTime(final HiResDate val) {
		_slider.setToolboxStartTime(val);
	}

	public void setUndoBuffer(final UndoBuffer _undoBuffer) {
		this._undoBuffer = _undoBuffer;
	}

	public void startStepping(final boolean go) {
		if (go) {
			super.startTimer();
		} else {
			super.stopTimer();
		}

		// inform the listeners
		final Enumeration<StepperListener> iter = getListeners().elements();
		while (iter.hasMoreElements()) {
			final StepperListener l = iter.nextElement();
			l.steppingModeChanged(go);
		}
	}

	@Override
	protected void updateForm(final HiResDate DTG) {
		final String str = _dateFormatter.format(DTG.getDate().getTime());
		_timeLabel.setValue(str);
		_timeLabel.setValue(DTG.getDate().getTime());
		DebriefRibbonTimeController.assignThisTimeFormat(_dateFormatter.toPattern(), false, true);
	}
}
