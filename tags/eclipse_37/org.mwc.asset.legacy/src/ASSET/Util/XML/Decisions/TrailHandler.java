package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Decision.TargetType;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class TrailHandler extends CoreDecisionHandler
  {

  private final static String type = "Trail";
  private final static String ALLOWABLE_ERROR = "AllowableError";
  private final static String ALLOWABLE_SPEED = "AllowSpeedChange";
  private final static String RANGE = "TrailRange";
  private final static String TRAIL_HEIGHT = "Height";


  TargetType _myTargetType;
  WorldDistance _myRange;
  WorldDistance _myAllowableError;
  WorldDistance _trailHeight = null;
  Boolean _myAllowSpeedChange;

  public TrailHandler()
  {
    super(type);

    addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler()
    {
      public void setTargetType(final ASSET.Models.Decision.TargetType type)
      {
        _myTargetType = type;
      }
    });
    addHandler(new WorldDistanceHandler(RANGE)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myRange = res;
      }
    });
    addHandler(new WorldDistanceHandler(TRAIL_HEIGHT)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _trailHeight = res;
      }
    });
    addHandler(new WorldDistanceHandler(ALLOWABLE_ERROR)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myAllowableError = res;
      }
    });
    
    addAttributeHandler(new HandleBooleanAttribute(ALLOWABLE_SPEED)
		{			
			@Override
			public void setValue(String name, boolean value)
			{
				_myAllowSpeedChange = new Boolean(value);
			}
		});

  }


  public void elementClosed()
  {
    final Trail tr = new Trail(_myRange);
    tr.setTargetType(_myTargetType);
    tr.setAllowableError(_myAllowableError);

    if(_myAllowSpeedChange != null)
    	tr.setAllowSpeedChange(_myAllowSpeedChange);
    
    super.setAttributes(tr);

    if (_trailHeight != null)
      tr.setTrailHeight(_trailHeight);

    setModel(tr);

    _myRange = null;
    _trailHeight = null;
    _myTargetType = null;
    _myAllowableError = null;
    _myAllowSpeedChange = null;

  }


  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Decision.Movement.Trail bb = (ASSET.Models.Decision.Movement.Trail) toExport;

    // first the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    if (bb.getTrailHeight() != null)
    {
      WorldDistanceHandler.exportDistance(TRAIL_HEIGHT, bb.getTrailHeight(), thisPart, doc);
    }
    ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);
    WorldDistanceHandler.exportDistance(RANGE, bb.getTrailRange(), thisPart, doc);
    WorldDistanceHandler.exportDistance(ALLOWABLE_ERROR, bb.getAllowableError(), thisPart, doc);

    thisPart.setAttribute(ALLOWABLE_SPEED, "" + bb.isAllowSpeedChange());
    

    parent.appendChild(thisPart);

  }


}