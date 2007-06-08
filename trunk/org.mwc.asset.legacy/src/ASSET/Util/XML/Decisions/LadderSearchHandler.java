package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.LadderSearch;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

abstract public class LadderSearchHandler extends CoreDecisionHandler
{


  private final static String type = "LadderSearch";
  private final static String MAX_NUM_LEGS = "MaxNumLegs";
  private final static String LADDER_AXIS = "LadderAxis";
  private final static String START_POINT = "StartPoint";
  private final static String TRACK_SPACING = "TrackSpacing";
  private final static String LEG_LENGTH = "LegLength";
  private final static String SEARCH_HEIGHT = "SearchHeight";
  private final static String SEARCH_SPEED = "SearchSpeed";


  private WorldLocation _origin;
  private double _ladderAxis;
  private Integer _numLegs = null;
  private WorldDistance _spacing;
  private WorldDistance _legLength;
  private WorldDistance _searchHeight;
  private WorldSpeed _searchSpeed;

  public LadderSearchHandler()
  {
    this(type);
  }

  private LadderSearchHandler(final String type)
  {
    super(type);

    addHandler(new ASSETLocationHandler(START_POINT)
    {
      public void setLocation(WorldLocation res)
      {
        _origin = res;
      }
    });

    addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(TRACK_SPACING)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _spacing = res;
      }
    });

    addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(LEG_LENGTH)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _legLength = res;
      }
    });

    addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(SEARCH_HEIGHT)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _searchHeight = res;
      }
    });

    addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler(SEARCH_SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _searchSpeed = res;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(LADDER_AXIS)
    {
      public void setValue(final String name, final double val)
      {
        _ladderAxis = val;
      }
    });

    addAttributeHandler(new HandleIntegerAttribute(MAX_NUM_LEGS)
    {
      public void setValue(String name, int value)
      {
        _numLegs = new Integer(value);
      }
    });

  }


  public final void elementClosed()
  {
    final LadderSearch ess = new LadderSearch(_ladderAxis, _numLegs,
                                              _origin, _spacing, _legLength, _searchHeight, _searchSpeed, null);

    super.setAttributes(ess);
    setModel(ess);

    _numLegs = null;
    _origin = null;
    _spacing = null;
    _legLength = null;
    _searchHeight = null;
    _searchSpeed = null;
  }

  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final LadderSearch bb = (LadderSearch) toExport;

    // first the parents
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    thisPart.setAttribute(LADDER_AXIS, writeThis(bb.getInitialTrack()));

    // do we know the number of legs?
    if (bb.getMaxLegs() != null)
      thisPart.setAttribute(MAX_NUM_LEGS, writeThis(bb.getMaxLegs().intValue()));

    // and now the objects
    ASSETLocationHandler.exportLocation(bb.getOrigin(), START_POINT, thisPart, doc);
    MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(TRACK_SPACING, bb.getTrackSpacing(), thisPart, doc);
    MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(LEG_LENGTH, bb.getLegLength(), thisPart, doc);

    if (bb.getSearchHeight() != null)
      MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(SEARCH_HEIGHT, bb.getSearchHeight(), thisPart, doc);

    if (bb.getSearchSpeed() != null)
      MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler.exportSpeed(SEARCH_SPEED, bb.getSearchSpeed(), thisPart, doc);


    parent.appendChild(thisPart);

  }
}