package MWC.Utilities.ReaderWriter.XML;


/**
 * interface for classes that are able to export a data object to XML
 * 
 * @author ian
 * 
 */
public interface ISAXImporter
{
  /** determine if this exporter can export this object
   * 
   * @param subject
   * @return
   */
  boolean canImportThis(String subject);

  /** provide a suitable handler object
   * 
   * @return
   */
  MWCXMLReader getHandler();
}
