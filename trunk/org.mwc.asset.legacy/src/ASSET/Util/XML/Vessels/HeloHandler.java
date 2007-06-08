package ASSET.Util.XML.Vessels;

import ASSET.Models.Movement.*;
import ASSET.Models.Vessels.Helo;
import ASSET.Util.XML.Movement.HeloMovementCharsHandler;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

abstract public class HeloHandler extends ParticipantHandler{

  static private final String myType = "Helo";

  public HeloHandler() {
    super(myType);

    // add handlers for the Helo properties
    // none in this instance

    addHandler(new ASSET.Util.XML.Movement.HeloMovementCharsHandler()
    {
      public void setMovement(final ASSET.Models.Movement.MovementCharacteristics chars)
      {
        _myMoveChars = chars;
      }
    });


  }

  protected ASSET.ParticipantType getParticipant(final int index)
  {
    final ASSET.Models.Vessels.Helo thisVessel = new ASSET.Models.Vessels.Helo(index);
    return thisVessel;
  }

  static public void exportThis(final Object toExport,final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(myType);

    Helo theHelo = (Helo)toExport;

    // export the movement chars
    // and the whole participant
    ParticipantHandler.exportThis(toExport, thisPart, doc);

    HeloMovementCharacteristics chars = (HeloMovementCharacteristics) theHelo.getMovementChars();
    HeloMovementCharsHandler.exportThis(chars, thisPart, doc);

    
    parent.appendChild(thisPart);

  }
}