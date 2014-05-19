package ASSET.Util.XML.Movement;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


abstract public class MovementHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String type = "Movement";

  public MovementHandler()
  {
    super(type);

  }


  public void elementClosed()
  {
    setModel(new ASSET.Models.Movement.CoreMovement());
  }


  abstract public void setModel(ASSET.Models.MovementType dec);

  static public void exportThis(Object toExport, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // output it's attributes
    parent.appendChild(thisPart);

  }


}