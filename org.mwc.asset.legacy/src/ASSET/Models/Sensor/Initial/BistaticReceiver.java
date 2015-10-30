package ASSET.Models.Sensor.Initial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Mediums.NarrowbandRadNoise;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class BistaticReceiver extends CoreSensor
{


	public BistaticReceiver(int id)
	{
		super(id, 0, "Bistatic Receiver");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** detection threshold
	 * 
	 */
	private static final double THRESHOLD = 50; // db

	@Override
	public WorldDistance getEstimatedRange()
	{
		return null;
	}

	@Override
	public int getMedium()
	{
		return EnvironmentType.NARROWBAND;
	}

	@Override
	public void update(DemandedStatus myDemandedStatus, Status myStatus,
			long newTime)
	{
	}

	@Override
	public boolean hasEditor()
	{
		return false;
	}

	@Override
	public EditorType getInfo()
	{
		return null;
	}

	@Override
	public String getVersion()
	{
		return "1.0";
	}
	
	@Override
	protected DetectionEvent detectThis(EnvironmentType environment,
			ParticipantType host, ParticipantType target, long time,
			ScenarioType scenario)
	{
		DetectionEvent res = null;
		
	  // just check this participant isn't a passive receiver		
		if(!target.radiatesThisNoise(EnvironmentType.NARROWBAND))
		{
			// ok, drop out
			return res;
		}
		
		// is this an active buoy?
		if(target.getCategory().getType().equals(Category.Type.BUOY))
		{
			// ok, provide the direct-path data
		}
		else
		{
			
			// ok, now see if we can find any active transmitters
			ArrayList<ParticipantType> transmitters = new ArrayList<ParticipantType>();

			// loop through the participants
			final Collection<ParticipantType> parts = scenario
					.getListOfVisibleParticipants();

			for (Iterator<ParticipantType> iterator = parts.iterator(); iterator
					.hasNext();)
			{
				ParticipantType thisP = (ParticipantType) iterator.next();
				
				// see if this is a buoy
				if(thisP.getCategory().getType().equals(Category.Type.BUOY))
				{
					// is it active?
					if(thisP.radiatesThisNoise(EnvironmentType.NARROWBAND))
					{
						// ok, remember it
						transmitters.add(thisP);
					}
				}
			}
			
			// did we find any?
			if(transmitters.size() > 0)
			{
				// ok, now consider the two way travel journey
				Iterator<ParticipantType> iter = transmitters.iterator();
				while (iter.hasNext())
				{
					ParticipantType transmitter = (ParticipantType) iter.next();
					
					// sort out the locations
					WorldLocation txLoc = transmitter.getStatus().getLocation();
					WorldLocation contactLoc = target.getStatus().getLocation();
					WorldLocation myLoc = host.getStatus().getLocation();
					
				  // use the environment to determine the loss
			    double legOneLoss = environment.getLossBetween(EnvironmentType.NARROWBAND, txLoc, contactLoc);
			    double legTwoLoss = environment.getLossBetween(EnvironmentType.NARROWBAND, contactLoc, myLoc);
			    
			    // ok, now the remaining value
			    RadiatedCharacteristics txChars = transmitter.getRadiatedChars();
			    NarrowbandRadNoise radNoise = (NarrowbandRadNoise) txChars.getMedium(EnvironmentType.NARROWBAND);
			    double radLevel = radNoise.getBaseNoiseLevel();			    
			    float remainingNoise = (float) (radLevel - legOneLoss - legTwoLoss);
			    
			    // is this sufficient?
			    if(remainingNoise > THRESHOLD)
			    {
			    	res = new DetectionEvent(0, host.getId(), myLoc, this, null, 
			    			null, null, null, remainingNoise, target.getCategory(), null, null, target);
			    	
			    	// ok - this method can only return one detection.
			    	return res;
			    }
				}
			}
		}
		
		return res;
	}

	@Override
	protected boolean canDetectThisType(NetworkParticipant ownship,
			ParticipantType other, EnvironmentType env)
	{
    return other.radiatesThisNoise(getMedium());
	}

	@Override
	public boolean canIdentifyTarget()
	{
		return true;
	}
	
	

}
