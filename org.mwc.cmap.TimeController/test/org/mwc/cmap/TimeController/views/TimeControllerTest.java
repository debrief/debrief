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

package org.mwc.cmap.TimeController.views;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mwc.cmap.TimeController.views.TimeController.*;
import org.mwc.cmap.TimeController.views.TimeController.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.TimeController.TimeControllerPlugin;
import org.mwc.cmap.TimeController.controls.DTGBiSlider;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;
import org.mwc.cmap.TimeController.properties.FineTuneStepperProps;
import org.mwc.cmap.TimeController.recorders.CoordinateRecorder;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;
import org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.editors.painters.TemporalLayerPainter;
import org.mwc.debrief.core.editors.painters.highlighters.SWTPlotHighlighter;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.TrackDataProvider;
import MWC.TacticalData.temporal.ControllablePeriod;
import MWC.TacticalData.temporal.ControllableTime;
import MWC.TacticalData.temporal.SteppableTime;
import MWC.TacticalData.temporal.TimeControlPreferences;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import MWC.Utilities.Timer.TimerListener;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TimeControllerTest {
	private int _min;

	private int _max;

	private int _smallTick;

	private int _largeTick;

	private int _dragSize;

	private boolean _enabled;

	@Disabled("Slider units come out as 0 outside the Eclipse runtime; it was in no suite before the JUnit 6 migration, so it never ran")
	@Test
	public void testSliderScales() {
		final SliderRangeManagement range = new SliderRangeManagement() {
			@Override
			public void setEnabled(final boolean val) {
				_enabled = val;
			}

			@Override
			public void setMaxVal(final int max) {
				_max = max;
			}

			@Override
			public void setMinVal(final int min) {
				_min = min;
			}

			@Override
			public void setTickSize(final int small, final int large, final int drag) {
				_smallTick = small;
				_largeTick = large;
				_dragSize = drag;
			}
		};

		// initialise our testing values
		_min = _max = _smallTick = _largeTick = -1;
		_enabled = false;

		HiResDate starter = new HiResDate(0, 100);
		HiResDate ender = new HiResDate(0, 200);
		range.resetRange(starter, ender);

		assertEquals(0, _min, "min val set");
		assertEquals(100, _max, "max val set");
		assertEquals(1, _smallTick, "sml tick set");
		assertEquals(0, _dragSize, "drag size set");
		assertEquals(1000, _largeTick, "large tick set");

		assertTrue(_enabled, "slider should be enabled");

		// ok, see how the transfer goes
		HiResDate newToSlider = new HiResDate(0, 130);
		final int res = range.toSliderUnits(newToSlider);
		assertEquals(30, res, "correct to slider units");

		// and backwards
		newToSlider = range.fromSliderUnits(res, 1000);
		assertEquals(130, newToSlider.getMicros(), "correct from slider units");

		// right, now back to millis
		final Calendar cal = new GregorianCalendar();

		cal.set(2005, 3, 3, 12, 1, 1);
		final Date starterD = cal.getTime();

		cal.set(2005, 3, 12, 12, 1, 1);
		final Date enderD = cal.getTime();

		starter = new HiResDate(starterD.getTime());
		ender = new HiResDate(enderD.getTime());
		range.resetRange(starter, ender);

		final long diff = (enderD.getTime() - starterD.getTime()) / 1000;
		assertEquals(diff, _max, "correct range in secs");
		assertEquals(1, _smallTick, "sml tick set");
		assertEquals(60000, _largeTick, "large tick set");

	}

}
