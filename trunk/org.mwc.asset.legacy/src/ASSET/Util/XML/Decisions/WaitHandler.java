package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.Wait;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract public class WaitHandler extends CoreDecisionHandler
  {

  private final static String type = "Wait";
  private final static String DURATION = "Duration";


  private Duration _myDuration;

  public WaitHandler()
  {
    super(type);

    addHandler(new DurationHandler(DURATION)
    {
      public void setDuration(Duration res)
      {
        _myDuration = res;
      }
    });
  }


  public void elementClosed()
  {
    final Wait tr = new Wait(_myDuration, null);
    super.setAttributes(tr);

    setModel(tr);

    _myDuration = null;
  }


  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final Wait bb = (Wait) toExport;

    // first output the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    DurationHandler.exportDuration(DURATION, bb.getDuration(), thisPart, doc);


    parent.appendChild(thisPart);

  }


}