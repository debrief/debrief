package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.SternArcClearance;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract public class SternArcClearanceHandler extends CoreDecisionHandler
{

  private final static String type = "SternArcClearance";

  private final static String FREQ = "Frequency";
  private final static String RANDOM = "RandomClearances";
  private final static String STYLE = "Style";
  private final static String COURSE_CHANGE = "CourseChange";

  private Duration _myFreq = null;
  private boolean _random;
  private String _style = null;
  private Double _turnAngle = null;

  public SternArcClearanceHandler()
  {
    super(type);

    addHandler(new DurationHandler(FREQ)
    {
      public void setDuration(Duration res)
      {
        _myFreq = res;
      }
    });
    addAttributeHandler(new HandleAttribute(STYLE)
    {
      public void setValue(String name, final String val)
      {
        _style = val;
      }
    });
    addAttributeHandler(new HandleAttribute(RANDOM)
    {
      public void setValue(String name, final String val)
      {
        _random = Boolean.valueOf(val).booleanValue();
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(COURSE_CHANGE)
    {
      public void setValue(String name, double value)
      {
        _turnAngle = new Double(value);
      }
    });
  }


  public void elementClosed()
  {
    final SternArcClearance ev = new SternArcClearance(_myFreq, _random, _style, _turnAngle);

    // set the parent attributes
    super.setAttributes(ev);

    // finally output it
    setModel(ev);

    _myFreq = null;
    _style = null;
    _turnAngle = null;
  }

  abstract public void setModel(ASSET.Models.DecisionType dec);


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Decision.Tactical.SternArcClearance bb = (ASSET.Models.Decision.Tactical.SternArcClearance) toExport;

    // first the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    thisPart.setAttribute(RANDOM, writeThis(bb.isRandomClearances()));
    thisPart.setAttribute(STYLE, bb.getStyle());
    thisPart.setAttribute(COURSE_CHANGE, writeThis(bb.getCourseChange()));

    DurationHandler.exportDuration(FREQ, bb.getFrequency(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}