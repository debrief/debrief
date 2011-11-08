package ASSET.Models.Movement;

import ASSET.Participants.DemandedStatus;
import ASSET.Util.SupportTesting;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 19-Aug-2003
 * Time: 14:52:26
 * To change this template use Options | File Templates.
 */
public class HighLevelDemandedStatus extends DemandedStatus
{

  /**********************************************************************
   * member variables
   *********************************************************************/

  /**
   * the path of points we want to follow
   */
  private WorldPath _myPath;

  /**
   * the current location we are heading for
   */
  private int _currentTarget = 0;

  /**
   * the visit strategy we employ
   */
  private WaypointVisitor _visitType;

  /**
   * location index to mark that path is complete
   */
  public static final int PATH_COMPLETE = -1;

  /**
   * whether to follow through points in reverse
   */
  private boolean _runInReverse = false;

  /**
   * an (optional) speed to travel at
   */
  private WorldSpeed _demSpeed = null;

  /**
   * *******************************************************************
   * constructors
   * *******************************************************************
   */
  public HighLevelDemandedStatus(final long id, final long time)
  {
    super(id, time);
  }

  public HighLevelDemandedStatus(final long id, final long time, HighLevelDemandedStatus original)
  {
    super(id, time);
    _myPath = original.getPath();
  }

  public HighLevelDemandedStatus(final long id, HighLevelDemandedStatus original)
  {
    super(id, original.getTime());
    _myPath = original.getPath();
  }


  /**
   * constructor to build this path
   *
   * @param currentTarget the current target to head for (or null if the first one)
   * @param myPath        the path of points to follow
   * @param visitType     the type of visiting process we use
   */
  public HighLevelDemandedStatus(final long id,
                                 final long time,
                                 int currentTarget,
                                 WorldPath myPath,
                                 WaypointVisitor visitType,
                                 WorldSpeed demSpeed)
  {
    this(id, time);
    this._currentTarget = currentTarget;
    this._myPath = myPath;
    this._visitType = visitType;
    this._demSpeed = demSpeed;
  }

  public WorldLocation getCurrentTarget()
  {
    final WorldLocation res;

    if (_currentTarget == PATH_COMPLETE)
    {
      res = null;
    }
    else
      res = _myPath.getLocationAt(_currentTarget);

    return res;
  }

  /**
   * retrieve the waypoint we head for after the current one.
   * (of particular relevance when doing a MakeWaypoint manoeuvre)
   *
   * @return not the current target, but the one after (or null)
   */
  public WorldLocation getNextTarget()
  {
    WorldLocation res = null;

    // have we finished?
    if (_currentTarget != PATH_COMPLETE)
    {
      if (_runInReverse)
      {
        if (_currentTarget != 0)
          res = _myPath.getLocationAt(_currentTarget - 1);
      }
      else
      {
        if (_currentTarget != _myPath.getPoints().size() - 1)
        {
          res = _myPath.getLocationAt(_currentTarget + 1);
        }
      }
    }

    return res;
  }

  /**
   * set the current target we are heading for (zero-indexed)
   *
   * @param currentTarget
   */
  public void setCurrentTargetIndex(int currentTarget)
  {
    this._currentTarget = currentTarget;
  }

  /**
   * the current target we are heading for (zero-indexed)
   *
   * @return
   */
  public int getCurrentTargetIndex()
  {
    return _currentTarget;
  }

  public void nextWaypointVisited()
  {
    if (_runInReverse)
    {
      _currentTarget--;

      // have we passed the end of our list of locaitgons?
      if (_currentTarget < 0)
      {
        // yes - mark path complete
        _currentTarget = PATH_COMPLETE;
      }

    }
    else
    {
      _currentTarget++;

      // have we passed the end of our list of locaitgons?
      if (_currentTarget >= _myPath.getPoints().size())
      {
        // yes - mark path complete
        _currentTarget = PATH_COMPLETE;
      }
    }

  }

  public WorldPath getPath()
  {
    return _myPath;
  }

  public boolean getRunInReverse()
  {
    return _runInReverse;
  }

  public void setRunInReverse(boolean runInReverse)
  {
    this._runInReverse = runInReverse;
  }

  public void setPath(WorldPath myPath)
  {
    this._myPath = myPath;
  }

  public WaypointVisitor getVisitType()
  {
    return _visitType;
  }

  public void setVisitType(WaypointVisitor visitType)
  {
    this._visitType = visitType;
  }

  public int size()
  {
    return _myPath.size();
  }

  public WorldSpeed getSpeed()
  {
    return _demSpeed;
  }

  public void setSpeed(WorldSpeed _demSpeed)
  {
    this._demSpeed = _demSpeed;
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class HighLevelDemStatTest extends SupportTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public HighLevelDemStatTest(final String val)
    {
      super(val);
    }

    public void testNextTarget()
    {
      WorldLocation wa = new WorldLocation(1, 0, 0);
      WorldLocation wb = new WorldLocation(2, 0, 0);
      WorldLocation wc = new WorldLocation(3, 0, 0);
      WorldLocation wd = new WorldLocation(4, 0, 0);
      WorldLocation we = new WorldLocation(5, 0, 0);
      WorldLocation wf = new WorldLocation(6, 0, 0);
      WorldLocation wg = new WorldLocation(7, 0, 0);

      WorldPath wp = new WorldPath();
      wp.addPoint(wa);
      wp.addPoint(wb);
      wp.addPoint(wc);
      wp.addPoint(wd);
      wp.addPoint(we);
      wp.addPoint(wf);
      wp.addPoint(wg);

      HighLevelDemandedStatus hl = new HighLevelDemandedStatus(12, 0, 2, wp, null, null);
      hl.setRunInReverse(false);

      WorldLocation res = hl.getCurrentTarget();
      assertEquals("returned current target", wc, res);

      res = hl.getNextTarget();
      assertEquals("returned next target", wd, res);

      // switch us into reverse
      hl.setRunInReverse(true);
      res = hl.getNextTarget();
      assertEquals("returned next target", wb, res);

      // and forwards again
      hl.setRunInReverse(false);

      // do the next waypoint
      hl.nextWaypointVisited();

      res = hl.getCurrentTarget();
      assertEquals("returned current target", wd, res);

      res = hl.getNextTarget();
      assertEquals("returned next target", we, res);

      // do the next few waypoint
      hl.nextWaypointVisited();
      hl.nextWaypointVisited();
      hl.nextWaypointVisited();

      res = hl.getCurrentTarget();
      assertEquals("returned current target", wg, res);

      res = hl.getNextTarget();
      assertEquals("returned next target", null, res);

      // and fall of fthe plot
      hl.nextWaypointVisited();

      res = hl.getCurrentTarget();
      assertEquals("returned current target", null, res);
      res = hl.getNextTarget();
      assertEquals("returned next target", null, res);

      // now go into reverse
      hl.setCurrentTargetIndex(3);
      hl.setRunInReverse(true);

      res = hl.getCurrentTarget();
      assertEquals("returned current target", wd, res);
      res = hl.getNextTarget();
      assertEquals("returned next target", wc, res);

      // do the next few waypoint
      hl.nextWaypointVisited();
      hl.nextWaypointVisited();
      hl.nextWaypointVisited();

      res = hl.getCurrentTarget();
      assertEquals("returned current target", wa, res);
      res = hl.getNextTarget();
      assertEquals("returned next target", null, res);

      // and fall of fthe plot
      hl.nextWaypointVisited();

      res = hl.getCurrentTarget();
      assertEquals("returned current target", null, res);
      res = hl.getNextTarget();
      assertEquals("returned next target", null, res);

    }
  }

}
