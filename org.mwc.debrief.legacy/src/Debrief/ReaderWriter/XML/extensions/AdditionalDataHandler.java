package Debrief.ReaderWriter.XML.extensions;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.Extensions.AdditionalData;
import Debrief.Wrappers.Extensions.AdditionalProvider;
import MWC.Utilities.ReaderWriter.XML.IDOMExporter;

public class AdditionalDataHandler
{
  public static interface ExporterProvider
  {
    public List<IDOMExporter> getExporters();
  }

  private static ExporterProvider _helper;

  public static void setExportHelper(ExporterProvider helper)
  {
    _helper = helper;
  }

  public static void appendChild(AdditionalProvider holder, Element parent,
      Document doc)
  {
    // ok, is there any extra data?
    AdditionalData additional = holder.getAdditionalData();
    if (additional != null)
    {
      // do we have any exporters?
      if (_helper != null)
      {
        List<IDOMExporter> exporters = _helper.getExporters();

        // ok, any children?
        if (additional.size() > 0)
        {
          boolean doneOne = false;

          // ok, we need the placeholder
          Element aData = doc.createElement("AdditionalData");

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

}
