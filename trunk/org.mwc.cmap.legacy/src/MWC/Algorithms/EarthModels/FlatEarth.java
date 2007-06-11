
/**
 * MWC.Algorithms.EarthModels.FlatEarth
 *
 * OK, I know this is called the FlatEarth earth model,
 * really it is using the "short distance sailing" algorithm of
 * rhumb lines.  So actually, it's a locally flat earth
 * distance algrorithm
 */

 package MWC.Algorithms.EarthModels;


// Copyright MWC 1999
// $RCSfile: FlatEarth.java,v $
// $Author: Ian.Mayo $
// $Log: FlatEarth.java,v $
// Revision 1.3  2006/03/23 09:18:39  Ian.Mayo
// Tidying (remove unused object)
//
// Revision 1.2  2004/05/24 16:28:17  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:13  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:06:59  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-03-14 08:34:59+00  ian_mayo
// provide subtract methods which re-use single WorldVector object, to reduce object creation
//
// Revision 1.3  2002-10-28 09:25:27+00  ian_mayo
// improve invalid data check
//
// Revision 1.2  2002-05-28 09:25:33+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:38+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:47:00+01  administrator
// Initial revision
//
// Revision 1.3  2001-06-04 09:29:17+01  novatech
// improve comments
//
// Revision 1.2  2001-01-18 13:52:40+00  novatech
// part way through optimisations, reduing object creation
//
// Revision 1.1  2001-01-03 13:43:12+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:23  ianmayo
// initial version
//
// Revision 1.2  2000-04-19 11:38:12+01  ian_mayo
// implement short distance sailing algorithms
//
// Revision 1.1  1999-10-12 15:37:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:54+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:11+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:03+01  sm11td
// Initial revision
//
// Revision 1.1  1999-06-04 08:45:29+01  sm11td
// Initial revision
//

import MWC.GenericData.*;
import MWC.Algorithms.*;

public class FlatEarth implements EarthModel
{


  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  /**
   * working world location, used to reduce amount of object creation
   */
  private WorldLocation _workingLocation = new WorldLocation(0,0,0);

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * add a vector to a location: note that the value returned
   * is the same instance of an object each time
   *
   */
	public WorldLocation add(WorldLocation start, WorldVector delta)
	{
		if((delta.getRange() == 0) && (delta.getDepth() == 0) )
			return start;

  	// the algorithm used here is from Short Sailing Calculations in the
  	// admiralty manual of navigation

  	// 0. convert our position, course and distance values in degrees to radians
  	double LAT = Conversions.Degs2Rads(start.getLat());
  	double LONG = Conversions.Degs2Rads(start.getLong());
  	double COURSE = delta.getBearing();

  	// produce a range in radians
  	double DISTANCE = Conversions.Degs2Rads(delta.getRange());

  	// 1.	find the departure
  	double DEPARTURE = DISTANCE * Math.sin(COURSE);

  	// 2. find the delta.lat
  	double DELTA_LAT = DISTANCE * Math.cos(COURSE);

  	// 3. find the mean latitude
  	double MEAN_LAT = LAT + (DELTA_LAT/2.0);

  	// 4. find the delta.long
  	double DELTA_LONG = DEPARTURE / Math.cos(MEAN_LAT);


  	// 5. produce the new position
  	double NEW_LAT = LAT + DELTA_LAT;
  	double NEW_LONG = LONG + DELTA_LONG;

    // use our internal object for calculation, to reduce object creation
    _workingLocation.setLat(Conversions.Rads2Degs(NEW_LAT));
    _workingLocation.setLong(Conversions.Rads2Degs(NEW_LONG));
    _workingLocation.setDepth(start.getDepth() + delta.getDepth());

  	// 6. Hooray, now produce the result
  	return _workingLocation;
  }


  /**
   * bearingBetween
   *
   * @param from parameter for bearingBetween
   *
   * @return the returned double
   */
  public double bearingBetween(WorldLocation from, WorldLocation to)
  {
		WorldVector res = from.subtract(to);

    return res.getBearing();
  }

  /**
   * rangeBetween
   *
   * @param from parameter for rangeBetween
   *
   * @return the returned double
   */
  public double rangeBetween(WorldLocation from, WorldLocation to)
  {
		WorldVector res = subtract(from, to);
		return res.getRange();
	}


  public WorldVector subtract(WorldLocation from,
                              WorldLocation to)
  {
    WorldVector res = new WorldVector(0,0,0);
    res = subtract(from, to, res);
    return res;
  }

	/**
   * subtract
   *
   * @param from parameter for subtract
   *
   * @return the returned WorldVector
   */
  public WorldVector subtract(WorldLocation from, WorldLocation to, WorldVector res)
	{

		// the algorithm used here is from Short Sailing Calculations in the
		// admiralty manual of navigation

	  // perform brief check to ensure that the positions are not identical
	  if (from.equals(to))
	  	return new WorldVector(0,0,0);

	  // 0. convert our position, course and distance values in degrees to radians
	  double LAT2 = Conversions.Degs2Rads(to.getLat());
	  double LONG2 = Conversions.Degs2Rads(to.getLong());
	  double LAT1 = Conversions.Degs2Rads(from.getLat());
	  double LONG1 = Conversions.Degs2Rads(from.getLong());

	  // 1.	find the deltas
	  double DELTA_LONG = LONG2 - LONG1;
	  double DELTA_LAT  = LAT2  - LAT1;

	  // 2. find the mean latitude
	  double MEAN_LAT = LAT1 + (DELTA_LAT) / 2.0;

	  // 3.	find the departure
	  double DEPARTURE = DELTA_LONG * Math.cos(MEAN_LAT);

	  // 4.	find the course
	  double COURSE = Math.atan2( DEPARTURE, DELTA_LAT );

	  // 5.	find the distance. There is a check here in case the delta_lat is zero,
	  // if this is the case then the dir is either e or w, so just use the delta_lat
	  double DISTANCE;
	  if(DELTA_LAT == 0)
	  	DISTANCE = DELTA_LONG * Math.cos(MEAN_LAT);
	  else
	  	DISTANCE = DELTA_LAT / Math.cos(COURSE);

	  // 6. Hooray, now produce the result from our course and distance
	  // measurements
	  double dist = Conversions.Rads2Degs(Math.abs(DISTANCE));

    // put the results back into the parameter supplied
    res.setValues(COURSE, dist, to.getDepth() - from.getDepth());

	  return res;
}

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////



}
