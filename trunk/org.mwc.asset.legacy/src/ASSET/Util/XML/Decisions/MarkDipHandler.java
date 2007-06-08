package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.MarkDip;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class MarkDipHandler extends CoreDecisionHandler
  {

  private final static String type = "MarkDip";
  private final static String DEPTH = "BodyDepth";


  private WorldDistance _theDepth;

  public MarkDipHandler()
  {
    super(type);

    addHandler(new WorldDistanceHandler(DEPTH)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _theDepth = res;
      }
    });


  }


  public void elementClosed()
  {
    final MarkDip tr = new MarkDip(_theDepth);
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
    final MarkDip bb = (MarkDip) toExport;

    // first the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    WorldDistanceHandler.exportDistance(DEPTH, bb.getBodyDepth(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}