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
package org.mwc.cmap.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.ui_support.SelectImportModeDialog;
import org.mwc.cmap.core.wizards.ImportRepFreqDialog;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.Replay.ImportReplay.ProvidesModeSelector;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Palette.CreateVPFLayers;

/**
 * @author ian.mayo
 */
public class DebriefToolParent implements ToolParent, ProvidesModeSelector
{
  /**
   * the set of preferences we support
   */
  private final IPreferenceStore _myPrefs;

  /**
   * the undo buffer we support
   * 
   */
  private final IOperationHistory _myUndo;

  /**
   * convenience object, used to get selected import mode back from the popup dialog
   * 
   */
  private static ImportSettings _selectedImportSettings = null;

  public DebriefToolParent(final IPreferenceStore prefs,
      final IOperationHistory undoBuffer)
  {
    _myPrefs = prefs;
    _myUndo = undoBuffer;
  }

  /**
   * @param theCursor
   */
  public void setCursor(final int theCursor)
  {

  }

  /**
	 * 
	 */
  public void restoreCursor()
  {

  }

  /**
   * @param theAction
   */
  public void addActionToBuffer(final Action theAction)
  {
    // ok, better wrap the action first
    final DebriefActionWrapper daw = new DebriefActionWrapper(theAction);

    // now add it to the buffer (though we don't need to start with the activate
    // bit)
    try
    {
      _myUndo.execute(daw, null, null);
    }
    catch (final ExecutionException e)
    {
      CorePlugin.logError(Status.ERROR, "Executing newly added action", e);
    }

  }

  /**
   * @param name
   * @return
   */
  public String getProperty(final String name)
  {
    final String res = _myPrefs.getString(name);

    return res;
  }

  /**
   * @param pattern
   * @return
   */
  public Map<String, String> getPropertiesLike(final String pattern)
  {
    final Map<String, String> retMap = new HashMap<String, String>();

    // SPECIAL PROCESSING. THE ONLY TIME WE USE CURRENTLY USE THIS IS FOR THE
    // VPF PATHS
    if (pattern.equals(CreateVPFLayers.VPF_DATABASE_PROPERTY))
    {
      //
      for (int i = 1; i < 10; i++)
      {
        final String thisVPFPath = pattern + "." + i;
        if (_myPrefs.contains(thisVPFPath))
        {
          // ok, has it been changed from the default?
          if (!_myPrefs.isDefault(thisVPFPath))
            retMap.put(thisVPFPath, _myPrefs.getString(thisVPFPath));
        }
      }
    }
    else
    {
      CorePlugin.logError(Status.ERROR,
          "Should not be requesting patterned properties", null);
    }
    return retMap;
  }

  /**
   * @param name
   * @param value
   */
  public void setProperty(final String name, final String value)
  {
    _myPrefs.putValue(name, value);

  }

  @Override
  public void
      logError(final int status, final String text, final Exception e, final boolean revealLog)
  {
    CorePlugin.logError(status, text, e);
    
    
    // prompt Error Log view to user up on error report (if requested)
    if (revealLog)
    {
      final Display current = Display.getDefault();
      if (current != null)
      {
        final Runnable showLog = new Runnable()
        {
          @Override
          public void run()
          {
            if (PlatformUI.getWorkbench() != null
                && PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null
                && PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage() != null)
            {
              try
              {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage()
                    .showView("org.eclipse.pde.runtime.LogView");
              }
              catch (PartInitException e1)
              {
                if(e1.getMessage().contains("Could not create view: org.eclipse.pde.runtime.LogView"))
                {
                  // it's ok, we're probably running JUnit test
                }
                else
                {
                  e1.printStackTrace();
                }
              }
            }
          }
        };
        current.asyncExec(showLog);
      }
    }
  }

  public void logError(final int status, final String text, final Exception e)
  {
    logError(status, text, e, false);
  }

  /**
   * popup a dialog to let the user select the import mode
   * 
   * @return selected mode, from ImportReplay
   */
  @Override
  public ImportSettings getSelectedImportMode(final String trackName)
  {
    _selectedImportSettings = null;

    final Shell active =
        PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
    // ok, popup our custom dialog, let user decide
    final SelectImportModeDialog dialog =
        new SelectImportModeDialog(active, trackName);
    // store the value
    _selectedImportSettings = dialog.openDialog();
    
    return _selectedImportSettings;
  }

  /**
   * popup a dialog to let the user select the import mode
   * 
   * @return selected mode, from ImportReplay
   */
  @Override
  public Long getSelectedImportFrequency(final String trackName)
  {
    _selectedImportSettings = null;

    final Display current = Display.getDefault();
    current.syncExec(new Runnable()
    {
      public void run()
      {
        final Shell active =
            PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
        // ok, popup our custom dialog, let user decide
        final ImportRepFreqDialog dialog =
            new ImportRepFreqDialog(active, trackName);

        int sel = dialog.open();

        if (sel != Dialog.CANCEL)
        {
          // store the value
          long freq = dialog.getSampleFreq();
          _selectedImportSettings =
              new ImportSettings(ImportReplay.IMPORT_AS_OTG, freq);
        }
      }
    });

    Long res;
    if (_selectedImportSettings != null)
    {
      res = _selectedImportSettings.sampleFrequency;
    }
    else
    {
      res = null;
    }
    return res;
  }

  @Override
  public void logStack(int status, String text)
  {
    CorePlugin.logError(status, text, null, true);
  }
}
