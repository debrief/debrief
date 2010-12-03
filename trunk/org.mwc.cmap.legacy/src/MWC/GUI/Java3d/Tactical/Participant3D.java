/*
* Created by IntelliJ IDEA.
* User: Ian.Mayo
* Date: Apr 11, 2002
* Time: 11:25:55 AM
* interface which defines information which a 3D participant should be capable of providing
*/
package MWC.GUI.Java3d.Tactical;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import MWC.GUI.Java3d.ColorChangeText2D;
import MWC.GUI.Java3d.ScaleTransform;
import MWC.GUI.Java3d.WatchableTransformGroup;
import MWC.GUI.Java3d.World;
import MWC.GUI.Java3d.WorldPlottingOptions;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Text2D;


public abstract class Participant3D extends BranchGroup implements java.beans.PropertyChangeListener, WorldMember
{
  ///////////////////////////
  // member variables
  ///////////////////////////

  /**
   * the transform which represents our current location
   */
  protected TransformGroup _myTransform;

  /**
   * the transform group to scale the model
   */
  protected TransformGroup _modelStretch;

  /**
   * a switch which will contain all of our data - so that we can switch it off
   */
  private Switch _myData;

  /**
   * the colour of this participant
   */
  protected Color3f _myColor;

  /**
   * the drop-bar for this participant
   */
  protected Shape3D _dropBar;

  /**
   * the holder for the drop-bar
   */
  protected Switch _dropBarHolder;

  /**
   * the holder for the model (so that we can switch models)
   */
  protected Group _modelHolder;

  /**
   * the current location of this participant
   */
  protected Shape3D _currentStat;

  /**
   * the trail holder for this participant
   */
  protected Switch _snailTrailHolder;

  /**
   * the trail itself for this participant
   */
  protected Shape3D _snailTrail;

  /**
   * the model of ownship (which gets rotated)
   */
  protected TransformGroup _ownshipCourseTransform;

  /**
   * the set of world options we adhere to
   */
  protected WorldPlottingOptions _options;

  /**
   * the selection shape for this object (in it's group)
   */
  protected Switch _statusComponents;

  /**
   * the scale component which looks after the status text
   */
  protected ScaleTransform _statusScaleTransform;

  /**
   * the last time we plotted
   */
  protected HiResDate _lastTime;

  /**
   * the World object, which provides our projection support
   */
  protected World _myWorld;

  /**
   * the text which shows the current vessel status
   */
  protected ColorChangeText2D _statusText;

  /**
   * the last recorded location
   */
  protected WorldLocation _lastLocation;

  /**
   * the last recorded course (radsa)
   */
  protected double _lastCourseRads;


  ///////////////////////////
  // constructor
  ///////////////////////////
  public Participant3D(WorldPlottingOptions options, World world)
  {
    _options = options;

    _myWorld = world;

    this.setCapability(Group.ALLOW_CHILDREN_READ);

    // listen to property changes in the options
    _options.addListener(this);
  }

  protected void build()
  {

    // create the transform
    _myTransform = new TransformGroup();

    // and add it
    this.addChild(_myTransform);

    // set our capabililities
    _myTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    _myTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    _myTransform.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

    // create the switch to hold the data
    _myData = new Switch();
    _myData.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _myData.setCapability(Switch.ALLOW_SWITCH_READ);

    _myTransform.addChild(_myData);

    // create the group which will store our (replaceable) model
    _modelHolder = new Group();
    _modelHolder.setCapability(Group.ALLOW_CHILDREN_READ);
    _modelHolder.setCapability(Group.ALLOW_CHILDREN_WRITE);
    _modelHolder.setCapability(Group.ALLOW_CHILDREN_EXTEND);

    // create the text scale transformation
    _statusScaleTransform = new ScaleTransform(_myTransform, (WatchableTransformGroup) _myWorld.getTransform());

    // create the ownship transformation
    _ownshipCourseTransform = new TransformGroup();
    _ownshipCourseTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    _ownshipCourseTransform.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    _myData.addChild(_ownshipCourseTransform);

    // create and colour the model
    buildModel();
    _ownshipCourseTransform.addChild(_modelHolder);

    // create our other components
    createDropBar();
    createSnailTrail();
    _myData.addChild(createStatusText());

    // setup listeners
    listenToHost();

    // get our initial statuses
    updated(null);
  }


