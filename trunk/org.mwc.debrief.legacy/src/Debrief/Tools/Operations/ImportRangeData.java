// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportRangeData.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: ImportRangeData.java,v $
// Revision 1.2  2005/12/13 09:04:46  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:31  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:01+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:28+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:55+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:58+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-11-20 15:30:24+00  administrator
// Don't provide default directory
//
// Revision 1.0  2001-07-17 08:41:19+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:31+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:21  ianmayo
// initial import of files
//
// Revision 1.2  2000-10-10 12:20:16+01  ian_mayo
// <>
//
// Revision 1.1  2000-09-26 10:58:08+01  ian_mayo
// Initial revision
//

package Debrief.Tools.Operations;

import Debrief.ReaderWriter.PCArgos.*;
import MWC.GUI.*;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.*;

/** command to import a file (initially just Replay) into Debrief. 
 * The data used to implement the command is stored as a command, 
 * so that it may be added to an undo buffer.
 */
public final class ImportRangeData extends PlainTool {

  ///////////////////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////////////////////
	 
  /** the properties panel to put ourselves into
	 */
  private final PropertiesPanel _thePanel;
	
	/** the layers we should add data to
	 */
  private final Layers _theLayers;
  
  ///////////////////////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////////////////////
  /** constructor, taking information ready for when the button
   * gets pressed
   * @param theParent the ToolParent window which we control the cursor of
   * @param theApplication the Application to create a blank 
   * session to import the file into, if the session val is null
   * @param theSessionVal the Session to add the file to (or null, see above)
   */
  public ImportRangeData(ToolParent theParent,
												 MWC.GUI.Properties.PropertiesPanel thePanel,
												 Layers theData){
    super(theParent, "Import Rng Data", "images/import_range.gif");
    // store the Session                              
		_thePanel = thePanel;
		_theLayers = theData;
  }                              

	public final Action getData()
	{
		return null;
	}


	public final void execute()
	{
    
		// create new panel
		ImportRangeDataPanel pn = new SwingImportRangeData(_theLayers,
																											 null,
																											 _thePanel);
		
	
		if(pn != null)
			pn = null;
	}
}
