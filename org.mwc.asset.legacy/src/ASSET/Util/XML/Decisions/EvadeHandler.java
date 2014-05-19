package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Movement.Evade;
import ASSET.Models.Decision.TargetType;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class EvadeHandler extends CoreDecisionHandler
{

  private final static String type = "Evade";

  private final static String HEIGHT = "FleeHeight";
  private final static String SPEED = "FleeSpeed";

  Duration _fleePeriod;
  WorldSpeed _fleeSpeed;
  WorldDistance _fleeHeight;
  TargetType _myTargetType;


  public EvadeHandler()
  {
    super(type);

    addHandler(new WorldDistanceHandler(HEIGHT)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _fleeHeight = res;
      }
    });

    addHandler(new WorldSpeedHandler(SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _fleeSpeed = res;
      }
    });

    addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler()
    {
      public void setTargetType(final ASSET.Models.Decision.TargetType type)
      {
        _myTargetType = type;
      }
    });
    addHandler(new DurationHandler()
    {
      public void setDuration(Duration res)
      {
        _fleePeriod = res;
      }
    });
  }


  public void elementClosed()
  {
    final Evade ev = new Evade(_fleePeriod, _fleeSpeed, _fleeHeight, _myTargetType);

    super.setAttributes(ev);

    // finally output it
    setModel(ev);

    _fleePeriod = null;
    _fleeSpeed = null;
    _fleeHeight = null;
    _myTargetType = null;
  }

  abstract public void setModel(ASSET.Models.DecisionType dec);


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Decision.Movement.Evade bb = (ASSET.Models.Decision.Movement.Evade) toExport;

    // first the parent
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    DurationHandler.exportDuration(bb.getFleePeriod(), thisPart, doc);
    ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);

    WorldSpeedHandler.exportSpeed(SPEED, bb.getFleeSpeed(), thisPart, doc);
    WorldDistanceHandler.exportDistance(HEIGHT, bb.getFleeHeight(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}