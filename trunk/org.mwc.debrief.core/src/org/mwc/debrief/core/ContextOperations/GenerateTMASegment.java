/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.wizards.s2r.TMAFromSensorWizard;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.TrackWrapper_Support.TMASegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * @author ian.mayo
 * 
 */
public class GenerateTMASegment implements RightClickContextItemGenerator
{

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testIWork()
		{

		}
	}

	private static class TMAfromCuts extends CMAPOperation
	{

		private Layers _layers;
		private SensorContactWrapper[] _items;
		private TrackWrapper _newTrack;
		private int _course;
		private WorldSpeed _speed;
		private WorldVector _offset;

		public TMAfromCuts(SensorContactWrapper[] items, Layers theLayers)
		{
			super("Create TMA solution");
			_items = items;
			_layers = theLayers;
			_course = 12;
			_speed = new WorldSpeed(4, WorldSpeed.Kts);
			
			// sneaky use the bearing of the first cut for the bearing
			double brg = items[0].getBearing();
			brg = MWC.Algorithms.Conversions.Degs2Rads(brg);
			
			_offset = new WorldVector(brg, new WorldDistance(1, WorldDistance.NM),
					new WorldDistance(0, WorldDistance.DEGS));
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// create it, then
			TMASegment seg = new TMASegment(_items, _offset, _speed, _course);

			// now wrap it
			_newTrack = new TrackWrapper();
			_newTrack.setColor(Color.red);
			String tNow = "TMA_"
					+ FormatRNDateTime.toShortString(new Date().getTime());
			_newTrack.setName(tNow);
			_newTrack.add(seg);

			_layers.addThisLayer(_newTrack);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// forget about the new tracks
			_layers.removeThisLayer(_newTrack);
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{
		//
		Action _myAction = null;

		// so, see if it's something we can do business with
		if (subjects.length == 1)
		{
			// ok, do I know how to create a TMA segment from this?
			Editable onlyOne = subjects[0];
			if (onlyOne instanceof SensorWrapper)
			{
				SensorWrapper sw = (SensorWrapper) onlyOne;
				// cool, go for it
				Vector<SensorContactWrapper> wraps = new Vector<SensorContactWrapper>();
				Enumeration<Editable> numer = sw.elements();
				while (numer.hasMoreElements())
				{
					wraps.add((SensorContactWrapper) numer.nextElement());
				}
				SensorContactWrapper[] items = new SensorContactWrapper[wraps.size()];
				final SensorContactWrapper[] finalItems = wraps.toArray(items);

				// cool wrap it in an action.
				_myAction = new Action("Generate TMA solution from all cuts")
				{
					@Override
					public void run()
					{
						
						// get the supporting data
				    TMAFromSensorWizard wizard = new TMAFromSensorWizard();
		         WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		         dialog.create();
		         dialog.open();
		         
		      //   int res = dialog.getReturnCode();
		       //  System.err.println("res is:" + res);

						
						// ok, go for it.
						// sort it out as an operation
						IUndoableOperation convertToTrack1 = new TMAfromCuts(finalItems,
								theLayers);

						// ok, stick it on the buffer
						runIt(convertToTrack1);
					}
				};
			}
		}
		else
		{
			// so, it's a number of items, Are they all sensor contact wrappers
			boolean allGood = true;
			final SensorContactWrapper[] items = new SensorContactWrapper[subjects.length];
			for (int i = 0; i < subjects.length; i++)
			{
				Editable editable = subjects[i];
				if (editable instanceof SensorContactWrapper)
				{
					// cool, stick with it
					items[i] = (SensorContactWrapper) editable;
				}
				else
				{
					allGood = false;
					break;
				}

				// are we good to go?
				if (allGood)
				{
					// cool wrap it in an action.
					_myAction = new Action(
							"Generate TMA solution from all cust")
					{
						@Override
						public void run()
						{
							// ok, go for it.
							// sort it out as an operation
							IUndoableOperation convertToTrack1 = new TMAfromCuts(items,
									theLayers);

							// ok, stick it on the buffer
							runIt(convertToTrack1);
						}
					};
				}
			}

		}

		// go for it, or not...
		if (_myAction != null)
			parent.add(_myAction);

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
}
