/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 12, 2002
 * Time: 2:29:01 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.GUI.Tools.Plot3D;

import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.ScenarioType;
import MWC.GUI.Java3d.ModelFactory;
import MWC.GUI.Java3d.Tactical.Participant3D;
import MWC.GUI.Java3d.World;
import MWC.GUI.Java3d.WorldPlottingOptions;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class ASSETParticipant3D extends Participant3D implements ScenarioSteppedListener,
  ParticipantMovedListener
{
  ///////////////////////////
  // member variables
  ///////////////////////////
  /**
   * the participant we are displaying
   */
  private ParticipantType _myParticipant = null;

  /**
   * the scenario we are listening to
   */
  private ScenarioType _myScenario = null;


  ///////////////////////////
  // constructor
  ///////////////////////////

  public ASSETParticipant3D(final WorldPlottingOptions options,
                            final World world,
                            final ParticipantType myParticipant,
                            final ScenarioType myScenario)
  {
    super(options, world);

    _myParticipant = myParticipant;
    _myScenario = myScenario;

    build();

    // also store the trak
    this.setUserData(myParticipant);

    // try to give ourselves some initial data
    if (myScenario != null)
    {
      final long tNow = myScenario.getTime();
      updated(new HiResDate(tNow));
    }

  }

  ///////////////////////////
  // member methods
  ///////////////////////////



  // setup the listeners
  protected void listenToHost()
  {
    // is it a real stepper?
    if (_myScenario != null)
      _myScenario.addScenarioSteppedListener(this);

    //    if(_myParticipant != null)
    //    {
    //      _myParticipant.addPropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
    //    }

    // listen to participant movement
    if (_myParticipant != null)
    {
      _myParticipant.addParticipantMovedListener(this);
    }

  }

  /**
   * stop listening to the stepper
   */
  public void doClose()
  {
    super.doClose();
    if (_myScenario != null)
      _myScenario.removeScenarioSteppedListener(this);

    if (_myParticipant != null)
    {
      _myParticipant.removeParticipantMovedListener(this);
    }

    //    if(_myParticipant != null)
    //      _myParticipant.removePropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
  }

  private Collection getTrail(final HiResDate start_time, final HiResDate end_time)
  {
    final Vector res = new Vector(0, 1);
    final WorldLocation newS = _myParticipant.getStatus().getLocation();

    // pass through our list of positions, retrieving all between start and end times
    final Iterator it = _myPositions.iterator();
    while (it.hasNext())
    {
      final Status thisS = (Status) it.next();
      if ((thisS.getTime() >= start_time.getDate().getTime()) && (thisS.getTime() <= end_time.getDate().getTime()))
      {
        res.add(thisS.getLocation());
      }
    }

    //    // create a fake, earlier location
    //    WorldLocation oldS = newS.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(0),
    //                                                  MWC.Algorithms.Conversions.Nm2Degs(10), 0));
    //
    //    res.add(oldS);
    //    res.add(newS);
    return res;
    //    return _myParticipant.getItemsBetween(start_time, end_time);
  }

  protected WorldLocation getLocation(HiResDate new_time)
  {
    WorldLocation res = null;
    res = _myParticipant.getStatus().getLocation();

    return res;
  }

  /**
   * get the current status for this track
   */
  protected String getStatusText(HiResDate new_time)
  {
    String res = "n/a";

    res = _myParticipant.getActivity();

    return res;
  }

  /**
   * get the name
   */
  public String getName()
  {
    return _myParticipant.getName();
  }

  /**
   * get the course
   */
  protected double getCourse(HiResDate new_time)
  {
    double res = -999;
    res = _myParticipant.getStatus().getCourse();

    // trim the res
    if ((res > -999) && (res < 0))
    {
      res += 360;
    }

    // convert to rads
    res = MWC.Algorithms.Conversions.Degs2Rads(res);

    return res;
  }

  /**
   * create the model, by over-writing the generic class
   */
  protected Node createModel(final boolean low_res)
  {
    // find out the track type
    final String theType = _myParticipant.getCategory().getType().toLowerCase();

    // create the model
    final Node res = ModelFactory.createThis(theType, low_res);

    return res;
  }

  public void propertyChange(final PropertyChangeEvent evt)
  {
    // see if it's our track which has changed
    if (evt.getSource() == _myParticipant)
    {
      if (evt.getPropertyName().equals(PlainWrapper.COLOR_CHANGED))
      {
        // update the colour of the track items
        updateColor();
      }
    }
    else
    {
      // no, it must just be the world options
      super.propertyChange(evt);
    }
  }

  protected Color getColor()
  {
    Color res = Color.green;

    if (_myParticipant.getCategory().getForce().equals(Category.Force.RED))
      res = Color.red;
    else if (_myParticipant.getCategory().getForce().equals(Category.Force.BLUE))
      res = Color.blue;


    return res;
  }

  protected void buildSnailTrail(final HiResDate start_time, final HiResDate end_time)
  {

    // find the back-trail
    final Collection trail = getTrail(start_time, end_time);

    // draw the geometry
    if (trail != null)
    {
      // build up the new geometry
      final int len = trail.size();

      LineArray line = null;

      if (len <= 1)
      {
        // zero trail, don't bother!
      }
      else
      {

        line = new LineArray((len - 1) * 2, LineArray.COORDINATES);
        line.setCapability(LineArray.ALLOW_COORDINATE_WRITE);

        final java.util.Iterator itar = trail.iterator();
        int index = 0;

        Point3d lastPoint = null;

        while (itar.hasNext())
        {
          final WorldLocation loc = (WorldLocation) itar.next();
          // produce an offset

          final WorldLocation curLoc = getLocation(end_time);

          final WorldVector vec = loc.subtract(curLoc);

          final WorldLocation offset = _myWorld._projection.getDataArea().getCentre().add(vec);

          final Point3d thisP = _myWorld.toScreen(offset);

          if (lastPoint != null)
          {
            line.setCoordinate(index++, lastPoint);
            line.setCoordinate(index++, thisP);
          }
          else
            lastPoint = new Point3d();

          lastPoint.set(thisP);
        }
      }

      _snailTrail.setGeometry(line);
    }

  }

  ///////////////////////////
  // stepper support
  ///////////////////////////

  /**
   * the scenario has stepped forward
   */
  public void step(final long newTime)
  {
    super.updated(new HiResDate(newTime));
  }

  /**
   * the scenario has restarted, reset
   */
  public void restart()
  {
    // ignroe this
  }


  /**
   * *************************************************
   * participant movement listener
   * *************************************************
   */

  private final int pos_size = 50;
  private Vector _myPositions = new Vector(pos_size);


  /**
   * this participant has moved
   */
  public void moved(final Status newStatus)
  {
    // is the list full yet?
    if (_myPositions.size() < pos_size)
    {
      // just add it
    }
    else
    {
      // trim one off
      _myPositions.remove(_myPositions.lastElement());
    }

    // now insert it
    _myPositions.insertElementAt(new Status(newStatus), 0);
  }
}

