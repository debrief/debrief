/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 22, 2002
 * Time: 11:56:16 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d.Tactical;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import MWC.GUI.Java3d.ColorChangeText2D;
import MWC.GUI.Java3d.ScaleTransform;
import MWC.GUI.Java3d.WatchableTransformGroup;
import MWC.GUI.Java3d.World;
import MWC.GUI.Java3d.WorldPlottingOptions;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

import com.sun.j3d.utils.geometry.Text2D;

abstract public class Label3D extends BranchGroup implements java.beans.PropertyChangeListener, WorldMember
{
  ///////////////////////////
  // member variables
  ///////////////////////////

  /**
   * the transform which sets our location
   */
  protected TransformGroup _myTransform;

  /**
   * the transform which scales us
   */
  protected ScaleTransform _myScaleTransform;

  /**
   * a switch which will contain all of our data - so that we can switch it off
   */
  protected Switch _mySymbol;

  /**
   * a switch containing the text labels - so that we can switch them off
   * individually (there just SOOO slow)
   */
  protected Switch _myLabel;

  /**
   * the text object itself
   */
  protected ColorChangeText2D _theTextObject;

  /**
   * the colour of this participant
   */
  protected Color3f _myColor;

  /**
   * the set of world options we adhere to
   */
  protected WorldPlottingOptions _options;

  /**
   * the last time we plotted
   */
  protected long _lastTime;

  /**
   * the World object, which provides our projection support
   */
  protected World _myWorld;

  /**
   * switch to determine if this object could even be visible right now
   */
  private Switch _validitySwitch;


  ///////////////////////////
  // constructor
  ///////////////////////////
  public Label3D(WorldPlottingOptions options, World world, Object userData)
  {
    _options = options;

    _myWorld = world;

    // listen to property changes in the options
    _options.addListener(this);

    // store the user data
    this.setUserData(userData);

  }


  ///////////////////////////
  // member methods
  ///////////////////////////


  protected void build()
  {
    _myTransform = new TransformGroup();
    this.addChild(_myTransform);

    // set our capabililities
    _myTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    _myTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);


    _validitySwitch = new Switch();
    _validitySwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _validitySwitch.setCapability(Switch.ALLOW_SWITCH_READ);
    _myTransform.addChild(_validitySwitch);

    // create the switch to hold the data
    _mySymbol = new Switch();
    _mySymbol.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _mySymbol.setCapability(Switch.ALLOW_SWITCH_READ);
    _validitySwitch.addChild(_mySymbol);


    // produce the base offset
    setLocation();

    // create the scaling transform
    _myScaleTransform = new ScaleTransform(_myTransform, (WatchableTransformGroup) _myWorld.getTransform());

    //////////////////////////////////////////////////
    // create our components
    //////////////////////////////////////////////////

    // first the billboard ones
    addBillboardComponents(_mySymbol);

    // and now the static ones
    addStaticComponents(_mySymbol);

    // setup listeners
    listenToHost();

