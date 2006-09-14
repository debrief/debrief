package ASSET.Util.XML.Vessels;

import ASSET.Util.XML.Movement.SurfaceMovementCharsHandler;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

abstract public class SurfaceHandler extends ParticipantHandler{

  static private final String myType = "Surface";

  public SurfaceHandler() {
    super(myType);

    // add handlers for the SU properties
    // none in this instance

    addHandler(new ASSET.Util.XML.Movement.SurfaceMovementCharsHandler()
    {
      public void setMovement(final ASSET.Models.Movement.MovementCharacteristics chars)
      {
        _myMoveChars = chars;
      }
    });

  }

  protected ASSET.ParticipantType getParticipant(final int index)
  {
    final ASSET.Models.Vessels.Surface thisVessel = new ASSET.Models.Vessels.Surface(index);
    return thisVessel;
  }

  static public void exportThis(final Object toExport,final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
      // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(myType);

    ParticipantHandler.exportThis(toExport, thisPart, doc);

    Surface surf = (Surface) toExport;
    SurfaceMovementCharsHandler.exportThis((SurfaceMovementCharacteristics) surf.getMovementChars(), thisPart, doc);

    parent.appendChild(thisPart);

  }
}