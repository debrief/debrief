package MWC.GUI;

import java.util.Enumeration;

/** interface for a Debrief entity that contains other child elements
 * 
 * @author ian
 *
 */
public interface CanEnumerate
{
  /** get the children
   * 
   * @return the elements
   */
  Enumeration<Editable> elements();
}