    // get our initial statuses
    updated(null);
  }

  /**
   * get the location of the object, and shift our base transform to it
   */
  protected void setLocation()
  {
    WorldLocation home = getLocation();
    Point3d pt = _myWorld.toScreen(home);
    if (pt != null)
    {
      Transform3D t3 = new Transform3D();
      t3.setTranslation(new Vector3d(pt));
      _myTransform.setTransform(t3);
    }
  }


  /**
   * initialise the listening to our host object
   */
  abstract protected void listenToHost();

  /**
   * we are about to close, shut down all listeners
   */
  public void doClose()
  {
    // stop the scale listening for view changes
    _myScaleTransform.doClose();
    _options.removeListener(this);
  }


  /**
   * update the text string in the object
   */
  protected void setText(String val)
  {
    _theTextObject.setString(val);
  }

  /**
   * create the model for this participant
   */
  protected void addBillboardComponents(Group parent)
  {
    // switch to contain the text label
    _myLabel = new Switch();
    _myLabel.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _myLabel.setCapability(Switch.ALLOW_SWITCH_READ);


    // Create the text for the text message, putting it into a slight transform
    Transform3D t3 = new Transform3D();
    t3.setTranslation(new Vector3f(0f, 0f, 0f));
    TransformGroup TextTransform = new TransformGroup();
    TextTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    TextTransform.setTransform(t3);

    _theTextObject = new ColorChangeText2D(" " + getText(),
                                           getColor(),
                                           "Helvetica", 56, Font.PLAIN);
    _theTextObject.setCapability(Text2D.ALLOW_GEOMETRY_WRITE);
    _theTextObject.setCapability(Text2D.ALLOW_APPEARANCE_WRITE);
    _theTextObject.setPickable(false);

    // set the text in the object
    setText(" " + getText());

    // create the billboard
    Billboard bboard = new Billboard(TextTransform);
    bboard.setAlignmentAxis(new Vector3f(0f, 1f, 0f));
    bboard.setAlignmentMode(Billboard.ROTATE_ABOUT_AXIS);
    bboard.setTarget(TextTransform);
    bboard.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.POSITIVE_INFINITY));

    // add to the scaling transform
    _myScaleTransform.addChild(_theTextObject);

    // add any other components
    addOtherBillboardComponents(_myScaleTransform);

    // and put it into the rotating holder
    TextTransform.addChild(bboard);
    TextTransform.addChild(_myScaleTransform);

    // and into the parent
    _myLabel.addChild(TextTransform);
    parent.addChild(_myLabel);
  }


  /**
   * method to add any other (non-text) billboard components to our shape
   */
  abstract protected void addOtherBillboardComponents(ScaleTransform parent);


  /**
   * method to add an static (non-billboard) components to our shape
   */
  abstract protected void addStaticComponents(Group parent);

  /**
   * update the colour used for the text
   */
  protected void updateColor()
  {
    Color3f theCol = getColor();

    // first change the colour of the text
    _theTextObject.setColor(theCol);

    updateSymbolColor(theCol);
  }

  /**
   * update the color of the symbol itself
   *
   * @param theCol the new color to use
   */
  abstract protected void updateSymbolColor(Color3f theCol);

  /**
   * create the model for this participant
   */
  abstract protected Node createFormattedSymbol();

  /**
   * get the colour for this track
   */
  abstract protected Color3f getColor();

  /**
   * get the location
   */
  abstract protected WorldLocation getLocation();

  /**
   * get the text
   */
  abstract protected String getText();

  /**
   * find out if we are visible at this DTG
   */
  abstract protected boolean visibleAt(HiResDate this_time);

  /**
   * callback to indicate that the participant has changed status
   */
  public void updated(HiResDate new_time)
  {
    if (new_time != null)
    {

      boolean isVisible = visibleAt(new_time);
      if (isVisible)
      {
        //set visible
        _validitySwitch.setWhichChild(Switch.CHILD_ALL);
      }
      else
      {
        // set invisible
        _validitySwitch.setWhichChild(Switch.CHILD_NONE);
      }
    }
  }

  /**
   * accessor to set this item to be visible or not visible
   */
  protected void setVisible(boolean isVisible)
  {
    if (isVisible)
    {
      //set visible
      _mySymbol.setWhichChild(Switch.CHILD_ALL);
    }
    else
    {
      // set invisible
      _mySymbol.setWhichChild(Switch.CHILD_NONE);
    }
  }

  /**
   * accessor setting whether the text label is visible
   */
  protected void setLabelVisible(boolean isVisible)
  {
    if (isVisible)
    {
      //set visible
      _myLabel.setWhichChild(Switch.CHILD_ALL);
    }
    else
    {
      // set invisible
      _myLabel.setWhichChild(Switch.CHILD_NONE);
    }
  }

  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt A PropertyChangeEvent object describing the event source
   *            and the property that has changed.
   */

  public void propertyChange(PropertyChangeEvent evt)
  {

  }

  /**
   * add a line to the line array, joining this point to the last
   *
   * @param lines     the list of lines we're building up
   * @param counter   how far the list we've got
   * @param pt        the current location
   * @param origin    the origin for the polygon
   * @param lastPoint the last point we plotted
   * @return the screen location of the new point
   */
  protected static Point3d addThisPoint(LineArray lines,
                                        int counter,
                                        WorldLocation pt,
                                        WorldLocation origin,
                                        Point3d lastPoint,
                                        World myWorld)
  {
    Point3d thisP = createThisPoint(myWorld, pt, origin);
    if (lastPoint != null)
    {
      lines.setCoordinate(counter * 2, lastPoint);
      lines.setCoordinate(counter * 2 + 1, thisP);
    }
    else
      lastPoint = new Point3d();

    lastPoint.set(thisP);

    return lastPoint;

  }

  /**
   * create a 3-d representation of this location, relative to the centre of the polygon
   *
   * @param loc    the point of interest
   * @param origin the centre of the polygon
   * @return the 3-d coordinates of the point in screen coordinates
   */
  protected static Point3d createThisPoint(World myWorld, WorldLocation loc, WorldLocation origin)
  {
    // find the points in screen coords
    Point3d originP = new Point3d(myWorld.toScreen(origin));
    Point3d locP = new Point3d(myWorld.toScreen(loc));

    // what's the delta (since we are plotting from the centre)
    locP.sub(originP);

    return locP;
  }

  /**
   * create the Geometry of the shape (unscaled - using true coordinates)
   *
   * @param thePoints the collection of lists of points we're going to plot
   * @return
   */
  protected static LineArray getUnscaledGeometry(Vector<Vector<Point2D>> thePoints)
  {

    // how many points are in the shape?
    int len = 0;

    // work through the lists
    Iterator<Vector<Point2D>> it = thePoints.iterator();
    while (it.hasNext())
    {
      Collection<Point2D> thisLine = it.next();
      // increment by this number of lines (-1, since we count the number of lines
      // and not the number of vertices)
      len += thisLine.size() - 1;
    }

    // create the lines to put the shape into
    LineArray lines = new LineArray(len * 2, LineArray.COORDINATES);
    lines.setCapability(LineArray.ALLOW_COORDINATE_WRITE);

    // keep track of where in the array we're writing to
    int index = 0;

    // did we find any?
    if (thePoints != null)
    {
      // get the list of lines
      Iterator<Vector<Point2D>> iter = thePoints.iterator();
      while (iter.hasNext())
      {
        // remember the last point, so we can join them up
        Point2D lastP = null;

        // get this list
        Collection<Point2D> thisPath =  iter.next();
        Iterator<Point2D> iter2 = thisPath.iterator();
        while (iter2.hasNext())
        {
          // get this location
          Point2D loc = (Point2D) iter2.next();

          // is this the first pass?
          if (lastP != null)
          {
            lines.setCoordinate(index * 2, new Point3d(lastP.getX(), 0, lastP.getY()));
            lines.setCoordinate(index * 2 + 1, new Point3d(loc.getX(), 0, loc.getY()));
            // and move on to the next one
            index++;
          }

          // and remember the point
          lastP = loc;
        }
      }
    }

    return lines;
  }

  /**
   * create the Geometry of the shape (scaled from world to screen coordiantes)
   *
   * @param theShape the shape we're looking at
   * @return
   */
  protected static LineArray getScaledGeometry(PlainShape theShape, World myWorld)
  {

    // now get the series of data points from the shape
    Collection<WorldLocation> dataPoints = theShape.getDataPoints();

    if (dataPoints == null)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("View 3d", "Sorry, but " + theShape.getName() + " does not have a 3d representation");
      return null;
    }

    // how many points are in the shape?
    int len = dataPoints.size();

    // create the line to put the shape into
    LineArray line = new LineArray(len * 2, LineArray.COORDINATES);
    line.setCapability(LineArray.ALLOW_COORDINATE_WRITE);

    // keep track of where in the array we're writing to
    int index = 0;

    // remember the last point, so we can join them up
    Point3d lastP = null;

    // remember the first location, so we can join up to it again at the end
    WorldLocation firstLocation = null;

    // find the origin of the data object
    WorldLocation origin = theShape.getBounds().getCentre();

    // did we find any?
    if (dataPoints != null)
    {
      Iterator<WorldLocation> iter = dataPoints.iterator();
      while (iter.hasNext())
      {
        // get this location
        WorldLocation loc = (WorldLocation) iter.next();

        // is this the first pass?
        if (lastP == null)
        {
          // get this point, so we can join the next one to it
          lastP = createThisPoint(myWorld, loc, origin);

          // and remember the start point of the polygon
          firstLocation = loc;
        }
        else
        {
          lastP = addThisPoint(line, index++, loc, origin, lastP, myWorld);
        }
      }

      // and back to the start
      addThisPoint(line, index++, firstLocation, origin, lastP, myWorld);
    }

    return line;


  }

  /**
   * create object itself
   *
   * @param lines the list of lines representing our shape/symbol
   * @return a 3d object presenting the lines
   */
  protected static Shape3D createShape(LineArray lines, Color3f theColor)
  {
    // did we get any lines?
    if (lines == null)
    {
      return null;
    }

    // create the shape to hold the geometry
    Shape3D theShape = new Shape3D();

    // configure the shape
    theShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    theShape.setPickable(false);

    // create the shape's appearance
    Appearance app = new Appearance();
    app.setColoringAttributes(new ColoringAttributes(theColor, ColoringAttributes.FASTEST));
    LineAttributes la = new LineAttributes();
    la.setLineWidth(1f);
    app.setLineAttributes(la);
    theShape.setAppearance(app);

    // and insert the geometry
    theShape.setGeometry(lines);

    return theShape;
  }
}
