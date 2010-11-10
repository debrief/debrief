package org.mwc.cmap.xyplot;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
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

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3;
import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Tools.Tote.toteCalculation;
import Debrief.Tools.Tote.Calculations.atbCalc;
import Debrief.Tools.Tote.Calculations.bearingCalc;
import Debrief.Tools.Tote.Calculations.bearingRateCalc;
import Debrief.Tools.Tote.Calculations.courseCalc;
import Debrief.Tools.Tote.Calculations.depthCalc;
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
	static HashMap<String, Integer> _pastSelections;

	/**
	 * constructor - just initialise ourselves
	 */
	public XYPlotGeneratorButtons()
	{
		super();

		if (_theOperations == null)
		{

			_theOperations = new Vector<CalculationHolder>(0, 1);

			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new depthCalc(), new DepthFormatter(), false, 0));
			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new courseCalc(), new CourseFormatter(), false, 360));
			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new speedCalc(), null, false, 0));
			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new rangeCalc(), null, true, 0));
			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new bearingCalc(), new CourseFormatter(), true, 360));
			_theOperations.addElement(new ShowTimeVariablePlot3.CalculationHolder(
					new bearingRateCalc(), new BearingRateFormatter(), true, 180));

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
		String[] choices = new String[_theOperations.size()];
		Object[] results = new Object[_theOperations.size()];
		for (int i = 0; i < _theOperations.size(); i++)
		{
			ShowTimeVariablePlot3.CalculationHolder thisC = (ShowTimeVariablePlot3.CalculationHolder) _theOperations
					.elementAt(i);
			choices[i] = thisC.toString();
			results[i] = thisC;
		}

		GetSelection dialog = new GetSelection("View time-variable plot",
				"Please select the attribute to view", choices, results);

		int selection = dialog.open();
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
		private String[] _titles;

		Object[] _choices;

		Object _result;

		int _resultIndex;

		private String _message;

		/**
		 * @param dialogTitle
		 * @param dialogMessage
		 * @param _titles
		 * @param _choices
		 */
		public GetSelection(String dialogTitle, String dialogMessage,
				String[] titles, Object[] choices)
		{
			super(Display.getCurrent().getActiveShell(), dialogTitle, null, null,
					MessageDialog.QUESTION, new String[]
					{ "OK", "Cancel" }, 1);
			this._titles = titles;
			this._choices = choices;
			_message = dialogMessage;
		}

		protected Control createDialogArea(Composite parent)
		{
			Composite holder = new Composite(parent, SWT.NONE);
			holder.setLayout(new RowLayout(SWT.HORIZONTAL));
			Label selection = new Label(holder, SWT.NONE);
			selection.setText(_message);
			final Combo theOps = new Combo(holder, SWT.NONE);
			theOps.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					int selItem = theOps.getSelectionIndex();
					_result = _choices[selItem];
					_resultIndex = selItem;
				}
			});
			theOps.setItems(_titles);

			// right, have we asked for this one before?
			Object lastAnswer = _pastSelections.get(_titles[0]);
			if (lastAnswer != null)
			{
				// re-select it then
				Integer lastInt = (Integer) lastAnswer;
				theOps.select(lastInt.intValue());
			}
			else
			{
				// just select the first one
				theOps.select(0);
			}

			// ok - just store the current one, in case the user just 'accepts' it.
			int selItem = theOps.getSelectionIndex();
			_result = _choices[selItem];
			_resultIndex = selItem;

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
	public void generate(IMenuManager parent, Layers theLayers,
			Layer[] parentLayers, final Editable[] subjects)
	{
		final Vector<Editable> candidates = new Vector<Editable>(0, 1);
		boolean duffItemFound = false;

		// right, go through the items and have a nice look at them
		for (int i = 0; i < subjects.length; i++)
		{
			Editable thisE = subjects[i];

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
				Action viewPlot = new Action("View XY plot")
				{
					public void run()
					{

						IWorkbench wb = PlatformUI.getWorkbench();
						IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
						IWorkbenchPage page = win.getActivePage();

						IEditorPart editor = page.getActiveEditor();

						// get ready for the start/end times
						HiResDate startTime, endTime;

						try
						{

							// right, we need the time controller if we're going to get the
							// times
							String timeId = "org.mwc.cmap.TimeController.views.TimeController";
							IViewReference timeRef = page.findViewReference(timeId);

							if (timeRef == null)
							{
								String title = "XY Plot";
								String message = "Time Controller is not open. Please open time-controller and select a time period";
								MessageDialog.openError(Display.getCurrent().getActiveShell(),
										title, message);
								return;
							}

							// ok, sort out what we're plotting
							// find out what the user wants to view
							ShowTimeVariablePlot3.CalculationHolder theHolder = getChoice();

							// did user cancel?
							if (theHolder == null)
								return;

							// retrieve the necessary input data
							toteCalculation myOperation = theHolder._theCalc;

							// did it work?
							if (theHolder == null)
								return;

							// who is the primary?
							// declare the primary track (even though we may end up not using
							// it)
							WatchableList thePrimary = null;

							// is this a relative calculation?
							if (theHolder._isRelative)
							{
								// retrieve the necessary input data
								thePrimary = getPrimary(subjects);
							}

							// ////////////////////////////////////////////////
							// sort out the title
							// ////////////////////////////////////////////////
							// get the title to use
							String theTitle = myOperation.getTitle() + " vs Time plot";

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

							// and the plot itself
							String plotId = "org.mwc.cmap.xyplot.views.XYPlotView";
							page.showView(plotId, theTitle, IWorkbenchPage.VIEW_ACTIVATE);

							// put our subjects into a vector
							Vector<WatchableList> theTracks = new Vector<WatchableList>(0, 1);
							for (int i = 0; i < subjects.length; i++)
							{
								Editable thisS = subjects[i];
								theTracks.add((WatchableList) thisS);
							}

							// ///////////////////////////////////
							// NOW for the time range
							// ///////////////////////////////////

							// have a go at determining the plot id
							TimeProvider tp = (TimeProvider) editor
									.getAdapter(TimeProvider.class);
							String thePlotId = null;
							if (tp != null)
							{
								thePlotId = tp.getId();
							}

							IAdaptable timeC = (IAdaptable) timeRef.getView(true);

							// that's it, now get the data
							TimePeriod period = (TimePeriod) timeC
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
								String title = "XY Plot";
								String message = "No time period has been selected.\nPlease select start/stop time from the Time Controller";
								MessageDialog.openError(Display.getCurrent().getActiveShell(),
										title, message);
								return;
							}

							// aah. does the primary track have it's own time period?
							if (thePrimary.getStartDTG() != null)
								startTime = thePrimary.getStartDTG();

							if (thePrimary.getEndDTG() != null)
								endTime = thePrimary.getEndDTG();

							// right, now for the data
							AbstractSeriesDataset ds = ShowTimeVariablePlot3.getDataSeries(
									thePrimary, theHolder, theTracks, startTime, endTime, null);

							// ok, try to retrieve the view
							IViewReference plotRef = page.findViewReference(plotId, theTitle);
							XYPlotView plotter = (XYPlotView) plotRef.getView(true);
							plotter.showPlot(theTitle, ds, myOperation.toString() + " ("
									+ myOperation.getUnits() + ")", theHolder._theFormatter,
									thePlotId);
						}
						catch (PartInitException e)
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

	protected WatchableList getPrimary(Editable[] subjects)
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
				Editable thisE = (Editable) subjects[i];
				labels[i] = thisE.toString();
				values[i] = thisE;
			}

			GetSelection sel = new GetSelection("Select primary",
					"Which is the primary track", labels, values);
			int selection = sel.open();
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