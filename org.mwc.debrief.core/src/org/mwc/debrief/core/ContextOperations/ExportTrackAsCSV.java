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
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.ExportCSVPreferencesPage;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class ExportTrackAsCSV implements RightClickContextItemGenerator
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
    // see if use wants to see this command.
    final IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
    final boolean isEnabled = store.getBoolean(ExportCSVPreferencesPage.PreferenceConstants.INCLUDE_COMMAND);
    
    System.out.println("Enabled:" + isEnabled);
    
    if(!isEnabled)
      return;
    
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

      final String theTitle = "Export Track to CSV Text format";
      final TrackWrapper finalItem = subject;

      // create this operation
      final Action doExport = new Action(theTitle)
      {
        public void run()
        {
          final IUndoableOperation theAction =
              new ExportTrackToCSV(theTitle, finalItem);

          CorePlugin.run(theAction);
        }
      };
      parent.add(doExport);
    }
  }

  private static class ExportTrackToCSV extends CMAPOperation
  {

    /**
     * the parent to update on completion
     */
    private final TrackWrapper _subject;

    public ExportTrackToCSV(final String title,
        final TrackWrapper subject)
    {
      super(title);
      _subject = subject;
    }

    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
     // TODO
      System.out.println("exporting " + _subject);

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
          "Undo not relevant to export Track to CSV", null);
      return null;
    }
  }

}
