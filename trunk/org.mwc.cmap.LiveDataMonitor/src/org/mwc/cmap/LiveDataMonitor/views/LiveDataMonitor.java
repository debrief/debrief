package org.mwc.cmap.LiveDataMonitor.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Scenario.LiveScenario.ISimulationQue;
import ASSET.Scenario.LiveScenario.MockSimulation;
import ASSET.Scenario.LiveScenario.Simulation;
import ASSET.Scenario.LiveScenario.SimulationQue;
import MWC.Algorithms.LiveData.Attribute;
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

public class LiveDataMonitor extends ViewPart implements ISelectionProvider
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "livedatamonitor.views.LiveDataMonitor";

	private Vector<ISelectionChangedListener> _selectionListeners;

	private JFreeChart _chart;

	private IAttribute _watchedAttr;

	private PropertyChangeListener _attListener;

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
				DataDoublet newD = (DataDoublet) evt.getNewValue();
				final long time = newD.getTime();
				final Number value = (Number) newD.getValue();

				// and store it
				TimeSeriesCollection coll = (TimeSeriesCollection) _chart
						.getXYPlot().getDataset();
				
				TimeSeries tmpSeries;
				
				if(coll == null)
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
					tmpSeries =  coll.getSeries(0);
				}
				
				final TimeSeries series = tmpSeries;

				// add to series in different thread...
				Display.getDefault().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						series.add(new Millisecond(new Date(time)), value);
					}
				});
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
		@SuppressWarnings("unused")
		ChartComposite frame = new ChartComposite(parent, SWT.NONE, _chart, true);

		configureListeners();
	
		// provide button to gen duff data
		Action genDuff = new Action("Gen mock"){
			public void run()
			{
				genMockSims();
			}};
			
		Vector<Action> startActions = new Vector<Action>();
		startActions.add(genDuff);
		createActions(startActions);
		

	}

	
	private void genMockSims()
	{
		// get ready to build up our list of simulations
		Vector<Action> actions = new Vector<Action>(0, 1);

		// create a block of attributes
		Vector<IAttribute> attrs = new Vector<IAttribute>();
		IAttribute att1 = new Attribute("Height","m",  true);
		attrs.add(att1);
		IAttribute att2 = new Attribute("Speed", "kts", false);
		attrs.add(att2);
		IAttribute att3 = new Attribute("Distance","yds",  true);
		attrs.add(att3);
		IAttribute att4 = new Attribute("Fuel", "%", false);
		attrs.add(att4);
		IAttribute att5 = new Attribute("Range","m",  true);
		attrs.add(att5);
		IAttribute att6 = new Attribute("Acceleration","m/s/s",  false);
		attrs.add(att6);
		IAttribute att7 = new Attribute("Water","ddegs",  false);
		attrs.add(att7);
		IAttribute att8 = new Attribute("Temperature","c",  false);
		attrs.add(att8);
		
		// sort out the simulation stuff
		Vector<ISimulation> _theSims = new Vector<ISimulation>();
		_theSims.add(new MockSimulation("sim1", 2600, attrs));
		_theSims.add(new MockSimulation("sim2", 4100, attrs));
		_theSims.add(new MockSimulation("sim3", 5600, attrs));

		final ISimulationQue sq = new SimulationQue(_theSims);
		// and the start stop
		final Action start = new Action("Start")
		{
			public void run()
			{
				sq.startQue();
				setText("Running");
			}
		};

		actions.add(start);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		actions.add(new SimMonitor(_theSims.elementAt(0), _theSims.elementAt(0)
				.getAttributes().elementAt(2), this, toolBarManager));
		actions.add(new SimMonitor(_theSims.elementAt(1), _theSims.elementAt(1)
				.getAttributes().elementAt(3), this, toolBarManager));
		actions.add(new SimMonitor(_theSims.elementAt(2), _theSims.elementAt(2)
				.getAttributes().elementAt(4), this, toolBarManager));

		createActions(actions);		

		// listen out for the last sim finishing
		sq.getSimulations().lastElement().getState().addPropertyChangeListener(
				new PropertyChangeListener()
				{
					public void propertyChange(PropertyChangeEvent evt)
					{
						DataDoublet dd = (DataDoublet) evt.getNewValue();
						if (dd.getValue() == Simulation.COMPLETE)
						{
							start.setText("Start");
						}
					}
				});

	}

	/** a watchable item has been selected, better show it
	 * 
	 * @param attribute what we're going to watch
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

			for (Iterator<DataDoublet> iterator = data.iterator(); iterator.hasNext();)
			{
				DataDoublet thisD = (DataDoublet) iterator.next();
				series.add(new Millisecond(new Date(thisD.getTime())), (Number) thisD
						.getValue());
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

	/** sort out who's listening to what
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

		getSite().setSelectionProvider(this);
	}

	private void createActions(Vector<Action> _theActions)
	{
		for (Iterator<Action> iterator = _theActions.iterator(); iterator.hasNext();)
		{
			Action action = (Action) iterator.next();
			this.getViewSite().getActionBars().getToolBarManager().add(action);
			this.getViewSite().getActionBars().getMenuManager().add(action);
		}
	};

	protected void updateSelection()
	{
		IStructuredSelection sel = new StructuredSelection(createSampleDataset());
		setSelection(sel);
	}

	private IAttribute createSampleDataset()
	{
		Attribute res = new Attribute("height", "m", false);
		int len = (int) (Math.random() * 1120);
		for (int i = 0; i < len; i++)
		{
			res.fireUpdate(this, i * 1000, new Double(Math.random() * 1000));
		}

		return res;
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
				if (_watchedAttr != attr)
				{
					// yup, all change

					// are we already listening to one?
					if (_watchedAttr != null)
						_watchedAttr.removePropertyChangeListener(_attListener);

					// store the new one
					_watchedAttr = attr;

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

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
		SelectionChangedEvent sEvent = new SelectionChangedEvent(this, selection);

		for (Iterator<ISelectionChangedListener> stepper = _selectionListeners
				.iterator(); stepper.hasNext();)
		{
			ISelectionChangedListener thisL = (ISelectionChangedListener) stepper
					.next();
			if (thisL != null)
			{
				thisL.selectionChanged(sEvent);
			}
		}

	}

	@Override
	public ISelection getSelection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private static class SimMonitor extends Action
	{
		private ISimulation _mySim;
		private IAttribute _myAttr;
		private ISelectionProvider _mySelly;

		public SimMonitor(final ISimulation sim, final IAttribute attr, final ISelectionProvider sel, final IToolBarManager toolBarManager)
		{
			_mySim = sim;
			_myAttr = attr;
			_mySelly = sel;

			setText(sim.getName() + "-" + attr.getName());

			// start listening
			_mySim.getState().addPropertyChangeListener(new PropertyChangeListener()
			{

				public void propertyChange(PropertyChangeEvent evt)
				{
					DataDoublet newState = (DataDoublet) evt.getNewValue();
					String state = (String) newState.getValue();

					// right, what's it up to?
					if (state == ISimulation.RUNNING)
						setText("*" + _mySim.getName() + "-" + _myAttr.getName() + "*");
					else if (state == ISimulation.COMPLETE)
						setText("[" + _mySim.getName() + "-" + _myAttr.getName() + "]");

					Display.getDefault().asyncExec(new Runnable()
					{

						@Override
						public void run()
						{
							toolBarManager.update(true);
						}
					});

				}
			});
		}

		@Override
		public void run()
		{
			super.run();

			_mySelly.setSelection(new StructuredSelection(_myAttr));
		}

	}
}