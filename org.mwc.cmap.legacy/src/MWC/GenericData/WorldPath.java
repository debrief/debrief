/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GenericData;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public final class WorldPath implements java.io.Serializable
{


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
  // Member Variables
  //////////////////////////////////////////////////////////////////////
  /**
   * our points
   */
  Vector<WorldLocation> _myPoints;

  /**
   * the area we cover
   */
  private WorldArea _myBounds;
  //////////////////////////////////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////////////////////////////////

  public WorldPath()
  {
    _myPoints = new Vector<WorldLocation>(0, 1);
  }

  public WorldPath(final WorldLocation[] path)
  {
  	_myPoints = new Vector<WorldLocation>(path.length, 1);
    for (int i = 0; i < path.length; i++)
    {
      _myPoints.add(new WorldLocation(path[i]));
    }
    refreshBounds();
  }

  public WorldPath(final WorldPath path)
  {
  	_myPoints = new Vector<WorldLocation>(path.size(),1);
  	for (int i = 0; i < path.size(); i++)
    {
      _myPoints.add(new WorldLocation(path.getLocationAt(i)));
    }
  	refreshBounds();
  }

  //////////////////////////////////////////////////////////////////////
  // Member methods
  //////////////////////////////////////////////////////////////////////
  /**
   * get the list of locations
   */
  public final Collection<WorldLocation> getPoints()
  {
    return _myPoints;
  }
  
  /** get the index of a particular point
   * 
   */
  public int indexOf(final WorldLocation loc)
  {
  	return _myPoints.indexOf(loc);
  }
  
  /** put this location in at the indicated point
   * 
   * @param loc
   * @param index
   */
  public void insertPointAt(final WorldLocation loc, final int index)
  {
  	_myPoints.insertElementAt(loc, index);
  }

  /**
   * add this location
   */
  public final void addPoint(final WorldLocation loc)
  {
    // check it's not null
    if (loc == null)
      return;

    _myPoints.addElement(loc);

    // update the bounds
    refreshBounds();
  }

  /**
   * get the bounds of the area
   */
  public final WorldArea getBounds()
  {
    return _myBounds;
  }

  /**
   * refresh the area covered
   */
  public final void refreshBounds()
  {
    WorldArea res = null;
    final Iterator<WorldLocation> it = _myPoints.iterator();
    while (it.hasNext())
    {
      final WorldLocation nx = (WorldLocation) it.next();
      if (res == null)
        res = new WorldArea(nx, nx);
      else
        res.extend(nx);
    }

    _myBounds = res;
  }

  /**
   * get the indicated location
   */
  public final WorldLocation getLocationAt(final int i)
  {
    return (WorldLocation) _myPoints.elementAt(i);
  }

  /**
   * the length of the list
   */
  public final int size()
  {
    return _myPoints.size();
  }

  /**
   * remove the indicated point from the list
   */
  public final void remove(final WorldLocation point)
  {
    _myPoints.remove(point);

    refreshBounds();
  }


  /**
   * copy the other path into ourselves
   *
   * @param destinations path to copy
   */
  public void add(final WorldPath destinations)
  {
    for (int i = 0; i < destinations.size(); i++)
    {
      addPoint(new WorldLocation(destinations.getLocationAt(i)));
    }

  }


  /**
   * move the indicate point up the list by one
   */
  public final void moveUpward(final WorldLocation point)
  {
    final int index = _myPoints.indexOf(point);
    if (index != -1)
    {
      // ok, at least it's in the list, now remove it
      _myPoints.remove(point);

      // produce the new index
      final int newIndex = Math.max(0, index - 1);

      // and put it in
      _myPoints.insertElementAt(point, newIndex);
    }
  }

  /**
   * move the indicated point down the list by one
   */
  public final void moveDownward(final WorldLocation point)
  {
    final int index = _myPoints.indexOf(point);
    if (index != -1)
    {
      // ok, at least it's in the list, now remove it
      _myPoints.remove(point);

      // produce the new index
      final int newIndex = Math.min(_myPoints.size(), index + 1);

      // and put it in
      _myPoints.insertElementAt(point, newIndex);
    }
  }

  /**
   * equality operator
   */
  public final boolean equals(final Object obj)
  {
  	if(obj == null)
  		return false;
  	
    final WorldPath other = (WorldPath) obj;

    boolean equals = true;

    // how long is our list
    final int num = _myPoints.size();

    // see if they are the same size
    if (other.size() != num)
    {
      // different lengths, cannot be equals
      equals = false;
    }
    else
    {
      // so they're the same length
      for (int i = 0; i < num; i++)
      {
        //
        if (!_myPoints.elementAt(i).equals(other._myPoints.elementAt(i)))
        {
          // hey, they're different!
          equals = false;
          break;
        }
      }
    }

    return equals;
  }

  /**
   * break the provided path into a number of straight legs
   *
   * @param numStops the number of legs to break down
   * @return broken down path
   */
  public WorldPath breakIntoStraightSections(final int numStops)
  {

    final double leg_length =  getTotalDistance().getValueIn(WorldDistance.DEGS) / numStops;
    double needed = leg_length;

    int thisSection = 1;
    int thisLeg = 0;

    WorldVector sep = getSeparation(thisLeg);
    double currentBearing = sep.getBearing();
    double leg_remaining = sep.getRange();


    final WorldPath res = new WorldPath();
    WorldLocation lastOrigin = (WorldLocation) _myPoints.firstElement();

    while(thisSection < numStops)
    {
      // ok, is there enough left in this leg to finish off a point?
      if(leg_remaining >= needed)
      {
        // ok, we can mark off this point, and move on to the next

        // consume a little more
        leg_remaining -= needed;

        // create this intermediate point
        final WorldLocation newLoc = lastOrigin.add(new WorldVector(currentBearing, needed, 0));
        res.addPoint(new WorldLocation(newLoc));
        thisSection++;

        needed = leg_length;
        lastOrigin = newLoc;
      }
      else
      {
        // or do we need to move onto the next path point?
        // ok, use up the remaining distance
        needed -= leg_remaining;

        // ok, move on to the next point.
        thisLeg++;

        sep = getSeparation(thisLeg);
        currentBearing = sep.getBearing();
        leg_remaining = sep.getRange();

        lastOrigin = (WorldLocation) _myPoints.elementAt(thisLeg);

      }

    }

    // ok, dropped out.  just use the last point as the final one
    res.addPoint(new WorldLocation((WorldLocation) _myPoints.lastElement()));
//
//
//    // do some funny processing just to make use of getTotalDistance.
//    WorldDistance totalLen = getTotalDistance();
//    if (totalLen != null)
//      totalLen = null;
//
//
//    // @@ hack - we should be calculating this stuff.
//    WorldPath res = new WorldPath(this);

    return res;
  }

  private WorldVector getSeparation(final int thisLeg)
  {
    final WorldLocation lasPoint = (WorldLocation) _myPoints.elementAt(thisLeg);
    final WorldLocation nextPoint = (WorldLocation) _myPoints.elementAt(thisLeg+1);
    final WorldVector sep = nextPoint.subtract(lasPoint);
    return sep;
  }
  
  /**
   * calculate the total distance covered by this path
   *
   * @return total distance
   */
  private WorldDistance getTotalDistance()
  {
    double degs = 0d;
    WorldLocation lastLocation = null;
    for (final Iterator<WorldLocation> iterator = _myPoints.iterator(); iterator.hasNext();)
    {
      final WorldLocation thisPoint = (WorldLocation) iterator.next();
      if (lastLocation != null)
      {
        final double thisDegs = thisPoint.rangeFrom(lastLocation);
        degs += thisDegs;
      }

      lastLocation = thisPoint;

    }

    // and convert the elapsed distance back
    final WorldDistance res = new WorldDistance(degs, WorldDistance.DEGS);

    return res;  //To change body of created methods use File | Settings | File Templates.
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class PathTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public PathTest(final String val)
    {
      super(val);
    }

    public final void testBreakPath()
    {
      final WorldLocation loc1 = new WorldLocation(0, 0, 0);
      final WorldLocation loc2 = new WorldLocation(0, 2, 0);
      final WorldLocation loc3 = new WorldLocation(0, 0, 4);
      final WorldLocation loc4 = new WorldLocation(0, 0, 8);
      final WorldLocation loc5 = new WorldLocation(0, 3, 4);

      final WorldPath newPath = new WorldPath(new WorldLocation[]{loc1, loc2, loc3, loc4, loc5});
      final WorldPath brokenDownPath = newPath.breakIntoStraightSections(2);
      assertEquals("broken down into wrong number of sections - is it implemented even?", 2, brokenDownPath.size(), 0);

    }

    public final void testMovement()
    {
      final WorldLocation w1 = new WorldLocation(1, 1, 1);
      final WorldLocation w2 = new WorldLocation(2, 2, 2);
      final WorldLocation w3 = new WorldLocation(3, 3, 3);
      final WorldLocation w4 = new WorldLocation(4, 4, 4);
      final WorldLocation w5 = new WorldLocation(5, 5, 5);

      final WorldPath path = new WorldPath();
      path.addPoint(w1);

      // check the area
      WorldArea ar = path.getBounds();
      assertTrue("area updated", ar.equals(new WorldArea(w1, w1)));

      path.addPoint(w2);

      assertTrue("area changed", !(ar.equals(path.getBounds())));

      path.addPoint(w3);
      path.addPoint(w4);

      // add duff point
      path.addPoint(null);

      // check they're all there
      assertEquals("all points loaded", 4, path._myPoints.size());

      // check the area
      ar = path.getBounds();

      assertTrue("full area contained", ar.equals(new WorldArea(w1, w4)));
      assertEquals("found fourth item", w4, path.getLocationAt(3));

      // move up
      path.moveUpward(w4);
      assertEquals("found third in fourth slot item", w3, path.getLocationAt(3));

      // move invalid upwards
      path.moveUpward(w5);

      // move first upwards
      path.moveUpward(w1);
      assertEquals("first item still in slot", w1, path.getLocationAt(0));

      // move last downwards
      path.moveDownward(w3);
      assertEquals("last item still in slot", w3, path.getLocationAt(3));

      // move invalid downwards
      path.moveDownward(w5);

      // remove invalid
      path.remove(w5);
      assertEquals("still contains 4", 4, path.size());

      // remove others
      path.remove(w3);
      assertEquals("dropped to 3", 3, path.size());

      // move first upwards
      path.moveUpward(w1);
      assertEquals("first item still in slot", w1, path.getLocationAt(0));

      // move last downwards
      path.moveDownward(w4);
      assertEquals("last item still in slot", w4, path.getLocationAt(2));

      path.remove(w2);
      path.remove(w1);

      path.moveUpward(w4);
      path.moveDownward(w4);

      path.remove(w4);

      path.moveUpward(w4);

      ar = path.getBounds();
      assertEquals("empty area with no points", null, ar);
    }

    public void testBreakSections()
    {
      final WorldPath thePath = new WorldPath(new WorldLocation[]{
                  new WorldLocation(0,0,0),
                  new WorldLocation(2, 1, 0),
                  new WorldLocation(3, 4, 0),
                  new WorldLocation(2, 5, 0),
                  new WorldLocation(0, 6, 0)});
      final int PATH_LENGTH = 8;
      final WorldPath brokenPath = thePath.breakIntoStraightSections(PATH_LENGTH);


      // get some checks in
      assertEquals("path not of correct length", PATH_LENGTH, brokenPath.size());
      assertEquals("doesn't finish at correct point", thePath._myPoints.lastElement(), brokenPath._myPoints.lastElement());

      showPath(thePath);
      showPath(brokenPath);
    }

    public void testBreakSections2()
    {
      final WorldPath thePath = new WorldPath(new WorldLocation[]{
   new WorldLocation.LocalLocation(new WorldDistance(0, WorldDistance.NM),
                                               new WorldDistance(0, WorldDistance.NM),0),
   new WorldLocation.LocalLocation(new WorldDistance(20, WorldDistance.NM),
                                               new WorldDistance(25, WorldDistance.NM),0),
   new WorldLocation.LocalLocation(new WorldDistance(40, WorldDistance.NM),
                                               new WorldDistance(30, WorldDistance.NM),0),
   new WorldLocation.LocalLocation(new WorldDistance(60, WorldDistance.NM),
                                               new WorldDistance(40, WorldDistance.NM),0)

      });
      final int PATH_LENGTH = 8;
      final WorldPath brokenPath = thePath.breakIntoStraightSections(PATH_LENGTH);


      // get some checks in
      assertEquals("path not of correct length", PATH_LENGTH, brokenPath.size());
      assertEquals("doesn't finish at correct point", thePath._myPoints.lastElement(), brokenPath._myPoints.lastElement());

      showPath(thePath);
      showPath(brokenPath);
    }

    private void showPath(final WorldPath path)
    {
      final Vector<WorldLocation> pts = path._myPoints;
      for (int i = 0; i < pts.size(); i++)
      {
        final WorldLocation location = (WorldLocation) pts.elementAt(i);
        System.out.println("" + location.getLong() + ", "+  location.getLat() + ", "+  location.getDepth());
      }
    }

  }


  public String toString()
  {
		return getPoints().size() + " Points";  	
  }
  /***************************************************************
   *  main method, to test this class
   ***************************************************************/


}