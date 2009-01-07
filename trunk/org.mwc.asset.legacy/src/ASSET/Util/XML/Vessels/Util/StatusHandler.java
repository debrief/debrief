package ASSET.Util.XML.Vessels.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


import ASSET.Participants.Status;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class StatusHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String type = "Status";

  double _myCourse;
  WorldSpeed _mySpeed;
  double _myFuel;
  long _myTime = -1;
  int _myId;
  MWC.GenericData.WorldLocation _myLoc;

  public StatusHandler()
  {
    super(type);

    super.addAttributeHandler(new HandleAttribute("Course")
    {
      public void setValue(String name, final String val)
      {
        _myCourse = Double.parseDouble(val);
      }
    });

    addHandler(new WorldSpeedHandler()
    {
      public void setSpeed(WorldSpeed res)
      {
        _mySpeed = res;
      }
    });
    super.addAttributeHandler(new HandleAttribute("Fuel")
    {
      public void setValue(String name, final String val)
      {
        _myFuel = Double.parseDouble(val);
      }
    });
    super.addAttributeHandler(new HandleAttribute("Id")
    {
      public void setValue(String name, final String val)
      {
        _myId = Integer.parseInt(val);
      }
    });
    addAttributeHandler(new HandleDateTimeAttribute("Time")
    {
      public void setValue(String name, final long value)
      {
        _myTime = value;
      }
    });
    addHandler(new ASSETLocationHandler("Location")
    {
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _myLoc = res;
      }
    });

  }

  public void elementClosed()
  {
    // create the category
    final Status stat = new Status(_myId, _myTime);
    stat.setCourse(_myCourse);
    stat.setSpeed(_mySpeed);
    stat.setFuelLevel(_myFuel);
    stat.setLocation(_myLoc);

    setStatus(stat);

    // and reset
    _myTime = -1;
    _mySpeed = null;
    _myLoc = null;
  }

  abstract public void setStatus(ASSET.Participants.Status stat);

  static public void exportThis(final ASSET.Participants.Status toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {

    // create the element
    final org.w3c.dom.Element stat = doc.createElement(type);

    // set the attributes
    stat.setAttribute("Id", writeThis(toExport.getId()));
    stat.setAttribute("Course", writeThis(toExport.getCourse()));
    stat.setAttribute("Fuel", writeThis(toExport.getFuelLevel()));
    stat.setAttribute("Time", writeThisInXML(new java.util.Date(toExport.getTime())));
    ASSETLocationHandler.exportLocation(toExport.getLocation(), "Location", stat, doc);
    WorldSpeedHandler.exportSpeed(toExport.getSpeed(), stat, doc);


    // add to parent
    parent.appendChild(stat);

  }

}