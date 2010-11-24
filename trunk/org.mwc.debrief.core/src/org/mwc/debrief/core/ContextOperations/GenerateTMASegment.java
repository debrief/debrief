/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;
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
import org.mwc.debrief.core.wizards.core.RangeBearingPage;
import org.mwc.debrief.core.wizards.s2r.EnterSolutionPage;
import org.mwc.debrief.core.wizards.s2r.TMAFromSensorWizard;
import org.mwc.debrief.core.wizards.s2r.EnterSolutionPage.SolutionDataItem;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
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

	private static final WorldSpeed DEFAULT_TARGET_SPEED = new WorldSpeed(12,
			WorldSpeed.Kts);
	private static final double DEFAULT_TARGET_COURSE = 120d;

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
		private double _courseDegs;
		private WorldSpeed _speed;
		private WorldVector _offset;

		public TMAfromCuts(SensorContactWrapper[] items, Layers theLayers,
				WorldVector offset, double courseDegs, WorldSpeed speed)
		{
			super("Create TMA solution");
			_items = items;
			_layers = theLayers;
			_courseDegs = courseDegs;
			_speed = speed;
			_offset = offset;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// create it, then
			TrackSegment seg = new RelativeTMASegment(_items, _offset, _speed,
					_courseDegs, _layers);

			// now wrap it
			_newTrack = new TrackWrapper();
			_newTrack.setColor(Color.red);
			_newTrack.add(seg);
			String tNow = "TMA_"
					+ FormatRNDateTime.toString(_newTrack.getStartDTG().getDate()
							.getTime());
			_newTrack.setName(tNow);

			_layers.addThisLayerAllowDuplication(_newTrack);

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
				final SensorContactWrapper firstContact = finalItems[0];

				// cool wrap it in an action.
				_myAction = new Action("Generate TMA solution from all cuts")
				{
					@Override
					public void run()
					{
						// get ready for the supporting data (using selected sensor data, if
						// we can)
						WorldVector res = null;
						double courseDegs = 0;
						WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);

						// get the supporting data
						TMAFromSensorWizard wizard = new TMAFromSensorWizard(firstContact
								.getBearing(), firstContact.getRange(), DEFAULT_TARGET_COURSE,
								DEFAULT_TARGET_SPEED);
						WizardDialog dialog = new WizardDialog(Display.getCurrent()
								.getActiveShell(), wizard);
						dialog.create();
						dialog.open();

						// did it work?
						if (dialog.getReturnCode() == WizardDialog.OK)
						{

							RangeBearingPage offsetPage = (RangeBearingPage) wizard
									.getPage(RangeBearingPage.NAME);
							if (offsetPage != null)
							{
								if (offsetPage.isPageComplete())
								{
									res = new WorldVector(MWC.Algorithms.Conversions
											.Degs2Rads(offsetPage.getBearingDegs()), offsetPage
											.getRange(), null);
								}
							}

							EnterSolutionPage solutionPage = (EnterSolutionPage) wizard
									.getPage(EnterSolutionPage.NAME);
							if (solutionPage != null)
							{
								if (solutionPage.isPageComplete())
								{
									EnterSolutionPage.SolutionDataItem item = (SolutionDataItem) solutionPage
											.getEditable();
									courseDegs = item.getCourse();
									speed = item.getSpeed();
								}
							}

							// ok, go for it.
							// sort it out as an operation
							IUndoableOperation convertToTrack1 = new TMAfromCuts(finalItems,
									theLayers, res, courseDegs, speed);

							// ok, stick it on the buffer
							runIt(convertToTrack1);

						}
						else
							System.err.println("user cancelled");
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
					final SensorContactWrapper firstContact = items[0];

					// cool wrap it in an action.
					_myAction = new Action("Generate TMA solution from selected cuts")
					{

						@Override
						public void run()
						{

							// get the supporting data
							TMAFromSensorWizard wizard = new TMAFromSensorWizard(firstContact
									.getBearing(), firstContact.getRange(),
									DEFAULT_TARGET_COURSE, DEFAULT_TARGET_SPEED);
							WizardDialog dialog = new WizardDialog(Display.getCurrent()
									.getActiveShell(), wizard);
							dialog.create();
							dialog.open();

							// did it work?
							if (dialog.getReturnCode() == WizardDialog.OK)
							{
								WorldVector res = new WorldVector(0, new WorldDistance(5,
										WorldDistance.NM), null);
								double courseDegs = 0;
								WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);

								RangeBearingPage offsetPage = (RangeBearingPage) wizard
										.getPage(RangeBearingPage.NAME);
								if (offsetPage != null)
								{
									if (offsetPage.isPageComplete())
									{
										res = new WorldVector(MWC.Algorithms.Conversions
												.Degs2Rads(offsetPage.getBearingDegs()), offsetPage
												.getRange(), null);
									}
								}

								EnterSolutionPage solutionPage = (EnterSolutionPage) wizard
										.getPage(EnterSolutionPage.NAME);
								if (solutionPage != null)
								{
									if (solutionPage.isPageComplete())
									{
										EnterSolutionPage.SolutionDataItem item = (SolutionDataItem) solutionPage
												.getEditable();
										courseDegs = item.getCourse();
										speed = item.getSpeed();
									}
								}

								// ok, go for it.
								// sort it out as an operation
								IUndoableOperation convertToTrack1 = new TMAfromCuts(items,
										theLayers, res, courseDegs, speed);

								// ok, stick it on the buffer
								runIt(convertToTrack1);

							}
							else
								System.err.println("user cancelled");

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
