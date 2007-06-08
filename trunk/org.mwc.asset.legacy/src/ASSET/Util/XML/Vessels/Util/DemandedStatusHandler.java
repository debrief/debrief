package ASSET.Util.XML.Vessels.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Movement.SimpleDemandedStatus;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;


abstract public class DemandedStatusHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String type = "DemandedStatus";

  private double _myCourse;
  private WorldSpeed _mySpeed;
  private WorldDistance _myHeight;
  private long _myTime;
  private long _myId;

  public DemandedStatusHandler()
  {
    super(type);

    super.addAttributeHandler(new HandleAttribute("Course")
    {
      public void setValue(String name, final String val)
      {
        _myCourse = Double.parseDouble(val);
      }
    });

    addHandler(new WorldSpeedHandler("Speed")
    {
      public void setSpeed(WorldSpeed res)
      {
        _mySpeed = res;
      }
    });
    addHandler(new WorldDistanceHandler("Height")
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myHeight = res;
      }
    });

    addAttributeHandler(new HandleAttribute("Time")
    {
      public void setValue(String name, final String value)
      {
        try
        {
          // the DTD specified that a default time value is zero, trap it
          if (value.equals("0"))
            return;

          final java.util.Date dtg = getRNDateFormatter().parse(value);
          _myTime = dtg.getTime();
        }
        catch (java.text.ParseException e)
        {
          System.err.println("error occured trying to parse:" + value);
          e.printStackTrace();
        }
      }
    });
    super.addAttributeHandler(new HandleAttribute("Id")
    {
      public void setValue(String name, final String val)
      {
        _myId = Long.parseLong(val);
      }
    });
  }


  public void elementClosed()
  {
    // create the category
    final SimpleDemandedStatus stat = new SimpleDemandedStatus(_myId, _myTime);
    stat.setCourse(_myCourse);
    stat.setSpeed(_mySpeed);
    stat.setHeight(_myHeight);

    setDemandedStatus(stat);

    _myHeight = null;
    _mySpeed = null;
  }

  abstract public void setDemandedStatus(ASSET.Participants.DemandedStatus stat);

  static public void exportThis(final ASSET.Participants.DemandedStatus toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {

    SimpleDemandedStatus sds = (SimpleDemandedStatus) toExport;

    // check we've got a data value
    if (toExport != null)
    {
      // create the element
      final org.w3c.dom.Element stat = doc.createElement(type);

      // set the attributes
      stat.setAttribute("Course", writeThis(sds.getCourse()));
      stat.setAttribute("Id", writeThis(toExport.getId()));
      stat.setAttribute("Time", writeThis(new java.util.Date(toExport.getTime())));

      WorldSpeedHandler.exportSpeed("Speed", new WorldSpeed(sds.getSpeed(), WorldSpeed.M_sec), stat, doc);
      WorldDistanceHandler.exportDistance("Height",
                                          new WorldDistance(sds.getHeight(), WorldDistance.METRES), stat, doc);

      // add to parent
      parent.appendChild(stat);
    }

  }

}