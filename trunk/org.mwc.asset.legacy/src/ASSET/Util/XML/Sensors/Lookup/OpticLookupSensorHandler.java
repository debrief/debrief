package ASSET.Util.XML.Sensors.Lookup;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import MWC.GenericData.Duration;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 03-Feb-2004
 * Time: 14:31:24
 * To change this template use Options | File Templates.
 */
abstract public class OpticLookupSensorHandler extends CoreLookupSensorHandler
{
  private final static String type = "OpticLookupSensor";

  public OpticLookupSensorHandler()
  {
    super(type);
  }

  protected LookupSensor createLookupSensor(int id,
                                            String name,
                                            double VDR, long TBDO, double MRF, double CRF, Duration CTP, double IRF,
                                            Duration ITP)
  {
    return new OpticLookupSensor(id, name, VDR, TBDO, MRF, CRF, CTP, IRF, ITP);
  }

}