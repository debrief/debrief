package org.mwc.debrief.satc_interface.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.mwc.debrief.satc_interface.data.wrappers.ContributionWrapper;
import org.mwc.debrief.satc_interface.utilities.conversions;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution.ROrigin;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class CreateSolutionFromSensorData implements
		RightClickContextItemGenerator
{

	@Override
	public void generate(IMenuManager parent, final Layers theLayers,
			Layer[] parentLayers, Editable[] subjects)
	{
		ArrayList<SensorContactWrapper> validCuts = null;

		IMenuManager thisMenu = new MenuManager("SemiAuto TMA");
		parent.add(thisMenu);

		for (int i = 0; i < subjects.length; i++)
		{
			Editable thisItem = subjects[i];
			if (thisItem instanceof SensorContactWrapper)
			{
				if (validCuts == null)
					validCuts = new ArrayList<SensorContactWrapper>();

				validCuts.add((SensorContactWrapper) thisItem);
			}
			else if (thisItem instanceof SensorWrapper)
			{
				if (validCuts == null)
					validCuts = new ArrayList<SensorContactWrapper>();

				SensorWrapper sw = (SensorWrapper) thisItem;
				Enumeration<Editable> cuts = sw.elements();
				while (cuts.hasMoreElements())
				{
					validCuts.add((SensorContactWrapper) cuts.nextElement());
				}
			}
			else if (thisItem instanceof ContributionWrapper)
			{
				if (subjects.length == 1)
				{
					ContributionWrapper cw = (ContributionWrapper) thisItem;
					BaseContribution theCont = cw.getContribution();
					String verb1 = "";

					SATC_Solution solution = (SATC_Solution) parentLayers[0];
					String actionTitle = "Add new contribution";

					parent.add(new DoIt(verb1 + "Speed Forecast for period covering "
							+ theCont.getName(), new SpeedForecastContributionFromCuts(
							solution, actionTitle, theLayers, theCont)));
					parent.add(new DoIt(verb1 + "Course Forecast for period covering "
							+ theCont.getName(), new CourseForecastContributionFromCuts(
							solution, actionTitle, theLayers, theCont)));
					parent.add(new DoIt(verb1 + "Straight Leg for period covering "
							+ theCont.getName(), new StraightLegForecastContributionFromCuts(
							solution, actionTitle, theLayers, theCont)));
				}
			}
		}

		// ok, is it worth going for?
		if (validCuts != null)
		{
			final String title;
			if (validCuts.size() > 1)
				title = "sensor cuts";
			else
				title = "sensor cut";

			// right,stick in a separator
			thisMenu.add(new Separator());

			// see if there's an existing solution in there.
			SATC_Solution[] existingSolutions = findExistingSolutionsIn(theLayers);

			if ((existingSolutions != null) && (existingSolutions.length > 0))
			{
				for (int i = 0; i < existingSolutions.length; i++)
				{
					final SATC_Solution layer = existingSolutions[i];

					// create a top level menu item
					MenuManager thisD = new MenuManager("Add to " + layer.getName());
					thisMenu.add(thisD);

					// add the child items
					addItemsTo(layer, thisD, validCuts, title, null, "");
				}
			}

			// and the new solution
			MenuManager thisD = new MenuManager("Create new solution");
			thisMenu.add(thisD);

			// add the child items
			addItemsTo(null, thisD, validCuts, title, theLayers, "Using ");

		}
	}

	protected void addItemsTo(final SATC_Solution solution,
			final MenuManager parent,
			final ArrayList<SensorContactWrapper> validItems, final String title,
			Layers layers, String verb1)
	{

		String actionTitle = "Add new contribution";

		parent.add(new DoIt(verb1 + "Bearing Measurement from " + title,
				new BearingMeasurementContributionFromCuts(solution, actionTitle,
						layers, validItems)));
		parent.add(new DoIt(verb1 + "Speed Forecast for period covering " + title,
				new SpeedForecastContributionFromCuts(solution, actionTitle, layers,
						validItems)));
		parent.add(new DoIt(verb1 + "Range Forecast for period covering " + title,
				new RangeForecastContributionFromCuts(solution, actionTitle, layers,
						validItems)));
		parent.add(new DoIt(verb1 + "Course Forecast for period covering " + title,
				new CourseForecastContributionFromCuts(solution, actionTitle, layers,
						validItems)));
		parent.add(new DoIt(verb1 + "Straight Leg for period covering " + title,
				new StraightLegForecastContributionFromCuts(solution, actionTitle,
						layers, validItems)));
	}

	protected static class DoIt extends Action
	{
		private final IUndoableOperation _myOperation;

		DoIt(String title, IUndoableOperation operation)
		{
			super(title);
			_myOperation = operation;
		}

		@Override
		public void run()
		{
			CorePlugin.run(_myOperation);
		}

	}

	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 * 
	 * @param operation
	 */
	protected void runIt(IUndoableOperation operation)
	{
		CorePlugin.run(operation);
	}

	private SATC_Solution[] findExistingSolutionsIn(Layers theLayers)
	{
		ArrayList<SATC_Solution> res = null;

		Enumeration<Editable> iter = theLayers.elements();
		while (iter.hasMoreElements())
		{
			Editable thisL = iter.nextElement();
			if (thisL instanceof SATC_Solution)
			{
				if (res == null)
					res = new ArrayList<SATC_Solution>();

				res.add((SATC_Solution) thisL);
			}
		}

		SATC_Solution[] list = null;
		if (res != null)
			list = res.toArray(new SATC_Solution[]
			{});

		return list;
	}

	private class BearingMeasurementContributionFromCuts extends
			CoreSolutionFromCuts
	{
		private ArrayList<SensorContactWrapper> _validCuts;

		public BearingMeasurementContributionFromCuts(
				SATC_Solution existingSolution, String title, Layers theLayers,
				ArrayList<SensorContactWrapper> validCuts)
		{
			super(existingSolution, title, theLayers, new TimePeriod.BaseTimePeriod(
					new HiResDate(validCuts.get(0).getDTG().getDate()), new HiResDate(
							validCuts.get(validCuts.size() - 1).getDTG())));
			_validCuts = validCuts;
		}

		protected BearingMeasurementContribution createContribution(String contName)
		{
			// ok, now collate the contriubtion
			final BearingMeasurementContribution bmc = new BearingMeasurementContribution();
			bmc.setName(contName);
			bmc.setBearingError(Math.toRadians(3.0));
			bmc.setAutoDetect(false);

			// add the bearing data
			Iterator<SensorContactWrapper> iter = _validCuts.iterator();
			while (iter.hasNext())
			{
				final SensorContactWrapper scw = (SensorContactWrapper) iter.next();
				WorldLocation theOrigin = scw.getOrigin();
				GeoPoint loc;

				if (theOrigin == null)
					theOrigin = scw.getCalculatedOrigin(scw.getSensor().getHost());

				loc = conversions.toPoint(theOrigin);

				double brg = Math.toRadians(scw.getBearing());
				Date date = scw.getDTG().getDate();
				Double theRange = null;
				if (scw.getRange() != null)
					theRange = scw.getRange().getValueIn(WorldDistance.METRES);

				final BMeasurement thisM = new BMeasurement(loc, brg, date, theRange);

				// give it the respective color
				thisM.setColor(scw.getColor());

				// ok, store it.
				bmc.addMeasurement(thisM);
			}
			return bmc;
		}

	}

	private class SpeedForecastContributionFromCuts extends
			ForecastContributionFromCuts
	{
		public SpeedForecastContributionFromCuts(SATC_Solution existingSolution,
				String title, Layers theLayers,
				ArrayList<SensorContactWrapper> validCuts)
		{
			super(existingSolution, title, theLayers, validCuts);
		}

		public SpeedForecastContributionFromCuts(SATC_Solution existingSolution,
				String title, Layers theLayers, BaseContribution oldCont)
		{
			super(existingSolution, title, theLayers, oldCont);
		}

		@Override
		protected BaseContribution getContribution()
		{
			return new SpeedForecastContribution();
		}

	}

	private class CourseForecastContributionFromCuts extends
			ForecastContributionFromCuts
	{
		public CourseForecastContributionFromCuts(SATC_Solution existingSolution,
				String title, Layers theLayers,
				ArrayList<SensorContactWrapper> validCuts)
		{
			super(existingSolution, title, theLayers, validCuts);
		}

		public CourseForecastContributionFromCuts(SATC_Solution existingSolution,
				String title, Layers theLayers, BaseContribution oldCont)
		{
			super(existingSolution, title, theLayers, oldCont);
		}

		@Override
		protected BaseContribution getContribution()
		{
			return new CourseForecastContribution();
		}

	}

	private class RangeForecastContributionFromCuts extends
			ForecastContributionFromCuts
	{
		private ArrayList<SensorContactWrapper> _validCuts;

		public RangeForecastContributionFromCuts(SATC_Solution existingSolution,
				String title, Layers theLayers,
				ArrayList<SensorContactWrapper> validCuts)
		{
			super(existingSolution, title, theLayers, validCuts);
			_validCuts = validCuts;
		}

		@Override
		protected BaseContribution getContribution()
		{
			RangeForecastContribution rfc = new RangeForecastContribution();

			// now set the range origins

			// add the bearing data
			Iterator<SensorContactWrapper> iter = _validCuts.iterator();
			while (iter.hasNext())
			{
				final SensorContactWrapper scw = (SensorContactWrapper) iter.next();
				WorldLocation theOrigin = scw.getOrigin();
				GeoPoint loc;

				if (theOrigin == null)
					theOrigin = scw.getCalculatedOrigin(scw.getSensor().getHost());

				loc = conversions.toPoint(theOrigin);

				RangeForecastContribution.ROrigin newR = new ROrigin(loc, scw.getDTG()
						.getDate());
				rfc.addThis(newR);

			}

			return rfc;
		}

	}

	private class StraightLegForecastContributionFromCuts extends
			ForecastContributionFromCuts
	{
		public StraightLegForecastContributionFromCuts(
				SATC_Solution existingSolution, String title, Layers theLayers,
				ArrayList<SensorContactWrapper> validCuts)
		{
			super(existingSolution, title, theLayers, validCuts);
		}

		public StraightLegForecastContributionFromCuts(
				SATC_Solution existingSolution, String title, Layers theLayers,
				BaseContribution oldCont)
		{
			super(existingSolution, title, theLayers, oldCont);
		}

		@Override
		protected BaseContribution getContribution()
		{
			return new StraightLegForecastContribution();
		}

	}

	private abstract class ForecastContributionFromCuts extends
			CoreSolutionFromCuts
	{
		public ForecastContributionFromCuts(SATC_Solution existingSolution,
				String title, Layers theLayers,
				ArrayList<SensorContactWrapper> validCuts)
		{
			super(existingSolution, title, theLayers, new TimePeriod.BaseTimePeriod(
					new HiResDate(validCuts.get(0).getDTG().getDate()), new HiResDate(
							validCuts.get(validCuts.size() - 1).getDTG())));
		}

		public ForecastContributionFromCuts(SATC_Solution existingSolution,
				String title, Layers theLayers, BaseContribution oldCont)
		{
			super(existingSolution, title, theLayers, new TimePeriod.BaseTimePeriod(
					new HiResDate(oldCont.getStartDate()), new HiResDate(
							oldCont.getFinishDate())));
		}

		protected final BaseContribution createContribution(String contName)
		{
			// ok, now collate the contriubtion
			BaseContribution bmc = getContribution();

			// ok, do some formatting
			bmc.setName(contName);

			// and the dates
			bmc.setStartDate(thePeriod.getStartDTG().getDate());
			bmc.setFinishDate(thePeriod.getEndDTG().getDate());

			return bmc;
		}

		abstract protected BaseContribution getContribution();

	}

	private abstract class CoreSolutionFromCuts extends CMAPOperation
	{

		private SATC_Solution _targetSolution;
		private final Layers _theLayers;
		protected TimePeriod thePeriod;

		public CoreSolutionFromCuts(SATC_Solution existingSolution, String title,
				Layers theLayers, BaseContribution existingContribution)
		{
			super(title);
			_targetSolution = existingSolution;
			_theLayers = theLayers;
			thePeriod = new TimePeriod.BaseTimePeriod(new HiResDate(
					existingContribution.getStartDate()), new HiResDate(
					existingContribution.getFinishDate()));

		}

		public CoreSolutionFromCuts(SATC_Solution existingSolution, String title,
				Layers theLayers, TimePeriod thePeriod)
		{
			super(title);
			_targetSolution = existingSolution;
			_theLayers = theLayers;
			this.thePeriod = thePeriod;
		}

		public String getDefaultSolutionName()
		{ // grab a name
			Date firstCutDate = thePeriod.getStartDTG().getDate();
			String formattedName = FormatRNDateTime.toString(firstCutDate.getTime());
			return formattedName;
		}

		@Override
		public boolean canUndo()
		{
			return false;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
		{

			// ok, do we have an existing solution
			if (_targetSolution == null)
			{
				String solutionName = getDefaultSolutionName();

				// create a new solution

				final ISolversManager solvMgr = SATC_Activator.getDefault().getService(
						ISolversManager.class, true);
				final ISolver newSolution = solvMgr.createSolver(solutionName);

				_targetSolution = new SATC_Solution(newSolution);
				newSolution.setAutoGenerateSolutions(false);
				_theLayers.addThisLayer(_targetSolution);

				// ok, give it the default contributions
				initialiseSolver();
			}

			// grab a name
			// create input box dialog
			InputDialog inp = new InputDialog(Display.getCurrent().getActiveShell(),
					"New contribution", "What is the name of this contribution",
					"name here", null);

			// did he cancel?
			if (inp.open() == InputDialog.OK)
			{
				// get the results
				String contName = inp.getValue();

				// ok = now get our specific contribution
				BaseContribution bmc = createContribution(contName);

				// and store it - if it worked
				if (bmc != null)
					_targetSolution.addContribution(bmc);
			}

			return Status.OK_STATUS;
		}

		private void initialiseSolver()
		{
			IContributions theConts = _targetSolution.getSolver().getContributions();
			theConts.addContribution(new LocationAnalysisContribution());
			theConts.addContribution(new SpeedAnalysisContribution());
			theConts.addContribution(new CourseAnalysisContribution());
		}

		abstract protected BaseContribution createContribution(String contName);

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// duh, ignore
			return null;
		}
	}

}
