package org.mwc.cmap.xyplot;

import java.util.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.mwc.cmap.TimeController.views.TimeController;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.xyplot.views.XYPlotView;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot2;
import Debrief.Tools.FilterOperations.ShowTimeVariablePlot2.CalculationHolder;
import Debrief.Tools.Tote.*;
import Debrief.Tools.Tote.Calculations.*;
import MWC.Algorithms.Plotting.*;
import MWC.GUI.*;
import MWC.GenericData.*;

import com.jrefinery.data.AbstractDataset;

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
			_theOperations.addElement(new ShowTimeVariablePlot2.CalculationHolder(
					new depthCalc(), new DepthFormatter(), false, 0));
			_theOperations.addElement(new ShowTimeVariablePlot2.CalculationHolder(
					new courseCalc(), new CourseFormatter(), false, 360));
			_theOperations.addElement(new ShowTimeVariablePlot2.CalculationHolder(
					new speedCalc(), null, false, 0));
			_theOperations.addElement(new ShowTimeVariablePlot2.CalculationHolder(
					new rangeCalc(), null, true, 0));
			_theOperations.addElement(new ShowTimeVariablePlot2.CalculationHolder(
					new bearingCalc(), new CourseFormatter(), true, 360));
			_theOperations.addElement(new ShowTimeVariablePlot2.CalculationHolder(
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
			_theOperations.addElement(new ShowTimeVariablePlot2.CalculationHolder(
					new relBearingCalc(), theFormatter, true, 180));
		}

		if (_pastSelections == null)
			_pastSelections = new HashMap<String, Integer>();
	}

	ShowTimeVariablePlot2.CalculationHolder getChoice()
	{

		// and create the title
		String[] choices = new String[_theOperations.size()];
		Object[] results = new Object[_theOperations.size()];
		for (int i = 0; i < _theOperations.size(); i++)
		{
			ShowTimeVariablePlot2.CalculationHolder thisC = (ShowTimeVariablePlot2.CalculationHolder) _theOperations
					.elementAt(i);
			choices[i] = thisC.toString();
			results[i] = thisC;
		}

		GetSelection dialog = new GetSelection("View time-variable plot",
				"Please select the attribute to view", choices, results);

		int selection = dialog.open();
		ShowTimeVariablePlot2.CalculationHolder res;
		if (selection == 0)
		{
			res = (ShowTimeVariablePlot2.CalculationHolder) dialog._result;

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
		public GetSelection(String dialogTitle, String dialogMessage, String[] titles,
				Object[] choices)
		{
			super(Display.getCurrent().getActiveShell(), dialogTitle, null, null,
					MessageDialog.QUESTION, new String[] { "OK", "Cancel" }, 1);
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
	public void generate(IMenuManager parent, Layers theLayers, Layer[] parentLayers,
			final Editable[] subjects)
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
				String txt = "Sorry, not all items are suitable data-sources for an xy plot";
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "XY Plot",
						txt);
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
								MessageDialog.openError(Display.getCurrent().getActiveShell(), title,
										message);
								return;
							}

							TimeController timer = (TimeController) timeRef.getView(true);

							// that's it, now get the data
							TimePeriod period = timer.getPeriod();
							startTime = period.getStartDTG();
							endTime = period.getEndDTG();

							if ((startTime.greaterThan(endTime)) || (startTime.equals(endTime)))
							{
								String title = "XY Plot";
								String message = "No time period has been selected.\nPlease select start/stop time from the Time Controller";
								MessageDialog.openError(Display.getCurrent().getActiveShell(), title,
										message);
								return;
							}

							// ok, sort out what we're plotting
							// find out what the user wants to view
							ShowTimeVariablePlot2.CalculationHolder theHolder = getChoice();
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
								// if it's relative, we use the primary track name in the title
								theTitle = thePrimary.getName() + " " + theTitle;
							}

							// and the plot itself
							String plotId = "org.mwc.cmap.xyplot.views.XYPlotView";
							page.showView(plotId, theTitle, IWorkbenchPage.VIEW_ACTIVATE);

							// put our subjects into a vector
							Vector<WatchableList> theTracks = new Vector<WatchableList>(0, 1);
							for (int i = 0; i < subjects.length; i++)
							{
								Editable thisS = subjects[i];
								theTracks.add((WatchableList)thisS);
							}

							// right, now for the data
							AbstractDataset ds = ShowTimeVariablePlot2.getDataSeries(thePrimary,
									theHolder, theTracks, startTime, endTime, null);

							// ok, try to retrieve the view
							IViewReference plotRef = page.findViewReference(plotId, theTitle);
							XYPlotView plotter = (XYPlotView) plotRef.getView(true);
							plotter.showPlot(theTitle, ds, myOperation.toString() + " ("
									+ myOperation.getUnits() + ")", theHolder._theFormatter);
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

			GetSelection sel = new GetSelection("Select primary", "Which is the primary track",
					labels, values);
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