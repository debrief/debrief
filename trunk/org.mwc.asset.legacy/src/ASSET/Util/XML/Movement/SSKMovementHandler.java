package ASSET.Util.XML.Movement;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


abstract public class SSKMovementHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String type = "SSKMovement";
  private final static String CHARGE_RATE = "ChargeRate";


  private double _chargeRate;


  public SSKMovementHandler()
  {
    super(type);
    super.addAttributeHandler(new HandleDoubleAttribute(CHARGE_RATE)
    {
      public void setValue(String name, final double val)
      {
        _chargeRate = val;
      }
    });
  }


  public void elementClosed()
  {
    final ASSET.Models.Movement.SSKMovement ss = new ASSET.Models.Movement.SSKMovement();
    ss.setChargeRate(_chargeRate);
    setModel(ss);
  }


  abstract public void setModel(ASSET.Models.MovementType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    final ASSET.Models.Movement.SSKMovement sskm = (ASSET.Models.Movement.SSKMovement) toExport;

    thisPart.setAttribute(CHARGE_RATE, writeThis(sskm.getChargeRate()));

    // output it's attributes
    parent.appendChild(thisPart);

  }


}