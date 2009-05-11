package MWC.Utilities.ReaderWriter.XML.Util;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


abstract public class WorldDistanceHandler extends MWCXMLReader
{

  String _units = WorldDistance.UnitLabels[WorldDistance.NM];
  double _value;
  private static final String UNITS = "Units";
  private static final String VALUE = "Value";

  public WorldDistanceHandler(String myType)
  {
    super(myType);

    addAttributeHandler(new HandleAttribute(UNITS)
    {
      public void setValue(String name, String val)
      {
        _units = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(VALUE)
    {
      public void setValue(String name, double val)
      {
        _value = val;
      }
    });
  }


  public WorldDistanceHandler()
  {
    // inform our parent what type of class we are
    this("WorldDistance");
  }


  public void elementClosed()
  {
    // produce a value using these units
    int theUnits = MWC.GenericData.WorldDistance.getUnitIndexFor(_units);
    WorldDistance res = new WorldDistance(_value, theUnits);

    setWorldDistance(res);

    // reset the units.  If it doesn't get overwritten we continue to use NM
    _units = WorldDistance.UnitLabels[WorldDistance.NM];
    _value = -1;


  }


  public  static WorldDistance extractWorldDistance(Element topL, String name)
  {
    WorldDistance myDist = null;
    NodeList list = topL.getElementsByTagName(name);
    if (list.getLength() > 0)
    {
      topL = (Element) list.item(0);
      String unitsVal = topL.getAttribute(UNITS);
      int units = WorldDistance.getUnitIndexFor(unitsVal);
      double value = Integer.parseInt(topL.getAttribute(VALUE));

      myDist = new WorldDistance(value, units);
    }
    return myDist;
  }


  abstract public void setWorldDistance(WorldDistance res);


  public static void exportDistance(WorldDistance distance, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    exportDistance("WorldDistance", distance, parent, doc);
  }

  public static void exportDistance(String myType, WorldDistance distance, org.w3c.dom.Element parent,
                                    org.w3c.dom.Document doc)
  {
  	// create the object
    org.w3c.dom.Element eLoc = doc.createElement(myType);
    
    // store the fields
    eLoc.setAttribute(VALUE, writeThis(distance.getValue()));
    eLoc.setAttribute(UNITS, distance.getUnitsLabel());

    // remember it
    parent.appendChild(eLoc);
  }

}