package ASSET.Models.Sensor.Cookie;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/** simple sensor that uses cookie cutter model for detections
 * 
 * @author ianmayo
 *
 */
public class PlainCookieSensor extends CoreSensor
{

	/** the detection range for this sensor
	 * 
	 */
	WorldDistance _detectionRange = null;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlainCookieSensor(int id, WorldDistance detRange)
	{
		super(id, 0, "Plain Cookie");
		
		_detectionRange = detRange;
	}

	public WorldDistance getDetectionRange()
	{
		return _detectionRange;
	}

	public void setDetectionRange(WorldDistance detectionRange)
	{
		_detectionRange = detectionRange;
	}

	@Override
	protected boolean canDetectThisType(ParticipantType ownship,
			ParticipantType other, EnvironmentType env)
	{
		return true;
	}

	@Override
	public boolean canIdentifyTarget()
	{
		return true;
	}

	@Override
	protected DetectionEvent detectThis(EnvironmentType environment,
			ParticipantType host, ParticipantType target, long time,
			ScenarioType scenario)
	{
		DetectionEvent res = null;
		
		// right, what's the distance?
		WorldVector sep = host.getStatus().getLocation().subtract(target.getStatus().getLocation());
		if(sep.getRange() < _detectionRange.getValueIn(WorldDistance.DEGS))
		{
			double brgDegs =MWC.Algorithms.Conversions.Rads2Degs(sep.getBearing());
			
			// cool, in contact. write it up.
			 res = new DetectionEvent(time,
           host.getId(),
           host.getStatus().getLocation(),
           this,
           new WorldDistance(sep.getRange(), WorldDistance.DEGS),
           new WorldDistance(sep.getRange(), WorldDistance.DEGS),
           new Float(brgDegs),
           new Float(super.relativeBearing(host.getStatus().getCourse(), brgDegs)), 
           new Float(1),
           target.getCategory(),
           new Float(target.getStatus().getSpeed().getValueIn(WorldSpeed.Kts)),
           new Float(target.getStatus().getCourse()),
           target);
		}
		
		return res;
	}

	@Override
	public WorldDistance getEstimatedRange()
	{
		return _detectionRange;
	}

	@Override
	public int getMedium()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(DemandedStatus myDemandedStatus, Status myStatus,
			long newTime)
	{
		// don't bother, nothing to do here
	}

	@Override
	public EditorType getInfo()
	{
		if(_myEditor == null)
			_myEditor = new PlainCookieInfo(this);
		return _myEditor;
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	@Override
	public String getVersion()
	{
    return "$Date: 2010-01-19 15:34:14 +0100  $";
	}
	

  ////////////////////////////////////////////////////
  // the editor object
  ////////////////////////////////////////////////////
  static public class PlainCookieInfo extends BaseSensorInfo
  {
    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public PlainCookieInfo(final PlainCookieSensor data)
    {
      super(data);
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("Name", "the name of this sensor"),
          prop("DetectionRange", "detection range of this sensor")
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class PlainCookieTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public PlainCookieTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new PlainCookieSensor(1, new WorldDistance(12,WorldDistance.DEGS));
    }

    public void testPlainSensor()
    {

      // reset the earth model
      WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      // ok, create the sensor
      PlainCookieSensor ps = new PlainCookieSensor(12, new WorldDistance(12, WorldDistance.DEGS));

      // now the objects
      WorldLocation l1 = new WorldLocation(0, 0, 0);
      WorldLocation l2 = l1.add(new WorldVector(0, 10, 0));
      WorldLocation l3 = l1.add(new WorldVector(0, 13, 0));

      ASSET.Models.Vessels.Surface su1 = new ASSET.Models.Vessels.Surface(12);
      Status theStat1 = new Status(12, 0);
      theStat1.setLocation(l1);
      theStat1.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      su1.setStatus(theStat1);
      SensorList sl = new SensorList();
      sl.add(ps);

      ASSET.Models.Vessels.Surface su2 = new ASSET.Models.Vessels.Surface(14);
      Status theStat2 = new Status(12, 0);
      theStat2.setLocation(l2);
      theStat2.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      su2.setStatus(theStat2);
      
      EnvironmentType env = new SimpleEnvironment(1, 1, 1);

      DetectionEvent de = ps.detectThis(env, su1, su2, 1000, null);
      
      assertNotNull("Made a detection", de);
      
      su2.getStatus().setLocation(l3);

      de = ps.detectThis(env, su1, su2, 1000, null);
      
      assertNull("Not made a detection", de);

    }
  }

}
