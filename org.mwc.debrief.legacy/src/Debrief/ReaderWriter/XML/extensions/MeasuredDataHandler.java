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
package Debrief.ReaderWriter.XML.extensions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import MWC.Utilities.ReaderWriter.XML.IDOMExporter;
import MWC.Utilities.ReaderWriter.XML.ISAXImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class MeasuredDataHandler implements IDOMExporter, ISAXImporter
{

  @Override
  public boolean canExportThis(Object subject)
  {
    final boolean res;
    if(subject instanceof DataFolder)
    {
      res = true;
    }
    else
    {
      res = false;
    }
    return res;
  }


  @Override
  public void export(Object subject, Element parent, Document doc)
  {
    DataFolder folder = (DataFolder) subject;
    
    // ok, now we need to walk the tree
    DataFolderHandler.exportThisFolder(folder, parent, doc);
  }

  @Override
  public MWCXMLReader getHandler(final DataCatcher storeMe)
  {
      return new DataFolderHandler()
      {
        @Override
        public void addFolder(DataFolder data)
        {
          storeMe.storeThis(data);
        }
      };
  }
}
