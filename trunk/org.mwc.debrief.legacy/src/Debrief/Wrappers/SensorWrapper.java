// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SensorWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.17 $
// $Log: SensorWrapper.java,v $
// Revision 1.17  2006/09/25 14:51:15  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.16  2006/02/13 16:19:07  Ian.Mayo
// Sort out problem with creating sensor data
//
// Revision 1.15  2006/01/06 10:37:42  Ian.Mayo
// Reflect tidying of sensor wrapper naming
//
// Revision 1.14  2005/06/06 14:45:06  Ian.Mayo
// Refactor how we support tma & sensor data
//
// Revision 1.13  2005/06/06 14:17:32  Ian.Mayo
// Reproduce TMAWrapper workaround for sensor data where track visible but none of the individual items
//
// Revision 1.12  2005/02/28 14:57:05  Ian.Mayo
// Handle situation when we have sensor & TUA data outside track period.
//
// Revision 1.11  2005/02/22 09:31:58  Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid data points are handled in generic fashion.  We did have two very similar implementations, tracking errors introduced after hi-res-date changes was proving expensive/unreliable.  All fine now though.
//
// Revision 1.10  2005/01/28 10:52:57  Ian.Mayo
// Fix problems where last data point not shown.
//
// Revision 1.9  2005/01/24 10:30:42  Ian.Mayo
// Provide accessor for host track - to help snail plotting
//
// Revision 1.8  2004/12/17 15:54:00  Ian.Mayo
// Get on top of some problems plotting sensor & tma data.
//
// Revision 1.7  2004/11/25 11:04:38  Ian.Mayo
// More test fixing after hi-res switch, largely related to me removing some unused accessors which were property getters
//
// Revision 1.6  2004/11/25 10:24:48  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.5  2004/11/22 13:41:05  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.4  2004/09/10 09:11:28  Ian.Mayo
// Correct prior mistaken implementation of add(Editable) - we should have just changed the signature of add(Plottable) et al
//
// Revision 1.3  2004/09/09 10:51:56  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.2  2004/09/09 10:23:13  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:49:25  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.12  2003-06-23 13:40:12+01  ian_mayo
// Change line width, if necessary
//
// Revision 1.11  2003-06-16 11:57:33+01  ian_mayo
// Improve tests to check we can add/remove sensor contact data
//
// Revision 1.10  2003-03-27 11:22:54+00  ian_mayo
// reflect new strategy where we return all data when asked to filter by invalid time
//
// Revision 1.9  2003-03-19 15:36:52+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.8  2003-01-15 15:48:23+00  ian_mayo
// With getNearestTo, return annotation when no DTG supplied
//
// Revision 1.7  2002-10-30 16:27:25+00  ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.6  2002-10-28 09:04:34+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.5  2002-10-01 15:41:40+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.4  2002-07-10 14:58:57+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.3  2002-07-09 15:27:28+01  ian_mayo
// Return zero-length list instead of null
//
// Revision 1.2  2002-05-28 09:25:13+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:38+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:54+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:27+01  ian_mayo
// Initial revision
//
// Revision 1.9  2001-10-02 09:32:15+01  administrator
// Use new methods for supporting sorted-lists, we aren't getting correct values for tailSet and subSet now that we have changed the comparable implementation within SensorContactWrapper.  We had to do this to allow more than one contact per DTG
//
// Revision 1.8  2001-10-01 12:49:50+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.7  2001-10-01 11:21:39+01  administrator
// Add tests to check we correctly add/manage multiple contacts with the same DTG
//
// Revision 1.6  2001-08-29 19:17:50+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.5  2001-08-24 12:40:25+01  administrator
// Implement remove method
//
// Revision 1.4  2001-08-21 15:19:06+01  administrator
// Improve RangeFrom method
//
// Revision 1.3  2001-08-21 12:05:01+01  administrator
// getFarEnd no longer tries to get its location from the parent
// class testing extended
//
// Revision 1.2  2001-08-17 07:59:19+01  administrator
// Tidying up comments
//
// Revision 1.1  2001-08-14 14:08:17+01  administrator
// finish the implementation
//
// Revision 1.0  2001-08-09 14:16:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-31 16:37:21+01  administrator
// show the length of the narrative list when we get its name
//
// Revision 1.0  2001-07-17 08:41:10+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-16 15:02:10+01  novatech
// provide methods to meet new Plottable signature (setVisible)
//
// Revision 1.2  2001-07-09 14:02:47+01  novatech
// let SensorWrapper handle the stepper control
//
// Revision 1.1  2001-07-06 16:00:27+01  novatech
// Initial revision
//

