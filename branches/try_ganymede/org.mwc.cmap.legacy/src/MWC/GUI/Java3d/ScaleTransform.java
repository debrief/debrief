/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 23, 2002
 * Time: 11:47:09 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import javax.media.j3d.*;
import javax.vecmath.*;

public class ScaleTransform extends TransformGroup implements WatchableTransformGroup.TransformListener
{
  ///////////////////////////
  // member variables
  ///////////////////////////
  /** the location we maintain scale from
   *
   */
  TransformGroup _targetLocation = null;

  /** the watchable transform group we listen to
   *
   */
  WatchableTransformGroup _viewTransform = null;

  /** basic (user-controlled)scale factor to apply in addition to automated values
   *
   */
  private double _userScaleFactor = 1;


  ///////////////////////////
  // constructor
  ///////////////////////////

  /**
   *
   * @param location the location we maintain the scale from
   * @param view  the view we listen to, to determine when to rescale
   */
  public ScaleTransform(TransformGroup location, WatchableTransformGroup view)
  {
    _targetLocation = location;
    _viewTransform = view;

    this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    _viewTransform.addListener(this);

    // just do a quick transform to initialise ourselves
    Transform3D t3 = new Transform3D();
    _viewTransform.getTransform(t3);
    this.newTransform(t3);
  }

  /**
   *
   * @param targetLocation the location we maintain the scale from
   * @param viewTransform  the view we listen to, to determine when to rescale
   * @param userScaleFactor an additional scale factor to apply
   */
  public ScaleTransform(TransformGroup targetLocation, WatchableTransformGroup viewTransform, double userScaleFactor) {
    this(targetLocation,viewTransform);

    // and store the scale
    _userScaleFactor = userScaleFactor;
  }

  ///////////////////////////
  // member methods
  ///////////////////////////

  /** change the user-supplied scale factor
   *
   * @param userScaleFactor - new scale to use
   */
  public void setUserScaleFactor(double userScaleFactor) {
    this._userScaleFactor = userScaleFactor;
  }

  public void doClose()
  {
    _viewTransform.removeListener(this);
  }

  /** our transform has changed to this new transform
   *
   */
  public void newTransform(Transform3D trans)
  {
    // find out where our target is
    Transform3D t3 = new Transform3D();
    _targetLocation.getTransform(t3);
    Vector3f myPos = new Vector3f();
    t3.get(myPos);

    // extract the rotation and translation elements from the new view
    Vector3f viewPos = new Vector3f();
    Matrix3f viewRot = new Matrix3f();
    trans.get(viewRot, viewPos);

    // create a working matrix containing the view translation
    Matrix3f tmpRot = new Matrix3f();
    tmpRot.setColumn(0, viewPos);

    // move the translation to the eye location
    viewRot.mul(tmpRot);

    // extract the translation
    viewRot.getColumn(0, viewPos);

    // subtract our location from the eye location
    viewPos.sub(myPos);

    // produce a scale factor according to the distance from the eye
    double new_scale = viewPos.length() / 10d;

    // apply the user-controlled scale
    new_scale *= _userScaleFactor;

    // scale ourselves accordingly
    Transform3D myScale = new Transform3D();
 //   getTransform(myScale);
    myScale.setScale(new_scale);
    setTransform(myScale);
  }
}
