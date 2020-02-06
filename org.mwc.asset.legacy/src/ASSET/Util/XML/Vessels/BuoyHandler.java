
package ASSET.Util.XML.Vessels;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

abstract public class BuoyHandler extends ParticipantHandler{

  static private final String myType = "Buoy";

  public BuoyHandler() {
    super(myType);


    // add handlers for the Helo properties
    // none in this instance

    addHandler(new ASSET.Util.XML.Movement.SSMovementCharsHandler()
    {
      public void setMovement(final ASSET.Models.Movement.MovementCharacteristics chars)
      {
        _myMoveChars = chars;
      }
    });
  }

  protected ASSET.ParticipantType getParticipant(final int index)
  {
    final ASSET.ParticipantType thisVessel = new ASSET.Models.Vessels.Buoy(index);
    return thisVessel;
  }

  static public void exportThis(final Object toExport,final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
      // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(myType);

    ParticipantHandler.exportThis(toExport, thisPart, doc);

    parent.appendChild(thisPart);

  }
}