/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 23, 2002
 * Time: 12:07:10 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d.Tactical;

/** interface for classes which participant in our 3-d world, including support for
 *  telling them that we have now closed
 */
public interface WorldMember
{
  /** we are about to close, shut down all listeners
   *
   */
  public void doClose();
}
