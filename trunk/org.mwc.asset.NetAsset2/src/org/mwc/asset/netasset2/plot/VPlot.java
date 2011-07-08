package org.mwc.asset.netasset2.plot;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mwc.asset.netasset2.part.IVPartUpdate;
import org.mwc.asset.netasset2.time.IVTime;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import ASSET.Participants.Status;
import MWC.GUI.Layers;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;

public class VPlot extends Composite implements IVPartUpdate, IVTime
{

	/**
	 * the chart we store/manager
	 */
	protected SWTChart _myChart = null;

	/**
	 * the graphic data we know about
	 */
	protected Layers _myLayers;

	protected NetPartWrapper myPart;
	
	private int _numUpdates = 0;
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VPlot(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		ToolBar toolBar = new ToolBar(this, SWT.PUSH);
		toolBar.setLayoutData(BorderLayout.NORTH);
		
		_myLayers = new Layers();
		_myChart = new SWTChart(_myLayers, this){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void chartFireSelectionChanged(ISelection sel)
			{
			}};
		_myChart.getCanvasControl().setLayoutData(BorderLayout.CENTER);
		
		ToolItem btnZoomOut = new ToolItem(toolBar, SWT.NONE);
		btnZoomOut.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e)
			{
				zoomOut();
			}});
		btnZoomOut.setText("Zoom out");

		ToolItem btnFitToWin = new ToolItem(toolBar, SWT.NONE);
		btnFitToWin.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e)
			{
				fitToWin();
			}});
		btnFitToWin.setText("Fit to win");

	}

	protected void zoomOut()
	{
		_myChart.getCanvas().getProjection().zoom(2.0);
		_myChart.update();
	}

	protected void fitToWin()
	{
		_myChart.rescale();
		_myChart.update();
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
	public void newTime(long newTime)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				_myChart.update();
			}
		});
	}

	@Override
	public void moved(final Status status)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				myPart.setStatus(status);
				_numUpdates ++;
				
				if(_numUpdates == 2)
					_myChart.rescale();
			}
		});
	}
}
