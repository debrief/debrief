package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 * Log:
 *   $Log: WanderHandler.java,v $
 *   Revision 1.2  2006/09/14 14:09:47  Ian.Mayo
 *   Lots of tidying
 *
 *   Revision 1.1  2006/08/08 14:22:51  Ian.Mayo
 *   Second import
 *
 *   Revision 1.1  2006/08/07 12:27:00  Ian.Mayo
 *   First versions
 *
 *   Revision 1.9  2004/08/20 13:33:34  Ian.Mayo
 *   Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *   Revision 1.8  2004/08/17 14:54:52  Ian.Mayo
 *   Refactor to introduce parent handler class capable of storing name & isActive flag
 *
 *   Revision 1.7  2004/05/24 16:12:58  Ian.Mayo
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

import ASSET.Models.Decision.Movement.Wander;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract class WanderHandler extends CoreDecisionHandler
{

  private final static String type = "Wander";
  private final static String WANDER_SPEED = "Speed";
  private final static String WANDER_HEIGHT = "Height";
  private final static String WANDER_RANGE = "Range";
  private final static String LOCATION_NAME = "Location";

  WorldLocation _myLocation;
  WorldDistance _myRange;
  WorldSpeed _mySpeed = null;
  WorldDistance _myHeight = null;

  public WanderHandler()
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

    addHandler(new ASSETLocationHandler(LOCATION_NAME)
    {
      public void setLocation(final WorldLocation res)
      {
        _myLocation = res;
      }
    });
    addHandler(new WorldDistanceHandler(WANDER_RANGE)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myRange = res;
      }
    });


  }


  public void elementClosed()
  {
    final Wander wr = new Wander(null);

    super.setAttributes(wr);

    wr.setOrigin(_myLocation);

    wr.setRange(_myRange);
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
    final Wander bb = (Wander) toExport;

    // first output the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);


    WorldDistanceHandler.exportDistance(WANDER_RANGE, bb.getRange(), thisPart, doc);
    ASSETLocationHandler.exportLocation(bb.getOrigin(), LOCATION_NAME, thisPart, doc);
    if (bb.getSpeed() != null)
      WorldSpeedHandler.exportSpeed(WANDER_SPEED, bb.getSpeed(), thisPart, doc);
    if (bb.getHeight() != null)
      WorldDistanceHandler.exportDistance(WANDER_HEIGHT, bb.getHeight(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}