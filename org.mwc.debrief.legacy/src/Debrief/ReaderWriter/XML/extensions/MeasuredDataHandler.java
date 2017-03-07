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
      return new DataFolderHandler(6)
      {
        @Override
        public void addFolder(DataFolder data)
        {
          System.out.println("storing folder at top level:" + data.getName());
          storeMe.storeThis(data);
        }
      };
  }
}
