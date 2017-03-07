package Debrief.ReaderWriter.XML.extensions;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.Extensions.Measurements.CoreDataset;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
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

  private void exportThisFolder(DataFolder folder, Element parent, Document doc)
  {
    Element df = doc.createElement("DataFolder");
    
    // attributes
    df.setAttribute("Name", folder.getName());

    for(DataItem child: folder)
    {
      if(child instanceof DataFolder)
      {
        DataFolder childFolder = (DataFolder) child;
        exportThisFolder(childFolder, df, doc);
      }
      else if(child instanceof CoreDataset)
      {
        CoreDataset childD = (CoreDataset) child;
        exportThisDataset(childD, df, doc);
      }
    }
    
    // now children
    parent.appendChild(df);
  }
  
  private void exportThisDataset(CoreDataset dataset, Element parent,
      Document doc)
  {
    Element ds = doc.createElement("Dataset");
    
    ds.setAttribute("Name", dataset.getName());
    ds.setAttribute("Units", dataset.getUnits());
    
    // ok, now work through the children
    Iterator<Long> indices = dataset.getIndices();
    Iterator<Double> values = dataset.getValues();
    
    while(indices.hasNext())
    {
      Element ele = doc.createElement("P");
      ele.setAttribute("Index","" + indices.next());
      ele.setAttribute("Value","" + values.next());
      ds.appendChild(ele);
    }
    
    parent.appendChild(ds);
  }

  @Override
  public void export(Object subject, Element parent, Document doc)
  {
    DataFolder folder = (DataFolder) subject;
    
    // ok, now we need to walk the tree
    exportThisFolder(folder, parent, doc);
  }

  @Override
  public boolean canImportThis(String subject)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public MWCXMLReader getHandler()
  {
    // TODO Auto-generated method stub
    return null;
  }


}
