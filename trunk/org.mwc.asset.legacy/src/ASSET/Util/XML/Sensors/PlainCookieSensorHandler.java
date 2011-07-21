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
  private final static String HAS_RANGE = "ProducesRange";

  private static final String DETECTION_RANGE = "DetectionRange";

  WorldDistance _detRange;
  boolean _produceRange = true;


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
    
    addAttributeHandler(new HandleBooleanAttribute(HAS_RANGE)
    {
      public void setValue(String name, final boolean val)
      {
      	_produceRange = val;
      }
    });
  }

  protected SensorType getSensor(int myId, String myName)
  {
    final ASSET.Models.Sensor.Cookie.PlainCookieSensor cookieS = new PlainCookieSensor(myId, _detRange);
    cookieS.setName(myName);
    
    cookieS.setProducesRange(_produceRange);


    return cookieS;
  }

  public void elementClosed()
  {
    super.elementClosed();
    
    // and now clear our data
    _detRange = null;
    _produceRange = true;
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