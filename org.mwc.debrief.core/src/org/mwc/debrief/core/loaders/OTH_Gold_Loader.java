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
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.FlatFile.OTH_Helper_Headless;
import Debrief.ReaderWriter.FlatFile.OTH_Importer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;

/**
 */
public class OTH_Gold_Loader extends CoreLoader
{

  public OTH_Gold_Loader()
  {
    super("OTH Gold", ".txt");
  }

  @Override
  public boolean canLoad(final String fileName)
  {
    boolean res = false;

    if (super.canLoad(fileName))
    {
      res = OTH_Importer.canLoad(fileName, CorePlugin.getToolParent());
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
        // create way of passing reference back from dialog
        final AtomicInteger msgRes = new AtomicInteger(SWT.CANCEL);

        Display.getDefault().syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            final Shell s = Display.getCurrent().getActiveShell();
            final MessageBox messageBox = new MessageBox(s, SWT.ICON_QUESTION
                | SWT.YES | SWT.NO | SWT.CANCEL);
            messageBox.setMessage(
                "Do you wish to generate ellipse shapes for TUAs?");
            messageBox.setText("Import OTH-Gold");
            msgRes.set(messageBox.open());
          }
        });

        if (msgRes.get() == SWT.CANCEL)
        {
          // ok, just drop out
          return;
        }

        final boolean importOTH = msgRes.get() == SWT.YES;
        final OTH_Importer importer = new OTH_Importer();
        final OTH_Helper_Headless othHelper = new OTH_Helper_Headless(
            importOTH);

        try
        {
          // ok - get loading going
          final Action importAction = importer.importThis(othHelper,
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
