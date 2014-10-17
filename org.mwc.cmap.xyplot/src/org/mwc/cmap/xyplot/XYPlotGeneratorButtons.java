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
package org.mwc.cmap.xyplot;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jfree.data.general.AbstractSeriesDataset;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.xyplot.views.XYPlotView;
import org.mwc.cmap.xyplot.views.XYPlotView.DatasetProvider;
import org.mwc.cmap.xyplot.wizards.DopplerPlotWizard;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3;
import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationWizard;
import Debrief.Tools.Tote.toteCalculation;
import Debrief.Tools.Tote.Calculations.atbCalc;
import Debrief.Tools.Tote.Calculations.bearingCalc;
import Debrief.Tools.Tote.Calculations.bearingRateCalc;
import Debrief.Tools.Tote.Calculations.courseCalc;
import Debrief.Tools.Tote.Calculations.depthCalc;
import Debrief.Tools.Tote.Calculations.dopplerCalc;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Tools.Tote.Calculations.relBearingCalc;
import Debrief.Tools.Tote.Calculations.speedCalc;
import Debrief.Wrappers.TacticalDataWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.JFreeChart.BearingRateFormatter;
import MWC.GUI.JFreeChart.CourseFormatter;
import MWC.GUI.JFreeChart.DepthFormatter;
import MWC.GUI.JFreeChart.RelBearingFormatter;
import MWC.GUI.JFreeChart.formattingOperation;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

/**
 * embedded class to generate menu-items for creating tactical plot
 */
public class XYPlotGeneratorButtons implements RightClickContextItemGenerator
{

	/**
	 * the operations we provide
	 */
	private Vector<CalculationHolder> _theOperations;

	/**
	 * remember past selections from the drop-down list
	 */
	private static HashMap<String, Integer> _pastSelections;

