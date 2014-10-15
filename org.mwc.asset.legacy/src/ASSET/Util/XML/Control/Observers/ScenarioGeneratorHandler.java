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
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 * @deprecated
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

abstract public class ScenarioGeneratorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private final static String type = "ScenarioGenerator";

  public ScenarioGeneratorHandler()
  {
    // inform our parent what type of class we are
    super(type);
  }


  public void elementClosed()
  {
    setScenarioGenerator(null);
  }


  abstract public void setScenarioGenerator(Object genny);


  public static void exportThis(final Object generator, final Element parent, final Document doc)
  {
    // create ourselves
    final Element sens = doc.createElement(type);

    parent.appendChild(sens);

  }


}