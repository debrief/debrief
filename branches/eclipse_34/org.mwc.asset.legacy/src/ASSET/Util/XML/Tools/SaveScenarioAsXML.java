package ASSET.Util.XML.Tools;

import MWC.GUI.Tools.*;
import MWC.GUI.*;
import java.io.*;
import ASSET.ScenarioType;

public class SaveScenarioAsXML extends MWC.GUI.Tools.Operations.Save
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private ScenarioType _theScenario = null;

  private final static String mySuffix = "xml";

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public SaveScenarioAsXML(final ToolParent theParent,
                    final ASSET.ScenarioType theScenario){
    this(theParent, theScenario,  "Save Scenario As...", "images/saveas.gif");
  }

  SaveScenarioAsXML(final ToolParent theParent,
                    final ASSET.ScenarioType theScenario,
                    final String theTitle,
                    final String theImage)
  {
    super(theParent, theTitle, "*." + mySuffix, theImage);

    // store the ScenarioType parameter
    _theScenario = theScenario;

    // see if we have an old directory to retrieve
    if(_lastDirectory == "")
    {
      final String val = getParent().getProperty("ASF_Directory");
      if(val != null)
        _lastDirectory = val;
    }
  }

  /////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////
  protected Action doSave(String filename)
  {
    Action res = null;

    // now save ScenarioType to this file
    try
    {

      // check if the file ends in XML
      final int idx = filename.toLowerCase().indexOf("." + mySuffix);
      final int CLASS_EXTENSION_LENGTH = 4;
      if(idx == -1)
      {
        filename += "." + mySuffix;
      }

      // open the file
      final OutputStream os = new FileOutputStream(filename);

      // inform the ScenarioType of it's filename
      // _theScenario.setFileName(filename); /** @todo store filename in scenario */

      // pass all of this to the XML exporter
      ASSET.Util.XML.ASSETReaderWriter.exportThis(_theScenario, null, os);

      os.close();

      res =  new SaveScenarioAction("the scenario"); /** create names for scenarios */

    }
    catch(IOException e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    return res;
  }


  protected ScenarioType getScenarioType()
  {
    return _theScenario;
  }

  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  protected class SaveScenarioAction implements Action{
    /** store the name of the ScenarioType we have saved
     */
    final String _theScenarioName;

    public SaveScenarioAction(final String theName){
      _theScenarioName = theName;
    }

    public boolean isRedoable(){
      return false;
    }


    public boolean isUndoable(){
      return false;
    }

    public String toString(){
      return "Save " + _theScenarioName;
    }

    public void undo(){
      // delete the plottables from the Application object
    }

    public void execute(){
    }

  }


  public void close()
  {
    super.close();

    _theScenario = null;
  }
}
