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

abstract public class WorldAreaHandler extends MWCXMLReader {

  static final private String _myType = "WorldArea";
  static final private String TOP_LEFT = "TopLeft";
  static final private String BOTTOM_RIGHT = "BottomRight";

  private WorldLocation _topLeft;
  private WorldLocation _bottomRight;

  public WorldAreaHandler() {
    super("_myType");

    addHandler(new LocationHandler(TOP_LEFT)
    {
      public void setLocation(WorldLocation res)
      {
        _topLeft = res;
      }
    });
    addHandler(new LocationHandler(BOTTOM_RIGHT)
    {
      public void setLocation(WorldLocation res)
      {
        _bottomRight = res;
      }
    });

  }

  public void elementClosed()
  {
    setArea(new WorldArea(_topLeft, _bottomRight));
    _topLeft = null;
    _bottomRight = null;
  }

  abstract public void setArea(WorldArea area);


  public static void exportThis(WorldArea area, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(_myType);

    // step through the list
    LocationHandler.exportLocation(area.getTopLeft(), TOP_LEFT, eLoc, doc);
    LocationHandler.exportLocation(area.getBottomRight(), BOTTOM_RIGHT, eLoc, doc);

    parent.appendChild(eLoc);
  }


}