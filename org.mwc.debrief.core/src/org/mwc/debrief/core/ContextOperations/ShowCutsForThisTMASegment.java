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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;

/**
 * @author ian.mayo
 * 
 */
public class ShowCutsForThisTMASegment implements
    RightClickContextItemGenerator
{
  private static class ShowCutsOperation extends CMAPOperation
  {
    private final Layers _layers;
    private final Map<SensorWrapper, ArrayList<TimePeriod>> _periods;

    public ShowCutsOperation(final Layers theLayers,
        final Map<SensorWrapper, ArrayList<TimePeriod>> periods)
    {
      super("Show sensor cuts for selected segment(s)");
      _layers = theLayers;
      _periods = periods;
    }

    @Override
    public boolean canExecute()
    {
      return true;
    }

    @Override
    public boolean canRedo()
    {
      return false;
    }

    @Override
    public boolean canUndo()
    {
      return false;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      final Set<SensorWrapper> sensors = _periods.keySet();

      // start off by hiding all cuts, for all sensors (not just selected ones)
      Iterator<SensorWrapper> sIter = sensors.iterator();
      if (sIter.hasNext())
      {
        final SensorWrapper sensor = sIter.next();
        final TrackWrapper host = sensor.getHost();
        final BaseLayer allSensors = host.getSensors();
        final Enumeration<Editable> sEnum = allSensors.elements();
        while (sEnum.hasMoreElements())
        {
          final SensorWrapper thisS = (SensorWrapper) sEnum.nextElement();
          if (thisS.getVisible())
          {
            // ok, run through sensor cuts
            final Enumeration<Editable> cuts = thisS.elements();
            while (cuts.hasMoreElements())
            {
              final SensorContactWrapper contact =
                  (SensorContactWrapper) cuts.nextElement();
              if (contact.getVisible())
              {
                contact.setVisible(false);
              }
            }
          }
        }
      }

      // ok, now reveal the ones of interest
      sIter = sensors.iterator();
      while (sIter.hasNext())
      {
        final SensorWrapper sensorWrapper = sIter.next();

        // get the data
        final ArrayList<TimePeriod> list = _periods.get(sensorWrapper);

        final Enumeration<Editable> cIter = sensorWrapper.elements();
        while (cIter.hasMoreElements())
        {
          final SensorContactWrapper thisS =
              (SensorContactWrapper) cIter.nextElement();

          // loop through the periods
          final Iterator<TimePeriod> pIter = list.iterator();
          while (pIter.hasNext())
          {
            if (pIter.next().contains(thisS.getDTG()))
            {

              // double-check the parent
              if (!thisS.getSensor().getVisible())
              {
                thisS.getSensor().setVisible(true);
              }
              // and now for the sonar cut
              thisS.setVisible(true);

            }
          }
        }
      }

      // fire updated / extended
      sIter = sensors.iterator();
      while (sIter.hasNext())
      {
        final SensorWrapper sensor = sIter.next();
        _layers.fireReformatted(sensor.getHost());
      }
      // hmm, maybe not

      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
    }

  }

  private static int storeTMASegments(
      final Map<SensorWrapper, ArrayList<TimePeriod>> suitableSegments,
      final Editable[] subjects)
  {
    int matchCount = 0;

    for (int i = 0; i < subjects.length; i++)
    {
      final Editable editable = subjects[i];
      if (editable instanceof RelativeTMASegment)
      {
        final RelativeTMASegment seg = (RelativeTMASegment) editable;
        final SensorWrapper sensor = seg.getReferenceSensor();

        // ok, found one. increment the counter
        matchCount++;

        // do we have a list for this segment
        ArrayList<TimePeriod> list = suitableSegments.get(sensor);

        // nope, create one
        if (list == null)
        {
          list = new ArrayList<TimePeriod>();
          suitableSegments.put(sensor, list);
        }

        // ok, now add this period
        list.add(new TimePeriod.BaseTimePeriod(seg.getDTG_Start(), seg
            .getDTG_End()));
      }
    }

    return matchCount;
  }

  /**
   * @param parent
   * @param theLayers
   * @param parentLayers
   * @param subjects
   */
  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {

    int matchCount = 0;

    // so, see if it's something we can do business with
    if (subjects.length > 0)
    {
      final Map<SensorWrapper, ArrayList<TimePeriod>> suitableSegments =
          new HashMap<SensorWrapper, ArrayList<TimePeriod>>();

      // ok, start off with the TMA segments
      matchCount = storeTMASegments(suitableSegments, subjects);

      storeDynamicInfills(subjects, suitableSegments);

      // did it work?
      if (suitableSegments.size() > 0)
      {
        final String phrase;
        if (matchCount > 1)
        {
          phrase = "segments";
        }
        else
        {
          phrase = "segment";
        }

        // ok, generate the operation
        final IUndoableOperation action =
            getOperation(theLayers, suitableSegments);

        // and now wrap it in an action
        final Action doIt = new Action(getTitlePrefix() + phrase)
        {
          @Override
          public void run()
          {
            runIt(action);
          }
        };

        // ok, go for it
        parent.add(doIt);
      }
    }

  }

  /**
   * move the operation generation to a method, so it can be overwritten (in testing)
   * 
   * 
   * @param theLayers
   * @param suitableSegments
   * @param commonParent
   * @return
   */
  protected IUndoableOperation getOperation(final Layers theLayers,
      final Map<SensorWrapper, ArrayList<TimePeriod>> periods)
  {
    return new ShowCutsOperation(theLayers, periods);
  }

  protected String getTitlePrefix()
  {
    return "Only display sensor cuts for selected ";
  }

  /**
   * put the operation firer onto the undo history. We've refactored this into a separate method so
   * testing classes don't have to simulate the CorePlugin
   * 
   * @param operation
   */
  protected void runIt(final IUndoableOperation operation)
  {
    CorePlugin.run(operation);
  }

  private void storeDynamicInfills(final Editable[] subjects,
      final Map<SensorWrapper, ArrayList<TimePeriod>> suitableSegments)
  {
    // do another pass, and look for dynamic infills
    if (suitableSegments != null)
    {
      for (int i = 0; i < subjects.length; i++)
      {
        final Editable editable = subjects[i];
        if (editable instanceof DynamicInfillSegment)
        {
          final DynamicInfillSegment infill = (DynamicInfillSegment) editable;
          final BaseTimePeriod thisPeriod =
              new TimePeriod.BaseTimePeriod(infill.startDTG(), infill.endDTG());

          // loop through the sensors we've decided to use
          for (final SensorWrapper s : suitableSegments.keySet())
          {
            // add a reference to display it for this segment
            final ArrayList<TimePeriod> hisList = suitableSegments.get(s);
            hisList.add(thisPeriod);
          }
        }
      }
    }
  }
}