package Debrief.Wrappers;

import java.beans.*;

import MWC.GUI.Editable;
import MWC.GenericData.*;

public final class SensorWrapper extends TacticalDataWrapper
{

  ////////////////////////////////////////
  // member variables
  ////////////////////////////////////////
 

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * more optimisatons
   */
  transient private SensorContactWrapper nearestContact;

  ////////////////////////////////////////
  // constructors
  /**
   * ////////////////////////////////////////
   */
  public SensorWrapper(final String title)
  {
  	super(title);
  }

  ////////////////////////////////////////
  // member methods to meet plain wrapper responsibilities
  ////////////////////////////////////////


  /**
   * the real getBounds object, which uses properties of the parent
   */
  public final MWC.GenericData.WorldArea getBounds()
  {
    // we no longer just return the bounds of the track, because a portion
    // of the track may have been made invisible.
    // instead, we will pass through the full dataset and find the outer bounds of the visible area
    WorldArea res = null;

    if (!getVisible())
    {
      // hey, we're invisible, return null
    }
    else
    {
      final java.util.Iterator<Editable> it = this._myContacts.iterator();
      while (it.hasNext())
      {
        final SensorContactWrapper fw = (SensorContactWrapper) it.next();

        // is this point visible?
        if (fw.getVisible())
        {

          // has our data been initialised?
          if (res == null)
          {
            // no, initialise it
            WorldLocation startOfLine = fw.getOrigin(_myHost);

            // we may not have a sensor-data origin, since the sensor may be out of the time period of the track
            if(startOfLine != null)
              res = new WorldArea(startOfLine, fw.getFarEnd());
          }
          else
          {
            // yes, extend to include the new area
            res.extend(fw.getOrigin(_myHost));
            res.extend(fw.getFarEnd());
          }
        }
      }
    }

    return res;
  }
 
  
  /**
   * getInfo
   *
   * @return the returned MWC.GUI.Editable.EditorType
   */
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new SensorInfo(this);

