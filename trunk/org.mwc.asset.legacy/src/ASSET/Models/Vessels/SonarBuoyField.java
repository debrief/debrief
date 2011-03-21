package ASSET.Models.Vessels;

import ASSET.Participants.CoreParticipant;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA
 */
public class SonarBuoyField extends CoreParticipant {
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** the area covered by this buoy field
	 * 
	 */
	private WorldArea _myCoverage;

	/** normal constructor
	 * @param coverage 
   *
   */
  public SonarBuoyField(final int id, WorldArea coverage) {
    // create the participant bits
    super(id, null, null, null);
    
    // store the coverage
    _myCoverage = coverage;

  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  
  
  
  /** return what this participant is currently doing
   *
   */
  public String getActivity() {
    return "Active";
  }


  /** the range calculation goes from our area, not just a single point
   * 
   */
	public WorldDistance rangeFrom(WorldLocation point)
	{
		double dist = 0;
		
		// is this point inside us?
		if(getCoverage().contains(point))
		{
			// done - return zero distance
		}
		else
		{
			// right, measure the distance from the sides
			dist = getCoverage().rangeFromEdge(point);
		}
		
		return new WorldDistance(dist, WorldDistance.DEGS);
	}


	public WorldArea getCoverage()
	{
		return _myCoverage;
	}

}
