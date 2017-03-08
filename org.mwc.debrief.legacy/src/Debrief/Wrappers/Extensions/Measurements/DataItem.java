package Debrief.Wrappers.Extensions.Measurements;

/** in our temporary data structures we allow a folder to contain
 * both dataset and other folders. This API is common
 * to both
 * 
 * @author ian
 *
 */
public interface DataItem
{
  /** the name of this item
   * 
   * @return
   */
  public String getName();
  
  /** for diagnostics - list the contents
   * of this item
   */
  public void printAll();

  /** keep track of the parent for this item
   * 
   * @param dataFolder
   */
  public void setParent(DataFolder dataFolder);
  
  /** find the parent of this item, if possible
   * 
   * @return
   */
  public DataFolder getParent();
  
}
