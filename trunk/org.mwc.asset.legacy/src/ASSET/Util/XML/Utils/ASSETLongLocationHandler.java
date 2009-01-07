package ASSET.Util.XML.Utils;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;


abstract public class ASSETLongLocationHandler extends MWCXMLReader
{


  private final static String type = "longLocation";
  private final static String LAT_DEG = "LatDeg";
  private final static String LONG_DEG = "LongDeg";
  private final static String LAT_MIN = "LatMin";
  private final static String LONG_MIN = "LongMin";
  private final static String LAT_SEC = "LatSec";
  private final static String LONG_SEC = "LongSec";
  private final static String LAT_HEM = "LatHem";
  private final static String LONG_HEM = "LongHem";
  private final static String HEIGHT = "Height";

  int _latDeg;
  double _latMin;
  double _latSec;
  char _latHem;
  int _longDeg;
  double _longMin;
  double _longSec;
  char _longHem;

  WorldDistance _height;

  public ASSETLongLocationHandler()
  {
    // inform our parent what type of class we are
    super(type);

    addAttributeHandler(new HandleIntegerAttribute(LAT_DEG)
    {
      public void setValue(String name, int val)
      {
        _latDeg = val;
      }
    });
    addAttributeHandler(new HandleIntegerAttribute(LONG_DEG)
    {
      public void setValue(String name, int val)
      {
        _longDeg = val;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(LAT_MIN)
    {
      public void setValue(String name, double val)
      {
        _latMin = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(LONG_MIN)
    {
      public void setValue(String name, double val)
      {
        _longMin = val;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(LAT_SEC)
    {
      public void setValue(String name, double val)
      {
        _latSec = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(LONG_SEC)
    {
      public void setValue(String name, double val)
      {
        _longSec = val;
      }
    });


    addAttributeHandler(new HandleAttribute(LAT_HEM)
    {
      public void setValue(String name, String value)
      {
        _latHem = value.charAt(0);
      }
    });
    addAttributeHandler(new HandleAttribute(LONG_HEM)
    {
      public void setValue(String name, String value)
      {
        _longHem = value.charAt(0);
      }
    });

    addHandler(new WorldDistanceHandler(HEIGHT)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _height = res;
      }
    });

  }

  public void elementClosed()
  {
    if (_latSec == 0)
    {
      _latSec = (_latMin - (int) _latMin) * 60.0f;
    }
    if (_longSec == 0)
    {
      _longSec = (_longMin - (int) _longMin) * 60.0f;
    }


    WorldLocation res = null;

    if (_height != null)
    {
      res =
        new WorldLocation.LocalLocation(_latDeg, (int) _latMin, _latSec, _latHem,
                                        _longDeg, (int) _longMin, _longSec, _longHem,
                                        _height);
    }
    else
    {
      res = new WorldLocation.LocalLocation(_latDeg, (int) _latMin, _latSec, _latHem,
                                            _longDeg, (int) _longMin, _longSec, _longHem);
    }

    setLocation(res);

    // initialise data
    _latMin = _latSec = _longMin = _longSec = 0.0f;
    _latDeg = _longDeg = 0;
    _height = null;

  }

  abstract public void setLocation(MWC.GenericData.WorldLocation res);

}