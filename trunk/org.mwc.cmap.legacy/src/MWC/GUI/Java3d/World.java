/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 10, 2002
 * Time: 2:46:36 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.j3d.geom.overlay.LabelOverlay;

import MWC.GUI.BaseLayer;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.ETOPO.BathyProvider;
import MWC.GUI.Java3d.Tactical.Participant3D;
import MWC.GUI.Java3d.Tactical.WorldMember;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class World extends BranchGroup implements PickSelectBehaviour.SelectionListener,
  java.beans.PropertyChangeListener
{

  ///////////////////////////
  // member variables
  ///////////////////////////

  /**
   * the amount to stretch
   */
  private final static double DEPTH_FACTOR = 10;


  /**
   * the canvas we are painting on
   */
  protected Canvas3D _myCanvas = null;

  /**
   * the parent for all of our data
   */
  protected WatchableTransformGroup _parentTransform;

  /**
   * our infinite bounds object - to make things work all of the time
   */
  protected static BoundingSphere _infiniteBounds = new BoundingSphere(new Point3d(0, 0, 0), Double.POSITIVE_INFINITY);


  /**
   * the currently selected object
   */
  protected Participant3D _selectedShape = null;

  /**
   * the highlight object
   */
  protected TransformGroup _highlightShape = null;

  /**
   * the ocean surface (shaded)
   */
  protected Switch _shadedOceanSurface = null;

  /**
   * the bathy (surface)
   */
  protected Switch _bathymetryLines = null;

  /**
   * the bathy (surface)
   */
  protected Switch _bathymetrySurface = null;

  /**
   * the gridded sea surface Switch, which allows use to switch the grid on and off
   */
  protected Switch _griddedSurfaceSwitch = null;

  /**
   * a switch to hold the whole bathy object
   */
  protected Switch _completeBathy = null;

  /**
   * the shape which represents the sea surface
   */
  protected Shape3D _gridSurfaceShape = null;

  /**
   * the basic size we use for objects
   */
  public final static float BASE_SIZE = 0.6f;

  /**
   * the size of the ocean we plot
   */
  final static float OCEAN_DIAMETER = 500;

  /**
   * the depth of the ocean we plot
   */
  final float OCEAN_DEPTH = 50;

  /**
   * the plotting options we adhere to
   */
  protected WorldPlottingOptions _options = null;

  /**
   * the projection we use to convert to screen units
   */
  public DoubleProjection _projection = null;

  /**
   * the object which does picking for us
   */
  protected PickSelectBehaviour _pickSelector = null;

  /**
   * the list of tracks we store
   */
  protected Group _myTracks = null;

  /**
   * the list of labels we store
   */
  protected Group _myLabels = null;

  /**
   * the bathy we are going to plot
   */
  BathyProvider _bathy = null;


  LabelOverlay overlay;

  /////////////////////////
  // constructor
  /////////////////////////

  /**
   * Constructor for the World object
   *
   * @param canvas    The canvas we're going to paint to
   * @param dataArea  The current data area
   * @param bathyData The provider for the BathyData
   */
  public World(Canvas3D canvas, WorldArea dataArea, BathyProvider bathyData)
  {
    // store the canvas
    _myCanvas = canvas;

    // store the bathy data
    _bathy = bathyData;

    //
    this.setCapability(BranchGroup.ALLOW_DETACH);

    _options = new WorldPlottingOptions();
    _options.addListener(this);

    // setup the parent group
    Transform3D transform = new Transform3D();
    _parentTransform = new WatchableTransformGroup();
    _parentTransform.setTransform(transform);
    _parentTransform.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    _parentTransform.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

    setTransformGroupCapabilities(_parentTransform);
    super.addChild(_parentTransform);

    // produce our two lists
    _myTracks = new Group();
    _myTracks.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    _myTracks.setCapability(Group.ALLOW_CHILDREN_READ);
    _myTracks.setCapability(Group.ALLOW_CHILDREN_WRITE);

    _myLabels = new Group();
    _myLabels.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    _myLabels.setCapability(Group.ALLOW_CHILDREN_READ);
    _myLabels.setCapability(Group.ALLOW_CHILDREN_WRITE);

    // insert them
    _parentTransform.addChild(_myTracks);
    _parentTransform.addChild(_myLabels);

    //set up the projection
    _projection = new DoubleProjection();

    final int PROPORTION_OF_SCREEN_COVERED_BY_DATA = 3;

    _projection.setScreenArea(new java.awt.Dimension((int) OCEAN_DIAMETER / PROPORTION_OF_SCREEN_COVERED_BY_DATA,
                                                     (int) OCEAN_DIAMETER / PROPORTION_OF_SCREEN_COVERED_BY_DATA));
    _projection.setDataArea(dataArea);

    _projection.zoom(0.0d);

    // initialise the depth stretch
    _projection.setDepthScale(_projection.getDepthScale() / DEPTH_FACTOR);

    // add the extra bits
    addLightSources();
    addMouseHandlers();

    // and the surface

    _griddedSurfaceSwitch = getSurface(_options, _projection);
    addThisItem(_griddedSurfaceSwitch);

    _shadedOceanSurface = WorldBathySupport.addOcean();
    addThisItem(_shadedOceanSurface);
    addBackground();

    // put in the bathy
    addBathyHolder();

    /////////////////////////////////
    // do picking
    /////////////////////////////////
    PickSelectBehaviour pickSelect = createPicking(this, canvas);
    _parentTransform.addChild(pickSelect);




    // todo: get this overlay working
    overlay = new LabelOverlay(canvas,
                               new Rectangle(10, 10, 100, 50),
                               "hello world")
    {
      public void paint(Graphics2D g)
      {
        super.paint(g);
      }
    };
    overlay.setColor(Color.red);
    overlay.setVisible(true);
    _parentTransform.addChild(overlay.getRoot());
    overlay.repaint();

    overlay.initialize();


  }
  ///////////////////////////
  // member methods
  ///////////////////////////

  /**
   * populate the bathy object
   */
  public void populateBathy(SpatialRasterPainter data, SimpleUniverse universe)
  {

    // check we have received the bathy
    if (universe == null)
    {

      System.out.println("UNIVERSE NOT RECEIVED");
      return;
    }

    // so, we need to get a fresh geometry array using the new stretch
    double _depthScale = _projection.getDepthScale();

    // ok, get the array of depths, format them, and return them as a geometry
    GeometryArray geom = WorldBathySupport.calculateAndCreateGeometryData(_depthScale,
                                                                          _projection,
                                                                          data);

    Shape3D fillShape = WorldBathySupport.createBathyShape(geom, new PolygonAttributes(PolygonAttributes.POLYGON_FILL,
                                                                                       PolygonAttributes.CULL_BACK, 0f));
    Shape3D lineShape = WorldBathySupport.createBathyLines(geom, new PolygonAttributes(PolygonAttributes.POLYGON_LINE,
                                                                                       PolygonAttributes.CULL_BACK, 0f));

    // ok, get ready to make the change
    this.detachForRemoval();

    // and stick it into the bathy
    _bathymetrySurface.addChild(fillShape);
    _bathymetryLines.addChild(lineShape);

    // ok, reattach our data objects
    this.andReattach(universe);

    // is it currently visible?
    updateBathyVis(data.getVisible());

    // and lastly listen out for the whole bathy becoming visible
    data.addPropertyChangeListener(new java.beans.PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent evt)
      {
        if (evt.getPropertyName() == BaseLayer.VISIBILITY_CHANGE)
        {
          Boolean val = (Boolean) evt.getNewValue();
          updateBathyVis(val.booleanValue());
        }
      }
    }, BaseLayer.VISIBILITY_CHANGE);
  }

  /**
   * method to update the bathy visibility
   */
  void updateBathyVis(boolean val)
  {
    if (val)
      _bathymetrySurface.setWhichChild(Switch.CHILD_ALL);
    else
      _bathymetrySurface.setWhichChild(Switch.CHILD_NONE);
  }


  /**
   * create and add the bathy object
   */
  public void addBathyHolder()
  {

    _completeBathy = new Switch();
    _completeBathy.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _completeBathy.setCapability(Switch.ALLOW_CHILDREN_READ);
    _completeBathy.setWhichChild(Switch.CHILD_ALL);

    // the switch to contain the ocean
    _bathymetrySurface = new Switch();
    _bathymetrySurface.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _bathymetrySurface.setCapability(Switch.ALLOW_CHILDREN_READ);
    if (_options.getShowBathySurface())
    {
      _bathymetrySurface.setWhichChild(Switch.CHILD_ALL);
    }
    else
    {
      _bathymetrySurface.setWhichChild(Switch.CHILD_NONE);
    }

    _bathymetryLines = new Switch();
    _bathymetryLines.setCapability(Switch.ALLOW_SWITCH_WRITE);
    _bathymetryLines.setCapability(Switch.ALLOW_SWITCH_READ);
    _bathymetryLines.setCapability(Switch.ALLOW_CHILDREN_READ);
    if (_options.getShowBathyLines())
    {
      _bathymetryLines.setWhichChild(Switch.CHILD_ALL);
    }
    else
    {
      _bathymetryLines.setWhichChild(Switch.CHILD_NONE);
    }


    //
    //
    //
    //
    //    // so, we need to get a fresh geometry array using the new stretch
    //    double _depthScale = _projection.getDepthScale();
    //
    //    // ok, get the array of depths, format them, and return them as a geometry
    //    GeometryArray geom = WorldBathySupport.calculateAndCreateGeometryData(_depthScale,
    //                                                                _projection,
    //                                                                _bathy);
    //
    //    Shape3D fillShape = WorldBathySupport.createBathyShape(geom, new PolygonAttributes(PolygonAttributes.POLYGON_FILL,
    //                                                                     PolygonAttributes.CULL_BACK, 0f));
    //    Shape3D lineShape = WorldBathySupport.createBathyLines(geom, new PolygonAttributes(PolygonAttributes.POLYGON_LINE,
    //                                                                     PolygonAttributes.CULL_BACK, 0f));
    //
    //    // and stick it into the bathy
    //    _bathymetrySurface.addChild(fillShape);
    //    _bathymetryLines.addChild(lineShape);


    // and store this data
    _completeBathy.addChild(_bathymetrySurface);
    _completeBathy.addChild(_bathymetryLines);
    addThisItem(_completeBathy);
  }


  /**
   * get the current list of tracks
   */
  public Group getTracks()
  {
    return _myTracks;
  }

  /**
   * get the list of labels
   */
  public Group getLabels()
  {
    return _myLabels;
  }

  /**
   * retrieve the projection
   */
  public DoubleProjection getProjection()
  {
    return _projection;
  }

  /**
   * close operation, shut all listeners
   *
   * @see WorldMember#doClose
   */
  @SuppressWarnings("unchecked")
	public void doClose()
  {

    // stop everything which listens to the stepper control
    Enumeration enumer = _parentTransform.getAllChildren();
    while (enumer.hasMoreElements())
    {
      Node thisN = (Node) enumer.nextElement();
      if (thisN instanceof WorldMember)
      {
        WorldMember c3 = (WorldMember) thisN;
        c3.doClose();
      }
    }

    // picking only has 1 listener, so we assign it to null
    _pickSelector.setListener(null);

    // options
    _options.removeListener(this);
    _options = null;
  }

  /**
   * listen out for property changes - particularly so that we can stretch the world depth
   */
  public void propertyChange(PropertyChangeEvent evt)
  {
    String name = evt.getPropertyName();
    if (name.equals("DepthStretch"))
    {
      BoundedInteger newD = (BoundedInteger) evt.getNewValue();
      int newStretch = newD.getCurrent();
      setDepthStretch(newStretch);

      // and try to trigger some kind of screen refresh
    }
    else if (name.equals("ShowFoggyOcean"))
    {
      Boolean val = (Boolean) evt.getNewValue();
      if (val.booleanValue())
        _shadedOceanSurface.setWhichChild(Switch.CHILD_ALL);
      else
        _shadedOceanSurface.setWhichChild(Switch.CHILD_NONE);

    }
    else if (name.equals("ShowSeaSurface"))
    {
      Boolean val = (Boolean) evt.getNewValue();
      if (val.booleanValue())
        _griddedSurfaceSwitch.setWhichChild(Switch.CHILD_ALL);
      else
        _griddedSurfaceSwitch.setWhichChild(Switch.CHILD_NONE);
    }
    else if (name.equals("ShowBathySurface"))
    {
      Boolean val = (Boolean) evt.getNewValue();
      if (val.booleanValue())
        _bathymetrySurface.setWhichChild(Switch.CHILD_ALL);
      else
        _bathymetrySurface.setWhichChild(Switch.CHILD_NONE);
    }
    else if (name.equals("ShowBathyLines"))
    {
      Boolean val = (Boolean) evt.getNewValue();
      if (val.booleanValue())
        _bathymetryLines.setWhichChild(Switch.CHILD_ALL);
      else
        _bathymetryLines.setWhichChild(Switch.CHILD_NONE);
    }
    else if (name.equals("GridDelta"))
    {
      // create the geometry
      LineArray lines = WorldBathySupport.createGrid(_options.getGridDelta(), _projection, OCEAN_DIAMETER);

      // put it in our shape
      _gridSurfaceShape.setGeometry(lines);

    }
  }

  /**
   * update the depth stretch (to aid readability)
   *
   * @param newStretch the new factor to apply (not accumulative)
   */
  private void setDepthStretch(int newStretch)
  {
    _projection.setDepthStretch(newStretch);

//    // so, we need to get a fresh geometry array using the new stretch
//    double currentStretch = _projection.getDepthScale();
//
//    // apply this stretch to is
//    currentStretch -= newStretch;
//
//    // ok, get the array of depths, format them, and return them as a geometry
//    GeometryArray geom = WorldBathySupport.calculateAndCreateGeometryData(currentStretch,
//                                                                          _projection,
//                                                                          _bathy);
//
//    // update the geometry in the surface object
//    Enumeration enumer = _bathymetrySurface.getAllChildren();
//    while (enumer.hasMoreElements())
//    {
//      Shape3D thisShape = (Shape3D) enumer.nextElement();
//      thisShape.setGeometry(geom);
//    }
//
//    // and in the set of lines
//    enumer = _bathymetryLines.getAllChildren();
//    while (enumer.hasMoreElements())
//    {
//      Shape3D thisShape = (Shape3D) enumer.nextElement();
//      thisShape.setGeometry(geom);
//    }
  }


  /**
   * set the current world area
   */
  public void setWorldArea(MWC.GenericData.WorldArea area)
  {
    _projection.setDataArea(area);
  }

  public WorldPlottingOptions getWorldPlottingOptions()
  {
    return _options;
  }

  public TransformGroup getTransform()
  {
    return _parentTransform;
  }

  public void newSelection(PickResult result)
  {
    Node nearest = result.getObject();

    if (nearest instanceof Shape3D)
    {
      Shape3D near = (Shape3D) nearest;

      Object data = near.getUserData();

      if (data instanceof Participant3D)
      {
        Participant3D c3 = (Participant3D) data;

        if (_selectedShape == null)
        {
          // just select it anyway
          c3.setSelected(true);
          _selectedShape = c3;
        }
        else
        {
          // clear the existing selectiojn
          _selectedShape.setSelected(false);

          // is this the existing selection?
          if (_selectedShape == c3)
          {
            // forget about it
            _selectedShape = null;
          }
          else
          {
            // no, select the new one
            _selectedShape = c3;
            _selectedShape.setSelected(true);
          }
        }

        // so, do we have anything to declare?
        if (_selectedShape != null)
          System.out.println(_selectedShape);

      }
    }
  }


  /**
   * create our picking behaviour
   */
  private PickSelectBehaviour createPicking(BranchGroup objRoot, Canvas3D canvas)
  {
    BoundingSphere infiniteBounds = new BoundingSphere(new Point3d(0d, 0d, 0d), Double.POSITIVE_INFINITY);
    _pickSelector = new PickSelectBehaviour(objRoot, canvas, infiniteBounds);
    _pickSelector.setMode(PickTool.GEOMETRY);
    _pickSelector.setListener(this);
    return _pickSelector;
  }


  /**
   * create the backdrop to the application
   */
  protected void addBackground()
  {
    Background backg = new Background(0.25f, 0.55f, 0.55f);

    backg.setApplicationBounds(_infiniteBounds);
    addThisItem(backg);
  }

  /**
   * create the mouse handlers for this group
   */
  protected void addMouseHandlers()
  {

    /////////////////////////////////
    // general mouse rotate
    /////////////////////////////////
    MouseRotateX mr2 = new MouseRotateX();
    mr2.setTransformGroup(_parentTransform);
    mr2.setSchedulingBounds(_infiniteBounds);
    _parentTransform.addChild(mr2);

    /////////////////////////////////
    // mouse zoom
    /////////////////////////////////
    MouseZoom mz = new MouseZoom();
    //   mz.setupCallback(this);
    mz.setTransformGroup(_parentTransform);
    mz.setSchedulingBounds(_infiniteBounds);
    _parentTransform.addChild(mz);

    /////////////////////////////////
    // mouse translate
    /////////////////////////////////
    MouseTranslate mt = new MouseTranslate();
    //   mt.setupCallback(this);
    mt.setTransformGroup(_parentTransform);
    mt.setSchedulingBounds(_infiniteBounds);
    addThisItem(mt);
  }

  /** handle the mouse callbacks
   *
   */
  //  public void transformChanged(int type, Transform3D transform) {
  //  //  System.out.println("type:" + type);
  //  }

  /**
   * create the light sources for this group
   */
  protected void addLightSources()
  {

    DirectionalLight lightD1 = new DirectionalLight();
    lightD1.setInfluencingBounds(_infiniteBounds);
    Vector3f direction = new Vector3f(-1.0f, -1.0f, -1.0f);
    direction.normalize();
    lightD1.setDirection(direction);
    lightD1.setColor(new Color3f(1.0f, 1.0f, 1.0f));
    _parentTransform.addChild(lightD1);

    AmbientLight ambLight = new AmbientLight();
    ambLight.setInfluencingBounds(_infiniteBounds);
    ambLight.setColor(new Color3f(1.0f, 1.0f, 1.0f));
    addThisItem(ambLight);
  }

  /**
   * enable picking within this transform group
   */
  protected static void setTransformGroupCapabilities(TransformGroup tg)
  {
    // transform
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    // picking
    tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
  }


  /**
   * set the shape-related accessibility aspects of this shape
   */
  public static void setShapeCapabilities(Shape3D shape)
  {
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    shape.setCapability(Shape3D.ALLOW_BOUNDS_READ);
    shape.getGeometry().setCapability(Geometry.ALLOW_INTERSECT);   //     @@@@ commented out because it was causing prob when opening 2nd view
  }

  /**
   * set the light-related colours for this shape
   */
  public static void setColor(Primitive shape, Color3f color)
  {
    Appearance app = new Appearance();
    app.setCapability(Appearance.ALLOW_MATERIAL_READ);
    app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    Material mat = new Material();
    mat.setAmbientColor(color.x * 0.4f, color.y * 0.4f, color.z * 0.4f);
    mat.setDiffuseColor(color.x * 0.6f, color.y * 0.6f, color.z * 0.6f);
    mat.setCapability(Material.ALLOW_COMPONENT_READ);
    mat.setCapability(Material.ALLOW_COMPONENT_WRITE);

    app.setMaterial(mat);
    shape.setAppearance(app);
    app.setColoringAttributes(new ColoringAttributes(color, ColoringAttributes.FASTEST));
  }

  /**
   * set the light-related colours for this shape
   */
  public static void setColor(Shape3D shape, Color3f color)
  {
    Appearance app = new Appearance();
    app.setCapability(Appearance.ALLOW_MATERIAL_READ);
    app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    Material mat = new Material();
    mat.setAmbientColor(color.x * 0.4f, color.y * 0.4f, color.z * 0.4f);
    mat.setDiffuseColor(color.x * 0.6f, color.y * 0.6f, color.z * 0.6f);
    mat.setCapability(Material.ALLOW_COMPONENT_READ);
    mat.setCapability(Material.ALLOW_COMPONENT_WRITE);

    app.setMaterial(mat);
    shape.setAppearance(app);
    app.setColoringAttributes(new ColoringAttributes(color, ColoringAttributes.FASTEST));
  }

  ///////////////////////////
  // add new features
  ///////////////////////////

  public void addThisItem(Node node)
  {
    _parentTransform.addChild(node);
  }

  public void addChild(Node node)
  {
    addThisItem(node);
  }


  /**
   * see if we contain this data item
   *
   * @param userData the item we're looking for
   * @return the node containing the item (or null)
   */
  public Node containsThis(Plottable userData)
  {
    Node res = null;

    res = testThisGroup(_parentTransform, userData);

    return res;

  }

  /**
   * pass through this group, see if contain the user data
   *
   * @param group    the group we're inspecting
   * @param userData the object we're looking for
   * @return the node containing this object, or null
   */
  private Node testThisGroup(Group group, Object userData)
  {
    Node res = null;

    if (group.getCapability(Group.ALLOW_CHILDREN_READ))
    {
      for (int i = 0; i < group.numChildren(); i++)
      {
        Node thisN = group.getChild(i);
        if (thisN.getUserData() == userData)
        {
          res = thisN;
          break;
        }
        // is it a group?
        if (thisN instanceof Group)
        {
          // does this group contain it?
          res = testThisGroup((Group) thisN, userData);

          if (res != null)
          {
            // yes, cool - drop out!
            break;
          }
        }
      }
    }
    else
    {
    }

    return res;
  }

  public void detachForRemoval()
  {
    detach();
  }

  public void andReattach(SimpleUniverse universe)
  {
    universe.addBranchGraph(this);
  }


  ///////////////////////////
  // listener support for the options
  ///////////////////////////
  public void addOptionListener(java.beans.PropertyChangeListener listener)
  {
    _options.addListener(listener);
  }

  public void removeOptionListener(java.beans.PropertyChangeListener listener)
  {
    _options.removeListener(listener);
  }

  ///////////////////////////
  // main class, used for testing
  ///////////////////////////
  public static void main(String[] args)
  {
    System.out.println("World: working!");

    // create our world
    JFrame frm = new JFrame("Test World");
    JPanel holder = new JPanel();
    holder.setLayout(new BorderLayout());
    frm.getContentPane().add(holder);
    frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    GraphicsDevice screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
    GraphicsConfiguration gc = screenDevice.getBestConfiguration(template);
    Canvas3D canvas3D = new Canvas3D(gc);
    holder.add("Center", canvas3D);

    // SimpleUniverse is a Convenience Utility class
    final SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

    // set the back clipping bit
    simpleU.getViewer().getView().setBackClipDistance(3000);

    World scene = new World(canvas3D, new WorldArea(new WorldLocation(2.2, 2.3, 0), new WorldLocation(1.2, 1.3, 0)), null);


    ////////////////////////////////////////////////
    // test projections
    ///////////////////////////////////////////////
    //    WorldLocation loc = new WorldLocation(44.33, 22.33, 12);
    //    Point3d pt = scene.toScreen(loc);
    //    System.out.println("loc:" + loc + " goes to pt:" + pt);
    //
    //    loc = scene.toWorld(pt);
    //    System.out.println("pt:" + pt + " goes to loc:" + loc);


    //  System.exit(0);

        addSampleData(scene._parentTransform);


    //    final Participant3D bPart = new Participant3D(scene.getWorldPlottingOptions(), scene)
    //    {
    //      protected void listenToHost() {
    //        // don't bother
    //      }
    //
    //      protected Collection getTrail(long start_time, long end_time) {
    //        return null;
    //      }
    //
    //      protected WorldLocation getLocation(long new_time) {
    //        return new WorldLocation(Math.random() * 12, Math.random() * 24, Math.random() * -14);
    //      }
    //
    //      protected Color getColor() {
    //        return java.awt.Color.yellow;
    //      }
    //    };
    //
    //    final Participant3D bPart3 = new Participant3D(scene.getWorldPlottingOptions(), scene)
    //    {
    //      protected void listenToHost() {
    //        // don't bother
    //      }
    //
    //      protected Collection getTrail(long start_time, long end_time) {
    //        return null;
    //      }
    //
    //      protected WorldLocation getLocation(long new_time) {
    //        return new WorldLocation(Math.random() * 12, Math.random() * 24, Math.random() * 14);
    //      }
    //
    //      protected Color getColor() {
    //        return java.awt.Color.cyan;
    //      }
    //    };

    //    scene._parentTransform.addChild(bPart);
    //    scene._parentTransform.addChild(bPart3);

    ////////////////////////////////////////
    // create our utility button
    /////////////////////////////////////////
    JPanel buttons = new JPanel();
    holder.add("South", buttons);
    buttons.setLayout(new GridLayout(1, 0));

    JButton stepper = new JButton("Step");
    stepper.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("stepping!");
        //        bPart.updated(12000);
        //        bPart3.updated(12000);
      }
    });
    buttons.add(stepper);

    JButton home = new JButton("Home");
    home.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        simpleU.getViewingPlatform().setNominalViewingTransform();

      }
    });
    buttons.add(home);


    //  scene.compile();

    // This will move the ViewPlatform back a bit
    simpleU.getViewingPlatform().setNominalViewingTransform();
    simpleU.addBranchGraph(scene);


    frm.setSize(600, 400);
    frm.setVisible(true);


  }


  public static void addSampleData(TransformGroup parent)
  {
    // add a second ColorCube object to the scene graph
    Transform3D transform = new Transform3D();
    transform.setTranslation(new Vector3f(0.6f, 0.0f, -0.6f));
    TransformGroup tGroup = new TransformGroup(transform);
    setTransformGroupCapabilities(tGroup);
    parent.addChild(tGroup);


    Sphere c2 = new Sphere(0.4f, Primitive.GEOMETRY_NOT_SHARED, null);
    setColor(c2, new Color3f(0.0f, 1.0f, 0.0f));
    c2.setCapability(Box.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    c2.setCapability(Box.ENABLE_GEOMETRY_PICKING);
    c2.setCapability(Box.ALLOW_BOUNDS_READ);
    c2.setCapability(Box.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    setShapeCapabilities(c2.getShape());
    tGroup.addChild(c2);

    // add a second ColorCube object to the scene graph
    transform = new Transform3D();
    transform.setTranslation(new Vector3f(-0.6f, 0.0f, 0.6f));
    tGroup = new TransformGroup(transform);
    setTransformGroupCapabilities(tGroup);
    parent.addChild(tGroup);

    Box cc = new Box(0.4f, 0.4f, 0.4f, Primitive.GEOMETRY_NOT_SHARED, null);
    setColor(cc, new Color3f(1.0f, 0.0f, 0.0f));
    cc.setCapability(ColorCube.ALLOW_GEOMETRY_READ);
    cc.setCapability(ColorCube.ALLOW_BOUNDS_READ);
    cc.setCapability(ColorCube.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    cc.setCapability(ColorCube.ALLOW_APPEARANCE_READ);
    cc.setCapability(ColorCube.ALLOW_APPEARANCE_WRITE);
    setShapeCapabilities(cc.getShape(Box.FRONT));
    setShapeCapabilities(cc.getShape(Box.BACK));
    setShapeCapabilities(cc.getShape(Box.LEFT));
    setShapeCapabilities(cc.getShape(Box.RIGHT));
    setShapeCapabilities(cc.getShape(Box.TOP));
    setShapeCapabilities(cc.getShape(Box.BOTTOM));
    tGroup.addChild(cc);

  }

  public WorldLocation toWorld(Point3d point)
  {
    WorldLocation loc = _projection.toWorld3D(point);
    return loc;
  }

  public Point3d toScreen(WorldLocation loc)
  {
    Point3d res = _projection.toScreen3D(loc);
    return res;
  }

  /**
   * create the sea surface
   */
  public Switch getSurface(WorldPlottingOptions options,
                           DoubleProjection projection)
  {
    // the switch to contain the ocean
    Switch griddedSurfaceSwitch = new Switch();
    griddedSurfaceSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
    griddedSurfaceSwitch.setWhichChild(Switch.CHILD_ALL);

    // create the geometry
    LineArray lines = WorldBathySupport.createGrid(options.getGridDelta(), projection, OCEAN_DIAMETER);

    // create the shape to hold the geometry
    _gridSurfaceShape = new Shape3D();
    _gridSurfaceShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    _gridSurfaceShape.setPickable(false);
    Appearance app = new Appearance();
    app.setColoringAttributes(new ColoringAttributes(new Color3f(0.4f, 0.4f, 0.4f), ColoringAttributes.FASTEST));
    LineAttributes la = new LineAttributes();
    la.setLineWidth(1f);
    app.setLineAttributes(la);
    _gridSurfaceShape.setAppearance(app);
    _gridSurfaceShape.setGeometry(lines);

    // add the shape itself
    griddedSurfaceSwitch.addChild(_gridSurfaceShape);

    return griddedSurfaceSwitch;
  }


}
