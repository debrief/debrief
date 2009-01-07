package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 * Log:
 *   $Log: RectangleWanderHandler.java,v $
 *   Revision 1.2  2006/09/11 15:15:49  Ian.Mayo
 *   Tidy to better reflect schema
 *
 *   Revision 1.1  2006/08/08 14:22:48  Ian.Mayo
 *   Second import
 *
 *   Revision 1.1  2006/08/07 12:26:57  Ian.Mayo
 *   First versions
 *
 *   Revision 1.4  2004/10/21 10:05:54  Ian.Mayo
 *   If depth isn't provided, set the depth range of target area to be huge
 *
 *   Revision 1.3  2004/08/20 13:33:26  Ian.Mayo
 *   Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *   Revision 1.2  2004/08/17 14:54:45  Ian.Mayo
 *   Refactor to introduce parent handler class capable of storing name & isActive flag
 *
 *   Revision 1.1  2004/05/24 16:12:56  Ian.Mayo
 *   Commit updates from home
 *
 *   Revision 1.2  2004/03/04 22:32:21  ian
 *   Minor tidying
 *
 *   Revision 1.1.1.1  2004/03/04 20:30:59  ian
 *   no message
 *
 *   Revision 1.6  2003/09/19 13:38:47  Ian.Mayo
 *   Switch to Speed and Distance objects instead of just doubles
 *
 *   Revision 1.5  2003/09/18 10:36:21  Ian.Mayo
 *   Reflect new location stucture
 *
 *   Revision 1.4  2003/09/11 13:48:02  Ian.Mayo
 *   Reflect moving decision models
 *
 *   Revision 1.3  2003/09/04 13:30:45  Ian.Mayo
 *   Reflect new Wander getter/setters
 *
 */

import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Utils.ASSETWorldAreaHandler;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract class RectangleWanderHandler extends CoreDecisionHandler
{

  private final static String type = "RectangleWander";
  private final static String WANDER_SPEED = "Speed";
  private final static String WANDER_HEIGHT = "Height";
  private final static String WANDER_AREA = "Area";

  WorldArea _myArea;
  WorldSpeed _mySpeed = null;
  WorldDistance _myHeight = null;

  public RectangleWanderHandler()
  {
    super(type);


    addHandler(new WorldSpeedHandler(WANDER_SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _mySpeed = res;
      }
    });
    addHandler(new WorldDistanceHandler(WANDER_HEIGHT)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myHeight = res;
      }
    });

    addHandler(new ASSETWorldAreaHandler(WANDER_AREA)
    {
      public void setArea(WorldArea area)
      {
        _myArea = area;
      }
    });


  }


  public void elementClosed()
  {
    final RectangleWander wr = new RectangleWander(_myArea, null);

    // just do a bit of a fiddle here.
    // if the user hasn't specified height/depth data for the area to wander in, he/she doesn't mind about it
    if((_myArea.getTopLeft().getDepth() == 0) && (_myArea.getBottomRight().getDepth() == 0))
    {      
      // yes.  so set the depth range to be huge
      wr.getArea().getTopLeft().setDepth(100000);
      wr.getArea().getBottomRight().setDepth(-10000);
    }

    super.setAttributes(wr);

    if (_myHeight != null)
      wr.setHeight(_myHeight);
    if (_mySpeed != null)
      wr.setSpeed(_mySpeed);

    setModel(wr);

    _myHeight = null;
    _mySpeed = null;

  }

  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final RectangleWander bb = (RectangleWander) toExport;

    // output the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);


    ASSETWorldAreaHandler.exportThis(WANDER_AREA, bb.getArea(), thisPart, doc);
    if (bb.getSpeed() != null)
      WorldSpeedHandler.exportSpeed(WANDER_SPEED, bb.getSpeed(), thisPart, doc);
    if (bb.getHeight() != null)
      WorldDistanceHandler.exportDistance(WANDER_HEIGHT, bb.getHeight(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}