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
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


abstract public class WorldVectorHandler extends MWCXMLReader
{

  WorldDistance _range;
  double _bearingDegs;
  private static final String BEARING = "BearingDegs";
  private static final String RANGE = "Range";

  public WorldVectorHandler(String myType)
  {
    super(myType);

    addHandler(new WorldDistanceHandler(RANGE){
		
			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_range = res;
			}
		});

    addAttributeHandler(new HandleDoubleAttribute(BEARING)
    {
      public void setValue(String name, double val)
      {
      	_bearingDegs = val;
      }
    });
  }


  public WorldVectorHandler()
  {
    // inform our parent what type of class we are
    this("WorldDistance");
  }


  public void elementClosed()
  {
  	WorldVector res = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(_bearingDegs),
  			_range, null);
    setWorldVector(res);

    // reset the units.  If it doesn't get overwritten we continue to use NM
    _range = null;
    _bearingDegs = 0;
  }

  abstract public void setWorldVector(WorldVector res);



  public static void exportVector(String myType, WorldVector vector, org.w3c.dom.Element parent,
                                    org.w3c.dom.Document doc)
  {
  	// create the object
    org.w3c.dom.Element eLoc = doc.createElement(myType);
    
    // store the fields
    eLoc.setAttribute(BEARING, writeThis(MWC.Algorithms.Conversions.Rads2Degs(vector.getBearing())));
    WorldDistance dist = new WorldDistance(vector.getRange(), WorldDistance.DEGS);
    WorldDistanceHandler.exportDistance(RANGE, dist, eLoc, doc);

    // remember it
    parent.appendChild(eLoc);
  }

}