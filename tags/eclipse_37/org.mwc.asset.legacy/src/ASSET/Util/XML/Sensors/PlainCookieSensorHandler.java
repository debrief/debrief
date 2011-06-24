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
import ASSET.Models.Sensor.Cookie.PlainCookieSensor;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public  class PlainCookieSensorHandler extends CoreSensorHandler
{

  private final static String type = "PlainCookieSensor";
  private static final String DETECTION_RANGE = "DetectionRange";

  WorldDistance _detRange;


  public PlainCookieSensorHandler()
  {
    super(type);


    addHandler(new WorldDistanceHandler(DETECTION_RANGE)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _detRange = res;
      }
    });
  }

  protected SensorType getSensor(int myId, String myName)
  {
    final ASSET.Models.Sensor.Cookie.PlainCookieSensor optic = new PlainCookieSensor(myId, _detRange);
    optic.setName(myName);

    return optic;
  }

  public void elementClosed()
  {
    super.elementClosed();
    
    // and now clear our data
    _detRange = null;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final PlainCookieSensor bb = (PlainCookieSensor) toExport;

    WorldDistanceHandler.exportDistance(DETECTION_RANGE, bb.getDetectionRange(), thisPart, doc);

    parent.appendChild(thisPart);

  }
}