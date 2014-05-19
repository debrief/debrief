package ASSET.Util.XML.Decisions;

/**
 * Read in a Switch behaviour object
 */

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Switch;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class SwitchHandler extends WaterfallHandler
{

  private final static String type = "Switch";
  private final static String INDEX = "Index";

  int _index;

  public SwitchHandler(int thisDepth)
  {
    super(type, thisDepth);

    super.addAttributeHandler(new HandleIntegerAttribute(INDEX)
    {
      public void setValue(String name, int value)
      {
        _index = value;
      }
    });
  }


  protected BehaviourList createNewList()
  {
    return new Switch();
  }

  /**
   * set our attributes within this decision object
   *
   * @param decision the decision object to update
   */
  protected void setAttributes(CoreDecision decision)
  {
    super.setAttributes(decision);
    ((Switch)_myList).setIndex(_index);
  }

  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final Switch bb = (Switch) toExport;

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

    thisPart.setAttribute(INDEX, writeThis(bb.getIndex()));

    parent.appendChild(thisPart);

  }
}