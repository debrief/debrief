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
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

abstract public class StopOnDetectionObserverHandler extends DetectionObserverHandler
{

  private final static String type = "StopOnDetectionObserver";

  public StopOnDetectionObserverHandler(String type)
  {
    super(type);
  }

  public StopOnDetectionObserverHandler()
  {
    this(type);
  }

  protected DetectionObserver getObserver(final TargetType watch, final TargetType target, final String name,
                                          final Integer detectionLevel, boolean isActive)
  {
    return new DetectionObserver.StopOnDetectionObserver(watch, target, name, detectionLevel, isActive);
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(getType());

    // get data item
    final DetectionObserver.StopOnDetectionObserver bb = (DetectionObserver.StopOnDetectionObserver) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

    if (bb.getDetectionLevel() != null)
      thisPart.setAttribute(DETECTION_LEVEL, writeThis(bb.getDetectionLevel().intValue()));

    TargetHandler.exportThis(bb.getTargetType(), thisPart, doc, TARGET_TYPE);
    TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);

    // output it's attributes
    parent.appendChild(thisPart);

  }

  /**
   * ************************************************************
   * handle our different types of target handler
   * *************************************************************
   */
  abstract protected static class TargetHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {
    TargetType _type = null;

    public TargetHandler(final String myType)
    {
      super(myType);
      addHandler(new TargetTypeHandler()
      {
        public void setTargetType(final TargetType type)
        {
          _type = type;
        }
      });
    }

    public void elementClosed()
    {
      setTargetType(_type);
      _type = null;
    }

    abstract public void setTargetType(TargetType type);

    static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                  final org.w3c.dom.Document doc, final String myType)
    {
      // create ourselves
      final org.w3c.dom.Element thisElement = doc.createElement(myType);

      // get data item
      final TargetType bb = (TargetType) toExport;

      // output it's attributes

      // output the target type
      TargetTypeHandler.exportThis(bb, thisElement, doc);

      // output it's attributes
      parent.appendChild(thisElement);
    }
  }


}