// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Fix.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: Fix.java,v $
// Revision 1.3  2004/11/24 16:05:34  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.2  2004/05/24 16:26:48  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:49  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:05+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:58+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:28+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:49+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:37+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:06  ianmayo
// initial version
//
// Revision 1.6  2000-08-09 16:03:29+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.5  2000-08-07 12:22:38+01  ian_mayo
// white space only
//
// Revision 1.4  2000-04-19 11:42:27+01  ian_mayo
// provide setters for speed and course
//
// Revision 1.3  2000-03-14 09:56:45+00  ian_mayo
// add setTime method (used when searching a SortedSet), and add SerialVersionID
//
// Revision 1.2  2000-02-04 16:06:28+00  ian_mayo
// Allow Location to be changed
//
// Revision 1.1  1999-10-12 15:36:16+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:45:33+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:31+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-19 12:40:33+01  administrator
// added storage of sub-second time data (Switched to storing as Long rather than java.utils.Date)
//
// Revision 1.1  1999-07-07 11:10:01+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:55+01  sm11td
// Initial revision
//
// Revision 1.2  1999-06-01 16:49:19+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.1  1999-01-31 13:32:58+00  sm11td
// Initial revision
//

package MWC.TacticalData;

import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;


/**
 * a composite structure containing a time stamp,
 * a location, and a set of vessel parameter
 */
public class Fix implements java.io.Serializable
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  static final long serialVersionUID = 5742516721502711888L;
  private double _theCourse; // in radians
  private double _theSpeed;  // in yards per second
  private HiResDate _theTime; // (micros)
  private WorldLocation _theLocation; 

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * default constructor, used serialization
   */
  public Fix()
  {
  }

  /**
   * Constructor, normal parameters
   *
   * @param theTime     dtg of fix
   * @param theLocation is the location for this fix
   * @param theCourse   the current course (in Radians)
   * @param theSpeed    the current speed (in yards per second)
   */
  public Fix(HiResDate theTime,
             WorldLocation theLocation,
             double theCourse,
             double theSpeed)
  {
    _theTime = theTime;
    _theLocation = theLocation;
    _theCourse = theCourse;
    _theSpeed = theSpeed;
  }
  
  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  /** create deep clone of ourselves
   * 
   */
  public Fix makeCopy()
  {
  	Fix newFix = new Fix();
  	newFix._theCourse = _theCourse;
  	newFix._theSpeed = _theSpeed;
  	newFix._theLocation = new WorldLocation(_theLocation);
  	newFix._theTime = new HiResDate(_theTime);
  	return newFix;
  }
  
  /**
   * get the current vessel speed (yps)
   *
   * @return vessel speed in yards per second
   */
  public double getSpeed()
  {
    return _theSpeed;
  }

  /**
   * get the current vessel course (rads)
   *
   * @return vessel course in radians
   */
  public double getCourse()
  {
    return _theCourse;
  }

  /**
   * get the current vessel location
   *
   * @return vessel location
   */
  public WorldLocation getLocation()
  {
    return _theLocation;
  }

  /**
   * set the location of the fix
   */
  public void setLocation(WorldLocation val)
  {
    _theLocation = val;
  }

  /**
   * set the time of the fix
   */
  public void setTime(HiResDate dtg)
  {
    _theTime = dtg;
  }

  /**
   * set the course of the fix (rads)
   */
  public void setCourse(double rads)
  {
    _theCourse = rads;
  }

  /**
   * set the speed of the fix (yards per sec)
   */
  public void setSpeed(double yps)
  {
    _theSpeed = yps;
  }

  /**
   * get the timestamp of this fix
   *
   * @return dtg of fix
   */
  public HiResDate getTime()
  {
    return _theTime;
  }

}




