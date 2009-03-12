package MWC.Utilities.ReaderWriter.XML.Util;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.xml.sax.*;

import MWC.Utilities.ReaderWriter.XML.*;


abstract public class LongLocationHandler extends MWCXMLReader
{

  private int _latDeg;
  private float _latMin;
  private float _latSec;
  private char _latHem;
  private int _longDeg;
  private float _longMin;
  private float _longSec;
  private char _longHem;
  private float _depth;

  public LongLocationHandler()
  {
    // inform our parent what type of class we are
    super("longLocation");
  }

  // this is one of ours, so get on with it!
  protected void handleOurselves(String name, Attributes attributes)
  {
    // initialise data
    _latMin = _latSec = _longMin = _longSec = _depth = 0.0f;
    _latDeg = _longDeg = 0;

    int len = attributes.getLength();
    for(int i=0; i<len;i++){

      String nm = attributes.getLocalName(i);
      String val = attributes.getValue(i);
      if(nm.equals("LatDeg"))
        _latDeg = Integer.valueOf(val).intValue();
      else if(nm.equals("LatMin"))
        _latMin = Float.valueOf(val).floatValue();
      else if(nm.equals("LatSec"))
        _latSec = Float.valueOf(val).floatValue();
      else if(nm.equals("LatHem"))
        _latHem = val.charAt(0);
      else if(nm.equals("LongDeg"))
        _longDeg = Integer.valueOf(val).intValue();
      else if(nm.equals("LongMin"))
        _longMin = Float.valueOf(val).floatValue();
      else if(nm.equals("LongSec"))
        _longSec = Float.valueOf(val).floatValue();
      else if(nm.equals("LongHem"))
        _longHem = val.charAt(0);
      else
        if(nm.equals("Depth"))
        _depth = Float.valueOf(val).floatValue();
    }

  }



  public void elementClosed()
  {
    if(_latSec == 0)
    {
      _latSec = (_latMin - (int)_latMin) * 60.0f;
    }
    if(_longSec == 0)
    {
      _longSec = (_longMin - (int)_longMin) * 60.0f;
    }


    MWC.GenericData.WorldLocation res =
      new MWC.GenericData.WorldLocation(_latDeg, (int)_latMin, (double)_latSec, _latHem,
                                        _longDeg, (int)_longMin, (double)_longSec, _longHem,
                                        _depth);
    setLocation(res);
  }

  abstract public void setLocation(MWC.GenericData.WorldLocation res);

}