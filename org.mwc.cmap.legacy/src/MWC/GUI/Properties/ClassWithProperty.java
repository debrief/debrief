package MWC.GUI.Properties;

import java.beans.PropertyChangeListener;

/** interface for classes that support property listeners
 * 
 * @author ian
 *
 */
public interface ClassWithProperty
{
  public void removePropertyListener(final PropertyChangeListener list);
  
  public void addPropertyListener(final PropertyChangeListener list);
}
