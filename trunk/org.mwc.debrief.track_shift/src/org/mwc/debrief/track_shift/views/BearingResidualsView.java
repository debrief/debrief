package org.mwc.debrief.track_shift.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.mwc.cmap.core.CorePlugin;

public class BearingResidualsView extends BaseStackedDotsView
{

	private static final String SHOW_COURSE = "SHOW_COURSE";
	private Action showCourse;

	@Override
	protected void makeActions()
	{
		super.makeActions();

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
		showCourse.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/showCourse.png"));

	}

	@Override
	protected void fillLocalToolBar(IToolBarManager toolBarManager)
	{
		toolBarManager.add(showCourse);
		super.fillLocalToolBar(toolBarManager);

	}

	protected String getUnits()
	{
		return "degs";
	}

	protected String getType()
	{
		return "Bearing";
	}

	protected void updateData(boolean updateDoublets)
	{
		_dotPlot.setDataset(null);
		_linePlot.setDataset(null);

		// update the current datasets
		_myHelper.updateBearingData(_dotPlot, _linePlot, _theTrackDataListener,
				_onlyVisible.isChecked(), showCourse.isChecked(), _holder, this,
				updateDoublets);

		// hide the line for the course dataset (if we're showing the course)
		DefaultXYItemRenderer lineRend = (DefaultXYItemRenderer) super._linePlot
				.getRenderer();
		if (showCourse.isChecked())
		{
			// right, we've got a course, and it's always in slot one - so hide the
			// shapes
			lineRend.setSeriesShapesVisible(0, false);
		}
		else
		{
			// just make sure the first series is visible, it's clearly not a course
			lineRend.setSeriesShapesVisible(0, true);
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
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);

		if (memento != null)
		{

			Boolean doCourse = memento.getBoolean(SHOW_COURSE);
			if (doCourse != null)
				showCourse.setChecked(doCourse.booleanValue());
		}
	}

	@Override
	public void saveState(IMemento memento)
	{
		super.saveState(memento);

		memento.putBoolean(SHOW_COURSE, showCourse.isChecked());
	}

}
