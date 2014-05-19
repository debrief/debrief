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
}