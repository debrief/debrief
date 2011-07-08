package org.mwc.asset.netasset2.plot;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.mwc.asset.netasset2.part.IVPartUpdate;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import MWC.GUI.Layers;

public class VPlot implements IVPartUpdate
{

	private List list;
	
	/**
	 * the chart we store/manager
	 */
	protected SWTChart _myChart = null;

	/**
	 * the graphic data we know about
	 */
	protected Layers _myLayers;

	protected NetPartWrapper myPart;
	

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VPlot(Composite parent, int style)
	{
	
		_myLayers = new Layers();
		_myChart = new SWTChart(_myLayers, parent){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void chartFireSelectionChanged(ISelection sel)
			{
			}};
	}

	public void setEnabled(final boolean val)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				list.setEnabled(val);
			}
		});
	}

	@Override
	public void setParticipant(final String name)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				// ok, clear out...
				_myLayers.clear();
				
				// create new participant painter
				 myPart = new NetPartWrapper(name);
				_myLayers.addThisLayer(myPart);
				
			}
		});
	}

	@Override
	public void setActSpeed(final double speedKts)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
			}
		});
	}

	@Override
	public void setActCourse(final double courseDegs)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
			}
		});
	}

	@Override
	public void setActDepth(final double depthM)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
			}
		});
	}
}
