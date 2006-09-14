package ASSET.Util.XML.Vessels;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

abstract public class SSNHandler extends ParticipantHandler{


  static private final String myType = "SSN";

  public SSNHandler() {
    super(myType);

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
    final ASSET.Models.Vessels.SSN thisVessel = new ASSET.Models.Vessels.SSN(index);
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