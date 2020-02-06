
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