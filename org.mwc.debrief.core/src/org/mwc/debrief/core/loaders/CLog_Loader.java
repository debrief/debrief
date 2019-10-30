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
package org.mwc.debrief.core.loaders;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;
import org.osgi.service.prefs.BackingStoreException;

import Debrief.ReaderWriter.FlatFile.CLogFileImporter;
import Debrief.ReaderWriter.FlatFile.CLogFileImporter.CLog_Helper;
import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;

/**
 */
public class CLog_Loader extends CoreLoader
{

  public CLog_Loader()
  {
    super("CLog File", ".txt");
  }

  @Override
  public boolean canLoad(final String fileName)
  {
    boolean res = false;

    if (super.canLoad(fileName))
    {
      res = CLogFileImporter.canLoad(fileName, CorePlugin.getToolParent());
    }
    return res;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
   * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
   */
  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers layers, final InputStream inputStream, final String fileName)
  {
    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)
      {
        final CLogFileImporter importer = new CLogFileImporter();
        
        // get the last filename used
        final String prefKey = "Last_Track_Name";
        final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID + ".New_CLog_File");
        final String defaultName = prefs.get(prefKey, "<Pending>");
        
        CLog_Helper helper = new CLog_Helper() {

          @Override
          public String getTrackName()
          {
            AtomicReference<String> res = new AtomicReference<String>(CLog_Helper.CANCEL_STRING);
            Display.getDefault().syncExec(new Runnable()
            {
              @Override
              public void run()
              {
                final Shell sw = Display.getCurrent().getActiveShell();
                final InputDialog id = new InputDialog(sw, "Load new CLog track", 
                    "Please enter the name for this new track.", defaultName, new IInputValidator() {
                  public String isValid(String newText) {
                    if (newText.length() < 3) {
                      return "Please provide a longer name";
                    }
                    return null;
                  }
                });
                
                int answer = id.open();
                if(answer == InputDialog.OK)
                {
                  res.set(id.getValue());
                  // and store it
                  prefs.put(prefKey, id.getValue());
                  
                  try
                  {
                    // forces the application to save the preferences
                    prefs.flush();
                  }
                  catch (BackingStoreException e)
                  {
                    e.printStackTrace();
                  }
                  
                }
              }
            });
            return res.get();
          }};

        try
        {
          // ok - get loading going
          final Action importAction = importer.importThis(helper,
              inputStream, layers, CorePlugin.getToolParent());

          final WrapDebriefAction dAction = new WrapDebriefAction(importAction);
          CorePlugin.run(dAction);
        }
        catch (final Exception e)
        {
          DebriefPlugin.logError(IStatus.ERROR, "Problem loading AIS datafile:"
              + fileName, e);
        }
      }
    };
  }
}
