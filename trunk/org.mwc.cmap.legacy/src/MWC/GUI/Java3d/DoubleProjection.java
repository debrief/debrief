/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 12, 2002
 * Time: 1:59:24 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import javax.vecmath.Point3d;

import MWC.Algorithms.Projections.FlatProjection;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DoubleProjection extends FlatProjection{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** working vector object, to reduce object creation ins screen operations
   */
  private WorldVector _workingVector = new WorldVector(0,0,0);

  /** working location object, to reduce object creation ins screen operations
   */
  private WorldLocation _workingLocation = new WorldLocation(0,0,0);

  /** working screen location, to reduce screen operations
   *
   */
  private Point3d scrRes = new Point3d(0,0, 0);

  /** we want an additional scale factor to handle plotting depth in metres
   *
   */
  private double _depthScale = 1;

  /** stretch factor to let us make depth easier to read
   *
   */
  private double _depthStretch = 1;

  ///////////////////////////
  // member methods
  ///////////////////////////

  public void zoom(double value) {
    // let our parent do the zoom first
    super.zoom(value);

    // sort out our depth_scale factor!
    double scale = this.getScaleFactor();

    // convert this from degrees to meters
    double metres = MWC.Algorithms.Conversions.Degs2m(scale);

    // and store it
    setDepthScale(metres);
  }

  /**  conversion factor used to convert from screen coordinates to depth in metres
   *
   */
  public double getDepthScale()
  {
    return _depthScale;
  }

  /** change the conversion factor used to convert from world depth (metres) to screen depth (units)
   *
   * @param depthScale
   */
  public void setDepthScale(double depthScale)
  {
    this._depthScale = depthScale;
  }

  /**  stretch to apply to depth data to make it easier to read
   *
   */
  public double getDepthStretch()
  {
    return _depthStretch;
  }

  /** change the exaggeration to apply to depth data
   *
   * @param depthStretch
   */
  public void setDepthStretch(double depthStretch)
  {
    this._depthStretch = depthStretch;
  }

  public Point3d toScreen3D(WorldLocation val)
  {

    // check we've got valid data
    if((_scaleVal == 0) ||
			 (Double.isInfinite(_scaleVal)))
      return null;

    // check that the data has it's origin defined,
    // even if we choose not to use it (since we may be
    // in relative mode
    if(_dataOrigin == null)
			offsetOrigin(false);


    // what is our origin going to be?
    WorldLocation myOrigin = null;
    double bearingOffset = 0.0;

    // see if we are in relative mode
    if(super.getRelativePlot())
    {
      // check if we have a parent defined
      if(super._relativePlotter != null)
      {
        // try to get the origin
        myOrigin = _relativePlotter.getLocation();

        // and the bearing offset
        bearingOffset = _relativePlotter.getHeading();

      }
    }

    // oh well, just use the traditional (absolute) origin anyway
    if(myOrigin == null)
      myOrigin = _dataOrigin;

		// find the offsets from the data origin
		WorldVector delta = val.subtract(myOrigin, _workingVector);
		double rng = delta.getRange();
		double brg = delta.getBearing() - bearingOffset;

    // scale from world to data
    rng = rng / _scaleVal;

    double xDelt =   (Math.sin(brg) * rng);
    double yDelt =   (Math.cos(brg) * rng);

    scrRes.x = xDelt;
    scrRes.z = yDelt;

    scrRes.y = - val.getDepth() / (getDepthScale() / getDepthStretch());

    // invert the y
    scrRes.z = - scrRes.z;

    // done, now we can return
    return scrRes;
  }

  public WorldLocation toWorld3D(Point3d val)
  {

    WorldLocation answer = null;
    Point3d p1 = new Point3d();

    if(_scaleVal == 0)
      return answer;

		if(_dataOrigin == null)
			return answer;

		// work out our offsets from the origin
		double X = val.x;
		double Y = val.z;
    double depth = val.y;

    // invert our y coordinate
    p1.y = -Y;
    p1.x = X;

    // so the coordinates in res now represent screen offset x and y
    // from the screen origin

    // produce vectors relative to origin
    double brg;
    double rng;

    rng = Math.sqrt(p1.x * p1.x + p1.y * p1.y);
    // scale this vector to world coordinates
    rng = rng * _scaleVal;
    brg = Math.atan2(p1.x, p1.y);

    // sort out if we are in relative projection mode anyway.
    // what is our origin going to be?
    WorldLocation myOrigin = null;
    double bearingOffset = 0.0;

    // see if we are in relative mode
    if(super.getRelativePlot())
    {
      // check if we have a parent defined
      if(super._relativePlotter != null)
      {
        // try to get the origin
        myOrigin = _relativePlotter.getLocation();

        // and the bearing offset
        bearingOffset = _relativePlotter.getHeading();
      }
    }

    // oh well, just use the absolute origin anyway
    if(myOrigin == null)
      myOrigin = _dataOrigin;

    // populate our working vector
    _workingVector.setValues(brg + bearingOffset, rng, - depth * (getDepthScale() / getDepthStretch()));

    // now add this data-scale vector to the data origin
    _workingLocation.copy(myOrigin);
    _workingLocation.addToMe(_workingVector);

    return _workingLocation;
  }
}
