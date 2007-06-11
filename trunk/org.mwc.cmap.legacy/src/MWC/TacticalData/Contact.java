// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Contact.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: Contact.java,v $
// Revision 1.4  2005/12/13 09:03:25  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2004/11/24 16:05:33  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.2  2004/05/24 16:26:46  Ian.Mayo
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
// Revision 1.1  2001-01-03 13:41:36+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:06  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:53:49+01  ian_mayo
// Initial revision
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
import MWC.GenericData.WorldVector;


/**
 * a composite structure containing a time stamp,
 * a location, and a set of vessel parameter
 */
public class Contact implements java.io.Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  protected WorldVector _sensorOffset;
  protected double _range;
  protected double _bearing;
  protected Fix _host;
  protected String _track;
  protected HiResDate _dtg;
  protected String _message;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * default constructor, used serialization
   */
  public Contact()
  {
  }

  /**
   * Constructor, normal parameters
   */
  public Contact(String track,
                 HiResDate dtg,
                 WorldVector sensorOffset,
                 double rangeYds,
                 double bearingDegs,
                 Fix host,
                 String message)
  {
    _sensorOffset = sensorOffset;
    _range = MWC.Algorithms.Conversions.Yds2Degs(rangeYds);
    _bearing = MWC.Algorithms.Conversions.Degs2Rads(bearingDegs);
    _host = host;
    _track = track;
    _dtg = dtg;
    _message = message;
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////
  public WorldVector getOffset()
  {
    return _sensorOffset;
  }

  /**
   * get the distance in degrees
   */
  public double getRange()
  {
    return _range;
  }

  /**
   * get the direction in radians (relative bearing)
   */
  public double getBearing()
  {
    return _bearing;
  }

  public String getTrackName()
  {
    return _track;
  }

  public HiResDate getTime()
  {
    return _dtg;
  }

  /**
   * return the label for this contact
   */
  public String getString()
  {
    return _message;
  }

}