	/**
	 * constructor - just initialise ourselves
	 */
	public XYPlotGeneratorButtons()
	{
		super();

		if (_theOperations == null)
		{

			_theOperations = new Vector<CalculationHolder>(0, 1);

			_theOperations.addElement(new CalculationHolder(new depthCalc(),
					new DepthFormatter(), false, 0));
			_theOperations.addElement(new CalculationHolder(new courseCalc(),
					new CourseFormatter(), false, 360));
			_theOperations.addElement(new CalculationHolder(new speedCalc(), null,
					false, 0));
			_theOperations.addElement(new CalculationHolder(new rangeCalc(), null,
					true, 0));
			_theOperations.addElement(new CalculationHolder(new bearingCalc(),
					new CourseFormatter(), true, 360));
			_theOperations.addElement(new CalculationHolder(new bearingRateCalc(),
					new BearingRateFormatter(), true, 180));
			_theOperations.addElement(new CalculationHolder(new dopplerCalc(), null,
					true, 0, new DopplerPlotWizard()));

			// provide extra formatting to the y-axis if we're plotting in uk format
			// (-180...+180).
			// but not for US format
			formattingOperation theFormatter = null;
			if (relBearingCalc.useUKFormat())
			{
				theFormatter = new RelBearingFormatter();
			}
			else
				theFormatter = null;

			// and add the relative bearing calcuation
			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new relBearingCalc(), theFormatter, true, 180));
			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new atbCalc(), theFormatter, true, 180));
		}

		if (_pastSelections == null)
			_pastSelections = new HashMap<String, Integer>();
	}

	ShowTimeVariablePlot3.CalculationHolder getChoice()
	{

		// and create the title
		final String[] choices = new String[_theOperations.size()];
		final Object[] results = new Object[_theOperations.size()];
		for (int i = 0; i < _theOperations.size(); i++)
		{
			final ShowTimeVariablePlot3.CalculationHolder thisC = (ShowTimeVariablePlot3.CalculationHolder) _theOperations
					.elementAt(i);
			choices[i] = thisC.toString();
			results[i] = thisC;
		}

		final GetSelection dialog = new GetSelection("View time-variable plot",
				"Please select the attribute to view", choices, results);

		final int selection = dialog.open();
		ShowTimeVariablePlot3.CalculationHolder res;
		if (selection == 0)
		{
			res = (ShowTimeVariablePlot3.CalculationHolder) dialog._result;

			// right, remember what was selected
			_pastSelections.put(choices[0], new Integer(dialog._resultIndex));

		}
		else
		{
			res = null;
		}
		return res;
	}

	public static class GetSelection extends MessageDialog
	{
		private final String[] _titles;

		Object[] _choices;

		Object _result;

		int _resultIndex;

		private final String _message;

		/**
		 * @param dialogTitle
		 * @param dialogMessage
		 * @param _titles
		 * @param _choices
		 */
		public GetSelection(final String dialogTitle, final String dialogMessage,
				final String[] titles, final Object[] choices)
		{
			super(Display.getCurrent().getActiveShell(), dialogTitle, null, null,
					MessageDialog.QUESTION, new String[]
					{ "OK", "Cancel" }, 1);
			this._titles = titles;
			this._choices = choices;
			_message = dialogMessage;
		}

		protected Control createDialogArea(final Composite parent)
		{
			final Composite holder = new Composite(parent, SWT.NONE);
			holder.setLayout(new RowLayout(SWT.HORIZONTAL));
			final Label selection = new Label(holder, SWT.NONE);
			selection.setText(_message);
			final Combo theOps = new Combo(holder, SWT.NONE);
			theOps.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(final SelectionEvent e)
				{
					final int selItem = theOps.getSelectionIndex();
					_result = _choices[selItem];
					_resultIndex = selItem;
				}
			});
			theOps.setItems(_titles);

			// right, have we asked for this one before?
			final Object lastAnswer = _pastSelections.get(_titles[0]);
			if (lastAnswer != null)
			{
				// re-select it then
				final Integer lastInt = (Integer) lastAnswer;
				theOps.select(lastInt.intValue());
			}
			else
			{
				// just select the first one
				theOps.select(0);
			}

			// ok - just store the current one, in case the user just 'accepts' it.
			final int selItem = theOps.getSelectionIndex();
			if (selItem != -1)
			{
				_result = _choices[selItem];
				_resultIndex = selItem;
			}

			return holder;
		}
	}

	/**
	 * add items to the popup menu (if suitable tracks are selected)
	 * 
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{
		final Vector<Editable> candidates = new Vector<Editable>(0, 1);
		boolean duffItemFound = false;

		// right, go through the items and have a nice look at them
		for (int i = 0; i < subjects.length; i++)
		{
			final Editable thisE = subjects[i];

			// is this one we can watch?
			if (thisE instanceof WatchableList)
			{
				if (thisE instanceof TacticalDataWrapper)
				{
					duffItemFound = true;
				}
				else
					// cool, go for it
					candidates.add(thisE);
			}
			else
				duffItemFound = true;
		}

		if ((candidates.size() >= 1))
		{
			if (duffItemFound)
			{
				// don't output this message, since it popups up when we're not even
				// trying to do an xy plot.
				// String txt =
				// "Sorry, not all items are suitable data-sources for an xy plot";
				// MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
				// "XY Plot",
				// txt);
				return;
			}
			else
			{
				final Action viewPlot = new Action("View XY plot")
				{
					public void run()
					{

						final IWorkbench wb = PlatformUI.getWorkbench();
						final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
						final IWorkbenchPage page = win.getActivePage();

						final IEditorPart editor = page.getActiveEditor();

						// get ready for the start/end times
						HiResDate startTime;
						HiResDate endTime;

						try
						{

							// right, we need the time controller if we're going to get the
							// times
							final String timeId = "org.mwc.cmap.TimeController.views.TimeController";
							final IViewReference timeRef = page.findViewReference(timeId);

							if (timeRef == null)
							{
								final String title = "XY Plot";
								final String message = "Time Controller is not open. Please open time-controller and select a time period";
								MessageDialog.openError(Display.getCurrent().getActiveShell(),
										title, message);
								return;
							}

							// ok, sort out what we're plotting
							// find out what the user wants to view
							final ShowTimeVariablePlot3.CalculationHolder theHolder = getChoice();

							// did user cancel?
							if (theHolder == null)
								return;

							// retrieve the necessary input data
							final toteCalculation myOperation = theHolder._theCalc;

							// who is the primary?
							// declare the primary track (even though we may end up not using
							// it)
							WatchableList tmpPrimary = null;

							// is this a relative calculation?
							if (theHolder._isRelative)
							{
								// hmm, double check we have more than one track
								if (subjects.length < 2)
								{
									final String title = "XY Plot";
									final String message = "You must have more than one track selected for this operation";
									MessageDialog.openError(
											Display.getCurrent().getActiveShell(), title, message);
									return;
								}

								// retrieve the necessary input data
								tmpPrimary = getPrimary(subjects);
							}
							final WatchableList thePrimary = tmpPrimary;

							// ////////////////////////////////////////////////
							// sort out the title
							// ////////////////////////////////////////////////
							// get the title to use
							String theTitle = myOperation.getTitle() + " vs Time plot";

							// if we just got one track - put the name in the title
							if (subjects.length == 1)
								theTitle = subjects[0].getName() + " " + theTitle;

							// is this a relative operation
							if (theHolder.isARelativeCalculation())
							{
								if (thePrimary != null)
								{
									// if it's relative, we use the primary track name in the
									// title
									theTitle = thePrimary.getName() + " " + theTitle;
								}
							}

							// lastly, see if there is a wizard
							CalculationWizard wizard = theHolder.getWizard();
							if (wizard != null)
							{
								int res = wizard.open(theHolder._theCalc, thePrimary, subjects);
								if (res == WizardDialog.CANCEL)
								{
									// ok, drop out#
									return;
								}
							}

							// and the plot itself
							final String plotId = "org.mwc.cmap.xyplot.views.XYPlotView";
							final IViewPart newPart = page.showView(plotId, theTitle,
									IWorkbenchPage.VIEW_ACTIVATE);

							// put our subjects into a vector
							final Vector<WatchableList> theTracks = new Vector<WatchableList>(
									0, 1);
							for (int i = 0; i < subjects.length; i++)
							{
								final Editable thisS = subjects[i];
								theTracks.add((WatchableList) thisS);
							}

							// ///////////////////////////////////
							// NOW for the time range
							// ///////////////////////////////////

							// have a go at determining the plot id
							final TimeProvider tp = (TimeProvider) editor
									.getAdapter(TimeProvider.class);
							String thePlotId = null;
							if (tp != null)
							{
								thePlotId = tp.getId();
							}

							final IAdaptable timeC = (IAdaptable) timeRef.getView(true);

							// that's it, now get the data
							final TimePeriod period = (TimePeriod) timeC
									.getAdapter(TimePeriod.class);
							if (period == null)
							{
								CorePlugin
										.logError(
												Status.ERROR,
												"TimeController view no longer provides TimePeriod adapter",
												null);
								return;
							}

							startTime = period.getStartDTG();
							endTime = period.getEndDTG();

							if ((startTime.greaterThan(endTime))
									|| (startTime.equals(endTime)))
							{
								final String title = "XY Plot";
								final String message = "No time period has been selected.\nPlease select start/stop time from the Time Controller";
								MessageDialog.openError(Display.getCurrent().getActiveShell(),
										title, message);
								return;
							}

							// NOTE: next section commented out. When doing a relative
							// calculation, it was causing the whole period to be shown
							// - not just the period selected in the Time Controller
							//
							//
							// aah. does the primary track have it's own time period?
							// if (thePrimary != null)
							// {
							// if (thePrimary.getStartDTG() != null)
							// startTime = thePrimary.getStartDTG();
							//
							// if (thePrimary.getEndDTG() != null)
							// endTime = thePrimary.getEndDTG();
							// }

							final HiResDate finalStart = startTime;
							final HiResDate finalEnd = endTime;

							final DatasetProvider prov = new DatasetProvider()
							{

								@Override
								public AbstractSeriesDataset getDataset()
								{
									return ShowTimeVariablePlot3.getDataSeries(thePrimary,
											theHolder, theTracks, finalStart, finalEnd, null);
								}

								@Override
								public Layers getLayers()
								{
									return theLayers;
								}
							};

							// ok, try to retrieve the view
							final IViewReference plotRef = page.findViewReference(plotId,
									theTitle);
							final XYPlotView plotter = (XYPlotView) plotRef.getView(true);

							try
							{
								plotter.showPlot(theTitle, prov, myOperation.toString() + " ("
										+ myOperation.getUnits() + ")", theHolder._theFormatter,
										thePlotId);
							}
							catch (RuntimeException ex)
							{
								// show the error message
								CorePlugin.errorDialog("Generate XY Plot", ex.getMessage());

								// and remove the view from the screen
								page.hideView(newPart);

								// lastly - try to ditch the view
								newPart.dispose();

								return;
							}

						}
						catch (final PartInitException e)
						{
							e.printStackTrace();
						}

					}
				};

				// ok - set the image descriptor
				viewPlot.setImageDescriptor(XYPlotPlugin
						.getImageDescriptor("icons/document_chart.png"));

				parent.add(new Separator());
				parent.add(viewPlot);
			}
		}

	}

	protected WatchableList getPrimary(final Editable[] subjects)
	{

		WatchableList res = null;

		// check we have some tracks selected
		if (subjects != null)
		{
			// sort out what we're looking at
			final String[] labels = new String[subjects.length];
			final Object[] values = new Object[subjects.length];
			for (int i = 0; i < subjects.length; i++)
			{
				final Editable thisE = (Editable) subjects[i];
				labels[i] = thisE.toString();
				values[i] = thisE;
			}

			final GetSelection sel = new GetSelection("Select primary",
					"Which is the primary track", labels, values);
			final int selection = sel.open();
			if (selection == 0)
			{
				res = (WatchableList) sel._result;

				// right, remember what was selected
				_pastSelections.put(labels[0], new Integer(sel._resultIndex));
			}
		}
		else
		{
			MWC.GUI.Dialogs.DialogFactory.showMessage("Track Selector",
					"Please select one or more tracks");
		}
		return res;

	}

}