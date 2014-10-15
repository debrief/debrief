/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.multipath2;

import java.awt.BorderLayout;
import java.awt.Frame;

import org.eclipse.jface.action.Action;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.debrief.multipath2.views.MultiPathUI;

import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.JFreeChart.RelativeDateAxis;

/**

 */

public class MultiPathView extends ViewPart implements
		MultiPathPresenter.Display
{

	private static final String HELP_CONTEXT = "org.mwc.debrief.help.MultipathAnalysis";
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.debrief.MultiPath2";
	private MultiPathUI _ui;
	private XYPlot _thePlot;
	protected MultiPathPresenter _presenter;
	private Action _helpAction;

	/**
	 * The constructor.
	 */
	public MultiPathView()
	{
		// now sort out the presenter
		_presenter = new MultiPathPresenter(this);
		
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent)
	{
		_ui = new MultiPathUI(parent, SWT.EMBEDDED);

		createPlot(_ui.getChartHolder());

		// let the presenter finish off
		_presenter.bind();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		
		// and declare our context sensitive help
		CorePlugin.declareContextHelp(parent, HELP_CONTEXT);
	}

	private void createPlot(final Composite ui)
	{ // create a date-formatting axis
		final DateAxis dateAxis = new RelativeDateAxis();
		dateAxis.setStandardTickUnits(DateAxisEditor
				.createStandardDateTickUnitsAsTickUnits());

		final NumberAxis valAxis = new NumberAxis("Delay (Secs)");
		final DefaultXYItemRenderer theRenderer = new DefaultXYItemRenderer();
		theRenderer.setBaseShapesVisible(false);

		_thePlot = new XYPlot(null, dateAxis, valAxis, theRenderer);
		final JFreeChart _plotArea = new JFreeChart(_thePlot);
		final ChartPanel _chartPanel = new ChartPanel(_plotArea);

		// now we need a Swing object to put our chart into
		final Frame _plotControl = SWT_AWT.new_Frame(ui);

		_plotControl.add(_chartPanel, BorderLayout.CENTER);

	}

	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException
	{
		super.init(site, memento);

		// let the parent do the hard work
		_presenter.init(memento);

	}

	@Override
	public void saveState(final IMemento memento)
	{
		super.saveState(memento);

		// pass it on to the presenter to sort out
		_presenter.saveState(memento);
	}

	private void hookContextMenu()
	{
	}

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager)
	{
		// and the help link
		manager.add(_helpAction);
	}

	private void fillLocalToolBar(final IToolBarManager manager)
	{
		// and the help link
		manager.add(_helpAction);

	}

	private void makeActions()
	{
		_helpAction = CorePlugin.createOpenHelpAction(
				HELP_CONTEXT,
				"Help on multi-path analysis", this);

	}

	private void hookDoubleClickAction()
	{

	}

	private void showMessage(final String message)
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
	public void addRangesListener(final FileHandler handler)
	{
		configureFileDropSupport(_ui.getRangeHolder(), handler);
	}

	
	@Override
	public void addSVPListener(final FileHandler handler)
	{
		configureFileDropSupport(_ui.getSVPHolder(), handler);
	}

	@Override
	public void addMagicListener(final SelectionListener listener)
	{
		_ui.addMagicHandler(listener);
	}
	
	@Override
	public void addTimeDeltaListener(final FileHandler handler)
	{
		configureFileDropSupport(_ui.getIntervalHolder(), handler);
	}

	@Override
	public void addDragHandler(final ValueHandler handler)
	{
		final Slider slider = _ui.getSlider();
		slider.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				handler.newValue(slider.getSelection());
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				handler.newValue(slider.getSelection());
			}
		});
	}

	@Override
	public TrackDataProvider getDataProvider()
	{
		TrackDataProvider res = null;

		// ok, grab the current editor
		final IEditorPart editor = this.getSite().getPage().getActiveEditor();
		final Object provider = editor.getAdapter(TrackDataProvider.class);
		if (provider != null)
		{
			res = (TrackDataProvider) provider;
		}

		return res;
	}

	@Override
	public void showError(final String string)
	{
		showMessage(string);
	}

	@Override
	public void display(final TimeSeries measured, final TimeSeries calculated)
	{
		// collate the data
		final TimeSeriesCollection coll = new TimeSeriesCollection();
		coll.addSeries(measured);
		coll.addSeries(calculated);

		// put the series onto the chart
		_thePlot.setDataset(coll);
	}

	@Override
	public void setEnabled(final boolean b)
	{
		_ui.setEnabled(b);

		if (b)
			_ui.getSlider().setSelection(MultiPathPresenter.DEFAULT_DEPTH);
	}

	/**d
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport(final Control _pusher,
			final FileHandler handler)
	{
		final int dropOperation = DND.DROP_COPY;
		final Transfer[] dropTypes =
		{ FileTransfer.getInstance() };

		final DropTarget target = new DropTarget(_pusher, dropOperation);
		target.setTransfer(dropTypes);
		target.addDropListener(new DropTargetListener()
		{
			public void dragEnter(final DropTargetEvent event)
			{
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					if (event.detail != DND.DROP_COPY)
					{
						event.detail = DND.DROP_COPY;
					}
				}
			}

			public void dragLeave(final DropTargetEvent event)
			{
			}

			public void dragOperationChanged(final DropTargetEvent event)
			{
			}

			public void dragOver(final DropTargetEvent event)
			{
			}

			public void drop(final DropTargetEvent event)
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

			public void dropAccept(final DropTargetEvent event)
			{
			}

		});

	}

	@Override
	public void setSVPName(final String fName)
	{
		_ui.setSVPName(fName);
	}

	@Override
	public void setIntervalName(final String fName)
	{
		_ui.setIntervalName(fName);
	}

	@Override
	public void setSliderText(final String text)
	{
		_ui.setSliderValText(text);
	}

	@Override
	public void setSliderVal(final int value)
	{
		_ui.getSlider().setSelection(value);
	}

	@Override
	public void setSliderMax(final int maxDepth)
	{
		_ui.getSlider().setMaximum(maxDepth);
	}

}