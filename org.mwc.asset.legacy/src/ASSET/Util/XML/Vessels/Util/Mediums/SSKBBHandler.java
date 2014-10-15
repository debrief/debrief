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
package ASSET.Util.XML.Vessels.Util.Mediums;

import ASSET.Models.Environment.EnvironmentType;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

abstract public class SSKBBHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  final static private String type = "SSKBroadband";
  final static private String LEVEL = "BaseNoiseLevel";
  final static private String SNORT_LEVEL = "SnortNoiseLevel";

  double _myNoise;
  double _mySnortNoise;

  public SSKBBHandler()
  {
    super(type);

    addAttributeHandler(new HandleDoubleAttribute(LEVEL)
    {
      public void setValue(String name, final double val)
      {
        _myNoise = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(SNORT_LEVEL)
    {
      public void setValue(String name, final double val)
      {
        _mySnortNoise = val;
      }
    });
  }


  public void elementClosed()
  {
    final ASSET.Models.Mediums.SSKBroadband bb = new ASSET.Models.Mediums.SSKBroadband(_myNoise, _mySnortNoise);
    setMedium(EnvironmentType.BROADBAND_PASSIVE, bb);
  }

  abstract public void setMedium(int index, ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    final ASSET.Models.Mediums.SSKBroadband bb = (ASSET.Models.Mediums.SSKBroadband) toExport;

    final org.w3c.dom.Element ele = doc.createElement(type);

    ele.setAttribute("BaseNoiseLevel", writeThis(bb.getBaseNoiseLevelFor(null)));
    ele.setAttribute("SnortNoiseLevel", writeThis(bb.getSnortNoiseLevel()));

    parent.appendChild(ele);
  }
}