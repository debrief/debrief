package MWC.Utilities.ReaderWriter.XML;



/**
 * interface for classes that are able to export a data object to XML
 * 
 * @author ian
 * 
 */
public interface ISAXImporter
{
  
  /** helper classes that can store data we've loaded
   * 
   * @author ian
   *
   */
  public static interface DataCatcher
  {
    /** store this data item
     * 
     * @param data
     */
    public void storeThis(Object data);
  }
  
  /** provide a suitable handler object
   * @param storeMe helper class, to store new data
   * 
   * @return
   */
  MWCXMLReader getHandler(DataCatcher storeMe);
}
