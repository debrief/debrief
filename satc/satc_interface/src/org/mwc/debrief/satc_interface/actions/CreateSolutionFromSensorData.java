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
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.satc_interface.data.ContributionWrapper;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.mwc.debrief.satc_interface.utilities.conversions;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldDistance;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;

public class CreateSolutionFromSensorData implements
		RightClickContextItemGenerator
{

	@Override
	public void generate(IMenuManager parent, final Layers theLayers,
			Layer[] parentLayers, Editable[] subjects)
	{
		ArrayList<SensorContactWrapper> validCuts = null;

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
				title = "Add sensor cuts";
			else
				title = "Add sensor cut";

			// right,stick in a separator
			parent.add(new Separator());

			// see if there's an existing solution in there.
			SATC_Solution[] existingSolutions = findExistingSolutionsIn(theLayers);

			// take a safe copy of the cuts
			final ArrayList<SensorContactWrapper> finalCuts = validCuts;

			if ((existingSolutions != null) && (existingSolutions.length > 0))
			{
				for (int i = 0; i < existingSolutions.length; i++)
				{
					final SATC_Solution layer = existingSolutions[i];

					// ok, go for it.
					final String title2 = title + " to existing solution:"
							+ layer.getName();

					// yes, create the action
					Action addToExisting = new Action(title2)
					{
						public void run()
						{
							// sort it out as an operation
							IUndoableOperation convertToTrack1 = new NewSolutionFromCuts(
									layer, title2, finalCuts, null);

							// ok, stick it on the buffer
							runIt(convertToTrack1);
						}
					};

					parent.add(addToExisting);

				}
			}

			// yes, create the action
			// ok, go for it.
			final String title2 = title + " to new solution";
			Action addToNew = new Action(title2)
			{
				public void run()
				{
					
					// sort it out as an operation
					IUndoableOperation addToNewSolution = new NewSolutionFromCuts(null,
							title2, finalCuts, theLayers);

					// ok, stick it on the buffer
					runIt(addToNewSolution);
				}
			};

			// ok - flash up the menu item
			parent.add(addToNew);
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

	private static class NewSolutionFromCuts extends CMAPOperation
	{

		private SATC_Solution _targetSolution;
		private final ArrayList<SensorContactWrapper> _validCuts;
		private final Layers _theLayers;

		public NewSolutionFromCuts(SATC_Solution existingSolution, String title,
				ArrayList<SensorContactWrapper> validCuts, Layers theLayers)
		{
			super(title);
			_targetSolution = existingSolution;
			_validCuts = validCuts;
			_theLayers = theLayers;
		}

		@Override
		public boolean canUndo()
		{
			return false;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{

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

				// ok, do we have an existing solution
				if (_targetSolution == null)
				{
					_targetSolution = new SATC_Solution();
					_theLayers.addThisLayer(_targetSolution);
				}

				// ok, now collate the contriubtion
				BearingMeasurementContribution bmc = new BearingMeasurementContribution();
				bmc.setName(contName);

				// add the bearing data
				Iterator<SensorContactWrapper> iter = _validCuts.iterator();
				while (iter.hasNext())
				{
					SensorContactWrapper scw = (SensorContactWrapper) iter.next();
					GeoPoint loc = conversions.toPoint(scw.getOrigin());
					double brg = Math.toRadians(scw.getBearing());
					Date date = scw.getDTG().getDate();
					Double theRange = null;
					if (scw.getRange() != null)
						theRange = scw.getRange().getValueIn(WorldDistance.METRES);

					BMeasurement thisM = new BMeasurement(loc, brg, date, theRange);
					bmc.addThis(thisM);
				}

				_targetSolution.add(new ContributionWrapper(bmc));

			}

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// duh, ignore
			return null;
		}
	}

}
