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
}
