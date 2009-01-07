package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.BearingTrail;
import ASSET.Models.Decision.TargetType;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class BearingTrailHandler extends CoreDecisionHandler
{

  private final static String type = "BearingTrail";
  private final static String ALLOWABLE_ERROR = "AllowableError";
  private final static String TRAIL_RANGE = "TrailRange";
  private final static String TRAIL_BEARING = "TrailBearing";

  TargetType _myTargetType;
  WorldDistance _myRange;
  double _myBearing;
  WorldDistance _myAllowableError;

  public BearingTrailHandler()
  {
    super(type);

    addAttributeHandler(new HandleDoubleAttribute(TRAIL_BEARING)
    {
      public void setValue(String name, final double val)
      {
        _myBearing = val;
      }
    });

    addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler()
    {
      public void setTargetType(final ASSET.Models.Decision.TargetType type)
      {
        _myTargetType = type;
      }
    });
    addHandler(new WorldDistanceHandler(TRAIL_RANGE)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myRange = res;
      }
    });
    addHandler(new WorldDistanceHandler(ALLOWABLE_ERROR)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myAllowableError = res;
      }
    });

  }


  public void elementClosed()
  {
    final BearingTrail tr = new BearingTrail(_myRange);
    tr.setTargetType(_myTargetType);
    tr.setAllowableError(_myAllowableError);
    tr.setTrailBearing(_myBearing);

    super.setAttributes(tr);

    setModel(tr);

  }


  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element element = doc.createElement(type);

    // get data item
    final ASSET.Models.Decision.Tactical.BearingTrail bb = (ASSET.Models.Decision.Tactical.BearingTrail) toExport;

    // parent attributes first
    CoreDecisionHandler.exportThis(bb, element, doc);

    // output it's attributes
    element.setAttribute(TRAIL_BEARING, writeThis(bb.getTrailBearing()));
    ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), element, doc);
    WorldDistanceHandler.exportDistance(TRAIL_RANGE, bb.getTrailRange(), element, doc);
    WorldDistanceHandler.exportDistance(ALLOWABLE_ERROR, bb.getTrailRange(), element, doc);

    parent.appendChild(element);

  }


}