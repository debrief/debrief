/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:59:07
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;

public interface PropertyChangeListenerRMI extends java.rmi.Remote
{
  /** receive infomation about a property being changed
   *
   */
  public void propertyChange(String property_name, Object old_val, Object new_val) throws java.rmi.RemoteException;
}
