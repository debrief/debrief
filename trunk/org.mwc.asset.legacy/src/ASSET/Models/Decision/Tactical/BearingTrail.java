package ASSET.Models.Decision.Tactical;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class BearingTrail extends Trail implements java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // Member Variables
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * the bearing to trail at (degrees)
   */
  private double _trailBearing;

  //////////////////////////////////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////////////////////////////////

  public BearingTrail(final WorldDistance trailRange)
  {
    super(trailRange);

    super.setName("Bearing Trail");
  }
  //////////////////////////////////////////////////////////////////////
  // Member methods
  //////////////////////////////////////////////////////////////////////

  /**
   * get the course and speed we need to get back on track - in this case,
   * produce a demanded status which will make us trail the target
   * at an indicated range from him, at an indicated ATB
   */
  protected DemandedStatus getDemanded(long time, final ASSET.Participants.Status status,
                                       final ASSET.Models.Detection.DetectionEvent detection)
  {
    SimpleDemandedStatus res = new SimpleDemandedStatus(time, status);

    final Float Brg = detection.getBearing();
    // check we have a value
    if (Brg != null)
    {
      // get the bearing to the target
      final double brg = this.courseToTarget(status.getId(), status.getLocation(), detection);

      // now work out the range to the target
      final WorldDistance Rng = detection.getRange();
      if (Rng != null)
      {

        // find the range to the target
        WorldDistance rngToTarget = rangeToTarget(status.getId(), status.getLocation(), detection);

        // store this range
        final double rng_degs = rngToTarget.getValueIn(WorldDistance.DEGS);

        //  work out where we should be
        final Float Crse = detection.getCourse();

        // check we have his course
        if (Crse != null)
        {
          // what's the vector to the target
          final MWC.GenericData.WorldVector toHim = new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg),
                                                                                    rng_degs,
                                                                                    0);



          // so what's the target location?
          final MWC.GenericData.WorldLocation hisLoc = detection.getSensorLocation().add(toHim);

          // what's his course
          double crse = Crse.doubleValue();

          // so what bearing do we want to be on, relative to it
          crse += _trailBearing;

          double rngDegs = super.getTrailRange().getValueIn(WorldDistance.DEGS);

          // so we've got our course.  work out where we should be relative to it
          final MWC.GenericData.WorldVector vec = new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(crse),
                                                                                  rngDegs,
                                                                                  0);

          // work out where want to be
          final MWC.GenericData.WorldLocation ourDestination = hisLoc.add(vec);

          // what's our bearing to this
          final MWC.GenericData.WorldVector demandedVec = ourDestination.subtract(status.getLocation());

          // work out the change in speed we should make
          final double spdChange = Math.max(0.4, res.getSpeed() * 0.05);

          // are we very far from our required location?
          final double distError = MWC.Algorithms.Conversions.Degs2Yds(demandedVec.getRange());

          // is this outside our allowable error
          if (distError > _allowable_error.getValueIn(WorldDistance.YARDS))
          {
            // we are further away than allowed, so speed up
            res.setSpeed(res.getSpeed() + spdChange);
          }
          else
          {
            // within the envelope, so slow down

            // do we know his speed?
            final Float Spd = detection.getSpeed();

            if (Spd != null)
            {
              // copy his speed
              res.setSpeed(Spd.doubleValue());
            }
            else
            {
              // just slow down a little
              res.setSpeed(res.getSpeed() - spdChange);
            }

          }

          // make a decision on our course
          res.setCourse(MWC.Algorithms.Conversions.Rads2Degs(demandedVec.getBearing()));
        } // if we know the course
      } // if we know the range
    }

    return res;
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    //
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new BearingTrailInfo(this);

    return _myEditor;
  }

  public double getTrailBearing()
  {
    return _trailBearing;
  }

  public void setTrailBearing(final double val)
  {
    _trailBearing = val;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: BearingTrail.java,v $
   * Revision 1.1  2006/08/08 14:21:31  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:40  Ian.Mayo
   * First versions
   *
   * Revision 1.12  2004/08/31 09:36:16  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   *
   * Revision 1.11  2004/08/26 16:26:58  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.10  2004/08/26 14:09:49  Ian.Mayo
   * Start switching to automated property editor testing.  Correct property editor bugs where they arise.
   * <p/>
   * Revision 1.9  2004/08/25 11:20:29  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.8  2004/08/20 13:32:24  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.7  2004/08/09 15:50:28  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.6  2004/05/24 15:57:03  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.5  2004/02/18 08:48:08  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.3  2003/11/05 09:19:51  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class BearingTrailInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public BearingTrailInfo(final BearingTrail data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("TargetType", "the type of vessel we are trailing"),
          prop("TrailBearing", "the bearing (ATB) at which we trail"),
          prop("TrailRange", "the range at which we trail"),
          prop("AllowableError", "the envelope allowed around the trail range"),
          prop("Name", "the name of this bearing trail model"),
        };
        //        res[0].setPropertyEditorClass(ASSET.GUI.Editors.TargetTypeEditor.class);
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class BearingTrailTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public BearingTrailTest(final String val)
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
      final BearingTrail bt = new BearingTrail(new WorldDistance(1200, WorldDistance.YARDS));
      return bt;
    }

    public void testBearingTrail()
    {

      final double rngToHim = 10000;
      final double crseToHim = 45;
      final double brgToHim = 110;

      //      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(0,0,0);
      //      final MWC.GenericData.WorldLocation hisLoc = origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(crseToHim),
      //                                                                                        MWC.Algorithms.Conversions.Yds2Degs(rngToHim),
      //                                                                                        0));

      // setup a couple of targets
      final ASSET.Models.Vessels.Surface ssn = new ASSET.Models.Vessels.Surface(1);
      ssn.setName("ssn");
      ssn.setCategory(new ASSET.Participants.Category(Category.Force.RED, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

      final ASSET.Participants.CoreParticipant target = new ASSET.Models.Vessels.Surface(1);
      target.setName("some target");

      final ASSET.Models.Sensor.Initial.BroadbandSensor bb = new ASSET.Models.Sensor.Initial.BroadbandSensor(12);
      final ASSET.Models.Detection.DetectionEvent de =
        new ASSET.Models.Detection.DetectionEvent(100l, ssn.getId(), new WorldLocation(0, 0, 0), bb,
                                                  new WorldDistance(rngToHim, WorldDistance.YARDS),
                                                  new WorldDistance(rngToHim, WorldDistance.YARDS),
                                                  new Float(brgToHim), new Float(crseToHim), new Float(10),
                                                  ssn.getCategory(), null, new Float(350), target);


      final ASSET.Models.Detection.DetectionList dl = new ASSET.Models.Detection.DetectionList();
      dl.add(de);

      final TargetType tt = new TargetType(ASSET.Participants.Category.Type.SUBMARINE);

      final BearingTrail bt = new BearingTrail(new WorldDistance(1200, WorldDistance.YARDS));
      bt.setTrailBearing(145.0);
      bt.setTrailRange(new WorldDistance(8000, WorldDistance.YARDS));
      bt.setTargetType(tt);

      final ASSET.Participants.Status status = new ASSET.Participants.Status(0, 0);
      status.setCourse(100);
      status.setLocation(new MWC.GenericData.WorldLocation(0, 0, 0));
      status.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

      ASSET.Participants.DemandedStatus dec = bt.decide(status, null, null, dl, null, 100);

      // now try another trail location
      bt.setTrailBearing(335.0);
      bt.setTrailRange(new WorldDistance(8000, WorldDistance.YARDS));

      dec = bt.decide(status, null, null, dl, null, 100);

      // now try another trail location
      bt.setTrailBearing(225.0);
      bt.setTrailRange(new WorldDistance(11000, WorldDistance.YARDS));

      dec = bt.decide(status, null, null, dl, null, 100);

      assertNotNull("I'm alive", dec);

      //    @@@ testing this, produce results on paper first!
      // produce genuinely flat model, & allow us to assign it to WorldLocation
    }
  }

}