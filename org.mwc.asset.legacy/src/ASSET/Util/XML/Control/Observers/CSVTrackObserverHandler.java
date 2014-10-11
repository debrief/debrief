/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
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

import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.Recording.CSVTrackObserver;
import ASSET.Scenario.Observers.ScenarioObserver;

abstract class CSVTrackObserverHandler extends CoreFileObserverHandler
{

  private final static String type = "CSVTrackObserver";

  public CSVTrackObserverHandler()
  {
    super(type);

  }

  public void elementClosed()
  {
    // create ourselves
    final CoreObserver timeO = new CSVTrackObserver(_directory, _fileName, false, _name, _isActive);

    setObserver(timeO);

    // and reset
    super.elementClosed();
  }


  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final RecordToFileObserverType bb = (RecordToFileObserverType) toExport;

    // output the parent attrbutes
    CoreFileObserverHandler.exportThis(bb, thisPart);

    // output it's attributes


    // output it's attributes
    parent.appendChild(thisPart);

  }


}