package ASSET.Util.XML.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.SensorType;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

public abstract class NarrowbandHandler extends CoreSensorHandler
{

  private final static String type = "NarrowbandSensor";
  protected final static String STEADY_TIME = "SteadyTime";

  protected Duration _mySteadyTime;

  public NarrowbandHandler(String myType)
  {
    super(myType);

    super.addHandler(new DurationHandler(STEADY_TIME)
    {
      public void setDuration(Duration res)
      {
        _mySteadyTime = res;
      }
    });
  }

  public NarrowbandHandler()
  {
    this(type);
  }


  /**
   * method for child class to instantiate sensor
   *
   * @param myId
   * @param myName
   * @return the new sensor
   */
  protected SensorType getSensor(int myId, String myName)
  {
    // get this instance
    final ASSET.Models.Sensor.Initial.NarrowbandSensor bb = new ASSET.Models.Sensor.Initial.NarrowbandSensor(myId);

    bb.setSteadyTime(_mySteadyTime);

    return bb;
  }


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Sensor.Initial.NarrowbandSensor bb = (ASSET.Models.Sensor.Initial.NarrowbandSensor) toExport;
    CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

    DurationHandler.exportDuration(STEADY_TIME, bb.getSteadyTime(), thisPart, doc);

    parent.appendChild(thisPart);

  }
}