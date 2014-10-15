
/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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

import MWC.Algorithms.Conversions;
import MWC.Algorithms.EarthModel;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class FlatEarth implements EarthModel
{


  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  /**
   * working world location, used to reduce amount of object creation
   */
  private final WorldLocation _workingLocation = new WorldLocation(0,0,0);

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * add a vector to a location: note that the value returned
   * is the same instance of an object each time
   *
   */
	public WorldLocation add(final WorldLocation start, final WorldVector delta)
	{
		if((delta.getRange() == 0) && (delta.getDepth() == 0) )
			return start;

  	// the algorithm used here is from Short Sailing Calculations in the
  	// admiralty manual of navigation

  	// 0. convert our position, course and distance values in degrees to radians
  	final double LAT = Conversions.Degs2Rads(start.getLat());
  	final double LONG = Conversions.Degs2Rads(start.getLong());
  	final double COURSE = delta.getBearing();

  	// produce a range in radians
  	final double DISTANCE = Conversions.Degs2Rads(delta.getRange());

  	// 1.	find the departure
  	final double DEPARTURE = DISTANCE * Math.sin(COURSE);

  	// 2. find the delta.lat
  	final double DELTA_LAT = DISTANCE * Math.cos(COURSE);

  	// 3. find the mean latitude
  	final double MEAN_LAT = LAT + (DELTA_LAT/2.0);

  	// 4. find the delta.long
  	final double DELTA_LONG = DEPARTURE / Math.cos(MEAN_LAT);


  	// 5. produce the new position
  	final double NEW_LAT = LAT + DELTA_LAT;
  	final double NEW_LONG = LONG + DELTA_LONG;

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
  public double bearingBetween(final WorldLocation from, final WorldLocation to)
  {
		final WorldVector res = from.subtract(to);

    return res.getBearing();
  }

  /**
   * rangeBetween
   *
   * @param from parameter for rangeBetween
   *
   * @return the returned double
   */
  public double rangeBetween(final WorldLocation from, final WorldLocation to)
  {
		final WorldVector res = subtract(from, to);
		return res.getRange();
	}


  public WorldVector subtract(final WorldLocation from,
                              final WorldLocation to)
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
  public WorldVector subtract(final WorldLocation from, final WorldLocation to, final WorldVector res)
	{

		// the algorithm used here is from Short Sailing Calculations in the
		// admiralty manual of navigation

	  // perform brief check to ensure that the positions are not identical
	  if (from == null || from.equals(to))
	  	return new WorldVector(0,0,0);

	  // 0. convert our position, course and distance values in degrees to radians
	  final double LAT2 = Conversions.Degs2Rads(to.getLat());
	  final double LONG2 = Conversions.Degs2Rads(to.getLong());
	  final double LAT1 = Conversions.Degs2Rads(from.getLat());
	  final double LONG1 = Conversions.Degs2Rads(from.getLong());

	  // 1.	find the deltas
	  final double DELTA_LONG = LONG2 - LONG1;
	  final double DELTA_LAT  = LAT2  - LAT1;

	  // 2. find the mean latitude
	  final double MEAN_LAT = LAT1 + (DELTA_LAT) / 2.0;

	  // 3.	find the departure
	  final double DEPARTURE = DELTA_LONG * Math.cos(MEAN_LAT);

	  // 4.	find the course
	  final double COURSE = Math.atan2( DEPARTURE, DELTA_LAT );

	  // 5.	find the distance. There is a check here in case the delta_lat is zero,
	  // if this is the case then the dir is either e or w, so just use the delta_lat
	  double DISTANCE;
	  if(DELTA_LAT == 0)
	  	DISTANCE = DELTA_LONG * Math.cos(MEAN_LAT);
	  else
	  	DISTANCE = DELTA_LAT / Math.cos(COURSE);

	  // 6. Hooray, now produce the result from our course and distance
	  // measurements
	  final double dist = Conversions.Rads2Degs(Math.abs(DISTANCE));

    // put the results back into the parameter supplied
    res.setValues(COURSE, dist, to.getDepth() - from.getDepth());

	  return res;
}

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////



}
