package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Waterfall;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class SequenceHandler extends WaterfallHandler
  {

  private final static String type = "Sequence";
  protected final static String STAY_ALIVE = "StayAlive";
  
  public SequenceHandler(String type, int thisDepth)
  {
    super(type, thisDepth);

    _myList = new Sequence();
    
    addAttributeHandler(new HandleBooleanAttribute(STAY_ALIVE){
      public void setValue(String name, boolean value)
      {
      	Sequence seq  = (Sequence) _myList;
      	seq.setStayAlive(value);
      }
    });    
  }

  public SequenceHandler(int thisDepth)
  {
    this(type, thisDepth);
  }


  protected BehaviourList createNewList()
  {
  	Sequence res = new Sequence();
    return res;
  }

  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportSequence(String theName, final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(theName);

    // get data item
    final Waterfall bb = (Waterfall) toExport;

    // do the parent export bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // thisPart.setAttribute("MIN_DEPTH", writeThis(bb.getMinDepth()));
    // step through the models
    final java.util.Iterator<DecisionType> it = bb.getModels().iterator();
    while (it.hasNext())
    {
      final ASSET.Models.DecisionType dec = (ASSET.Models.DecisionType) it.next();

      exportThisDecisionModel(dec, thisPart, doc);
    }

    parent.appendChild(thisPart);

  }
  static public void exportSequence(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    exportSequence(type, toExport, parent, doc);
  }


  /**
   * *******************************************************************
   * interface to represent this and the Sequence class, allowing us to re-use the
   * add behaviours method
   * *******************************************************************
   */
  public static interface BehaviourListHandler
  {
    public void addModel(final ASSET.Models.DecisionType dec);
  }

}