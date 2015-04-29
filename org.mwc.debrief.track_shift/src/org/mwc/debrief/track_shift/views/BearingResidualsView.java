/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.track_shift.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.Activator;

public class BearingResidualsView extends BaseStackedDotsView
{

	public BearingResidualsView()
	{
		super(true, false);
	}

	private static final String SHOW_COURSE = "SHOW_COURSE";
	private Action showCourse;
	private Action flipCourse;
	protected Action _5degResize;

	@Override
	protected void makeActions()
	{
		super.makeActions();

		// now the course action
		flipCourse = new Action("Use +/- 180 scale for absolute data",
				IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();

				processShowCourse();
			}
		};
		flipCourse.setChecked(false);
		flipCourse.setToolTipText("Use +/- 180 scale for absolute data");
		flipCourse.setImageDescriptor(Activator
				.getImageDescriptor("icons/24/swap_axis.png"));

		// now the course action
		showCourse = new Action("Show ownship course", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();

				processShowCourse();
			}
		};
		showCourse.setChecked(true);
		showCourse.setToolTipText("Show ownship course in absolute plot");
		showCourse.setImageDescriptor(Activator
				.getImageDescriptor("icons/24/ShowCourse.png"));

		_5degResize = new Action("Auto resize", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();
				final boolean val = _5degResize.isChecked();
				if (_showDotPlot.isChecked())
				{
					if(val)
					{
					_dotPlot.getRangeAxis().setAutoRange(false);
					_dotPlot.getRangeAxis().setRange(-5, 5);
					}
					else
					{
						_dotPlot.getRangeAxis().setAutoRange(true);
					}
				}
			}
		};
		_5degResize.setChecked(true);
		_5degResize.setToolTipText("Fix bearing range to +/- 5 degs");
		_5degResize.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/24/Binocular.png"));
		
		
		
	}

	@Override
	protected void fillLocalToolBar(final IToolBarManager toolBarManager)
	{
		toolBarManager.add(showCourse);
		toolBarManager.add(flipCourse);
		toolBarManager.add(_5degResize);
		super.fillLocalToolBar(toolBarManager);

	}

	protected void fillLocalPullDown(final IMenuManager manager)
	{
		manager.add(flipCourse);
		super.fillLocalPullDown(manager);
	}

	protected String getUnits()
	{
		return "degs";
	}

	protected String getType()
	{
		return "Bearing";
	}

	protected void updateData(final boolean updateDoublets)
	{
		// update the current datasets
		_myHelper.updateBearingData(_dotPlot, _linePlot, _theTrackDataListener,
				_onlyVisible.isChecked(), showCourse.isChecked(),
				flipCourse.isChecked(), _holder, this, updateDoublets);

		// hide the line for the course dataset (if we're showing the course)
		if (_showLinePlot.isChecked())
		{
			final DefaultXYItemRenderer lineRend = (DefaultXYItemRenderer) super._linePlot
					.getRenderer();
			if (showCourse.isChecked())
			{
				// right, we've got a course, and it's always in slot one - so
				// hide the
				// shapes
				lineRend.setSeriesShapesVisible(0, false);
			}
			else
			{
				// just make sure the first series is visible, it's clearly not
				// a course
				lineRend.setSeriesShapesVisible(0, true);
			}
		}
	}

	private void processShowCourse()
	{
		// ok - redraw the plot we may have changed the course visibility
		updateStackedDots(false);

		// ok - if we're on auto update, do the update
		updateLinePlotRanges();
	}

	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException
	{
		super.init(site, memento);

		if (memento != null)
		{

			final Boolean doCourse = memento.getBoolean(SHOW_COURSE);
			if (doCourse != null)
				showCourse.setChecked(doCourse.booleanValue());
		}
	}

	@Override
	public void saveState(final IMemento memento)
	{
		super.saveState(memento);

		memento.putBoolean(SHOW_COURSE, showCourse.isChecked());
	}
}
