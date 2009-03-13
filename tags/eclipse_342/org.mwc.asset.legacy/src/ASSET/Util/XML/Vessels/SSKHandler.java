package ASSET.Util.XML.Vessels;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Vessels.SSK;

abstract public class SSKHandler extends ParticipantHandler
  {

  static private final String type = "SSK";
  static private final String _rechargeRate = "ChargeRate";

  double _myChargeRate = -1;

  public SSKHandler()
  {
    super(type);

    // add handlers for the SSK properties
    addAttributeHandler(new HandleDoubleAttribute(_rechargeRate)
    {
      public void setValue(String name, final double val)
      {
        _myChargeRate = val;
      }
    });


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
    final ASSET.Models.Vessels.SSK thisVessel = new ASSET.Models.Vessels.SSK(index);
    return thisVessel;
  }

  /**
   * extra method provided to allow child classes to interrupt the participant
   * creation process
   */
  protected void finishParticipant(final ASSET.ParticipantType newPart)
  {
    final ASSET.Models.Vessels.SSK thisVessel = (SSK) newPart;
    thisVessel.setChargeRate(_myChargeRate);
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    SSK theSub = (SSK) toExport;

    ParticipantHandler.exportThis(theSub, thisPart, doc);

    thisPart.setAttribute(_rechargeRate, writeThis(theSub.getChargeRate()));

    parent.appendChild(thisPart);

  }

}