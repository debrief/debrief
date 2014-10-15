/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.UserControl;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

abstract public class UserControlHandler extends CoreDecisionHandler
  {

  private final static String type = "UserControl";
  private final static String DEPTH = "Depth";
  private final static String SPEED = "Speed";
  private final static String COURSE = "Course";


  double _speed;
  double _course;
  double _depth;


  public UserControlHandler()
  {
    super("UserControl");

    addAttributeHandler(new HandleDoubleAttribute(DEPTH)
    {
      public void setValue(String name, final double val)
      {
        _depth = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(SPEED)
    {
      public void setValue(String name, final double val)
      {
        _speed = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(COURSE)
    {
      public void setValue(String name, final double val)
      {
        _course = val;
      }
    });

  }


  public void elementClosed()
  {
    final UserControl ev = new UserControl(_course, new WorldSpeed(_speed, WorldSpeed.Kts), new WorldDistance(_depth, WorldDistance.METRES));

    super.setAttributes(ev);

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
    final UserControl bb = (UserControl) toExport;

    // first output the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    thisPart.setAttribute(DEPTH, writeThis(bb.getDepth().getValueIn(WorldDistance.METRES)));
    thisPart.setAttribute(SPEED, writeThis(bb.getSpeed().getValueIn(WorldSpeed.Kts)));
    thisPart.setAttribute(COURSE, writeThis(bb.getCourse()));

    parent.appendChild(thisPart);

  }


}