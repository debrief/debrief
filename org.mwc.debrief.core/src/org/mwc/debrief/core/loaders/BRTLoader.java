/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.wizards.ImportBRTDialog;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.BRT.BRTImporter;
import Debrief.ReaderWriter.BRT.BRTImporter.ImportBRTAction;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;

public class BRTLoader extends CoreLoader
{

  private static class WrapAction implements IUndoableOperation
  {
    private final Action _action;

    public WrapAction(final Action action)
    {
      _action = action;
    }

    @Override
    public void addContext(final IUndoContext context)
    {
      // skip;
    }

    @Override
    public boolean canExecute()
    {
      return true;
    }

    @Override
    public boolean canRedo()
    {
      return _action.isRedoable();
    }

    @Override
    public boolean canUndo()
    {
      return _action.isUndoable();
    }

    @Override
    public void dispose()
    {
      // skip.
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      _action.execute();
      return Status.OK_STATUS;
    }

    @Override
    public IUndoContext[] getContexts()
    {
      return new UndoContext[]
      {};
    }

    @Override
    public String getLabel()
    {

      return _action.toString();
    }

    @Override
    public boolean hasContext(final IUndoContext context)
    {

      return false;
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      _action.execute();
      return Status.OK_STATUS;
    }

    @Override
    public void removeContext(final IUndoContext context)
    {
      // skip.
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      _action.undo();
      return Status.OK_STATUS;
    }

  }

  public BRTLoader()
  {
    super("BRT", ".brt");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers layers, final InputStream inputStream, final String fileName)
      throws Exception
  {
    final Layers theLayers = (Layers) target.getAdapter(Layers.class);

    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)
      {
        try
        {
          final BRTImporter importer = new BRTImporter();
          final TrackWrapper[] allTracks = BRTImporter.getTracks(theLayers);
          if (allTracks.length == 0)
          {
            Display.getDefault().asyncExec(new Runnable()
            {

              @Override
              public void run()
              {
                MessageDialog.open(MessageDialog.INFORMATION, Display.getDefault()
                    .getActiveShell(), "BRT Import",
                    "You need to have at least 1 track loaded to add the BRT Data",
                    MessageDialog.INFORMATION);
              }
            });
            return;
          }
          final TrackWrapper theTrack = BRTImporter.findTrack(allTracks);
          final ImportBRTDialog wizard = new ImportBRTDialog(theTrack,
              allTracks);

          final WizardDialog dialog = new WizardDialog(null, wizard);
          Display.getDefault().syncExec(new Runnable()
          {
            @Override
            public void run()
            {
              dialog.create();
              dialog.open();
            }
          });
          if (dialog.getReturnCode() == Window.OK)
          {
            final ImportBRTAction action = importer.importThis(wizard, fileName,
                inputStream);
            final IUndoableOperation operation = new WrapAction(action);
            CorePlugin.run(operation);
          }
          else
          {
            DebriefPlugin.logError(IStatus.INFO, "User cancelled loading:"
                + fileName, null);
          }
        }
        catch (final Exception e)
        {
          DebriefPlugin.logError(IStatus.ERROR, "Problem loading BRT datafile:"
              + fileName, e);
        }
      }
    };
  }

}
