package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Movement.WaypointVisitor;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Utils.ASSETWorldPathHandler;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class TransitWaypointHandler extends CoreDecisionHandler
{


  private final static String type = "TransitWaypoint";
  private final static String LOOPING = "Looping";
  private final static String REVERSE = "Reverse";
  private final static String VISITOR = "Visitor";
  private final static String SPEED = "Speed";

  private boolean _looping;
  private boolean _reverse;
  private MWC.GenericData.WorldPath _myPath;
  private WorldSpeed _speed;
  private String _visitor = null;

  public TransitWaypointHandler()
  {
    this(type);
  }

  private TransitWaypointHandler(final String type)
  {
    super(type);

    final MWC.Utilities.ReaderWriter.XML.MWCXMLReader hand = new ASSETWorldPathHandler()
    {
      public void setPath(final MWC.GenericData.WorldPath path)
      {
        _myPath = path;
      }
    };

    addHandler(hand);
    addHandler(new WorldSpeedHandler(SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _speed = res;
      }
    });
    addAttributeHandler(new HandleAttribute(VISITOR)
    {
      public void setValue(final String name, final String val)
      {
        _visitor = val;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(LOOPING)
    {
      public void setValue(final String name, final boolean val)
      {
        _looping = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(REVERSE)
    {
      public void setValue(final String name, final boolean val)
      {
        _reverse = val;
      }
    });

  }


  public final void elementClosed()
  {
    final WaypointVisitor wv = WaypointVisitor.createVisitor(_visitor);

    final TransitWaypoint route = new TransitWaypoint(_myPath, _speed, _looping, wv);

    super.setAttributes(route);

    route.setReverse(_reverse);
    setModel(route);

    _myPath = null;
    _visitor = null;
    _speed = null;
    _reverse = false;
    _looping = false;
  }


  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final TransitWaypoint bb = (TransitWaypoint) toExport;

    // output the parent bits first
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes

    final WorldSpeed theSpeed = bb.getSpeed();
    if (theSpeed != null)
    {
      WorldSpeedHandler.exportSpeed(SPEED, bb.getSpeed(), thisPart, doc);
    }
    thisPart.setAttribute(VISITOR, bb.getVisitor().getType());
    thisPart.setAttribute(LOOPING, writeThis(bb.getLoop()));
    thisPart.setAttribute(REVERSE, writeThis(bb.getReverse()));

    ASSETWorldPathHandler.exportThis(bb.getDestinations(), thisPart, doc);

    parent.appendChild(thisPart);

  }
}