  ///////////////////////////
  // member methods
  ///////////////////////////


  /**
   * replace the model used in our object
   */
  @SuppressWarnings("rawtypes")
	protected void buildModel()
  {
    // remove any existing model
    Enumeration enumer = _modelHolder.getAllChildren();
    while (enumer.hasMoreElements())
    {
      Node child = (Node) enumer.nextElement();
      if (child instanceof BranchGroup)
      {
        BranchGroup bg = (BranchGroup) child;
        bg.detach();
      }
    }

    // get ready to stretch the model
    // (the model units should be in meters)
    _modelStretch = new TransformGroup();
    _modelStretch.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    // create the new model
    Node newModel = createModel(!_options.getShowComplexModels());

    // did we get the model?
    if (newModel != null)
    {
      newModel.setCapability(Group.ALLOW_CHILDREN_READ);

      // change the model so that all of it's children may be read
      setChildCapabilities(newModel);

      // colour the new model
      colourModel(newModel);

      // insert the model
      _modelStretch.addChild(newModel);
    }

    // set the scale of the stretch
    setModelStretch();

    // put it into a BranchGraph
    BranchGroup mGroup = new BranchGroup();
    mGroup.setCapability(BranchGroup.ALLOW_DETACH);
    mGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    mGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    mGroup.addChild(_modelStretch);

    // store the new model
    _modelHolder.addChild(mGroup);
  }


  /**
   * set the capability bits for the supplied model in order to allow child reading
   */
  @SuppressWarnings("rawtypes")
	private void setChildCapabilities(Node node)
  {
    if (node instanceof Group)
    {
      Group grp = (Group) node;
      grp.setCapability(Group.ALLOW_CHILDREN_READ);
      Enumeration enumer = grp.getAllChildren();
      while (enumer.hasMoreElements())
      {
        Node thisN = (Node) enumer.nextElement();
        setChildCapabilities(thisN);
      }
    }
    else if (node instanceof Shape3D)
    {
      Shape3D theS = (Shape3D) node;
      theS.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    }
  }

