package org.mwc.debrief.multipath2.views;

import java.awt.BorderLayout;
import java.awt.Frame;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;

import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.JFreeChart.RelativeDateAxis;

/**

 */

public class MultiPathView extends ViewPart implements MultiPathPresenter.Display
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.debrief.MultiPath2";
	private MultiPathUI _ui;
	private MultiPathPresenter _presenter;

	/**
	 * The constructor.
	 */
	public MultiPathView()
	{
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_ui = new MultiPathUI(parent, SWT.EMBEDDED);
		
		
		createPlot(_ui.getChartHolder());
		
		// now sort out the presenter
		_presenter = new MultiPathPresenter(this);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}
	
private void createPlot(Composite ui)
{ // create a date-formatting axis
	final DateAxis dateAxis = new RelativeDateAxis();
	dateAxis.setStandardTickUnits(DateAxisEditor
			.createStandardDateTickUnitsAsTickUnits());
	
	NumberAxis valAxis = new NumberAxis("Secs");
	DefaultXYItemRenderer theRenderer = new	DefaultXYItemRenderer();

	
	XYPlot _thePlot = new XYPlot(null, dateAxis, valAxis, theRenderer );
	JFreeChart _plotArea = new JFreeChart(_thePlot);
	ChartPanel _chartPanel = new ChartPanel(_plotArea);
	
	// now we need a Swing object to put our chart into
	Frame _plotControl = SWT_AWT.new_Frame(ui);
	
	_plotControl.add(_chartPanel, BorderLayout.CENTER);

}

	private void hookContextMenu()
	{
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{

	}

	private void fillContextMenu(IMenuManager manager)
	{
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{

	}

	private void makeActions()
	{
	
	}

	private void hookDoubleClickAction()
	{

	}

	private void showMessage(String message)
	{
		
		MessageDialog.openInformation(this.getSite().getShell(),
				"Multpath Analysis", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		
	}

	@Override
	public void addSVPListener(FileHandler handler)
	{
		configureFileDropSupport(_ui.getSVPHolder(), handler);
	}

	@Override
	public void addTimeDeltaListener(FileHandler handler)
	{
		configureFileDropSupport(_ui.getIntervalHolder(), handler);
	}

	@Override
	public void addDragHandler(final ValueHandler handler)
	{
		final Slider slider = _ui.getSlider();
		slider.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				handler.newValue(slider.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				handler.newValue(slider.getSelection());
			}});
	}

	@Override
	public TrackDataProvider getDataProvider()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showError(String string)
	{
		showMessage(string);
	}

	@Override
	public void display(TimeSeries _measuredSeries, TimeSeries calculated)
	{
		// put the series onto the chart
	}

	@Override
	public void setEnabled(boolean b)
	{
		_ui.setEnabled(b);
	}
	

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport(Control _pusher, final FileHandler handler)
	{
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes =
		{ FileTransfer.getInstance() };

		DropTarget target = new DropTarget(_pusher, dropOperation);
		target.setTransfer(dropTypes);
		target.addDropListener(new DropTargetListener()
		{
			public void dragEnter(DropTargetEvent event)
			{
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					if (event.detail != DND.DROP_COPY)
					{
						event.detail = DND.DROP_COPY;
					}
				}
			}

			public void dragLeave(DropTargetEvent event)
			{
			}

			public void dragOperationChanged(DropTargetEvent event)
			{
			}

			public void dragOver(DropTargetEvent event)
			{
			}

			public void drop(DropTargetEvent event)
			{
				String[] fileNames = null;
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					fileNames = (String[]) event.data;
				}
				if (fileNames != null)
				{
					
					if (handler != null)
						handler.newFile(fileNames[0]);
				}
			}

			public void dropAccept(DropTargetEvent event)
			{
			}

		});

	}

	@Override
	public void setSVPName(String fName)
	{
		_ui.setSVPName(fName);
	}

	@Override
	public void setIntervalName(String fName)
	{
		_ui.setIntervalName(fName);
	}
	
}