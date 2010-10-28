package ASSET.Util.XML.Control.Observers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.DetectionObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Plotting.PlotInvestigationSubjectObserver;
import ASSET.Util.XML.Control.Observers.StopOnDetectionObserverHandler.TargetHandler;

abstract public class PlotInvestigationSubjectObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final static String type = "PlotInvestigationSubjectObserver";

  final static String ACTIVE = "Active";
  final static String WATCH_TYPE = "Watch";

  TargetType _watchType = null;
  boolean _isActive;
  String _name;

  PlotInvestigationSubjectObserverHandler(final String obs_type)
  {
    super(obs_type);

    addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
    {
      public void setValue(String name, final boolean val)
      {
        _isActive = val;
      }
    });

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, final String val)
      {
        _name = val;
      }
    });

    addHandler(new TargetHandler(WATCH_TYPE)
    {
      public void setTargetType(final TargetType type)
      {
        _watchType = type;
      }
    });
  }

  public PlotInvestigationSubjectObserverHandler()
  {
    this(type);
  }

  static String getType()
  {
    return type;
  }

  DetectionObserver getObserver(final TargetType watch, final TargetType target, final String name,
                                final Integer detectionLevel, boolean isActive)
  {
    return new DetectionObserver(watch, target, name, detectionLevel, isActive);
  }

  public void elementClosed()
  {
    PlotInvestigationSubjectObserver poi = new PlotInvestigationSubjectObserver(_watchType, _name, _isActive);
    setObserver(poi);
    // and reset
    _name = null;
    _watchType = null;
  }


  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(getType());

    // get data item
    final PlotInvestigationSubjectObserver bb = (PlotInvestigationSubjectObserver) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));
    TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);

    // output it's attributes
    parent.appendChild(thisPart);

  }

}