package ASSET.Util.XML.Control.Observers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.TimeObserver;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract class TimeObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final static String type = "TimeObserver";

  private final static String ACTIVE = "Active";

  private boolean _isActive;
  private String _name;

  private Duration _theDuration;

  public TimeObserverHandler()
  {
    super(type);

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

    addHandler(new DurationHandler()
    {
      public void setDuration(Duration res)
      {
        _theDuration = res;
      }
    });
  }

  public void elementClosed()
  {
    // create ourselves
    final ScenarioObserver timeO = new TimeObserver(_theDuration, _name, _isActive);

    setObserver(timeO);

    // and reset
    _name = null;
    _theDuration = null;
  }


  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final TimeObserver bb = (TimeObserver) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

    DurationHandler.exportDuration(bb.getElapsed(), thisPart, doc);


    // output it's attributes
    parent.appendChild(thisPart);

  }


}