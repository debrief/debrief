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

  String _units;
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

    _units = null;
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


    org.w3c.dom.Element eLoc = doc.createElement(myType);
    // set the attributes
    int theUnit = MWC.GUI.Properties.DistancePropertyEditor.selectUnitsFor(distance.getValueIn(WorldDistance.NM));

    // and get value
    double value = distance.getValueIn(theUnit);


    // get the name of the units
    String units = WorldDistance.getLabelFor(theUnit);

    eLoc.setAttribute(VALUE, writeThis(value));
    eLoc.setAttribute(UNITS, units);

    parent.appendChild(eLoc);
  }

}