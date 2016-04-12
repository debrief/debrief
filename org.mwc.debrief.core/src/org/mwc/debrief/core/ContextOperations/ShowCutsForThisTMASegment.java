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
import Debrief.Wrappers.Track.RelativeTMASegment;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.TimePeriod;

/**
 * @author ian.mayo
 * 
 */
public class ShowCutsForThisTMASegment implements RightClickContextItemGenerator
{
  private static class ShowCutsOperation extends CMAPOperation
  {
    private final Layers _layers;
    private Map<SensorWrapper, ArrayList<TimePeriod>> _periods;

    public ShowCutsOperation(Layers theLayers,
        Map<SensorWrapper, ArrayList<TimePeriod>> periods)
    {
      super("Select sensor cuts for selected segment(s)");
      _layers = theLayers;
      _periods = periods;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      Set<SensorWrapper> sensors = _periods.keySet();

      // start off by hiding all cuts, for all sensors (not just selected ones)
      Iterator<SensorWrapper> sIter = sensors.iterator();
      if (sIter.hasNext())
      {
        SensorWrapper sensor = sIter.next();
        TrackWrapper host = sensor.getHost();
        BaseLayer allSensors = host.getSensors();
        Enumeration<Editable> sEnum = allSensors.elements();
        while (sEnum.hasMoreElements())
        {
          SensorWrapper thisS = (SensorWrapper) sEnum.nextElement();
          if (thisS.getVisible())
          {
            // ok, run through sensor cuts
            Enumeration<Editable> cuts = thisS.elements();
            while (cuts.hasMoreElements())
            {
              SensorContactWrapper contact =
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
        SensorWrapper sensorWrapper = (SensorWrapper) sIter.next();

        // get the data
        ArrayList<TimePeriod> list = _periods.get(sensorWrapper);

        Enumeration<Editable> cIter = sensorWrapper.elements();
        while (cIter.hasMoreElements())
        {
          SensorContactWrapper thisS =
              (SensorContactWrapper) cIter.nextElement();

          // loop through the periods
          Iterator<TimePeriod> pIter = list.iterator();
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
        SensorWrapper sensor = sIter.next();
        _layers.fireReformatted(sensor.getHost());
      }
      // hmm, maybe not

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
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
      return false;
    }

    @Override
    public boolean canUndo()
    {
      return false;
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
    int matchCount = 0;

    // so, see if it's something we can do business with
    if (subjects.length > 0)
    {
      Map<SensorWrapper, ArrayList<TimePeriod>> suitableSegments = null;

      for (int i = 0; i < subjects.length; i++)
      {
        Editable editable = subjects[i];
        if (editable instanceof RelativeTMASegment)
        {
          RelativeTMASegment seg = (RelativeTMASegment) editable;
          SensorWrapper sensor = seg.getReferenceSensor();

          // ok, found one. increment the counter
          matchCount++;

          // have we created our map yet?
          if (suitableSegments == null)
          {
            suitableSegments =
                new HashMap<SensorWrapper, ArrayList<TimePeriod>>();
          }

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

      // did it work?
      if (suitableSegments != null)
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
        Action doIt =
            new Action("Only display sensor cuts for selected " + phrase)
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
  protected IUndoableOperation getOperation(Layers theLayers,
      Map<SensorWrapper, ArrayList<TimePeriod>> periods)
  {
    return new ShowCutsOperation(theLayers, periods);
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
}
