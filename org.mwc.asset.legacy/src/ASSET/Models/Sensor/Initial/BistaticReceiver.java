package ASSET.Models.Sensor.Initial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.BroadbandRadNoise;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Vessels.Buoy;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

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
	private final double THRESHOLD = 5; // db

	@Override
	public WorldDistance getEstimatedRange()
	{
		return null;
	}

	
	
	@Override
	protected boolean canProduceBearing()
	{
		return true;
	}



	@Override
	protected boolean canProduceRange()
	{
		return false;
	}



	@Override
	public int getMedium()
	{
		return EnvironmentType.NARROWBAND;
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
				
		// is this an active buoy?
		if(Category.Type.BUOY.equals(target.getCategory().getType()) && target.radiatesThisNoise(EnvironmentType.BROADBAND_ACTIVE))
		{
			// sort out the locations
			WorldLocation contactLoc = target.getStatus().getLocation();
			WorldLocation myLoc = host.getStatus().getLocation();
			
		  // use the environment to determine the loss
	    double legTwoLoss = environment.getLossBetween(EnvironmentType.BROADBAND_PASSIVE, contactLoc, myLoc);
	    
	    // ok, now the remaining value
	    RadiatedCharacteristics txChars = target.getRadiatedChars();
	    BroadbandRadNoise radNoise = (BroadbandRadNoise) txChars.getMedium(EnvironmentType.BROADBAND_ACTIVE);
	    double radLevel = radNoise.getBaseNoiseLevel();			    
	    float remainingNoise = (float) (radLevel - legTwoLoss);
	    
	    // is this sufficient?
	    if(remainingNoise > THRESHOLD)
	    {
	    	float bearing = (float) MWC.Algorithms.Conversions.Rads2Degs(contactLoc.subtract(myLoc).getBearing()); 
	    	res = new DetectionEvent(0, host.getId(), myLoc, this, null, 
	    			null, bearing, null, remainingNoise, target.getCategory(), null, null, target);
	    	
	    	// ok - this method can only return one detection.
	    	return res;
	    }			
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
				if(Category.Type.BUOY.equals(thisP.getCategory().getType()))
				{
					// is it active?
					if(thisP.radiatesThisNoise(EnvironmentType.BROADBAND_ACTIVE))
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
			    double legOneLoss = environment.getLossBetween(EnvironmentType.BROADBAND_PASSIVE, txLoc, contactLoc);
			    double legTwoLoss = environment.getLossBetween(EnvironmentType.BROADBAND_PASSIVE, contactLoc, myLoc);
			    
			    // ok, now the remaining value
			    RadiatedCharacteristics txChars = transmitter.getRadiatedChars();
			    BroadbandRadNoise radNoise = (BroadbandRadNoise) txChars.getMedium(EnvironmentType.BROADBAND_ACTIVE);
			    double radLevel = radNoise.getBaseNoiseLevel();			    
			    float remainingNoise = (float) (radLevel - legOneLoss - legTwoLoss);
			    
			    // is this sufficient?
			    if(remainingNoise > THRESHOLD)
			    {
			    	float bearing = (float) MWC.Algorithms.Conversions.Rads2Degs(contactLoc.subtract(myLoc).getBearing()); 
			    	res = new DetectionEvent(0, host.getId(), myLoc, this, null, 
			    			null, bearing, null, remainingNoise, target.getCategory(), null, null, target);
			    	
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
		// hey, we can probably detect anything that's not airborne
		return !Category.Environment.AIRBORNE.equals(other.getCategory().getEnvironment());
	}

	@Override
	public boolean canIdentifyTarget()
	{
		return true;
	}
	
  static public class BistaticTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public BistaticTest(final String name)
    {
      super(name);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      BistaticReceiver theDet = new BistaticReceiver(12);
      return theDet;
    }

    public void testBistatics()
    {
    	final CoreScenario scenario = new CoreScenario();
    	
      // reset the earth model
      WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      // now the objects
      WorldLocation l1 = new WorldLocation(0, 0, 0);
      WorldLocation l2 = l1.add(new WorldVector(0, MWC.Algorithms.Conversions.m2Degs(2000), 0));

      final Status theStat = new Status(12, 0);
      theStat.setLocation(l1);
      theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      ASSET.Models.Vessels.SSN ssn = new ASSET.Models.Vessels.SSN(14);
      Status otherStat = new Status(theStat);
      otherStat.setLocation(l2);
      otherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new
        ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
      Optic opticRadNoise = new Optic(2, new WorldDistance(2, WorldDistance.METRES));
      rc.add(EnvironmentType.VISUAL, opticRadNoise);
      ssn.setRadiatedChars(rc);
      ssn.setStatus(otherStat);

      EnvironmentType env = new SimpleEnvironment(1, 1, 1);

      // ok, generate the transmitter
      Buoy tx = new Buoy(2);
      tx.setName("TX");
      Status txStat = new Status(theStat);
      txStat.setLocation(txStat.getLocation().add(new WorldVector(3, 0.004, 0)));
      txStat.setSpeed(new WorldSpeed(0, WorldSpeed.Kts));
      ActiveBroadbandSensor noiseSource = new ActiveBroadbandSensor(33);
      ASSET.Models.Mediums.BroadbandRadNoise brn = new ASSET.Models.Mediums.BroadbandRadNoise(150);
      ASSET.Models.Vessels.Radiated.RadiatedCharacteristics txRadChars = new
          ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
      txRadChars.add(EnvironmentType.BROADBAND_ACTIVE, brn);
			tx.setRadiatedChars(txRadChars);
      tx.getSensorFit().add(noiseSource);
      tx.setStatus(txStat);
      
      Buoy rx = new Buoy(3);
      rx.setName("RX");
      Status rxStat = new Status(theStat);
      rxStat.setLocation(rxStat.getLocation().add(new WorldVector(1, 0.004, 0)));
      rxStat.setSpeed(new WorldSpeed(0, WorldSpeed.Kts));
      BistaticReceiver receiver = new BistaticReceiver(44);
      rx.getSensorFit().add(receiver);
      rx.setStatus(rxStat);
      
      scenario.addParticipant(ssn.getId(), ssn);
      scenario.addParticipant(tx.getId(), tx);
      scenario.addParticipant(rx.getId(), rx);
      
			DetectionList detections = new DetectionList();
			receiver.detects(env, detections , rx, scenario, 200);
			
			assertEquals("got some detections", 2, detections.size());
    }
  }

	@Override
	public void update(DemandedStatus myDemandedStatus, Status myStatus,
			long newTime)
	{
		// don't bother...
	}

}
