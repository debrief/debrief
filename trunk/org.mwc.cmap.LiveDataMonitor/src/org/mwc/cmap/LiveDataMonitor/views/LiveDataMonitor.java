package org.mwc.cmap.LiveDataMonitor.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.Algorithms.LiveData.IAttribute.IndexedAttribute;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class LiveDataMonitor extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "livedatamonitor.views.LiveDataMonitor";

	private JFreeChart _chart;

	private IAttribute _watchedAttr;

	private PropertyChangeListener _attListener;

	private IndexedAttribute _myIndexedAttr;

	private ChartComposite _chartFrame;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */

	/**
	 * The constructor.
	 */
	public LiveDataMonitor()
	{
		_attListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				// aah, is this for the scenario we're watching
				if (_myIndexedAttr != null)
					if (evt.getSource() == _myIndexedAttr.index)
					{

						DataDoublet newD = (DataDoublet) evt.getNewValue();
						final long time = newD.getTime();
						final Number value = (Number) newD.getValue();

						// and store it
						TimeSeriesCollection coll = (TimeSeriesCollection) _chart
								.getXYPlot().getDataset();

						TimeSeries tmpSeries;

						if (coll == null)
						{
							final TimeSeriesCollection dataset = new TimeSeriesCollection();
							tmpSeries = new TimeSeries(_watchedAttr.getName());
							dataset.addSeries(tmpSeries);
							// add to series in different thread...
							Display.getDefault().asyncExec(new Runnable()
							{

								@Override
								public void run()
								{
									_chart.getXYPlot().setDataset(dataset);
								}
							});
						}
						else
						{
							tmpSeries = coll.getSeries(0);
						}

						final TimeSeries series = tmpSeries;

						// add to series in current thread, accepting it will slow down the
						// UI
						Display.getDefault().syncExec(new Runnable()
						{
							@Override
							public void run()
							{
								// are we still open?i
								if (!_chartFrame.isDisposed())
								{
									// sure, go for it,
									series.addOrUpdate(new Millisecond(new Date(time)), value);
								}
							}
						});
					}
			}
		};
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_chart = createChart(null);
		_chartFrame = new ChartComposite(parent, SWT.NONE, _chart, true);

		configureListeners();
	}

	/**
	 * a watchable item has been selected, better show it
	 * 
	 * @param attribute
	 *          what we're going to watch
	 */
	private void storeDataset(IAttribute attribute, Object index)
	{
		Vector<DataDoublet> data = attribute.getHistoricValues(index);

		// is there any data in it?
		if (data.size() == 0)
		{
			_chart.setTitle(attribute.getName() + " has no data");
			_chart.getXYPlot().setDataset(null);
		}
		else
		{
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			TimeSeries series = new TimeSeries(attribute.getName());
			_chart.setTitle(attribute.getName());
			_chart.getXYPlot().getRangeAxis().setLabel(attribute.getUnits());

			for (Iterator<DataDoublet> iterator = data.iterator(); iterator.hasNext();)
			{
				DataDoublet thisD = (DataDoublet) iterator.next();
				series.addOrUpdate(new Millisecond(new Date(thisD.getTime())),
						(Number) thisD.getValue());
			}
			dataset.addSeries(series);

			_chart.getXYPlot().setDataset(dataset);
		}
	}

	/**
	 * Creates the Chart based on a dataset
	 */
	private JFreeChart createChart(TimeSeriesCollection dataset)
	{

		String annTitle = "[PENDING]";
		String catLabel = "Time";
		String valueLabel = "Value";
		JFreeChart chart = ChartFactory.createXYLineChart(annTitle, catLabel,
				valueLabel, dataset, PlotOrientation.VERTICAL, false, true, false);

		XYPlot plot = chart.getXYPlot();
		DateAxis dateA = new DateAxis();
		plot.setDomainAxis(dateA);
		plot.setRenderer(new XYLineAndShapeRenderer());
		plot.setNoDataMessage("No data available");
		return chart;

	}

	/**
	 * sort out who's listening to what
	 * 
	 */
	private void configureListeners()
	{
		final ISelectionListener mylistener = new ISelectionListener()
		{
			public void selectionChanged(IWorkbenchPart sourcepart,
					ISelection selection)
			{
				// is this something we can get at?
				if (selection instanceof IStructuredSelection)
				{
					IStructuredSelection strS = (IStructuredSelection) selection;
					Object val = strS.getFirstElement();
					showNewSelection(val);
				}
			}
		};

		IWorkbenchWindow theWindow = getSite().getWorkbenchWindow();
		theWindow.getSelectionService().addSelectionListener(mylistener);
	}

	protected void showNewSelection(Object sel)
	{
		// it it something we want to watch?
		if (sel instanceof IndexedAttribute)
		{
			IAttribute.IndexedAttribute ind = (IndexedAttribute) sel;

			// cool, go for it.
			final IAttribute attr = ind.attribute;
			final Object index = ind.index;

			// is this a different one?
			if (_myIndexedAttr != ind)
			{
				// yup, all change

				// are we already listening to one?
				if (_watchedAttr != null)
					_watchedAttr.removePropertyChangeListener(_attListener);

				// store the new ones
				_watchedAttr = attr;
				_myIndexedAttr = ind;

				// and start listening to it
				_watchedAttr.addPropertyChangeListener(_attListener);

				storeDataset(_watchedAttr, index);
			}

		}

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{

	}

}