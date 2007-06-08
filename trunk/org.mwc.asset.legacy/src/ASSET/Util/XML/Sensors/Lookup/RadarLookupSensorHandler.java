package ASSET.Util.XML.Sensors.Lookup;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import MWC.GenericData.Duration;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 03-Feb-2004
 * Time: 14:31:24
 * To change this template use Options | File Templates.
 */
abstract public class RadarLookupSensorHandler extends CoreLookupSensorHandler
{
  private final static String type = "RadarLookupSensor";
  private final static String K = "K";

  double _k;


  public RadarLookupSensorHandler()
  {
    super(type);

    super.addAttributeHandler(new HandleDoubleAttribute(K)
    {
      public void setValue(String name, final double val)
      {
        _k = val;
      }
    });
  }

  protected LookupSensor createLookupSensor(int id,
                                            String name,
                                            double VDR, long TBDO, double MRF, double CRF, Duration CTP, double IRF,
                                            Duration ITP)
  {
    return new RadarLookupSensor(id, name, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, _k);
  }

}