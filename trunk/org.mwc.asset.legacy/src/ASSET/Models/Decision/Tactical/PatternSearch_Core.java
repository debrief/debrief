package ASSET.Models.Decision.Tactical;

import java.awt.Point;
import java.util.Iterator;

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.OnTopWaypoint;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

public abstract class PatternSearch_Core extends CoreDecision implements
		DecisionType.Paintable
{

	/**
	 * whether we've finished yet or not.
	 */
	protected boolean _finished = false;
	/**
	 * the route of points to pass through
	 */
	protected TransitWaypoint _myRoute;
	/**
	 * the (optional) height to conduct search at
	 */
	protected WorldDistance _searchHeight;
	/**
	 * the (optional) speed to conduct search at
	 */
	protected WorldSpeed _searchSpeed;

	/**
	 * store the supplied point in our route, assigning the search height if we
	 * have one
	 * 
	 * @param searchHeight
	 *          the (optional) height to search at
	 * @param currentLocation
	 *          the location to store
	 * @param route
	 *          the route containing the points
	 */
	protected static void addPointToRoute(final WorldDistance searchHeight,
			WorldLocation currentLocation, final WorldPath route)
	{
		// insert the start datum
		// do we have a height?
		if (searchHeight != null)
		{
			// yes, store it.
			currentLocation.setDepth(-searchHeight.getValueIn(WorldDistance.METRES));
		}
		route.addPoint(currentLocation);
	}

	/**
	 * decide the course of action to take, or return null to no be used
	 * 
	 * @param status
	 *          the current status of the participant
	 * @param chars
	 *          the movement chars for this participant
	 * @param demStatus
	 *          the current demanded status
	 * @param detections
	 *          the current list of detections for this participant
	 * @param monitor
	 *          the object which handles weapons release/detonation
	 * @param newTime
	 *          the time this decision is to be made
	 */
	public final DemandedStatus decide(final Status status,
			MovementCharacteristics chars, final DemandedStatus demStatus,
			final DetectionList detections, final ScenarioActivityMonitor monitor,
			final long newTime)
	{
		HighLevelDemandedStatus res = null;

		String myActivity = null;

		// have we finished yet?
		if (!_finished)
		{
			// no, have we defined our route yet?
			if (_myRoute == null)
			{
				// ok, do we have an origin?
				if (getOrigin() == null)
				{
					// ok, initialise with our current location
					setOrigin(status.getLocation());
				}

				// create the path to follow
				final WorldPath route = createSearchRoute(status.getLocation());

				// and put it into a route
				_myRoute = new TransitWaypoint(route, null, false, new OnTopWaypoint());
				myActivity = "Generating new route";
			}

			// so, we now have our route. Has it finished yet?
			if (_myRoute.isFinished())
			{
				// done
				res = null;

				// and reset our list of points
				_myRoute.getDestinations().getPoints().clear();
				_myRoute = null;

				// remember the fact that we've now finished
				_finished = true;

				myActivity = null;

			}
			else
			{
				// ok - still going, ask the transitter what it wants to do
				res = (HighLevelDemandedStatus) _myRoute.decide(status, chars,
						demStatus, detections, monitor, newTime);

				// do we have an intended path?
				if (res != null)
				{

					// aah, do we have a search speed?
					if (getSearchSpeed() != null)
					{
						res.setSpeed(getSearchSpeed());
					}
				}

				// just double-check if we have now finished.
				if (res == null)
				{
					myActivity = _myRoute.getActivity();
					_finished = true;
				}
				else
				{
					myActivity = getDescriptor() + ":";

					// have we been interrupted?
					if (_myRoute.getVisitor().hasBeenInterrupted())
					{
						myActivity += " Resuming from interruption";
					}
					else
					{
						myActivity += "Heading for point:"
								+ (_myRoute.getCurrentDestinationIndex());

					}
				}
			}
		}

		this.setLastActivity(myActivity);

		return res;
	}

	protected abstract String getDescriptor();

	protected abstract WorldPath createSearchRoute(WorldLocation currentLocation);

	protected EditorType _myEditor;
	/**
	 * the start point for the search
	 */
	protected WorldLocation _myOrigin;
	/**
	 * the track spacing
	 */
	protected WorldDistance _trackSpacing;
	/**
	 * the dimensions of the area
	 */
	protected WorldDistance _width;
	protected WorldDistance _height;

	/**
	 * reset this decision model
	 */
	public final void restart()
	{

		// forget that we were in a cycle
		_finished = false;

		// and clear the route
		_myRoute = null;
	}

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 * 
	 * @param currentStatus
	 */
	public void interrupted(Status currentStatus)
	{
		// hey, we may be resuming this behaviour from another location.
		// We need to forget the current path to the next target so that it gets
		// recalculated
		_myRoute.getVisitor().routeInterrupted(currentStatus);
	}

	public final WorldLocation getOrigin()
	{
		return _myOrigin;
	}

	/**
	 * set the start location for this manoeuvre (or supply null to use the
	 * platform location when tactic first called)
	 * 
	 * @param _myOrigin
	 */
	public final void setOrigin(final WorldLocation _myOrigin)
	{
		this._myOrigin = _myOrigin;
	}

	public final boolean getFinished()
	{
		return _finished;
	}

	/**
	 * return the (optional) height to search at
	 * 
	 * @return the height.
	 */
	public WorldDistance getSearchHeight()
	{
		return _searchHeight;
	}

	/**
	 * set the (optional) height to search at
	 * 
	 * @param searchHeight
	 *          search height
	 */
	public void setSearchHeight(WorldDistance searchHeight)
	{
		_searchHeight = searchHeight;
	}

	/**
	 * the (optional) search speed
	 * 
	 * @return
	 */
	public WorldSpeed getSearchSpeed()
	{
		return _searchSpeed;
	}

	/**
	 * the (optional) search speed
	 * 
	 * @param searchSpeed
	 */
	public void setSearchSpeed(WorldSpeed searchSpeed)
	{
		this._searchSpeed = searchSpeed;
	}

	/**
	 * retrieve the points we intend to travel through
	 * 
	 * @return
	 */
	public final WorldPath getRoute()
	{
		WorldPath res = null;
		if (_myRoute != null)
			res = _myRoute.getDestinations();
		return res;
	}

	/**
	 * set the points we intend to travel through
	 * 
	 * @param thePath
	 *          the path to follow
	 */
	public final void setRoute(WorldPath thePath)
	{
		_myRoute.setDestinations(thePath);
	}

	public PatternSearch_Core(String name, WorldLocation myOrigin,
			WorldDistance searchHeight, WorldSpeed searchSpeed,
			WorldDistance trackSpacing, WorldDistance height, WorldDistance width)
	{
		super(name);
		_myOrigin = myOrigin;
		_searchHeight = searchHeight;
		_searchSpeed = searchSpeed;
		_trackSpacing = trackSpacing;
		_height = height;
		_width = width;
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	abstract public EditorType getInfo();

	/**
	 * get the version details for this model.
	 * 
	 * <pre>
	 * $Log: LadderSearch.java,v $
	 * Revision 1.2  2006/09/11 09:36:04  Ian.Mayo
	 * Fix dodgy accessor
	 * 
	 * Revision 1.1  2006/08/08 14:21:34  Ian.Mayo
	 * Second import
	 * 
	 * Revision 1.1  2006/08/07 12:25:43  Ian.Mayo
	 * First versions
	 * 
	 * Revision 1.16  2004/11/25 14:29:29  Ian.Mayo
	 * Handle switch to hi-res dates
	 * 
	 * Revision 1.15  2004/11/03 09:54:50  Ian.Mayo
	 * Allow search speed to be set
	 * <p/>
	 * Revision 1.14  2004/11/01 14:31:44  Ian.Mayo
	 * Do more elaborate processing when creating sample instance for property testing
	 * <p/>
	 * Revision 1.13  2004/11/01 10:47:47  Ian.Mayo
	 * Provide accessor for series of generated points
	 * <p/>
	 * Revision 1.12  2004/08/31 09:36:22  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.11  2004/08/26 16:27:03  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.10  2004/08/25 11:20:37  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.9  2004/08/24 10:36:25  Ian.Mayo
	 * Do correct activity management bits (using parent)
	 * <p/>
	 * Revision 1.8  2004/08/20 13:32:29  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.7  2004/08/17 14:22:06  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.6  2004/08/09 15:50:31  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.5  2004/08/06 12:52:03  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.4  2004/08/06 11:14:25  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.3  2004/08/05 14:54:11  Ian.Mayo
	 * Provide accessor to get series of waypoints
	 * <p/>
	 * Revision 1.2  2004/08/04 10:33:13  Ian.Mayo
	 * Add optional search height, use it
	 * <p/>
	 * Revision 1.1  2004/05/24 15:56:29  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.4  2004/04/22 21:37:38  ian
	 * Tidying
	 * <p/>
	 * Revision 1.3  2004/04/13 20:53:45  ian
	 * Implement restart functionality
	 * <p/>
	 * Revision 1.2  2004/03/25 21:06:14  ian
	 * Correct placing of first point, correct tests
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:52  ian
	 * no message
	 * <p/>
	 * <p/>
	 * </pre>
	 */
	public String getVersion()
	{
		return "$Date: 2010-10-28 11:43:11 +0100 (Thu, 28 Oct 2010) $";
	}

	public WorldDistance getWidth()
	{
		return _width;
	}

	public void setWidth(WorldDistance width)
	{
		this._width = width;
	}

	public WorldDistance getHeight()
	{
		return _height;
	}

	public void setHeight(WorldDistance height)
	{
		this._height = height;
	}

	public final WorldDistance getTrackSpacing()
	{
		return _trackSpacing;
	}

	public final void setTrackSpacing(final WorldDistance _trackSpacing)
	{
		this._trackSpacing = _trackSpacing;
	}

	@Override
	public void paint(CanvasType dest)
	{
		// ok, do we have a route?
		if (_myRoute != null)
		{
			// does it have any points?
			if (_myRoute.getDestinations().getPoints().size() > 0)
			{
				// ok, make it a feint line
				dest.setLineStyle(CanvasType.SHORT_DASHES);

				// ok, go for it
				Iterator<WorldLocation> pts = _myRoute.getDestinations().getPoints()
						.iterator();
				Point lastP = null;
				while (pts.hasNext())
				{
					WorldLocation thisP = pts.next();
					Point pt = dest.toScreen(thisP);

					if (lastP != null)
					{
						dest.drawLine(lastP.x, lastP.y, pt.x, pt.y);
					}

					// is this the current target?
					boolean painted = false;
					int index = _myRoute.getCurrentDestinationIndex();
					if (index != HighLevelDemandedStatus.PATH_COMPLETE)
					{
						WorldLocation tgt = _myRoute.getDestinations().getLocationAt(index);
						if (tgt.equals(thisP))
						{
							// shade it in solid
							dest.fillRect(pt.x - 3, pt.y - 3, 7, 7);
							painted = true;
						}
					}
					if (!painted)
					{
						dest.drawRect(pt.x - 3, pt.y - 3, 7, 7);
					}
					lastP = new Point(pt);
				}
			}
		}
	}

}