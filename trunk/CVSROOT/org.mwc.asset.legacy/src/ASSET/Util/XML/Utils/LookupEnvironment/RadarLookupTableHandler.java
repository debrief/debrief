package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 26-Oct-2004
 * Time: 09:24:08
 * To change this template use File | Settings | File Templates.
 */
abstract public class RadarLookupTableHandler extends MWCXMLReader
{
  public static final String TARGET_ASPECT_SET = "TargetAspectSet";
  private static final String TARGET_ASPECT_DATUM = "TargetAspectDatum";
  public static final String[] ASPECT_HEADINGS = {"DeadAhead", "Bow", "Beam", "Quarter", "Astern"};

  public static final String TARGET_SEA_STATE_SET = "TargetSeaStateSet";
  public static final String TARGET_SEA_STATE_DATUM = "TargetSeaStateDatum";
  public static final String[] SEA_STATE_HEADINGS = {"SeaState_0", "SeaState_1", "SeaState_2", "SeaState_3",
                                                     "SeaState_4", "SeaState_5", "SeaState_6", "SeaState_7",
                                                     "SeaState_8", "SeaState_9", "SeaState_10"};


  private LookupSensor.IntegerTargetTypeLookup _targetAspect;
  private LookupSensor.IntegerTargetTypeLookup _targetSeaState;
  private static final Double DEFAULT_ASPECT = new Double(1000);
  private static final Double DEFAULT_SEA_STATE = new Double(1);


  private String _myName = null;


  public RadarLookupTableHandler(final String myType)
  {
    super(myType);

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String value)
      {
        _myName = value;
      }
    });

    addHandler(new IntegerTargetTypeLookupHandler(TARGET_ASPECT_SET, TARGET_ASPECT_DATUM, ASPECT_HEADINGS)
    {
      public void setLookup(LookupSensor.IntegerTargetTypeLookup val)
      {
        _targetAspect = val;
      }
    });

    addHandler(new IntegerTargetTypeLookupHandler(TARGET_SEA_STATE_SET, TARGET_SEA_STATE_DATUM, SEA_STATE_HEADINGS)
    {
      public void setLookup(LookupSensor.IntegerTargetTypeLookup val)
      {
        _targetSeaState = val;
      }
    });
  }

  public void elementClosed()
  {
    RadarLookupSensor.RadarEnvironment res = new RadarLookupSensor.RadarEnvironment(_myName, _targetSeaState, _targetAspect);

    setRadarEnvironment(res);

    _targetSeaState = null;
    _targetAspect = null;
    _myName = null;
  }

  abstract public void setRadarEnvironment(RadarLookupSensor.RadarEnvironment env);
}
