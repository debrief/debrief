package Debrief.Wrappers.Track;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Enumeration;

import junit.framework.TestCase;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.PlainWrapper;
import MWC.GUI.ToolParent;
import MWC.GenericData.ColoredWatchable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class DynamicInfillSegment extends TrackSegment implements
    ColoredWatchable
{

  public static final String RANDOM_INFILL = "RANDOM_INFILL";

  public static final String DARKER_INFILL = "DARKER_INFILL";

  public static final String GREEN_INFILL = "GREEN_INFILL";

  public static final String INFILL_COLOR_STRATEGY = "INFILL_COLOR_STRATEGY";

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  /**
   * get the first 'n' elements from this segment
   * 
   * @param trackOne
   *          the segment to get the data from
   * @param oneUse
   *          how many elements to use
   * @return the subset
   */
  public static FixWrapper[] getFirstElementsFrom(final TrackSegment trackTwo,
      final int num)
  {
    final FixWrapper[] res = new FixWrapper[num];

    HiResDate lastDate = null;
    int ctr = 0;

    final Enumeration<Editable> items = trackTwo.elements();
    for (int i = 0; i < trackTwo.size(); i++)
    {
      final FixWrapper next = (FixWrapper) items.nextElement();
      final HiResDate thisDate = next.getDateTimeGroup();
      if (lastDate != null)
      {
        // ok, is this a new date
        if (thisDate.equals(lastDate))
        {
          // skip this cycle
          continue;
        }
      }
      lastDate = thisDate;

      res[ctr++] = next;

      if (ctr == num)
        break;
    }

    return res;
  }

  /**
   * get the last 'n' elements from this segment
   * 
   * @param trackOne
   *          the segment to get the data from
   * @param num
   *          how many elements to use
   * @return the subset
   */
  public static FixWrapper[] getLastElementsFrom(final TrackSegment trackOne,
      final int num)
  {
    final FixWrapper[] res = new FixWrapper[num];

    // right, careful here, we can't use points at the same time
    final Object[] data = trackOne.getData().toArray();
    final int theLen = data.length;
    HiResDate lastDate = null;
    int ctr = 0;
    for (int i = theLen - 1; i >= 0; i--)
    {
      final FixWrapper fix = (FixWrapper) data[i];
      if (lastDate != null)
      {
        // is this the same date?
        final HiResDate thisDate = fix.getDateTimeGroup();
        if (thisDate.equals(lastDate))
        {
          // ok, can't use it - skip to next cycle
          continue;
        }
      }

      lastDate = fix.getDateTimeGroup();

      // ok, we must be ok
      res[res.length - ++ctr] = fix;

      // are we done?
      if (ctr == num)
        break;

    }

    return res;
  }

  /**
   * the segment that appears immediately before us
   * 
   */
  private TrackSegment _before;

  /**
   * the segment that appears immediately after us
   * 
   */
  private TrackSegment _after;

  /**
   * our internal class that listens to tracks moving
   * 
   */
  private transient PropertyChangeListener _moveListener;

  /**
   * our internal class that listens to tracks moving
   * 
   */
  private transient PropertyChangeListener _wrapperListener;

  /**
   * a utility logger
   * 
   */
  private transient ErrorLogger _myParent;

  private final Color _myColor;

  /**
   * for XML restore, we only have the name of the previous track. Store it.
   */
  private final String _beforeName;

  /**
   * for XML restore, we only have the name of the following track. Store it.
   */
  private final String _afterName;

  /**
   * restore from file, where we only know the names of the legs
   * 
   * @param beforeName
   * @param afterName
   */
  public DynamicInfillSegment(final String beforeName, final String afterName,
      final Color trackColor)
  {
    // we have to be in absolute mode, due to the way we use spline positions
    super(TrackSegment.ABSOLUTE);

    _beforeName = beforeName;
    _afterName = afterName;
    _myColor = trackColor;

    checkListeners();

    _myParent = Trace.getParent();
  }

  private void checkListeners()
  {
    if (_moveListener == null)
    {
      _moveListener = new PropertyChangeListener()
      {
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          // note, we used to call "recalculate" here. But, sometimes our
          // precedent
          // data doesn't know it's new location until after a paint event.
          // so, we'll defer generating the points until they're required
          reconstruct();
        }
      };
    }
    ;

    if (_wrapperListener == null)
    {
      _wrapperListener = new PropertyChangeListener()
      {
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          wrapperChange();
        }
      };
    }
    ;
  }

  /**
   * create an infill track segment between the two supplied tracks
   * 
   * @param before
   * @param after
   */
  public DynamicInfillSegment(final TrackSegment before,
      final TrackSegment after)
  {
    this(before.getName(), after.getName(),
        getColorStrategy(((FixWrapper) before.last()).getColor().darker()
            .darker()));

    // ok, and start listening
    configure(before, after);
  }

  public void clear()
  {
    stopWatching(_before);
    stopWatching(_after);

    _before = null;
    _after = null;
  }

  /**
   * listen to the specified tracks
   * 
   * @param before
   * @param after
   */
  private void configure(final TrackSegment before, final TrackSegment after)
  {
    // ok, remember the tracks
    _before = before;
    _after = after;

    // also, we need to listen out for changes in these tracks
    startWatching(_before);
    startWatching(_after);

    // ok, produce an initial version
    reconstruct();
  }

  @Override
  protected void finalize() throws Throwable
  {
    super.finalize();

    // ditch the listeners
    clear();

  }

  /**
   * find the named segment
   * 
   * @param name
   *          name of the segment to find
   * @return the matching segment
   */
  private TrackSegment findSegment(final String name)
  {
    TrackSegment res = null;
    final SegmentList segs = super.getWrapper().getSegments();
    final Enumeration<Editable> numer = segs.elements();
    while (numer.hasMoreElements())
    {
      final Editable editable = numer.nextElement();
      if (editable.getName().equals(name))
      {
        res = (TrackSegment) editable;
        break;
      }
    }

    if (res == null)
    {
      Application.logError2(ErrorLogger.ERROR,
          "Unable to find host segnent named:" + name, null);
    }

    return res;
  }

  /**
   * accessor, used for file storage
   * 
   * @return
   */
  public String getAfterName()
  {
    // use the actual segment name, if we know it
    final String res = _after != null ? _after.getName() : _afterName;

    return res;
  }

  /**
   * accessor, used for file storage
   * 
   * @return
   */
  public String getBeforeName()
  {
    // use the actual segment name, if we know it
    final String res = _before != null ? _before.getName() : _beforeName;

    return res;
  }

  @Override
  public String getName()
  {
    String res = super.getName();

    if (this.size() == 0)
    {
      res += " [Recalculating]";
    }

    return res;
  }

  /**
   * we're overriding this method, since it's part of the rendering cycle, and we're confident that
   * rendering will only happen once the data is loaded and collated.
   */
  @Override
  public boolean getVisible()
  {
    checkListeners();

    // do we still have a parent? if not, we've prob been deleted
    if(this.getWrapper() == null)
    {
      return false;
    }
    
    // ok, do we know our tracks?
    if (_before == null)
    {
      final TrackSegment before = findSegment(_beforeName);
      final TrackSegment after = findSegment(_afterName);

      if ((before != null) && (after != null))
      {
        // ok, now we're ready
        configure(before, after);
      }
      else
      {
        // clear the listeners, just in case
        if (_before != null)
        {
          _before.removePropertyChangeListener(CoreTMASegment.ADJUSTED,
              _moveListener);
        }
        if (_after != null)
        {
          _after.removePropertyChangeListener(CoreTMASegment.ADJUSTED,
              _moveListener);
        }

        // we'd better hide ourselves too
        setVisible(false);
      }
    }
    else
    {
      // just double-check if we still need to be generated
      if (this.size() == 0)
      {
        // ok, tell the track to sort out the relative tracks
        // this will trigger our own reconstruct
        this.getWrapper().sortOutRelativePositions();
      }
    }

    return super.getVisible();
  }

  public void sortOutDateLabel(final HiResDate startDTG)
  {
    // skip - we'll use the infill name
  }

  /**
   * recalculate our set of positions
   * 
   */
  public void reconstruct()
  {
    // check we know our data
    if (_before == null || _after == null)
      return;

    // now the num to use
    final int oneUse = Math.min(2, _before.size());
    final int twoUse = Math.min(3, _after.size());

    // generate the data for the splines
    final FixWrapper[] oneElements = getLastElementsFrom(_before, oneUse);
    final FixWrapper[] twoElements = getFirstElementsFrom(_after, twoUse);
    final FixWrapper[] allElements = new FixWrapper[oneUse + twoUse];
    System.arraycopy(oneElements, 0, allElements, 0, oneUse);
    System.arraycopy(twoElements, 0, allElements, oneUse, twoUse);

    // ok, clear ourselves out
    this.removeAllElements();

    // generate the location spline
    final double[] times = new double[allElements.length];
    final double[] lats = new double[allElements.length];
    final double[] longs = new double[allElements.length];
    final double[] depths = new double[allElements.length];
    for (int i = 0; i < allElements.length; i++)
    {
      final FixWrapper fw = allElements[i];
      times[i] = fw.getDTG().getDate().getTime();

      // we have an occasional problem where changing details of hte
      // before/after tracks results in us updating mid-way through their
      // update.
      // (since the RelativeTMA segment positions actually get generated in the
      // paint cycle).
      if (fw.getLocation() == null)
      {
        return;
      }
      lats[i] = fw.getLocation().getLat();
      longs[i] = fw.getLocation().getLong();
      depths[i] = fw.getLocation().getDepth();
    }

    final UnivariateInterpolator interpolator = new SplineInterpolator();
    final UnivariateFunction latInterp = interpolator.interpolate(times, lats);
    final UnivariateFunction longInterp =
        interpolator.interpolate(times, longs);
    final UnivariateFunction depthInterp =
        interpolator.interpolate(times, depths);

    // what's the interval?
    long tDelta = meanIntervalFor(_before);

    // just check it's achievable
    if (tDelta == 0)
      throw new RuntimeException(
          "cannot generate infill when calculated step time is zero");

    // also give the time delta a minimum step size (10 secs), we were getting
    // really
    // screwy infills generated with tiny time deltas
    tDelta = Math.max(tDelta, 10000);

    // sort out the start & end times of the infill segment
    final long tStart = _before.endDTG().getDate().getTime() + tDelta;
    final long tEnd = _after.startDTG().getDate().getTime();

    // remember the last point on the first track, in case we're generating
    // a
    // relative solution
    FixWrapper origin = oneElements[oneElements.length - 1];

    // remember how the previous track is styled
    final String labelFormat = origin.getLabelFormat();
    final boolean labelOn = origin.getLabelShowing();
    final Integer labelLoc = origin.getLabelLocation();

    if (_myParent != null)
    {
      if (origin.getLocation() == null)
        _myParent.logError(ToolParent.INFO,
            "origin element has empty location", null);
    }

    // get going then! Note, we go past the end of the required data,
    // - so that we can generate the correct course and speed for the last
    // DR entry
    // Note: we drop out at least tDelta before the end. Having a really small
    // last point sends the maths screwy.
    for (long tNow = tStart; tNow <= tEnd - tDelta; tNow += tDelta)
    {
      // sort out the location & details for this infill location
      final double thisLat;
      final double thisLong;
      final double thisDepth;
      final double nextTime;
      
      if(tNow + tDelta < tEnd)
      {
        // ok, use an interpolated value
        thisLat = latInterp.value(tNow);
        thisLong = longInterp.value(tNow);
        thisDepth = depthInterp.value(tNow);
        nextTime = tNow;
      }
      else
      {
        // special case, if we're the last fix
        // then work out the course speed to the first point on the 
        // after segment, not the interpolated value
        thisLat = twoElements[0].getFixLocation().getLat();
        thisLong= twoElements[0].getFixLocation().getLong();
        thisDepth = twoElements[0].getFixLocation().getDepth();
        nextTime = twoElements[0].getDTG().getDate().getTime();
      }
      
      // create the new location
      final WorldLocation newLocation =
          new WorldLocation(thisLat, thisLong, thisDepth);

      // how far have we travelled since the last location?
      final WorldVector offset = newLocation.subtract(origin.getLocation());

      // how long since the last position?
      final double timeSecs =
          (nextTime - origin.getTime().getDate().getTime()) / 1000;

      // start off with the course
      double thisCourseRads = offset.getBearing();

      // and now the speed
      final double distYds =
          new WorldDistance(offset.getRange(), WorldDistance.DEGS)
              .getValueIn(WorldDistance.YARDS);
      final double spdYps = distYds / timeSecs;
      final double thisSpeedKts = MWC.Algorithms.Conversions.Yps2Kts(spdYps);

      // put course in the +ve domain
      while (thisCourseRads < 0)
        thisCourseRads += Math.PI * 2;

      // convert the speed
      final WorldSpeed theSpeed = new WorldSpeed(thisSpeedKts, WorldSpeed.Kts);
      final double speedYps = theSpeed
          .getValueIn(WorldSpeed.ft_sec) / 3;
      // create the fix
      final Fix newFix =
          new Fix(new HiResDate(tNow), newLocation, thisCourseRads, speedYps);

      final FixWrapper fw = new FixWrapper(newFix);
      fw.setSymbolShowing(true);

      fw.setColor(_myColor);

      // and sort out the time lable
      fw.resetName();

      // and copy the other formatting
      fw.setLabelFormat(labelFormat);
      fw.setLabelLocation(labelLoc);
      fw.setLabelShowing(labelOn);

      // only add it if we're still in the time period. We generate one
      // position
      // past the end of the time period in order to set the correct DR
      // course
      // for the last position.
      if (tNow < tEnd)
      {
        this.addFix(fw);
      }
      
      // move along the bus, please (used if we're doing a DR Track).
      origin = fw;           
    }

    // sort out our name
    if (getName() == null)
    {
      final String name =
          "infill_" + FormatRNDateTime.toShortString(new Date().getTime());
      this.setName(name);
    }

    // also make it dotted, since it's artificially generated
    this.setLineStyle(CanvasType.DOTTED);
    
    // ok, also try to fire track shifted, if we know our parent
    // (we may not know it when we're first generated)
    if(this.getWrapper() != null)
    {
      this.getWrapper().firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, this);
    }

  }

  private static long meanIntervalFor(TrackSegment segment)
  {
    long sum = 0;
    int ctr = 0;
    long lastTime = -1;
    Enumeration<Editable> iter = segment.elements();
    while(iter.hasMoreElements())
    {
      FixWrapper thisF = (FixWrapper) iter.nextElement();
      final long thisTime = thisF.getDTG().getDate().getTime();
      if(lastTime != -1)
      {
        // ok, we've got a previous value we can compare to
        sum += thisTime - lastTime;
        ctr++;
      }

      lastTime = thisTime;
    }
    
    // what's the average?
    final double mean = sum / ctr;
    
    // trim it to a whole second, if it's large enough
    final long res;
    res = roundToInterval(mean);
    
    return res;
  }

  private static long roundToInterval(final double mean)
  {
    final long res;
    final long MINUTE = 60000;
    final long SECOND = 1000;
    if(mean > MINUTE)
    {
      res = (long) (MINUTE * (Math.floor(mean / MINUTE)));
    }
    else if(mean > SECOND)
    {
      res = (long) (SECOND * (Math.floor(mean / SECOND)));      
    }
    else
    {
      res = (long) mean;
    }
    return res;
  }

  private static Color getColorStrategy(Color trackColor)
  {
    String colorStrategy = Application.getThisProperty(INFILL_COLOR_STRATEGY);
    if (colorStrategy == null)
    {
      colorStrategy = RANDOM_INFILL;
    }

    final Color res;
    switch (colorStrategy)
    {
    case RANDOM_INFILL:
      res = Color.getHSBColor((float) (Math.random() * 360f), 0.8f, 0.8f);
      break;
    case GREEN_INFILL:
      res = Color.GREEN;
      break;
    case DARKER_INFILL:
    default:
      res = trackColor;
    }

    // see what the
    return res;

  }

  private void startWatching(final TrackSegment segment)
  {
    checkListeners();

    segment.addPropertyChangeListener(CoreTMASegment.ADJUSTED, _moveListener);
    segment.addPropertyChangeListener(BaseItemLayer.WRAPPER_CHANGED,
        _wrapperListener);

    // hmm, is it a relative segment?
    if (segment instanceof RelativeTMASegment)
    {
      RelativeTMASegment rel = (RelativeTMASegment) segment;
      SensorWrapper sensor = rel.getReferenceSensor();
      if (sensor != null)
      {
        sensor.addPropertyChangeListener(SensorWrapper.LOCATION_CHANGED,
            _moveListener);
      }
    }
  }

  private void stopWatching(final TrackSegment segment)
  {
    checkListeners();

    if (segment != null)
    {
      segment.removePropertyChangeListener(CoreTMASegment.ADJUSTED,
          _moveListener);
      segment.removePropertyChangeListener(BaseItemLayer.WRAPPER_CHANGED,
          _wrapperListener);

      // hmm, is it a relative segment?
      if (segment instanceof RelativeTMASegment)
      {
        RelativeTMASegment rel = (RelativeTMASegment) segment;
        SensorWrapper sensor = rel.getReferenceSensor();
        if (sensor != null)
        {
          sensor.removePropertyChangeListener(SensorWrapper.LOCATION_CHANGED,
              _moveListener);
        }
      }

    }
  }

  /**
   * one of our tracks has had it's wrapper change. handle this
   * 
   */
  private final void wrapperChange()
  {

    boolean codeRed = false;

    // check the before leg
    if ((_before != null) && (_before.getWrapper() == null))
    {
      codeRed = true;
    }

    // check the after leg
    if ((_after != null) && (_after.getWrapper() == null))
    {
      codeRed = true;
    }

    // are we in trouble
    if (codeRed)
    {
      // ok, burn everything!
      clear();

      // safety check. if we're being deleted, our parent may already be
      // nnull
      if (getWrapper() != null)
      {

        // and remove ourselves from our parent
        getWrapper().removeElement(this);
      }
    }
  }

  @Override
  public Color getColor()
  {
    return _myColor;
  }

  public static class TestInterp extends TestCase
  {
    public void testSimple()
    {
      // generate the location spline
      final double[] times = new double[]{500, 2000, 4000, 5000};
      final double[] longs = new double[]{1d, 1d, 3d, 4d};
      final double[] lats = new double[]{1d, 2d, 4d, 5d};

      final UnivariateInterpolator interpolator = new SplineInterpolator();
      final UnivariateFunction latInterp = interpolator.interpolate(times, lats);
      final UnivariateFunction longInterp =
          interpolator.interpolate(times, longs);

      assertEquals(2.447, latInterp.value(2500), 0.01);
      assertEquals(1.3421, longInterp.value(2500), 0.001);
    }
    
    public void testRound()
    {
      assertEquals(6000, roundToInterval(6100));
      assertEquals(100, roundToInterval(100));
      assertEquals(1000, roundToInterval(1100));
      assertEquals(59000, roundToInterval(59900));
      assertEquals(60000, roundToInterval(69900));
      assertEquals(2220000, roundToInterval(2269900));
    }
  }
  
}