  /**
   * apply our colour to the model and it's children
   */
  @SuppressWarnings("rawtypes")
	protected void colourModel(Node model)
  {
    if (model instanceof Group)
    {
      Group theGrp = (Group) model;
      try
      {
        Enumeration enumer = theGrp.getAllChildren();

        while (enumer.hasMoreElements())
        {
          Node thisChild = (Node) enumer.nextElement();

          // is this editable?
          if (thisChild instanceof Group)
          {
            Group anotherGrp = (Group) thisChild;

            // recursively set it's colours
            colourModel(anotherGrp);
          }
          else if (thisChild instanceof Primitive)
          {
            Primitive thisPrim = (Primitive) thisChild;
            Color3f col = new Color3f();
            col.set(getColor());
            World.setColor(thisPrim, col);
          }
          else if (thisChild instanceof Shape3D)
          {
            Shape3D thisShape = (Shape3D) thisChild;
            Color3f col = new Color3f();
            col.set(getColor());
            World.setColor(thisShape, col);

            PolygonAttributes pa = new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0);
            thisShape.getAppearance().setPolygonAttributes(pa);
          }
        }

      }
      catch (javax.media.j3d.CapabilityNotSetException ce)
      {
      }
    }
  }

  /**
   * initialise the listening to our host object
   */
  abstract protected void listenToHost();

  /**
   * stop listening to the host - don't forget to call this in the parent!
   */
  public void doClose()
  {
    _options.removeListener(this);
    _statusScaleTransform.doClose();
  }

  ///////////////////////////
  // buffered versions of status accessors which return the last recorded location and course
  ///////////////////////////

  /**
   * retrieve the current location of the host
   */
  public WorldLocation getLocation()
  {
    return _lastLocation;
  }

  /**
   * retrieve course (in rads) or -999 if unavailable
   */
  public double getCourse()
  {
    return _lastCourseRads;
  }


  /**
   * retrieve the current location of the host
   */
  abstract protected WorldLocation getLocation(HiResDate new_time);

  /**
   * retrieve course (in rads) or -999 if unavailable
   */
  abstract protected double getCourse(HiResDate new_time);

  /**
   * build the snail trail using the collection
   */
  abstract protected void buildSnailTrail(HiResDate start_time, HiResDate end_time);

  /**
   * get the colour for this track
   */
  abstract protected java.awt.Color getColor();

  /**
   * get the name of this track
   */
  abstract public String getName();

  /**
   * get the current status for this track
   */
  abstract protected String getStatusText(HiResDate new_time);

  /**
   * set/clear the selected highlight for this object
   */
  public boolean getSelected()
  {
    boolean res = false;

    if (_statusComponents.getWhichChild() == Switch.CHILD_NONE)
      res = false;
    else
      res = true;

    return res;
  }

  /**
   * set/clear the selected highlight for this object
   */
  public void setSelected(boolean selected)
  {
    if (selected)
      _statusComponents.setWhichChild(Switch.CHILD_ALL);
    else
      _statusComponents.setWhichChild(Switch.CHILD_NONE);
  }

  /**
   * whether to show the snail trail
   */
  public void setShowSnail(boolean visible)
  {
    if (visible)
      _snailTrailHolder.setWhichChild(Switch.CHILD_ALL);
    else
      _snailTrailHolder.setWhichChild(Switch.CHILD_NONE);
  }

  /**
   * the plotting options have changed, see what they are
   */
  public void propertyChange(PropertyChangeEvent evt)
  {

    String name = evt.getPropertyName();
    if (name.equals("ShowDropBars"))
    {
      Boolean val = (Boolean) evt.getNewValue();
      if (val.booleanValue())
        _dropBarHolder.setWhichChild(Switch.CHILD_ALL);
      else
        _dropBarHolder.setWhichChild(Switch.CHILD_NONE);

    }
    else if (name.equals("ShowComplexModels"))
    {
      // change the level of detail shown for the models
      buildModel();
    }
    else if (name.equals("ShowSnailTrail"))
    {
      Boolean val = (Boolean) evt.getNewValue();
      // set the visibility of the snail trail
      setShowSnail(val.booleanValue());
    }
    else if (name.equals("SnailTrailLength"))
    {
      updated(new HiResDate(_lastTime));
    }
    else if (name.equals("SnailLineWidth"))
    {
      setSnailLineAppearance();
    }
    else if (name.equals("ModelStretch"))
    {
      setModelStretch();
    }
    else if (name.equals("TextSize"))
    {
      setStatusLabelSize();
    }
    else if (name.equals("ShowVesselStatuses"))
    {
      Boolean val = (Boolean) evt.getNewValue();
      if (val.booleanValue())
        _statusComponents.setWhichChild(Switch.CHILD_ALL);
      else
        _statusComponents.setWhichChild(Switch.CHILD_NONE);
    }


  }

  /**
   * create the selected icon for this participant
   */
  protected Node createStatusText()
  {
    // the switch, to contain our data - so that we can switch it on and off
    _statusComponents = new Switch();
    _statusComponents.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _statusComponents.setCapability(Switch.ALLOW_SWITCH_READ);

    // Create the text for the text message, putting it into a slight transform
    Transform3D t3 = new Transform3D();
    t3.setTranslation(new Vector3f(0f, 0f, 0f));
    TransformGroup TextHolder = new TransformGroup();
    TextHolder.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    TextHolder.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    TextHolder.setTransform(t3);

    _statusText = new ColorChangeText2D(" ",
                                        new Color3f(getColor()),
                                        "Helvetica",
                                        _options.getTextSize().getCurrent(), Font.PLAIN);
    _statusText.setCapability(Text2D.ALLOW_GEOMETRY_WRITE);
    _statusText.setCapability(Text2D.ALLOW_APPEARANCE_WRITE);
    _statusText.setPickable(false);

    // create the billboard
    Billboard bboard = new Billboard(TextHolder);
    bboard.setAlignmentAxis(new Vector3f(0f, 1f, 0f));
    bboard.setAlignmentMode(Billboard.ROTATE_ABOUT_AXIS);
    bboard.setTarget(TextHolder);
    bboard.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.POSITIVE_INFINITY));

    // add to the scaling transform
    _statusScaleTransform.addChild(_statusText);

    TextHolder.addChild(bboard);
    TextHolder.addChild(_statusScaleTransform);

    _statusComponents.addChild(TextHolder);
    _statusComponents.setWhichChild(Switch.CHILD_NONE);

    return _statusComponents;

  }

  /**
   * create the drop-bar for this participant
   */
  protected void createDropBar()
  {
    _dropBarHolder = new Switch();
    _dropBarHolder.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _dropBarHolder.setCapability(Switch.ALLOW_SWITCH_READ);

    // produce the line itself
    LineArray line = new LineArray(2, LineArray.COORDINATES);
    line.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
    line.setCoordinate(0, new Point3d(0.0, 0.0, 0.0));
    line.setCoordinate(1, new Point3d(0, 0, 0.0));

    // create the object
    _dropBar = new Shape3D();
    _dropBar.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    _dropBar.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    _dropBar.setPickable(false);
    _dropBar.setGeometry(line);

    // set the formatting
    Appearance app = new Appearance();
    app.setColoringAttributes(new ColoringAttributes(new Color3f(0.6f, 0.6f, 0.6f), ColoringAttributes.FASTEST));
    app.setLineAttributes(new LineAttributes(1f, LineAttributes.PATTERN_SOLID, true));
    _dropBar.setAppearance(app);

    _dropBarHolder.addChild(_dropBar);

    // start off invisible
    _dropBarHolder.setWhichChild(Switch.CHILD_ALL);

    // and store it
    _myData.addChild(_dropBarHolder);
  }

  public String toString()
  {
    return "New Track";
  }

  /**
   * create the model for this participant
   */
  protected Node createModel(boolean low_res)
  {
    Cone c2 = new Cone(World.BASE_SIZE / 4, World.BASE_SIZE, Primitive.GEOMETRY_NOT_SHARED, null);

    Color3f col = new Color3f();
    col.set(getColor());
    World.setColor(c2, col);

    c2.getShape(Cone.BODY).setUserData(this);
    c2.getShape(Cone.CAP).setUserData(this);
    c2.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    c2.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
    c2.setCapability(Primitive.ALLOW_BOUNDS_READ);
    c2.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    World.setShapeCapabilities(c2.getShape(Cone.BODY));
    World.setShapeCapabilities(c2.getShape(Cone.CAP));

    _ownshipCourseTransform.addChild(c2);
    return _ownshipCourseTransform;

  }

  /**
   * create the snail-trail for this participant
   */
  protected void createSnailTrail()
  {
    _snailTrailHolder = new Switch();
    _snailTrailHolder.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _snailTrailHolder.setCapability(Switch.ALLOW_SWITCH_READ);
    // Create the text for the text message, putting it into a slight transform
    Transform3D t3 = new Transform3D();
    t3.setTranslation(new Vector3f(0f, 0f, 0f));
    TransformGroup TextHolder = new TransformGroup();
    TextHolder.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    TextHolder.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    TextHolder.setTransform(t3);

    // produce the line itself
    LineArray line = new LineArray(2, LineArray.COORDINATES);
    line.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
    line.setCoordinate(0, new Point3d(1.0, 6.0, 0.0));
    line.setCoordinate(1, new Point3d(0, -2, 1.0));

    // create the object
    _snailTrail = new Shape3D();
    _snailTrail.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    _snailTrail.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    _snailTrail.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    _snailTrail.setPickable(false);
    _snailTrail.setGeometry(line);

    // set the formatting
    setSnailLineAppearance();

    _snailTrailHolder.addChild(_snailTrail);

    // start off invisible
    _snailTrailHolder.setWhichChild(Switch.CHILD_ALL);

    // and store it
    _myData.addChild(_snailTrailHolder);
  }

  /**
   * method for when the track has changed colour
   */
  protected void updateColor()
  {
    // first the snail trail
    setSnailLineAppearance();

    // now the status text
    _statusText.setColor(new Color3f(getColor()));

    // now the model
    colourModel(_modelHolder);
  }

  /**
   * method for when an object has changed it's visibility
   */
  protected void updateVisibility(boolean isVisible)
  {
    if (isVisible)
    {
      _myData.setWhichChild(Switch.CHILD_ALL);
    }
    else
    {
      _myData.setWhichChild(Switch.CHILD_NONE);
    }
  }


  /**
   * method to set the appearance of the line using the width in the options editor
   */
  protected void setSnailLineAppearance()
  {
    // retrieve the current width
    int line_wid = _options.getSnailLineWidth().getCurrent();

    // create a new appearance
    Appearance app = new Appearance();
    Color3f trailCol = new Color3f(getColor().darker().darker());
    app.setColoringAttributes(new ColoringAttributes(trailCol, ColoringAttributes.FASTEST));

    // set the width
    app.setLineAttributes(new LineAttributes((float) line_wid, LineAttributes.PATTERN_SOLID, true));

    // update the object
    _snailTrail.setAppearance(app);
  }

  /**
   * the amount to stretch the model
   */
  protected void setModelStretch()
  {
    // retrieve the model stretch
    double stretch = _options.getModelStretch().intValue();

    // what is the transform factor
    double scaleF = _myWorld.getProjection().getScaleFactor();

    // convert scale factor to metres
    double scaleMetres = MWC.Algorithms.Conversions.Degs2m(scaleF);

    // scale the transform
    Transform3D t3 = new Transform3D();
    t3.setScale(1d / scaleMetres * stretch);
    _modelStretch.setTransform(t3);
  }

  /**
   * the size to make the text label
   */
  public void setStatusLabelSize()
  {
    // retrieve the model stretch
    int size = _options.getTextSize().getCurrent();

    _statusText.setFontSize(size);
  }


  /**
   * rotate the ownship model to this direction
   */
  protected void setModelRotation(double rads)
  {
    // convert to anti-clockwise
    rads = -rads;
    rads += Math.PI / 2;
    Transform3D t3 = new Transform3D();
    double cosR = Math.cos(rads);
    double sinR = Math.sin(rads);
    t3.setRotation(new Matrix3d(cosR, 0, sinR,
                                0, 1, 0,
                                -sinR, 0, cosR));


    Transform3D otherT3 = new Transform3D();
    double rot = 270;
    double rad = Math.toRadians(rot);
    otherT3.setRotation(new Matrix3d(Math.cos(rad), -Math.sin(rad), 0, Math.sin(rad), Math.cos(rad), 0, 0, 0, 1));
    //  t3.mul(otherT3);

    _ownshipCourseTransform.setTransform(t3);
  }

  /**
   * callback to indicate that the participant has changed status
   */
  public void updated(HiResDate new_time)
  {
    // find the new location
    _lastLocation = getLocation(new_time);

    if (_lastLocation == null)
    {
      this.updateVisibility(false);
      return;
    }
    else
    {
      this.updateVisibility(true);
    }

    // get the course
    _lastCourseRads = getCourse(new_time);

    // rotate the ownship model
    setModelRotation(_lastCourseRads);

    // translate this world location
    Point3d p3 = _myWorld.toScreen(_lastLocation);

    // update our transform group (which will move the model)
    Transform3D t3 = new Transform3D();
    _myTransform.getTransform(t3);
    t3.setTranslation(new Vector3d(p3));
    _myTransform.setTransform(t3);

    ///////////////////////////////////////////
    // drop-bar
    ///////////////////////////////////////////
    // update the "foot" geometry
    LineArray lArr = (LineArray) _dropBar.getGeometry();
    lArr.setCoordinate(0, new Point3d(0, 0, 0.0));
    lArr.setCoordinate(1, new Point3d(0, -p3.y, 0.0));
    _dropBar.setGeometry(lArr);

    // produce the start time for the trail
    if (new_time != null)

    {
      HiResDate start_time = new HiResDate(0, new_time.getMicros() - (long) _options.getSnailTrailLength().getValueIn(Duration.MICROSECONDS));

      // update the trail
      buildSnailTrail(start_time, new_time);

      // remember the time
      _lastTime = new HiResDate(new_time);

      // update the status text
      _statusText.setString(getStatusText(new_time));
    }
  }


}
