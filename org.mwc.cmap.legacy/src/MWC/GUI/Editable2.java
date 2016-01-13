package MWC.GUI;

import java.util.Collection;

/** more versatile version of editable, that supports children & some property access
 * 
 * @author ian
 *
 */
public interface Editable2 extends Editable
{
  /** indicator
   * 
   * @return yes/no if this object has children
   */
  boolean hasChildren();
 
  /** access children
   * 
   * @return list of child entities
   */
  Collection<Editable> getChildren();
  
  /** get the value for the named property
   * 
   * @param descriptor
   * @return
   */
  public Object getValue(Object id);

  /** update the specified value
   *  
   * @param string
   * @param theValue
   */
  void setValue(Object id, Object theValue);
}
