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

import java.text.DecimalFormat;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 */
public class CalculateTrackLength implements RightClickContextItemGenerator
{

  /**
   * @param parent
   * @param theLayers
   * @param parentLayers
   * @param subjects
   */
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    TrackWrapper subject = null;

    // we're only going to work with two or more items
    if (subjects.length == 1)
    {
      Editable item = subjects[0];
      if (item instanceof TrackWrapper)
      {
        subject = (TrackWrapper) item;
      }
    }

    // ok, is it worth going for?
    if (subject != null)
    {

      // right,stick in a separator
      parent.add(new Separator());

      final String theTitle = "Calculate track length (visible positions)";
      final TrackWrapper finalItem = subject;

      // create this operation
      final Action doMerge = new Action(theTitle)
      {
        public void run()
        {
          final IUndoableOperation theAction =
              new CalculateTrackLengthOperation(theTitle, finalItem);

          CorePlugin.run(theAction);
        }
      };
      parent.add(doMerge);
    }
  }

  private static class CalculateTrackLengthOperation extends CMAPOperation
  {

    /**
     * the parent to update on completion
     */
    private final TrackWrapper _subject;

    public CalculateTrackLengthOperation(final String title,
        final TrackWrapper subject)
    {
      super(title);
      _subject = subject;
    }

    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      // get the positions
      Enumeration<Editable> positions = _subject.getPositions();

      double distanceDegs = 0;
      WorldLocation lastLoc = null;

      // get distance frmm previous
      while (positions.hasMoreElements())
      {
        FixWrapper thisF = (FixWrapper) positions.nextElement();
        if (thisF.getVisible())
        {
          if (lastLoc != null)
          {
            // distance
            distanceDegs += thisF.getLocation().subtract(lastLoc).getRange();
          }
          // remember the location
          lastLoc = thisF.getLocation();
        }
      }

      // convert to current units
      rangeCalc calc = new rangeCalc();
      String units = calc.getUnits();
      double range = rangeCalc.convertRange(distanceDegs, units);

      DecimalFormat df = new DecimalFormat("0.0000");
      String res = df.format(range) + " " + units;

      // and show the message dialog
      MessageDialog.openInformation(Display.getDefault().getActiveShell(),
          "Calculate length of visible parts of track", _subject.getName()
              + " has length " + res);

      // return CANCEL so this event doesn't get put onto the undo buffer,
      // and unnecessarily block the undo queue
      return Status.CANCEL_STATUS;
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
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      CorePlugin.logError(Status.INFO,
          "Undo not relevant to calculate track length", null);
      return null;
    }
  }
}
