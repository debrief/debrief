package MWC.GUI;

import java.util.Enumeration;

/** interface for a Debrief entity that contains other child elements
 * 
 * @author ian
 *
 */
public interface HasEditables
{
  /** get the children
   * 
   * @return the elements
   */
  Enumeration<Editable> elements();

  /** whether the children should be ordered
   * 
   * @return yes/no
   */
  boolean hasOrderedChildren();
  
  
  /** interface for class that normally provides it's elements in a tiered fashion,
   * but is able to provide them as a single list (for when an external class wants to
   * process all of them as one list - double-click nearest testing).
   */
  public static interface ProvidesContiguousElements
  {
    public Enumeration<Editable> contiguousElements();
  }


  void add(Editable point);

  void removeElement(Editable point);

}
