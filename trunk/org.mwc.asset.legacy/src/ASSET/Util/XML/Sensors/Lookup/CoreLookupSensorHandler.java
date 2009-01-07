package ASSET.Util.XML.Sensors.Lookup;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.SensorType;
import ASSET.Util.XML.Sensors.CoreSensorHandler;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract class CoreLookupSensorHandler extends CoreSensorHandler
{

  /**
   * and the other values
   */
  private static final String VDR = "VDR";
  private static final String TBDO = "TBDO";
  private static final String MRF = "MRF";
  private static final String CRF = "CRF";
  private static final String CTP = "CTP";
  private static final String IRF = "IRF";
  private static final String ITP = "ITP";

  double _vdr;
  Duration _tbdo;
  double _mrf;
  double _crf;
  Duration _ctp;
  double _irf;
  Duration _itp;


  public CoreLookupSensorHandler(String myType)
  {
    super(myType);

    super.addAttributeHandler(new HandleDoubleAttribute(MRF)
    {
      public void setValue(String name, final double val)
      {
        _mrf = val;
      }
    });
    super.addAttributeHandler(new HandleDoubleAttribute(CRF)
    {
      public void setValue(String name, final double val)
      {
        _crf = val;
      }
    });
    super.addAttributeHandler(new HandleDoubleAttribute(IRF)
    {
      public void setValue(String name, final double val)
      {
        _irf = val;
      }
    });
    super.addAttributeHandler(new HandleDoubleAttribute(VDR)
    {
      public void setValue(String name, final double val)
      {
        _vdr = val;
      }
    });

    addHandler(new DurationHandler(CTP)
    {
      public void setDuration(Duration res)
      {
        _ctp = res;
      }
    });
    addHandler(new DurationHandler(ITP)
    {
      public void setDuration(Duration res)
      {
        _itp = res;
      }
    });

    addHandler(new DurationHandler(TBDO)
    {
      public void setDuration(Duration res)
      {
        _tbdo = res;
      }
    });


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
    LookupSensor theSensor = createLookupSensor(myId, myName, _vdr, _tbdo.getMillis(), _mrf,
                                                _crf, _ctp, _irf, _itp);
    return theSensor;
  }


  public void elementClosed()
  {
    // let the parent do it's stuff
    super.elementClosed();

    // and clear our data
    _ctp = null;
    _itp = null;
    _tbdo = null;

  }

  abstract protected LookupSensor createLookupSensor(int id,
                                                     String name,
                                                     double VDR1, long TBDO1, double MRF1, double CRF1, Duration CTP1,
                                                     double IRF1, Duration ITP1);


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    throw new RuntimeException("Export lookup sensor not implemented!");
  }
}