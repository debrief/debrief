package ASSET.Models.Decision.Movement;

import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 04-Mar-2004
 * Time: 21:02:00
 * To change this template use File | Settings | File Templates.
 */
public class RectangleWander extends Wander
{
  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * the area we wander around
   */
  WorldArea _ourArea;
  
  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * create a wander behaviour contained in a rectangle
   *
   * @param area
   * @param name
   */
  public RectangleWander(WorldArea area, String name)
  {
    super(name);
    this._ourArea = area;
    super.setOrigin(area.getCentre());
  }



  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////

  /**
   * function to determine if it's time to turn to the new course
   *
   * @param current the current status
   * @return
   */
  protected boolean isBeyondLimit(Status current)
  {
    return !_ourArea.contains(current.getLocation());
  }

  /**
   * ok, plot a new course because we're at the edge
   *
   * @param status       our current status
   * @param oldDemCourse the old demanded course
   * @param res          the demanded status object (to place our data into)
   * @return the new activity
   */
  protected String setNewCourse(Status status, Double oldDemCourse, SimpleDemandedStatus res)
  {
    String activity;

    //    if (true)
    //    {
    activity = super.setNewCourse(status, oldDemCourse, res);
    return activity;
    //    }
    //
    //    // do we have a demanded course?
    //    if (_demCourse != null)
    //    {
    //
    //      // is the user already on course?
    //      double courseError = Math.abs(_demCourse.doubleValue() - status.getCourse());
    //
    //      if (courseError > 180)
    //        courseError -= 360;
    //
    //      if (courseError < -180)
    //        courseError += 360;
    //
    //      // is the error acceptable?
    //      if (courseError < 5)
    //      {
    //        // yes, decide a new course
    //        _demCourse = null;
    //      }
    //    }
    //
    //
    //    if (_demCourse == null)
    //    {
    //      // determine the new course
    //      double demCourse = status.getCourse() + 180;
    //
    //      if (demCourse > 360)
    //        demCourse -= 360;
    //
    //      if (demCourse < -360)
    //        demCourse += 360;
    //
    //      _demCourse = new Double(demCourse);
    //    }
    //    else
    //    {
    //      _demCourse = new Double(status.getCourse());
    //    }
    //
    //    // and head for the new dem course
    //    res.setCourse(_demCourse.doubleValue());
  }


  //////////////////////////////////////////////////
  // accessors (largely to support editing)
  //////////////////////////////////////////////////

  public WorldLocation getTopLeft()
  {
    return _ourArea.getTopLeft();
  }

  public WorldLocation getBottomRight()
  {
    return _ourArea.getBottomRight();
  }

  public void setTopLeft(WorldLocation val)
  {
    _ourArea.setTopLeft(val);
    _ourArea.normalise();
  }

  public void setBottomRight(WorldLocation val)
  {
    _ourArea.setBottomRight(val);
    _ourArea.normalise();
  }

  //////////////////////////////////////////////////
  // editor support
  //////////////////////////////////////////////////

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public final boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new RectWanderInfo(this);

    return _myEditor;
  }

  static public final class RectWanderInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public RectWanderInfo(final Wander data)
    {
      super(data, data.getName(), "RectangleWander");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public final java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("TopLeft", "the top left corner of the area we wander around"),
          prop("BottomRight", "the bottom right corner of the area we wander around"),
          prop("Name", "the name of this wandering model"),
          prop("Speed", "the speed we wander at (kts)"),
          prop("Height", "the Height we wander at (m)"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  //////////////////////////////////////////////////
  // testing support
  //////////////////////////////////////////////////

  public static final class WanderTest extends SupportTesting.EditableTesting
  {
    public WanderTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
      final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
      final WorldArea theArea = new WorldArea(topLeft, bottomRight);
      final RectangleWander tw = new RectangleWander(theArea, "rect wander");
      return tw;
    }

    public final void testNoDemCourseSpeed()
    {
      final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
      final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
      final WorldArea theArea = new WorldArea(topLeft, bottomRight);
      final RectangleWander tw = new RectangleWander(theArea, "rect wander");

      int seed = (int) (RandomGenerator.nextRandom() * 1000d);
      System.out.println("seed:" + seed);

      final Status stat = new Status(1, 0);
      stat.setLocation(SupportTesting.createLocation(3000, 3000));
      stat.setCourse(RandomGenerator.nextRandom() * 180);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      final DemandedStatus dem = null;

      final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

      assertNotNull("dem returned", ds);
      //      assertEquals("still on old course", 22, ds.getCourse(), 0);
      //      assertEquals("still on old speed", MWC.Algorithms.Conversions.Kts2Mps(12), ds.getSpeed(), 0);

      Surface fisher = new Surface(23);
      fisher.setName("Fisher");
      fisher.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
      fisher.setStatus(stat);
      fisher.setDecisionModel(tw);
      fisher.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

      CoreScenario cs = new CoreScenario();
      cs.setScenarioStepTime(10000);
      cs.addParticipant(fisher.getId(), fisher);

      //      DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp", "rect_wander_search.rep", false);
      //      TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300, "rect_wander_search.png", null, false, true);
      //      dro.setup(cs);
      //      tpo.setup(cs);
      //
      //      dro.outputThisArea(theArea);

      // now run through to completion
      while (cs.getTime() < 8000000)
      {
        cs.step();
      }

      //      dro.tearDown(cs);
      //      tpo.tearDown(cs);


    }

    public final void testWithDemCourseSpeed()
    {
      final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
      final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
      final WorldArea theArea = new WorldArea(topLeft, bottomRight);
      final RectangleWander tw = new RectangleWander(theArea, "rect wander");
      tw.setSpeed(new WorldSpeed(44, WorldSpeed.M_sec));
      tw.setHeight(new WorldDistance(45, WorldDistance.METRES));


      final Status stat = new Status(1, 0);
      stat.setLocation(theArea.getCentre().add(new WorldVector(100, WorldDistance.METRES, 0)));
      stat.setCourse(22);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

      final DemandedStatus dem = null;

      final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

      assertNotNull("dem returned", ds);
      assertEquals("new demanded Height", 45, ds.getHeight(), 0);
      assertEquals("new demanded speed", 44, ds.getSpeed(), 0);
    }
  }

  /**
   * accessor function to return the area we are going to search
   *
   * @return our area.
   */
  public WorldArea getArea()
  {
    return _ourArea;
  }

}
