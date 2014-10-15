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
package ASSET.Util.XML.Decisions.Conditions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Decision.Conditions.Detection;
import ASSET.Models.Decision.TargetType;
import MWC.GenericData.WorldDistance;

abstract public class DetectionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String type = "Detection";

//  private final static String THRESHOLD = "Threshold";

  WorldDistance _rangeThreshold;
  String _name;
  TargetType _myTargetType;


  public DetectionHandler()
  {
    super("Detection");

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, final String val)
      {
        _name = val;
      }
    });

    addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler()
    {
      public void setWorldDistance(final WorldDistance res)
      {
        _rangeThreshold = res;
      }
    });

    addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler()
    {
      public void setTargetType(final TargetType type)
      {
        _myTargetType = type;
      }
    });
  }


  public void elementClosed()
  {
    final Detection res = new Detection(_myTargetType, _rangeThreshold);
    res.setName(_name);

    // finally output it
    setCondition(res);

    // and reset
    _myTargetType = null;
    _rangeThreshold = null;
    _name = null;
  }

  abstract public void setCondition(Condition dec);


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final Detection bb = (Detection) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(bb.getRangeThreshold(), thisPart, doc);
    ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}