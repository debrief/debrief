package Debrief.ReaderWriter.XML.extensions;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.Extensions.AdditionalData;
import Debrief.Wrappers.Extensions.AdditionalProvider;
import MWC.Utilities.ReaderWriter.XML.IDOMExporter;
import MWC.Utilities.ReaderWriter.XML.ISAXImporter;
import MWC.Utilities.ReaderWriter.XML.ISAXImporter.DataCatcher;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class AdditionalDataHandler extends MWCXMLReader
{
  private static final String MY_TYPE = "AdditionalData";
  
  private AdditionalData aData = new AdditionalData();

  
  public AdditionalDataHandler()
  {
    super(MY_TYPE);
    
    // ok, now for the child data. See if we have other handlers to declare
    if(_eHelper != null)
    {
      // create helper to store dta
      final DataCatcher storeMe = new DataCatcher(){

        @Override
        public void storeThis(Object data)
        {
          aData.add(data);
        }
      };
      
      List<ISAXImporter> helpers = _eHelper.getImporters();
      for(final ISAXImporter t: helpers)
      {
        addHandler(t.getHandler(storeMe));
      }
    }
  }
  
  

  @Override
  public void elementClosed()
  {
    super.elementClosed();
    
    // ok, store the data
    storeData(aData);
    
    // now clear it
    aData = new AdditionalData();
  }

  public static interface ExportProvider
  {
    public List<IDOMExporter> getExporters();
    public List<ISAXImporter> getImporters();
  }

  private static ExportProvider _eHelper;

  public static void setExportHelper(ExportProvider helper)
  {
    _eHelper = helper;
  }
  
  public static void appendChild(AdditionalProvider holder, Element parent,
      Document doc)
  {
    // ok, is there any extra data?
    AdditionalData additional = holder.getAdditionalData();
    if (additional != null)
    {
      // do we have any exporters?
      if (_eHelper != null)
      {
        List<IDOMExporter> exporters = _eHelper.getExporters();

        // ok, any children?
        if (additional.size() > 0)
        {
          boolean doneOne = false;

          // ok, we need the placeholder
          Element aData = doc.createElement(MY_TYPE);

          for (Object item : additional)
          {
            for (final IDOMExporter t : exporters)
            {
              if (t.canExportThis(item))
              {
                t.export(item, aData, doc);

                doneOne = true;
              }
            }
          }

          // add to parent, if we have anything
          if (doneOne)
          {
            parent.appendChild(aData);
          }
        }
      }
    }
  }

  /** implementing classes must override this, to store the data
   * 
   * @param data
   */
  abstract public void storeData(AdditionalData data);

}
