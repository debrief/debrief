package ASSET.Util.XML.Tools;

import MWC.GUI.ToolParent;

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
    {
			// just let the parent do it's normal processing
			super.execute();
		}
  }
}
