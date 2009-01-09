/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 16, 2002
 * Time: 3:06:48 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;

public class WatchableTransformGroup extends TransformGroup {

  ///////////////////////////
  // member variables
  ///////////////////////////

  /** the list of listeners to this transform
   *
   */
  protected java.util.Vector<TransformListener> _myListeners = new java.util.Vector<TransformListener>(0,1);



  ///////////////////////////
  // constructor
  ///////////////////////////

  public void setTransform(Transform3D d) {
    super.setTransform(d);

    // can we determine the rotation
    Matrix3f m3 = new Matrix3f();
    d.getRotationScale(m3);

    // fire the event
    java.util.Iterator<TransformListener> iter = _myListeners.iterator();
    while(iter.hasNext())
    {
      TransformListener tl = (TransformListener)iter.next();
      tl.newTransform(d);
    }
  }


  ///////////////////////////
  // listener support
  ///////////////////////////
  public void addListener(TransformListener listener)
  {
    _myListeners.add(listener);
  }

  public void removeListener(TransformListener listener)
  {
    _myListeners.remove(listener);
  }

  ///////////////////////////
  // listener class
  ///////////////////////////
  public static interface TransformListener
  {
    /** our transform has changed to this new transform
     *
     */
    public void newTransform(Transform3D trans);
  }




}
