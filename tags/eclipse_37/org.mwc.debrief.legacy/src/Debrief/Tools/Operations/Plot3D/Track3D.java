/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 12, 2002
 * Time: 2:29:01 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package Debrief.Tools.Operations.Plot3D;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.Collection;

import javax.media.j3d.LineArray;
import javax.media.j3d.Node;
import javax.vecmath.Point3d;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.StepperListener;
import MWC.GUI.Java3d.ModelFactory;
import MWC.GUI.Java3d.World;
import MWC.GUI.Java3d.WorldPlottingOptions;
import MWC.GUI.Java3d.Tactical.Participant3D;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;

public final class Track3D extends Participant3D implements StepperListener {
  ///////////////////////////
  // member variables
  ///////////////////////////
  /** the track we are displaying
   *
   */
  private TrackWrapper _myTrack = null;

  /** the step control we listen to
   *
   */
  private final StepperController _myStepper;

  ///////////////////////////
  // constructor
  ///////////////////////////

  public Track3D(WorldPlottingOptions options,
                 World world,
                 TrackWrapper myTrack,
                 StepperController stepper) {
    super(options, world);

    _myTrack = myTrack;
    _myStepper = stepper;

    build();

    // also store the trak
    this.setUserData(myTrack);

    // set our visibility
    super.updateVisibility(myTrack.getVisible());

    // try to give ourselves some initial data
    if(_myStepper != null)
    {
      HiResDate tNow = _myStepper.getCurrentTime();
      updated(tNow);
    }

  }

  ///////////////////////////
  // member methods
  ///////////////////////////



  // setup the listeners
  protected final void listenToHost() {
    // is it a real stepper?
    if(_myStepper != null)
      _myStepper.addStepperListener(this);

    if(_myTrack != null)
    {
      _myTrack.addPropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
      _myTrack.addPropertyChangeListener(PlainWrapper.VISIBILITY_CHANGED, this);
      _myTrack.addPropertyChangeListener(WatchableList.FILTERED_PROPERTY, this);
    }
  }

  /** stop listening to the stepper
   *
   */
  public final void doClose() {
    super.doClose();
    if(_myStepper != null)
      _myStepper.removeStepperListener(this);

    if(_myTrack != null)
    {
      _myTrack.removePropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
      _myTrack.removePropertyChangeListener(PlainWrapper.VISIBILITY_CHANGED, this);
      _myTrack.removePropertyChangeListener(WatchableList.FILTERED_PROPERTY, this);
    }
  }

  private Collection<Editable> getTrail(HiResDate start_time, HiResDate end_time) {
  	Collection<Editable> res = _myTrack.getUnfilteredItems(start_time, end_time);
    return res;
  }

  protected final WorldLocation getLocation(HiResDate new_time) {
    WorldLocation res = null;
    Watchable[] nearest = _myTrack.getNearestTo(new_time);
    if(nearest != null)
      if(nearest.length > 0)
      {
        if(nearest[0] != null)
          res = nearest[0].getLocation();
      }

    return res;
  }

  /** get the current status for this track
   *
   */
  protected final String getStatusText(HiResDate new_time)
  {
    String res = "n/a";

    Watchable[] currentStat = _myTrack.getNearestTo(new_time);
    if(currentStat != null)
    {
      if(currentStat.length > 0)
      {
        Watchable thisOne = currentStat[0];
        double course = thisOne.getCourse();
        res = " " + (int)Math.toDegrees(course) + "\u00b0 " + (int)(thisOne.getSpeed()) + " kts";
      }
    }

    return res;
  }

  /** get the name
   *
   */
  public final String getName()
  {
    return _myTrack.getName();
  }

  /** get the course
   *
   */
  protected final double getCourse(HiResDate new_time) {
    double res = -999;
    Watchable[] nearest = _myTrack.getNearestTo(new_time);
    if(nearest != null)
      if(nearest.length > 0)
      {
        if(nearest[0] != null)
          res = nearest[0].getCourse();
      }

    // trim the res
    if((res > -999) && (res < 0))
    {
      res += Math.PI * 2;
    }

    return res;
  }


  /** create the model, by over-writing the generic class
   *
   */
  protected final Node createModel(boolean low_res)
  {
    // find out the track type
    String theType = _myTrack.getSymbolType();

    // create the model
    Node res = ModelFactory.createThis(theType, low_res);

    return res;
  }

  public final void propertyChange(PropertyChangeEvent evt)
  {
    // see if it's our track which has changed
    if(evt.getSource() == _myTrack)
    {
      if(evt.getPropertyName().equals(PlainWrapper.COLOR_CHANGED))
      {
        // update the colour of the track items
        updateColor();
      }
      // or is it that the track's no longer visible
      if(evt.getPropertyName().equals(PlainWrapper.VISIBILITY_CHANGED))
      {
        Boolean isVis = (Boolean)evt.getNewValue();
        super.updateVisibility(isVis.booleanValue());
      }

      // or is it that the track's no longer visible
      if(evt.getPropertyName().equals(WatchableList.FILTERED_PROPERTY))
      {
        super.updated(super._lastTime);
      }

    }
    else
    {
      // no, it must just be the world options
      super.propertyChange(evt);
    }
  }

  protected final Color getColor() {
    return _myTrack.getColor();
  }

  protected final void buildSnailTrail(HiResDate start_time, HiResDate end_time) {

    // find the back-trail
  	Collection<Editable> trail = getTrail(start_time, end_time);

    // draw the geometry
    if(trail != null)
    {
      // build up the new geometry
      int len = trail.size();

      LineArray line = null;

      if(len <= 1)
      {
        // zero trail, don't bother!
      }
      else
      {

        line = new LineArray((len-1) * 2, LineArray.COORDINATES);
        line.setCapability(LineArray.ALLOW_COORDINATE_WRITE);

        java.util.Iterator<Editable> itar = trail.iterator();
        int index = 0;

        Point3d lastPoint = null;

        // get the origin in screen coords
        Point3d originP = new Point3d(_myWorld.toScreen(getLocation(end_time)));

        // step through the trail
        while(itar.hasNext())
        {
          Watchable wl = (Watchable)itar.next();
          WorldLocation loc = wl.getLocation();

          // produce an offset
          Point3d currP = new Point3d(_myWorld.toScreen(loc));

          // what's the delta
          currP.sub(originP);

          // is this the first pass?
          if(lastPoint != null)
          {
            line.setCoordinate(index++, lastPoint);
            line.setCoordinate(index++, currP);
          }
          else
            lastPoint = new Point3d();

          // remember the last point
          lastPoint.set(currP);
        }
      }

      _snailTrail.setGeometry(line);
    }

  }

  ///////////////////////////
  // stepper support
  ///////////////////////////
  public final void steppingModeChanged(boolean on) {
    // ignore this
  }

  public final void newTime(HiResDate oldDTG, HiResDate newDTG, CanvasType canvas) {
    super.updated(newDTG);
  }
}
