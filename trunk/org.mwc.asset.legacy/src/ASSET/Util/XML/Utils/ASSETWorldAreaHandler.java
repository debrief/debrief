package ASSET.Util.XML.Utils;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class ASSETWorldAreaHandler extends MWCXMLReader
{

  static final private String type = "WorldArea";
  static final private String TOP_LEFT = "TopLeft";
  static final private String BOTTOM_RIGHT = "BottomRight";

  private WorldLocation _topLeft;
  private WorldLocation _bottomRight;

  public ASSETWorldAreaHandler()
  {
    this(type);
  }

  public ASSETWorldAreaHandler(String type)
  {
    super(type);

    addHandler(new ASSETLocationHandler(TOP_LEFT)
    {
      public void setLocation(WorldLocation res)
      {
        _topLeft = res;
      }
    });
    addHandler(new ASSETLocationHandler(BOTTOM_RIGHT)
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
    exportThis(type, area, parent, doc);
  }

  public static void exportThis(String name, WorldArea area, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(name);

    // step through the list
    ASSETLocationHandler.exportLocation(area.getTopLeft(), TOP_LEFT, eLoc, doc);
    ASSETLocationHandler.exportLocation(area.getBottomRight(), BOTTOM_RIGHT, eLoc, doc);

    parent.appendChild(eLoc);
  }


}