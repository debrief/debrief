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
import org.mwc.debrief.satc_interface.utilities.conversions;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;

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
		parent.add(new DoIt(verb1 + "Specify straight leg for period covering "
				+ title, new StraightLegForecastContributionFromCuts(solution,
				actionTitle, layers, validItems)));
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
		public BearingMeasurementContributionFromCuts(
				SATC_Solution existingSolution, String title, Layers theLayers,
				ArrayList<SensorContactWrapper> validCuts)
		{
			super(existingSolution, title, theLayers, validCuts);
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
					theRange = scw.getRange().getValueIn(WorldDistance.DEGS);

				final BMeasurement thisM = new BMeasurement(loc, brg, date, theRange);
				bmc.addThis(thisM);
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

		@Override
		protected BaseContribution getContribution()
		{
			return new SpeedForecastContribution();
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
			super(existingSolution, title, theLayers, validCuts);
		}

		protected final BaseContribution createContribution(String contName)
		{
			// ok, now collate the contriubtion
			BaseContribution bmc = getContribution();

			// ok, do some formatting
			bmc.setName(contName);

			// and the dates
			bmc.setStartDate(_validCuts.get(0).getDTG().getDate());
			bmc.setFinishDate(_validCuts.get(_validCuts.size() - 1).getDTG()
					.getDate());

			return bmc;
		}

		abstract protected BaseContribution getContribution();

	}

	private abstract class CoreSolutionFromCuts extends CMAPOperation
	{

		private SATC_Solution _targetSolution;
		private final Layers _theLayers;
		protected final ArrayList<SensorContactWrapper> _validCuts;

		public CoreSolutionFromCuts(SATC_Solution existingSolution, String title,
				Layers theLayers, ArrayList<SensorContactWrapper> validCuts)
		{
			super(title);
			_targetSolution = existingSolution;
			_theLayers = theLayers;
			_validCuts = validCuts;
		}

		public String getDefaultSolutionName()
		{ // grab a name
			Date firstCutDate = _validCuts.get(0).getDTG().getDate();
			String formattedName = FormatRNDateTime.toString(firstCutDate.getTime());
			return formattedName;
		}

		@Override
		public boolean canUndo()
		{
			return false;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{

			// ok, do we have an existing solution
			if (_targetSolution == null)
			{
				String solutionName = getDefaultSolutionName();
				_targetSolution = new SATC_Solution(solutionName);
				_theLayers.addThisLayer(_targetSolution);
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
