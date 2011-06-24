package ASSET.Models.Vessels;

import ASSET.ScenarioType;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA
 */
public class SonarBuoyField extends CoreParticipant
{
	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the area covered by this buoy field
	 * 
	 */
	private WorldArea _myCoverage;

	private TimePeriod _myPeriod;

	/**
	 * normal constructor
	 * 
	 * @param coverage
	 * 
	 */
	public SonarBuoyField(final int id, WorldArea coverage)
	{
		// create the participant bits
		super(id, null, null, null);

		// store the coverage
		_myCoverage = coverage;

		// and set our location to be the centre of the buoyfield
		this.getStatus().setLocation(getCoverage().getCentre());
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	/**
	 * return what this participant is currently doing
	 * 
	 */
	public String getActivity()
	{
		return "Active";
	}

	@Override
	public void setStatus(Status val)
	{
		if (val != null)
		{
			super.setStatus(val);
		}
		else
		{
			super.setStatus(new Status(0, 0));
		}
	}

	/**
	 * the range calculation goes from our area, not just a single point
	 * 
	 */
	public WorldDistance rangeFrom(WorldLocation point)
	{
		double dist = 0;

		// right, measure the distance from the sides
		dist = getCoverage().rangeFromEdge(point);

		return new WorldDistance(dist, WorldDistance.DEGS);
	}

	public WorldArea getCoverage()
	{
		return _myCoverage;
	}

	@Override
	public void doDetection(long oldtime, long newTime, ScenarioType scenario)
	{
		// aaah, are we active?
		if (isActiveAt(newTime))
			super.doDetection(oldtime, newTime, scenario);
	}

	/** convenience funtion for if we're currently active
	 * 
	 * @param newTime current time
	 * @return yes/no
	 */
	public boolean isActiveAt(long newTime)
	{
		boolean res = true;
		if(_myPeriod != null)
			res = _myPeriod.contains(new HiResDate(newTime));
		return res;
	}

	@Override
	public void doMovement(long oldtime, long newTime, ScenarioType scenario)
	{
		super.doMovement(oldtime, newTime, scenario);

		// update our status
		this.getStatus().setTime(newTime);
	}

	public void setTimePeriod(TimePeriod period)
	{
		_myPeriod = period;
	}

	public TimePeriod getTimePeriod()
	{
		return _myPeriod;
	}

}
