package ASSET.Util.XML.Vessels.Util.Mediums;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

abstract public class NBHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  final static private String type = "Narrowband";
  final static private String LEVEL = "BaseNoiseLevel";
  final static private String FREQ = "Frequency";

  double _myNoise;
  double _myFreq;

  public NBHandler()
  {
    super(type);


    addAttributeHandler(new HandleDoubleAttribute(LEVEL)
    {
      public void setValue(String name, final double val)
      {
        _myNoise = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(FREQ)
    {
      public void setValue(String name, final double val)
      {
        _myFreq = val;
      }
    });
  }


  public void elementClosed()
  {
    final Medium bb = new ASSET.Models.Mediums.NarrowbandRadNoise(_myNoise, _myFreq);
    setMedium(EnvironmentType.NARROWBAND, bb);
  }

  abstract public void setMedium(int index, ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med);


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    final ASSET.Models.Mediums.NarrowbandRadNoise bb = (ASSET.Models.Mediums.NarrowbandRadNoise) toExport;

    final org.w3c.dom.Element ele = doc.createElement(type);

    ele.setAttribute(LEVEL, writeThis(bb.getBaseNoiseLevel()));
    ele.setAttribute(FREQ, writeThis(bb.getFrequency()));

    parent.appendChild(ele);
  }
}