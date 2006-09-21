package ASSET.Util.XML.Sensors;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import ASSET.Models.Sensor.Lookup.MADLookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import ASSET.Models.SensorType;
import ASSET.Util.XML.Sensors.Lookup.MADLookupSensorHandler;
import ASSET.Util.XML.Sensors.Lookup.OpticLookupSensorHandler;
import ASSET.Util.XML.Sensors.Lookup.RadarLookupSensorHandler;
import org.w3c.dom.Element;


abstract  public class SensorFitHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private final static String type = "SensorFit";

  private ASSET.Models.Sensor.SensorList _myList;

  public SensorFitHandler()
  {
    // inform our parent what type of class we are
    super(type);

    addHandler(new ASSET.Util.XML.Sensors.OpticSensorHandler()
    {
      public void addSensor(final SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new ASSET.Util.XML.Sensors.BroadbandHandler()
    {
      public void addSensor(final SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new ASSET.Util.XML.Sensors.ActiveBroadbandHandler()
    {
      public void addSensor(final SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new ASSET.Util.XML.Sensors.DippingActiveBroadbandHandler()
    {
      public void addSensor(final SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new ASSET.Util.XML.Sensors.NarrowbandHandler()
    {
      public void addSensor(final SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new ASSET.Util.XML.Sensors.ActiveInterceptHandler()
    {
      public void addSensor(final SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new OpticLookupSensorHandler()
    {
      public void addSensor(SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new RadarLookupSensorHandler()
    {
      public void addSensor(SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
    addHandler(new MADLookupSensorHandler()
    {
      public void addSensor(SensorType sensor)
      {
        addThisSensor(sensor);
      }
    });
  }


  public void elementClosed()
  {
    setSensorFit(_myList);
    _myList = null;
  }

  private void addThisSensor(final SensorType sensor)
  {
    if (_myList == null)
      _myList = new ASSET.Models.Sensor.SensorList();

    _myList.add(sensor);
  }

  abstract public void setSensorFit(ASSET.Models.Sensor.SensorList list);


  public static void exportThis(final ASSET.ParticipantType participant, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final Element sens = doc.createElement(type);

    // step through data
    final int len = participant.getNumSensors();
    for (int i = 0; i < len; i++)
    {
      final ASSET.Models.SensorType sensor = participant.getSensorAt(i);

      // see what type it is
      if (sensor instanceof ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor)
      {
        DippingActiveBroadbandHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof ASSET.Models.Sensor.Initial.ActiveBroadbandSensor)
      {
        ActiveBroadbandHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof ASSET.Models.Sensor.Initial.BroadbandSensor)
      {
        BroadbandHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof ASSET.Models.Sensor.Initial.OpticSensor)
      {
        OpticSensorHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof ASSET.Models.Sensor.Initial.NarrowbandSensor)
      {
        NarrowbandHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof ASSET.Models.Sensor.Initial.ActiveInterceptSensor)
      {
        ActiveInterceptHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof OpticLookupSensor)
      {
        OpticLookupSensorHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof MADLookupSensor)
      {
        MADLookupSensorHandler.exportThis(sensor, sens, doc);
      }
      if (sensor instanceof RadarLookupSensor)
      {
        RadarLookupSensorHandler.exportThis(sensor, sens, doc);
      }
    }

    parent.appendChild(sens);

  }


}