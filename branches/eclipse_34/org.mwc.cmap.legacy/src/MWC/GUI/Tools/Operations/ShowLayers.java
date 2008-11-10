package MWC.GUI.Tools.Operations;

// Copyright MWC 1999
// $RCSfile: ShowLayers.java,v $
// $Author: Ian.Mayo $
// $Log: ShowLayers.java,v $
// Revision 1.2  2004/05/25 15:44:10  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:45  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:22:29+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.0  2001-07-17 08:42:54+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:47  ianmayo
// initial version
//


import MWC.GUI.Tools.*;
import MWC.GUI.*;
import java.io.*;


 public class ShowLayers extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  MWC.GUI.Properties.PropertiesPanel _theProperties;

  MWC.GUI.Layers _theData;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /** ShowLayers an existing data file
   * @param theParent parent application, where we can show the busy cursor
   * @param theLabel the label to put on the button
   * @param theSuffix file suffix for type of file we are importing
   * @param theDescription textual description of file type
   */
  public ShowLayers(ToolParent theParent,
              String theLabel,
              MWC.GUI.Properties.PropertiesPanel thePanel,
              MWC.GUI.Layers theData){

    super(theParent, theLabel, "images/layer_mgr.gif");

    // store the properties window, it's the destination we have to use
    _theProperties = thePanel;

    // store the layers, it's what we have to show
    _theData = theData;

  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** collate the data ready to perform the operations
   */
  public Action getData()
  {

    Action res = null;

    final Editable.EditorType et = _theData.getInfo();
    // is this projection editable?
    if(et != null)
    {
      _theProperties.addEditor(et, null);
    }

    // return the product
    return res;
  }

}
