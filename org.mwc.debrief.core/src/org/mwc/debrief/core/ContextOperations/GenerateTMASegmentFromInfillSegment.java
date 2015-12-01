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
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;

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
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage.SolutionDataItem;
import org.mwc.debrief.core.wizards.s2r.TMAFromSensorWizard;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * @author ian.mayo
 * 
 */
public class GenerateTMASegmentFromInfillSegment implements
		RightClickContextItemGenerator
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

	private static class TMAfromPositions extends CMAPOperation
	{

		private final Layers _layers;
		private final FixWrapper[] _items;
		private TrackWrapper _newTrack;
		private final double _courseDegs;
		private final WorldSpeed _speed;
		private final WorldVector _offset;

		public TMAfromPositions(final FixWrapper[] items, WorldVector offset,
				final Layers theLayers, final double courseDegs, final WorldSpeed speed)
		{
			super("Create TMA solution");
			_items = items;
			_layers = theLayers;
			_courseDegs = courseDegs;
			_speed = speed;
			_offset = offset;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			HiResDate startTime = _items[0].getDTG();
			HiResDate endTime = _items[_items.length - 1].getDTG();
			WorldLocation startPoint = _items[0].getLocation().add(_offset);
			final TrackSegment seg = new AbsoluteTMASegment(_courseDegs, _speed,
					startPoint, startTime, endTime);

			// _items, _offset, _speed,
			// _courseDegs, _layers);

			// now wrap it
			_newTrack = new TrackWrapper();
			_newTrack.setColor(Color.red);
			_newTrack.add(seg);
			final String tNow = TrackSegment.TMA_LEADER
					+ FormatRNDateTime.toString(_newTrack.getStartDTG().getDate()
							.getTime());
			_newTrack.setName(tNow);

			_layers.addThisLayerAllowDuplication(_newTrack);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
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
			// hmm, let's not allow it for just one item
			// see the equivalent part of RelativeTMASegment if we wish to support
			// this
		}
		else
		{
			// so, it's a number of items, Are they all sensor contact wrappers
			boolean allGood = true;
			final FixWrapper[] items = new FixWrapper[subjects.length];
			for (int i = 0; i < subjects.length; i++)
			{
				final Editable editable = subjects[i];
				if (editable instanceof FixWrapper)
				{
					// hmm, we need to check if this fix is part of a solution. have a
					// look at the parent
					FixWrapper fix = (FixWrapper) editable;
					TrackWrapper track = fix.getTrackWrapper();
					SegmentList segments = track.getSegments();
					Editable first = segments.getData().iterator().next();
					
					// is this first leg a TMA segment?
					if (first instanceof CoreTMASegment)
					{
						// yes = in which case we won't offer to 
						// generate a track based upon it
						allGood = false;
					}
					else
					{
						// cool, stick with it
						items[i] = (FixWrapper) editable;
					}
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
							"Generate TMA solution from selected positions")
					{

						@Override
						public void run()
						{

							// get the supporting data
							final TMAFromSensorWizard wizard = new TMAFromSensorWizard(45d,
									new WorldDistance(5, WorldDistance.NM),
									DEFAULT_TARGET_COURSE, DEFAULT_TARGET_SPEED);
							final WizardDialog dialog = new WizardDialog(Display.getCurrent()
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

								final RangeBearingPage offsetPage = (RangeBearingPage) wizard
										.getPage(RangeBearingPage.NAME);
								if (offsetPage != null)
								{
									if (offsetPage.isPageComplete())
									{
										res = new WorldVector(
												MWC.Algorithms.Conversions.Degs2Rads(offsetPage
														.getBearingDegs()), offsetPage.getRange(), null);
									}
								}

								final EnterSolutionPage solutionPage = (EnterSolutionPage) wizard
										.getPage(EnterSolutionPage.NAME);
								if (solutionPage != null)
								{
									if (solutionPage.isPageComplete())
									{
										final EnterSolutionPage.SolutionDataItem item = (SolutionDataItem) solutionPage
												.getEditable();
										courseDegs = item.getCourse();
										speed = item.getSpeed();
									}
								}

								// ok, go for it.
								// sort it out as an operation
								final IUndoableOperation convertToTrack1 = new TMAfromPositions(
										items, res, theLayers, courseDegs, speed);

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
	protected void runIt(final IUndoableOperation operation)
	{
		CorePlugin.run(operation);
	}
}
