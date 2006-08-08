package ASSET.Util.XML.Tools;

import MWC.GUI.*;
import ASSET.ScenarioType;

public class SaveScenarioXML extends SaveScenarioAsXML
{
  ///////////////////////////////////
  // member variables
  //////////////////////////////////

  ///////////////////////////////////
  // constructor
  //////////////////////////////////
  public SaveScenarioXML(final ToolParent theParent,
                  final ASSET.ScenarioType theScenario){
    super(theParent, theScenario, "Save Scenario", "images/save.gif");

  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////

  public void execute()
  {
    // do we have a filename already?
    final String fn = null; /** @todo store filename in a scenario */

    if(fn != null)
    {
      // just re-save the Scenario to the (known) filename
      this.doSave(fn);
    }
    else
    {
      // just let the parent do it's normal processing
      super.execute();
    }
  }
}
