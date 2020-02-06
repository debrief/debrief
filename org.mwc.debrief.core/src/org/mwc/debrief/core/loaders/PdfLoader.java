/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.core.loaders;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument;
import MWC.GUI.Layers;

public class PdfLoader extends CoreLoader
{

  public PdfLoader()
  {
    super("pdf", "pdf");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers layers, final InputStream inputStream, final String fileName)
  {
    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)
      {
        ImportNarrativeDocument iw = new ImportNarrativeDocument(layers);
        ArrayList<String> strings = ImportNarrativeDocument.importFromPdf(
            fileName, inputStream);
        iw.processThese(strings);
      }
    };
  }

}
