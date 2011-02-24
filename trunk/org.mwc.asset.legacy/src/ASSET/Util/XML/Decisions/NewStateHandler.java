package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.NewState;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class NewStateHandler extends CoreDecisionHandler
  {

  private final static String type = "NewState";
  private final static String State = "RequestedState";


  String _newState;

  public NewStateHandler()
  {
    super(type);
    
    
    this.addAttributeHandler(new HandleAttribute(State)
		{
			
			@Override
			public void setValue(String name, String value)
			{
				_newState = value;
			}
		});
  }


  public void elementClosed()
  {
    final NewState tr = new NewState(_newState, null);
    super.setAttributes(tr);

    setModel(tr);

    _newState = null;
  }


  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final NewState bb = (NewState) toExport;

    // first output the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);
    
    thisPart.setAttribute(State,bb.getState());

    parent.appendChild(thisPart);

  }


}