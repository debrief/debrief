/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class StopOnProximityDetectionObserverHandler extends StopOnDetectionObserverHandler
{

  private final static String type = "StopOnProximityDetectionObserver";

  private final static String MIN_RANGE = "Range";

  WorldDistance _range;


  public StopOnProximityDetectionObserverHandler()
  {
    super(type);

    // add the range handler
    addHandler(new WorldDistanceHandler(MIN_RANGE)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _range = res;
      }
    });
  }

  protected DetectionObserver getObserver(final TargetType watch,
                                          final TargetType target,
                                          final String name,
                                          final Integer detectionLevel,
                                          boolean isActive)
  {
    return new DetectionObserver.StopOnProximityDetectionObserver(watch, target, _range,
                                                                  name, detectionLevel, isActive);
  }

  public void elementClosed()
  {
    super.elementClosed();    //To change body of overridden methods use File | Settings | File Templates.
    _range = null;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(getType());

    // get data item
    final DetectionObserver.StopOnProximityDetectionObserver bb = (DetectionObserver.StopOnProximityDetectionObserver) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

    WorldDistanceHandler.exportDistance(MIN_RANGE, bb.getRange(), thisPart, doc);

    if (bb.getDetectionLevel() != null)
      thisPart.setAttribute(DETECTION_LEVEL, writeThis(bb.getDetectionLevel().intValue()));

    TargetHandler.exportThis(bb.getTargetType(), thisPart, doc, TARGET_TYPE);
    TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);

    // output it's attributes
    parent.appendChild(thisPart);

  }


}