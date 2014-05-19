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
import ASSET.Models.Decision.Conditions.DetectionForPeriod;
import ASSET.Models.Decision.TargetType;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;

abstract public class DetectionForPeriodHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String type = "DetectionForPeriod";

  private final static String CONTINUE_MONITORING = "ContinueMonitoring";
//  private final static String THRESHOLD = "Threshold";

  WorldDistance _rangeThreshold;
  String _name;
  TargetType _myTargetType;
  Duration _myDuration;
  boolean _continueMonitoring;


  public DetectionForPeriodHandler()
  {
    super("DetectionForPeriod");

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, final String val)
      {
        _name = val;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(CONTINUE_MONITORING)
    {
      public void setValue(String name, boolean value)
      {
        _continueMonitoring = value;
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

    addHandler(new MWC.Utilities.ReaderWriter.XML.Util.DurationHandler()
    {
      public void setDuration(Duration res)
      {
        _myDuration = res;
      }

    });


  }


  public void elementClosed()
  {
    final Detection res = new DetectionForPeriod(_myTargetType,
                                                 _rangeThreshold,
                                                 _myDuration,
                                                 _continueMonitoring);
    res.setName(_name);

    // finally output it
    setCondition(res);

    // and reset
    _myTargetType = null;
    _rangeThreshold = null;
    _myDuration = null;
    _name = null;
  }

  abstract public void setCondition(Condition dec);


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final DetectionForPeriod bb = (DetectionForPeriod) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    thisPart.setAttribute(CONTINUE_MONITORING, writeThis(bb.getContinueMonitoring()));
    MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(bb.getRangeThreshold(), thisPart, doc);
    MWC.Utilities.ReaderWriter.XML.Util.DurationHandler.exportDuration(bb.getValidPeriod(), thisPart, doc);
    ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);

    parent.appendChild(thisPart);
  }
}