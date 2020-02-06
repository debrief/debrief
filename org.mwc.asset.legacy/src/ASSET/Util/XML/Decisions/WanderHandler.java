
package ASSET.Util.XML.Decisions;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

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