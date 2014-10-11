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
package ASSET.Util.XML.Control;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Scenario.Observers.ScenarioObserver;

import java.util.Vector;

abstract public class StandaloneObserverListHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  public final static String type = "StandaloneObserverList";

  private Vector<ScenarioObserver> _myList;

  public StandaloneObserverListHandler()
  {
    // inform our parent what type of class we are
    super(type);


    addHandler(new ASSET.Util.XML.Control.Observers.ObserverListHandler()
    {
      public void setObserverList(final Vector<ScenarioObserver> list)
      {
        _myList = new Vector<ScenarioObserver>(list);
      }
    });
  }


  public void elementClosed()
  {
    setObserverList(_myList);

    _myList = null;
  }


  abstract public void setObserverList(Vector<ScenarioObserver> list);


  public static void exportThis(final Vector<ScenarioObserver> list, final Element parent, final Document doc)
  {
    // create ourselves
    final Element sens = doc.createElement(type);

    // and the list
    ASSET.Util.XML.Control.Observers.ObserverListHandler.exportThis(list, sens, doc);

    parent.appendChild(sens);

  }


}