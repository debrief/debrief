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
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public  class OpticSensorHandler extends CoreSensorHandler
{

  private final static String type = "OpticSensor";
  private static final String MIN_HEIGHT = "MinHeight";

  WorldDistance _myMinHeight;


  public OpticSensorHandler()
  {
    super(type);


    addHandler(new WorldDistanceHandler(MIN_HEIGHT)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myMinHeight = res;
      }
    });
  }

  protected SensorType getSensor(int myId, String myName)
  {
    final ASSET.Models.Sensor.Initial.OpticSensor optic = new ASSET.Models.Sensor.Initial.OpticSensor(myId);
    optic.setName(myName);

    optic.setMinHeight(_myMinHeight);

    return optic;
  }

  public void elementClosed()
  {
    super.elementClosed();
    
    // and now clear our data
    _myMinHeight = null;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Sensor.Initial.OpticSensor bb = (ASSET.Models.Sensor.Initial.OpticSensor) toExport;

    WorldDistanceHandler.exportDistance(MIN_HEIGHT, bb.getMinHeight(), thisPart, doc);

    parent.appendChild(thisPart);

  }
}