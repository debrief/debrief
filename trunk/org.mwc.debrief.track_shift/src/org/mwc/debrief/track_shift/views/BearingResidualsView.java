package org.mwc.debrief.track_shift.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.mwc.cmap.core.CorePlugin;

public class BearingResidualsView extends BaseStackedDotsView
{
	


	private Action showCourse;

	@Override
	protected void makeActions() {
		super.makeActions();
		
		// now the course action
		showCourse = new Action("Show ownship course", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();
				// ok - redraw the plot we may have changed the course visibility
				updateStackedDots();
			}
		};
		showCourse.setChecked(true);
		showCourse.setToolTipText("Show ownship course in absolute plot");
		showCourse.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/follow_selection.gif"));

	}

	@Override
	protected void fillLocalToolBar(IToolBarManager toolBarManager) {
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
	
	protected void updateData()
	{
		// update the current datasets
		_myHelper.updateBearingData(_dotPlot, _linePlot, _theTrackDataListener,
				_onlyVisible.isChecked(), showCourse.isChecked(), _holder, this);	
		
		// hide the line for the course dataset
		DefaultXYItemRenderer lineRend = (DefaultXYItemRenderer) super._linePlot.getRenderer();
		lineRend.setSeriesShapesVisible(1, false);
		
	}
}
