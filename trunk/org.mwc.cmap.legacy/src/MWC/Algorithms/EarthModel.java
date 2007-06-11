// Copyright MWC 1999
// $RCSfile: EarthModel.java,v $
// $Author: Ian.Mayo $
// $Log: EarthModel.java,v $
// Revision 1.2  2004/05/24 16:28:13  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:13  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:06:59  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-03-14 08:35:00+00  ian_mayo
// provide subtract methods which re-use single WorldVector object, to reduce object creation
//
// Revision 1.2  2002-05-28 09:25:33+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:39+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:47:01+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:13+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:21  ianmayo
// initial version
//
// Revision 1.3  2000-04-19 11:37:15+01  ian_mayo
// provide Subtract method
//
// Revision 1.2  2000-01-12 15:38:49+00  ian_mayo
// made imports more specific
//
// Revision 1.1  1999-10-12 15:37:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:54+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:12+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:03+01  sm11td
// Initial revision
//
// Revision 1.1  1999-06-04 08:45:30+01  sm11td
// Initial revision
//

package MWC.Algorithms;


import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public interface EarthModel
{
  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////
  public double rangeBetween(WorldLocation from,
                             WorldLocation to);

  public double bearingBetween(WorldLocation from,
                               WorldLocation to);

  public WorldLocation add(WorldLocation base,
                           WorldVector delta);

	public WorldVector subtract(WorldLocation from,
															WorldLocation to);

  public WorldVector subtract(WorldLocation from,
                              WorldLocation to,
                              WorldVector res);


}
