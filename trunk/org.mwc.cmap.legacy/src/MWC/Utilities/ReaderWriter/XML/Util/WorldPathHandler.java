package MWC.Utilities.ReaderWriter.XML.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import java.util.*;

abstract public class WorldPathHandler extends MWCXMLReader {

  static final private String _myType = "WorldPath";
  static final private String POINT = "Point";

  private WorldPath _myPath;

  public WorldPathHandler() {
    super("WorldPath");

    addHandler(new LocationHandler(POINT)
    {
      public void setLocation(WorldLocation res)
      {
        addThis(res);
      }
    });

  }

  public void addThis(WorldLocation res)
  {
    if(_myPath == null)
      _myPath = new WorldPath();

    _myPath.addPoint(res);
  }

  public void elementClosed()
  {
    setPath(_myPath);
    _myPath = null;
  }

  abstract public void setPath(WorldPath path);


  public static void exportThis(WorldPath path, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(_myType);

    // step through the list
    Iterator it = path.getPoints().iterator();

    while(it.hasNext())
    {
      WorldLocation wl = (WorldLocation)it.next();
      LocationHandler.exportLocation(wl, POINT, eLoc, doc);
    }


    parent.appendChild(eLoc);
  }


}