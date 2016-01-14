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
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage.SolutionDataItem;
import org.mwc.debrief.core.wizards.s2r.TMAFromSensorWizard;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * @author ian.mayo
 * 
 */
public class GenerateTMASegmentFromCuts implements
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

    @SuppressWarnings("deprecation")
    private TrackWrapper getLongerTrack()
    {
    	final TrackWrapper tw = new TrackWrapper();
    
    	final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
    	WorldLocation lastLoc = loc_1;
    	
    	for(int i=0;i<50;i++)
    	{
    	  long thisTime = new Date(2016, 1, 14, 12, i, 0).getTime();
        final FixWrapper fw = new FixWrapper(new Fix(new HiResDate(thisTime),
            lastLoc.add(getVector(25, 0)), MWC.Algorithms.Conversions.Degs2Rads(0),
            110));
        fw.setLabel("fw1");
        tw.addFix(fw);
        
        lastLoc = new WorldLocation(fw.getLocation());
    	}
    
      final SensorWrapper swa = new SensorWrapper("title one");
      tw.add(swa);
      swa.setSensorOffset(new ArrayLength(-400));
    
      for(int i=0;i<50;i+=3)
      {
        long thisTime = new Date(2016, 1, 14, 12, i, 30).getTime();
        final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
            new HiResDate(thisTime), null, null, null, null, null, 0, null);
        swa.add(scwa1);
      }
    
    	return tw;
    }

    public void testSplitWithOffset() throws ExecutionException
    {
      TrackWrapper tw = getLongerTrack();
      
      assertNotNull(tw);
      
      // get the sensor data
      SensorWrapper sw = (SensorWrapper) tw.getSensors().elements().nextElement();
      
      assertNotNull(sw);
      
      // create a list of cuts (to simulate the selection)
      SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
      Enumeration<Editable> numer = sw.elements();
      int ctr=0;
      while (numer.hasMoreElements())
      {
        SensorContactWrapper cut = (SensorContactWrapper) numer.nextElement();
        items[ctr++] = cut;
      }
      
      Layers theLayers = new Layers();
      WorldVector worldOffset= new WorldVector(Math.PI, 0.002, 0);
      double tgtCourse = 0;
      WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);
      
      // ok, generate the target track
      CMAPOperation op = new TMAfromCuts(items, theLayers, worldOffset, tgtCourse, tgtSpeed);
      
      // and run it
      op.execute(null, null);
      
      assertEquals("has new data", 1, theLayers.size());

      TrackWrapper sol = (TrackWrapper) theLayers.elementAt(0);
      assertNotNull("new layer not found", sol);
      
      // ok, now try to split it
      assertEquals("only has one segment", 1, sol.getSegments().size());

      RelativeTMASegment seg = (RelativeTMASegment) sol.getSegments().elements().nextElement();

      assertNotNull("new seg not found", seg);
      
      // ok, and we split it.
      int ctr2 = 0;
      FixWrapper beforeF = null;
      FixWrapper afterF = null;
      Enumeration<Editable> eF = seg.elements();
      while (eF.hasMoreElements())
      {
        FixWrapper fix = (FixWrapper) eF.nextElement();
        ctr2++;
        if(ctr2 > seg.size() / 2)
        {
          if(beforeF == null)
          {
            beforeF = fix;
          }
          else
          {
            afterF = fix;
            break;
          }
        }
      }
      
      assertNotNull("fix not found", beforeF);

      // ok, what's the time offset 
      WorldLocation afterBeforeSplit = afterF.getLocation();
      
      // ok, time to split
      SubjectAction[] actions = beforeF.getInfo().getUndoableActions();
      SubjectAction doSplit = actions[1];
      doSplit.execute(beforeF);
      
      // ok, have another look
      assertEquals("now has two segments", 2, sol.getSegments().size());
      Enumeration<Editable> aNum = sol.getSegments().elements();
      aNum.nextElement();
      TrackSegment afterSeg = (TrackSegment) aNum.nextElement();
      WorldLocation locAfterSplit = afterSeg.getTrackStart();
      
      assertEquals("origin remains valid", afterBeforeSplit, locAfterSplit);
      
      // hey, try the undo
      doSplit.undo(beforeF);

      assertEquals("now has one segment again", 1, sol.getSegments().size());
      
      // hey, try the undo
      doSplit.execute(beforeF);
      assertEquals("now has two segments", 2, sol.getSegments().size());

      aNum = sol.getSegments().elements();
      aNum.nextElement();
      afterSeg = (TrackSegment) aNum.nextElement();
      locAfterSplit = afterSeg.getTrackStart();
      assertEquals("origin remains valid, after undo/redo", afterBeforeSplit, locAfterSplit);


    }

    /**
     * @return
     */
    private WorldVector getVector(final double courseDegs, final double distM)
    {
    	return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
    			new WorldDistance(distM, WorldDistance.METRES), null);
    }
	}

	private static class TMAfromCuts extends CMAPOperation
	{

		private final Layers _layers;
		private final SensorContactWrapper[] _items;
		private TrackWrapper _newTrack;
		private final double _courseDegs;
		private final WorldSpeed _speed;
		private final WorldVector _offset;

		public TMAfromCuts(final SensorContactWrapper[] items,
				final Layers theLayers, final WorldVector offset,
				final double courseDegs, final WorldSpeed speed)
		{
			super("Create TMA solution from sensor cuts");
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
			// create it, then
			final TrackSegment seg = new RelativeTMASegment(_items, _offset, _speed,
					_courseDegs, _layers);

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

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			_layers.addThisLayerAllowDuplication(_newTrack);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
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

		// so, see if it's something we can do business with
		if (subjects.length == 1)
		{
			// ok, do I know how to create a TMA segment from this?
			final Editable onlyOne = subjects[0];
			if (onlyOne instanceof SensorWrapper)
			{
				final SensorWrapper sw = (SensorWrapper) onlyOne;
				// cool, go for it
				final Vector<SensorContactWrapper> wraps = new Vector<SensorContactWrapper>();
				final Enumeration<Editable> numer = sw.elements();
				while (numer.hasMoreElements())
				{
					wraps.add((SensorContactWrapper) numer.nextElement());
				}
				if (!wraps.isEmpty())
				{
					final SensorContactWrapper[] items = new SensorContactWrapper[wraps
							.size()];
					final SensorContactWrapper[] finalItems = wraps.toArray(items);
					final SensorContactWrapper firstContact = finalItems[0];

					// cool wrap it in an action.
					_myAction = new Action("Generate TMA solution from all cuts")
					{
						@Override
						public void run()
						{
							// get ready for the supporting data (using selected sensor data,
							// if
							// we can)
							WorldVector res = null;
							double courseDegs = 0;
							WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);

							// just check we have some kind of range
							WorldDistance theDist = firstContact.getRange();
							if (theDist == null)
								theDist = new WorldDistance(6, WorldDistance.NM);

							// get the supporting data
							final TMAFromSensorWizard wizard = new TMAFromSensorWizard(
									firstContact.getBearing(), theDist, DEFAULT_TARGET_COURSE,
									DEFAULT_TARGET_SPEED);
							final WizardDialog dialog = new WizardDialog(Display.getCurrent()
									.getActiveShell(), wizard);
							dialog.create();
							dialog.open();

							// did it work?
							if (dialog.getReturnCode() == WizardDialog.OK)
							{

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
								final IUndoableOperation convertToTrack1 = new TMAfromCuts(
										finalItems, theLayers, res, courseDegs, speed);

								// ok, stick it on the buffer
								runIt(convertToTrack1);

							}
							else
								System.err.println("user cancelled");
						}
					};
				}
				; // whether there are any cuts for this sensor
			}
		}
		else
		{
			// so, it's a number of items, Are they all sensor contact wrappers
			boolean allGood = true;
			final SensorContactWrapper[] items = new SensorContactWrapper[subjects.length];
			for (int i = 0; i < subjects.length; i++)
			{
				final Editable editable = subjects[i];
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
							final TMAFromSensorWizard wizard = new TMAFromSensorWizard(
									firstContact.getBearing(), firstContact.getRange(),
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
								final IUndoableOperation convertToTrack1 = new TMAfromCuts(
										items, theLayers, res, courseDegs, speed);

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
