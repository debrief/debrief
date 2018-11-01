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
import java.text.ParseException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.FlatFile.ImportSATC;
import MWC.GUI.Layers;

/**
 */
public class SATCLoader extends CoreLoader
{

  public SATCLoader()
  {
    super("SATC Scenario", null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
   * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
   */
  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers theLayers,
      final InputStream inputStream, final String fileName)
  {
    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)

      {
        try
        {
          // ok - get loading going
          ImportSATC importer = new ImportSATC(theLayers);
          importer.importThis(fileName, inputStream);
        }
        catch(ParseException pe)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading SATC datafile:"
              + fileName, pe);
        }
        catch (final RuntimeException e)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading SATC datafile:"
              + fileName, e);
        }
        catch (final Exception e)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading SATC datafile:"
              + fileName, e);
        }
      }
    };
  }
}
