/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XML.Decisions.Tactical;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.Intercept;
import ASSET.Models.Decision.TargetType;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

abstract public class InterceptHandler extends CoreDecisionHandler
{

  private final static String type = "Intercept";
  private final static String TARGET_TYPE = "TargetType";
  private final static String SPEED_CHANGE_ALLLOWED = "AllowSpeedchange";

  TargetType _myTargetType;
  boolean _allowSpeedChange;


  public InterceptHandler()
  {
    super(type);

    addHandler(new TargetTypeHandler(TARGET_TYPE)
    {
      public void setTargetType(final TargetType type)
      {
        _myTargetType = type;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SPEED_CHANGE_ALLLOWED)
    {
      public void setValue(String name, boolean value)
      {
        _allowSpeedChange = value;
      }
    });
  }


  public void elementClosed()
  {
    final Intercept tr = new Intercept(_myTargetType, _allowSpeedChange);

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
    final Intercept bb = (Intercept) toExport;

    // parent attributes first
    CoreDecisionHandler.exportThis(bb, element, doc);

    // output it's attributes
    TargetTypeHandler.exportThis(TARGET_TYPE, bb.getTargetType(), element, doc);
    element.setAttribute(SPEED_CHANGE_ALLLOWED, writeThis(bb.getSpeedChangeAllowed()));

    parent.appendChild(element);

  }


}