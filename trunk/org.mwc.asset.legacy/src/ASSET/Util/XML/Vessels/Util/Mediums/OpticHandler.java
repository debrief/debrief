package ASSET.Util.XML.Vessels.Util.Mediums;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Mediums.Optic;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

abstract public class OpticHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  final static private String type = "Optic";

  final static private String NOISE = "BaseNoiseLevel";
  final static private String AREA = "XSectArea";
  final static private String HEIGHT = "Height";


  private double _myNoise = Optic.INVALID_HEIGHT;
  private double _myArea = Optic.INVALID_HEIGHT;
  private WorldDistance _myHeight = new WorldDistance(Optic.INVALID_HEIGHT, WorldDistance.METRES);

  public OpticHandler()
  {
    super(type);

    addAttributeHandler(new HandleDoubleAttribute(NOISE)
    {
      public void setValue(String name, final double val)
      {
        _myNoise = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(AREA)
    {
      public void setValue(String name, final double val)
      {
        _myArea = val;
      }
    });

    addHandler(new WorldDistanceHandler(HEIGHT)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myHeight = res;
      }
    });
  }


  public void elementClosed()
  {

    Optic res = null;

    // do we have height?
    if (_myHeight == null)
    {
      res = new ASSET.Models.Mediums.Optic(_myNoise, new WorldDistance(Optic.INVALID_HEIGHT, WorldDistance.METRES));

    }
    else
    {
      res = new ASSET.Models.Mediums.Optic(_myArea, _myHeight);
    }

    res.setXSectArea(_myArea);
    setMedium(EnvironmentType.VISUAL, res);

    // reset vars
    _myNoise = Optic.INVALID_HEIGHT;
    _myArea = Optic.INVALID_HEIGHT;
    _myHeight = null;

  }

  abstract public void setMedium(int index, ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    final ASSET.Models.Mediums.Optic bb = (ASSET.Models.Mediums.Optic) toExport;

    final org.w3c.dom.Element ele = doc.createElement(type);

    ele.setAttribute(AREA, writeThis(bb.getXSectArea()));

    WorldDistanceHandler.exportDistance(HEIGHT, bb.getHeight(), ele, doc);

    parent.appendChild(ele);
  }
}