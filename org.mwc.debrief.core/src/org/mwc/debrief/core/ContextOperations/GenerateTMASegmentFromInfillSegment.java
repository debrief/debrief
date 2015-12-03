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
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

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

	private static class TMAfromInfill extends CMAPOperation
	{

		private final Layers _layers;
		private final double _courseDegs;
		private final WorldSpeed _speed;
		private final TrackWrapper _solution;
		private final TimePeriod _period;
		private final DynamicInfillSegment _infill;
		private AbsoluteTMASegment _newSegment;

		public TMAfromInfill(TrackWrapper solution, TimePeriod requestedPeriod,
				DynamicInfillSegment infill,
				final Layers theLayers, final double courseDegs, final WorldSpeed speed)
		{
			super("Create TMA solution to replace infill points");
			_solution = solution;
			_period = requestedPeriod;
			_layers = theLayers;
			_courseDegs = courseDegs;
			_speed = speed;
			_infill = infill;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			Watchable[] matches = _solution.getNearestTo(_period.getStartDTG());
			if (matches.length != 1)
			{
				CorePlugin.logError(Status.ERROR,
						"Not possible to find host location at " + _period.getStartDTG(),
						null);
				return Status.CANCEL_STATUS;
			}
			else
			{
				WorldLocation startPoint = new WorldLocation(matches[0].getLocation());

				_newSegment = new AbsoluteTMASegment(_courseDegs, _speed, startPoint,
						_period.getStartDTG(), _period.getEndDTG());

				// ok, go for it
				return redo(monitor, info);
			}
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			return insertNewSeg(_infill, _newSegment, _infill.getWrapper());
		}

		private IStatus insertNewSeg(TrackSegment toRemove, TrackSegment toInsert,
				TrackWrapper solution)
		{
			// remove the infill
			solution.removeElement(toRemove);

			// add the new TMA segment
			solution.add(toInsert);

			// sorted, do the update
			_layers.fireExtended(toInsert, solution);

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			return insertNewSeg(_newSegment, _infill, _newSegment.getWrapper());
		}

		@Override
		public boolean canExecute()
		{
			return true;
		}

		@Override
		public boolean canRedo()
		{
			return true;
		}

		@Override
		public boolean canUndo()
		{
			return true;
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
		TimePeriod requestedPeriod = null;
		TrackWrapper hostTrack = null;
		DynamicInfillSegment infill = null;

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

					TrackSegment parentSegment = segments.getSegmentFor(fix
							.getDateTimeGroup().getDate().getTime());

					// is this first leg a TMA segment?
					if (parentSegment instanceof DynamicInfillSegment)
					{
						// initialise ourselves
						if (requestedPeriod == null)
						{
							requestedPeriod = new TimePeriod.BaseTimePeriod(
									fix.getDateTimeGroup(), fix.getDateTimeGroup());
							hostTrack = parentSegment.getWrapper();
						}
						else
						{
							requestedPeriod.extend(fix.getDateTimeGroup());
						}

						// have we already found an infill?
						if (infill == null)
						{
							infill = (DynamicInfillSegment) parentSegment;
						}
						else
						{
							// yes, is it the same one as this new one?
							if (infill != parentSegment)
							{
								CorePlugin.logError(Status.WARNING,
										"We need all positions to be in the same infill", null);
								return;
							}
						}
					}
					else
					{
						CorePlugin.logError(Status.WARNING,
								"We only allow positions from infill segments", null);
						return;
					}
				}
				else
				{
					break;
				}

			}

			// are we good to go?
			if (requestedPeriod != null)
			{
				final TrackWrapper fHost = hostTrack;
				final TimePeriod fPeriod = requestedPeriod;
				final DynamicInfillSegment fInfill = infill;
				
				// cool wrap it in an action.
				_myAction = new Action(
						"Generate TMA solution for times at selected positions")
				{

					@Override
					public void run()
					{

						// get the supporting data
						final TMAFromSensorWizard wizard = new TMAFromSensorWizard(45d,
								new WorldDistance(5, WorldDistance.NM), DEFAULT_TARGET_COURSE,
								DEFAULT_TARGET_SPEED, false);
						final WizardDialog dialog = new WizardDialog(Display.getCurrent()
								.getActiveShell(), wizard);
						dialog.create();
						dialog.open();

						// did it work?
						if (dialog.getReturnCode() == WizardDialog.OK)
						{
							double courseDegs = 0;
							WorldSpeed speed = null;
							
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
							final IUndoableOperation convertToTrack1 = new TMAfromInfill(
									fHost, fPeriod, fInfill, theLayers, courseDegs, speed);

							// ok, stick it on the buffer
							runIt(convertToTrack1);

						}
						else
							System.err.println("user cancelled");

					}
				};
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
