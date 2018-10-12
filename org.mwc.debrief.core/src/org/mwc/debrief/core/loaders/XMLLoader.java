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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class XMLLoader extends CoreLoader
{

  /**
   * the static object we use for data-file load/open
   */
  private static DebriefEclipseXMLReaderWriter _myReader;

  public XMLLoader()
  {
    super("XML", null);
    if (_myReader == null)
    {
      _myReader = new DebriefEclipseXMLReaderWriter();
    }
  }


  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers theLayers, final InputStream inputStream,
      final String fileName)
  {
    return new IRunnableWithProgress()
    {
      public void run(final IProgressMonitor pm)
      {
        IControllableViewport view = (IControllableViewport) target.getAdapter(
            IControllableViewport.class);
        PlotEditor plot = (PlotEditor) target.getAdapter(PlotEditor.class);
        _myReader.importThis(fileName, inputStream, theLayers, view, plot);
      }
    };
  }
}
