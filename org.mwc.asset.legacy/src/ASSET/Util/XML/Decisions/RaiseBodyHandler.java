package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.RaiseBody;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class RaiseBodyHandler extends CoreDecisionHandler
  {

  private final static String type = "RaiseBody";

  public RaiseBodyHandler()
  {
    super(type);
  }


  public void elementClosed()
  {
    final RaiseBody tr = new RaiseBody();
    super.setAttributes(tr);

    setModel(tr);
  }


  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final RaiseBody bb = (RaiseBody) toExport;

    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes

    parent.appendChild(thisPart);

  }


}