    return _myEditor;
  }

  /**
   * add
   *
   * @param plottable parameter for add
   */
  public final void add(final MWC.GUI.Editable plottable)
  {
    // check it's a sensor contact entry
    if (plottable instanceof SensorContactWrapper)
    {
      _myContacts.add(plottable);

      final SensorContactWrapper scw = (SensorContactWrapper) plottable;

      // maintain our time period
      if (_timePeriod == null)
        _timePeriod = new MWC.GenericData.TimePeriod.BaseTimePeriod(scw.getDTG(), scw.getDTG());
      else
        _timePeriod.extend(scw.getDTG());

      // and tell the contact about us
      scw.setSensor(this);
    }
  }


	
	public boolean hasOrderedChildren()
	{
		return false;
	}
	
  /////////////////////////////////////////
  // other member functions
  /////////////////////////////////////////

  /**
   */

  public final String toString()
  {
    return "Sensor:" + getName() + " (" + _myContacts.size() + " items)";
  }
  
  /** how far away are we from this point?
   * or return null if it can't be calculated
   */
  public final double rangeFrom(final WorldLocation other)
  {
  	// and convert back to degrees
  	return nearestContact.rangeFrom(other);
  }  

  ///////////////////////////////////////////////////////////////////
  // support for WatchableList interface (required for Snail Trail plotting)
  ////////////////////////////////////////////////////////////////////


  /**
   * get the watchable in this list nearest to the specified DTG - we take most of this processing
   * from the similar method in TrackWrappper. If the DTG is after our end, return our last point
   *
   * @param DTG the DTG to search for
   * @return the nearest Watchable
   */
  public final Debrief.Tools.Tote.Watchable[] getNearestTo(final HiResDate DTG)
  {

    /** we need to end up with a watchable, not a fix,
     * so we need to work our way through the fixes
     */
    Debrief.Tools.Tote.Watchable[] res = new Debrief.Tools.Tote.Watchable[]{};

    // check that we do actually contain some data
    if (_myContacts.size() == 0)
      return res;

    // see if this is the DTG we have just requestsed
    if ((DTG.equals(lastDTG)) && (lastContact != null))
    {
      res = lastContact;
    }
    else
    {
      // see if this DTG is inside our data range
      // in which case we will just return null
      final SensorContactWrapper theFirst = (SensorContactWrapper) _myContacts.first();
      final SensorContactWrapper theLast = (SensorContactWrapper) _myContacts.last();

      if ((DTG.greaterThanOrEqualTo(theFirst.getDTG())) &&
        (DTG.lessThanOrEqualTo(theLast.getDTG())))
      {
        // yes it's inside our data range, find the first fix
        // after the indicated point

        // see if we have to create our local temporary fix
        if (nearestContact == null)
        {
          nearestContact = new SensorContactWrapper( null, DTG, null, -1, null, null, null, 0, getName());
        }
        else
          nearestContact.setDTG(DTG);

        // get the data..
        final java.util.Vector<SensorContactWrapper> list = new java.util.Vector<SensorContactWrapper>(0, 1);
        boolean finished = false;
        final java.util.Iterator<Editable> it = _myContacts.iterator();
        while ((it.hasNext()) && (!finished))
        {
          final SensorContactWrapper scw = (SensorContactWrapper) it.next();
          HiResDate thisDate = scw.getTime();
          if (thisDate.lessThan(DTG))
          {
            // before it, ignore!
          }
          else if (thisDate.greaterThan(DTG))
          {
            // hey, it's a possible - if we haven't found an exact match
            if (list.size() == 0)
            {
              list.add(scw);
            }
            else
            {
              // hey, we're finished!
              finished = true;
            }
          }
          else
          {
            // hey, it must be at the same time!
            list.add(scw);
          }

        }

        if (list.size() > 0)
        {
          final Debrief.Tools.Tote.Watchable[] dummy = new Debrief.Tools.Tote.Watchable[]{null};
          res = list.toArray(dummy);
        }
      }
      else if (DTG.greaterThanOrEqualTo(theLast.getDTG()))
      {
        // is it after the last one?  If so, just plot the last one.  This helps us when we're doing snail trails.
        final java.util.Vector<SensorContactWrapper> list = new java.util.Vector<SensorContactWrapper>(0, 1);
        list.add(theLast);
        final Debrief.Tools.Tote.Watchable[] dummy = new Debrief.Tools.Tote.Watchable[]{null};
        res = list.toArray(dummy);
      }

      // and remember this fix
      lastContact = res;
      lastDTG = DTG;
    }

    return res;


  }

  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the projection
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public final class SensorInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public SensorInfo(final SensorWrapper data)
    {
      super(data, data.getName(), "Sensor");
    }

    /**
     * The things about these Layers which are editable.
     * We don't really use this list, since we have our own custom editor anyway
     *
     * @return property descriptions
     */
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name for this sensor"),
          prop("Visible", "whether this sensor data is visible"),
          prop("LineThickness", "the thickness to draw these sensor lines"),
          prop("Color", "the colour to plot this set of sensor data"),
        };

        res[2].setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);


        return res;
      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////////
  // nested class for testing
  ///////////////////////////////////////////////////////

  static public final class testSensors extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testSensors(final String val)
    {
      super(val);
    }

    public final void testValues()
    {
      // ok, create the test object
      final SensorWrapper sensor = new SensorWrapper("tester");

      final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

      // and create the list of sensor contact data items
      cal.set(2001, 10, 4, 4, 4, 0);
      final long start_time = cal.getTime().getTime();
      sensor.add(new SensorContactWrapper( "tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 23);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 25);
      sensor.add(new SensorContactWrapper( "tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 27);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 02);
      sensor.add(new SensorContactWrapper( "tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 01);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 05);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 55);
      final long end_time = cal.getTime().getTime();
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      // so, we've now build up the list
      // check it has the correct quantity
      assertTrue("Count of items", (sensor._myContacts.size() == 8));

      // check the outer limits
      final HiResDate start = sensor.getStartDTG();
      final HiResDate end = sensor.getEndDTG();
      assertEquals("first time", start.getDate().getTime(), start_time);
      assertEquals("last time", end.getDate().getTime(), end_time);

      ////////////////////////////////////////////////////////////////////////
      // finding the nearest entry
      cal.set(2001, 10, 4, 4, 4, 05);
      Debrief.Tools.Tote.Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime()));
      SensorContactWrapper nearest = (SensorContactWrapper) list[0];
      assertEquals("Nearest matching fix", nearest.getDTG().getDate().getTime(), cal.getTime().getTime());

      final java.util.Calendar cal_other = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);
      cal_other.set(2001, 10, 4, 4, 4, 03);
      list = sensor.getNearestTo(new HiResDate(cal_other.getTime().getTime()));
      nearest = (SensorContactWrapper) list[0];
      assertTrue("Nearest or greater than fix", (nearest.getDTG().getMicros() / 1000 == cal.getTime().getTime()));

      /////////////////////////////////////////////////////////////////////
      // filter the list
      cal.set(2001, 10, 4, 4, 4, 22);
      cal_other.set(2001, 10, 4, 4, 4, 25);

      //////////////////////////////////////////////////////////////////////////
      // do the filter
      sensor.filterListTo(new HiResDate(cal.getTime().getTime()), new HiResDate(cal_other.getTime().getTime()));

      // see how many remain visible
      java.util.Enumeration<Editable> iter = sensor.elements();
      int counter = 0;
      while (iter.hasMoreElements())
      {
        final SensorContactWrapper contact = (SensorContactWrapper) iter.nextElement();
        if (contact.getVisible())
          counter++;
      }
      // check that the correct number are visible
      assertTrue("Correct filtering of list", (counter == 2));

      // clear the filter
      sensor.filterListTo(sensor.getStartDTG(), sensor.getEndDTG());
      // see how many remain visible
      iter = sensor.elements();
      counter = 0;
      while (iter.hasMoreElements())
      {
        final SensorContactWrapper contact = (SensorContactWrapper) iter.nextElement();
        if (contact.getVisible())
          counter++;
      }
      // check that the correct number are visible
      assertTrue("Correct removal of list filter", (counter == 8));

      ////////////////////////////////////////////////////////
      // get items between
      java.util.Collection<Editable> res = sensor.getItemsBetween(new HiResDate(cal.getTime().getTime()),
                                                        new HiResDate(cal_other.getTime().getTime()));
      assertTrue("get items between", (res.size() == 2));

      // do recheck, since this time we will be resetting the working variables, rather and creating them
      cal.set(2001, 10, 4, 4, 4, 5);
      cal_other.set(2001, 10, 4, 4, 4, 27);
      res = sensor.getItemsBetween(new HiResDate(cal.getTime().getTime()),
                                    new HiResDate(cal_other.getTime().getTime()));
      assertEquals("recheck get items between:" + res.size(), 4, res.size());

      // and show all of the data
      res = sensor.getItemsBetween(sensor.getStartDTG(), sensor.getEndDTG());
      assertTrue("recheck get items between:" + res.size(), (res.size() == 8));

      ///////////////////////////////////////////////////////////
      // test the position related stuff
      final TrackWrapper track = new TrackWrapper();
      track.setTrack(new MWC.TacticalData.Track());

      // and add the fixes
      cal.set(2001, 10, 4, 4, 4, 0);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.0, 2.0, 0.0), 12, 12)));


      cal.set(2001, 10, 4, 4, 4, 01);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.0, 2.25, 0.0), 12, 12)));

      cal.set(2001, 10, 4, 4, 4, 02);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.0, 2.5, 0.0), 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 05);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.0, 2.75, 0.0), 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 23);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.25, 2.0, 0.0), 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 25);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.5, 2.0, 0.0), 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 28);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.75, 2.0, 0.0), 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 55);
      track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
                                                           new MWC.GenericData.WorldLocation(2.25, 2.25, 0.0), 12, 12)));

      // ok, put the sensor data into the track
      track.add(sensor);

      // now find the location of an item, any item!
      cal.set(2001, 10, 4, 4, 4, 27);
      list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(),0));
      nearest = (SensorContactWrapper) list[0];
      assertEquals("first test", nearest.getOrigin(track), new MWC.GenericData.WorldLocation(2.75, 2.0, 0.0));

      // ah-ha! what about a contact between two fixes
      cal.set(2001, 10, 4, 4, 4, 26);
      list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
      nearest = (SensorContactWrapper) list[0];
      assertEquals("test mid way", nearest.getOrigin(track), new MWC.GenericData.WorldLocation(2.75, 2.0, 0.0));

      // ok, that was half-way, what making it nearer to one of the fixes
      cal.set(2001, 10, 4, 4, 4, 25);
      list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
      nearest = (SensorContactWrapper) list[0];
      assertEquals("test nearer first point", nearest.getOrigin(track), (new MWC.GenericData.WorldLocation(2.5, 2.0, 0.0)));

      // start point?
      cal.set(2001, 10, 4, 4, 4, 0);
      list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
      nearest = (SensorContactWrapper) list[0];
      assertEquals("test start point", nearest.getOrigin(track), new MWC.GenericData.WorldLocation(2.0, 2.0, 0.0));

      // end point?
      cal.set(2001, 10, 4, 4, 4, 55);
      list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
      nearest = (SensorContactWrapper) list[0];
      assertEquals("test end point", nearest.getOrigin(track), new MWC.GenericData.WorldLocation(2.25, 2.25, 0.0));

      // before start of track data?
      cal.set(2001, 10, 4, 4, 3, 0);
      list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
      assertEquals("before range of data", list.length, 0);

      // after end of track data?
      cal.set(2001, 10, 4, 4, 7, 0);
      list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
      assertEquals("after end of data", list.length, 1);


    }

    public final void testDuplicates()
    {
      // ok, create the test object
      final SensorWrapper sensor = new SensorWrapper("tester");

      final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

      // and create the list of sensor contact data items
      cal.set(2001, 10, 4, 4, 4, 0);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 23);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 24);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 25);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()),null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 25);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 01);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 05);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 55);
      sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, 0, null, null, null, 1, sensor.getName()));

      // so, we've now build up the list
      // check it has the correct quantity
      assertEquals("Count of items", 8, sensor._myContacts.size());

      // check the correct number get returned
      cal.set(2001, 10, 4, 4, 4, 25);
      final Debrief.Tools.Tote.Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
      assertEquals("after end of data", 2, list.length);

    }


    public void testMultipleContacts()
    {
      SensorWrapper sw = new SensorWrapper("bbb");
      SensorContactWrapper sc1 = new SensorContactWrapper("bbb", new HiResDate(0,9), null, 0, null, null, "first", 0, sw.getName());
      SensorContactWrapper sc2 = new SensorContactWrapper("bbb", new HiResDate(0,12), null, 0, null, null, "first", 0, sw.getName());
      SensorContactWrapper sc3 = new SensorContactWrapper("bbb", new HiResDate(0,7), null, 0, null, null, "first", 0, sw.getName());
      SensorContactWrapper sc4 = new SensorContactWrapper("bbb", new HiResDate(0,13), null, 0, null, null, "first", 0, sw.getName());

      sw.add(sc1);
      sw.add(sc2);
      sw.add(sc3);
      sw.add(sc4);

      assertEquals("four contacts loaded", 4, sw._myContacts.size());

      // check we can delete from it
      sw.removeElement(sc3);

      assertEquals("now only three contacts loaded", 3, sw._myContacts.size());


    }
  }

  public static void main(final String[] args)
  {
    final testSensors ts = new testSensors("Ian");
    ts.testDuplicates();
    ts.testValues();
  }


}
