package MWC.Utilities.ReaderWriter.XML;


/**
 * interface for classes that are able to export a data object to XML
 * 
 * @author ian
 * 
 */
public interface IDOMExporter
{
  /** determine if this exporter can export this object
   * 
   * @param item
   * @return
   */
  boolean canExportThis(Object item);

   
  /** export this object
   * 
   * @param item
   * @param parent
   * @param doc
   */
  void export(Object item, final org.w3c.dom.Element parent,
      final org.w3c.dom.Document doc);

}
