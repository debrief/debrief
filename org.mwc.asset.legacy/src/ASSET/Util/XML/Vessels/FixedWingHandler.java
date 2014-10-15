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
package ASSET.Util.XML.Vessels;

import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Vessels.Helo;
import ASSET.Util.XML.Movement.FixedWingMovementCharsHandler;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

abstract public class FixedWingHandler extends ParticipantHandler
  {

  static private final String myType = "FixedWing";

  public FixedWingHandler()
  {
    super(myType);

    // add handlers for the Helo properties
    // none in this instance

    addHandler(new FixedWingMovementCharsHandler()
    {
      public void setMovement(final MovementCharacteristics chars)
      {
        _myMoveChars = chars;
      }
    });


  }

  protected ASSET.ParticipantType getParticipant(final int index)
  {
    final Helo thisVessel = new Helo(index);
    return thisVessel;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(myType);

    Helo theHelo = (Helo) toExport;

    // export the movement chars
    MovementCharacteristics chars = theHelo.getMovementChars();
    FixedWingMovementCharsHandler.exportThis(chars, thisPart, doc);

    // and the whole participant
    ParticipantHandler.exportThis(toExport, thisPart, doc);

    parent.appendChild(thisPart);

  }
}