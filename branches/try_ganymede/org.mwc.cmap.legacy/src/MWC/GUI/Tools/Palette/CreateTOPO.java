// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CreateTOPO.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: CreateTOPO.java,v $
// Revision 1.5  2005/09/08 10:59:38  Ian.Mayo
// Lots of support for deferred instantiation
//
// Revision 1.4  2005/06/09 10:58:18  Ian.Mayo
// Make more flexible - suited to Eclipse incorporation
//
// Revision 1.3  2004/12/10 15:57:02  Ian.Mayo
// Handle problems encountered when reloading incomplete plot file.  Changes made in support of Debrief SWT
//
// Revision 1.2  2004/05/25 15:44:22  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:46  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-03-03 10:30:06+00  ian_mayo
// Comment marker to remind about fix
//
// Revision 1.3  2002-11-27 11:44:27+00  ian_mayo
// Match CreateETOPO more closely
//
// Revision 1.2  2002-11-25 14:41:35+00  ian_mayo
// Select best TOPO dataset available
//
// Revision 1.1  2002-11-13 13:09:40+00  ian_mayo
// Initial revision
//
// Revision 1.3  2002-07-09 15:29:38+01  ian_mayo
// improve comments
//
// Revision 1.2  2002-05-29 10:06:32+01  ian_mayo
// Tidy toolbar label
//
// Revision 1.1  2002-05-28 09:14:03+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-05-23 13:13:22+01  ian
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:35+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-23 13:27:55+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.0  2001-07-17 08:42:52+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:40+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:58  ianmayo
// initial version
//

package MWC.GUI.Tools.Palette;

import java.io.File;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.ETOPO.ETOPO_2_Minute;

/** class to create an ETOPO object,
 * @see MWC.GUI.Chart.Painters.ETOPOPainter
 */
public class CreateTOPO extends PlainCreateLayer
{

  /** static copy of parent, so we can retrieve the path
   *
   */
  private static MWC.GUI.ToolParent _myParent = null;

  /** the name of the property which indicates where the etopo data is
   *
   */
  public static String ETOPO_PATH = "ETOPO_Directory";

  /** the default path to find the ETOPO data
   *
   */
  private static String DEFAULT_PATH = "etopo";


  /** constructor, taking normal PlainCreate parameters
   *
   */
	public CreateTOPO(MWC.GUI.ToolParent theParent,
										MWC.GUI.Properties.PropertiesPanel thePanel,
										Layers theData,
										MWC.GUI.PlainChart theChart)
	{
		super(theParent, thePanel, theData, theChart, "TOPO Gridded Bathy", "images/etopo.gif");

    _myParent = theParent;
	}

	
	/** initialise the tool, so that it knows where to get it's layers information
	 * 
	 * @param theParent
	 */
	public static void initialise(ToolParent theParent)
	{
		_myParent = theParent;
	}	
	
  /** get the current ETOPO path
   *
   */
  public static String getETOPOPath()
  {

    // do we have a parent?
    if(_myParent == null)
    {
      System.err.println("Parent has not been set up for CreateTOPO.  Implementation fault");
      return null;
    }

    // retrieve the path to the ETOPO layers
    //
    String newPath =  _myParent.getProperty(ETOPO_PATH);

    if(newPath == null)
      newPath = DEFAULT_PATH;

    return newPath;

  }

  /** create this item. The Layers object will know to put this layer at the back because
   * of it's BackgroundLayer marker interface
   *
   */
	protected Layer createItem(MWC.GUI.PlainChart theChart)
	{
    // todo: overcome problem caused by loading ETOPO twice

	 	 return loadBathyData(theChart.getLayers());
	}

	
  /** static class which allows other layers to load data
   *
   */
  public static Layer load2MinBathyData()
  {
    String newPath = getETOPOPath();

    Layer res = null;

    // just see if we've already got a complete path
    File tstFile = new File(newPath);
    boolean foundIt = tstFile.isFile();
    
    // hmm, we've either got a full path, or just the directory and we need to append the file itself
    if(foundIt || ETOPO_2_Minute.dataFileExists(newPath))
    {
      res = new ETOPO_2_Minute(newPath);
    }
    else
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("ETOPO Not Found",
             "Sorry neither of the ETOPO datafiles can be found. Please check the ETOPO section of the userguide.");
    }

    return res;
  }
	
  /** static class which allows other layers to load data
   *
   */
  public static Layer loadBathyData(Layers theLayers)
  {
    String newPath = getETOPOPath();

    Layer res = null;

    // right, see if we can find either of the ETOPO datasets.
    // try the 5 2 minute first
    if(ETOPO_2_Minute.dataFileExists(newPath))
    {
      res = new ETOPO_2_Minute(newPath);
    }
    else if(ETOPOPainter.dataFileExists(newPath))
    {
      res = new ETOPOPainter(newPath, theLayers);
    }
    else
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("ETOPO Not Found",
             "Sorry neither of the ETOPO datafiles can be found. Please check the ETOPO section of the userguide.");
    }

    return res;
  }
}
