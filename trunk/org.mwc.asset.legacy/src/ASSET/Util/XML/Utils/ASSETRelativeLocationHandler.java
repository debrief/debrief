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
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Class which is able to store a location expressed in relative terms
 */
abstract public class ASSETRelativeLocationHandler extends MWCXMLReader
{
  private final static String type = "relativeLocation";
  private final static String NORTH = "North";
  private final static String EAST = "East";
  private final static String HEIGHT = "Height";
  private WorldDistance _north;
  private WorldDistance _east;
  private WorldDistance _height;


  public ASSETRelativeLocationHandler()
  {
    this(type);
  }

  public ASSETRelativeLocationHandler(String theType)
  {
    // inform our parent what type of class we are
    super(theType);

    addHandler(new WorldDistanceHandler(NORTH)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _north = res;
      }
    });

    addHandler(new WorldDistanceHandler(EAST)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _east = res;
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
    WorldLocation res;

    if (_height != null)
      res = new WorldLocation.LocalLocation(_north, _east, _height);
    else
      res = new WorldLocation.LocalLocation(_north, _east, 0);

    setLocation(res);

    _height = null;
    _north = null;
    _east = null;
  }

  abstract public void setLocation(WorldLocation res);

  public static void exportLocation(WorldLocation loc, Element parent, Document doc)
  {
    Element eLoc = doc.createElement(type);
    eLoc.setAttribute(NORTH, writeThisLong(MWC.Algorithms.Conversions.Degs2m(loc.getLat())));
    eLoc.setAttribute(EAST, writeThisLong(MWC.Algorithms.Conversions.Degs2m(loc.getLong())));

    // now output the depth
    WorldDistance depth = new WorldDistance(-loc.getDepth(), WorldDistance.METRES);
    WorldDistanceHandler.exportDistance(HEIGHT, depth, eLoc, doc);

    parent.appendChild(eLoc);
  }

}