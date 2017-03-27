package Debrief.Wrappers.Extensions;


/** API for classes that are capable of storing supplementatal datasets
 * 
 * @author ian
 *
 */
public interface AdditionalProvider
{
  /** get the additional data
   * 
   * @return
   */
  AdditionalData getAdditionalData();

  /** marker for classes that MAY need the existing elements() to be
   * wrapped. SensorWrapper will need to be wrapped. TrackWrapper
   * already breaks elements() down into a number of children
   * 
   * @author ian
   *
   */
  public static interface ExistingChildrenMayNeedToBeWrapped
  {
    
    /** whether to wrap the elements() entry in an wrapper
     * object - so that this wrapped object and AdditionalData
     * both sit at the same level
     * @return
     */
    boolean childrenNeedWrapping();
    
    /** get the collective name for other objects held by this object.
     * This is used in the Outline view.  If there is measured data,
     * we don't show all the children then the measured data.  We demote
     * the children to a named item, then we show the measured data
     * 
     * @return
     */
    String getItemsName();
  }
}
