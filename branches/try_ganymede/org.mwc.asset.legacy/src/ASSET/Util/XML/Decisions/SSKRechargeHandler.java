package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.SSKRecharge;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class SSKRechargeHandler extends CoreDecisionHandler
  {

  private final static String type = "SSKRecharge";

  private final static String MIN_LEVEL = "MinLevel";
  private final static String SAFE_LEVEL = "SafeLevel";
  private final static String SNORT_SPEED = "SnortSpeed";
  private final static String EVADE_THESE = "EvadeThese";

  double _minLevel;
  double _safeLevel;
  WorldSpeed _snortSpeed;
  ASSET.Models.Decision.TargetType _evadeThese;

  public SSKRechargeHandler()
  {
    super(type);

    addAttributeHandler(new HandleDoubleAttribute(MIN_LEVEL)
    {
      public void setValue(String name, final double val)
      {
        _minLevel = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(SAFE_LEVEL)
    {
      public void setValue(String name, final double val)
      {
        _safeLevel = val;
      }
    });
    addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler(EVADE_THESE)
    {
      public void setTargetType(final ASSET.Models.Decision.TargetType type)
      {
        _evadeThese = type;
      }
    });
    addHandler(new WorldSpeedHandler(SNORT_SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _snortSpeed = res;
      }
    });
  }

  public void elementClosed()
  {
    final SSKRecharge ev = new SSKRecharge();
    super.setAttributes(ev);
    ev.setMinLevel(_minLevel);
    ev.setSafeLevel(_safeLevel);
    ev.setSnortSpeed(_snortSpeed);
    ev.setTargetToEvade(_evadeThese);

    // finally output it
    setModel(ev);
  }

  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Decision.Tactical.SSKRecharge bb = (ASSET.Models.Decision.Tactical.SSKRecharge) toExport;

    // first output the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    thisPart.setAttribute(MIN_LEVEL, writeThis(bb.getMinLevel()));
    thisPart.setAttribute(SAFE_LEVEL, writeThis(bb.getSafeLevel()));

    ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetToEvade(), thisPart, doc);
    WorldSpeedHandler.exportSpeed(SNORT_SPEED, bb.getSnortSpeed(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}